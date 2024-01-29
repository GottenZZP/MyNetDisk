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
    /**
     * 分享Id
     */
    private String shareId;
    /**
     * 分享人Id
     */
    private String shareUserId;
    /**
     * 分享失效时间
     */
    private Date expireTime;
    /**
     * 文件id
     */
    private String fileId;
}
