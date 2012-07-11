package org.phw.hbaser.ui;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class CheckList {
    public static void createCheckList(final JList listTables) {
        listTables.setCellRenderer(new CheckListRenderer());
        listTables.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listTables.setBorder(new EmptyBorder(0, 4, 0, 0));
        listTables.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listTables.locationToIndex(e.getPoint());
                CheckableItem item = (CheckableItem) listTables.getModel().getElementAt(index);
                item.setSelected(!item.isSelected());
                Rectangle rect = listTables.getCellBounds(index, index);
                listTables.repaint(rect);
            }
        });
    }

}
