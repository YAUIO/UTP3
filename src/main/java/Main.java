import javax.swing.*;
import java.io.File;

public class Main {
    static final String title = "UTP3s30174";
    static File modelDir;
    static File dataDir;

    private static File getDirectory(String dirName) {
        String userDir = System.getProperty("user.dir");

        JFileChooser jfc = new JFileChooser(userDir);
        JDialog jd = new JDialog();
        String fcTitle = "Choose " + dirName + " directory";
        jfc.setDialogTitle(fcTitle);
        jd.setTitle(fcTitle);

        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        File chosenDir = null;

        if (jfc.showOpenDialog(jd) == JFileChooser.APPROVE_OPTION) {
            chosenDir = new File(jfc.getSelectedFile().getAbsolutePath());
        }

        jd.dispose();

        return chosenDir;
    }

    public static void main(String[] args) {
        modelDir = getDirectory("models");
        dataDir = getDirectory("data");

        if (modelDir != null && dataDir != null && modelDir.exists() && dataDir.exists()) {
            new GUI();
        } else {
            GUI.error(new RuntimeException("Data and Model directories don't exist"));
        }
    }
}
