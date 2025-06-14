package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailStatisticsRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailAttachmentDO;
import cn.iocoder.yudao.module.email.dal.mysql.EmailMessageMapper;
import cn.iocoder.yudao.module.email.dal.mysql.EmailAttachmentMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Resource
    private EmailAttachmentMapper emailAttachmentMapper;

    @Resource
    private EmailAttachmentService emailAttachmentService;

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
        
        try {
            // 邮件总数
            Long totalCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false));
            statistics.setTotalCount(totalCount != null ? totalCount : 0L);
            
            // 已标记星标数量 - 注意处理NULL值
            Long starredCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false)
                .eq(EmailMessageDO::getIsStarred, true));
            statistics.setStarredCount(starredCount != null ? starredCount : 0L);
            
            // 有附件的邮件数量 - 注意处理NULL值和0值
            Long withAttachmentsCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false)
                .gt(EmailMessageDO::getAttachmentCount, 0));
            statistics.setWithAttachmentsCount(withAttachmentsCount != null ? withAttachmentsCount : 0L);
            
            // 今日新增邮件数
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1);
            Long todayCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false)
                .between(EmailMessageDO::getCreateTime, todayStart, todayEnd));
            statistics.setTodayCount(todayCount != null ? todayCount : 0L);
            
            // 本周新增邮件数
            LocalDateTime weekStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
            Long weekCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false)
                .ge(EmailMessageDO::getCreateTime, weekStart));
            statistics.setWeekCount(weekCount != null ? weekCount : 0L);
            
            // 本月新增邮件数
            LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            Long monthCount = emailMessageMapper.selectCount(new LambdaQueryWrapper<EmailMessageDO>()
                .eq(EmailMessageDO::getDeleted, false)
                .ge(EmailMessageDO::getCreateTime, monthStart));
            statistics.setMonthCount(monthCount != null ? monthCount : 0L);
            
            log.info("邮件统计信息 - 总数:{}, 星标:{}, 有附件:{}, 今日:{}, 本周:{}, 本月:{}", 
                totalCount, starredCount, withAttachmentsCount, todayCount, weekCount, monthCount);
            
        } catch (Exception e) {
            log.error("获取邮件统计信息失败", e);
            // 如果发生异常，返回全0的统计信息，避免返回null或负数
            statistics.setTotalCount(0L);
            statistics.setStarredCount(0L);
            statistics.setWithAttachmentsCount(0L);
            statistics.setTodayCount(0L);
            statistics.setWeekCount(0L);
            statistics.setMonthCount(0L);
        }
        
        return statistics;
    }

} 