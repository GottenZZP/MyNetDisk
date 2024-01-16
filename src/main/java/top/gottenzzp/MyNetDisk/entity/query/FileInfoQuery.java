package top.gottenzzp.MyNetDisk.entity.query;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件信息表参数
 * @author gottenzzp
 */
@Getter
@Setter
public class FileInfoQuery extends BaseParam {


	/**
	 * 文件ID
	 */
	private String fileId;

	private String fileIdFuzzy;

	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 文件MD5值
	 */
	private String fileMd5;

	private String fileMd5Fuzzy;

	/**
	 * 父级ID
	 */
	private String filePid;

	private String filePidFuzzy;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名
	 */
	private String fileName;

	private String fileNameFuzzy;

	/**
	 * 文件封面
	 */
	private String fileCover;

	private String fileCoverFuzzy;

	/**
	 * 文件路径
	 */
	private String filePath;

	private String filePathFuzzy;

	/**
	 * 文件创建时间
	 */
	private String createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 文件最后更新时间
	 */
	private String lastUpdateTime;

	private String lastUpdateTimeStart;

	private String lastUpdateTimeEnd;

	/**
	 * 0:文件 1:目录
	 */
	private Integer folderType;

	/**
	 * 文件分类 1:视频 2:音频 3:图片 4:文档 5:其他
	 */
	private Integer fileCategory;

	/**
	 * 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
	 */
	private Integer fileType;

	/**
	 * 0:转码中 1:转码失败 2:转码成功
	 */
	private Integer status;

	/**
	 * 进入回收站时间
	 */
	private String recoveryTime;

	private String recoveryTimeStart;

	private String recoveryTimeEnd;

	/**
	 * 标记删除 0:删除 1:回收站 2:正常
	 */
	private Integer delFlag;
}
