package top.gottenzzp.MyNetDisk.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类，用于复制对象的属性
 * @author gottenzzp
 */
public class CopyTools {
    /**
     * 复制列表中的对象属性到新的列表中
     * @param sList 源对象列表
     * @param classz 目标对象的类类型
     * @return 返回复制后的新列表
     */
    public static <T, S> List<T> copyList(List<S> sList, Class<T> classz) {
        List<T> list = new ArrayList<T>();
        for (S s : sList) {
            T t = null;
            try {
                t = classz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(s, t);
            list.add(t);
        }
        return list;
    }

    /**
     * 复制单个对象的属性到新的对象中
     * @param s 源对象
     * @param classz 目标对象的类类型
     * @return 返回复制后的新对象
     */
    public static <T, S> T copy(S s, Class<T> classz) {
        T t = null;
        try {
            t = classz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(s, t);
        return t;
    }
}