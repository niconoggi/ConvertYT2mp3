package de.noggi.convertyt2mp3.ui;

import de.noggi.convertyt2mp3.api.search.SearchResourceSnippet;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class SearchResultTableModel implements TableModel {

    private final List<SearchResourceSnippet> rowData = new ArrayList<>();

    @Override
    public int getRowCount() {
        return 25;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return switch(columnIndex) {
            case 0 -> "Titel";
            default -> "Kanal";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowData.size() <= rowIndex) {
            return null;
        }
        final SearchResourceSnippet snippet = rowData.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> snippet.getTitle();
            default -> snippet.getChannelTitle();
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowData.size() <= rowIndex) {
            return;
        }
        final SearchResourceSnippet snippet = rowData.get(rowIndex);
        switch (columnIndex) {
            case 0 -> snippet.setTitle((String) aValue);
            default -> snippet.setChannelTitle((String) aValue);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public void updateModel(final List<SearchResourceSnippet> snippets) {
        rowData.clear();
        rowData.addAll(snippets);
    }
}
