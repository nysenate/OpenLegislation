package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.view.cache.CacheStatsView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeSessionId;
import gov.nysenate.openleg.model.law.LawVersionId;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/cache")
public class CacheCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(CacheCtrl.class);

    @Autowired private EventBus eventBus;
    @Autowired private CacheManager cacheManager;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /**
     * Cache Stats API
     * ---------------
     *
     * Gets stats for all memory caches: (GET) /api/3/admin/cache/
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getCacheStats() {
        return ListViewResponse.of(Arrays.asList(cacheManager.getCacheNames()).stream()
            .map(cn -> new CacheStatsView(cacheManager.getCache(cn).getStatistics()))
            .collect(Collectors.toList()), cacheManager.getCacheNames().length, LimitOffset.ALL);
    }

    /**
     * Cache Warming API
     * -----------------
     *
     * This api can be used to clear out and pre-load a pre-determined subset of data into the cache
     * to boost performance of commonly used api calls.
     *
     * Warm memory caches: (PUT) /api/3/admin/cache/{cacheType}
     * The cacheType can be either 'all' for all caches, or one of the values in the
     * {@link gov.nysenate.openleg.model.cache.ContentCache} enumeration.
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.PUT)
    public BaseResponse warmCache(@PathVariable String cacheType) {
        Set<ContentCache> targetCaches = getTargetCaches(cacheType);
        eventBus.post(new CacheWarmEvent(targetCaches));
        return new SimpleResponse(true, "Cache warming requests completed for " + targetCaches, "cache-warm");
    }

    /**
     * Cache Evict API
     * ---------------
     *
     * Delete all entries in the specified cache(s): (DELETE) /api/3/admin/cache/{cacheType}
     * @see #warmCache(String) for details about 'cacheType'
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}", method = RequestMethod.DELETE)
    public BaseResponse deleteCache(@PathVariable String cacheType) {
        Set<ContentCache> targetCaches = getTargetCaches(cacheType);
        eventBus.post(new CacheEvictEvent(targetCaches));
        return new SimpleResponse(true, "Cache eviction request sent for " + targetCaches, "cache-evict");
    }

    /**
     * Cache Evict by Id API
     * ---------------------
     *
     * Delete the entry in the specified cache designated by the given id:
     * (DELETE) /api/3/admin/cache/{cacheType}/id
     *
     * The id is specified through required request parameters, which depend on the cacheType
     *
     * Request params for BILL: printNo (string) - a bill print number
     *                          session (integer) - session year of the bill
     *
     * Request params for AGENDA: agendaNo (integer) - an agenda number
     *                            year (integer) - year of the agenda
     *
     * Request params for CALENDAR: calNo (integer) - a calendar number
     *                              year (integer) - year of the calendar
     *
     * Request params for LAW: lawId (string) - three letter law identifier
     *                         publishedDate (date) - published date of this law version
     *
     * Request params for COMMITTEE: chamber (string) - senate or assembly
     *                               committeeName (string) - the name of the committee
     *                               year (integer) - year of the committee
     *
     * Request params for MEMBER: memberId (integer) - member id
     *
     * Request params for APIUSER: key (string) - api user's key
     */
    @RequiresPermissions("admin:cacheEdit")
    @RequestMapping(value = "/{cacheType}/id", method = RequestMethod.DELETE)
    public BaseResponse evictContentId(@PathVariable String cacheType, WebRequest webRequest)
            throws MissingServletRequestParameterException {
        ContentCache targetCache = getTargetCache(cacheType);
        Object contentId = getContentId(targetCache, webRequest);
        if (contentId == null) {
            throw new InvalidRequestParamEx(cacheType, "cacheType", "string", "Supported cache type.");
        }
        eventBus.post(new CacheEvictIdEvent<>(targetCache, contentId));
        return new SimpleResponse(true, "Cache eviction request sent for " + targetCache + ": " + contentId, "cache-evict");
    }

    /** --- Internal --- */

    private Set<ContentCache> getTargetCaches(String cacheType) {
        if (cacheType.equalsIgnoreCase("all")) {
            return Sets.newHashSet(ContentCache.values());
        }
        return Sets.newHashSet(getTargetCache(cacheType));
    }

    private ContentCache getTargetCache(String cacheType) {
        return getEnumParameter("cacheType", cacheType, ContentCache.class);
    }

    private Object getContentId(ContentCache targetCache, WebRequest request)
            throws MissingServletRequestParameterException {
        switch (targetCache) {
            case BILL:
                return getBaseBillId(request);
            case AGENDA:
                return getAgendaId(request);
            case CALENDAR:
                return getCalendarId(request);
            case LAW:
                return getLawVersionId(request);
            case COMMITTEE:
                return getCommitteeSessionId(request);
            case MEMBER:
                requireParameters(request, "memberId", "integer");
                return getIntegerParam(request, "memberId");
            case APIUSER:
                requireParameters(request, "key", "string");
                return request.getParameter("key");
            case NOTIFICATION_SUBSCRIPTION:
                return "all subscriptions";
            default:
                return null;
        }
    }

    private BaseBillId getBaseBillId(WebRequest request) throws MissingServletRequestParameterException {
        requireParameters(request, "printNo", "string", "session", "integer");
        return getBaseBillId(request.getParameter("printNo"), getIntegerParam(request, "session"), "printNo");
    }

    private AgendaId getAgendaId(WebRequest request) throws MissingServletRequestParameterException {
        requireParameters(request, "agendaNo", "integer", "year", "integer");
        return new AgendaId(getIntegerParam(request, "agendaNo"), getIntegerParam(request, "year"));
    }

    private CalendarId getCalendarId(WebRequest request) throws MissingServletRequestParameterException {
        requireParameters(request, "calNo", "integer", "year", "integer");
        return new CalendarId(getIntegerParam(request, "calNo"), getIntegerParam(request, "year"));
    }

    private LawVersionId getLawVersionId(WebRequest request) throws MissingServletRequestParameterException {
        requireParameters(request, "lawId", "string", "publishedDate", "date");
        return new LawVersionId(request.getParameter("lawId"), parseISODate(request.getParameter("publishedDate"), "publishedDate"));
    }

    private CommitteeSessionId getCommitteeSessionId(WebRequest request) throws MissingServletRequestParameterException {
        requireParameters(request, "chamber", "string", "committeeName", "string", "year", "integer");
            return new CommitteeSessionId(getEnumParameter("chamber", request.getParameter("chamber"), Chamber.class),
                    request.getParameter("committeeName"),
                    SessionYear.of(getIntegerParam(request, "year")));
    }
}