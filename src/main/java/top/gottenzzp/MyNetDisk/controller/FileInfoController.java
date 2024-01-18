package top.gottenzzp.MyNetDisk.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UploadResultDto;
import top.gottenzzp.MyNetDisk.entity.enums.FileCategoryEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileDelFlagEnums;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

	@RequestMapping("/getImage/{imageFolder}/{imageName}")
	public void getImage(HttpServletResponse response, @PathVariable("imageFolder") String imageFolder, @PathVariable("imageName") String imageName) {
		super.getImage(response, imageFolder, imageName);
	}

	@RequestMapping("/ts/getVideoInfo/{fileId}")
	public void getVideoInfo(HttpServletResponse response, HttpSession session, @PathVariable("fileId") String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}

	@RequestMapping("/getFile/{fileId}")
	public void getFile(HttpServletResponse response, HttpSession session, @PathVariable("fileId") String fileId) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		super.getFile(response, fileId, webUserDto.getUserId());
	}
}