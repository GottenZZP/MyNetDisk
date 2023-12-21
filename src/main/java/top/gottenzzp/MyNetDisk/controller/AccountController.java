package top.gottenzzp.MyNetDisk.controller;

import java.util.List;

import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户信息表 Controller
 */
@RestController("userInfoController")
@RequestMapping("/userInfo")
public class AccountController extends ABaseController{

	@Resource
	private UserInfoService userInfoService;
	/**
	 * 根据条件分页查询
	 */
	@RequestMapping("/loadDataList")
	public ResponseVO loadDataList(UserInfoQuery query){
		return getSuccessResponseVO(userInfoService.findListByPage(query));
	}

	/**
	 * 新增
	 */
	@RequestMapping("/add")
	public ResponseVO add(UserInfo bean) {
		userInfoService.add(bean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增
	 */
	@RequestMapping("/addBatch")
	public ResponseVO addBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 批量新增/修改
	 */
	@RequestMapping("/addOrUpdateBatch")
	public ResponseVO addOrUpdateBatch(@RequestBody List<UserInfo> listBean) {
		userInfoService.addBatch(listBean);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId查询对象
	 */
	@RequestMapping("/getUserInfoByUserId")
	public ResponseVO getUserInfoByUserId(String userId) {
		return getSuccessResponseVO(userInfoService.getUserInfoByUserId(userId));
	}

	/**
	 * 根据UserId修改对象
	 */
	@RequestMapping("/updateUserInfoByUserId")
	public ResponseVO updateUserInfoByUserId(UserInfo bean,String userId) {
		userInfoService.updateUserInfoByUserId(bean,userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据UserId删除
	 */
	@RequestMapping("/deleteUserInfoByUserId")
	public ResponseVO deleteUserInfoByUserId(String userId) {
		userInfoService.deleteUserInfoByUserId(userId);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName查询对象
	 */
	@RequestMapping("/getUserInfoByEmailAndQqOpenIdAndNickName")
	public ResponseVO getUserInfoByEmailAndQqOpenIdAndNickName(String email,String qqOpenId,String nickName) {
		return getSuccessResponseVO(userInfoService.getUserInfoByEmailAndQqOpenIdAndNickName(email,qqOpenId,nickName));
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName修改对象
	 */
	@RequestMapping("/updateUserInfoByEmailAndQqOpenIdAndNickName")
	public ResponseVO updateUserInfoByEmailAndQqOpenIdAndNickName(UserInfo bean,String email,String qqOpenId,String nickName) {
		userInfoService.updateUserInfoByEmailAndQqOpenIdAndNickName(bean,email,qqOpenId,nickName);
		return getSuccessResponseVO(null);
	}

	/**
	 * 根据EmailAndQqOpenIdAndNickName删除
	 */
	@RequestMapping("/deleteUserInfoByEmailAndQqOpenIdAndNickName")
	public ResponseVO deleteUserInfoByEmailAndQqOpenIdAndNickName(String email,String qqOpenId,String nickName) {
		userInfoService.deleteUserInfoByEmailAndQqOpenIdAndNickName(email,qqOpenId,nickName);
		return getSuccessResponseVO(null);
	}
}