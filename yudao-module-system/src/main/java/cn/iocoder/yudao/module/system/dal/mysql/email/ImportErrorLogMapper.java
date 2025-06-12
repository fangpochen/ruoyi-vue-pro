package cn.iocoder.yudao.module.system.dal.mysql.email;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.email.ImportErrorLogDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 导入错误日志 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface ImportErrorLogMapper extends BaseMapperX<ImportErrorLogDO> {

    /**
     * 根据导入批次ID查询错误日志列表
     */
    default List<ImportErrorLogDO> selectListByBatchId(Long importBatchId) {
        return selectList(new LambdaQueryWrapperX<ImportErrorLogDO>()
                .eq(ImportErrorLogDO::getImportBatchId, importBatchId)
                .orderByDesc(ImportErrorLogDO::getId)
        );
    }

} 