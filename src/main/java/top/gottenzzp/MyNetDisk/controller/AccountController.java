package top.gottenzzp.MyNetDisk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.CreateImageCode;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;
import top.gottenzzp.MyNetDisk.entity.enums.VerifyRegexEnum;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.service.EmailCodeService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;
import top.gottenzzp.MyNetDisk.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户控制员
 * 用户信息表 Controller
 *
 * @author GottenZZP
 * @date 2023/12/21
 */
@RestController("userInfoController")
public class AccountController extends ABaseController{
	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";

	/**
	 * 用户信息服务
	 */
	@Resource
	private UserInfoService userInfoService;

	@Resource
	private EmailCodeService emailCodeService;

	@Resource
	private AppConfig appConfig;

	@Resource
	private RedisComponent redisComponent;

	/**
	 * 校验码
	 * 根据条件分页查询
	 *
	 * @param response 响应
	 * @param session  会话
	 * @param type     类型
	 * @throws IOException IOEXCEPTION
	 */
	@GetMapping("/checkCode")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
		CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
		String code = vCode.getCode();
		System.out.println(code);
		if (type == null || type == 0) {
			session.setAttribute(Constants.CHECK_CODE_KEY, code);
		} else {
			session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
		}
		vCode.write(response.getOutputStream());
	}

	/**
	 * 发送邮箱验证码
	 *
	 * @param session   会话
	 * @param email     电子邮件
	 * @param checkCode 校验码
	 * @param type      类型
	 * @return {@link ResponseVO}
	 */
	@RequestMapping("/sendEmailCode")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO sendEmailCode(HttpSession session,
									@VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
									@VerifyParam(required = true) String checkCode,
									@VerifyParam(required = true) Integer type) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))) {
				throw new BusinessException("验证码不正确");
			}
			emailCodeService.sendEmailCode(email, type);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
		}
	}

	/**
	 * 注册账户
	 * @param session   会话
	 * @param email		电子邮件
	 * @param nickName	昵称
	 * @param password	密码
	 * @param checkCode	校验码
	 * @param emailCode	电子邮件验证码
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/register")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO register(HttpSession session,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							   @VerifyParam(required = true) String nickName,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							   @VerifyParam(required = true) String checkCode,
							   @VerifyParam(required = true) String emailCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("验证码不正确");
			}
			userInfoService.register(email, nickName, password, emailCode);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	/**
	 * 登陆
	 * @param session 会话
	 * @param email	电子邮件
	 * @param password	密码
	 * @param checkCode	校验码
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/login")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO login(HttpSession session,
							@VerifyParam(required = true) String email,
							@VerifyParam(required = true) String password,
							@VerifyParam(required = true) String checkCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("验证码不正确");
			}
			SessionWebUserDto sessionWebUserDto = userInfoService.login(email, password);
			session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
			return getSuccessResponseVO(sessionWebUserDto);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	/**
	 * 重置密码
	 * @param session  会话
	 * @param email	电子邮件
	 * @param password	密码
	 * @param checkCode	校验码
	 * @param emailCode	电子邮件验证码
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/resetPwd")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO resetPwd(HttpSession session,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.EMAIL, max = 150) String email,
							   @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password,
							   @VerifyParam(required = true) String checkCode,
							   @VerifyParam(required = true) String emailCode) {
		try {
			if (!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
				throw new BusinessException("验证码不正确");
			}
			userInfoService.resetPwd(email, password, emailCode);
			return getSuccessResponseVO(null);
		} finally {
			session.removeAttribute(Constants.CHECK_CODE_KEY);
		}
	}

	/**
	 * 获取用户头像
	 * @param response 		响应
	 * @param userId		用户ID
	 */
	@RequestMapping("/getAvatar/{userId}")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public void getAvatar(HttpServletResponse response, @VerifyParam(required = true) @PathVariable("userId") String userId) {
		// 获取头像文件夹路径
		String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR_NAME;
		File file = new File(appConfig.getProjectFolder() + avatarFolderName);
		// 若路径不存在则创建
		if (!file.exists()) {
			file.mkdirs();
		}
		// 获取该用户头像路径
		String avatarPath = appConfig.getProjectFolder() + avatarFolderName + userId + Constants.AVATAR_SUFFIX;
		File file1 = new File(avatarPath);
		// 若头像不存在则使用默认头像
		if (!file1.exists()) {
			if (!new File(appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT).exists()) {
				printNoDefaultImage(response);
			}
			avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.AVATAR_DEFAULT;
		}
		// 返回头像
		response.setContentType("image/jpg");
		readFile(response, avatarPath);
	}

	/**
	 * 输出无默认图的信息
	 * @param response 响应
	 */
	private void printNoDefaultImage(HttpServletResponse response) {
		response.setHeader(CONTENT_TYPE, CONTENT_TYPE_VALUE);
		response.setStatus(HttpStatus.OK.value());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print("请在头像目录下放置默认头像default.jpg");
			writer.close();
		} catch (Exception e) {
			logger.error("输出无默认图失败", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	/**
	 * 获取用户信息
	 * @param session 会话
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/getUserInfo")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO getUserInfo(HttpSession session) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		return getSuccessResponseVO(sessionWebUserDto);
	}

	/**
	 * 获取用户已使用空间
	 * @param session 会话
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/getUseSpace")
	@GlobalInterceptor
	public ResponseVO getUseSpace(HttpSession session) {
		SessionWebUserDto sessionWebUserDto = getUserInfoFromSession(session);
		UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(sessionWebUserDto.getUserId());
		return getSuccessResponseVO(spaceDto);
	}

	/**
	 * 退出登陆
	 * @param session 	会话
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/logout")
	public ResponseVO logout(HttpSession session) {
		session.invalidate();
		return getSuccessResponseVO(null);
	}

	/**
	 * 更新用户头像
	 * @param session 	会话
	 * @param avatar	头像
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/updateUserAvatar")
	@GlobalInterceptor
	public ResponseVO updateUserAvatar(HttpSession session, MultipartFile avatar) {
		// 从会话中获取用户信息
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		// 获取头像文件夹路径
		String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
		// 若路径不存在则创建
		File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR_NAME);
		if (!targetFileFolder.exists()) {
			targetFileFolder.mkdirs();
		}
		// 获取该用户头像路径
		File targetFile = new File(targetFileFolder.getPath() + "/" + webUserDto.getUserId() + Constants.AVATAR_SUFFIX);
		// 将头像保存到本地
		try {
			avatar.transferTo(targetFile);
		} catch (Exception e) {
			logger.error("上传头像失败", e);
		}
		// 更新数据库中的头像路径
		UserInfo userInfo = new UserInfo();
		// 若本地上传了头像，则不使用QQ头像，将QQ头像设置为空
		userInfo.setQqAvatar("");
		userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
		webUserDto.setAvatar(null);
		session.setAttribute(Constants.SESSION_KEY, webUserDto);
		return getSuccessResponseVO(null);
	}

	/**
	 * 更新用户密码
	 * @param session 	会话
	 * @param password	密码
	 * @return	{@link ResponseVO}
	 */
	@RequestMapping("/updatePassword")
	@GlobalInterceptor(checkParams = true)
	public ResponseVO updatePassword(HttpSession session,
									 @VerifyParam(required = true, regex = VerifyRegexEnum.PASSWORD, min = 8, max = 18) String password) {
		SessionWebUserDto webUserDto = getUserInfoFromSession(session);
		UserInfo userInfo = new UserInfo();
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfoService.updateUserInfoByUserId(userInfo, webUserDto.getUserId());
		return getSuccessResponseVO(null);
	}

	@RequestMapping("/qqlogin")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqlogin(HttpSession session, String callbackUrl) throws UnsupportedEncodingException {
		String state = StringTools.getRandomNumber(Constants.LENGTH_30);
		if (!StringTools.isEmpty(callbackUrl)) {
			session.setAttribute(state, callbackUrl);
		}
		String url = String.format(appConfig.getQqUrlAuthorization(), appConfig.getQqAppId(), URLEncoder.encode(appConfig.getQqUrlRedirect(),"utf-8"), state);
		return getSuccessResponseVO(url);
	}

	@RequestMapping("/qqlogin/callback")
	@GlobalInterceptor(checkParams = true, checkLogin = false)
	public ResponseVO qqloginCallback(HttpSession session, @VerifyParam(required = true) String code, @VerifyParam(required = true) String state) {
		SessionWebUserDto sessionWebUserDto = userInfoService.qqLogin(code);
		session.setAttribute(Constants.SESSION_KEY, sessionWebUserDto);
		Map<String, Object> result = new HashMap<>();
		result.put("callbackUrl", session.getAttribute(state));
		result.put("userInfo", sessionWebUserDto);
		return getSuccessResponseVO(result);
	}
}