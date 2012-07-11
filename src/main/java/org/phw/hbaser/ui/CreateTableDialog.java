package org.phw.hbaser.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.io.hfile.Compression;
import org.phw.core.lang.Strings;
import org.phw.hbaser.util.HBaserConfig;
import org.phw.hbaser.util.HBaserUtils;
import org.phw.hbaser.util.HTableDdls;

public class CreateTableDialog extends JDialog {
    private static final long serialVersionUID = 302021700928275196L;
    private JTextField textTableName;
    private ArrayList<FamilyPanel> familyPanels = new ArrayList<FamilyPanel>(5);
    JButton btnAddFamily;
    JButton btnDelLastFamily;

    public static void main(String[] args) {
        try {
            final CreateTableDialog dialog = new CreateTableDialog(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the panel.
     */
    public CreateTableDialog(Frame parent) {
        super(parent, true);
        setTitle("Create Hbase Table");
        setBounds(100, 100, 574, 375);

        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        JLabel lblTableName = new JLabel("Table Name");
        GridBagConstraints gbc_lblTableName = new GridBagConstraints();
        gbc_lblTableName.insets = new Insets(0, 5, 5, 5);
        gbc_lblTableName.gridx = 0;
        gbc_lblTableName.gridy = 0;
        getContentPane().add(lblTableName, gbc_lblTableName);

        textTableName = new JTextField();
        GridBagConstraints gbc_textTableName = new GridBagConstraints();
        gbc_textTableName.insets = new Insets(5, 5, 5, 0);
        gbc_textTableName.fill = GridBagConstraints.HORIZONTAL;
        gbc_textTableName.gridx = 1;
        gbc_textTableName.gridy = 0;
        getContentPane().add(textTableName, gbc_textTableName);
        textTableName.setColumns(10);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        getContentPane().add(scrollPane, gbc_scrollPane);

        final JPanel panel = new JPanel();
        scrollPane.setViewportView(panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        JPanel panel_1 = new JPanel();
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.gridwidth = 2;
        gbc_panel_1.insets = new Insets(0, 5, 0, 0);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 2;
        getContentPane().add(panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        btnAddFamily = new JButton("+ Add Family");
        btnAddFamily.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        createFamilyPanel(panel);
                    }
                });
            }
        });
        GridBagConstraints gbc_btnAddFamily = new GridBagConstraints();
        gbc_btnAddFamily.insets = new Insets(0, 0, 0, 5);
        gbc_btnAddFamily.gridx = 0;
        gbc_btnAddFamily.gridy = 0;
        panel_1.add(btnAddFamily, gbc_btnAddFamily);

        btnDelLastFamily = new JButton("- Remove Last Family");
        btnDelLastFamily.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        removeLastFamilyPanel(panel);
                    }
                });
            }
        });
        GridBagConstraints gbc_btnDelLastFamily = new GridBagConstraints();
        gbc_btnDelLastFamily.insets = new Insets(0, 0, 0, 5);
        gbc_btnDelLastFamily.gridx = 1;
        gbc_btnDelLastFamily.gridy = 0;
        panel_1.add(btnDelLastFamily, gbc_btnDelLastFamily);

        JButton btnSaveTable = new JButton("Create Table");
        btnSaveTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTable();
            }
        });
        GridBagConstraints gbc_btnSaveTable = new GridBagConstraints();
        gbc_btnSaveTable.anchor = GridBagConstraints.EAST;
        gbc_btnSaveTable.gridx = 2;
        gbc_btnSaveTable.gridy = 0;
        panel_1.add(btnSaveTable, gbc_btnSaveTable);

        createFamilyPanel(panel);
    }

    void createFamilyPanel(final JPanel panel) {
        FamilyPanel panelFamily = new FamilyPanel(familyPanels.size());
        GridBagConstraints gbc_panelFamily = new GridBagConstraints();
        gbc_panelFamily.insets = new Insets(0, 0, 5, 0);
        gbc_panelFamily.fill = GridBagConstraints.BOTH;
        gbc_panelFamily.gridx = 0;
        gbc_panelFamily.gridy = familyPanels.size();
        panel.add(panelFamily, gbc_panelFamily);
        familyPanels.add(panelFamily);

        btnDelLastFamily.setEnabled(familyPanels.size() > 1);

        panel.revalidate();
        panel.repaint();
    }

    void removeLastFamilyPanel(JPanel panel) {
        int index = familyPanels.size() - 1;
        panel.remove(familyPanels.remove(index));

        btnDelLastFamily.setEnabled(familyPanels.size() > 1);

        panel.revalidate();
        panel.repaint();
    }

    public void createTable() {
        try {
            HBaseAdmin admin = new HBaseAdmin(HBaserConfig.config);
            String tableName = textTableName.getText().trim();
            if (Strings.isEmpty(tableName)) {
                HBaserUtils.message("请输入表名");
                return;
            }

            if (admin.tableExists(tableName)) {
                int confirm = JOptionPane.showConfirmDialog(null, "表" + tableName + "已经存在，是否重建？");
                if (confirm != JOptionPane.OK_OPTION) {
                    return;
                }

                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }

            HTableDescriptor tableDesc = new HTableDescriptor(tableName);

            for (FamilyPanel familyPanel : familyPanels) {
                String familyName = familyPanel.textFamily.getText().trim();
                if (Strings.isEmpty(familyName)) {
                    HBaserUtils.message("请输入列族名");
                    return;
                }

                HColumnDescriptor family = new HColumnDescriptor(familyName);
                String versions = familyPanel.textVersions.getText().trim();
                if (!Strings.isEmpty(versions)) {
                    if (Strings.isInteger(versions)) {
                        family.setMaxVersions(Integer.valueOf(versions));
                    }
                    else {
                        HBaserUtils.message(versions + "不是整数（VERSION要求整数），忽略");
                    }
                }

                String ttl = familyPanel.textTTL.getText().trim();
                if (!Strings.isEmpty(ttl)) {
                    if (Strings.isInteger(ttl)) {
                        family.setTimeToLive(Integer.valueOf(ttl));
                    }
                    else {
                        HBaserUtils.message(ttl + "不是整数（TTL要求整数），忽略");
                    }
                }

                family.setInMemory(familyPanel.chkInMemory.isSelected());

                family.setBlockCacheEnabled(familyPanel.chkBlockCache.isSelected());
                if (familyPanel.chkBlockCache.isSelected()) {
                    String cacheSize = familyPanel.textBlockSize.getText().trim();
                    if (!Strings.isEmpty(cacheSize)) {
                        if (Strings.isInteger(cacheSize)) {
                            family.setBlocksize(Integer.valueOf(ttl));
                        }
                        else {
                            HBaserUtils.message(ttl + "不是整数（BLOCK SIZE要求整数），忽略");
                        }
                    }
                }
                String compression = (String) familyPanel.chkCompression.getSelectedItem();
                family.setCompressionType(Compression.Algorithm.valueOf(compression));

                tableDesc.addFamily(family);
            }

            admin.createTable(tableDesc);

            HTableDdls.listTables();

            HBaserUtils.message(tableName + "建表成功");
            setVisible(false);
            dispose();
        }
        catch (Exception e) {
            HBaserUtils.error(e);
        }

    }
}
