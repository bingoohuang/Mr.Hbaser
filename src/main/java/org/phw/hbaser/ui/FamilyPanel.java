package org.phw.hbaser.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FamilyPanel extends JPanel {
    private static final long serialVersionUID = -134798003582645833L;
    final JTextField textFamily;
    final JTextField textVersions;
    final JTextField textTTL;
    final JTextField textBlockSize;
    final JCheckBox chkBlockCache;
    final JComboBox chkCompression;
    final JCheckBox chkInMemory;

    public FamilyPanel(int familyNo) {
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        setLayout(gbl_panel);

        JLabel label = new JLabel("" + (familyNo + 1));
        label.setForeground(SystemColor.textHighlight);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        add(label, gbc_label);

        JLabel label_1 = new JLabel("Family Name");
        label_1.setForeground(SystemColor.textHighlight);
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.WEST;
        gbc_label_1.insets = new Insets(0, 0, 5, 5);
        gbc_label_1.gridx = 1;
        gbc_label_1.gridy = 0;
        add(label_1, gbc_label_1);

        textFamily = new JTextField();
        textFamily.setColumns(10);
        GridBagConstraints gbc_textFamily = new GridBagConstraints();
        gbc_textFamily.fill = GridBagConstraints.HORIZONTAL;
        gbc_textFamily.gridwidth = 4;
        gbc_textFamily.insets = new Insets(5, 0, 5, 5);
        gbc_textFamily.gridx = 2;
        gbc_textFamily.gridy = 0;
        add(textFamily, gbc_textFamily);

        JLabel label_2 = new JLabel("Versions:");
        GridBagConstraints gbc_label_2 = new GridBagConstraints();
        gbc_label_2.anchor = GridBagConstraints.WEST;
        gbc_label_2.insets = new Insets(0, 0, 5, 5);
        gbc_label_2.gridx = 1;
        gbc_label_2.gridy = 1;
        add(label_2, gbc_label_2);

        textVersions = new JTextField();
        textVersions.setColumns(10);
        GridBagConstraints gbc_textVersions = new GridBagConstraints();
        gbc_textVersions.fill = GridBagConstraints.HORIZONTAL;
        gbc_textVersions.insets = new Insets(0, 0, 5, 5);
        gbc_textVersions.gridx = 2;
        gbc_textVersions.gridy = 1;
        add(textVersions, gbc_textVersions);

        JLabel label_3 = new JLabel("TTL(sec):");
        GridBagConstraints gbc_label_3 = new GridBagConstraints();
        gbc_label_3.anchor = GridBagConstraints.WEST;
        gbc_label_3.insets = new Insets(0, 0, 5, 5);
        gbc_label_3.gridx = 3;
        gbc_label_3.gridy = 1;
        add(label_3, gbc_label_3);

        textTTL = new JTextField();
        textTTL.setColumns(10);
        GridBagConstraints gbc_textTTL = new GridBagConstraints();
        gbc_textTTL.fill = GridBagConstraints.HORIZONTAL;
        gbc_textTTL.insets = new Insets(0, 0, 5, 5);
        gbc_textTTL.gridx = 4;
        gbc_textTTL.gridy = 1;
        add(textTTL, gbc_textTTL);

        JLabel label_4 = new JLabel("30d:108000");
        GridBagConstraints gbc_label_4 = new GridBagConstraints();
        gbc_label_4.insets = new Insets(0, 0, 5, 5);
        gbc_label_4.gridx = 5;
        gbc_label_4.gridy = 1;
        add(label_4, gbc_label_4);

        chkInMemory = new JCheckBox("In Memory");
        GridBagConstraints gbc_chkInMemory = new GridBagConstraints();
        gbc_chkInMemory.anchor = GridBagConstraints.WEST;
        gbc_chkInMemory.insets = new Insets(0, 0, 5, 5);
        gbc_chkInMemory.gridx = 1;
        gbc_chkInMemory.gridy = 2;
        add(chkInMemory, gbc_chkInMemory);

        chkBlockCache = new JCheckBox("Block Cache");
        chkBlockCache.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textBlockSize.setEditable(chkBlockCache.isSelected());
            }
        });
        GridBagConstraints gbc_chkBlockCache = new GridBagConstraints();
        gbc_chkBlockCache.anchor = GridBagConstraints.WEST;
        gbc_chkBlockCache.insets = new Insets(0, 0, 5, 5);
        gbc_chkBlockCache.gridx = 2;
        gbc_chkBlockCache.gridy = 2;
        add(chkBlockCache, gbc_chkBlockCache);

        JLabel label_5 = new JLabel("Block Size:");
        GridBagConstraints gbc_label_5 = new GridBagConstraints();
        gbc_label_5.anchor = GridBagConstraints.WEST;
        gbc_label_5.insets = new Insets(0, 0, 0, 5);
        gbc_label_5.gridx = 1;
        gbc_label_5.gridy = 3;
        add(label_5, gbc_label_5);

        textBlockSize = new JTextField();
        textBlockSize.setEditable(false);
        textBlockSize.setColumns(10);
        GridBagConstraints gbc_textBlockSize = new GridBagConstraints();
        gbc_textBlockSize.fill = GridBagConstraints.HORIZONTAL;
        gbc_textBlockSize.insets = new Insets(0, 0, 0, 5);
        gbc_textBlockSize.gridx = 2;
        gbc_textBlockSize.gridy = 3;
        add(textBlockSize, gbc_textBlockSize);

        JLabel label_6 = new JLabel("Compression:");
        GridBagConstraints gbc_label_6 = new GridBagConstraints();
        gbc_label_6.anchor = GridBagConstraints.WEST;
        gbc_label_6.insets = new Insets(0, 0, 0, 5);
        gbc_label_6.gridx = 3;
        gbc_label_6.gridy = 3;
        add(label_6, gbc_label_6);

        chkCompression = new JComboBox();
        chkCompression.setModel(new DefaultComboBoxModel(new String[] { "NONE", "GZ", "LZO" }));
        GridBagConstraints gbc_chkCompression = new GridBagConstraints();
        gbc_chkCompression.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkCompression.insets = new Insets(0, 0, 0, 5);
        gbc_chkCompression.gridx = 4;
        gbc_chkCompression.gridy = 3;
        add(chkCompression, gbc_chkCompression);

    }

}
