package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * @Title: FileFolderTypeEnums
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.enums
 * @Date 2024/1/17 22:40
 * @description: 文件类型的枚举
 */
@Getter
public enum FileFolderTypeEnums {
    // 文件类型枚举
    FILE(0, "文件"),
    FOLDER(1, "文件夹");

    private final Integer type;
    private final String desc;

    FileFolderTypeEnums(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
