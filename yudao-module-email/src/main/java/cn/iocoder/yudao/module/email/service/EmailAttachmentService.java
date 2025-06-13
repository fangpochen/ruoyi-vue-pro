package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.module.email.dal.dataobject.EmailAttachmentDO;

import java.util.List;

/**
 * 邮件附件 Service 接口
 *
 * @author 方总牛逼
 */
public interface EmailAttachmentService {

    /**
     * 根据邮件ID获取附件列表
     *
     * @param emailMessageId 邮件ID
     * @return 附件列表
     */
    List<EmailAttachmentDO> getAttachmentsByEmailId(Long emailMessageId);

    /**
     * 根据附件ID获取附件信息
     *
     * @param attachmentId 附件ID
     * @return 附件信息
     */
    EmailAttachmentDO getAttachmentById(Long attachmentId);

    /**
     * 下载附件
     *
     * @param attachmentId 附件ID
     * @return 附件字节内容
     */
    byte[] downloadAttachment(Long attachmentId);

    /**
     * 根据邮件ID删除附件
     *
     * @param emailMessageId 邮件ID
     */
    void deleteAttachmentsByEmailId(Long emailMessageId);
} 