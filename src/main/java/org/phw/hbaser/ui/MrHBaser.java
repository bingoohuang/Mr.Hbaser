package org.phw.hbaser.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;

import org.phw.hbaser.util.HBaserConfig;

public class MrHBaser {

    JFrame frmMrHbaser;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MrHBaser window = new MrHBaser();
                    window.frmMrHbaser.setVisible(true);
                    window.panel.setDividerLocation();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     * @throws IOException 
     */
    public MrHBaser() throws IOException {
        this.initialize();
    }

    final HBaserQueryPanel panel = new HBaserQueryPanel(frmMrHbaser);

    /**
     * Initialize the contents of the frame.
     * @throws IOException 
     */
    private void initialize() throws IOException {
        this.frmMrHbaser = new JFrame();
        this.frmMrHbaser.setIconImage(Toolkit.getDefaultToolkit().getImage(
                MrHBaser.class.getResource("/org/phw/hbaser/ui/hbase-logo.ico")));
        this.frmMrHbaser.setTitle("Mr. HBaser（0.2.14 For HBase v0.92.1）");
        this.frmMrHbaser.setBounds(100, 100, 742, 597);
        this.frmMrHbaser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        HBaserConfig.loginDialog.setHBaserQueryPanel(this.panel);
        this.panel.setEnv(null);
        this.frmMrHbaser.getContentPane().add(this.panel, BorderLayout.CENTER);

        this.frmMrHbaser.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MrHBaser.this.panel.saveEnvMeta();
            }
        });

        this.frmMrHbaser.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                MrHBaser.this.panel.setDividerLocation();
            }
        });
    }

}
