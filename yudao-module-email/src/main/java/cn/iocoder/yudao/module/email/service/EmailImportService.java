package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 邮件导入 Service 接口
 *
 * @author 方总牛逼
 */
public interface EmailImportService {

    /**
     * 上传并处理 ZIP 文件
     *
     * @param file ZIP 文件
     * @return 导入批次信息
     */
    ImportBatchRespVO uploadZipFile(MultipartFile file);

}