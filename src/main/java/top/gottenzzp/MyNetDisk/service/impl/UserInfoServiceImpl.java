package top.gottenzzp.MyNetDisk.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.QQInfoDto;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;
import top.gottenzzp.MyNetDisk.entity.enums.PageSize;
import top.gottenzzp.MyNetDisk.entity.enums.UserStatusEnum;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.query.SimplePage;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.mappers.UserInfoMapper;
import top.gottenzzp.MyNetDisk.service.EmailCodeService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;
import top.gottenzzp.MyNetDisk.utils.JsonUtils;
import top.gottenzzp.MyNetDisk.utils.OKHttpUtils;
import top.gottenzzp.MyNetDisk.utils.StringTools;


/**
 * 用户信息表 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private EmailCodeService emailCodeService;

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private AppConfig appConfig;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmailAndQqOpenIdAndNickName(String email, String qqOpenId, String nickName) {
		return this.userInfoMapper.selectByEmailAndQqOpenIdAndNickName(email, qqOpenId, nickName);
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName修改
	 */
	@Override
	public Integer updateUserInfoByEmailAndQqOpenIdAndNickName(UserInfo bean, String email, String qqOpenId, String nickName) {
		return this.userInfoMapper.updateByEmailAndQqOpenIdAndNickName(bean, email, qqOpenId, nickName);
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName删除
	 */
	@Override
	public Integer deleteUserInfoByEmailAndQqOpenIdAndNickName(String email, String qqOpenId, String nickName) {
		return this.userInfoMapper.deleteByEmailAndQqOpenIdAndNickName(email, qqOpenId, nickName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String nickName, String password, String emailCode) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo != null) {
			throw new BusinessException("邮箱账号已存在！");
		}
		UserInfo nickNameUser = userInfoMapper.selectByNickName(nickName);
		if (nickNameUser != null) {
			throw new BusinessException("昵称已存在！");
		}
		// 检验邮箱验证码
		emailCodeService.checkCode(email, emailCode);

		String userId = StringTools.getRandomNumber(Constants.LENGTH_15);
		userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setNickName(nickName);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMD5(password));
		userInfo.setRegisterTime(new Date());
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		userInfo.setUseSpace(0L);
		SysSettingsDto sysSettingDto = redisComponent.getSysSettingDto();
		userInfo.setTotalSpace(sysSettingDto.getUserInitUseSpace() * Constants.MB);
		userInfoMapper.insert(userInfo);
	}

	/**
	 * 使用邮箱登陆账号
	 * @param email    邮箱
	 * @param password 密码
	 * @return	SessionWebUserDto
	 */
	@Override
	public SessionWebUserDto login(String email, String password) {
		// 检验邮箱是否存在
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null || !userInfo.getPassword().equals(password)) {
			throw new BusinessException("账号或密码错误");
		}
		if (UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
			throw new BusinessException("账号已被禁用");
		}
		// 设置最后登陆时间
		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setLastLoginTime(new Date());
		userInfoMapper.updateByUserId(updateUserInfo, userInfo.getUserId());

		// 设置session
		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setUserId(userInfo.getUserId());
		sessionWebUserDto.setNickName(userInfo.getNickName());
		if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), email)) {
			sessionWebUserDto.setIsAdmin(true);
		} else {
			sessionWebUserDto.setIsAdmin(false);
		}

		UserSpaceDto userSpaceDto = new UserSpaceDto();
		// TODO 查询当前用户已使用空间
		userSpaceDto.setUseSpace(0L);
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
		return sessionWebUserDto;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resetPwd(String email, String password, String emailCode) {
		UserInfo userInfo = userInfoMapper.selectByEmail(email);
		if (userInfo == null) {
			throw new BusinessException("邮箱账号不存在！");
		}
		// 检验邮箱验证码
		emailCodeService.checkCode(email, emailCode);

		UserInfo updateUserInfo = new UserInfo();
		updateUserInfo.setPassword(StringTools.encodeByMD5(password));
		userInfoMapper.updateByEmail(updateUserInfo, email);
	}

	@Override
	public SessionWebUserDto qqLogin(String code) {
		// 通过回调code，获取accessToken
		String accessToken = getQQAccessToken(code);
		// 获取openId
		String qqOpenId = getQQOpenId(accessToken);

		UserInfo userInfo = userInfoMapper.selectByQqOpenId(qqOpenId);
		String avatar = null;
		if (userInfo == null) {
			QQInfoDto qqInfoDto = getQQUserInfo(accessToken, qqOpenId);
			userInfo = new UserInfo();
			String nickname = qqInfoDto.getNickname();
			nickname = nickname.length() > Constants.LENGTH_20 ? nickname.substring(0, Constants.LENGTH_20) : nickname;
			avatar = StringTools.isEmpty(qqInfoDto.getFigureurl_qq_2()) ? qqInfoDto.getFigureurl_qq_1() : qqInfoDto.getFigureurl_qq_2();

			Date date = new Date();
			userInfo.setQqOpenId(qqOpenId);
			userInfo.setNickName(nickname);
			userInfo.setRegisterTime(date);
			userInfo.setUserId(StringTools.getRandomNumber(Constants.LENGTH_15));
			userInfo.setLastLoginTime(date);
			userInfo.setUseSpace(0L);
			userInfo.setTotalSpace(redisComponent.getSysSettingDto().getUserInitUseSpace() * Constants.MB);
			userInfo.setQqAvatar(avatar);
			userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
			userInfoMapper.insert(userInfo);
			UserInfo userInfo1 = userInfoMapper.selectByQqOpenId(qqOpenId);
		} else {
			UserInfo updateUser = new UserInfo();
			updateUser.setLastLoginTime(new Date());
			String qqAvatar = userInfo.getQqAvatar();
			userInfoMapper.updateByQqOpenId(updateUser, qqOpenId);
		}
		SessionWebUserDto sessionWebUserDto = new SessionWebUserDto();
		sessionWebUserDto.setUserId(userInfo.getUserId());
		sessionWebUserDto.setNickName(userInfo.getNickName());
		sessionWebUserDto.setAvatar(avatar);
		if (ArrayUtils.contains(appConfig.getAdminEmails().split(","), userInfo.getEmail() == null ? "" : userInfo.getEmail())) {
			sessionWebUserDto.setIsAdmin(true);
		} else {
			sessionWebUserDto.setIsAdmin(false);
		}

		UserSpaceDto userSpaceDto = new UserSpaceDto();
		// TODO 查询当前用户已使用空间
		userSpaceDto.setUseSpace(0L);
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		redisComponent.saveUserSpaceUse(userInfo.getUserId(), userSpaceDto);
		return sessionWebUserDto;
	}

	private String getQQAccessToken(String code) {
		/*
		  返回结果是字符串 access_token=*&expires_in=7776000&refresh_token=* 返回错误 callback({UcWebConstants.VIEW_OBJ_RESULT_KEY:111,error_description:"error msg"})
		 */
		String accessToken = null;
		String url = null;
		try {
			url = String.format(appConfig.getQqUrlAccessToken(), appConfig.getQqAppId(), appConfig.getQqAppKey(), code, URLEncoder.encode(appConfig
					.getQqUrlRedirect(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("encode失败");
		}
		String tokenResult = OKHttpUtils.getRequest(url);
		if (tokenResult == null || tokenResult.indexOf(Constants.VIEW_OBJ_RESULT_KEY) != -1) {
			logger.error("获取qqToken失败:{}", tokenResult);
			throw new BusinessException("获取qqToken失败");
		}
		String[] params = tokenResult.split("&");
		if (params != null && params.length > 0) {
			for (String p : params) {
				if (p.indexOf("access_token") != -1) {
					accessToken = p.split("=")[1];
					break;
				}
			}
		}
		return accessToken;
	}

	private String getQQOpenId(String accessToken) throws BusinessException {
		// 获取openId
		String url = String.format(appConfig.getQqUrlOpenId(), accessToken);
		String openIDResult = OKHttpUtils.getRequest(url);
		String tmpJson = this.getQQResp(openIDResult);
		if (tmpJson == null) {
			logger.error("调qq接口获取openID失败:tmpJson{}", tmpJson);
			throw new BusinessException("调qq接口获取openID失败");
		}
		Map jsonData = JsonUtils.convertJson2Obj(tmpJson, Map.class);
		if (jsonData == null || jsonData.containsKey(Constants.VIEW_OBJ_RESULT_KEY)) {
			logger.error("调qq接口获取openID失败:{}", jsonData);
			throw new BusinessException("调qq接口获取openID失败");
		}
		return String.valueOf(jsonData.get("openid"));
	}


	private QQInfoDto getQQUserInfo(String accessToken, String qqOpenId) throws BusinessException {
		String url = String.format(appConfig.getQqUrlUserInfo(), accessToken, appConfig.getQqAppId(), qqOpenId);
		String response = OKHttpUtils.getRequest(url);
		if (StringUtils.isNotBlank(response)) {
			QQInfoDto qqInfo = JsonUtils.convertJson2Obj(response, QQInfoDto.class);
			if (qqInfo.getRet() != 0) {
				logger.error("qqInfo:{}", response);
				throw new BusinessException("调qq接口获取用户信息异常");
			}
			return qqInfo;
		}
		throw new BusinessException("调qq接口获取用户信息异常");
	}

	private String getQQResp(String result) {
		if (StringUtils.isNotBlank(result)) {
			int pos = result.indexOf("callback");
			if (pos != -1) {
				int start = result.indexOf("(");
				int end = result.lastIndexOf(")");
				String jsonStr = result.substring(start + 1, end - 1);
				return jsonStr;
			}
		}
		return null;
	}
}