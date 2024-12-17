import models.Bind;
import models.Model1;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Controller {
    private final Object model;

    public Controller(String modelName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        model = Class.forName(modelName).getDeclaredConstructor().newInstance();
    }

    public void readDataFrom(String fname) throws FileNotFoundException {
        File file = new File(fname);

        BufferedReader data = new BufferedReader(new FileReader(file));

        HashMap<String, ArrayList<?>> dataMap = new HashMap<>();

        try {
            while (data.ready()) {
                ArrayList<String> rawLine = new ArrayList<>(Arrays.stream(data.readLine().split("\\s+")).toList());
                dataMap.put(rawLine.getFirst(), new ArrayList<>(rawLine.subList(1, rawLine.size())));
            }
        } catch (IOException e) {
            System.out.println("Reading failed in ReadDataFrom: " + e.getMessage());
        }

        Arrays.stream(model.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Bind.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        if (!field.getName().equals("LL")) {
                            if (dataMap.get(field.getName()) != null) {

                                double[] val = new double[dataMap.get("LATA").size() - 1];

                                for (int i = 0; i < dataMap.get(field.getName()).size(); i++) {
                                    val[i] = Double.parseDouble((String) dataMap.get(field.getName()).get(i));
                                }

                                field.set(model, val);
                            }
                        } else {
                            field.set(model, dataMap.get("LATA").size() - 1);
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("Access error while reading data: " + e.getMessage());
                    }
                });
    }

    public void runModel() {
        Arrays.stream(model.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("run")).forEach(method -> {
                    try {
                        method.invoke(model);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        System.out.println("Error while running model: " + e.getMessage() + " " + e.getClass().getName());
                    }
                });
    }

    public Object runScriptFromFile(String fname) throws FileNotFoundException {
        File file = new File(fname);
        BufferedReader data = new BufferedReader(new FileReader(file));
        StringBuilder code = new StringBuilder();

        try {
            while (data.ready()) {
                code.append(data.readLine());
            }
        } catch (IOException e) {
            System.out.println("Script reading failed " + e.getMessage());
            return null;
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");

        try {
            return groovy.eval(code.toString());
        } catch (ScriptException e) {
            System.out.println("Script evaluation failed: " + e.getMessage());
            return null;
        }
    }

    public Object runScript(String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");

        try {
            return groovy.eval(script);
        } catch (ScriptException e) {
            System.out.println("Script evaluation failed: " + e.getMessage());
            return null;
        }
    }

    String getResultsAsTsv() { //TODO proper formatting
        StringBuilder returnStr = new StringBuilder();
        Arrays.stream(this.model.getClass().getDeclaredFields())
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        if (f.get(this.model).getClass() == double[].class) {
                            returnStr.append(f.getName()).append(' ').append(Arrays.toString((double[]) f.get(this.model))).append('\n');
                        } else if (f.get(this.model).getClass() == Integer.class) {
                            returnStr.append(f.getName()).append(' ').append(f.get(this.model)).append('\n');
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("Access error while reading data: " + e.getMessage());
                    }
                });
        return returnStr.toString();
    }
}
