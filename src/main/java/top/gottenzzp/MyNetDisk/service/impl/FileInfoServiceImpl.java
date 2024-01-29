package top.gottenzzp.MyNetDisk.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.server.SkeletonNotFoundException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import top.gottenzzp.MyNetDisk.entity.component.RedisComponent;
import top.gottenzzp.MyNetDisk.entity.config.AppConfig;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.SessionWebUserDto;
import top.gottenzzp.MyNetDisk.entity.dto.UploadResultDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;
import top.gottenzzp.MyNetDisk.entity.enums.*;
import top.gottenzzp.MyNetDisk.entity.po.UserInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.query.UserInfoQuery;
import top.gottenzzp.MyNetDisk.entity.vo.PaginationResultVO;
import top.gottenzzp.MyNetDisk.entity.query.SimplePage;
import top.gottenzzp.MyNetDisk.exception.BusinessException;
import top.gottenzzp.MyNetDisk.mappers.FileInfoMapper;
import top.gottenzzp.MyNetDisk.mappers.UserInfoMapper;
import top.gottenzzp.MyNetDisk.service.FileInfoService;
import top.gottenzzp.MyNetDisk.utils.DateUtil;
import top.gottenzzp.MyNetDisk.utils.ProcessUtils;
import top.gottenzzp.MyNetDisk.utils.ScaleFilter;
import top.gottenzzp.MyNetDisk.utils.StringTools;


/**
 * 文件信息表 业务接口实现
 *
 * @author gottenzzp
 */
@Service("fileInfoService")
public class FileInfoServiceImpl implements FileInfoService {
    private static final Logger logger = LoggerFactory.getLogger(FileInfoServiceImpl.class);

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    @Resource
    private AppConfig appConfig;

    @Resource
    @Lazy
    private FileInfoServiceImpl fileInfoService;

