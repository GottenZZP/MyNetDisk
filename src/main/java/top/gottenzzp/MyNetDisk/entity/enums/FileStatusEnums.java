package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * @Title: FileStatusEnums
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.enums
 * @Date 2024/1/16 15:46
 * @description: 文件状态枚举
 */
@Getter
public enum FileStatusEnums {
    // 0 转码中 1 转码失败 2 使用中
    TRANSFER(0, "转码中"),
    TRANSFER_FAIL(1, "转码失败"),
    USING(2, "使用中");
    private final Integer status;
    private final String desc;

    FileStatusEnums(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
