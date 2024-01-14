package top.gottenzzp.MyNetDisk.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.catalina.User;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
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
import top.gottenzzp.MyNetDisk.utils.StringTools;


/**
 * 用户信息表 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

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
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
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
		userSpaceDto.setTotalSpace(userInfo.getTotalSpace());
		// userSpaceDto.setUseSpace(userInfo.getUseSpace());
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
}