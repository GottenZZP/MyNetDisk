package top.gottenzzp.MyNetDisk.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingsDto implements Serializable {
    private String registerMailTitle = "欢迎注册MyNetDisk, 邮箱验证";

    private String registerMailContent = "欢迎注册MyNetDisk, 您的验证码为: %s, 请在15分钟内完成验证";

    private Integer userInitUseSpace = 5242880;

    public String getRegisterMailTitle() {
        return registerMailTitle;
    }

    public void setRegisterMailTitle(String registerMailTitle) {
        this.registerMailTitle = registerMailTitle;
    }

    public String getRegisterMailContent() {
        return registerMailContent;
    }

    public void setRegisterMailContent(String registerMailContent) {
        this.registerMailContent = registerMailContent;
    }

    public Integer getUserInitUseSpace() {
        return userInitUseSpace;
    }

    public void setUserInitUseSpace(Integer userInitUseSpace) {
        this.userInitUseSpace = userInitUseSpace;
    }
}
