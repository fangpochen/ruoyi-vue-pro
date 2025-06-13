package cn.iocoder.yudao.module.email.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import cn.iocoder.yudao.module.email.convert.ImportBatchConvert;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportBatchDO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportErrorLogDO;
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

import java.util.List;
import java.util.stream.Collectors;

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
    public CommonResult<PageResult<ImportBatchRespVO>> getImportBatches(@Valid ImportBatchPageReqVO pageReqVO) {
        PageResult<ImportBatchDO> pageResult = emailImportService.getImportBatchPage(pageReqVO);
        
        // 临时处理：直接手动转换，避免转换器问题
        PageResult<ImportBatchRespVO> convertedResult = new PageResult<>();
        convertedResult.setList(pageResult.getList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList()));
        convertedResult.setTotal(pageResult.getTotal());
        
        return success(convertedResult);
    }
    
    private ImportBatchRespVO convertToRespVO(ImportBatchDO batch) {
        return ImportBatchRespVO.builder()
                .id(batch.getId())
                .batchName(batch.getBatchName())
                .zipFilename(batch.getZipFilename())
                .status(batch.getStatus())
                .totalFiles(batch.getTotalFiles())
                .successCount(batch.getSuccessCount())
                .failCount(batch.getFailCount())
                .startTime(batch.getStartTime())
                .endTime(batch.getEndTime())
                .errorMessage(batch.getErrorMessage())
                .createTime(batch.getCreateTime())
                .build();
    }

    @GetMapping("/import/errors/{batchId}")
    @Operation(summary = "获取导入错误日志")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<List<ImportErrorLogDO>> getImportErrors(@PathVariable("batchId") Long batchId) {
        List<ImportErrorLogDO> errorLogs = emailImportService.getImportErrorLogs(batchId);
        return success(errorLogs);
    }

    @GetMapping("/import/status/{batchId}")
    @Operation(summary = "获取导入状态")
    @PreAuthorize("@ss.hasPermission('system:eml:query')")
    public CommonResult<ImportBatchRespVO> getImportStatus(@PathVariable("batchId") Long batchId) {
        ImportBatchRespVO batchStatus = emailImportService.getImportStatus(batchId);
        return success(batchStatus);
    }

} 