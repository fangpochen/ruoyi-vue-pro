package cn.iocoder.yudao.module.system.dal.dataobject.email;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 导入错误日志 DO
 *
 * @author 方总牛逼
 */
@TableName("system_import_error_log")
@KeySequence("system_import_error_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 导入批次ID
     */
    private Long importBatchId;

    /**
     * 错误文件路径
     */
    private String filePath;

    /**
     * 错误类型
     */
    private String errorType;

    /**
     * 错误详情
     */
    private String errorMessage;

} 