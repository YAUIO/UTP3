import javax.swing.*;
import java.io.File;

public class Main {
    static final String title = "UTP3s30174";
    static File modelDir = new File("src/main/java/models/");
    static File dataDir;

    private static File getDirectory() {
        String userDir = System.getProperty("user.dir");

        JFileChooser jfc = new JFileChooser(userDir);
        JDialog jd = new JDialog();
        String fcTitle = "Choose data directory";
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
        dataDir = getDirectory();

        if (dataDir == null || !dataDir.exists()) {
            dataDir = new File("data/");
            GUI.error(new RuntimeException("Data directory doesn't exist, using default..."));
        }

        new GUI();
    }
}
