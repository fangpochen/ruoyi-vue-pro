package cn.iocoder.yudao.module.email.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邮件附件 Response VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件附件 Response VO")
@Data
public class EmailAttachmentRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "邮件消息ID", example = "1")
    private Long emailMessageId;

    @Schema(description = "文件名", example = "attachment.pdf")
    private String filename;

    @Schema(description = "文件类型", example = "application/pdf")
    private String contentType;

    @Schema(description = "文件大小", example = "1024")
    private Long fileSize;

    @Schema(description = "文件存储URL", example = "http://localhost:9000/yudao/email/attachment.pdf")
    private String fileUrl;

} 