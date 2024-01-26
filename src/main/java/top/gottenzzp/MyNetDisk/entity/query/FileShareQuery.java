package top.gottenzzp.MyNetDisk.entity.query;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 参数
 * @author gottenzzp
 */
@Getter
@Setter
public class FileShareQuery extends BaseParam {


	/**
	 * 主键
	 */
	private String shareId;

	private String shareIdFuzzy;

	/**
	 * 文件id
	 */
	private String fileId;

	private String fileIdFuzzy;

	/**
	 * 分享人
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 有效时间 0:1天 1:7天 2:30天 4:永久
	 */
	private Integer validType;

	/**
	 * 失效时间
	 */
	private String expireTime;

	private String expireTimeStart;

	private String expireTimeEnd;

	/**
	 * 分享时间
	 */
	private String shareTime;

	private String shareTimeStart;

	private String shareTimeEnd;

	/**
	 * 分享密码
	 */
	private String code;

	private String codeFuzzy;

	/**
	 * 浏览次数
	 */
	private Integer showCount;

	/**
	 * 是否查询文件名称
	 */
	private Boolean queryFileName;
}
