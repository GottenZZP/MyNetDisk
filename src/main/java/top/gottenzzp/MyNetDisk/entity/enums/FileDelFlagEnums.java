package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @Title: FileDelFlagEnums
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.enums
 * @Date 2024/1/16 10:56
 * @description: 文件删除状态枚举
 */
@Getter
public enum FileDelFlagEnums {
    // 0:删除 1:回收站 2:使用中
    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");
    private final Integer flag;
    private final String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }
}
