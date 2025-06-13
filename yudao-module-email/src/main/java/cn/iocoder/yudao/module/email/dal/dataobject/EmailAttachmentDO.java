package cn.iocoder.yudao.module.email.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 邮件附件 DO
 *
 * @author 方总牛逼
 */
@TableName("system_email_attachment")
@KeySequence("email_attachment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailAttachmentDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    
    /**
     * 邮件ID
     */
    private Long emailMessageId;
    
    /**
     * 文件名
     */
    private String filename;
    
    /**
     * 文件类型
     */
    private String contentType;
    
    /**
     * 文件大小
     */
    private Long fileSize;
    
    /**
     * MinIO存储URL
     */
    private String fileUrl;
    
    /**
     * MinIO存储路径
     */
    private String filePath;

}