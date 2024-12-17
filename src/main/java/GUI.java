import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame {
    public GUI() {
        Dimension size = new Dimension(1280, 720);
        setSize(size);
        setPreferredSize(size);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Select model and data"));

        File fmodel = new File("src/main/java/models/");
        File fdata = new File("data/");

        File[] models = fmodel.listFiles();

        String[] ml = new String[models.length];

        for (int i = 0; i < models.length; i++) {
            ml[i] = models[i].getName();
            i++;
        }

        JList<String> modelList = new JList<>(ml);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(modelList);

        File[] datas = fdata.listFiles();

        String[] dl = new String[models.length];

        for (int i = 0; i < models.length; i++) {
            dl[i] = datas[i].getName();
            i++;
        }
        JList<String> dataList = new JList<>(dl);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane dataScrollPane = new JScrollPane(dataList);

        leftPanel.add(listScrollPane, BorderLayout.WEST);
        leftPanel.add(dataScrollPane, BorderLayout.EAST);

        // Create a "Run model" button
        JButton runModelButton = new JButton("Run model");
        leftPanel.add(runModelButton, BorderLayout.SOUTH);

        // Add the left panel to the frame
        add(leftPanel, BorderLayout.WEST);

        // Create the table data
        String[] columnNames = {"", "2015", "2016", "2017", "2018", "2019"};
        Object[][] dataA = {
                {"twKI", "1.03", "1.04", "1.03", "1.04", "1.03"},
                {"twKS", "1.04", "1.12", "1.04", "1.14", "1.02"},
                {"twINW", "1.03", "1.13", "1.13", "1.12", "1.14"},
                {"twEKS", "1.12", "1.12", "1.11", "1.12", "1.12"},
                {"twIMP", "1.03", "1.11", "1.12", "1.12", "1.14"},
                {"KI", "1023752.2", "1112664.1", "1408880.98", "1301254.01", "1203268.1"},
                {"KS", "315397.5", "322096.4", "342134.4", "353403.4", "365002.3"},
                {"INW", "846184.3", "916883.4", "941998.3", "960374.3", "985126.4"},
                {"EKS", "881374.4", "917342.1", "954664.2", "974682.2", "991237.2"},
                {"IMP", "784324.4", "894507.3", "1019314.1", "1082631.2", "1224713.1"},
                {"PKB", "1744237.4", "1795443.8", "1801635.6", "1826784.7", "2267128.2"}
        };

        // Create table
        DefaultTableModel model = new DefaultTableModel(dataA, columnNames);
        JTable table = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Add table to the center of the frame
        add(tableScrollPane, BorderLayout.CENTER);

        // Create bottom panel for additional buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runScriptFileButton = new JButton("Run script from file");
        JButton createAdhocScriptButton = new JButton("Create and run ad hoc script");

        bottomPanel.add(runScriptFileButton);
        bottomPanel.add(createAdhocScriptButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Add button actions (simple popups for demonstration)
        runModelButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Run Model clicked!"));
        runScriptFileButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Run Script from File clicked!"));
        createAdhocScriptButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Create and Run Ad Hoc Script clicked!"));

        setVisible(true);
    }
}
