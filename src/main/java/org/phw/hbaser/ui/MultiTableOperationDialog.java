package org.phw.hbaser.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import org.phw.hbaser.util.HBaserUtils;
import org.phw.hbaser.util.HTableDdls;
import org.phw.hbaser.util.HTablesMultiExport;

public class MultiTableOperationDialog extends JDialog {
    private static final long serialVersionUID = -8788569498105312058L;
    private final JPanel contentPanel = new JPanel();
    private DefaultListModel listModel = new DefaultListModel();

    /**
     * Create the dialog.
     */
    public MultiTableOperationDialog() {
        super((Frame) null, true);

        this.setTitle("多张表格操作");
        this.setBounds(100, 100, 450, 442);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new BorderLayout(0, 0));

        final JList listTables = new JList();
        HTablesMultiExport.createData(this.listModel);
        listTables.setModel(this.listModel);

        CheckList.createCheckList(listTables);

        JScrollPane sp = new JScrollPane(listTables);
        this.contentPanel.add(sp, BorderLayout.CENTER);
        JPanel buttonPane = new JPanel();
        this.getContentPane().add(buttonPane, BorderLayout.SOUTH);
        GridBagLayout gbl_buttonPane = new GridBagLayout();
        gbl_buttonPane.columnWidths = new int[] { 69, 57, 57, 69, 0, 69, 0 };
        gbl_buttonPane.rowHeights = new int[] { 23, 0 };
        gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        buttonPane.setLayout(gbl_buttonPane);
        JButton btnSelectAll = new JButton("全选");
        btnSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel model = listTables.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (!item.isSelected()) {
                        item.setSelected(true);
                    }
                }
                listTables.repaint();
            }
        });
        JButton btnClearSelection = new JButton("全不选");
        btnClearSelection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel model = listTables.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (item.isSelected()) {
                        item.setSelected(false);
                    }
                }
                listTables.repaint();
            }
        });
        GridBagConstraints gbc_btnClearSelection = new GridBagConstraints();
        gbc_btnClearSelection.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnClearSelection.insets = new Insets(0, 5, 0, 5);
        gbc_btnClearSelection.gridx = 0;
        gbc_btnClearSelection.gridy = 0;
        buttonPane.add(btnClearSelection, gbc_btnClearSelection);
        GridBagConstraints gbc_btnSelectAll = new GridBagConstraints();
        gbc_btnSelectAll.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnSelectAll.insets = new Insets(0, 0, 0, 5);
        gbc_btnSelectAll.gridx = 1;
        gbc_btnSelectAll.gridy = 0;
        buttonPane.add(btnSelectAll, gbc_btnSelectAll);

        JButton button = new JButton("反选");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel model = listTables.getModel();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    item.setSelected(!item.isSelected());
                }
                listTables.repaint();
            }
        });
        GridBagConstraints gbc_button = new GridBagConstraints();
        gbc_button.anchor = GridBagConstraints.NORTHWEST;
        gbc_button.insets = new Insets(0, 0, 0, 5);
        gbc_button.gridx = 2;
        gbc_button.gridy = 0;
        buttonPane.add(button, gbc_button);
        JButton btnDrop = new JButton("Drop");
        btnDrop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel) listTables.getModel();
                int count = 0;
                ArrayList<CheckableItem> removeList = new ArrayList<CheckableItem>();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (item.isSelected()) {
                        ++count;
                        HTableDdls.dropTable(item.toString(), null);
                        removeList.add(item);
                    }
                }
                for (CheckableItem item : removeList) {
                    model.removeElement(item);
                }

                if (count == 0) {
                    HBaserUtils.message("没有选择需要Drop的表!");
                    return;
                }
                HBaserUtils.message("Drop完成！");
            }
        });
        GridBagConstraints gbc_btnDrop = new GridBagConstraints();
        gbc_btnDrop.anchor = GridBagConstraints.NORTHEAST;
        gbc_btnDrop.insets = new Insets(0, 0, 0, 5);
        gbc_btnDrop.gridx = 3;
        gbc_btnDrop.gridy = 0;
        buttonPane.add(btnDrop, gbc_btnDrop);
        JButton btnClose = new JButton("关闭");
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MultiTableOperationDialog.this.setVisible(false);
                MultiTableOperationDialog.this.dispose();
            }
        });

        JButton btnTrunc = new JButton("Trunc");
        btnTrunc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel model = (DefaultListModel) listTables.getModel();
                int count = 0;
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (item.isSelected()) {
                        ++count;
                        HTableDdls.truncateTable(item.toString(), null);
                    }
                }

                if (count == 0) {
                    HBaserUtils.message("没有选择需要Trunc的表!");
                    return;
                }

                HBaserUtils.message("Trunc完成！");
            }
        });
        GridBagConstraints gbc_btnTrunc = new GridBagConstraints();
        gbc_btnTrunc.insets = new Insets(0, 0, 0, 5);
        gbc_btnTrunc.gridx = 4;
        gbc_btnTrunc.gridy = 0;
        buttonPane.add(btnTrunc, gbc_btnTrunc);
        btnClose.setActionCommand("Cancel");
        GridBagConstraints gbc_btnClose = new GridBagConstraints();
        gbc_btnClose.anchor = GridBagConstraints.NORTHWEST;
        gbc_btnClose.gridx = 5;
        gbc_btnClose.gridy = 0;
        buttonPane.add(btnClose, gbc_btnClose);
    }
}
