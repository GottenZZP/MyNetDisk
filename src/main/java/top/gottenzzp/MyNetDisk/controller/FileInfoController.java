package top.gottenzzp.MyNetDisk.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UploadResultDto;
import top.gottenzzp.MyNetDisk.entity.enums.FileCategoryEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileDelFlagEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileFolderTypeEnums;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gottenzzp.MyNetDisk.utils.CopyTools;
import top.gottenzzp.MyNetDisk.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author GottenZZP
 * 文件信息表 Controller
 */
@RestController("fileInfoController")
@RequestMapping("/file")
public class FileInfoController extends CommonFileController {
	@Resource
	private FileInfoService fileInfoService;

	/**
	 * 加载数据列表
	 *
	 * @param session  会话
	 * @param query    查询
	 * @param category 类别
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/loadDataList")
	@GlobalInterceptor
	public ResponseVO loadDataList(HttpSession session, FileInfoQuery query, String category) {
		// 搜索文件类别
		FileCategoryEnums categoryEnums = FileCategoryEnums.getByCode(category);
		// 若文件类别不为空，则设置查找条件
		if (categoryEnums != null) {
			query.setFileCategory(categoryEnums.getCategory());
		}
		// 获取当前用户的文件信息
		query.setUserId(getUserInfoFromSession(session).getUserId());
		// 设置排序条件
		query.setOrderBy("last_update_time desc");
		// 设置删除状态为使用中
		query.setDelFlag(FileDelFlagEnums.USING.getFlag());
		// 分页查询
		PaginationResultVO<FileInfo> page = fileInfoService.findListByPage(query);
		// 将分页查询的结果转换为前端需要的格式:FileInfoVO
		return getSuccessResponseVO(convert2PaginationVO(page, FileInfoVO.class));
	}

	/**
	 * 上传档案
	 *
	 * @param session    会话
	 * @param fileId     文件id
	 * @param file       文件
	 * @param fileName   文件名
	 * @param filePid    文件pid
	 * @param fileMd5    文件md5
	 * @param chunkIndex 分片索引
	 * @param chunks     所有分片
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/uploadFile")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO uploadFile(HttpSession session,
								 String fileId,
								 MultipartFile file,
								 @VerifyParam(required = true) String fileName,
								 @VerifyParam(required = true) String filePid,
								 @VerifyParam(required = true) String fileMd5,
								 @VerifyParam(required = true) Integer chunkIndex,
								 @VerifyParam(required = true) Integer chunks) {
		// 获取当前用户的文件信息
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		UploadResultDto uploadResultDto = fileInfoService.uploadFile(webUserDto, fileId, file, fileName, filePid, fileMd5, chunkIndex, chunks);
		return getSuccessResponseVO(uploadResultDto);
	}

	/**
	 * 获取图像
	 *
	 * @param response	  响应
	 * @param imageFolder 图像文件夹
	 * @param imageName   图像名称
	 */
	@Override
	@RequestMapping("/getImage/{imageFolder}/{imageName}")
	public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
		super.getImage(response, imageFolder, imageName);
	}

	/**
	 * 获取视频信息
	 *
	 * @param response 响应
	 * @param session  会话
	 * @param fileId   文件身份证件
	 */
	@RequestMapping("/ts/getVideoInfo/{fileId}")
	public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}

	/**
	 * 获取文件
	 *
	 * @param response 响应
	 * @param session  会话
	 * @param fileId   文件id
	 */
	@RequestMapping("/getFile/{fileId}")
	public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}

	/**
	 * 创建新文件夹
	 *
	 * @param session    会话
	 * @param filePid    文件pid
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/newFoloder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO newFoloder(HttpSession session,
						@VerifyParam(required = true) String filePid,
						@VerifyParam(required = true) String fileName) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		FileInfo fileInfo = fileInfoService.newFolder(filePid, webUserDto.getUserId(), fileName);
		return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
	}

	/**
	 * 获取path路径的文件结构信息
	 *
	 * @param path   路径
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/getFolderInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getFolderInfo(HttpSession session,
								 @VerifyParam(required = true) String path) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		return super.getFolderInfo(path, webUserDto.getUserId());
	}

	/**
	 * 文件重命名
	 *
	 * @param session  会话
	 * @param fileId   文件id
	 * @param fileName 文件名称
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/rename")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO rename(HttpSession session,
							 @VerifyParam(required = true) String fileId,
							 @VerifyParam(required = true) String fileName) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		FileInfo fileInfo = fileInfoService.rename(fileId, webUserDto.getUserId(), fileName);
		return getSuccessResponseVO(CopyTools.copy(fileInfo, FileInfoVO.class));
	}

	/**
	 * 加载文件夹目录列表
	 *
	 * @param session        会话
	 * @param filePid        文件pid
	 * @param currentFileIds 当前文件id列表
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/loadAllFolder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO loadAllFolder(HttpSession session,
									@VerifyParam(required = true) String filePid,
									String currentFileIds) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		// 搜索当前目录下的所有文件夹
		FileInfoQuery infoQuery = new FileInfoQuery();
		infoQuery.setUserId(webUserDto.getUserId());
		infoQuery.setFilePid(filePid);
		infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
		if (!StringTools.isEmpty(currentFileIds)) {
			infoQuery.setExcludeFileIdArray(currentFileIds.split(","));
		}
		infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
		infoQuery.setOrderBy("create_time desc");
		List<FileInfo> infoList = fileInfoService.findListByParam(infoQuery);
		return getSuccessResponseVO(CopyTools.copyList(infoList, FileInfoVO.class));
	}

	/**
	 * 移动文件至其他文件夹
	 *
	 * @param session 会话
	 * @param fileIds 文件ids
	 * @param filePid 文件pid
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/changeFileFolder")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO changeFileFolder(HttpSession session,
									@VerifyParam(required = true) String fileIds,
									   @VerifyParam(required = true) String filePid) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		fileInfoService.changeFileFolder(fileIds, filePid, webUserDto.getUserId());
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/createDownloadUrl/{fileId}")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO createDownloadUrl(HttpSession session,
									   @VerifyParam(required = true) @PathVariable("fileId") String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		return super.createDownloadUrl(fileId, webUserDto.getUserId());
	}

	@Override
	@RequestMapping("/download/{code}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void download(HttpServletRequest request, HttpServletResponse response,
							   @VerifyParam(required = true) @PathVariable("code") String code) throws Exception {
		super.download(request, response, code);
	}
}