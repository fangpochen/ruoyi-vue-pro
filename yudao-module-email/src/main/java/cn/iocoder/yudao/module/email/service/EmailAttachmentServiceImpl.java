package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.module.email.dal.dataobject.EmailAttachmentDO;
import cn.iocoder.yudao.module.email.dal.mysql.EmailAttachmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.email.enums.ErrorCodeConstants.EMAIL_ATTACHMENT_NOT_EXISTS;

/**
 * 邮件附件 Service 实现类
 *
 * @author 方总牛逼
 */
@Service
@Slf4j
public class EmailAttachmentServiceImpl implements EmailAttachmentService {

    @Resource
    private EmailAttachmentMapper emailAttachmentMapper;

    @Resource
    private LocalFileStorageService localFileStorageService;

    @Override
    public List<EmailAttachmentDO> getAttachmentsByEmailId(Long emailMessageId) {
        return emailAttachmentMapper.selectListByEmailMessageId(emailMessageId);
    }

    @Override
    public EmailAttachmentDO getAttachmentById(Long attachmentId) {
        EmailAttachmentDO attachment = emailAttachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw exception(EMAIL_ATTACHMENT_NOT_EXISTS);
        }
        return attachment;
    }

    @Override
    public byte[] downloadAttachment(Long attachmentId) {
        EmailAttachmentDO attachment = emailAttachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw exception(EMAIL_ATTACHMENT_NOT_EXISTS);
        }
        
        try {
            return localFileStorageService.getAttachment(attachment.getFilePath());
        } catch (Exception e) {
            log.error("下载附件失败: attachmentId={}, filePath={}", attachmentId, attachment.getFilePath(), e);
            throw exception(EMAIL_ATTACHMENT_NOT_EXISTS);
        }
    }

    @Override
    public void deleteAttachmentsByEmailId(Long emailMessageId) {
        List<EmailAttachmentDO> attachments = getAttachmentsByEmailId(emailMessageId);
        
        // 删除物理文件
        for (EmailAttachmentDO attachment : attachments) {
            try {
                localFileStorageService.deleteAttachment(attachment.getFilePath());
            } catch (Exception e) {
                log.warn("删除附件文件失败: {}", attachment.getFilePath(), e);
            }
        }
        
        // 删除数据库记录
        emailAttachmentMapper.deleteByEmailMessageId(emailMessageId);
    }
} 