    /**
     * 根据条件查询列表
     */
    @Override
    public List<FileInfo> findListByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectList(param);
    }

    /**
     * 根据条件查询列表
     */
    @Override
    public Integer findCountByParam(FileInfoQuery param) {
        return this.fileInfoMapper.selectCount(param);
    }

    /**
     * 按页面查找列表
     * 分页查询方法
     *
     * @param param 参数
     * @return {@link PaginationResultVO}<{@link FileInfo}>
     */
    @Override
    public PaginationResultVO<FileInfo> findListByPage(FileInfoQuery param) {
        // 获取总记录数
        int count = this.findCountByParam(param);
        // 获取每页显示的记录数
        int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();
        // 创建分页对象
        SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
        // 设置分页对象
        param.setSimplePage(page);
        // 获取分页数据
        List<FileInfo> list = this.findListByParam(param);
        // 返回分页结果
        return new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
    }

    /**
     * 新增
     */
    @Override
    public Integer add(FileInfo bean) {
        return this.fileInfoMapper.insert(bean);
    }

    /**
     * 批量新增
     */
    @Override
    public Integer addBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertBatch(listBean);
    }

    /**
     * 批量新增或者修改
     */
    @Override
    public Integer addOrUpdateBatch(List<FileInfo> listBean) {
        if (listBean == null || listBean.isEmpty()) {
            return 0;
        }
        return this.fileInfoMapper.insertOrUpdateBatch(listBean);
    }

    /**
     * 多条件更新
     */
    @Override
    public Integer updateByParam(FileInfo bean, FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.updateByParam(bean, param);
    }

    /**
     * 多条件删除
     */
    @Override
    public Integer deleteByParam(FileInfoQuery param) {
        StringTools.checkParam(param);
        return this.fileInfoMapper.deleteByParam(param);
    }

    /**
     * 根据FileIdAndUserId获取对象
     */
    @Override
    public FileInfo getFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
    }

    /**
     * 根据FileIdAndUserId修改
     */
    @Override
    public Integer updateFileInfoByFileIdAndUserId(FileInfo bean, String fileId, String userId) {
        return this.fileInfoMapper.updateByFileIdAndUserId(bean, fileId, userId);
    }

    /**
     * 根据FileIdAndUserId删除
     */
    @Override
    public Integer deleteFileInfoByFileIdAndUserId(String fileId, String userId) {
        return this.fileInfoMapper.deleteByFileIdAndUserId(fileId, userId);
    }

    /**
     * 上传文件
     *
     * @param webUserDto web用户dto
     * @param fileId     文件id
     * @param file       文件
     * @param fileName   文件名
     * @param filePid    文件pid
     * @param fileMd5    文件md5
     * @param chunkIndex 块索引
     * @param chunks     大块
     * @return {@link UploadResultDto}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadResultDto uploadFile(SessionWebUserDto webUserDto, String fileId, MultipartFile file, String fileName,
                                      String filePid, String fileMd5, Integer chunkIndex, Integer chunks) {
        // 用于返回前端当前上传状态（如是秒传还是正常分片上传），前端根据后端返回的状态给出措施
        UploadResultDto resultDto = null;
        Boolean uploadFlag = true;
        File tempFolder = null;
        try {
            resultDto = new UploadResultDto();
            // 若文件id为空, 则生成一个（因为当第一个文件分片传过来的时候，数据库里是没有该文件的id的，所以当第一个分片传来时需要分配一个id）
            if (StringTools.isEmpty(fileId)) {
                fileId = StringTools.getRandomString(Constants.LENGTH_10);
            }
            resultDto.setFileId(fileId);
            Date curDate = new Date();
            // 从redis中获取用户信息
            UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
            // 若索引为0, 则表示是第一次上传，需要判断用户空间是否足够
            if (chunkIndex == 0) {
                FileInfoQuery infoQuery = new FileInfoQuery();
                infoQuery.setFileMd5(fileMd5);
                infoQuery.setSimplePage(new SimplePage(0, 1));
                infoQuery.setStatus(FileStatusEnums.USING.getStatus());
                // 搜索数据库中是否有相同的文件
                List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);
                // 如果有，则直接秒传
                if (!fileInfoList.isEmpty()) {
                    FileInfo fileInfo = fileInfoList.get(0);
                    // 判断用户空间是否足够（文件大小+用户已使用的空间大小如果大于用户总的空间大小，则表示用户空间不够了）
                    if (fileInfo.getFileSize() + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                        throw new BusinessException(ResponseCodeEnum.CODE_904);
                    }
                    // 更新文件信息
                    // 因为用户上传的文件名称可能和系统中的文件名称一致，所以需要更换文件名称
                    fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
                    fileInfo.setFileId(fileId);
                    fileInfo.setFilePid(filePid);
                    fileInfo.setUserId(webUserDto.getUserId());
                    fileInfo.setCreateTime(curDate);
                    fileInfo.setLastUpdateTime(curDate);
                    fileInfo.setStatus(FileStatusEnums.USING.getStatus());
                    fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
                    fileInfo.setFileMd5(fileMd5);
                    fileInfo.setFileName(fileName);
                    fileInfoMapper.insert(fileInfo);
                    resultDto.setStatus(UploadStatusEnums.UPLOAD_SECONDS.getCode());
                    // 更新用户空间
                    updateUserSpace(webUserDto, fileInfo.getFileSize());
                    return resultDto;
                }
            }
            // 否则正常上传
            // 判断用户空间是否足够（因为每次切片上传，都会在redis中续存已上传的分片大小，所以在判断用户剩余空间时，可以直接在redis中取，
            // 					 从而减少数据库IO操作）
            Long fileTempSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            // 当前文件切片的大小+文件之前上传的切片大小+用户已使用的空间大小如果大于用户总的空间大小，则表示用户空间不够了
            if (file.getSize() + fileTempSize + spaceDto.getUseSpace() > spaceDto.getTotalSpace()) {
                throw new BusinessException(ResponseCodeEnum.CODE_904);
            }
            // 暂存临时目录
            String tempFolderPath = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            // 文件名（如果在上传中的话，为临时目录的目录名）
            String curUserFolderName = webUserDto.getUserId() + fileId;
            tempFolder = new File(tempFolderPath + curUserFolderName);
            if (!tempFolder.exists()) {
                tempFolder.mkdirs();
            }
            File newFile = new File(tempFolder.getPath() + "/" + chunkIndex);
            file.transferTo(newFile);
            // 保存当前分片大小到redis
            redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
            // 如果不是最后一个分片，则将每次的分片大小累加到redis中，方便后续计算用户空间时不用每次都从数据库里取，减少数据库的IO操作
            if (chunkIndex < chunks - 1) {
                resultDto.setStatus(UploadStatusEnums.UPLOADING.getCode());
                return resultDto;
            }
            redisComponent.saveFileTempSize(webUserDto.getUserId(), fileId, file.getSize());
            // 如果是最后一个分片，则表示上传完成，异步记录到数据库中
            String month = DateUtil.format(new Date(), DateTimePatternEnum.YYYY_MM.getPattern());
            String fileSuffix = StringTools.getFileSuffix(fileName);
            // 文件名全称（带后缀）
            String realFileName = curUserFolderName + fileSuffix;
            FileTypeEnums fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            fileName = autoRename(filePid, webUserDto.getUserId(), fileName);
            // 写入数据库
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(fileId);
            fileInfo.setFilePid(filePid);
            fileInfo.setUserId(webUserDto.getUserId());
            fileInfo.setCreateTime(curDate);
            fileInfo.setLastUpdateTime(curDate);
            fileInfo.setFilePath(month + "/" + realFileName);
            fileInfo.setStatus(FileStatusEnums.TRANSFER.getStatus());
            fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            fileInfo.setFileMd5(fileMd5);
            fileInfo.setFileName(fileName);
            fileInfo.setFileCategory(fileTypeEnums.getCategory().getCategory());
            fileInfo.setFileType(fileTypeEnums.getType());
            fileInfo.setFolderType(FileFolderTypeEnums.FILE.getType());
            fileInfoMapper.insert(fileInfo);
            // 文件总空间大小
            Long totalSize = redisComponent.getFileTempSize(webUserDto.getUserId(), fileId);
            // 更新用户已使用的空间大小
            updateUserSpace(webUserDto, totalSize);
            resultDto.setStatus(UploadStatusEnums.UPLOAD_FINISH.getCode());
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    fileInfoService.transferFile(fileInfo.getFileId(), webUserDto);
                }
            });
            return resultDto;
        } catch (BusinessException e) {
            logger.error("文件上传失败", e);
            uploadFlag = false;
            throw e;
        } catch (Exception e) {
            logger.error("文件上传失败", e);
            uploadFlag = false;
        } finally {
            if (!uploadFlag && tempFolder != null) {
                // 如果上传失败，则删除临时文件夹
                try {
                    FileUtils.deleteDirectory(tempFolder);
                } catch (IOException e) {
                    logger.error("删除临时文件夹失败", e);
                }
            }
        }
        return resultDto;
    }

    /**
     * 创建新文件夹
     *
     * @param filePid    文件pid
     * @param userId     用户id
     * @param folderName 文件夹名称
     * @return {@link FileInfo}
     */
    @Override
    public FileInfo newFolder(String filePid, String userId, String folderName) {
        // 检查文件夹名称是否合法
        checkFileName(filePid, userId, folderName, FileFolderTypeEnums.FOLDER.getType());
        // 插入到数据库中
        Date curDate = new Date();
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(StringTools.getRandomString(Constants.LENGTH_10));
        fileInfo.setFilePid(filePid);
        fileInfo.setUserId(userId);
        fileInfo.setCreateTime(curDate);
        fileInfo.setLastUpdateTime(curDate);
        fileInfo.setFileName(folderName);
        fileInfo.setStatus(FileStatusEnums.USING.getStatus());
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfo.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        fileInfoMapper.insert(fileInfo);
        return fileInfo;
    }

    /**
     * 文件重命名
     *
     * @param fileId   文件id
     * @param userId   用户id
     * @param fileName 文件名称
     * @return {@link FileInfo}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileInfo rename(String fileId, String userId, String fileName) {
        // 先检索出该文件
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }
        // 检查是否有重名文件
        String filePid = fileInfo.getFilePid();
        checkFileName(filePid, userId, fileName, fileInfo.getFolderType());
        // 由于前端传来的是不带后缀的，所以要拼接上
        if (FileFolderTypeEnums.FILE.getType().equals(fileInfo.getFolderType())) {
            fileName += StringTools.getFileSuffix(fileInfo.getFileName());
        }
        // 更新重命名后的文件
        Date curDate = new Date();
        FileInfo dbFile = new FileInfo();
        dbFile.setFileName(fileName);
        dbFile.setLastUpdateTime(curDate);
        fileInfoMapper.updateByFileIdAndUserId(dbFile, fileId, userId);
        // 以防万一，检索一下看数据库里是否有其他与重命名文件相同名称的文件
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFilePid(filePid);
        infoQuery.setUserId(userId);
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setFileName(fileName);
        Integer count = fileInfoMapper.selectCount(infoQuery);
        if (count > 1) {
            throw new BusinessException("文件名" + fileName + "已经存在");
        }
        fileInfo.setFileName(fileName);
        fileInfo.setLastUpdateTime(curDate);
        return fileInfo;
    }

    /**
     * 改变文件文件夹
     *
     * @param fileIds 要移动的文件id列表
     * @param filePid 要改变的目录id
     * @param userId  用户id
     */
    @Override
    public void changeFileFolder(String fileIds, String filePid, String userId) {
        // 若文件id本身就是文件的上级目录id，再移动文件就无意义了。
        if (fileIds.equals(filePid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果不是根目录，则检测要转移到的目录该用户是否存在，且该目录是否是有效的。
        if (!Constants.ZERO_STR.equals(filePid)) {
            FileInfo fileInfo = fileInfoService.getFileInfoByFileIdAndUserId(filePid, userId);
            if (fileInfo == null || !FileDelFlagEnums.USING.getFlag().equals(fileInfo.getDelFlag())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }
        // 检索当前目录下的所有文件和文件夹，并构造一个Map，key为这些文件的文件名，value为文件本身
        String[] fileIdList = fileIds.split(",");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFilePid(filePid);
        infoQuery.setUserId(userId);
        List<FileInfo> infoList = findListByParam(infoQuery);
        Map<String, FileInfo> dbFileNameMap = infoList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (data1, data2) -> data2));
        // 在所有目录中检索当前要转移的文件
        infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFileIdArray(fileIdList);
        List<FileInfo> selectFileList = findListByParam(infoQuery);
        // 遍历所有要转移的文件，若该文件已经存在在要移动进的目录内，则将其重命名后再移动进去
        for (FileInfo item : selectFileList) {
            FileInfo fileInfo = dbFileNameMap.get(item.getFileName());
            FileInfo updateFile = new FileInfo();
            if (fileInfo != null) {
                String filaName = StringTools.rename(item.getFileName());
                updateFile.setFileName(filaName);
            }
            updateFile.setFilePid(filePid);
            fileInfoMapper.updateByFileIdAndUserId(updateFile, item.getFileId(), userId);
        }
    }

    /**
     * 批量删除文件
     *
     * @param userId  使用者id
     * @param fileIds 文件ids
     */
    @Override
    public void removeFile2RecycleBatch(String userId, String fileIds) {
        // 待删除文件列表
        String[] fileIdList = fileIds.split(",");
        // 搜索数据库是否有要删除的文件
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFileIdArray(fileIdList);
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);
        // 如果没有要删除的文件，则直接返回
        if (fileInfoList.isEmpty()) {
            return;
        }
        // 获取所有要删除的文件的子文件夹
        List<String> delFilePidList = new ArrayList<>();
        // 遍历所有要删除的子目录，将其加入到删除的目录列表中
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileList(delFilePidList, userId, fileInfo.getFileId(), FileDelFlagEnums.USING.getFlag());
        }
        // 如果删除目录列表不为空，则将设为删除状态
        if (!delFilePidList.isEmpty()) {
            FileInfo updateFileInfo = new FileInfo();
            updateFileInfo.setDelFlag(FileDelFlagEnums.DEL.getFlag());
            fileInfoMapper.updateFileDelFlagBatch(updateFileInfo, userId, delFilePidList, null, FileDelFlagEnums.USING.getFlag());
        }
        // 将要删除的文件设为删除状态
        List<String> delFileIdList = Arrays.asList(fileIdList);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setRecoveryTime(new Date());
        fileInfo.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileIdList, FileDelFlagEnums.USING.getFlag());
    }

    /**
     * 批量恢复文件
     *
     * @param userId  用户id
     * @param fileIds 文件ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recoverFileBatch(String userId, String fileIds) {
        String[] fileList = fileIds.split(",");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFileIdArray(fileList);
        infoQuery.setUserId(userId);
        infoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);
        ArrayList<String> delSubFile = new ArrayList<>();
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
                findAllSubFolderFileList(delSubFile, userId, fileInfo.getFileId(), FileDelFlagEnums.DEL.getFlag());
            }
        }
        // 查询所有根目录文件
        infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setFilePid(Constants.ZERO_STR);
        List<FileInfo> infoList = findListByParam(infoQuery);
        Map<String, FileInfo> rootFileMap = infoList.stream().collect(Collectors.toMap(FileInfo::getFileName, Function.identity(), (data1, data2) -> data2));

        // 查询所有所选文件，将目录下的所有删除的文件更新为使用中
        if (!delSubFile.isEmpty()) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
            fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, delSubFile, null, FileDelFlagEnums.DEL.getFlag());
        }

        // 将选中的文件更新为正常，从父级目录到根目录
        List<String> delFileList = Arrays.asList(fileList);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setDelFlag(FileDelFlagEnums.USING.getFlag());
        fileInfo.setFilePid(Constants.ZERO_STR);
        fileInfo.setLastUpdateTime(new Date());
        fileInfoMapper.updateFileDelFlagBatch(fileInfo, userId, null, delFileList, FileDelFlagEnums.RECYCLE.getFlag());

        // 将所选文件重命名
        for (FileInfo info : fileInfoList) {
            FileInfo rootFileInfo = rootFileMap.get(info.getFileName());
            if (rootFileInfo != null) {
                String rename = StringTools.rename(info.getFileName());
                FileInfo updateInfo = new FileInfo();
                updateInfo.setFileName(rename);
                fileInfoMapper.updateByFileIdAndUserId(updateInfo, info.getFileId(), userId);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delFileBatch(String userId, String fileIds, Boolean adminOp) {
        String[] fileIdList = fileIds.split(",");
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFileIdArray(fileIdList);
        if (!adminOp) {
            infoQuery.setDelFlag(FileDelFlagEnums.RECYCLE.getFlag());
        }
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);

        ArrayList<String> listAllSubfolders = new ArrayList<>();
        // 获取所选文件的子目录ID
        for (FileInfo fileInfo : fileInfoList) {
            if (FileFolderTypeEnums.FOLDER.getType().equals(fileInfo.getFolderType())) {
                findAllSubFolderFileList(listAllSubfolders, userId, fileInfo.getFileId(), FileDelFlagEnums.DEL.getFlag());
            }
        }
        // 删除所选目录
        if (!listAllSubfolders.isEmpty()) {
            fileInfoMapper.delFileBatch(userId, listAllSubfolders, null, adminOp ? null : FileDelFlagEnums.DEL.getFlag());
        }
        // 删除所选文件
        fileInfoMapper.delFileBatch(userId, null, Arrays.asList(fileIdList), adminOp ? null : FileDelFlagEnums.RECYCLE.getFlag());
        // 更新用户空间
        Long useSpace = fileInfoMapper.selectUseSpace(userId);
        UserInfo userInfo = new UserInfo();
        userInfo.setUseSpace(useSpace);
        userInfoMapper.updateByUserId(userInfo, userId);
        // 设置缓存
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(userId);
        spaceDto.setUseSpace(useSpace);
        redisComponent.saveUserSpaceUse(userId, spaceDto);
    }

    @Override
    public Long getUserUseSpace(String userId) {
        return fileInfoMapper.selectUseSpace(userId);
    }

    /**
     * 检查fileId是否是分享的根目录下的文件
     *
     * @param rootFilePid 分享的根文件id
     * @param userId      用户id
     * @param fileId      接口传来的要看的文件id
     */
    @Override
    public void checkRootFilePid(String rootFilePid, String userId, String fileId) {
        // 如果接口传来的文件id为空，则抛出异常
        if (StringTools.isEmpty(fileId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果接口传来的文件id和分享的根文件id相同，则表示是根目录，直接返回
        if (rootFilePid.equals(fileId)) {
            return;
        }
        // 否则递归检查该文件的父级目录是否是分享的根目录，防止用户通过接口传来的文件id不是分享的根目录下的文件
        checkFilePid(rootFilePid, fileId, userId);
    }

    private void checkFilePid(String rootFilePid, String fileId, String userId) {
        // 如果该用户下直接搜不到该文件，则抛出异常
        FileInfo fileInfo = this.fileInfoMapper.selectByFileIdAndUserId(fileId, userId);
        if (fileInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果该文件的父级目录是该用户的根目录，直接返回（不允许分享根目录）
        if (Constants.ZERO_STR.equals(fileInfo.getFilePid())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果该文件的父级目录是分享的根目录，则表示是分享的根目录下的文件，直接返回
        if (fileInfo.getFilePid().equals(rootFilePid)) {
            return;
        }
        // 否则递归检查该文件的父级目录是否是分享的根目录
        checkFilePid(rootFilePid, fileInfo.getFilePid(), userId);
    }

    /**
     * 获取fileId下的所有子文件夹
     *
     * @param delFilePidList 删除文件pid列表
     * @param userId         使用者id
     * @param fileId         文件id
     * @param flag           删除标记
     */
    private void findAllSubFolderFileList(List<String> delFilePidList, String userId, String fileId, Integer flag) {
        // 将当前文件加入到删除文件pid列表中
        delFilePidList.add(fileId);
        // 检索当前文件夹下的所有文件
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setUserId(userId);
        infoQuery.setFilePid(fileId);
        infoQuery.setDelFlag(flag);
        infoQuery.setFolderType(FileFolderTypeEnums.FOLDER.getType());
        List<FileInfo> fileInfoList = fileInfoMapper.selectList(infoQuery);
        // 递归遍历所有文件，将其加入到删除文件pid列表中
        for (FileInfo fileInfo : fileInfoList) {
            findAllSubFolderFileList(delFilePidList, userId, fileInfo.getFileId(), flag);
        }
    }

    /**
     * 检查文件夹名称, 若同级目录有重名的文件夹，则抛出异常
     *
     * @param filePid    文件pid
     * @param userId     用户id
     * @param folderName 文件夹名称
     * @param folderType 文件夹类型
     */
    private void checkFileName(String filePid, String userId, String folderName, Integer folderType) {
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFolderType(folderType);
        infoQuery.setFilePid(filePid);
        infoQuery.setUserId(userId);
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setFileName(folderName);
        Integer count = fileInfoMapper.selectCount(infoQuery);
        if (count > 0) {
            throw new BusinessException("此目录下已经存在同名文件夹，请修改名称");
        }
    }



    @Async
    public void transferFile(String fileId, SessionWebUserDto webUserDto) {
        Boolean transferSuccess = true;
        String targetFilePath = null;
        String cover = null;
        FileTypeEnums fileTypeEnums = null;
        FileInfo fileInfo = fileInfoMapper.selectByFileIdAndUserId(fileId, webUserDto.getUserId());
        try {
            // 若文件为空，或者文件不是转码中，则无需处理
            if (fileInfo == null || !FileStatusEnums.TRANSFER.getStatus().equals(fileInfo.getStatus())) {
                return;
            }
            // 否则转移临时文件到正式文件目录中去
            String tempFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_TEMP;
            String curUserFolderName = webUserDto.getUserId() + fileId;
            File sourceFile = new File(tempFolderName + curUserFolderName);
            // 获取文件后缀
            String fileSuffix = StringTools.getFileSuffix(fileInfo.getFileName());
            String month = DateUtil.format(fileInfo.getCreateTime(), DateTimePatternEnum.YYYY_MM.getPattern());
            // 正式文件目录
            String targetFolderName = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
            File targetFile = new File(targetFolderName + '/' + month);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            // 实际文件名
            String realFileName = curUserFolderName + fileSuffix;
            targetFilePath = targetFile.getPath() + "/" + realFileName;

            // 合并分片文件
            union(sourceFile.getPath(), targetFilePath, fileInfo.getFileName(), true);

            // 视频文件切割
            fileTypeEnums = FileTypeEnums.getFileTypeBySuffix(fileSuffix);
            if (fileTypeEnums.equals(FileTypeEnums.VIDEO)) {
                cutFileForVideo(fileId, targetFilePath);
                // 视频生成缩略图
                cover = month + "/" + curUserFolderName + Constants.IMAGE_PNG_SUFFIX;
                String coverPath = targetFolderName + "/" + cover;
                ScaleFilter.createCover4Video(new File(targetFilePath), Constants.LENGTH_150, new File(coverPath));
            } else if (fileTypeEnums.equals(FileTypeEnums.IMAGE)) {
                cover = month + "/" + realFileName.replace(".", "_.");
                String coverPath = targetFolderName + cover;
                Boolean created = ScaleFilter.createThumbnailWidthFFmpeg(new File(targetFilePath), Constants.LENGTH_150,
                        new File(coverPath), false);
                // 如果生成缩略图失败，则表示原图像素已经很小了，直接将原图复制为缩略图。
                if (!created) {
                    FileUtils.copyFile(new File(targetFilePath), new File(coverPath));
                }
            }
        } catch (Exception e) {
            logger.error("文件转码失败, 文件ID:{}, userId:{}", fileId, webUserDto.getUserId(), e);
            transferSuccess = false;
        } finally {
            FileInfo updateFile = new FileInfo();
            updateFile.setFileSize(new File(targetFilePath).length());
            updateFile.setFileCover(cover);
            updateFile.setStatus(transferSuccess ? FileStatusEnums.USING.getStatus() :
                    FileStatusEnums.TRANSFER_FAIL.getStatus());
            fileInfoMapper.updateFileStatusWithOldStatus(fileId, webUserDto.getUserId(), updateFile, FileStatusEnums.TRANSFER.getStatus());
        }
    }

    private void cutFileForVideo(String fileId, String videoFilePath) {
        File tsfile = new File(videoFilePath.substring(0, videoFilePath.lastIndexOf('.')));
        if (!tsfile.exists()) {
            tsfile.mkdirs();
        }
        // 这个命令的作用是将输入文件的视频和音频流直接复制到输出文件中，并将视频流转换为Annex B格式。
        final String CMD_TRANSFER_2TS = "ffmpeg -y -i %s  -vcodec copy -acodec copy -vbsf h264_mp4toannexb %s";
        // 这个命令的作用是将输入视频文件按照30秒的时长分割成小片段，并生成一个分段列表文件，输出的小片段文件以指定的命名模式保存在指定的目录中。
        final String CMD_CUT_TS = "ffmpeg -i %s -c copy -map 0 -f segment -segment_list %s -segment_time 30 %s/%s_%%4d.ts";
        String tsPath = tsfile + "/" + Constants.TS_NAME;
        // 生成.ts文件
        String cmd = String.format(CMD_TRANSFER_2TS, videoFilePath, tsPath);
        ProcessUtils.executeCommand(cmd, false);
        // 生成索引文件.m3u8和ts切片文件
        cmd = String.format(CMD_CUT_TS, tsPath, tsfile.getPath() + "/" + Constants.M3U8_NAME, tsfile.getPath(), fileId);
        ProcessUtils.executeCommand(cmd, false);
        // 删除index.ts
        new File(tsPath).delete();
    }

    private void union(String dirPath, String toFilePath, String fileName, Boolean delSource) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            throw new BusinessException("目录不存在");
        }
        File[] fileList = dir.listFiles();
        File targetFile = new File(toFilePath);
        RandomAccessFile writeFile = null;
        try {
            writeFile = new RandomAccessFile(targetFile, "rw");
            byte[] bytes = new byte[1024 * 10];
            for (int i = 0; i < fileList.length; i++) {
                int len = -1;
                File chunkFile = new File(dirPath + "/" + i);
                RandomAccessFile readFile = null;
                try {
                    readFile = new RandomAccessFile(chunkFile, "r");
                    while ((len = readFile.read(bytes)) != -1) {
                        writeFile.write(bytes, 0, len);
                    }
                } catch (Exception e) {
                    logger.error("合并分片失败", e);
                    throw new BusinessException("合并分片失败");
                } finally {
                    readFile.close();
                }
            }
        } catch (Exception e) {
            logger.error("合并文件:{}失败", fileName, e);
            throw new BusinessException("合并文件" + fileName + "失败");
        } finally {
            if (writeFile != null) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (delSource && dir.exists()) {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自动重命名
     *
     * @param filePid  文件pid
     * @param userId   用户id
     * @param fileName 文件名称
     * @return {@link String}
     */
    private String autoRename(String filePid, String userId, String fileName) {
        // 去数据库搜索是否有重名的文件
        FileInfoQuery infoQuery = new FileInfoQuery();
        infoQuery.setFilePid(filePid);
        infoQuery.setUserId(userId);
        infoQuery.setDelFlag(FileDelFlagEnums.USING.getFlag());
        infoQuery.setFileName(fileName);
        Integer count = fileInfoMapper.selectCount(infoQuery);
        // 如果有重名的文件，则重命名
        if (count > 0) {
            fileName = StringTools.rename(fileName);
        }
        return fileName;
    }

    /**
     * 更新用户空间
     *
     * @param webUserDto web用户dto
     * @param useSpace   使用空间
     */
    private void updateUserSpace(SessionWebUserDto webUserDto, Long useSpace) {
        // 更新用户空间, count为更新的条数
        Integer count = userInfoMapper.updateUserSpace(webUserDto.getUserId(), useSpace, null);
        // 如果更新失败，则抛出异常
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_904);
        }
        // 更新redis中的用户空间
        UserSpaceDto spaceDto = redisComponent.getUserSpaceUse(webUserDto.getUserId());
        spaceDto.setUseSpace(spaceDto.getUseSpace() + useSpace);
        redisComponent.saveUserSpaceUse(webUserDto.getUserId(), spaceDto);
    }
}