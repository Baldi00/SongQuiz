package it.andrea;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javazoom.jl.player.Player;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomWindow extends JFrame implements ActionListener, ListSelectionListener {

    private final File folder;
    private final transient Map<File, MyMp3Tag> songListFiles;
    private final int filter1;
    private final int filter2;
    private final int filter3;
    private final String filterString1;
    private final String filterString2;
    private final String filterString3;
    private final Map<String, String> cached;
    private JLabel status;
    private DefaultListModel<String> songListModelByTitle;
    private DefaultListModel<String> songListModelByArtist;
    private JList<String> songListByTitle;
    private JList<String> songListByArtist;
    private JSpinner sampleDuration;
    private File currentSong;
    private int currentSongSize;

    private Random rand;

    public CustomWindow(File folder, int f1, int f2, int f3, String fs1, String fs2, String fs3) {
        try {
            rand = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        filter1 = f1;
        filter2 = f2;
        filter3 = f3;
        filterString1 = fs1;
        filterString2 = fs2;
        filterString3 = fs3;
        this.folder = folder;
        songListFiles = new HashMap<>();
        cached = new HashMap<>();
        setCache();
        setupGraphic();
    }

    private void setCache() {
        File cacheFolder = new File("cache");
        for (File f : Objects.requireNonNull(cacheFolder.listFiles())) {
            cached.put(f.getName().substring(0, f.getName().indexOf(';')), f.getName().substring(f.getName().indexOf(';') + 1));
        }
    }

    private void setupGraphic() {
        setLayout(new GridLayout(1, 3));
        JPanel mainPanel = new JPanel(new GridLayout(9, 1));
        JScrollPane scrollPaneByTitle = new JScrollPane();
        JScrollPane scrollPaneByArtist = new JScrollPane();
        songListModelByTitle = new DefaultListModel<>();
        songListModelByArtist = new DefaultListModel<>();
        addSongList(folder);
        songListByTitle = new JList<>(songListModelByTitle);
        songListByArtist = new JList<>(songListModelByArtist);
        JPanel songListPanelByTitle = new JPanel();
        JPanel songListPanelByArtist = new JPanel();
        songListByTitle.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        sampleDuration = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));

        status = new JLabel("INDOVINA LA CANZONE");
        JLabel secondsLabel = new JLabel("Secondi d'esempio");
        JButton confirmSong = new JButton("Conferma");
        JButton nextSong = new JButton("Prossima");
        JButton beginningSong = new JButton("Dall'inizio");
        JButton randomPointSong = new JButton("Punto casuale");
        JButton revealButton = new JButton("Rivela");
        JButton openSongButton = new JButton("Apri la canzone");

        confirmSong.setName("confirm");
        nextSong.setName("next");
        beginningSong.setName("beginning");
        randomPointSong.setName("random");
        revealButton.setName("reveal");
        openSongButton.setName("open");
        songListByTitle.setName("byTitle");
        songListByArtist.setName("byArtist");

        confirmSong.addActionListener(this);
        nextSong.addActionListener(this);
        beginningSong.addActionListener(this);
        randomPointSong.addActionListener(this);
        revealButton.addActionListener(this);
        openSongButton.addActionListener(this);

        songListByTitle.addListSelectionListener(this);
        songListByArtist.addListSelectionListener(this);

        songListByTitle.setFont(songListByTitle.getFont().deriveFont(18f));
        songListByArtist.setFont(songListByArtist.getFont().deriveFont(18f));
        sampleDuration.setFont(sampleDuration.getFont().deriveFont(25f));
        status.setFont(status.getFont().deriveFont(40f));
        secondsLabel.setFont(status.getFont().deriveFont(25f));
        confirmSong.setFont(confirmSong.getFont().deriveFont(18f));
        nextSong.setFont(nextSong.getFont().deriveFont(18f));
        beginningSong.setFont(beginningSong.getFont().deriveFont(18f));
        randomPointSong.setFont(randomPointSong.getFont().deriveFont(18f));
        revealButton.setFont(revealButton.getFont().deriveFont(18f));
        openSongButton.setFont(openSongButton.getFont().deriveFont(18f));

        status.setHorizontalAlignment(SwingConstants.CENTER);
        secondsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        secondsLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        confirmSong.setForeground(new Color(0, 154, 41));
        revealButton.setForeground(Color.RED);
        openSongButton.setForeground(Color.RED);
        beginningSong.setForeground(new Color(0, 69, 218));
        randomPointSong.setForeground(new Color(0, 69, 218));
        nextSong.setForeground(new Color(0, 69, 218));

        songListPanelByTitle.add(songListByTitle);
        scrollPaneByTitle.setViewportView(songListPanelByTitle);
        scrollPaneByTitle.getVerticalScrollBar().setUnitIncrement(16);
        songListPanelByArtist.add(songListByArtist);
        scrollPaneByArtist.setViewportView(songListPanelByArtist);
        scrollPaneByArtist.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(status);
        mainPanel.add(secondsLabel);
        mainPanel.add(sampleDuration);
        mainPanel.add(beginningSong);
        mainPanel.add(randomPointSong);
        mainPanel.add(confirmSong);
        mainPanel.add(revealButton);
        mainPanel.add(openSongButton);
        mainPanel.add(nextSong);
        add(scrollPaneByTitle);
        add(scrollPaneByArtist);
        add(mainPanel);

        selectRandomSong();

        pack();
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void selectRandomSong() {
        int max = songListFiles.size();
        if (max == 0) {
            System.exit(0);
        }

        int random = rand.nextInt(max);

        currentSong = (File) songListFiles.keySet().toArray()[random];

        try {
            max = (int) Files.size(currentSong.toPath()) * 3 / 4;
            int min = (int) Files.size(currentSong.toPath()) / 4;
            currentSongSize = rand.nextInt(max - min) + min;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSongList(File folder) {
        List<String> byTitle = new ArrayList<>();
        List<String> byArtist = new ArrayList<>();
        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (f.isFile() && f.getName().endsWith(".mp3")) {
                insertFileInList(byTitle, byArtist, f);
            } else if (f.isDirectory()) {
                addSongList(f);
            }
        }

        Collections.sort(byTitle);
        Collections.sort(byArtist);

        for (String s : byTitle) {
            songListModelByTitle.addElement(s);
        }
        for (String s : byArtist) {
            songListModelByArtist.addElement(s);
        }
    }

    private void insertFileInList(List<String> byTitle, List<String> byArtist, File f) {
        if (cached.containsKey(f.getName())) {
            String[] splitted = cached.get(f.getName()).split(";");
            MyMp3Tag tag = new MyMp3Tag(splitted[0], splitted[1], splitted[2]);
            checkFiltersAndInsert(byTitle, byArtist, f, tag);
        } else {
            try {
                Mp3File mp3file = new Mp3File(f);
                if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2 = mp3file.getId3v2Tag();
                    MyMp3Tag tag = new MyMp3Tag(id3v2.getTitle(), id3v2.getArtist(), id3v2.getAlbum());
                    checkFiltersAndInsert(byTitle, byArtist, f, tag);
                    createCacheFile(f, id3v2);
                }
            } catch (UnsupportedTagException | IOException | InvalidDataException e) {
                e.printStackTrace();
            }
        }
    }

    private void createCacheFile(File f, ID3v2 id3v2) throws IOException {
        try {
            if (!new File("cache\\" + f.getName() + ";" + id3v2.getTitle() + ";" + id3v2.getArtist() + ";" + id3v2.getAlbum()).createNewFile()) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            Logger.getLogger(CustomWindow.class.getName()).log(Level.INFO, () -> "Couldn't create cache file for " + f.getName());
        }
    }

    private void checkFiltersAndInsert(List<String> byTitle, List<String> byArtist, File f, MyMp3Tag tag) {
        checkTitleFilter(byTitle, byArtist, f, tag);
    }

    private void checkTitleFilter(List<String> byTitle, List<String> byArtist, File f, MyMp3Tag tag) {
        switch (filter1) {
            case 0:
                checkArtistFilter(byTitle, byArtist, f, tag);
                break;
            case 1:
                if (tag.title().contains(filterString1)) checkArtistFilter(byTitle, byArtist, f, tag);
                break;
            case 2:
                if (!tag.title().contains(filterString1)) checkArtistFilter(byTitle, byArtist, f, tag);
                break;
            default:
                throw new IllegalArgumentException("Illegal title filter: " + filter1);
        }
    }

    private void checkArtistFilter(List<String> byTitle, List<String> byArtist, File f, MyMp3Tag tag) {
        switch (filter2) {
            case 0:
                checkAlbumFilter(byTitle, byArtist, f, tag);
                break;
            case 1:
                if (tag.artist().contains(filterString2)) checkAlbumFilter(byTitle, byArtist, f, tag);
                break;
            case 2:
                if (!tag.artist().contains(filterString2)) checkAlbumFilter(byTitle, byArtist, f, tag);
                break;
            default:
                throw new IllegalArgumentException("Illegal artist filter: " + filter2);
        }
    }

    private void checkAlbumFilter(List<String> byTitle, List<String> byArtist, File f, MyMp3Tag tag) {
        switch (filter3) {
            case 0:
                insertSong(byTitle, byArtist, f, tag);
                break;
            case 1:
                if (tag.album().contains(filterString3)) insertSong(byTitle, byArtist, f, tag);
                break;
            case 2:
                if (!tag.album().contains(filterString3)) insertSong(byTitle, byArtist, f, tag);
                break;
            default:
                throw new IllegalArgumentException("Illegal album filter: " + filter3);
        }
    }

    private void insertSong(List<String> byTitle, List<String> byArtist, File f, MyMp3Tag tag) {
        byTitle.add(tag.title() + " - " + tag.artist());
        byArtist.add(tag.artist() + " - " + tag.title());
        songListFiles.put(f, tag);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JButton jButton) {
            switch (jButton.getName()) {
                case "confirm" -> userConfirmed();
                case "next" -> goToNextSong();
                case "beginning" -> playSongFromBeginning();
                case "random" -> playSongFromRandomPoint();
                case "reveal" -> revealSong();
                case "open" -> openSong();
                default -> throw new IllegalArgumentException("Illegal button pressed: " + jButton.getName());
            }
        }
    }

    private void userConfirmed() {
        MyMp3Tag currentSongTag = songListFiles.get(currentSong);
        if ((!songListByTitle.isSelectionEmpty() && songListByTitle.getSelectedValue().equals(currentSongTag.getTitle() + " - " + currentSongTag.getArtist()))
                || (!songListByArtist.isSelectionEmpty() && songListByArtist.getSelectedValue().equals(currentSongTag.getArtist() + " - " + currentSongTag.getTitle()))) {
            status.setText("GIUSTO!");
            status.setForeground(new Color(0, 154, 41));
        } else {
            status.setText("SBAGLIATO...");
            status.setForeground(Color.RED);
        }
    }

    private void goToNextSong() {
        selectRandomSong();
        status.setText("INDOVINA LA CANZONE");
        status.setForeground(Color.BLACK);
    }

    private void playSongFromBeginning() {
        try {
            FileInputStream fis = new FileInputStream(currentSong.getAbsolutePath());
            Player playMP3 = new Player(fis);
            playMP3.play((Integer) sampleDuration.getValue() * 30);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void playSongFromRandomPoint() {
        try {
            FileInputStream fis = new FileInputStream(currentSong.getAbsolutePath());
            Player playMP3 = new Player(fis);
            fis.readNBytes(currentSongSize);
            playMP3.play((Integer) sampleDuration.getValue() * 30);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void revealSong() {
        status.setText(currentSong.getName());
        status.setForeground(Color.BLACK);
    }

    private void openSong() {
        try {
            Desktop.getDesktop().open(currentSong);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() instanceof JList) {
            JComponent jList = (JComponent) e.getSource();
            String name = jList.getName();
            if ("byTitle".equals(name)) {
                songListByArtist.clearSelection();
            } else if ("byArtist".equals(name)) {
                songListByTitle.clearSelection();
            }
        }
    }
}
