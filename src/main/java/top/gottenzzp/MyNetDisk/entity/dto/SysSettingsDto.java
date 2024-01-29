package top.gottenzzp.MyNetDisk.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author gottenzzp
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SysSettingsDto implements Serializable {
    private String registerEmailTitle = "欢迎注册MyNetDisk, 邮箱验证";

    private String registerEmailContent = "欢迎注册MyNetDisk, 您的验证码为: %s, 请在15分钟内完成验证";

    private Integer userInitUseSpace = 1024;
}
