package top.gottenzzp.MyNetDisk.mappers;

import org.apache.ibatis.annotations.Param;

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


}
