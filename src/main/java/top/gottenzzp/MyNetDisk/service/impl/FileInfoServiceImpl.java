package top.gottenzzp.MyNetDisk.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UploadResultDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;
import top.gottenzzp.MyNetDisk.entity.enums.*;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.query.SimplePage;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.mappers.FileInfoMapper;
import top.gottenzzp.MyNetDisk.mappers.UserInfoMapper;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.utils.StringTools;


/**
 * 文件信息表 业务接口实现
 * @author gottenzzp
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
	private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

	@Resource
	private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private AppConfig appConfig;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<FileInfo> findListByParam(FileInfoQuery param) {
		return this.fileInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(FileInfoQuery param) {
		return this.fileInfoMapper.selectCount(param);
	}

	/**
	 * 按页面查找列表
	 * 分页查询方法
	 *
	 * @param param 参数
	 * @return {@link PaginationResultVO}<{@link FileInfo}>
	 */
	@Override
	public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
		// 获取总记录数
		int count = this.findCountByParam(param);
		// 获取每页显示的记录数
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
		// 创建分页对象
		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		// 设置分页对象
		param.setSimplePage(page);
		// 获取分页数据
		List<FileInfo> list = this.findListByParam(param);
		// 返回分页结果
        return new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(FileInfo bean) {
		return this.fileInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<FileInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.fileInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
		StringTools.checkParam(param);
		return this.fileInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(FileInfoQuery param) {
		StringTools.checkParam(param);
		return this.fileInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据FileIdAndUserId获取对象
	 */
	@Override
	public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId修改
	 */
	@Override
	public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
		return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
	}

	/**
	 * 根据FileIdAndUserId删除
	 */
	@Override
	public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
		return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
	}

	/**
	 * 上传文件
	 *
	 * @param webUserDto web用户dto
	 * @param fileId     文件id
	 * @param file       文件
	 * @param fileName   文件名
	 * @param filePid    文件pid
	 * @param fileMd5    文件md5
	 * @param chunkIndex 块索引
	 * @param chunks     大块
	 * @return {@link UploadResultDto}
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName,
									  String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
		UploadResultDto resultDto = null;
		try {
			resultDto = new UploadResultDto();
			// 若文件id为空, 则生成一个（因为当第一个文件分片传过来的时候，数据库里是没有该文件的id的，所以当第一个分片传来时需要分配一个id）
			if (StringTools.isEmpty(fileId)) {
				fileId = StringTools.getRandomString(Constants.LENGTH_10);
			}
			resultDto.setFileId(fileId);
			Date curDate = new Date();
			// 从redis中获取用户信息
			UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
			// 若索引为0, 则表示是第一次上传，需要判断用户空间是否足够
			if (chunkIndex == 0) {
				FileInfoQuery infoQuery = new FileInfoQuery();
				infoQuery.setFileMd5(fileMd5);
				infoQuery.setSimplePage(new SimplePage(0, 1));
				infoQuery.setStatus(FileStatusEnums.USING.getStatus());
				// 搜索数据库中是否有相同的文件
				List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);
				// 如果有，则直接秒传
				if (!fileInfoList.isEmpty()) {
					FileInfo fileInfo = fileInfoList.get(0);
					// 判断用户空间是否足够（文件大小+用户已使用的空间大小如果大于用户总的空间大小，则表示用户空间不够了）
					if (fileInfo.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
						throw new BusinessException(ResponseCodeEnum.CODE_904);
					}
					// 更新文件信息
					// 因为用户上传的文件名称可能和系统中的文件名称一致，所以需要更换文件名称
					fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
					fileInfo.setFileId(fileId);
					fileInfo.setFilePid(filePid);
					fileInfo.setUserId(webUserDto.getUserId());
					fileInfo.setCreateTime(curDate);
					fileInfo.setLastUpdateTime(curDate);
					fileInfo.setStatus(FileStatusEnums.USING.getStatus());
					fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
					fileInfo.setFileMd5(fileMd5);
					fileInfo.setFileName(fileName);
					fileInfoMapper.insert(fileInfo);
					resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
					// 更新用户空间
					updateUserSpace(webUserDto, fileInfo.getFileSize());
					return resultDto;
				}
			}
			// 否则正常上传
			// 判断用户空间是否足够（因为每次切片上传，都会在redis中续存已上传的分片大小，所以在判断用户剩余空间时，可以直接在redis中取，
			// 					 从而减少数据库IO操作）
			Long fileTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
			// 当前文件切片的大小+文件之前上传的切片大小+用户已使用的空间大小如果大于用户总的空间大小，则表示用户空间不够了
			if (file.getSize() + fileTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
				throw new BusinessException(ResponseCodeEnum.CODE_904);
			}
			// 暂存临时目录
			String tempFolderPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
			String curUserFolderName = webUserDto.getUserId() + fileId;
			File tempFolder = new File(tempFolderPath + curUserFolderName);
			if (!tempFolder.exists()) {
				tempFolder.mkdirs();
			}
			File newFile = new File(tempFolder.getPath() + "/" + chunkIndex);
			file.transferTo(newFile);
			// 如果不是最后一个分片，则将每次的分片大小累加到redis中，方便后续计算用户空间时不用每次都从数据库里取，减少数据库的IO操作
			if (chunkIndex < chunks - 1) {
				// 保存当前分片大小到redis
				redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
				resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
				return resultDto;
			}
		} catch (Exception e) {
			logger.error("文件上传失败", e);
		}
		return resultDto;
    }

	/**
	 * 自动重命名
	 *
	 * @param filePid  文件pid
	 * @param userId   用户id
	 * @param fileName 文件名称
	 * @return {@link String}
	 */
	private String autoRename(String filePid, String userId, String fileName) {
		// 去数据库搜索是否有重名的文件
		FileInfoQuery infoQuery = new FileInfoQuery();
		infoQuery.setFilePid(filePid);
		infoQuery.setUserId(userId);
		infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		infoQuery.setFileName(fileName);
		Integer count = fileInfoMapper.selectCount(infoQuery);
		// 如果有重名的文件，则重命名
		if (count > 0) {
			fileName = StringTools.rename(fileName);
		}
		return fileName;
	}

	/**
	 * 更新用户空间
	 *
	 * @param webUserDto web用户dto
	 * @param useSpace   使用空间
	 */
	private void updateUserSpace(SessionWebUserDto webUserDto, Long useSpace) {
		// 更新用户空间, count为更新的条数
		Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSpace, null);
		// 如果更新失败，则抛出异常
		if (count == 0) {
			throw new BusinessException(ResponseCodeEnum.CODE_904);
		}
		// 更新redis中的用户空间
		UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
		spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
		redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
	}
}