package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.environment.EnvVarNotFoundException;
import gov.nysenate.openleg.client.view.environment.EnvironmentVariableView;
import gov.nysenate.openleg.client.view.environment.ImmutableEnvVarException;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/environment")
public class EnvironmentCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentCtrl.class);

    /** A set of variables that are allowed to be modified, mapped to their respective setter functions */
    private ImmutableMap<String, Consumer<String>> mutableProperties;

    /** A set of variables whose values are not allowed to be shown through this api */
    private static final ImmutableSet<String> hiddenProperties = ImmutableSet.<String>builder()
            .add("emailPass")
            .add("apiSecret")
            .add("defaultAdminPass")
            .build();


    @Autowired Environment env;

    @PostConstruct
    public void init() {
        mutableProperties = ImmutableMap.<String, Consumer<String>>builder()
                .put("processingEnabled", setBoolean(env::setProcessingEnabled))
                .put("processingScheduled", setBoolean(env::setProcessingScheduled))
                .put("spotcheckScheduled", setBoolean(env::setSpotcheckScheduled))
                .put("notificationsEnabled", setBoolean(env::setNotificationsEnabled))
                .put("billScrapeQueueEnabled", setBoolean(env::setBillScrapeQueueEnabled))
                .build();
    }

    /**
     * Set Environment Variable API
     * ----------------------------
     *
     * Set the value of an environment variable:
     * (GET) /api/3/admin/environment/set
     *
     * Request Params: varName - String - the name of the environment variable to set
     *                 value - String - a string representation of the value to set
     *
     */
    @RequiresPermissions("admin:envEdit")
    @RequestMapping(value = "/set")
    public BaseResponse setVariable(@RequestParam String varName,
                                    @RequestParam String value) {
        if (mutableProperties.containsKey(varName)) {
            logger.info("Setting environment variable '{}' to '{}'", varName, value);
            mutableProperties.get(varName).accept(value);
            return new ViewObjectResponse<>(getVariable(varName), "Environment variable value successfully changed");
        } else {
            throw new ImmutableEnvVarException(getVariable(varName));
        }
    }

    /**
     * Get Environment Variable API
     * ----------------------------
     *
     * Get the value of one or more environment variables
     * (GET) /api/3/admin/environment
     *
     * Request Params: varName - String[] - names of variables to retrieve, gets all if empty
     *                 mutableOnly - boolean - default false - gets only mutable variables when set to true
     */
    @RequiresPermissions("admin:envEdit")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getVariables(@RequestParam(defaultValue = "") String[] varName,
                                     @RequestParam(defaultValue = "false") boolean mutableOnly) {
        List<EnvironmentVariableView> variables = new ArrayList<>();
        if (varName.length > 0) {
            Arrays.asList(varName).stream()
                    .map(this::getVariable)
                    .forEach(variables::add);
        } else {
            for (Field field : Environment.class.getDeclaredFields()) {
                try {
                    variables.add(getVariable(field));
                } catch (NoSuchMethodException ignored) {}
            }
        }
        return new ViewObjectResponse<>(ListView.of(
                variables.stream()
                        .filter(var -> !mutableOnly || var.isMutable())
                        .sorted((a, b) -> ComparisonChain.start()
                                .compare(a.isMutable(), b.isMutable())
                                .compare(a.getName(), b.getName())
                                .result())
                        .collect(Collectors.toList())));
    }

    @ExceptionHandler(EnvVarNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public BaseResponse handleEnvVarNotFoundEx(EnvVarNotFoundException ex) {
        return new ViewObjectErrorResponse(ErrorCode.NO_SUCH_ENV_VARIABLE, ex.getVarName());
    }

    @ExceptionHandler(ImmutableEnvVarException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public BaseResponse handleImmutableEnvVarEx(ImmutableEnvVarException ex) {
        return new ViewObjectErrorResponse(ErrorCode.IMMUTABLE_ENV_VARIABLE, ex.getVar());
    }

    /**
     * --- Internal Methods ---
     */

    private EnvironmentVariableView getVariable(String name) throws EnvVarNotFoundException {
        try {
            return getVariable(Environment.class.getDeclaredField(name));
        } catch (NoSuchFieldException | NoSuchMethodException ex) {
            throw new EnvVarNotFoundException(name);
        }
    }

    private EnvironmentVariableView getVariable(Field field) throws NoSuchMethodException {
        Method getter = getGetter(field);
        return new EnvironmentVariableView(field.getName(), getVarValue(field, getter), getter.getReturnType(),
                mutableProperties.containsKey(field.getName()));
    }

    private Consumer<String> setBoolean(Consumer<Boolean> setter) {
        return val -> setter.accept(Boolean.parseBoolean(val));
    }

    private Object getVarValue(Field field, Method getter) {
        if (hiddenProperties.contains(field.getName())) {
            return "* HIDDEN *";
        }
        try {
            return getter.invoke(env);
        } catch (InvocationTargetException | IllegalAccessException ex) {
            logger.error("Get Environment variable ex\n", ex);
            return null;
        }
    }

    private Method getGetter(Field field) throws NoSuchMethodException {
        String getterSuffix = StringUtils.capitalize(field.getName());
        try {
            return Environment.class.getDeclaredMethod("get" + getterSuffix);
        } catch (NoSuchMethodException e) {
            return Environment.class.getDeclaredMethod("is" + getterSuffix);
        }
    }
}
