package top.gottenzzp.MyNetDisk.entity.enums;


import lombok.Getter;

/**
 * @author gottenzzp
 */

@Getter
public enum DateTimePatternEnum {
    // 日期格式
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), YYYY_MM_DD("yyyy-MM-dd");

    private final String pattern;

    DateTimePatternEnum(String pattern) {
        this.pattern = pattern;
    }

}
