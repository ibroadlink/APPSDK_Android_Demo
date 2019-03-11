package cn.com.broadlink.blappsdkdemo.common;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON处理工具
 */
public class DataParseUtils {

    /**
     * 取得所有域变量（包括父类）
     * 
     * @param cls
     *            类
     * @param end
     *            最终父类
     */
    public static List<Field> getFields(Class<?> cls, Class<?> end) {

        List<Field> list = new ArrayList<Field>();

        if (!cls.equals(end)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                list.add(field);
            }

            Class<?> superClass = (Class<?>) cls.getGenericSuperclass();
            list.addAll(getFields(superClass, end));
        }

        return list;
    }
}
