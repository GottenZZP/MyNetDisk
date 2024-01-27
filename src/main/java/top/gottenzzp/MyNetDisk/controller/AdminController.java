package top.gottenzzp.MyNetDisk.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.query.FileShareQuery;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.entity.vo.UserInfoVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Title: AdminController
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.controller
 * @Date 2024/1/27 16:21
 * @description: 管理员控制类
 */
@RestController("AdminController")
@RequestMapping("/admin")
public class AdminController extends CommonFileController {
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
     * @param registerEmailTitle   注册电子邮件标题
     * @param registerEmailContent 注册邮件内容
     * @param userInitUseSpace     用户初始化使用空间
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
        userInfoQuery.setOrderBy("register_time desc");
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

    @RequestMapping("/updateUserSpace")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO updateUserSpace(@VerifyParam(required = true) String userId,
                                      @VerifyParam(required = true) Integer changeSpace) {
        userInfoService.changeUserSpace(userId, changeSpace);
        return getSuccessResponseVO(null);
    }

    /**
     * 加载文件列表
     *
     * @param fileInfoQuery 文件信息查询
     * @return {@link ResponseVO}
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO loadFileList(FileInfoQuery fileInfoQuery) {
        fileInfoQuery.setOrderBy("last_update_time desc");
        fileInfoQuery.setQueryNickName(true);
        PaginationResultVO<FileInfo> resultVO = fileInfoService.findListByPage(fileInfoQuery);
        return getSuccessResponseVO(resultVO);
    }

    /**
     * 获取文件夹信息
     *
     * @param path 路径
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO getFolderInfo(@VerifyParam(required = true) String path) {
        return getSuccessResponseVO(super.getFolderInfo(path, null));
    }

    @Override
    @RequestMapping("/getFile/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getFile(
            HttpServletResponse response,
            @PathVariable("userId") String userId,
            @PathVariable("fileId") String fileId) {
        super.getFile(response, fileId, userId);
    }

    /**
     * 获取视频信息
     *
     * @param response 响应
     * @param userId   用户id
     * @param fileId   文件id
     */
    @Override
    @RequestMapping("/ts/getVideoInfo/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public void getVideoInfo(
            HttpServletResponse response,
            @PathVariable("userId") String userId,
            @PathVariable("fileId") String fileId) {
        super.getFile(response, fileId, userId);
    }

    /**
     * 创建下载url
     *
     * @param userId 用户id
     * @param fileId 文件id
     * @return {@link ResponseVO}
     */
    @Override
    @RequestMapping("/createDownloadUrl/{userId}/{fileId}")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO createDownloadUrl(
            @PathVariable("userId") String userId,
            @PathVariable("fileId") String fileId) {
        return super.createDownloadUrl(fileId, userId);
    }

    @Override
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @VerifyParam(required = true) @PathVariable("code") String code) throws Exception {
        super.download(request, response, code);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor(checkParams = true, checkAdmin = true)
    public ResponseVO delFile(@VerifyParam(required = true) String fileIdAndUserIds) throws Exception {
        String[] fileIdAndUserIdArray = fileIdAndUserIds.split(",");
        for (String fileIdAndUserId : fileIdAndUserIdArray) {
            String[] itemArray = fileIdAndUserId.split("_");
            fileInfoService.delFileBatch(itemArray[0], itemArray[1], true);
        }
        return getSuccessResponseVO(null);
    }
}
