package top.gottenzzp.MyNetDisk.entity.component;

import org.springframework.stereotype.Component;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;
import top.gottenzzp.MyNetDisk.entity.dto.UserSpaceDto;

import javax.annotation.Resource;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

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
            // TODO 查询当前用户已使用空间
            spaceDto.setUseSpace(0L);
            spaceDto.setTotalSpace(getSysSettingDto().getUserInitUseSpace() * Constants.MB);
            redisUtils.setex(Constants.REDIS_KEY_USER_SPACE_USE + userId, spaceDto, Constants.REDIS_KEY_EXPIRES_DAY);
        }
        return spaceDto;
    }
}
