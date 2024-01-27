package top.gottenzzp.MyNetDisk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.entity.vo.UserInfoVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;

import javax.annotation.Resource;

/**
 * @Title: AdminController
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.controller
 * @Date 2024/1/27 16:21
 * @description: 管理员控制类
 */
@RestController("AdminController")
@RequestMapping("/admin")
public class AdminController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 获取系统设置
     *
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getSysSettings() {
        return getSuccessResponseVO(redisComponent.getSysSettingDto());
    }

    /**
     * 保存系统设置
     *
     * @param registerEmailTitle  注册电子邮件标题
     * @param registerEmailContent 注册邮件内容
     * @param userInitUseSpace    用户初始化使用空间
     * @return {@link ResponseVO}
     */
    @RequestMapping("/saveSysSettings")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO saveSysSettings(@VerifyParam(required = true) String registerEmailTitle,
                                      @VerifyParam(required = true) String registerEmailContent,
                                      @VerifyParam(required = true) Integer userInitUseSpace) {
        SysSettingsDto sysSettingsDto = new SysSettingsDto();
        sysSettingsDto.setRegisterEmailTitle(registerEmailTitle);
        sysSettingsDto.setRegisterEmailContent(registerEmailContent);
        sysSettingsDto.setUserInitUseSpace(userInitUseSpace);
        redisComponent.saveSysSettingsDto(sysSettingsDto);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载用户列表
     *
     * @param userInfoQuery 用户信息查询
     * @return {@link ResponseVO}
     */
    @RequestMapping("/loadUserList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadUserList(UserInfoQuery userInfoQuery) {
        userInfoQuery.setOrderBy("join_time desc");
        PaginationResultVO<UserInfo> resultVO = userInfoService.findListByPage(userInfoQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, UserInfoVO.class));
    }

    @RequestMapping("/updateUserStatus")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserStatus(@VerifyParam(required = true) String userId,
                                       @VerifyParam(required = true) Integer status) {
        userInfoService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }
}
