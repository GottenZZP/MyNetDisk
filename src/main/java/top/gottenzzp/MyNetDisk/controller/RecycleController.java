package top.gottenzzp.MyNetDisk.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.gottenzzp.MyNetDisk.annotation.GlobalInterceptor;
import top.gottenzzp.MyNetDisk.annotation.VerifyParam;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.enums.FileCategoryEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileDelFlagEnums;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.FileInfoVO;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.service.FileInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Title: RecycleController
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.controller
 * @Date 2024/1/25 14:28
 * @description: 回收站Controller
 */
@RestController("recycleController")
@RequestMapping("/recycle")
public class RecycleController extends ABaseController {
    @Resource
    private FileInfoService fileInfoService;

    @RequestMapping("/loadRecycleList")
    @GlobalInterceptor
    public ResponseVO loadRecycleList(HttpSession session, Integer pageNo, Integer pageSize) {
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        infoQuery.setOrderBy("recovery_time desc");
        infoQuery.setPageNo(pageNo);
        infoQuery.setPageSize(pageSize);
        infoQuery.setUserId(getUserInfoFromSession(session).getUserId());
        PaginationResultVO<FileInfo> page = fileInfoService.findListByPage(infoQuery);
        return getSuccessResponseVO(convert2PaginationVO(page, FileInfoVO.class));
    }

    @RequestMapping("/recoverFile")
    @GlobalInterceptor
    public ResponseVO recoverFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.recoverFileBatch(webUserDto.getUserId(), fileIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delFile")
    @GlobalInterceptor
    public ResponseVO delFile(HttpSession session, @VerifyParam(required = true) String fileIds) {
        SessionWebUserDto webUserDto = getUserInfoFromSession(session);
        fileInfoService.delFileBatch(webUserDto.getUserId(), fileIds, false);
        return getSuccessResponseVO(null);
    }
}
