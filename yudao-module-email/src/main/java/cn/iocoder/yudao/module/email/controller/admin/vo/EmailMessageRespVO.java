package cn.iocoder.yudao.module.email.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件消息 Response VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件消息 Response VO")
@Data
public class EmailMessageRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "导入批次ID", example = "1")
    private Long importBatchId;

    @Schema(description = "邮件Message-ID头", example = "<123456@example.com>")
    private String messageId;

    @Schema(description = "发件人", example = "sender@example.com")
    private String sender;

    @Schema(description = "收件人列表")
    private List<String> recipients;

    @Schema(description = "抄送列表")
    private List<String> ccRecipients;

    @Schema(description = "密送列表")
    private List<String> bccRecipients;

    @Schema(description = "邮件主题", example = "测试邮件")
    private String subject;

    @Schema(description = "发送时间")
    private LocalDateTime sendDate;

    @Schema(description = "接收时间")
    private LocalDateTime receiveDate;

    @Schema(description = "纯文本内容")
    private String contentText;

    @Schema(description = "HTML内容")
    private String contentHtml;

    @Schema(description = "原始文件路径", example = "/path/to/email.eml")
    private String originalPath;

    @Schema(description = "附件数量", example = "3")
    private Integer attachmentCount;

    @Schema(description = "是否标记星标", example = "true")
    private Boolean isStarred;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}