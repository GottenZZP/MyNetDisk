package top.gottenzzp.MyNetDisk.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Title: SessionShareDto
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.dto
 * @Date 2024/1/28 23:22
 * @description: 用户共享dto
 */
@Getter
@Setter
public class SessionShareDto {
    private String shareId;
    private String shareUserId;
    private Date expireTime;
    private String fileId;
}
