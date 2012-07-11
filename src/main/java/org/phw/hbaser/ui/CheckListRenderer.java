package org.phw.hbaser.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer {
    private static final long serialVersionUID = 9165603195751500300L;

    public CheckListRenderer() {
        setBackground(UIManager.getColor("List.textBackground"));
        setForeground(UIManager.getColor("List.textForeground"));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus) {
        CheckableItem ci = (CheckableItem) value;
        setEnabled(list.isEnabled());
        setSelected(ci.isSelected());
        setFont(list.getFont());
        setText(value.toString());
        // based on the index you set the color.  This produces the every other effect.
        setBackground(index % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
        return this;
    }
}