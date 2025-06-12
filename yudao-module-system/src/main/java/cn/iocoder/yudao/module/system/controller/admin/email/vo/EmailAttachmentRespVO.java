package cn.iocoder.yudao.module.system.controller.admin.email.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件附件响应 VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件附件 Response VO")
@Data
public class EmailAttachmentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "邮件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long emailMessageId;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "attachment.pdf")
    private String filename;

    @Schema(description = "文件类型", example = "application/pdf")
    private String contentType;

    @Schema(description = "文件大小", example = "1024")
    private Long fileSize;

    @Schema(description = "MinIO存储URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://localhost:9000/yudao/attachment.pdf")
    private String fileUrl;

    @Schema(description = "MinIO存储路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/attachment/2024/01/01/attachment.pdf")
    private String filePath;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}