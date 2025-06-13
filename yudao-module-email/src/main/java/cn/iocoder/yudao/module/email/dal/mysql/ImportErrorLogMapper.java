package cn.iocoder.yudao.module.email.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportErrorLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 导入错误日志 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface ImportErrorLogMapper extends BaseMapperX<ImportErrorLogDO> {

    default List<ImportErrorLogDO> selectListByBatchId(Long batchId) {
        return selectList(new LambdaQueryWrapperX<ImportErrorLogDO>()
                .eq(ImportErrorLogDO::getImportBatchId, batchId)
                .orderByDesc(ImportErrorLogDO::getId));
    }
} 