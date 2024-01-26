package top.gottenzzp.MyNetDisk.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import top.gottenzzp.MyNetDisk.entity.enums.DateTimePatternEnum;
import top.gottenzzp.MyNetDisk.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 
 */
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


	public void setShareId(String shareId){
		this.shareId = shareId;
	}

	public String getShareId(){
		return this.shareId;
	}

	public void setFileId(String fileId){
		this.fileId = fileId;
	}

	public String getFileId(){
		return this.fileId;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setValidType(Integer validType){
		this.validType = validType;
	}

	public Integer getValidType(){
		return this.validType;
	}

	public void setExpireTime(Date expireTime){
		this.expireTime = expireTime;
	}

	public Date getExpireTime(){
		return this.expireTime;
	}

	public void setShareTime(Date shareTime){
		this.shareTime = shareTime;
	}

	public Date getShareTime(){
		return this.shareTime;
	}

	public void setCode(String code){
		this.code = code;
	}

	public String getCode(){
		return this.code;
	}

	public void setShowCount(Integer showCount){
		this.showCount = showCount;
	}

	public Integer getShowCount(){
		return this.showCount;
	}

	@Override
	public String toString (){
		return "主键:"+(shareId == null ? "空" : shareId)+"，文件id:"+(fileId == null ? "空" : fileId)+"，分享人:"+(userId == null ? "空" : userId)+"，有效时间 0:1天 1:7天 2:30天 4:永久:"+(validType == null ? "空" : validType)+"，失效时间:"+(expireTime == null ? "空" : DateUtil.format(expireTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，分享时间:"+(shareTime == null ? "空" : DateUtil.format(shareTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，分享密码:"+(code == null ? "空" : code)+"，浏览次数:"+(showCount == null ? "空" : showCount);
	}
}
