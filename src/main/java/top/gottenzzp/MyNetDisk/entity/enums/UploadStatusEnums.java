package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * @Title: UploadStatusEnums
 * @Author GottenZZP
 * @Package top.gottenzzp.MyNetDisk.entity.enums
 * @Date 2024/1/16 17:11
 * @description: 上传状态枚举
 */

@Getter
public enum UploadStatusEnums {
    // 上传枚举状态
    UPLOAD_SECONDS("upload_seconds", "秒传"),
    UPLOADING("uploading", "上传中"),
    UPLOAD_FINISH("upload_finish", "上传完成"),;

    private final String code;
    private final String desc;

    UploadStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
