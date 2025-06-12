package cn.iocoder.yudao.module.system.dal.dataobject.email;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 导入批次 DO
 *
 * @author 方总牛逼
 */
@TableName("system_import_batch")
@KeySequence("system_import_batch_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportBatchDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 批次名称
     */
    private String batchName;

    /**
     * ZIP文件名
     */
    private String zipFilename;

    /**
     * 状态：1-处理中,2-成功,3-部分失败,4-失败
     */
    private Integer status;

    /**
     * 总文件数
     */
    private Integer totalFiles;

    /**
     * 成功数
     */
    private Integer successCount;

    /**
     * 失败数
     */
    private Integer failCount;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 导入批次状态枚举
     */
    public enum Status {
        PROCESSING(1, "处理中"),
        SUCCESS(2, "成功"),
        PARTIAL_FAILED(3, "部分失败"),
        FAILED(4, "失败");

        private final Integer code;
        private final String desc;

        Status(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

} 