package top.gottenzzp.MyNetDisk.mappers;

import org.apache.ibatis.annotations.Param;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;

/**
 * 用户信息表 数据库操作接口
 */
public interface UserInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据UserId更新
	 */
	Integer updateByUserId(@Param("bean") T t,@Param("userId") String userId);


	/**
	 * 根据UserId删除
	 */
	Integer deleteByUserId(@Param("userId") String userId);


	/**
	 * 根据UserId获取对象
	 */
	T selectByUserId(@Param("userId") String userId);


	/**
	 * 根据EmailAndQqOpenIdAndNickName更新
	 */
	Integer updateByEmailAndQqOpenIdAndNickName(@Param("bean") T t,@Param("email") String email,@Param("qqOpenId") String qqOpenId,@Param("nickName") String nickName);


	/**
	 * 根据EmailAndQqOpenIdAndNickName删除
	 */
	Integer deleteByEmailAndQqOpenIdAndNickName(@Param("email") String email,@Param("qqOpenId") String qqOpenId,@Param("nickName") String nickName);


	/**
	 * 根据EmailAndQqOpenIdAndNickName获取对象
	 */
	T selectByEmailAndQqOpenIdAndNickName(@Param("email") String email,@Param("qqOpenId") String qqOpenId,@Param("nickName") String nickName);

	T selectByEmail(@Param("email") String email);

	/**
	 * 获取昵称
	 *
	 * @param nickName 昵称
	 * @return {@link T}
	 */
	T selectByNickName(@Param("nickName") String nickName);

	/**
	 * 通过电子邮件更新
	 *
	 * @param updateUserInfo 更新用户信息
	 * @param email          电子邮件
	 */
	void updateByEmail(T updateUserInfo, String email);

	/**
	 * 获取通过qq openid
	 *
	 * @param qqOpenId qq开放id
	 * @return {@link T}
	 */
	T selectByQqOpenId(String qqOpenId);

	/**
	 * 更新qq openid更新
	 *
	 * @param t        t
	 * @param qqOpenId qq开放id
	 * @return {@link Integer}
	 */
	Integer updateByQqOpenId(@Param("bean") T t, @Param("qqOpenId") String qqOpenId);

	/**
	 * 更新用户空间
	 *
	 * @param userId     用户id
	 * @param useSpace   使用空间
	 * @param totalSpace 总空间
	 * @return {@link Integer}
	 */
	Integer updateUserSpace(String userId, Long useSpace, Long totalSpace);
}
