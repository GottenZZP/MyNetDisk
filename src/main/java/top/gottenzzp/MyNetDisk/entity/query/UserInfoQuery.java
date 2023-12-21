package top.gottenzzp.MyNetDisk.entity.query;

import java.util.Date;


/**
 * 用户信息表参数
 */
public class UserInfoQuery extends BaseParam {


	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;

	/**
	 * 昵称
	 */
	private String nickName;

	private String nickNameFuzzy;

	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;

	/**
	 * QQ open ID
	 */
	private String qqOpenId;

	private String qqOpenIdFuzzy;

	/**
	 * QQ头像
	 */
	private String qqAvatar;

	private String qqAvatarFuzzy;

	/**
	 * 密码
	 */
	private String password;

	private String passwordFuzzy;

	/**
	 * 注册时间
	 */
	private String registerTime;

	private String registerTimeStart;

	private String registerTimeEnd;

	/**
	 * 最后一次登录时间
	 */
	private String lastLoginTime;

	private String lastLoginTimeStart;

	private String lastLoginTimeEnd;

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

	public void setUserIdFuzzy(String userIdFuzzy){
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy(){
		return this.userIdFuzzy;
	}

	public void setNickName(String nickName){
		this.nickName = nickName;
	}

	public String getNickName(){
		return this.nickName;
	}

	public void setNickNameFuzzy(String nickNameFuzzy){
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public String getNickNameFuzzy(){
		return this.nickNameFuzzy;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setEmailFuzzy(String emailFuzzy){
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy(){
		return this.emailFuzzy;
	}

	public void setQqOpenId(String qqOpenId){
		this.qqOpenId = qqOpenId;
	}

	public String getQqOpenId(){
		return this.qqOpenId;
	}

	public void setQqOpenIdFuzzy(String qqOpenIdFuzzy){
		this.qqOpenIdFuzzy = qqOpenIdFuzzy;
	}

	public String getQqOpenIdFuzzy(){
		return this.qqOpenIdFuzzy;
	}

	public void setQqAvatar(String qqAvatar){
		this.qqAvatar = qqAvatar;
	}

	public String getQqAvatar(){
		return this.qqAvatar;
	}

	public void setQqAvatarFuzzy(String qqAvatarFuzzy){
		this.qqAvatarFuzzy = qqAvatarFuzzy;
	}

	public String getQqAvatarFuzzy(){
		return this.qqAvatarFuzzy;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPasswordFuzzy(String passwordFuzzy){
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy(){
		return this.passwordFuzzy;
	}

	public void setRegisterTime(String registerTime){
		this.registerTime = registerTime;
	}

	public String getRegisterTime(){
		return this.registerTime;
	}

	public void setRegisterTimeStart(String registerTimeStart){
		this.registerTimeStart = registerTimeStart;
	}

	public String getRegisterTimeStart(){
		return this.registerTimeStart;
	}
	public void setRegisterTimeEnd(String registerTimeEnd){
		this.registerTimeEnd = registerTimeEnd;
	}

	public String getRegisterTimeEnd(){
		return this.registerTimeEnd;
	}

	public void setLastLoginTime(String lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginTime(){
		return this.lastLoginTime;
	}

	public void setLastLoginTimeStart(String lastLoginTimeStart){
		this.lastLoginTimeStart = lastLoginTimeStart;
	}

	public String getLastLoginTimeStart(){
		return this.lastLoginTimeStart;
	}
	public void setLastLoginTimeEnd(String lastLoginTimeEnd){
		this.lastLoginTimeEnd = lastLoginTimeEnd;
	}

	public String getLastLoginTimeEnd(){
		return this.lastLoginTimeEnd;
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

}
