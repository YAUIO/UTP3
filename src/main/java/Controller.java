import models.Bind;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Controller {
    private final Object model;
    protected final HashMap<String, double[]> auxFields;

    public Controller(String modelName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        model = Class.forName(modelName).getDeclaredConstructor().newInstance();
        auxFields = new HashMap<>();
    }

    public void readDataFrom(String fname) throws FileNotFoundException {
        readDataFrom(new File(fname));
    }

    public void readDataFrom(File file) throws FileNotFoundException {
        BufferedReader data = new BufferedReader(new FileReader(file));

        HashMap<String, ArrayList<?>> dataMap = new HashMap<>();

        try {
            while (data.ready()) {
                ArrayList<String> rawLine = new ArrayList<>(Arrays.stream(data.readLine().split("\\s+")).toList());
                dataMap.put(rawLine.getFirst(), new ArrayList<>(rawLine.subList(1, rawLine.size())));
            }
        } catch (IOException e) {
            GUI.error(e);
        }

        if (dataMap.get("LATA") != null) {
            Arrays.stream(model.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Bind.class))
                    .forEach(field -> {
                        field.setAccessible(true);
                        try {
                            if (!field.getName().equals("LL")) {
                                if (dataMap.get(field.getName()) != null) {

                                    double[] val = new double[dataMap.get("LATA").size()];

                                    for (int i = 0; i < dataMap.get(field.getName()).size(); i++) {
                                        val[i] = Double.parseDouble((String) dataMap.get(field.getName()).get(i));
                                    }

                                    if (dataMap.get(field.getName()).size() < dataMap.get("LATA").size()) {
                                        for (int i = dataMap.get(field.getName()).size() - 1; i < val.length; i++) {
                                            val[i] = val[dataMap.get(field.getName()).size() - 1];
                                        }
                                    }

                                    field.set(model, val);
                                }
                            } else {
                                double[] val = new double[dataMap.get("LATA").size()];

                                for (int i = 0; i < val.length; i++) {
                                    val[i] = Double.parseDouble((String) dataMap.get("LATA").get(i));
                                }

                                auxFields.put("LATA", val);

                                field.set(model, dataMap.get("LATA").size());
                            }
                        } catch (IllegalAccessException e) {
                            GUI.error(e);
                        }
                    });
        } else {
            GUI.error(new RuntimeException("Incorrect data format. LATA field is missing."));
        }
    }

    public void runModel() {
        Arrays.stream(model.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals("run")).forEach(method -> {
                    try {
                        method.invoke(model);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        GUI.error(e);
                    }
                });
    }

    public Object runScriptFromFile(String fname) throws FileNotFoundException {
        File file = new File(fname);
        BufferedReader data = new BufferedReader(new FileReader(file));
        StringBuilder code = new StringBuilder();

        try {
            while (data.ready()) {
                code.append(data.readLine()).append('\n');
            }
        } catch (IOException e) {
            GUI.error(e);
            return null;
        }

        return runScript(code.toString());
    }

    public Object runScript(String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine groovy = manager.getEngineByName("groovy");

        HashMap<String, Field> fields = new HashMap<>();

        Arrays.stream(model.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Bind.class))
                .forEach(field -> {
                    fields.put(field.getName(), field);
                    try {
                        field.setAccessible(true);
                        groovy.put(field.getName(), field.get(model));
                    } catch (IllegalAccessException e) {
                        GUI.error(e);
                    }
                });

        for (String fieldName : auxFields.keySet()) {
            if (auxFields.get(fieldName) != null) {
                groovy.put(fieldName, auxFields.get(fieldName));
            }
        }

        Object result;

        try {
            result = groovy.eval(script);
        } catch (ScriptException e) {
            GUI.error(e);
            return null;
        }

        for (String name : groovy.getBindings(ScriptContext.ENGINE_SCOPE).keySet()) {
            if (groovy.get(name) != null && groovy.get(name).getClass() == double[].class) {
                if (fields.containsKey(name)) {
                    try {
                        fields.get(name).set(model, groovy.get(name));
                    } catch (IllegalAccessException e) {
                        GUI.error(e);
                    }
                } else {
                    auxFields.put(name, (double[]) groovy.get(name));
                }
            }
        }

        return result;
    }

    String getResultsAsTsv() {
        StringBuilder returnStr = new StringBuilder();
        Arrays.stream(this.model.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Bind.class))
                .forEach(f -> {
                    try {
                        f.setAccessible(true);
                        if (f.get(this.model) != null) {
                            if (f.get(this.model).getClass() == double[].class) {
                                returnStr.append(f.getName());
                                for (double d : (double[]) f.get(this.model)) {
                                    String number = String.valueOf(d);
                                    int coma = number.indexOf('.');
                                    if (number.length() - coma > 2) {
                                        returnStr.append(' ').append(number, 0, coma + 3);
                                    } else {
                                        returnStr.append(' ').append(number);

                                    }
                                }
                                returnStr.append('\n');
                            }
                        }
                    } catch (IllegalAccessException e) {
                        GUI.error(e);
                    }
                });

        for (String name : auxFields.keySet()) {
            if (auxFields.get(name) != null) {
                returnStr.append(name);
                for (double d : auxFields.get(name)) {
                    String number = String.valueOf(d);
                    int coma = number.indexOf('.');
                    if (number.length() - coma > 2) {
                        returnStr.append(' ').append(number, 0, coma + 3);
                    } else {
                        returnStr.append(' ').append(number);

                    }
                }
                returnStr.append('\n');
            }
        }

        return returnStr.toString();
    }
}
