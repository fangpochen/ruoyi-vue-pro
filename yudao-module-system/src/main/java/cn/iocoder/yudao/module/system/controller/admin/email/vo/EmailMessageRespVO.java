package cn.iocoder.yudao.module.system.controller.admin.email.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件消息响应 VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件消息 Response VO")
@Data
public class EmailMessageRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "导入批次ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long importBatchId;

    @Schema(description = "Message-ID头", example = "<123@example.com>")
    private String messageId;

    @Schema(description = "发件人", requiredMode = Schema.RequiredMode.REQUIRED, example = "test@example.com")
    private String sender;

    @Schema(description = "收件人列表", example = "[\"user1@example.com\", \"user2@example.com\"]")
    private List<String> recipients;

    @Schema(description = "抄送列表", example = "[\"cc@example.com\"]")
    private List<String> ccRecipients;

    @Schema(description = "密送列表", example = "[\"bcc@example.com\"]")
    private List<String> bccRecipients;

    @Schema(description = "主题", example = "测试邮件")
    private String subject;

    @Schema(description = "发送时间", example = "2024-01-01 10:00:00")
    private LocalDateTime sendDate;

    @Schema(description = "接收时间", example = "2024-01-01 10:01:00")
    private LocalDateTime receiveDate;

    @Schema(description = "纯文本内容")
    private String contentText;

    @Schema(description = "HTML内容")
    private String contentHtml;

    @Schema(description = "原始文件路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "/path/to/email.eml")
    private String originalPath;

    @Schema(description = "附件数量", example = "2")
    private Integer attachmentCount;

    @Schema(description = "是否标记星标", example = "false")
    private Boolean isStarred;

    @Schema(description = "附件列表")
    private List<EmailAttachmentRespVO> attachments;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}