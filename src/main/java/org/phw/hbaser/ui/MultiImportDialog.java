package org.phw.hbaser.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import org.phw.hbaser.util.HBaserUtils;
import org.phw.hbaser.util.HTablesMultiImport;

public class MultiImportDialog extends JDialog {
    private static final long serialVersionUID = -8788569498105312058L;
    private final JPanel contentPanel = new JPanel();

    /**
     * Create the dialog.
     * @throws FileNotFoundException 
     */
    public MultiImportDialog(Frame parent, final File file) {
        super(parent, true);

        setTitle("导入多张表格");
        setBounds(100, 100, 450, 442);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        final JList listTables = new JList(HTablesMultiImport.createData(file));

        CheckList.createCheckList(listTables);

        JScrollPane sp = new JScrollPane(listTables);
        contentPanel.add(sp, BorderLayout.CENTER);
        JPanel buttonPane = new JPanel();
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        GridBagLayout gbl_buttonPane = new GridBagLayout();
        gbl_buttonPane.columnWidths = new int[] { 69, 57, 57, 69, 69, 0 };
        gbl_buttonPane.rowHeights = new int[] { 23, 0 };
        gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
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
        gbc_btnClearSelection.insets = new Insets(0, 5, 5, 5);
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
        JButton btnImport = new JButton("导入");
        btnImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ListModel model = listTables.getModel();
                ArrayList<String> selectedTables = new ArrayList<String>();
                for (int i = 0; i < model.getSize(); i++) {
                    CheckableItem item = (CheckableItem) model.getElementAt(i);
                    if (item.isSelected()) {
                        selectedTables.add(item.toString());
                    }
                }
                if (selectedTables.size() == 0) {
                    HBaserUtils.message("没有选择需要导入的表!");
                    return;
                }

                boolean ok = new HTablesMultiImport().exportHTables(file, selectedTables);
                if (ok) {
                    HBaserUtils.message("导入完成！");
                }

                setVisible(false);
                dispose();
            }
        });
        GridBagConstraints gbc_btnImport = new GridBagConstraints();
        gbc_btnImport.anchor = GridBagConstraints.NORTHEAST;
        gbc_btnImport.insets = new Insets(0, 0, 0, 5);
        gbc_btnImport.gridx = 3;
        gbc_btnImport.gridy = 0;
        buttonPane.add(btnImport, gbc_btnImport);
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        cancelButton.setActionCommand("Cancel");
        GridBagConstraints gbc_cancelButton = new GridBagConstraints();
        gbc_cancelButton.anchor = GridBagConstraints.NORTHWEST;
        gbc_cancelButton.gridx = 4;
        gbc_cancelButton.gridy = 0;
        buttonPane.add(cancelButton, gbc_cancelButton);
    }

}
