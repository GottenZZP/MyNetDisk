package top.gottenzzp.MyNetDisk.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import top.gottenzzp.MyNetDisk.entity.enums.DateTimePatternEnum;
import top.gottenzzp.MyNetDisk.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 *
 * @author gottenzzp
 */
@Setter
@Getter
public class FileShare implements Serializable {


	/**
	 * 主键
	 */
	private String shareId;

	/**
	 * 文件id
	 */
	private String fileId;

	/**
	 * 分享人
	 */
	private String userId;

	/**
	 * 有效时间 0:1天 1:7天 2:30天 4:永久
	 */
	private Integer validType;

	/**
	 * 失效时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expireTime;

	/**
	 * 分享时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date shareTime;

	/**
	 * 分享密码
	 */
	private String code;

	/**
	 * 浏览次数
	 */
	private Integer showCount;

	private String fileName;

	private String fileCover;

	private Integer folderType;
	private Integer fileCategory;
	private Integer fileType;

	@Override
	public String toString (){
		return "主键:"+(shareId == null ? "空" : shareId)+"，文件id:"+(fileId == null ? "空" : fileId)+"，分享人:"+(userId == null ? "空" : userId)+"，有效时间 0:1天 1:7天 2:30天 4:永久:"+(validType == null ? "空" : validType)+"，失效时间:"+(expireTime == null ? "空" : DateUtil.format(expireTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，分享时间:"+(shareTime == null ? "空" : DateUtil.format(shareTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，分享密码:"+(code == null ? "空" : code)+"，浏览次数:"+(showCount == null ? "空" : showCount);
	}
}
