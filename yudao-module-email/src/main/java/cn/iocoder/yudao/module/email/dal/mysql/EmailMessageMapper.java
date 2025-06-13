package cn.iocoder.yudao.module.email.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件消息 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface EmailMessageMapper extends BaseMapperX<EmailMessageDO> {

    default PageResult<EmailMessageDO> selectPage(EmailMessagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmailMessageDO>()
                .likeIfPresent(EmailMessageDO::getSubject, reqVO.getSubject())
                .likeIfPresent(EmailMessageDO::getSender, reqVO.getSender())
                .likeIfPresent(EmailMessageDO::getRecipients, reqVO.getRecipient())
                .likeIfPresent(EmailMessageDO::getOriginalPath, reqVO.getOriginalPath())
                .eqIfPresent(EmailMessageDO::getIsStarred, reqVO.getIsStarred())
                .betweenIfPresent(EmailMessageDO::getSendDate, reqVO.getSendDate())
                .orderByDesc(EmailMessageDO::getSendDate));
    }

} 