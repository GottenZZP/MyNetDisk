package top.gottenzzp.MyNetDisk.annotation;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface GlobalInterceptor {
    boolean checkParams() default false;


}