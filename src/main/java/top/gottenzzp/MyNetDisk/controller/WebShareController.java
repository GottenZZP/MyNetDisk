package top.gottenzzp.MyNetDisk.controller;

import org.springframework.web.bind.annotation.PathVariable;
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
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.entity.vo.ShareInfoVO;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.service.FileShareService;
import top.gottenzzp.MyNetDisk.service.UserInfoService;
import top.gottenzzp.MyNetDisk.utils.CopyTools;
import top.gottenzzp.MyNetDisk.utils.StringTools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    /**
     * 加载文件列表
     *
     * @param session 会话
     * @param shareId 分享id
     * @param filePid 要查看的文件目录
     * @return {@link ResponseVO}
     */
    @RequestMapping("/loadFileList")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO loadFileList(HttpSession session,
                                   @VerifyParam(required = true) String shareId, String filePid) {
        // 检查分享是否失效
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        FileInfoQuery query = new FileInfoQuery();
        // 如果文件目录不为空且不为根目录
        if (!StringTools.isEmpty(filePid) && !Constants.ZERO_STR.equals(filePid)) {
            // 检查接口要查看文件是否在所分享的文件根目录下
            fileInfoService.checkRootFilePid(shareSessionDto.getFileId(), shareSessionDto.getShareUserId(), filePid);
            // 到这步没报异常说明要查看的文件是在分享的文件根目录下，则设置搜索的文件父id为filePid
            query.setFilePid(filePid);
        } else {
            // 否则直接设置搜索的文件id为分享的文件id
            query.setFileId(shareSessionDto.getFileId());
        }
        // 搜索文件并返回
        query.setUserId(shareSessionDto.getShareUserId());
        query.setOrderBy("last_update_time desc");
        query.setDelFlag(FileDelFlagEnums.USING.getFlag());
        PaginationResultVO resultVO = fileInfoService.findListByPage(query);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, FileInfoVO.class));
    }

    /**
     * 检查分享是否失效
     *
     * @param session 会话
     * @param shareId 共有id
     * @return {@link SessionShareDto}
     */
    private SessionShareDto checkShare(HttpSession session, String shareId) {
        // 获取分享信息
        SessionShareDto shareSessionDto = getSessionShareFromSession(session, shareId);
        // 如果分享不存在
        if (shareSessionDto == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_903);
        }
        // 或者分享已经过期，则抛出异常
        if (shareSessionDto.getExpireTime() != null && new Date().after(shareSessionDto.getExpireTime())) {
            throw new BusinessException(ResponseCodeEnum.CODE_902);
        }
        // 返回分享信息
        return shareSessionDto;
    }

    /**
     * 获取文件夹信息
     *
     * @param session 会话
     * @param shareId 共有id
     * @param path    路径
     * @return {@link ResponseVO}
     */
    @RequestMapping("/getFolderInfo")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO getFolderInfo(HttpSession session,
                                    @VerifyParam(required = true) String shareId,
                                    @VerifyParam(required = true) String path) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.getFolderInfo(path, shareSessionDto.getShareUserId());
    }

    /**
     * 获取文件
     *
     * @param response 响应
     * @param session  会话
     * @param shareId  分享id
     * @param fileId   文件id
     */
    @RequestMapping("/getFile/{shareId}/{fileId}")
    public void getFile(HttpServletResponse response, HttpSession session,
                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 获取视频信息
     *
     * @param response 响应
     * @param session  会话
     * @param shareId  分享id
     * @param fileId   文件id
     */
    @RequestMapping("/ts/getVideoInfo/{shareId}/{fileId}")
    public void getVideoInfo(HttpServletResponse response,
                             HttpSession session,
                             @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                             @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        super.getFile(response, fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 创建下载url
     *
     * @param session 会话
     * @param shareId 分享id
     * @param fileId  文件id
     * @return {@link ResponseVO}
     */
    @RequestMapping("/createDownloadUrl/{shareId}/{fileId}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public ResponseVO createDownloadUrl(HttpSession session,
                                        @PathVariable("shareId") @VerifyParam(required = true) String shareId,
                                        @PathVariable("fileId") @VerifyParam(required = true) String fileId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        return super.createDownloadUrl(fileId, shareSessionDto.getShareUserId());
    }

    /**
     * 下载
     *
     * @param request  请求
     * @param response 响应
     * @param code     密码
     * @throws Exception 异常
     */
    @RequestMapping("/download/{code}")
    @GlobalInterceptor(checkLogin = false, checkParams = true)
    public void download(HttpServletRequest request, HttpServletResponse response,
                         @PathVariable("code") @VerifyParam(required = true) String code) throws Exception {
        super.download(request, response, code);
    }

    @RequestMapping("/saveShare")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveShare(HttpSession session,
                                @VerifyParam(required = true) String shareId,
                                @VerifyParam(required = true) String shareFileIds,
                                @VerifyParam(required = true) String myFolderId) {
        SessionShareDto shareSessionDto = checkShare(session, shareId);
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        if (shareSessionDto.getShareUserId().equals(webUserDto.getUserId())) {
            throw new BusinessException("自己分享的文件无法保存到自己的网盘");
        }
        fileInfoService.saveShare(shareSessionDto.getFileId(), shareFileIds, myFolderId, shareSessionDto.getShareUserId(), webUserDto.getUserId());
        return getSuccessResponseVO(null);
    }
}
