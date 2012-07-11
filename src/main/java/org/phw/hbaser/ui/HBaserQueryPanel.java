package org.phw.hbaser.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;

import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.RowLock;
import org.phw.core.lang.Ios;
import org.phw.core.lang.Pair;
import org.phw.hbaser.util.HBaserConfig;
import org.phw.hbaser.util.HBaserUtils;
import org.phw.hbaser.util.HTableDdls;
import org.phw.hbaser.util.HTableDeletes;
import org.phw.hbaser.util.HTableMetaMgr;
import org.phw.hbaser.util.HTablePuts;
import org.phw.hbaser.util.HTableQuerys;
import org.phw.hbaser.util.ImageMeta;
import org.phw.hbaser.util.JImageLabel;
import org.phw.hbaser.util.TextTransfer;
import org.phw.hbaser.util.HBaserUtils.FileOp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONEx;
import com.alibaba.fastjson.JSONObject;

public class HBaserQueryPanel extends JPanel {
    private static final long serialVersionUID = 1151634200099445159L;
    private JButton btnQuery;
    JTextField textSelect;
    JTextField textWhere;
    JTextPane textJSON;
    final JComboBox comboTables = new JComboBox();
    final JComboBox comboEnv = new JComboBox();
    private final JCheckBox chkUseMetaTable = new JCheckBox("Use Meta Table");
    final HTableMetaMgr extendIni = new HTableMetaMgr(this.chkUseMetaTable);
    JTextPane textTableMeta = new JTextPane();
    String imageFormatName;
    JFrame frameParent;

