package cn.iocoder.yudao.module.email.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportBatchDO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportErrorLogDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    /**
     * 获得导入批次分页
     *
     * @param pageReqVO 分页查询
     * @return 导入批次分页
     */
    PageResult<ImportBatchDO> getImportBatchPage(ImportBatchPageReqVO pageReqVO);

    /**
     * 获得指定批次的错误日志列表
     *
     * @param batchId 批次编号
     * @return 错误日志列表
     */
    List<ImportErrorLogDO> getImportErrorLogs(Long batchId);

    /**
     * 获得导入状态
     *
     * @param batchId 批次编号
     * @return 导入状态
     */
    ImportBatchRespVO getImportStatus(Long batchId);
}