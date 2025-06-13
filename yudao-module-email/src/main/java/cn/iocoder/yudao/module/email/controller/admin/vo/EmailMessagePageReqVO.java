package cn.iocoder.yudao.module.email.controller.admin.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 邮件消息分页查询 Request VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件消息分页查询 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmailMessagePageReqVO extends PageParam {

    @Schema(description = "邮件主题", example = "测试邮件")
    private String subject;

    @Schema(description = "发件人", example = "sender@example.com")
    private String sender;

    @Schema(description = "收件人", example = "recipient@example.com")
    private String recipient;

    @Schema(description = "原始文件路径", example = "/path/to/email.eml")
    private String originalPath;

    @Schema(description = "是否标记星标")
    private Boolean isStarred;

    @Schema(description = "发送时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] sendDate;

} 