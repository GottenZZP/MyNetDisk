package top.gottenzzp.MyNetDisk.entity.component;

import org.springframework.stereotype.Component;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.DownloadFileDto;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;
import top.gottenzzp.MyNetDisk.entity.po.FileInfo;
import top.gottenzzp.MyNetDisk.entity.query.FileInfoQuery;
import top.gottenzzp.MyNetDisk.mappers.FileInfoMapper;

import javax.annotation.Resource;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private FileInfoMapper<FileInfo, FileInfoQuery> fileInfoMapper;

    /**
     * 若redis中不存在邮箱配置, 则初始化一个并返回
     * @return SysSettingsDto
     */
    public SysSettingsDto getSysSettingDto() {
        SysSettingsDto sysSettingsDto = (SysSettingsDto) redisUtils.get(Constants.REDIS_KEY_SYS_SETTINGS);
        if (sysSettingsDto == null) {
            sysSettingsDto = new SysSettingsDto();
            redisUtils.set(Constants.REDIS_KEY_SYS_SETTINGS, sysSettingsDto);
        }
        return sysSettingsDto;
    }

    public void saveUserSpaceUse(String userId, UserSpaceDto userSpaceDto) {
        redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, userSpaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 获取用户已使用空间
     * @param userId 用户id
     * @return {{@link UserSpaceDto}}
     */
    public UserSpaceDto getUserSpaceUse(String userId) {
        // 从redis中获取用户已使用空间
        UserSpaceDto spaceDto = (UserSpaceDto) redisUtils.get(Constants.REDIS_KEY_USER_SPACE_USE + userId);
        // 若redis中不存在, 则初始化一个并返回
        if (spaceDto == null) {
            spaceDto = new UserSpaceDto();
            spaceDto.setUseSpace(fileInfoMapper.selectUseSpace(userId));
            spaceDto.setTotalSpace(getSysSettingDto().getUserInitUseSpace() * Constants.MB);
            redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }

    public void saveFileTempSize(String userId, String fileId, Long size) {
        // 获取当前文件已上传大小
        Long curSize = getFileTempSize(userId, fileId);
        // 将当前文件已上传大小加上本次上传大小上传至redis
        redisUtils.setex(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId, curSize + size, Constants.REDIS_KEY_EXPIRES_HOUR);
    }

    public Long getFileTempSize(String userId, String fileId) {
        return getFileSizeFromRedis(Constants.REDIS_KEY_USER_FILE_TEMP_SIZE + userId + fileId);
    }

    /**
     * 从redis中获取文件大小
     *
     * @param key key
     * @return {@link Long}
     */
    private Long getFileSizeFromRedis(String key) {
        Object sizeObj = redisUtils.get(key);
        if (sizeObj == null) {
            return 0L;
        }
        if (sizeObj instanceof Integer) {
            return ((Integer) sizeObj).longValue();
        } else if (sizeObj instanceof Long) {
            return (Long) sizeObj;
        }
        return 0L;
    }

    /**
     * 保存下载代码
     *
     * @param code            密码
     * @param downloadFileDto 下载文件DTO
     */
    public void saveDownloadCode(String code, DownloadFileDto downloadFileDto) {
        redisUtils.setex(Constants.REDIS_KEY_DOWNLOAD + code, downloadFileDto, Constants.REDIS_KEY_EXPIRES_FIVE_MIN);
    }

    /**
     * 获取下载代码
     *
     * @param code 密码
     * @return {@link DownloadFileDto}
     */
    public DownloadFileDto getDownloadCode(String code) {
        return (DownloadFileDto) redisUtils.get(Constants.REDIS_KEY_DOWNLOAD + code);
    }
}
