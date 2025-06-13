package cn.iocoder.yudao.module.email.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.email.controller.admin.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.email.dal.dataobject.ImportBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导入批次 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface ImportBatchMapper extends BaseMapperX<ImportBatchDO> {

    default PageResult<ImportBatchDO> selectPage(ImportBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImportBatchDO>()
                .likeIfPresent(ImportBatchDO::getBatchName, reqVO.getBatchName())
                .eqIfPresent(ImportBatchDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImportBatchDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImportBatchDO::getId));
    }
} 