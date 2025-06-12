package cn.iocoder.yudao.module.system.controller.admin.email;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.module.system.controller.admin.email.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.email.vo.EmailMessageRespVO;
import cn.iocoder.yudao.module.system.controller.admin.email.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.email.ImportBatchDO;
import cn.iocoder.yudao.module.system.service.email.EmlImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;

/**
 * 管理后台 - 邮件导入
 *
 * @author 方总牛逼
 */
@Tag(name = "管理后台 - 邮件导入")
@RestController
@RequestMapping("/admin-api/system/email")
public class EmailController {

    @Resource
    private EmlImportService emlImportService;

    @PostMapping("/upload-zip")
    @Operation(summary = "上传ZIP压缩包进行EML导入")
    @PreAuthorize("@ss.hasPermission('system:email:import')")
    @OperateLog(type = CREATE)
    public CommonResult<Long> uploadZip(@RequestParam("file") MultipartFile file) {
        Long batchId = emlImportService.uploadZipFile(file);
        return success(batchId);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询邮件列表")
    @PreAuthorize("@ss.hasPermission('system:email:query')")
    public CommonResult<PageResult<EmailMessageRespVO>> getEmailMessagePage(@Valid EmailMessagePageReqVO pageReqVO) {
        PageResult<EmailMessageRespVO> pageResult = emlImportService.getEmailMessagePage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取邮件详情")
    @Parameter(name = "id", description = "邮件ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:email:query')")
    public CommonResult<EmailMessageRespVO> getEmailMessage(@PathVariable("id") Long id) {
        EmailMessageRespVO emailMessage = emlImportService.getEmailMessage(id);
        return success(emailMessage);
    }

    @PutMapping("/{id}/toggle-star")
    @Operation(summary = "切换邮件星标状态")
    @Parameter(name = "id", description = "邮件ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:email:star')")
    @OperateLog(type = UPDATE)
    public CommonResult<Boolean> toggleStar(@PathVariable("id") Long id) {
        Boolean result = emlImportService.toggleStar(id);
        return success(result);
    }

    @DeleteMapping("")
    @Operation(summary = "批量删除邮件")
    @PreAuthorize("@ss.hasPermission('system:email:delete')")
    @OperateLog(type = DELETE)
    public CommonResult<Boolean> deleteEmailMessages(@RequestBody List<Long> ids) {
        Boolean result = emlImportService.deleteEmailMessages(ids);
        return success(result);
    }

    @GetMapping("/attachment/{attachmentId}/download")
    @Operation(summary = "下载附件")
    @Parameter(name = "attachmentId", description = "附件ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('system:email:download')")
    public void downloadAttachment(@PathVariable("attachmentId") Long attachmentId, HttpServletResponse response) {
        emlImportService.downloadAttachment(attachmentId, response);
    }

    @GetMapping("/import-batches")
    @Operation(summary = "分页查询导入批次")
    @PreAuthorize("@ss.hasPermission('system:email:query')")
    public CommonResult<PageResult<ImportBatchDO>> getImportBatchPage(@Valid ImportBatchPageReqVO pageReqVO) {
        PageResult<ImportBatchDO> pageResult = emlImportService.getImportBatchPage(pageReqVO);
        return success(pageResult);
    }

} 