    ActionListener comboTableActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 选择表名，加载表元信息配置
            loadTableMeta();
        }
    };
    private final ActionListener comboEnvActionPerformed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                extendIni.save();
            }
            catch (Exception ex) {
                HBaserUtils.error(ex);
            }

            String env = (String) comboEnv.getSelectedItem();
            if (env.equals("配置环境(config)")) {
                HBaserConfig.loginDialog.setVisible(true);
            }
            else {
                try {
                    HBaserConfig.setConfig(HBaserConfig.loginDialog.ini.properties(env));
                    comboTables.removeActionListener(comboTableActionListener);
                    HTableDdls.listTables2(comboTables);
                    comboTables.addActionListener(comboTableActionListener);
                    extendIni.load(env);
                    loadTableMeta();
                    comboEnv.removeItem("请选择环境");
                }
                catch (IOException ex) {
                    HBaserUtils.error(ex);
                    comboEnv.setSelectedIndex(0);
                }
            }
        }
    };
    private JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    private JSplitPane splitPane_1 = new JSplitPane();
    private JTextField textCheck;

    public void setDividerLocation() {
        // 要在setVisiable(true)之后才有效。
        this.splitPane.setDividerLocation(0.8);
        this.splitPane_1.setDividerLocation(0.8);
    }

    /**
     * Create the panel.
     * @throws IOException 
     */
    public HBaserQueryPanel(JFrame frame) throws IOException {
        final JPopupMenu jPopupMenu2 = new JPopupMenu();
        final JPopupMenu jImagePopupMenu = new JPopupMenu();
        final JPopupMenu jPopupMenu = new JPopupMenu();
        frameParent = frame;

        GridBagConstraints gbc_labelLogin = new GridBagConstraints();
        gbc_labelLogin.anchor = GridBagConstraints.EAST;
        gbc_labelLogin.insets = new Insets(0, 0, 0, 5);
        gbc_labelLogin.gridx = 3;
        gbc_labelLogin.gridy = 0;
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWeights = new double[] { 1.0 };
        gridBagLayout.rowWeights = new double[] { 1.0 };
        gridBagLayout.rowHeights = new int[] { 512 };
        this.setLayout(gridBagLayout);

        JPanel panel_7 = new JPanel();
        GridBagLayout gbl_panel_7 = new GridBagLayout();
        gbl_panel_7.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 };
        gbl_panel_7.columnWeights = new double[] { 0.0, 1.0 };
        gbl_panel_7.rowHeights = new int[] { 0, 0, 73, 0, 0, 0, 0 };
        panel_7.setLayout(gbl_panel_7);

        JLabel lblSelect = new JLabel("Select");
        GridBagConstraints gbc_lblSelect = new GridBagConstraints();
        gbc_lblSelect.insets = new Insets(0, 0, 5, 5);
        gbc_lblSelect.gridx = 0;
        gbc_lblSelect.gridy = 0;
        panel_7.add(lblSelect, gbc_lblSelect);

        this.textSelect = new JTextField();
        GridBagConstraints gbc_textSelect = new GridBagConstraints();
        gbc_textSelect.insets = new Insets(0, 0, 5, 0);
        gbc_textSelect.fill = GridBagConstraints.BOTH;
        gbc_textSelect.gridx = 1;
        gbc_textSelect.gridy = 0;

        panel_7.add(this.textSelect, gbc_textSelect);
        this.textSelect.setToolTipText(Ios.toString(this.getClass().getClassLoader().getResourceAsStream(
                "org/phw/hbaser/help/select.html"), "UTF-8"));
        this.textSelect.setColumns(10);

        JLabel lblFrom = new JLabel("From");
        GridBagConstraints gbc_lblFrom = new GridBagConstraints();
        gbc_lblFrom.insets = new Insets(0, 0, 5, 5);
        gbc_lblFrom.gridx = 0;
        gbc_lblFrom.gridy = 1;
        panel_7.add(lblFrom, gbc_lblFrom);

        HTableDdls.setComboTable(this.comboTables);
        GridBagConstraints gbc_comboTables = new GridBagConstraints();
        gbc_comboTables.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboTables.insets = new Insets(0, 0, 5, 0);
        gbc_comboTables.gridx = 1;
        gbc_comboTables.gridy = 1;
        panel_7.add(this.comboTables, gbc_comboTables);
        this.comboTables.setToolTipText("选择要操作的表格。\r\n新建表时，输入格式：新建表名,列族１,列族２,...,列族n");

        JLabel lblMeta = new JLabel("Meta");
        GridBagConstraints gbc_lblMeta = new GridBagConstraints();
        gbc_lblMeta.insets = new Insets(0, 0, 5, 5);
        gbc_lblMeta.gridx = 0;
        gbc_lblMeta.gridy = 2;
        panel_7.add(lblMeta, gbc_lblMeta);

        JScrollPane scrollPane_3 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
        gbc_scrollPane_3.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_3.gridx = 1;
        gbc_scrollPane_3.gridy = 2;
        panel_7.add(scrollPane_3, gbc_scrollPane_3);
        this.textTableMeta.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                saveTableMeta();
            }
        });
        scrollPane_3.setViewportView(this.textTableMeta);
        this.textTableMeta.setToolTipText(Ios.toString(this.getClass().getClassLoader().getResourceAsStream(
                "org/phw/hbaser/help/meta.html"), "UTF-8"));

        JLabel lblRowkey = new JLabel("Rowkey");
        GridBagConstraints gbc_lblRowkey = new GridBagConstraints();
        gbc_lblRowkey.insets = new Insets(0, 0, 5, 5);
        gbc_lblRowkey.gridx = 0;
        gbc_lblRowkey.gridy = 3;
        panel_7.add(lblRowkey, gbc_lblRowkey);

        final JTextField textRowkey = new JTextField();
        GridBagConstraints gbc_textRowkey = new GridBagConstraints();
        gbc_textRowkey.insets = new Insets(0, 0, 5, 0);
        gbc_textRowkey.fill = GridBagConstraints.HORIZONTAL;
        gbc_textRowkey.gridx = 1;
        gbc_textRowkey.gridy = 3;
        panel_7.add(textRowkey, gbc_textRowkey);
        textRowkey.setToolTipText(Ios.toString(this.getClass().getClassLoader().getResourceAsStream(
                "org/phw/hbaser/help/rowkey.html"), "UTF-8"));

        JLabel lblWhere = new JLabel("Where");
        GridBagConstraints gbc_lblWhere = new GridBagConstraints();
        gbc_lblWhere.insets = new Insets(0, 0, 5, 5);
        gbc_lblWhere.gridx = 0;
        gbc_lblWhere.gridy = 4;
        panel_7.add(lblWhere, gbc_lblWhere);

        this.textWhere = new JTextField();
        GridBagConstraints gbc_textWhere = new GridBagConstraints();
        gbc_textWhere.insets = new Insets(0, 0, 5, 0);
        gbc_textWhere.fill = GridBagConstraints.HORIZONTAL;
        gbc_textWhere.gridx = 1;
        gbc_textWhere.gridy = 4;
        panel_7.add(this.textWhere, gbc_textWhere);
        this.textWhere.setToolTipText(Ios.toString(this.getClass().getClassLoader().getResourceAsStream(
                "org/phw/hbaser/help/where.html"), "UTF-8"));
        this.textWhere.setColumns(10);

        this.splitPane_1.setOneTouchExpandable(true);
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
        gbc_splitPane_1.gridwidth = 2;
        gbc_splitPane_1.fill = GridBagConstraints.BOTH;
        gbc_splitPane_1.gridx = 0;
        gbc_splitPane_1.gridy = 6;
        panel_7.add(this.splitPane_1, gbc_splitPane_1);

        JPanel panel_2 = new JPanel();
        this.splitPane_1.setLeftComponent(panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] { 0, 0 };
        gbl_panel_2.rowHeights = new int[] { 0, 0 };
        gbl_panel_2.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_2.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        panel_2.setLayout(gbl_panel_2);

        JScrollPane scrollPane_1 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 0;
        gbc_scrollPane_1.gridy = 0;
        panel_2.add(scrollPane_1, gbc_scrollPane_1);

        final JTextPane textResult = new JTextPane();
        scrollPane_1.setViewportView(textResult);
        textResult.setToolTipText("鼠标右键有惊喜");
        textResult.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    if (textResult.getSelectedText() == null) {
                        int offset = textResult.viewToModel(e.getPoint());
                        textResult.setCaretPosition(offset);
                    }

                    jPopupMenu.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });

        JPanel panel_3 = new JPanel();
        this.splitPane_1.setRightComponent(panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[] { 0, 0 };
        gbl_panel_3.rowHeights = new int[] { 0, 0 };
        gbl_panel_3.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_3.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        panel_3.setLayout(gbl_panel_3);

        JPanel panel_5 = new JPanel();
        GridBagConstraints gbc_panel_5 = new GridBagConstraints();
        gbc_panel_5.fill = GridBagConstraints.BOTH;
        gbc_panel_5.gridx = 0;
        gbc_panel_5.gridy = 0;
        panel_3.add(panel_5, gbc_panel_5);
        GridBagLayout gbl_panel_5 = new GridBagLayout();
        gbl_panel_5.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_5.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel_5.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel_5.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        panel_5.setLayout(gbl_panel_5);

        JLabel lblCheck = new JLabel("Check");
        GridBagConstraints gbc_lblCheck = new GridBagConstraints();
        gbc_lblCheck.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblCheck.insets = new Insets(0, 0, 5, 5);
        gbc_lblCheck.gridx = 0;
        gbc_lblCheck.gridy = 0;
        panel_5.add(lblCheck, gbc_lblCheck);

        this.textCheck = new JTextField();
        this.textCheck.setToolTipText("[family.]qualifier=value/null");
        GridBagConstraints gbc_textCheck = new GridBagConstraints();
        gbc_textCheck.insets = new Insets(0, 0, 5, 0);
        gbc_textCheck.fill = GridBagConstraints.HORIZONTAL;
        gbc_textCheck.gridx = 1;
        gbc_textCheck.gridy = 0;
        panel_5.add(this.textCheck, gbc_textCheck);
        this.textCheck.setColumns(10);

        JLabel lblSet = new JLabel("Set");
        GridBagConstraints gbc_lblSet = new GridBagConstraints();
        gbc_lblSet.insets = new Insets(0, 0, 0, 5);
        gbc_lblSet.gridx = 0;
        gbc_lblSet.gridy = 1;
        panel_5.add(lblSet, gbc_lblSet);

        JScrollPane scrollPane = new JScrollPane();
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 1;
        gbc_scrollPane.gridy = 1;
        panel_5.add(scrollPane, gbc_scrollPane);

        this.textJSON = new JTextPane();
        scrollPane.setViewportView(this.textJSON);
        this.textJSON.setFont(new Font("Consolas", Font.PLAIN, 15));
        this.textJSON
                .setToolTipText("单行操作条件，例如.\r\n{rowkey: \"huangjb\", fam: {age: \"32\", name: \"bingoohuang\", sex: \"男\"}}");
        this.textJSON.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    jPopupMenu2.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });

        JPanel panel_1 = new JPanel();

        this.splitPane.setLeftComponent(panel_7);
        this.splitPane.setRightComponent(panel_1);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setOneTouchExpandable(true);

        GridBagConstraints gbc_splitPane = new GridBagConstraints();
        gbc_splitPane.fill = GridBagConstraints.BOTH;
        gbc_splitPane.gridx = 0;
        gbc_splitPane.gridy = 0;
        this.add(this.splitPane, gbc_splitPane);

        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);
        GridBagConstraints gbc_comboEnv = new GridBagConstraints();
        gbc_comboEnv.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboEnv.insets = new Insets(0, 0, 5, 0);
        gbc_comboEnv.gridx = 0;
        gbc_comboEnv.gridy = 0;
        panel_1.add(this.comboEnv, gbc_comboEnv);
        this.comboEnv.setFont(new Font("微软雅黑", Font.BOLD, 12));
        this.comboEnv.setForeground(Color.BLUE);

        final JComboBox comboOperation = new JComboBox();
        GridBagConstraints gbc_comboOperation = new GridBagConstraints();
        gbc_comboOperation.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboOperation.insets = new Insets(0, 0, 5, 0);
        gbc_comboOperation.gridx = 0;
        gbc_comboOperation.gridy = 1;
        panel_1.add(comboOperation, gbc_comboOperation);
        comboOperation.setModel(new DefaultComboBoxModel(new String[] { "--表操作--", "刷新列表(refresh)", "表信息(describe)",
                "创建表(create)", "删除表(drop)", "启用表(enable)", "停用表(disable)", "删数据(truncate)", "多表操作(multioperate)" }));
        comboOperation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String operation = (String) comboOperation.getSelectedItem();
                comboOperation.setSelectedIndex(0);

                if (!operation.contains("create") && comboTables.getSelectedIndex() < 0) {
                    return;
                }

                String tableName = operation.contains("create") ?
                        (String) comboTables.getEditor().getItem() : getTableName(true);
                if (tableName == null) {
                    return;
                }

                HBaserUtils.dispatchOperation(textResult, operation, tableName);
            }
        });

        final JComboBox comboExport = new JComboBox();
        GridBagConstraints gbc_comboExport = new GridBagConstraints();
        gbc_comboExport.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboExport.insets = new Insets(0, 0, 5, 0);
        gbc_comboExport.gridx = 0;
        gbc_comboExport.gridy = 2;
        panel_1.add(comboExport, gbc_comboExport);
        comboExport.setToolTipText("导出查询结果到文件，或者从文件导入数据到表");
        comboExport.setModel(new DefaultComboBoxModel(new String[] { "--导入/导出--", "导出当前表(Export)", "多表导出(MultiExport)",
                "导入当前表(Import)", "多表导入(MultiImport)" }));

        comboExport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String op = (String) comboExport.getSelectedItem();
                comboExport.setSelectedIndex(0);

                if (op.contains("MultiExport")) {
                    MultiExportDialog multiExportDialog = new MultiExportDialog(
                            (String) comboEnv.getSelectedItem(), null);
                    multiExportDialog.setVisible(true);

                }
                else if (op.contains("MultiImport")) {
                    JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                    fc.setFileFilter(new FileNameExtensionFilter("hbaser htable file", "hbaser", "zip"));
                    fc.setSelectedFile(new File((String) comboEnv.getSelectedItem() + ".zip"));
                    // fc.setMultiSelectionEnabled(true);
                    File fFile = HBaserUtils.getSelectedFile(fc, FileOp.OpenFile);
                    if (fFile == null) {
                        return;
                    }
                    MultiImportDialog multiImportDialog = new MultiImportDialog(null, fFile);
                    multiImportDialog.setVisible(true);

                }
                else if (op.contains("Export")) {
                    String tableName = getTableName(true);
                    if (tableName == null) {
                        return;
                    }

                    JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                    fc.setFileFilter(new FileNameExtensionFilter("hbaser htable file", "hbaser"));
                    fc.setSelectedFile(new File(tableName + ".hbaser"));

                    File fFile = HBaserUtils.getSelectedFile(fc, FileOp.Save);
                    if (fFile == null) {
                        return;
                    }

                    OutputStreamWriter out = null;
                    try {
                        out = new OutputStreamWriter(new FileOutputStream(fFile), "UTF-8");
                        HTableQuerys query = new HTableQuerys();
                        QueryOption queryOption = new QueryOption();

                        query.queryTable(lockPair, tableName, textTableMeta
                                .getText().trim(),
                                textRowkey.getText().trim(),
                                textSelect.getText().trim(),
                                textWhere.getText().trim(),
                                textResult, queryOption, out);
                    }
                    catch (Exception ex) {
                        HBaserUtils.error(ex);
                    }
                    finally {
                        Ios.closeQuietly(out);
                    }

                }
                else if (op.contains("Import")) {
                    JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                    fc.setFileFilter(new FileNameExtensionFilter("hbaser htable file", "hbaser"));
                    File file = HBaserUtils.getSelectedFile(fc, FileOp.OpenFile);
                    if (file == null) {
                        return;
                    }

                    new HTablePuts().importData(file, textResult, comboTables,
                            textTableMeta);
                }
            }
        });

        final JCheckBox chkTimeStamp = new JCheckBox("显示时间戳");
        GridBagConstraints gbc_chkTimeStamp = new GridBagConstraints();
        gbc_chkTimeStamp.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkTimeStamp.insets = new Insets(0, 0, 5, 0);
        gbc_chkTimeStamp.gridx = 0;
        gbc_chkTimeStamp.gridy = 3;
        panel_1.add(chkTimeStamp, gbc_chkTimeStamp);
        chkTimeStamp.setToolTipText("在查询结果中显示值的时间戳");

        final JCheckBox chkShowLineNo = new JCheckBox("显示行号");
        GridBagConstraints gbc_chkShowLineNo = new GridBagConstraints();
        gbc_chkShowLineNo.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkShowLineNo.insets = new Insets(0, 0, 5, 0);
        gbc_chkShowLineNo.gridx = 0;
        gbc_chkShowLineNo.gridy = 4;
        panel_1.add(chkShowLineNo, gbc_chkShowLineNo);

        final JCheckBox chkKeyOnly = new JCheckBox("KeyOnly");
        GridBagConstraints gbc_chkFirstkeyOnly = new GridBagConstraints();
        gbc_chkFirstkeyOnly.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkFirstkeyOnly.insets = new Insets(0, 0, 5, 0);
        gbc_chkFirstkeyOnly.gridx = 0;
        gbc_chkFirstkeyOnly.gridy = 5;
        panel_1.add(chkKeyOnly, gbc_chkFirstkeyOnly);

        this.chkUseMetaTable.setSelected(true);
        GridBagConstraints gbc_chkUseMetaTable = new GridBagConstraints();
        gbc_chkUseMetaTable.fill = GridBagConstraints.HORIZONTAL;
        gbc_chkUseMetaTable.insets = new Insets(0, 0, 5, 0);
        gbc_chkUseMetaTable.gridx = 0;
        gbc_chkUseMetaTable.gridy = 6;
        panel_1.add(this.chkUseMetaTable, gbc_chkUseMetaTable);

        JPanel panel_4 = new JPanel();
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.insets = new Insets(0, 0, 5, 0);
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 7;
        panel_1.add(panel_4, gbc_panel_4);
        GridBagLayout gbl_panel_4 = new GridBagLayout();
        gbl_panel_4.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_4.rowHeights = new int[] { 0, 0 };
        gbl_panel_4.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        gbl_panel_4.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel_4.setLayout(gbl_panel_4);

        JLabel lblRowlimit = new JLabel("MaxRows");
        GridBagConstraints gbc_lblRowlimit = new GridBagConstraints();
        gbc_lblRowlimit.insets = new Insets(0, 0, 0, 5);
        gbc_lblRowlimit.gridx = 0;
        gbc_lblRowlimit.gridy = 0;
        panel_4.add(lblRowlimit, gbc_lblRowlimit);

        final JFormattedTextField textRowLimit = new JFormattedTextField();
        textRowLimit.setText("5");
        GridBagConstraints gbc_textRowLimit = new GridBagConstraints();
        gbc_textRowLimit.fill = GridBagConstraints.HORIZONTAL;
        gbc_textRowLimit.gridx = 1;
        gbc_textRowLimit.gridy = 0;
        panel_4.add(textRowLimit, gbc_textRowLimit);
        textRowLimit.setColumns(3);

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 8;
        panel_1.add(panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 55, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0 };
        gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        panel.setLayout(gbl_panel);

        this.btnQuery = new JButton("Query");
        GridBagConstraints gbc_btnQuery = new GridBagConstraints();
        gbc_btnQuery.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnQuery.gridx = 0;
        gbc_btnQuery.gridy = 0;
        panel.add(this.btnQuery, gbc_btnQuery);

        JScrollPane scrollPane_2 = new JScrollPane();
        GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
        gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_2.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_2.gridx = 0;
        gbc_scrollPane_2.gridy = 9;
        panel_1.add(scrollPane_2, gbc_scrollPane_2);

        final JLabel imageLabel = new JLabel("");
        imageLabel.setLayout(new GridLayout(1, 1));
        scrollPane_2.setViewportView(imageLabel);

        final JComboBox comboRowAction = new JComboBox();
        GridBagConstraints gbc_comboRowAction = new GridBagConstraints();
        gbc_comboRowAction.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboRowAction.anchor = GridBagConstraints.SOUTH;
        gbc_comboRowAction.gridx = 0;
        gbc_comboRowAction.gridy = 10;
        panel_1.add(comboRowAction, gbc_comboRowAction);

        comboRowAction.setModel(new DefaultComboBoxModel(new String[]
        { "--更新操作--",
                "Insert row By Rowkey", "Update values By Rowkey",
                "Put values By Rowkey", "Increment By Rowkey", "Delete values By Rowkey", "Delete row By Rowkey",
                "Lock Row", "UnLock Row",
                "Put values By Query", "Increment By Query", "Delete values By Query", "Delete rows By Query",
                "CAS Put By Rowkey", "CAS Delete values By Rowkey", "CAS Delete row By Rowkey",
                "CAS Put By Query", "CAS Delete values By Query", "CAS Delete rows By Query" }));

        comboRowAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String op = (String) comboRowAction.getSelectedItem();
                comboRowAction.setSelectedIndex(0);

                String tableName = getTableName(true);
                if (tableName == null) {
                    return;
                }

                String json = textJSON.getText().trim();
                if (!op.equals("Delete rows By Query") && json.length() <= 0) {
                    HBaserUtils.message("No data for Puts");
                    return;
                }

                dispatchUpdateOperation(textRowkey, textResult, op, tableName, json);
            }
        });

        //        textTableMeta.getDocument().addDocumentListener(new DocumentListener() {
        //            @Override
        //            public void insertUpdate(DocumentEvent evt) {
        //                // saveTableMeta();
        //            }
        //
        //            @Override
        //            public void removeUpdate(DocumentEvent evt) {
        //                // saveTableMeta();
        //            }
        //
        //            @Override
        //            public void changedUpdate(DocumentEvent evt) {
        //                saveTableMeta();
        //            }
        //        });

        this.comboTables.addActionListener(this.comboTableActionListener);

        JMenuItem clear = new JMenuItem("Clear");
        JMenuItem copySelect = new JMenuItem("Copy Selection");
        JMenuItem selectLine = new JMenuItem("Select Line");
        JMenuItem copyLineToBelow = new JMenuItem("Copy JSON to below");
        JMenuItem showImage = new JMenuItem("Show Image");
        JMenuItem saveAs = new JMenuItem("Save As...");
        jPopupMenu.add(clear);
        jPopupMenu.add(selectLine);
        jPopupMenu.add(copySelect);
        jPopupMenu.add(copyLineToBelow);
        jPopupMenu.add(showImage);
        jPopupMenu.add(saveAs);
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HBaserUtils.clearTextPane(textResult);
            }
        });

        selectLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HBaserUtils.selectCurrentPositionLine(textResult);
            }
        });

        copySelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextTransfer textTransfer = new TextTransfer();
                String selectedText = textResult.getSelectedText();
                if (selectedText != null) {
                    textTransfer.setClipboardContents(selectedText);
                }
            }
        });

        showImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = getTableName(true);
                if (tableName == null) {
                    return;
                }

                String json = HBaserUtils.getCurrentPositionJson(textResult);
                if (json == null) {
                    return;
                }
                textJSON.setText(json);

                try {
                    imageLabel.removeAll();
                    JSONObject jsonObj = JSON.parseObject(json);
                    HashMap<String, ImageMeta> imageMetas = HBaserUtils.getImageMetasFromJSON(jsonObj);
                    if (imageMetas.size() == 0) {
                        imageLabel.repaint();
                        return;
                    }
                    else if (imageMetas.size() == 1) {
                        for (String key : imageMetas.keySet()) {
                            ImageMeta meta = imageMetas.get(key);
                            String formatName = meta.getFormat();
                            Image image = ImageIO.read(new ByteArrayInputStream(meta.getImage()));
                            imageLabel.add(new JImageLabel(image, formatName));
                        }
                    }
                    else {
                        Set<String> cacheIDSet = imageMetas.keySet();
                        JImageDialog dialog = new JImageDialog(frameParent, true, cacheIDSet);
                        dialog.setVisible(true);
                        String cacheID = dialog.getSelectedCacheID();
                        if (cacheID.equals("")) {
                            imageLabel.repaint();
                            return;
                        }
                        ImageMeta meta = imageMetas.get(cacheID);
                        String formatName = meta.getFormat();
                        Image image = ImageIO.read(new ByteArrayInputStream(meta.getImage()));
                        imageLabel.add(new JImageLabel(image, formatName));
                    }
                    imageLabel.validate();
                }
                catch (Exception ex) {
                    HBaserUtils.error(ex);
                }
            }
        });
        copyLineToBelow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String json = HBaserUtils.getCurrentPositionJson(textResult);
                if (json != null) {
                    textJSON.setText(json);
                }
            }
        });
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                fc.setSelectedFile(new File("result.txt"));
                File fFile = HBaserUtils.getSelectedFile(fc, FileOp.Save);
                if (fFile == null) {
                    return;
                }
                OutputStreamWriter out = null;
                try {
                    out = new OutputStreamWriter(new FileOutputStream(fFile), "UTF-8");
                    out.write(textResult.getText());
                }
                catch (Exception ex) {
                    HBaserUtils.error(ex);
                }
                finally {
                    Ios.closeQuietly(out);
                }
            }
        });

        JMenuItem clear2 = new JMenuItem("Clear");
        JMenuItem copyItem = new JMenuItem("Copy Selection");
        JMenuItem pastItem = new JMenuItem("Paste");
        JMenuItem createTmpl = new JMenuItem("Create Template");
        JMenuItem prettyJSON = new JMenuItem("Pretty JSON");
        jPopupMenu2.add(clear2);
        jPopupMenu2.add(copyItem);
        jPopupMenu2.add(pastItem);
        jPopupMenu2.add(createTmpl);
        jPopupMenu2.add(prettyJSON);

        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextTransfer textTransfer = new TextTransfer();
                String selectedText = textJSON.getSelectedText();
                if (selectedText != null) {
                    textTransfer.setClipboardContents(selectedText);
                }
            }
        });
        pastItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StyledDocument doc = textJSON.getStyledDocument();
                TextTransfer textTransfer = new TextTransfer();
                String str = textTransfer.getClipboardContents();
                try {
                    doc.insertString(textJSON.getCaretPosition(), str, null);
                }
                catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
            }
        });

        prettyJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JSONObject jsonObj = JSON.parseObject(textJSON.getText());
                String prettyJson = JSONEx.toJSONString(jsonObj, true);
                textJSON.setText(prettyJson);
            }
        });

        createTmpl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = getTableName(true);
                if (tableName == null) {
                    return;
                }

                textJSON.setText(HTableDdls.getTableRowTemplate(tableName,
                        textTableMeta.getText(), textResult));
            }
        });
        clear2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                HBaserUtils.clearTextPane(textJSON);
            }
        });

        JMenuItem clearImage = new JMenuItem("Clear");
        JMenuItem saveImageAs = new JMenuItem("Save Image As...");
        JMenuItem loadImageFrom = new JMenuItem("Load Image From...");
        JMenuItem loadToCache = new JMenuItem("Load Image To Cache");
        jImagePopupMenu.add(clearImage);
        jImagePopupMenu.add(saveImageAs);
        jImagePopupMenu.add(loadImageFrom);
        jImagePopupMenu.add(loadToCache);

        clearImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageLabel.removeAll();
                imageLabel.repaint();
            }
        });

        loadToCache.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = getTableName(true);
                if (tableName == null) {
                    return;
                }

                try {
                    if (imageLabel.getComponentCount() == 0) {
                        HBaserUtils.message("图片无效！");
                        return;
                    }
                    JImageLabel iLabel = (JImageLabel) imageLabel.getComponent(0);
                    Image image = iLabel.getImage();
                    String formatName = iLabel.getImageFormatName();
                    if (image == null || formatName.equals("")) {
                        HBaserUtils.message("图片无效！");
                        return;
                    }

                    byte[] imageBytes = HBaserUtils.getImage((BufferedImage) image, formatName);
                    HBaserUtils.loadToCache(imageBytes, textResult);
                }
                catch (Exception ex) {
                    HBaserUtils.error(ex);
                }
            }
        });

        loadImageFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                fc.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "gif", "png", "jpeg", "bmp"));
                File file = HBaserUtils.getSelectedFile(fc, FileOp.OpenFile);
                if (file == null) {
                    return;
                }
                FileInputStream fis = null;
                try {
                    imageLabel.removeAll();
                    fis = new FileInputStream(file);
                    String formatName = HBaserUtils.getImageType(fis);
                    Image image = ImageIO.read(file);
                    if (image == null || formatName.equals("")) {
                        HBaserUtils.message("数据错误！");
                        imageLabel.repaint();
                        return;
                    }

                    imageLabel.add(new JImageLabel(image, formatName));
                    imageLabel.validate();
                }
                catch (IOException ex) {
                    HBaserUtils.error(ex);
                }
                finally {
                    Ios.closeQuietly(fis);
                }
            }
        });

        saveImageAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (imageLabel.getComponentCount() == 0) {
                        HBaserUtils.message("不能保存！");
                        return;
                    }
                    JImageLabel iLabel = (JImageLabel) imageLabel.getComponent(0);
                    Image image = iLabel.getImage();
                    String formatName = iLabel.getImageFormatName();
                    if (image == null || formatName.equals("")) {
                        HBaserUtils.message("不能保存！");
                        return;
                    }
                    JFileChooser fc = HBaserUtils.getJFileChooserWithLastPath();
                    fc.setFileFilter(new FileNameExtensionFilter("Image File("
                            + formatName + ")", formatName));
                    File file = HBaserUtils.getSelectedFile(fc, FileOp.Save);
                    if (file == null) {
                        return;
                    }

                    ImageIO.write((BufferedImage) image, formatName, file);
                    HBaserUtils.message("保存成功！");
                }
                catch (IOException ex) {
                    HBaserUtils.error(ex);
                }
            }
        });

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    jImagePopupMenu.show((JComponent) e.getSource(), e.getX(), e.getY());
                }
            }
        });

        this.btnQuery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tableName = getTableName(true);
                if (tableName == null) {
                    return;
                }

                HTableQuerys query = new HTableQuerys();
                QueryOption queryOption = new QueryOption();
                queryOption.setShowTimeStamp(chkTimeStamp.isSelected());
                queryOption.setShowLineNo(chkShowLineNo.isSelected());
                queryOption.setKeyOnlyFilter(chkKeyOnly.isSelected());
                queryOption.setRowLimit(textRowLimit.getText());

                query.queryTable(lockPair, tableName, textTableMeta
                        .getText().trim(),
                        textRowkey.getText().trim(),
                        textSelect.getText().trim(),
                        textWhere.getText().trim(),
                        textResult, queryOption, null);
            }
        });
    }

    public void setEnv(String env) {
        this.resetEnvCombo(env);

        if (env != null) {
            this.comboEnv.setSelectedItem(env);
            HBaserConfig.setConfig(HBaserConfig.loginDialog.ini.properties(env));
            HTableDdls.listTables(this.comboTables);

            try {
                this.extendIni.load(env);
                this.loadTableMeta();
            }
            catch (IOException ex) {
                HBaserUtils.error(ex);
            }
        }
    }

    String getTableName(boolean showMessage) {
        String tableName = (String) this.comboTables.getSelectedItem();
        if (tableName == null && showMessage) {
            HBaserUtils.message("请先选择表格!");

        }
        return tableName;
    }

    public void loadTableMeta() {
        String tableName = this.getTableName(false);
        if (tableName == null) {
            return;
        }

        String section = this.extendIni.getSection(tableName);
        this.textTableMeta.setText(section != null ? section : "");
    }

    public void saveTableMeta() {
        String tableName = this.getTableName(false);
        if (tableName == null) {
            return;
        }
        this.extendIni.setSection(tableName, this.textTableMeta.getText());
    }

    public void saveEnvMeta() {
        try {
            this.extendIni.save();
        }
        catch (Exception ex) {
            HBaserUtils.error(ex);
        }
    }

    private void resetEnvCombo(String env) {
        this.comboEnv.removeActionListener(this.comboEnvActionPerformed);
        this.comboEnv.removeAllItems();
        if (env == null) {
            this.comboEnv.addItem("请选择环境");
        }

        for (String sec : HBaserConfig.loginDialog.ini.sections()) {
            this.comboEnv.addItem(sec);
        }
        this.comboEnv.addItem("配置环境(config)");

        this.comboEnv.addActionListener(this.comboEnvActionPerformed);
    }

    Pair<HTable, RowLock> lockPair = null;

    void dispatchUpdateOperation(final JTextField textRowkey, final JTextPane textResult, String op,
            String tableName, String json) {

        if (op.equals("Lock Row")) {
            if (this.lockPair != null) {
                HBaserUtils.message("已经处在行锁定状态，该操作不支持！");
            }
            else {
                HTablePuts hTablePuts = new HTablePuts();
                Pair<HTable, RowLock> lockPair = hTablePuts.lockRow(tableName, this.textTableMeta.getText(), json,
                        textResult);
                if (lockPair != null) {
                    this.comboTables.setEnabled(false);
                    this.lockPair = lockPair;
                }
            }
        }
        else if (op.equals("UnLock Row")) {
            if (this.lockPair == null) {
                HBaserUtils.message("不处在行锁定状态，该操作不支持！");
            }
            else {
                HTablePuts hTablePuts = new HTablePuts();
                boolean ok = hTablePuts.unlockRow(tableName, this.lockPair, textResult);
                if (ok) {
                    this.comboTables.setEnabled(true);
                    this.lockPair = null;
                }
            }
        }
        else if (op.equals("Put values By Rowkey")) {
            HTablePuts tablePuts = new HTablePuts();
            tablePuts.putValuesByRowkey(this.lockPair, tableName, this.textTableMeta.getText(), json, textResult);
        }
        else if (op.equals("Update values By Rowkey")) {
            HTablePuts tablePuts = new HTablePuts();
            tablePuts.mergeValuesByRowkey(this.lockPair, tableName, this.textTableMeta.getText(), json, textResult,
                    false);
        }
        else if (op.equals("Insert row By Rowkey")) {
            HTablePuts tablePuts = new HTablePuts();
            tablePuts.mergeValuesByRowkey(this.lockPair, tableName, this.textTableMeta.getText(), json, textResult,
                    true);
        }
        else if (op.equals("Delete values By Rowkey")) {
            HTableDeletes hTableDeletes = new HTableDeletes();
            hTableDeletes
                    .deleteValuesByRowkey(this.lockPair, tableName, this.textTableMeta.getText(), json, textResult);
        }
        else if (this.lockPair != null) {
            HBaserUtils.message("目前处在行锁定状态，该操作不支持！");
        }
        else if (op.equals("Put values By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.putsByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(), json, textResult);
        }
        else if (op.equals("Increment By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.incrementByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(), json, textResult);
        }
        else if (op.equals("Delete values By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.deleteValuesByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(), json, textResult);
        }
        else if (op.equals("Delete rows By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.deleteRowsByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(), textResult);
        }

        else if (op.equals("Increment By Rowkey")) {
            HTablePuts tablePuts = new HTablePuts();
            tablePuts.increment(tableName, this.textTableMeta.getText(), json, textResult);
        }
        else if (op.equals("CAS Put By Rowkey")) {
            HTablePuts tablePuts = new HTablePuts();
            tablePuts.checkAndPutByRow(tableName, this.textTableMeta.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }

        else if (op.equals("Delete row By Rowkey")) {
            HTableDeletes hTableDeletes = new HTableDeletes();
            hTableDeletes.deleteRow(tableName, this.textTableMeta.getText(), json, textResult);
        }
        else if (op.equals("CAS Delete row By Rowkey")) {
            HTableDeletes hTableDeletes = new HTableDeletes();
            hTableDeletes.checkAndDeleteRowByRowkey(tableName, this.textTableMeta.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }
        else if (op.equals("CAS Delete values By Rowkey")) {
            HTableDeletes hTableDeletes = new HTableDeletes();
            hTableDeletes.checkAndDeleteValuesByRowkey(tableName, this.textTableMeta.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }
        else if (op.equals("CAS Put By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.checkAndPutsByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }
        else if (op.equals("CAS Delete values By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.checkAndDeleteValuesByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }
        else if (op.equals("CAS Delete rows By Query")) {
            HTableQuerys query = new HTableQuerys();
            query.checkAndDeleteRowsByQuery(tableName, this.textTableMeta.getText(),
                    textRowkey.getText(), this.textWhere.getText(),
                    this.textCheck.getText().trim(), json, textResult);
        }
    }
}
