package top.gottenzzp.MyNetDisk.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import top.gottenzzp.MyNetDisk.entity.enums.DateTimePatternEnum;
import top.gottenzzp.MyNetDisk.utils.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;


/**
 * 用户信息表
 */
public class UserInfo implements Serializable {


	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * QQ open ID
	 */
	private String qqOpenId;

	/**
	 * QQ头像
	 */
	private String qqAvatar;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 注册时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date registerTime;

	/**
	 * 最后一次登录时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTime;

	/**
	 * 用户状态 0:禁用 1:启用
	 */
	private Integer status;

	/**
	 * 用户使用空间
	 */
	private Long useSpace;

	/**
	 * 用户总空间大小
	 */
	private Long totalSpace;


	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setQqOpenId(String qqOpenId){
		this.qqOpenId = qqOpenId;
	}

	public String getQqOpenId(){
		return this.qqOpenId;
	}

	public void setQqAvatar(String qqAvatar){
		this.qqAvatar = qqAvatar;
	}

	public String getQqAvatar(){
		return this.qqAvatar;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setRegisterTime(Date registerTime){
		this.registerTime = registerTime;
	}

	public Date getRegisterTime(){
		return this.registerTime;
	}

	public void setLastLoginTime(Date lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime(){
		return this.lastLoginTime;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return this.status;
	}

	public void setUseSpace(Long useSpace){
		this.useSpace = useSpace;
	}

	public Long getUseSpace(){
		return this.useSpace;
	}

	public void setTotalSpace(Long totalSpace){
		this.totalSpace = totalSpace;
	}

	public Long getTotalSpace(){
		return this.totalSpace;
	}

	@Override
	public String toString (){
		return "用户ID:"+(userId == null ? "空" : userId)+"，昵称:"+(nickName == null ? "空" : nickName)+"，邮箱:"+(email == null ? "空" : email)+"，QQ open ID:"+(qqOpenId == null ? "空" : qqOpenId)+"，QQ头像:"+(qqAvatar == null ? "空" : qqAvatar)+"，密码:"+(password == null ? "空" : password)+"，注册时间:"+(registerTime == null ? "空" : DateUtil.format(registerTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，最后一次登录时间:"+(lastLoginTime == null ? "空" : DateUtil.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()))+"，用户状态 0:禁用 1:启用:"+(status == null ? "空" : status)+"，用户使用空间:"+(useSpace == null ? "空" : useSpace)+"，用户总空间大小:"+(totalSpace == null ? "空" : totalSpace);
	}
}
