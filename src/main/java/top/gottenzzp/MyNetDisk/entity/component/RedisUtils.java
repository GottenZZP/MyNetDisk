package top.gottenzzp.MyNetDisk.entity.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component("redisUtils")
public class RedisUtils<V> {
    @Resource
    private RedisTemplate<String, V> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);

    /**
     * 获取redis key value
     * @param key   键
     * @return      值
     */
    public V get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 设置redis key value
     * @param key   键
     * @param value 值
     * @return  true：成功，false：失败
     */
    public boolean set(String key, V value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            logger.error("设置redis key:{}, value:{}失败", key, value);
            return false;
        }
    }

    /**
     * 设置redis key value 并设置过期时间
     * @param key   键
     * @param value 值
     * @param time  过期时间，单位秒
     * @return  true：成功，false：失败
     */
    public boolean setex(String key, V value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            logger.error("设置redis key:{}, value:{}失败", key, value);
            return false;
        }
    }
}
