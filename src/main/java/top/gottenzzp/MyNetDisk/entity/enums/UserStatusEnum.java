package top.gottenzzp.MyNetDisk.entity.enums;

import lombok.Getter;

/**
 * @author gottenzzp
 */

@Getter
public enum UserStatusEnum {
    // 0 禁用 1 启用
    DISABLE(0, "禁用"), ENABLE(1, "启用");

    private final Integer status;
    private final String desc;

    UserStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static UserStatusEnum getByStatus(Integer status) {
        for (UserStatusEnum value : UserStatusEnum.values()) {
            if (value.getStatus().equals(status)) {
                return value;
            }
        }
        return null;
    }
}
