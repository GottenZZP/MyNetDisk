package top.gottenzzp.MyNetDisk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "top.gottenzzp.MyNetDisk")
@MapperScan(basePackages = "top.gottenzzp.MyNetDisk.mappers")
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class MyNetDiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyNetDiskApplication.class, args);
    }
}
