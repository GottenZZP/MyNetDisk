package top.gottenzzp.MyNetDisk.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UploadResultDto;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;


/**
 * 文件信息表 业务接口
 */
public interface FileInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<FileInfo> findListByParam(FileInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(FileInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(FileInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<FileInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<FileInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(FileInfo bean,FileInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(FileInfoQuery param);

	/**
	 * 根据FileIdAndUserId查询对象
	 */
	FileInfo getFileInfoByFileIdAndUserId(String fileId,String userId);


	/**
	 * 根据FileIdAndUserId修改
	 */
	Integer updateFileInfoByFileIdAndUserId(FileInfo bean,String fileId,String userId);


	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteFileInfoByFileIdAndUserId(String fileId,String userId);

	UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

	FileInfo newFolder(String filePid, String userId, String folderName);

	FileInfo rename(String fileId, String userId, String fileName);

	void changeFileFolder(String fileIds, String filePid, String userId);
}