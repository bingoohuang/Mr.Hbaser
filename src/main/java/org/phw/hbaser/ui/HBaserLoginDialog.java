package org.phw.hbaser.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.phw.core.lang.Ini;
import org.phw.core.lang.Ios;

public class HBaserLoginDialog extends JDialog {
    private static final long serialVersionUID = 892963433521975933L;
    private static final String CONFIG_FILE = "hbasersetting.ini";
    private final JPanel contentPanel = new JPanel();
    public Ini ini;
    final JTextPane txtSetting = new JTextPane();
    final DefaultListModel listModel = new DefaultListModel();
    final JList listLogins = new JList(listModel);

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            final HBaserLoginDialog dialog = new HBaserLoginDialog(null);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSetting() {
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(CONFIG_FILE), "UTF-8");
            out.write(txtSetting.getText());

        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(getParent(), "Fail:" + ex.getMessage());
        }
        finally {
            Ios.closeQuietly(out);
        }

        loadConfigList();
    }

    void loadConfigList() {
        ini = new Ini(Ios.toInputStream(txtSetting.getText()));
        listModel.clear();
        Set<String> sections = ini.sections();
        for (String section : sections) {
            listModel.addElement(section);
        }
    }

    public void loadSetting() {
        if (new File(CONFIG_FILE).exists()) {
            try {
                txtSetting.setText(Ios.readFile(CONFIG_FILE));
            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(getParent(), "Fail:" + ex.getMessage());
            }
        }
        else {
            txtSetting.setText("[LOCAL]\n" +
                    "hbase.zookeeper.quorum=127.0.0.1\n" +
                    "hbase.zookeeper.property.clientPort=2181\n\n" +
                    "[BEIJING]\n" +
                    "hbase.zookeeper.quorum=132.35.81.218\n" +
                    "hbase.zookeeper.property.clientPort=2181\n\n");
        }
        loadConfigList();
    }

    /**
     * Create the dialog.
     * @param parent 
     */
    public HBaserLoginDialog(Frame parent) {
        super(parent, true);
        loadSetting();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                // Nothing TO DO.
            }

            @Override
            public void windowClosing(WindowEvent e) {
                saveSetting();
            }
        });

        setTitle("Choose HBaser Server");
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
            contentPanel.add(tabbedPane, BorderLayout.CENTER);
            {
                JPanel panel = new JPanel();
                tabbedPane.addTab("Login", null, panel, null);
                GridBagLayout gbl_panel = new GridBagLayout();
                gbl_panel.columnWidths = new int[] { 0, 0 };
                gbl_panel.rowHeights = new int[] { 0, 0, 0 };
                gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
                panel.setLayout(gbl_panel);
                {
                    JPanel panel_1 = new JPanel();
                    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
                    gbc_panel_1.insets = new Insets(0, 0, 5, 0);
                    gbc_panel_1.fill = GridBagConstraints.BOTH;
                    gbc_panel_1.gridx = 0;
                    gbc_panel_1.gridy = 0;
                    panel.add(panel_1, gbc_panel_1);
                    GridBagLayout gbl_panel_1 = new GridBagLayout();
                    gbl_panel_1.columnWidths = new int[] { 1, 0 };
                    gbl_panel_1.rowHeights = new int[] { 1, 0 };
                    gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                    gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                    panel_1.setLayout(gbl_panel_1);

                    listLogins.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                int index = listLogins.locationToIndex(e.getPoint());
                                listLogins.setSelectedIndex(index);
                                login();
                            }
                        }
                    });
                    {

                        listLogins.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        GridBagConstraints gbc_listLogins = new GridBagConstraints();
                        gbc_listLogins.fill = GridBagConstraints.BOTH;
                        gbc_listLogins.gridx = 0;
                        gbc_listLogins.gridy = 0;
                        panel_1.add(listLogins, gbc_listLogins);
                    }
                }
                {
                    JPanel panel_1 = new JPanel();
                    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
                    gbc_panel_1.fill = GridBagConstraints.BOTH;
                    gbc_panel_1.gridx = 0;
                    gbc_panel_1.gridy = 1;
                    panel.add(panel_1, gbc_panel_1);
                    GridBagLayout gbl_panel_1 = new GridBagLayout();
                    gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
                    gbl_panel_1.rowHeights = new int[] { 0, 0 };
                    gbl_panel_1.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
                    gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                    panel_1.setLayout(gbl_panel_1);
                    {
                        JButton btnLogin = new JButton("Login");
                        btnLogin.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (listLogins.getSelectedValue() == null) {
                                    JOptionPane.showMessageDialog(null, "Please select login environment!");
                                    return;
                                }

                                login();
                            }
                        });
                        GridBagConstraints gbc_btnLogin = new GridBagConstraints();
                        gbc_btnLogin.anchor = GridBagConstraints.EAST;
                        gbc_btnLogin.insets = new Insets(0, 0, 0, 5);
                        gbc_btnLogin.gridx = 0;
                        gbc_btnLogin.gridy = 0;
                        panel_1.add(btnLogin, gbc_btnLogin);
                    }
                    {
                        JButton btnCancel = new JButton("Cancel");
                        btnCancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setVisible(false);
                            }
                        });
                        GridBagConstraints gbc_btnCancel = new GridBagConstraints();
                        gbc_btnCancel.anchor = GridBagConstraints.WEST;
                        gbc_btnCancel.gridx = 1;
                        gbc_btnCancel.gridy = 0;
                        panel_1.add(btnCancel, gbc_btnCancel);
                    }
                }
            }
            {
                JPanel panel = new JPanel();
                tabbedPane.addTab("Setting", null, panel, null);
                GridBagLayout gbl_panel = new GridBagLayout();
                gbl_panel.columnWidths = new int[] { 0, 0 };
                gbl_panel.rowHeights = new int[] { 0, 0, 0 };
                gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
                panel.setLayout(gbl_panel);
                {
                    JPanel panel_1 = new JPanel();
                    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
                    gbc_panel_1.insets = new Insets(0, 0, 5, 0);
                    gbc_panel_1.fill = GridBagConstraints.BOTH;
                    gbc_panel_1.gridx = 0;
                    gbc_panel_1.gridy = 0;
                    panel.add(panel_1, gbc_panel_1);
                    GridBagLayout gbl_panel_1 = new GridBagLayout();
                    gbl_panel_1.columnWidths = new int[] { 0, 0 };
                    gbl_panel_1.rowHeights = new int[] { 0, 0 };
                    gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                    gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                    panel_1.setLayout(gbl_panel_1);
                    {
                        JScrollPane scrollPane = new JScrollPane();
                        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
                        gbc_scrollPane.fill = GridBagConstraints.BOTH;
                        gbc_scrollPane.gridx = 0;
                        gbc_scrollPane.gridy = 0;
                        panel_1.add(scrollPane, gbc_scrollPane);
                        {
                            scrollPane.setViewportView(txtSetting);
                        }
                    }
                }
                {
                    JPanel panel_1 = new JPanel();
                    GridBagConstraints gbc_panel_1 = new GridBagConstraints();
                    gbc_panel_1.fill = GridBagConstraints.BOTH;
                    gbc_panel_1.gridx = 0;
                    gbc_panel_1.gridy = 1;
                    panel.add(panel_1, gbc_panel_1);
                    GridBagLayout gbl_panel_1 = new GridBagLayout();
                    gbl_panel_1.columnWidths = new int[] { 0, 0 };
                    gbl_panel_1.rowHeights = new int[] { 0, 0 };
                    gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                    gbl_panel_1.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                    panel_1.setLayout(gbl_panel_1);
                    {
                        JButton btnSave = new JButton("Save");
                        btnSave.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                loadConfigList();
                            }
                        });
                        GridBagConstraints gbc_btnSave = new GridBagConstraints();
                        gbc_btnSave.gridx = 0;
                        gbc_btnSave.gridy = 0;
                        panel_1.add(btnSave, gbc_btnSave);
                    }
                }
            }
        }
    }

    public String getEnv() {
        String env = (String) listLogins.getSelectedValue();
        return env == null ? "Not Logon" : env;
    }

    private HBaserQueryPanel queryPanel;

    public void setHBaserQueryPanel(HBaserQueryPanel panel) {
        queryPanel = panel;
    }

    void login() {
        queryPanel.setEnv(getEnv());
        setVisible(false);
    }
}
