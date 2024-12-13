import models.Bind;
import models.Model1;

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

                                double[] val = new double[dataMap.get("LATA").size()-1];

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
}
