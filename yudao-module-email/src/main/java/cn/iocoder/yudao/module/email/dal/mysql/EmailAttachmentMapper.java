package cn.iocoder.yudao.module.email.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailAttachmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 邮件附件 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface EmailAttachmentMapper extends BaseMapperX<EmailAttachmentDO> {

    /**
     * 根据邮件ID查询附件列表
     *
     * @param emailMessageId 邮件ID
     * @return 附件列表
     */
    default List<EmailAttachmentDO> selectListByEmailMessageId(Long emailMessageId) {
        return selectList(EmailAttachmentDO::getEmailMessageId, emailMessageId);
    }

    /**
     * 根据邮件ID删除附件
     *
     * @param emailMessageId 邮件ID
     */
    default void deleteByEmailMessageId(Long emailMessageId) {
        delete(EmailAttachmentDO::getEmailMessageId, emailMessageId);
    }
} 