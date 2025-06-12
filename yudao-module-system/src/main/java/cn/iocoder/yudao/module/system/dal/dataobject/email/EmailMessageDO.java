package cn.iocoder.yudao.module.system.dal.dataobject.email;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件消息 DO
 *
 * @author 方总牛逼
 */
@TableName(value = "system_email_message", autoResultMap = true)
@KeySequence("system_email_message_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDO extends BaseDO {

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
     * Message-ID头
     */
    private String messageId;

    /**
     * 发件人
     */
    private String sender;

    /**
     * 收件人列表(JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> recipients;

    /**
     * 抄送列表(JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> ccRecipients;

    /**
     * 密送列表(JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> bccRecipients;

    /**
     * 主题
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