package cn.iocoder.yudao.module.email.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;


import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import cn.iocoder.yudao.module.email.service.EmailImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 邮件导入
 *
 * @author 方总牛逼
 */
@Tag(name = "管理后台 - 邮件导入")
@RestController
@RequestMapping("/system/email-import")
@Validated
@Slf4j
public class EmailImportController {

    @Resource
    private EmailImportService emailImportService;

    @PostMapping("/upload-zip")
    @Operation(summary = "上传ZIP文件进行邮件导入")
    @PreAuthorize("@ss.hasPermission('system:eml:import')")
    public CommonResult<ImportBatchRespVO> uploadZip(@RequestParam("file") MultipartFile file) {
        ImportBatchRespVO result = emailImportService.uploadZipFile(file);
        return success(result);
    }

    @GetMapping("/import/batches")
    @Operation(summary = "获取导入批次列表")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<String> getImportBatches() {
        // TODO: 实现获取导入批次列表功能
        return success("暂未实现");
    }

    @GetMapping("/import/errors/{batchId}")
    @Operation(summary = "获取导入错误日志")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<Object> getImportErrors(@PathVariable("batchId") Long batchId) {
        // TODO: 实现获取导入错误日志的功能
        return success("暂未实现");
    }

} 