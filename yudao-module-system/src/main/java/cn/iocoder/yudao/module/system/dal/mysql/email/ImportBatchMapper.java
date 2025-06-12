package cn.iocoder.yudao.module.system.dal.mysql.email;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.email.vo.ImportBatchPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.email.ImportBatchDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 导入批次 Mapper
 *
 * @author 方总牛逼
 */
@Mapper
public interface ImportBatchMapper extends BaseMapperX<ImportBatchDO> {

    /**
     * 分页查询导入批次
     */
    default PageResult<ImportBatchDO> selectPage(ImportBatchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ImportBatchDO>()
                .likeIfPresent(ImportBatchDO::getBatchName, reqVO.getBatchName())
                .likeIfPresent(ImportBatchDO::getZipFilename, reqVO.getZipFilename())
                .eqIfPresent(ImportBatchDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ImportBatchDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ImportBatchDO::getId)
        );
    }

} 