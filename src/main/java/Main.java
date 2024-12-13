import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {
       /* try {
            Object w = Class.forName("java.util.ArrayList").getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.out.println("Error controller");
        }
        */
       /*

        int llLoadedFromAFile = 5;

        Model1 m = new Model1();

        Arrays.stream(m.getClass().getDeclaredFields()) // = Class.getForName("models.Model1");
                .filter(field -> field.isAnnotationPresent(Bind.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        if (field.getName().equals("LL")) {
                            field.set(m, llLoadedFromAFile);
                        } else {
                            field.set(m, new double[llLoadedFromAFile]);
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("LOL");
                    }
                });

        m.run();*/

        /*ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");
        String code = "def x = \"hello\" \n" + "return x * 5";
        groovy.put("x",5);
        String c = "return x * 5";
        try {
            Object result = groovy.eval(code);
            System.out.println(result);
            result = groovy.eval(c);
            System.out.println(result);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }*/

        try {
            Controller test = new Controller("models.Model1");
            test.readDataFrom("Z:\\S3\\UTP\\pro3\\data\\data2.txt");
            test.runModel();
        } catch (FileNotFoundException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.out.println("Error: " + e.getMessage() + " " + e.getClass().getName());
        }

    }
}
