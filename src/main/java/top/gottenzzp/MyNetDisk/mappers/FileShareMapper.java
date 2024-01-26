package top.gottenzzp.MyNetDisk.mappers;

import org.apache.ibatis.annotations.Param;

/**
 *  数据库操作接口
 */
public interface FileShareMapper<T,P> extends BaseMapper<T,P> {

	/**
	 * 根据ShareId更新
	 */
	 Integer updateByShareId(@Param("bean") T t,@Param("shareId") String shareId);


	/**
	 * 根据ShareId删除
	 */
	 Integer deleteByShareId(@Param("shareId") String shareId);


	/**
	 * 根据ShareId获取对象
	 */
	 T selectByShareId(@Param("shareId") String shareId);


	/**
	 * 批量删除共享文件
	 *
	 * @param shareIds 共享ID
	 * @param userId   用户id
	 */
	Integer batchDeletionSharedFiles(@Param("shareIds") String[] shareIds, @Param("userId") String userId);
}
