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
public enum FileDelFlagEnums {
    // 0:删除 1:回收站 2:使用中
    DEL(0, "删除"),
    RECYCLE(1, "回收站"),
    USING(2, "使用中");
    private Integer flag;
    private String desc;

    FileDelFlagEnums(Integer flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }
}
