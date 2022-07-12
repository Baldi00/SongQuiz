package it.andrea;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        setLookAndFeel();
        createCacheFolder();

        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jFileChooser.setDialogTitle("Scegli la cartella in cui si trovano le canzoni");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnValue = jFileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION && jFileChooser.getSelectedFile().isDirectory()) {
            askForFilters(jFileChooser.getSelectedFile());
        }
    }

    private static void createCacheFolder() {
        File cacheFolder = new File("cache");
        if (!cacheFolder.exists()) {
            boolean created = cacheFolder.mkdir();
            if (!created) {
                Logger.getLogger(CustomWindow.class.getName()).log(Level.INFO, () -> "Cannot create cache folder");
            }
        }
    }

    private static void askForFilters(File folder) {
        new FiltersWindow(folder);
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
