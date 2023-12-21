package top.gottenzzp.MyNetDisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "top.gottenzzp.MyNetDisk")
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
public class MyNetDiskApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyNetDiskApplication.class, args);
    }
}
