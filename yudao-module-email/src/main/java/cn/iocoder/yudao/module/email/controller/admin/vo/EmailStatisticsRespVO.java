package cn.iocoder.yudao.module.email.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邮件统计信息 Response VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 邮件统计信息 Response VO")
@Data
public class EmailStatisticsRespVO {

    @Schema(description = "邮件总数", example = "1000")
    private Long totalCount;

    @Schema(description = "已标记星标数量", example = "50")
    private Long starredCount;

    @Schema(description = "有附件的邮件数量", example = "200")
    private Long withAttachmentsCount;

    @Schema(description = "今日新增邮件数量", example = "10")
    private Long todayCount;

    @Schema(description = "本周新增邮件数量", example = "70")
    private Long weekCount;

    @Schema(description = "本月新增邮件数量", example = "300")
    private Long monthCount;

} 