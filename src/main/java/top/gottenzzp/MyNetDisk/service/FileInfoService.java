package top.gottenzzp.MyNetDisk.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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
	 * 通过文件id和使用者id更新文件信息
	 * 根据FileIdAndUserId修改
	 *
	 * @param bean   豆
	 * @param fileId 文件id
	 * @param userId 使用者id
	 * @return {@link Integer}
	 */
	Integer updateFileInfoByFileIdAndUserId(FileInfo bean,String fileId,String userId);


	/**
	 * 通过文件id和使用者id删除文件信息
	 * 根据FileIdAndUserId删除
	 *
	 * @param fileId 文件id
	 * @param userId 使用者id
	 * @return {@link Integer}
	 */
	Integer deleteFileInfoByFileIdAndUserId(String fileId,String userId);

	/**
	 * 更新文件信息
	 *
	 * @param webUserDto web用户dto
	 * @param fileId     文件id
	 * @param file       文件
	 * @param fileName   文件名称
	 * @param filePid    文件pid
	 * @param fileMd5    文件md5
	 * @param chunkIndex 块索引
	 * @param chunks     大块
	 * @return {@link UploadResultDto}
	 */
	UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName, String filePid, String fileMd5, Integer chunkIndex, Integer chunks);

	/**
	 * 创建新文件夹
	 *
	 * @param filePid    文件pid
	 * @param userId     使用者id
	 * @param folderName 文件夹名称
	 * @return {@link FileInfo}
	 */
	FileInfo newFolder(String filePid, String userId, String folderName);

	/**
	 * 文件重命名
	 *
	 * @param fileId   文件id
	 * @param userId   使用者id
	 * @param fileName 文件名称
	 * @return {@link FileInfo}
	 */
	FileInfo rename(String fileId, String userId, String fileName);

	/**
	 * 移动文件和文件夹
	 *
	 * @param fileIds 文件ids
	 * @param filePid 文件pid
	 * @param userId  使用者id
	 */
	void changeFileFolder(String fileIds, String filePid, String userId);

	/**
	 * 删除文件进回收站
	 *
	 * @param userId  使用者id
	 * @param fileIds 文件ids
	 */
	void removeFile2RecycleBatch(String userId, String fileIds);

	/**
	 * 批量恢复文件
	 *
	 * @param userId  用户id
	 * @param fileIds 文件ids
	 */
	void recoverFileBatch(String userId, String fileIds);

	/**
	 * 批量删除文件
	 *
	 * @param userId  用户id
	 * @param fileIds 文件ids
	 * @param adminOp 管理员操作
	 */
	void delFileBatch(String userId, String fileIds, Boolean adminOp);

	Long getUserUseSpace(@Param("userId") String userId);

    void checkRootFilePid(String rootFilePid, String userId, String fileId);
}