package cn.iocoder.yudao.module.email.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.email.controller.admin.vo.EmailMessageRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.EmailMessageDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 邮件消息 Convert
 *
 * @author 方总牛逼
 */
@Mapper
public interface EmailMessageConvert {

    EmailMessageConvert INSTANCE = Mappers.getMapper(EmailMessageConvert.class);

    @Mapping(target = "recipients", expression = "java(parseJsonToList(bean.getRecipients()))")
    @Mapping(target = "ccRecipients", expression = "java(parseJsonToList(bean.getCcRecipients()))")
    @Mapping(target = "bccRecipients", expression = "java(parseJsonToList(bean.getBccRecipients()))")
    EmailMessageRespVO convert(EmailMessageDO bean);

    default List<String> parseJsonToList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JsonUtils.parseArray(json, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    default PageResult<EmailMessageRespVO> convertPage(PageResult<EmailMessageDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

    List<EmailMessageRespVO> convertList(List<EmailMessageDO> list);

}