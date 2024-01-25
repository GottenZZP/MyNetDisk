package top.gottenzzp.MyNetDisk.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件信息表 数据库操作接口
 */
public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {

    /**
     * 根据FileIdAndUserId更新
     */
    Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);


    /**
     * 根据FileIdAndUserId删除
     */
    Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);


    /**
     * 根据FileIdAndUserId获取对象
     */
    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    /**
     * 根据用户id获取使用空间
     *
     * @param userId 用户id
     * @return {@link Long}
     */
    @Select("select ifnull(sum(file_size), 0) from file_info where user_id = #{userId}")
    Long selectUseSpace(@Param("userId") String userId);

    /**
     * 更新文件状态
     *
     * @param fileId    文件id
     * @param userId    用户id
     * @param t         t
     * @param oldStatus 旧状态
     */
    void updateFileStatusWithOldStatus(@Param("fileId") String fileId, @Param("userId") String userId,
                                       @Param("bean") T t, @Param("oldStatus") Integer oldStatus);

    /**
     * 批量删除文件
     *
     * @param fileInfo    文件信息类
     * @param userId      使用者id
     * @param filePidList 文件pid列表
     * @param fileIdList  文件id列表
     * @param oldDelFlag  旧删除标签
     */
    void updateFileDelFlagBatch(@Param("bean") FileInfo fileInfo,
                                @Param("userId") String userId,
                                @Param("filePidList") List<String> filePidList,
                                @Param("fileIdList") List<String> fileIdList,
                                @Param("oldDelFlag") Integer oldDelFlag);

    void delFileBatch(@Param("userId") String userId,
                      @Param("filePidList") List<String> filePidList,
                      @Param("fileIdList") List<String> fileIdList,
                      @Param("oldDelFlag") Integer oldDelFlag);
}
