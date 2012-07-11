package org.phw.hbaser.ui;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

public class JImageDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -3686487158330970054L;
    final private JButton btnOK = new JButton("确定");
    final private JButton btnCancel = new JButton("取消");
    final private JComboBox comboCacheIDs = new JComboBox();
    private String selectedCacheID = "";

    public JImageDialog(Frame owner, boolean modal, Set<String> cacheIDSet) {
        super(owner, "选择要显示的图片", modal);
        comboCacheIDs.removeAllItems();
        comboCacheIDs.addItem("请选择要显示的图片Cache");
        for (String cacheID : cacheIDSet) {
            comboCacheIDs.addItem(cacheID);
        }
        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);
        setLayout(new FlowLayout());
        add(comboCacheIDs);
        add(btnOK);
        add(btnCancel);
        setSize(200, 100);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }
        });
    }

    public String getSelectedCacheID() {
        return selectedCacheID;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOK) {
            if (((String) comboCacheIDs.getSelectedItem()).equals("请选择要显示的图片Cache")) {
                selectedCacheID = "";
            }
            else {
                selectedCacheID = (String) comboCacheIDs.getSelectedItem();
            }
        }
        else if (e.getSource() == btnCancel) {
            selectedCacheID = "";
        }
        setVisible(false);
    }

}
