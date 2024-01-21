package top.gottenzzp.MyNetDisk.controller;

import org.apache.commons.lang3.StringUtils;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.DownloadFileDto;
import top.gottenzzp.MyNetDisk.entity.enums.FileCategoryEnums;
import top.gottenzzp.MyNetDisk.entity.enums.FileFolderTypeEnums;
import top.gottenzzp.MyNetDisk.entity.enums.ResponseCodeEnum;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.ResponseVO;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.utils.StringTools;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

/**
 * @Title: CommonFileController
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.controller
 * @Date 2024/1/18 17:44
 * @description: 基础超类
 */
public class CommonFileController extends ABaseController {

    @Resource
    private AppConfig appConfig;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private FileInfoService fileInfoService;

    protected void getImage(HttpServletResponse response, String imageFolder, String imageName) {
        if (StringTools.isEmpty(imageFolder) || StringTools.isEmpty(imageName) || !StringTools.pathIsOk(imageFolder) ||
                !StringTools.pathIsOk(imageName)) {
            return;
        }
        String fileSuffix = StringTools.getFileSuffix(imageName);
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + imageFolder + "/" + imageName;
        fileSuffix = fileSuffix.replace(".", "");
        String contentType = "image/" + fileSuffix;
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=2592000");
        readFile(response, filePath);
    }

    protected void getFile(HttpServletResponse response, String fileId, String userId) {
        String filePath = null;

        if (fileId.endsWith(".ts")) {
            String[] tsArray = fileId.split("_");
            String realFileId = tsArray[0];
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(realFileId, userId);
            if (fileInfo == null) {
                return;
            }
            // /Users/gottenzzp/Documents/java_code/MyNetDisk/file/202401/0212739720596836uAALaM79k.mp4
            String fileName = fileInfo.getFilePath();
            // /Users/gottenzzp/Documents/java_code/MyNetDisk/file/202401/0212739720596836uAALaM79k/
            fileName = StringTools.getFileNameNoSuffix(fileName) + "/" + fileId;
            filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileName;
        } else {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
            if (fileInfo == null) {
                return;
            }
            // 若是视频文件，则需要获取m3u8文件
            if (FileCategoryEnums.VIDEO.getCategory().equals(fileInfo.getFileCategory())) {
                String fileNameNoSuffix = StringTools.getFileNameNoSuffix(fileInfo.getFilePath());
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileNameNoSuffix + "/" + Constants.M3U8_NAME;
            } else {    // 否则直接获取文件
                filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + fileInfo.getFilePath();
            }
            File file = new File(filePath);
            if (!file.exists()) {
                return;
            }
        }
        readFile(response, filePath);
    }

    /**
     * 获取path路径的文件结构信息
     *
     * @param path   路径
     * @param userId 用户id
     * @return {@link ResponseVO}
     */
    protected ResponseVO getFolderInfo(String path, String userId) {
        // 传入进来的是"xnVzRSIFAV/J2fhGMlYsO"结构的path，每个/分割开的是一个文件夹id
        String[] pathList = path.split("/");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        infoQuery.setFileIdArray(pathList);
        // 按照传入的path路径里的文件夹id顺序来查询数据库
        String orderBy = "field(file_id,\"" + StringUtils.join(pathList, "\",\"") + "\")";
        infoQuery.setOrderBy(orderBy);
        List<FileInfo> infoList = fileInfoService.findListByParam(infoQuery);
        return getSuccessResponseVO(infoList);
    }

    /**
     * 创建下载url
     *
     * @param fileId 文件id
     * @param userId 使用者id
     * @return {@link ResponseVO}
     */
    protected ResponseVO createDownloadUrl(String fileId, String userId) {
        FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(fileId, userId);
        // 若文件不存在或当前文件是文件夹则直接报错
        if (fileInfo == null || FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 生成一段随机数
        String code = StringTools.getRandomString(Constants.LENGTH_50);
        DownloadFileDto downloadFileDto = new DownloadFileDto();
        downloadFileDto.setDownloadCode(code);
        downloadFileDto.setFilePath(fileInfo.getFilePath());
        downloadFileDto.setFileName(fileInfo.getFileName());
        // 将其保存到redis当中，当用户调用下载的时候，会从redis中取该随机数
        redisComponent.saveDownloadCode(code, downloadFileDto);
        return getSuccessResponseVO(code);
    }

    protected void download(HttpServletRequest request, HttpServletResponse response, String code) throws Exception {
        DownloadFileDto downloadDto = redisComponent.getDownloadCode(code);
        if (downloadDto == null) {
            return;
        }
        String filePath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + downloadDto.getFilePath();
        String fileName = downloadDto.getFileName();
        response.setContentType("application/x-msdownload; charset=UTF-8");
        if (request.getHeader("User-Agent").toLowerCase().indexOf("msie") > 0) {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        readFile(response, filePath);
    }
}
