package cn.iocoder.yudao.module.email.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件消息 DO
 *
 * @author 方总牛逼
 */
@TableName("system_email_message")
@KeySequence("email_message_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    
    /**
     * 导入批次ID
     */
    private Long importBatchId;
    
    /**
     * 邮件Message-ID头
     */
    private String messageId;
    
    /**
     * 发件人
     */
    private String sender;
    
    /**
     * 收件人列表(JSON格式)
     */
    private String recipients;
    
    /**
     * 抄送列表(JSON格式)
     */
    private String ccRecipients;
    
    /**
     * 密送列表(JSON格式)
     */
    private String bccRecipients;
    
    /**
     * 邮件主题
     */
    private String subject;
    
    /**
     * 发送时间
     */
    private LocalDateTime sendDate;
    
    /**
     * 接收时间
     */
    private LocalDateTime receiveDate;
    
    /**
     * 纯文本内容
     */
    private String contentText;
    
    /**
     * HTML内容
     */
    private String contentHtml;
    
    /**
     * 原始文件路径
     */
    private String originalPath;
    
    /**
     * 附件数量
     */
    private Integer attachmentCount;
    
    /**
     * 是否标记星标
     */
    private Boolean isStarred;

} 