package top.gottenzzp.MyNetDisk.entity.component;

import org.springframework.stereotype.Component;
import top.gottenzzp.MyNetDisk.entity.constants.Constants;
import top.gottenzzp.MyNetDisk.entity.dto.SysSettingsDto;

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

}
