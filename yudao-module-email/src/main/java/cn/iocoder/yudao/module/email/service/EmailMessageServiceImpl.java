package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailStatisticsRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import cn.iocoder.yudao.module.email.dal.mysql.EmailMessageMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.email.enums.ErrorCodeConstants.EMAIL_MESSAGE_NOT_EXISTS;

/**
 * 邮件消息 Service 实现类
 *
 * @author 方总牛逼
 */
@Service
@Slf4j
public class EmailMessageServiceImpl implements EmailMessageService {

    @Resource
    private EmailMessageMapper emailMessageMapper;

    @Override
    public PageResult<EmailMessageDO> getEmailMessagePage(@Valid EmailMessagePageReqVO pageReqVO) {
        return emailMessageMapper.selectPage(pageReqVO);
    }

    @Override
    public EmailMessageDO getEmailMessage(Long id) {
        EmailMessageDO emailMessage = emailMessageMapper.selectById(id);
        if (emailMessage == null) {
            throw exception(EMAIL_MESSAGE_NOT_EXISTS);
        }
        return emailMessage;
    }

    @Override
    public Boolean toggleStar(Long id) {
        EmailMessageDO emailMessage = getEmailMessage(id);
        Boolean newStarStatus = !Boolean.TRUE.equals(emailMessage.getIsStarred());
        
        EmailMessageDO updateObj = new EmailMessageDO();
        updateObj.setId(id);
        updateObj.setIsStarred(newStarStatus);
        emailMessageMapper.updateById(updateObj);
        
        return newStarStatus;
    }

    @Override
    public void deleteEmailMessage(Long id) {
        // 先验证邮件是否存在
        getEmailMessage(id);
        // 删除邮件
        emailMessageMapper.deleteById(id);
    }

    @Override
    public EmailStatisticsRespVO getEmailStatistics() {
        EmailStatisticsRespVO statistics = new EmailStatisticsRespVO();
        
        // 邮件总数
        statistics.setTotalCount(emailMessageMapper.selectCount());
        
        // 已标记星标数量
        statistics.setStarredCount(emailMessageMapper.selectCount(EmailMessageDO::getIsStarred, true));
        
        // 有附件的邮件数量 - 暂时设为0，后续可以用SQL实现
        statistics.setWithAttachmentsCount(0L);
        
        // 今日、本周、本月新增数量可以根据需要实现
        // 这里先设置为0，后续可以根据create_time字段统计
        statistics.setTodayCount(0L);
        statistics.setWeekCount(0L);
        statistics.setMonthCount(0L);
        
        return statistics;
    }

} 