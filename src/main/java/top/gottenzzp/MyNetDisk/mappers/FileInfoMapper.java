package top.gottenzzp.MyNetDisk.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文件信息表 数据库操作接口
 */
public interface FileInfoMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据FileIdAndUserId更新
	 */
	 Integer updateByFileIdAndUserId(@Param("bean") T t,@Param("fileId") String fileId,@Param("userId") String userId);


	/**
	 * 根据FileIdAndUserId删除
	 */
	 Integer deleteByFileIdAndUserId(@Param("fileId") String fileId,@Param("userId") String userId);


	/**
	 * 根据FileIdAndUserId获取对象
	 */
	 T selectByFileIdAndUserId(@Param("fileId") String fileId,@Param("userId") String userId);

	/**
	 * 根据用户id获取使用空间
	 *
	 * @param userId 用户id
	 * @return {@link Long}
	 */
	@Select("select ifnull(sum(file_size), 0) from file_info where user_id = #{userId}")
	Long selectUseSpace(@Param("userId") String userId);
}
