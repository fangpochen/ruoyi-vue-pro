package cn.iocoder.yudao.module.system.controller.admin.email.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 邮件消息分页请求 VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件消息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EmailMessagePageReqVO extends PageParam {

    @Schema(description = "主题", example = "测试邮件")
    private String subject;

    @Schema(description = "发件人", example = "test@example.com")
    private String sender;

    @Schema(description = "原始路径", example = "/path/to/email.eml")
    private String originalPath;

    @Schema(description = "是否星标", example = "true")
    private Boolean isStarred;

    @Schema(description = "发送时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] sendDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

} 