import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

public class GUI extends JFrame {
    private JTable table;
    private JScrollPane tableScrollPane;
    private final JList<String> dataList;
    private final JList<String> modelList;
    private Controller controllerInUse;

    public GUI() {
        GUI _this = this;

        Dimension size = new Dimension(1280, 720);
        setSize(size);
        setPreferredSize(size);
        setTitle(Main.title);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Select model and data"));

        Dimension minL = new Dimension(120, 680);

        File fmodel = Main.modelDir;
        File fdata = Main.dataDir;

        File[] models = fmodel.listFiles(e -> e.getName().contains("Model"));

        if (models == null) {
            error(new RuntimeException("No models found in " + fmodel.getAbsolutePath()));
            dataList = null;
            modelList = null;
            return;
        }

        String[] ml = new String[models.length];

        for (int i = 0; i < models.length; i++) {
            ml[i] = models[i].getName();
        }

        modelList = new JList<>(ml);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(modelList);
        setSizeDim(listScrollPane, minL);

        File[] datas = fdata.listFiles();

        assert datas != null;

        String[] dl = new String[datas.length];

        for (int i = 0; i < datas.length; i++) {
            dl[i] = datas[i].getName();
        }

        dataList = new JList<>(dl);
        modelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane dataScrollPane = new JScrollPane(dataList);
        setSizeDim(dataScrollPane, minL);

        leftPanel.add(listScrollPane, BorderLayout.WEST);
        leftPanel.add(dataScrollPane, BorderLayout.EAST);

        JButton runModelButton = new JButton("Run model");
        leftPanel.add(runModelButton, BorderLayout.SOUTH);

        Dimension minLP = new Dimension(280, 720);
        setSizeDim(leftPanel, minLP);

        add(leftPanel, BorderLayout.WEST);

        table = new JTable(null);
        tableScrollPane = new JScrollPane(table);

        add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runScriptFileButton = new JButton("Run script from file");
        JButton createAdhocScriptButton = new JButton("Create and run ad hoc script");
        JButton changeDataFolderButton = new JButton("Change data folder");

        bottomPanel.add(changeDataFolderButton);
        bottomPanel.add(runScriptFileButton);
        bottomPanel.add(createAdhocScriptButton);

        add(bottomPanel, BorderLayout.SOUTH);

        runModelButton.addActionListener(_ -> {
            if (controllerInUse != null) {
                if (modelList.getSelectedValue() != null && dataList.getSelectedValue() != null) {
                    controllerInUse.runModel();
                    refreshData();
                }
            } else {
                noControllerErrorDiag();
            }
        });

        modelList.addListSelectionListener(_ -> listActionListener());
        dataList.addListSelectionListener(_ -> listActionListener());

        Dimension dSize = new Dimension(400,400);

        changeDataFolderButton.addActionListener(_ -> {
            Main.getDataDirectory();
            _this.dispose();
            new GUI();
        });

        runScriptFileButton.addActionListener(_ -> {
            if (controllerInUse != null) {
                JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));
                jfc.setSize(dSize);
                jfc.setPreferredSize(dSize);
                jfc.setDialogTitle(Main.title);
                jfc.setVisible(true);
                jfc.setMultiSelectionEnabled(false);

                JDialog dialog = new JDialog();
                dialog.setLocationRelativeTo(this);
                dialog.setSize(dSize);
                dialog.setPreferredSize(dSize);

                jfc.addActionListener(ef -> {
                    if (ef.getActionCommand().equals("ApproveSelection")) {
                        try {
                            dialog.dispose();
                            if (controllerInUse != null) {
                                Object result = controllerInUse.runScriptFromFile(jfc.getSelectedFile().getAbsolutePath());
                                if (result != null) {
                                    JOptionPane.showMessageDialog(this, "Return value of script: " + result);
                                }
                            }
                            refreshData();
                        } catch (FileNotFoundException ex) {
                            error(ex);
                        }
                    }
                });


                dialog.setLocationRelativeTo(this);
                jfc.showOpenDialog(dialog);
            } else {
                noControllerErrorDiag();
            }
        });

        createAdhocScriptButton.addActionListener(_ -> {
            if (controllerInUse != null) {
                JDialog jd = new JDialog(this);
                jd.setTitle(Main.title);
                jd.setLayout(new BorderLayout());
                JTextArea editor = new JTextArea();
                JPanel buttons = new JPanel();
                buttons.setLayout(new FlowLayout(FlowLayout.CENTER));
                JButton submit = new JButton("Run");
                buttons.add(submit);

                jd.add(editor, BorderLayout.CENTER);
                jd.add(buttons, BorderLayout.SOUTH);

                jd.setSize(dSize);
                jd.setPreferredSize(dSize);
                jd.setLocationRelativeTo(this);
                jd.pack();
                jd.setVisible(true);

                submit.addActionListener(_ -> {
                    jd.dispose();
                    if (editor.getText() != null && controllerInUse != null) {
                        Object result = controllerInUse.runScript(editor.getText());
                        if (result != null) {
                            JOptionPane.showMessageDialog(this, "Return value of script: " + result);
                        }
                        refreshData();
                    }
                });
            } else {
                noControllerErrorDiag();
            }
        });

        setVisible(true);
    }

    private void noControllerErrorDiag() {
        JOptionPane.showMessageDialog(this, "No model and data in use", "NoControllerException", JOptionPane.ERROR_MESSAGE);
    }

    private void setData(String dname, String mname) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, FileNotFoundException {
        File fdata = new File(Main.dataDir.getAbsolutePath() + "/" + dname);
        controllerInUse = new Controller(mname);
        controllerInUse.readDataFrom(fdata);
    }

    private void refreshData() {
        String[] tsv = controllerInUse.getResultsAsTsv().split("\n");

        Object[][] dt = new Object[tsv.length - 1][tsv[0].split(" ").length + 1];
        int i = 0;
        for (String line : tsv) {
            if (!line.startsWith("LATA")) {
                dt[i] = line.split(" ");
                i++;
            }
        }

        String[] columnNames = null;

        if (controllerInUse != null && controllerInUse.auxFields.get("LATA") != null) {
            columnNames = new String[controllerInUse.auxFields.get("LATA").length + 1];

            columnNames[0] = "Name";

            for (int c = 1; c < columnNames.length; c++) {
                String val = String.valueOf(controllerInUse.auxFields.get("LATA")[c - 1]);
                columnNames[c] = val.substring(0,val.indexOf('.'));
            }
        }

        DefaultTableModel model = new DefaultTableModel(dt, columnNames);

        remove(tableScrollPane);

        table = new JTable(model);
        tableScrollPane = new JScrollPane(table);

        add(tableScrollPane);

        pack();
        setVisible(true);
    }

    private void listActionListener() {
        if (modelList.getSelectedValue() != null && dataList.getSelectedValue() != null) {
            try {
                setData(dataList.getSelectedValue(), "models." + modelList.getSelectedValue().substring(0, modelList.getSelectedValue().indexOf('.')));
                refreshData();
            } catch (Exception ex) {
                error(ex);
            }
        }
    }

    private static void setSizeDim(JComponent jp, Dimension d) {
        jp.setMinimumSize(d);
        jp.setPreferredSize(d);
        jp.setSize(d);
    }

    public static void error(Exception e) {
        JOptionPane.showMessageDialog(null, e.getMessage(), e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
    }
}
