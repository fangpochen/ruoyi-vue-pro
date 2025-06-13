package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessagePageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailStatisticsRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;

import javax.validation.Valid;

/**
 * 邮件消息 Service 接口
 *
 * @author 方总牛逼
 */
public interface EmailMessageService {

    /**
     * 获得邮件消息分页
     *
     * @param pageReqVO 分页查询
     * @return 邮件消息分页
     */
    PageResult<EmailMessageDO> getEmailMessagePage(@Valid EmailMessagePageReqVO pageReqVO);

    /**
     * 获得邮件消息
     *
     * @param id 编号
     * @return 邮件消息
     */
    EmailMessageDO getEmailMessage(Long id);

    /**
     * 切换邮件星标状态
     *
     * @param id 编号
     * @return 切换后的星标状态
     */
    Boolean toggleStar(Long id);

    /**
     * 删除邮件消息
     *
     * @param id 编号
     */
    void deleteEmailMessage(Long id);

    /**
     * 获取邮件统计信息
     *
     * @return 统计信息
     */
    EmailStatisticsRespVO getEmailStatistics();

} 