package top.gottenzzp.MyNetDisk.entity.enums;


import lombok.Getter;

/**
 * @author gottenzzp
 */

@Getter
public enum ResponseCodeEnum {
    // 200 请求成功 404 请求地址不存在 600 请求参数错误 601 信息已经存在 500 服务器返回错误，请联系管理员 901 登陆超时，请重新登陆 904 网盘空间不足，请扩容
    CODE_200(200, "请求成功"),
    CODE_404(404, "请求地址不存在"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已经存在"),
    CODE_500(500, "服务器返回错误，请联系管理员"),
    CODE_901(901, "登陆超时，请重新登陆"),
    CODE_904(904, "网盘空间不足，请扩容"),
    CODE_902(902, "分享不存在或者已经过期");

    private final Integer code;

    private final String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
