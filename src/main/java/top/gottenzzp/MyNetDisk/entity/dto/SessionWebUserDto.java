package top.gottenzzp.MyNetDisk.entity.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class SessionWebUserDto implements Serializable {
    private String nickName;
    private String userId;
    private Boolean admin;
    private String avatar;
}
