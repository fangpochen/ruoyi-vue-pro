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
 * 导入批次分页请求 VO
 *
 * @author 方总牛逼
 */
@Schema(description = "管理后台 - 导入批次分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ImportBatchPageReqVO extends PageParam {

    @Schema(description = "批次名称", example = "2024-01-01-batch")
    private String batchName;

    @Schema(description = "ZIP文件名", example = "emails.zip")
    private String zipFilename;

    @Schema(description = "状态", example = "2")
    private Integer status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

} 