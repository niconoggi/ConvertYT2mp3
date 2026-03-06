package de.noggi.convertyt2mp3.ui;

import de.noggi.convertyt2mp3.LogWriter;
import de.noggi.convertyt2mp3.api.APIConsumer;
import de.noggi.convertyt2mp3.api.exception.APIConsumerException;
import de.noggi.convertyt2mp3.api.exception.TokensExceededException;
import de.noggi.convertyt2mp3.api.search.YouTubeSearchResource;
import de.noggi.convertyt2mp3.api.search.YouTubeSearchResponse;
import de.noggi.convertyt2mp3.audio.AudioException;
import de.noggi.convertyt2mp3.audio.AudioPlayer;
import de.noggi.convertyt2mp3.convert.ConversionDto;
import de.noggi.convertyt2mp3.convert.ConversionUtil;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UIFrame extends JFrame {

    private static final String TOPIC = "Topic";
    private static final Vector<String> COLUMN_VECTOR = new Vector<>();

    static {
        COLUMN_VECTOR.add("Titel");
        COLUMN_VECTOR.add("Kanal");
    }

    private JTextField searchInputField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JPopupMenu popupMenu;

    private YouTubeSearchResponse activeSearchResponse;

    public UIFrame() {
        setTitle("YouTube Convert 2 Mp 3");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        pack();
        setFocusable(true);
        setVisible(true);
    }

    private void initUI() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AudioPlayer.stop();
            }
        });

        final Container searchContainer = new Container();
        searchContainer.setLayout(new GridLayout(1, 3));
        final JLabel searchLabel = new JLabel("Eingabe Suche: ");
        searchInputField = new JTextField();
        searchInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    triggerSearch();
                }
            }
        });
        final JButton searchButton = new JButton("Suchen");
        searchButton.addActionListener(_ -> triggerSearch());

        searchContainer.add(searchLabel);
        searchContainer.add(searchInputField);
        searchContainer.add(searchButton);
        add(searchContainer, BorderLayout.NORTH);

        initTable();

        initPopupMenu();
    }

    private void initTable() {
        final Container tableContainer = new Container();
        tableContainer.setLayout(new GridLayout(1, 0));
        resultTable = new JTable();
        tableModel = new DefaultTableModel();
        resultTable.setModel(tableModel);
        resultTable.setAutoCreateColumnsFromModel(true);

        final TableColumn titleColumn = new TableColumn();
        titleColumn.setHeaderValue("Titel");
        titleColumn.setWidth(200);
        resultTable.addColumn(titleColumn);

        final TableColumn channelColumn = new TableColumn();
        channelColumn.setHeaderValue("Kanal");
        titleColumn.setWidth(100);
        resultTable.addColumn(channelColumn);

        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AudioPlayer.stop();
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popupMenu.show(resultTable, e.getX(), e.getY());
                } else {
                    popupMenu.setVisible(false);
                }
            }
        });

        tableContainer.add(new JScrollPane(resultTable));
        add(tableContainer);
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();

        final JMenuItem play = new JMenuItem("Abspielen");
        play.addActionListener(_ -> {
            if (triggerDownload(".wav")) {
                try {
                    AudioPlayer.playTemporaryWav();
                } catch (AudioException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                }
            }
        });

        final JMenuItem download = new JMenuItem("Herunterladen");
        download.addActionListener(_ -> {
            triggerDownload(".mp3");
        });

        popupMenu.add(play);
        popupMenu.add(download);
    }

    private boolean triggerDownload(final String format) {
        final YouTubeSearchResource selectedItem = activeSearchResponse.getItems().get(resultTable.getSelectedRow());
        if (selectedItem == null || selectedItem.getId() == null || selectedItem.getId().getVideoId() == null) {
            LogWriter.info(UIFrame.class, "No Data available at the row: " + resultTable.getSelectedRow());
            return false;
        }

        final ConversionDto result = ConversionUtil.convert(selectedItem.getId().getVideoId(), selectedItem.getSnippet().getTitle(), format);
        if (result.getErrorMsg() != null) {
            JOptionPane.showMessageDialog(null, result.getErrorMsg());
            return false;
        }

        return true;
    }

    private void triggerSearch() {
        if (searchInputField.getText() == null || searchInputField.getText().isEmpty()) {
            return;
        }

        try {
            activeSearchResponse = APIConsumer.search(searchInputField.getText());
            updateResults();
        } catch (APIConsumerException e) {
            if (e instanceof TokensExceededException) {
                JOptionPane.showConfirmDialog(null, "ACHTUNG:\nFür heute sind keine Anfragen mehr möglich,\n da das Kontingent überzogen wurde!");
            } else {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private void updateResults() {
        if (activeSearchResponse == null || activeSearchResponse.getItems() == null || activeSearchResponse.getItems().isEmpty()) {
            LogWriter.info(UIFrame.class, "Search " + searchInputField.getText() + " lent no results");
            JOptionPane.showConfirmDialog(null, "Scheinbar wurden keine Ergebnisse gefunden");
            return;
        }

        final Vector<Vector<String>> dataVector = new Vector<>();
        for (final YouTubeSearchResource item : orderResources()) {
            if (item.getSnippet() == null) {
                LogWriter.warn(UIFrame.class, "Attention: A null snippet sneaked it's way into the results");
                continue;
            }
            final Vector<String> itemVector = new Vector<>();
            itemVector.add(item.getSnippet().getTitle());
            itemVector.add(item.getSnippet().getChannelTitle());
            dataVector.add(itemVector);
        }

        tableModel.setDataVector(dataVector, COLUMN_VECTOR);
        resultTable.repaint();
    }

    /**
     * Tries to move results of interest to the top (i.e. results with *topic)
     */
    private List<YouTubeSearchResource> orderResources() {
        final List<YouTubeSearchResource> ordered = new ArrayList<>();
        for (final YouTubeSearchResource item : activeSearchResponse.getItems()) {
            if (item.getSnippet() == null) {
                continue;
            }

            if (item.getSnippet().getChannelTitle().contains(TOPIC)) {
                ordered.addFirst(item);
            } else {
                ordered.add(item);
            }
        }

        activeSearchResponse.setItems(ordered);
        return ordered;
    }
}
