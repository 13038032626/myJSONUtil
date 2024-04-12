import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectMatcher<T> {
    //根据类来匹配破损JSON

    static List<String> keys;
    static List<List<String>> attrs;

    public T match(String json, Class<T> clazz) {
        try {
            attrs = getFieldsFromClass(clazz);
            T obj = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            Pattern pattern = Pattern.compile("\"([^\"]*?)\"");
            Matcher matcher = pattern.matcher(json);

            while (matcher.find()) {
                String rowKey = matcher.group(1);
                keys.add(rowKey.substring(1, rowKey.length() - 2));
            }
            for (Field field : fields) {
                String key = field.getName();
                String value = getValueFromJson(json, key);
                if (value != null) {
                    setFieldValue(obj, field, value);
                }
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getValueFromJson(String json, String key) {
        //为了避免由外层 name 错误匹配到内层 name，
        // 需要一个策略<根据上下文有效内容判断所属>：判断 name 前k个key里有无引用类型的key，有则表明是内层的
        Pattern pattern = Pattern.compile("\"" + key + "\":\"?([^\"]+)\"?");
        Matcher matcher = pattern.matcher(json);

        //遍历attrs属性列表，对于里面有name的外层引用类型子对象，判断在破损JSON中，里子对象key的距离
        while (matcher.find()) {
            int startIndex = matcher.start();
            dode:
            for (List<String> list : attrs) {
                String outerAttr = list.get(0);
                int outerAttrStart = json.indexOf(outerAttr);
                for (int i = 1; i < list.size(); i++) {
                    String str = list.get(i);
                    if (str.equals(key)) {
                        if (outerAttrStart - startIndex < 50) {
                            break dode;
                        }
                    } else {
                        String outerOne = matcher.group(1);
                        return outerOne.split(":")[1];
                    }
                }
            }
        }
        return null;
    }

    private List<List<String>> getFieldsFromClass(Class<?> clazz) {
        List<List<String>> allFields = new ArrayList<>();
        getFieldsRecursively(clazz, allFields, new ArrayList<>());
        return allFields;
    }

    private static void getFieldsRecursively(Class<?> clazz, List<List<String>> allFields, List<String> prefix) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            List<String> fieldPath = new ArrayList<>(prefix);
            fieldPath.add(field.getName());
            if (fieldType.isPrimitive() || fieldType.equals(String.class)) {
                allFields.add(fieldPath);
            } else {
                getFieldsRecursively(fieldType, allFields, fieldPath);
            }
        }
    }

    private void setFieldValue(T obj, Field field, String value) {
        try {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (fieldType == String.class) {
                field.set(obj, value);
            } else if (fieldType == int.class || fieldType == Integer.class) {
                field.set(obj, Integer.parseInt(value));
            } else if (fieldType == long.class || fieldType == Long.class) {
                field.set(obj, Long.parseLong(value));
            } else if (fieldType == float.class || fieldType == Float.class) {
                field.set(obj, Float.parseFloat(value));
            } else if (fieldType == double.class || fieldType == Double.class) {
                field.set(obj, Double.parseDouble(value));
            } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                field.set(obj, Boolean.parseBoolean(value));
            } else {
                // 其他类型暂不处理
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
