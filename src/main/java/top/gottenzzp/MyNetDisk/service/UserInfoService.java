package top.gottenzzp.MyNetDisk.service;

import java.util.List;

import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;


/**
 * 用户信息表 业务接口
 */
public interface UserInfoService {

	/**
	 * 根据条件查询列表
	 */
	List<UserInfo> findListByParam(UserInfoQuery param);

	/**
	 * 根据条件查询列表
	 */
	Integer findCountByParam(UserInfoQuery param);

	/**
	 * 分页查询
	 */
	PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param);

	/**
	 * 新增
	 */
	Integer add(UserInfo bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<UserInfo> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<UserInfo> listBean);

	/**
	 * 多条件更新
	 */
	Integer updateByParam(UserInfo bean,UserInfoQuery param);

	/**
	 * 多条件删除
	 */
	Integer deleteByParam(UserInfoQuery param);

	/**
	 * 根据UserId查询对象
	 */
	UserInfo getUserInfoByUserId(String userId);


	/**
	 * 根据UserId修改
	 */
	Integer updateUserInfoByUserId(UserInfo bean,String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteUserInfoByUserId(String userId);


	/**
	 * 根据EmailAndQqOpenIdAndNickName查询对象
	 */
	UserInfo getUserInfoByEmailAndQqOpenIdAndNickName(String email,String qqOpenId,String nickName);


	/**
	 * 根据EmailAndQqOpenIdAndNickName修改
	 */
	Integer updateUserInfoByEmailAndQqOpenIdAndNickName(UserInfo bean,String email,String qqOpenId,String nickName);


	/**
	 * 根据EmailAndQqOpenIdAndNickName删除
	 */
	Integer deleteUserInfoByEmailAndQqOpenIdAndNickName(String email,String qqOpenId,String nickName);

	/**
	 * @param email 邮箱
	 * @param nickName 昵称
	 * @param password 密码
	 * @param emailCode 邮箱验证码
	 */
	void register(String email, String nickName, String password, String emailCode);

	/**
	 * @param email 邮箱
	 * @param password 密码
	 * @return {@link SessionWebUserDto}
	 */
	SessionWebUserDto login(String email, String password);

    void resetPwd(String email, String password, String emailCode);

    SessionWebUserDto qqLogin(String code);

	/**
	 * 更新用户状态
	 *
	 * @param userId 用户id
	 * @param status 状态
	 */
	void updateUserStatus(String userId, Integer status);

	/**
	 * 改变用户空间大小
	 *
	 * @param userId      用户id
	 * @param changeSpace 变更空间
	 */
	void changeUserSpace(String userId, Integer changeSpace);
}