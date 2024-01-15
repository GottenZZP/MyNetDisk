package top.gottenzzp.MyNetDisk.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    /**
     * @return 是否校验参数
     */
    boolean checkParams() default false;

    /**
     * @return 是否校验登录
     */
    boolean checkLogin() default true;

    /**
     * @return 是否校验管理员
     */
    boolean checkAdmin() default false;
}
