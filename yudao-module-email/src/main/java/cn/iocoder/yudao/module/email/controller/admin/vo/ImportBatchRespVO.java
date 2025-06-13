package cn.iocoder.yudao.module.email.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导入批次 Response VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 导入批次 Response VO")
@Data
public class ImportBatchRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "批次名称", example = "邮件导入_20240613")
    private String batchName;

    @Schema(description = "ZIP文件名", example = "emails.zip")
    private String zipFilename;

    @Schema(description = "状态：1-处理中,2-成功,3-部分失败,4-失败", example = "2")
    private Integer status;

    @Schema(description = "总文件数", example = "100")
    private Integer totalFiles;

    @Schema(description = "成功数量", example = "95")
    private Integer successCount;

    @Schema(description = "失败数量", example = "5")
    private Integer failCount;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

} 