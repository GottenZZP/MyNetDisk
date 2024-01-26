package top.gottenzzp.MyNetDisk.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.enums.PageSize;
import top.gottenzzp.MyNetDisk.entity.enums.ResponseCodeEnum;
import top.gottenzzp.MyNetDisk.entity.enums.ShareValidTypeEnums;
import top.gottenzzp.MyNetDisk.entity.query.FileShareQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileShare;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.query.SimplePage;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.mappers.FileShareMapper;
import top.gottenzzp.MyNetDisk.service.FileShareService;
import top.gottenzzp.MyNetDisk.utils.DateUtil;
import top.gottenzzp.MyNetDisk.utils.StringTools;


/**
 *  业务接口实现
 */
@Service("fileShareService")
public class FileShareServiceImpl implements FileShareService {

	@Resource
	private FileShareMapper<FileShare, FileShareQuery> fileShareMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileShare> findListByParam(FileShareQuery param) {
		return this.fileShareMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(FileShareQuery param) {
		return this.fileShareMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<FileShare> findListByPage(FileShareQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<FileShare> list = this.findListByParam(param);
		PaginationResultVO<FileShare> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(FileShare bean) {
		return this.fileShareMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<FileShare> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileShareMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileShare> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileShareMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(FileShare bean, FileShareQuery param) {
		StringTools.checkParam(param);
		return this.fileShareMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(FileShareQuery param) {
		StringTools.checkParam(param);
		return this.fileShareMapper.deleteByParam(param);
	}

	/**
	 * 根据ShareId获取对象
	 */
	@Override
	public FileShare getFileShareByShareId(String shareId) {
		return this.fileShareMapper.selectByShareId(shareId);
	}

	/**
	 * 根据ShareId修改
	 */
	@Override
	public Integer updateFileShareByShareId(FileShare bean, String shareId) {
		return this.fileShareMapper.updateByShareId(bean, shareId);
	}

	/**
	 * 根据ShareId删除
	 */
	@Override
	public Integer deleteFileShareByShareId(String shareId) {
		return this.fileShareMapper.deleteByShareId(shareId);
	}

	/**
	 * 保存共享信息
	 *
	 * @param fileShare 文件分享类
	 */
	@Override
	public void saveShare(FileShare fileShare) {
		// 获取要分享的失效时间
		ShareValidTypeEnums typeEnums = ShareValidTypeEnums.getByType(fileShare.getValidType());
		if (typeEnums == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		// 若失效时间不是永久，则设置失效时间
		if (ShareValidTypeEnums.FOREVER != typeEnums) {
			fileShare.setExpireTime(DateUtil.getAfterDate(typeEnums.getDays()));
		}
		Date curDate = new Date();
		fileShare.setShareTime(curDate);
		// 若提取码为空，则随机生成一个
		if (StringTools.isEmpty(fileShare.getCode())) {
			fileShare.setCode(StringTools.getRandomString(Constants.LENGTH_5));
		}
		fileShare.setShareId(StringTools.getRandomString(Constants.LENGTH_20));
		// 插入数据库
		fileShareMapper.insert(fileShare);
	}

	/**
	 * 批量删除共享文件
	 *
	 * @param shareIds 共享ID
	 * @param userId   用户id
	 */
	@Override
    public void batchDeletionSharedFiles(String[] shareIds, String userId) {
		Integer count = fileShareMapper.batchDeletionSharedFiles(shareIds, userId);
		if (count != shareIds.length) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
	}
}