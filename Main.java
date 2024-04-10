import java.lang.reflect.InvocationTargetException;

public class Main {

    public <T> boolean parseJSON(String string,T t) throws Exception {
        String[] objects = string.split("\\{");
//        Student target = new Student();
        T target = ((T) t.getClass().getConstructor().newInstance());
        return true;

    }

//    public <T> Student digui(String s,int l,int r,T t,String field) throws IllegalAccessException {
//        if(field == null){
//            //表示是student的基本属性
//            String[] attributes = s.substring(l, r).split(",");
//            for (String str:attributes) {
//                String[] kv = str.split(":");
//                if (kv[1].startsWith("{")) {
//                    //TODO:l和 r不确定
//                    digui(s,l,r,t,kv[0]);
//                }
//                //setAttribute(t,kv[0],kv[1])
////                student.setAttribute(kv[0],kv[1]);
//            }
//        } else {
//            //表示是第一层的引用
////            Class<?> aClass = Class.forName(field);
////            Object reference = student.getReference(field);
//
//        }
//    }

}
