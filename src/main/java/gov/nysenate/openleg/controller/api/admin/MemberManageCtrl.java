package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.view.entity.ExtendedMemberView;
import gov.nysenate.openleg.client.view.entity.FullMemberView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.entity.FullMember;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/member")
public class MemberManageCtrl extends BaseCtrl {

    private static final Logger logger = LoggerFactory.getLogger(MemberManageCtrl.class);

    @Autowired MemberService memberService;

    private static class MemberViewList extends ArrayList<ExtendedMemberView>{}

    @RequiresPermissions("admin:member:get")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse getExtendedMembers(@RequestParam(defaultValue = "false") boolean unverifiedOnly) {
        Map<Boolean, List<FullMember>> poopTition = memberService.getAllFullMembers().stream()
                .collect(Collectors.partitioningBy(FullMember::isVerified));
        logger.info("true: {}, false: {}", Optional.ofNullable(poopTition.get(true)).map(List::size).orElse(0),
                Optional.ofNullable(poopTition.get(false)).map(List::size).orElse(0));
        List<FullMemberView> fullMembers = memberService.getAllFullMembers().stream()
                .filter(member -> !unverifiedOnly || !member.isVerified())
                .map(FullMemberView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(fullMembers, fullMembers.size(), LimitOffset.ALL);
    }

    @RequiresPermissions("admin:member:post")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse updateMembers(@RequestBody MemberViewList memberViewList) {
        memberService.updateMembers(memberViewList.stream()
                .map(ExtendedMemberView::toMember)
                .collect(Collectors.toList()));
        return new SimpleResponse(true, "members updated", "member-update-success");
    }


}
