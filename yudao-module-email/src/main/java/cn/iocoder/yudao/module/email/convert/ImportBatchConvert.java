package cn.iocoder.yudao.module.email.convert;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchRespVO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportBatchDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 导入批次 Convert
 *
 * @author 方总牛逼
 */
@Mapper
public interface ImportBatchConvert {

    ImportBatchConvert INSTANCE = Mappers.getMapper(ImportBatchConvert.class);

    ImportBatchRespVO convert(ImportBatchDO bean);

    PageResult<ImportBatchRespVO> convertPage(PageResult<ImportBatchDO> page);
} 