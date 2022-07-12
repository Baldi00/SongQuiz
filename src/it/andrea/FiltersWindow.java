package it.andrea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FiltersWindow extends JFrame implements ActionListener {

    private static final String NO_FILTERS = "No filtri";
    private static final String CONTAINS = "Contiene";
    private static final String NOT_CONTAINS = "Non contiene";

    private final JTextField filter1;
    private final JTextField filter2;
    private final JTextField filter3;

    private final File folder;
    private final JRadioButton contains1;
    private final JRadioButton notContains1;
    private final JRadioButton contains2;
    private final JRadioButton notContains2;
    private final JRadioButton contains3;
    private final JRadioButton notContains3;

    public FiltersWindow(File folder) {
        this.folder = folder;
        setTitle("Scegli se applicare dei filtri");
        setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel();
        JPanel artistPanel = new JPanel();
        JPanel albumPanel = new JPanel();
        JPanel mainPanel = new JPanel(new GridLayout(1, 3));

        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        artistPanel.setLayout(new BoxLayout(artistPanel, BoxLayout.Y_AXIS));
        albumPanel.setLayout(new BoxLayout(albumPanel, BoxLayout.Y_AXIS));

        JRadioButton noFilters1 = new JRadioButton(NO_FILTERS);
        contains1 = new JRadioButton(CONTAINS);
        notContains1 = new JRadioButton(NOT_CONTAINS);
        JRadioButton noFilters2 = new JRadioButton(NO_FILTERS);
        contains2 = new JRadioButton(CONTAINS);
        notContains2 = new JRadioButton(NOT_CONTAINS);
        JRadioButton noFilters3 = new JRadioButton(NO_FILTERS);
        contains3 = new JRadioButton(CONTAINS);
        notContains3 = new JRadioButton(NOT_CONTAINS);

        noFilters1.setName("noFilters1");
        contains1.setName("contains1");
        notContains1.setName("notContains1");
        noFilters2.setName("noFilters2");
        contains2.setName("contains2");
        notContains2.setName("notContains2");
        noFilters3.setName("noFilters3");
        contains3.setName("contains3");
        notContains3.setName("notContains3");

        ButtonGroup titleButtonGroup = new ButtonGroup();
        titleButtonGroup.add(noFilters1);
        titleButtonGroup.add(contains1);
        titleButtonGroup.add(notContains1);

        ButtonGroup artistButtonGroup = new ButtonGroup();
        artistButtonGroup.add(noFilters2);
        artistButtonGroup.add(contains2);
        artistButtonGroup.add(notContains2);

        ButtonGroup albumButtonGroup = new ButtonGroup();
        albumButtonGroup.add(noFilters3);
        albumButtonGroup.add(contains3);
        albumButtonGroup.add(notContains3);

        filter1 = new JTextField();
        filter2 = new JTextField();
        filter3 = new JTextField();
        filter1.setEnabled(false);
        filter2.setEnabled(false);
        filter3.setEnabled(false);

        JLabel titleLabel = new JLabel("Titolo");
        JLabel artistLabel = new JLabel("Artista");
        JLabel albumLabel = new JLabel("Album");

        JButton confirmButton = new JButton("Conferma");

        titlePanel.add(titleLabel);
        titlePanel.add(noFilters1);
        titlePanel.add(contains1);
        titlePanel.add(notContains1);
        titlePanel.add(filter1);

        artistPanel.add(artistLabel);
        artistPanel.add(noFilters2);
        artistPanel.add(contains2);
        artistPanel.add(notContains2);
        artistPanel.add(filter2);

        albumPanel.add(albumLabel);
        albumPanel.add(noFilters3);
        albumPanel.add(contains3);
        albumPanel.add(notContains3);
        albumPanel.add(filter3);

        mainPanel.add(titlePanel);
        mainPanel.add(artistPanel);
        mainPanel.add(albumPanel);

        noFilters1.setSelected(true);
        noFilters2.setSelected(true);
        noFilters3.setSelected(true);

        noFilters1.addActionListener(this);
        contains1.addActionListener(this);
        notContains1.addActionListener(this);
        noFilters2.addActionListener(this);
        contains2.addActionListener(this);
        notContains2.addActionListener(this);
        noFilters3.addActionListener(this);
        contains3.addActionListener(this);
        notContains3.addActionListener(this);

        confirmButton.addActionListener(this);

        add(mainPanel);
        add(confirmButton, BorderLayout.SOUTH);

        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JRadioButton jRadioButton) {
            switch (jRadioButton.getName()) {
                case "noFilters1" -> filter1.setEnabled(false);
                case "contains1", "notContains1" -> filter1.setEnabled(true);
                case "noFilters2" -> filter2.setEnabled(false);
                case "contains2", "notContains2" -> filter2.setEnabled(true);
                case "noFilters3" -> filter3.setEnabled(false);
                case "contains3", "notContains3" -> filter3.setEnabled(true);
                default -> throw new IllegalArgumentException("Illegal radio button pressed: " + jRadioButton.getName());
            }
        } else if (e.getSource() instanceof JButton) {
            int f1 = 0;
            int f2 = 0;
            int f3 = 0;
            String fs1 = filter1.getText();
            String fs2 = filter2.getText();
            String fs3 = filter3.getText();

            if (contains1.isSelected()) f1 = 1;
            else if (notContains1.isSelected()) f1 = 2;

            if (contains2.isSelected()) f2 = 1;
            else if (notContains2.isSelected()) f2 = 2;

            if (contains3.isSelected()) f3 = 1;
            else if (notContains3.isSelected()) f3 = 2;

            new CustomWindow(folder, f1, f2, f3, fs1, fs2, fs3);
            dispose();
        }
    }
}
