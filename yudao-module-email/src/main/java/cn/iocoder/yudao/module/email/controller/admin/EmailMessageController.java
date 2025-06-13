package cn.iocoder.yudao.module.email.controller.admin;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessageRespVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailStatisticsRespVO;
import cn.iocoder.yudao.module.email.convert.EmailMessageConvert;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import cn.iocoder.yudao.module.email.service.EmailMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 邮件消息
 *
 * @author 方总牛逼
 */
@Tag(name = "管理后台 - 邮件消息")
@RestController
@RequestMapping("/system/email")
@Validated
@Slf4j
public class EmailMessageController {

    @Resource
    private EmailMessageService emailMessageService;

    @GetMapping("/page")
    @Operation(summary = "获得邮件消息分页")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<PageResult<EmailMessageRespVO>> getEmailMessagePage(@Valid EmailMessagePageReqVO pageReqVO) {
        PageResult<EmailMessageDO> pageResult = emailMessageService.getEmailMessagePage(pageReqVO);
        return success(EmailMessageConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/get")
    @Operation(summary = "获得邮件消息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<EmailMessageRespVO> getEmailMessage(@RequestParam("id") Long id) {
        EmailMessageDO emailMessage = emailMessageService.getEmailMessage(id);
        return success(EmailMessageConvert.INSTANCE.convert(emailMessage));
    }

    @PutMapping("/toggle-star")
    @Operation(summary = "切换邮件星标状态")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:eml:star')")
    public CommonResult<Boolean> toggleStar(@RequestParam("id") Long id) {
        Boolean result = emailMessageService.toggleStar(id);
        return success(result);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除邮件消息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:eml:delete')")
    public CommonResult<Boolean> deleteEmailMessage(@RequestParam("id") Long id) {
        emailMessageService.deleteEmailMessage(id);
        return success(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取邮件统计信息")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<EmailStatisticsRespVO> getEmailStatistics() {
        EmailStatisticsRespVO statistics = emailMessageService.getEmailStatistics();
        return success(statistics);
    }

    // 添加测试接口，无需认证
    @GetMapping("/test")
    @Operation(summary = "测试邮件模块是否正常")
    @PermitAll
    public CommonResult<String> testModule() {
        return success("邮件模块运行正常！方总牛逼！");
    }

}