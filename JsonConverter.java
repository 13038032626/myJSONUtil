import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class JsonConverter {
    public static <T> T convertJsonToEntity(String json, Class<T> clazz, int start, int end) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        T entity = clazz.getDeclaredConstructor().newInstance();

        try {
            if(json.charAt(start) == '{' && json.charAt(end) == '}'){
                start++;
                end--;
                //为了防止原本属于子对象的{或}被异常清理，只能这样；但此时无法处理异常缺失{或者}
            }
            String targetJson = json.substring(start,end);
            targetJson = addGapToEscapeSplitBug(targetJson);
            String[] keyValuePairs = targetJson.split("[,{}]");
            Map<String, Object> map = new HashMap<>();
            int sonIndex = 0;//用于表示{}是否全部抵消
            int sonStart = start;
            int sonEnd = sonStart;
            String sonClass = null;
            Iterator<String> iterator = Arrays.stream(keyValuePairs).iterator();
            boolean inner = false;
            while (iterator.hasNext()) {
                String next = iterator.next();

                if (inner) {
                    if (next.endsWith(":")) {
                        sonIndex++;
                        sonEnd += next.length() + 1 + 1; // 因为{
                        continue;
                    }
                    if (next.length() == 0 || " ".equals(next)) {//1. 出现 }  2. 兼容split异常情况
                        sonIndex--;
//                        sonEnd += 1;//增加 } 的长度 1
                        if (sonIndex == 0) {  //出来了
                            sonEnd--; //由于子对象入口处+2表示将末尾的}算进去了，此时--表示最后一个k-v没有,之前多算了一个
                            inner = false;
                            //对刚刚完成的部分进行子JSON查询
                            Class<?> son = Class.forName(classConverter(sonClass));
                            Object o = convertJsonToEntity(json, son, sonStart, sonEnd);
                            map.put(sonClass.substring(1, sonClass.length() - 2), o);
                            //用于标记子JSON的两个指针对齐
                            sonStart = sonEnd;
                        }
                        continue;
                    }
                    sonEnd += next.length() + 1;
                    continue;
                }
                if (next.endsWith(":")) {
//                    if (sonIndex == 0) {
                    sonStart += next.length() + 1 ;//由于有{ +1
                    sonClass = next;
                    sonEnd = sonStart;
                    inner = true;
//                    }
                    sonIndex++;
                    continue;
                }
                sonStart += next.length() + 1;//加一因为末尾有逗号
                String[] entry = next.split(":");
                map.put(removeQuotationMarkIfExists(entry[0]), removeQuotationMarkIfExists(entry[1]));
            }


            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Object value = map.get(fieldName);
                    if (value != null) {
                        if (field.getType() == int.class) {
                            field.setInt(entity, Integer.parseInt((String) value));
                        } else {
                            field.set(entity, value);
                        }
                    }
                }
            }

            return entity;
        } catch (Exception e) {
            System.out.println("异常");
            e.printStackTrace();
            return entity;
        }
    }

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String json = "{\"name\":\"杨得志\",\"age\":12,\"address\":{\"name\":\"123\",\"num\":{\"big\":1,\"small\":100}},\"status\":\"student\"}";
        Student student = convertJsonToEntity(json, Student.class, 0, json.length() - 1);
        System.out.println(student);
//        System.out.println(student.getName());
    }

    public static String classConverter(String origin) {
        StringBuilder sb = new StringBuilder();
        String start = origin.substring(1, 2).toUpperCase();
        String then = origin.substring(2, origin.length() - 2);
        return sb.append(start).append(then).toString();
    }
    public static String removeQuotationMarkIfExists(String origin){
        if(origin.startsWith("\"") && origin.endsWith("\"")){
            return origin.substring(1,origin.length()-1);
        }else {
            return origin;
        }
    }
    public static String addGapToEscapeSplitBug(String origin){
        if(origin.endsWith("}") && !origin.startsWith("{")){
            return origin + " ";
        }
        return origin;
    }

    static class Student {
        private int id;
        private String name;
        private int age;
        private Address address;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", address=" + address +
                    '}';
        }
    }
}
