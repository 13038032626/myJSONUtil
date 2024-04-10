import java.lang.reflect.Field;

public class Student {

    public String name;

    public Integer age;

    public Address address;

    public void setAttribute(String k, String v) {
        Field[] declaredFields = this.getClass().getDeclaredFields();
        for (Field f : declaredFields) {
            if (k.equals(f.getName())) {
                f.setAccessible(true);
                try {
                    if (f.getType() == String.class) {
                        f.set(this, v);

                    } else if (f.getType() == Integer.class) {
                        f.set(this, Integer.valueOf(v));
                    } else {
                        //其他类型
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public Object getReference(String path) throws IllegalAccessException {
        String[] paths = path.split("\\.");
        if (paths.length == 0) {
            //单层
            paths = new String[]{path};
        }
        Class target = this.getClass();
        Object tempObj = this;
        for (int i = 0; i < paths.length; i++) {
            Field[] fields = target.getDeclaredFields();
            for (Field f : fields) {
                if (paths[i].equals(f.getName())) {
                    tempObj = f.get(tempObj);
                    target = f.getType();
                }
            }
        }
        return tempObj;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address=" + address +
                '}';
    }
}
