import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Locale;

public class test {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, JsonProcessingException, ClassNotFoundException {
//        Student student =new Student();
//        student.name = "杨得志";
//        student.age = 12;
//        Address address = new Address();
//        address.name = "123";
//        student.address = address;
//
//
////        Field address1 = Student.class.getField("address");
////        Address o = (Address)address1.get(student);
////        System.out.println("o.name = " + o.name);
//        ObjectMapper mapper = new ObjectMapper();
////        System.out.println(mapper.writeValueAsString(student));
//        String str = "{\"name\":\"杨得志\",\"age\":12,\"address\":{\"name\":\"123\",\"num\":{\"big\":1,\"small\":100}},\"status\":\"student\"}";
//        Student s = mapper.readValue(str, student.getClass())

//        System.out.println(s);
//        System.out.println(str.substring(33,75));
//        System.out.println(Class.forName("Address"));
//        String[] keyValuePairs = str.split("[,{}]");
//        System.out.println(Arrays.toString(keyValuePairs));
//        System.out.println(classConverter("address:"));
        String[] split = "}}, ".split("[{},]");
        //split逆天方法，完全按照regex分后残留空白字符串时，会从后往前清理空白字符串
        System.out.println(Arrays.toString(split));
    }
    public static String classConverter(String origin){
        StringBuilder sb = new StringBuilder();
        String start = origin.substring(0, 1).toUpperCase(Locale.ROOT);
        String then = origin.substring(1, origin.length() - 1);
        return sb.append(start).append(then).toString();
    }
}
//{"name":"杨得志","age":12,"address":{"name":"123","num":null}}