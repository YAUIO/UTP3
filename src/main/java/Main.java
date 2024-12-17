import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            GUI gui = new GUI();

            Controller test = new Controller("models.Model1");
            //test.readDataFrom("F:\\Users\\User\\Documents\\JavaProjects\\UTP3\\data\\data2.txt");
            test.readDataFrom("Z:\\S3\\UTP\\UTP3\\data\\data2.txt");
            test.runModel();
            System.out.println(test.getResultsAsTsv());
        } catch (FileNotFoundException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            System.out.println("Error: " + e.getMessage() + " " + e.getClass().getName());
        }
    }
}
