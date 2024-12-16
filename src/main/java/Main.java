import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

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
        Scanner in = new Scanner(System.in);
        try {
            Object result = groovy.eval(in.nextLine());
            System.out.println(result);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }*/

        try {
            Controller test = new Controller("models.Model1");
            test.readDataFrom("F:\\Users\\User\\Documents\\JavaProjects\\UTP3\\data\\data2.txt");
            test.runModel();
            System.out.println(test.getResultsAsTsv());
        } catch (FileNotFoundException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.out.println("Error: " + e.getMessage() + " " + e.getClass().getName());
        }

    }
}
