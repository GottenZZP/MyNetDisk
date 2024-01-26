package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * @Title: ShareValidTypeEnums
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.enums
 * @Date 2024/1/26 15:58
 * @description: 分享类型枚举
 */
@Getter
public enum ShareValidTypeEnums {
    // 生效时间枚举
    DAT_1(0, 1,"1天"),
    DAT_2(1, 7, "7天"),
    DAT_30(2, 30, "30天"),
    FOREVER(3, -1, "永久有效");
    private final Integer type;
    private final Integer days;
    private final String desc;

    ShareValidTypeEnums(Integer type, Integer days, String desc) {
        this.type = type;
        this.days = days;
        this.desc = desc;
    }

    public static ShareValidTypeEnums getByType(Integer type) {
        for (ShareValidTypeEnums typeEnums : ShareValidTypeEnums.values()) {
            if (typeEnums.getType().equals(type)) {
                return typeEnums;
            }
        }
        return null;
    }
}
