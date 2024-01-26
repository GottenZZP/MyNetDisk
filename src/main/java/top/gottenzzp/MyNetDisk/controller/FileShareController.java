package top.gottenzzp.MyNetDisk.controller;

import java.util.List;

import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.query.FileShareQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileShare;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.FileShareService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 *  Controller
 */
@RestController("fileShareController")
@RequestMapping("/share")
public class FileShareController extends ABaseController{

	@Resource
	private FileShareService fileShareService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadShareList")
	@GlobalInterceptor
	public ResponseVO loadShareList(HttpSession session, FileShareQuery query){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		query.setOrderBy("share_time desc");
		query.setUserId(webUserDto.getUserId());
		return getSuccessResponseVO(fileShareService.findListByPage(query));
	}

	/**
	 * 分享文件
	 *
	 * @param session   会话
	 * @param fileId    文件id
	 * @param validType 有效类型
	 * @param code      提取码 前端若传了就用前端的，否则后端随机生成一个随机数
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/shareFile")
	@GlobalInterceptor(checkLogin = true)
	public ResponseVO shareFile(HttpSession session,
								@VerifyParam(required = true) String fileId,
								@VerifyParam(required = true) Integer validType,
								String code){
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		FileShare fileShare = new FileShare();
		fileShare.setValidType(validType);
		fileShare.setCode(code);
		fileShare.setFileId(fileId);
		fileShare.setUserId(webUserDto.getUserId());
		fileShareService.saveShare(fileShare);
		return getSuccessResponseVO(fileShare);
	}
}