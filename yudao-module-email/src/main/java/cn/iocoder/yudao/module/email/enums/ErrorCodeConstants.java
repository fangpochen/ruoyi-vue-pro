package cn.iocoder.yudao.module.email.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 邮件管理模块错误码枚举类
 *
 * 邮件管理模块，使用 1-003-xxx 段
 */
public interface ErrorCodeConstants {

    // ========== 邮件消息 1-003-001-xxx ==========
    ErrorCode EMAIL_MESSAGE_NOT_EXISTS = new ErrorCode(1_003_001_001, "邮件消息不存在");

    // ========== 邮件附件 1-003-002-xxx ==========
    ErrorCode EMAIL_ATTACHMENT_NOT_EXISTS = new ErrorCode(1_003_002_001, "邮件附件不存在");
    ErrorCode EMAIL_ATTACHMENT_DOWNLOAD_FAIL = new ErrorCode(1_003_002_002, "邮件附件下载失败");

}