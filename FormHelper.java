import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.constant.Constable;
import java.sql.*;
import java.util.*;

public class FormHelper {
    //尝试获取到数据库上下正确的JSON，通过将异常JSON合理投影到正确的JSON槽上，来尽量获取更多信息
    public static LinkedHashMap<String, Object> getDataFromDB(String tableName, String field, String JSON) {
        LinkedHashMap<String, Object> rightMap = tryGetCurrentForm(tableName, field);
        return compareAndSet(rightMap, JSON);
    }

    private static LinkedHashMap<String, Object> tryGetCurrentForm(String tableName, String field) {
        LinkedHashMap<String, Object> result = null;

        String url = "jdbc:mysql://192.168.56.10/demo";
        String username = "root";
        String password = "root";

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection(url, username, password);
            // 创建 SQL 语句
            String query = "SELECT " + field + " FROM " + tableName;

            // 创建 Statement 对象
            Statement statement = connection.createStatement();
            // 执行查询
            ResultSet resultSet = statement.executeQuery(query);
            // 将查询结果放入 LinkedHashMap
            while (resultSet.next() && result == null) {
                try {
                    String temp = (String) resultSet.getObject(1); // 假设查询结果只有一列
                    LinkedHashMap<String, Object> jsonData =
                            (LinkedHashMap) objectMapper.readValue(temp, Map.class);
                    result = jsonData;
                } catch (JsonProcessingException e) {
                    continue;
                }
            }
            if (result == null) {
                return null; //没找到合适的JSON
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static LinkedHashMap<String, Object> compareAndSet(LinkedHashMap<String, Object> template, String targetJSON) {
        //通过和已知正确的JSON对应的LinkedHashMap对比，尽量将信息填充到新LinkedHashMap中
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        String[] split = targetJSON.split("[{},]");
        int splitIndex = 0;
        for (Map.Entry<String, Object> entry : template.entrySet()) {
            //linkedHashMap为什么不提供将全部有序内容放到有序集合中的方法？？？
            Object obj = entry.getValue();
            if (obj instanceof LinkedHashMap<?, ?>) {
                //是JSON子对象
                String[] next = new String[split.length - splitIndex];
                if (split.length - splitIndex >= 0)
                    System.arraycopy(split, splitIndex, next, 0, split.length - splitIndex);
                LinkedHashMap<String, Object> sonMap = compareAndSet((LinkedHashMap<String, Object>) obj, next);
                map.put(entry.getKey(), sonMap);
                int sonSize = sonMap.size();
                splitIndex += sonSize;
            } else {
                if (matched(split[splitIndex++], entry.getKey())) {
                    map.put(entry.getKey(), entry.getValue());
                } else break;
            }
        }
        return map;
    }

    private static LinkedHashMap<String, Object> compareAndSet(LinkedHashMap<String, Object> template, String[] split) {
        //通过和已知正确的JSON对应的LinkedHashMap对比，尽量将信息填充到新LinkedHashMap中
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        int splitIndex = 0;
        for (Map.Entry<String, Object> entry : template.entrySet()) {
            //linkedHashMap为什么不提供将全部有序内容放到有序集合中的方法？？？
            Object obj = entry.getValue();
            if (obj instanceof LinkedHashMap<?, ?>) {
                //是JSON子对象
                String[] next = new String[split.length - splitIndex];
                if (split.length - splitIndex >= 0)
                    System.arraycopy(split, splitIndex, next, 0, split.length - splitIndex);
                LinkedHashMap<String, Object> sonMap = compareAndSet((LinkedHashMap<String, Object>) obj, next);
                map.put(entry.getKey(), sonMap);
                int sonSize = sonMap.size();
                splitIndex += sonSize;
            } else {
                if (matched(split[splitIndex++], entry.getKey())) {
                    map.put(entry.getKey(), entry.getValue());
                } else break;
            }
        }
        return map;
    }

    public static boolean matched(String originJSON, String template) {
        int startIndex = originJSON.indexOf(template);
        int midIndex = originJSON.indexOf(":");
        return startIndex != -1 && midIndex != -1 && midIndex > startIndex;
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Object> data = FormHelper.getDataFromDB("test",
                "json",
                "\"name\":\"杨得志\",\"age\":12,\"address\":{\"name\":\"123\",\"num\":{\"big\":1,\"small\":100}},\"status\":\"student\"");
        System.out.println(data);
    }
}

