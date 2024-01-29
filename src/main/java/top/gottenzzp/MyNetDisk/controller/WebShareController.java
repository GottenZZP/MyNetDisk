package top.gottenzzp.MyNetDisk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.SessionShareDto;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.enums.FileDelFlagEnums;
import top.gottenzzp.MyNetDisk.entity.enums.ResponseCodeEnum;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.po.FileShare;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.entity.vo.ShareInfoVO;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.service.FileShareService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;
import top.gottenzzp.MyNetDisk.utils.CopyTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @Title: WebShareController
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.controller
 * @Date 2024/1/28 15:58
 * @description: 外部分享类
 */
@RestController("WebShareController")
@RequestMapping("/showShare")
public class WebShareController extends CommonFileController {
    @Resource
    private FileShareService fileShareService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 获取分享登录信息
     *
     * @param session 会话
     * @param shareId 共有id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getShareLoginInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareLoginInfo(HttpSession session, @VerifyParam(required = true) String shareId) {
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        if (shareSessionDto == null) {
            return getSuccessResponseVO(null);
        }
        ShareInfoVO shareInfoVO = getShareInfoCommon(shareId);
        //判断是否是当前用户分享的文件
        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null && userDto.getUserId().equals(shareSessionDto.getShareUserId())) {
            shareInfoVO.setCurrentUser(true);
        } else {
            shareInfoVO.setCurrentUser(false);
        }
        return getSuccessResponseVO(shareInfoVO);
    }

    @RequestMapping("/getShareInfo")
    @GlobalInterceptor(checkParams = true, checkLogin = false)
    public ResponseVO getShareInfo(@VerifyParam(required = true) String shareId) {
        return getSuccessResponseVO(getShareInfoCommon(shareId));
    }

    private ShareInfoVO getShareInfoCommon(String shareId) {
        FileShare share = fileShareService.getFileShareByShareId(shareId);
        // 如果分享不存在或者分享已经过期
        if (share == null || (share.getExpireTime() != null && new Date().after(share.getExpireTime()))) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        ShareInfoVO shareInfoVO = CopyTools.copy(share, ShareInfoVO.class);
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(share.getFileId(), share.getUserId());
        // 如果文件不存在或者文件已经被删除
        if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902.getMsg());
        }
        shareInfoVO.setFileName(fileInfo.getFileName());
        UserInfo userInfo = userInfoService.getUserInfoByUserId(share.getUserId());
        shareInfoVO.setNickName(userInfo.getNickName());
        shareInfoVO.setAvatar(userInfo.getQqAvatar());
        shareInfoVO.setUserId(userInfo.getUserId());
        return shareInfoVO;
    }

    /**
     * 检查分享提取码
     *
     * @param session 会话
     * @param shareId 共有id
     * @param code    密码
     * @return {@link ResponseVO}
     */
    @RequestMapping("/checkShareCode")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO checkShareCode(HttpSession session,
                                     @VerifyParam(required = true) String shareId,
                                     @VerifyParam(required = true) String code) {
        SessionShareDto shareSessionDto = fileShareService.checkShareCode(shareId, code);
        session.setAttribute(Constants.SESSION_SHARE_KEY + shareId, shareSessionDto);
        return getSuccessResponseVO(null);
    }
}
