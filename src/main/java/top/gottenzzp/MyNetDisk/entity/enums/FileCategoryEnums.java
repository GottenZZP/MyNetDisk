package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * 文件分类枚举
 * @author gottenzzp
 */
@Getter
public enum FileCategoryEnums {
    // 1:视频 2:音频 3:图片 4:文档 5:其他
    VIDEO(1, "video", "视频"),
    MUSIC(2, "music", "音频"),
    IMAGE(3, "image", "图片"),
    DOC(4, "doc", "文档"),
    OTHERS(5, "others", "其他");
    private final Integer category;
    private final String code;
    private final String desc;

    FileCategoryEnums(Integer category, String code, String desc) {
        this.category = category;
        this.code = code;
        this.desc = desc;
    }

    public static FileCategoryEnums getByCode(String code) {
        for (FileCategoryEnums value : FileCategoryEnums.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
