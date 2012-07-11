package org.phw.hbaser.util;

import static org.phw.hbaser.util.JTextPaneStyle.FAMATTR;
import static org.phw.hbaser.util.JTextPaneStyle.REDATTR;
import static org.phw.hbaser.util.JTextPaneStyle.TIPATTR;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import org.phw.core.lang.Dates;

public class Watcher {
    private long start;
    private StyledDocument doc;
    private JTextPane pane;

    public Watcher(JTextPane pane, String info) {
        this.pane = pane;
        doc = pane == null ? null : pane.getStyledDocument();
        start = System.currentTimeMillis();
        try {
            if (doc != null) {
                doc.insertString(doc.getLength(), Dates.format(start) + " 开始", FAMATTR);
                doc.insertString(doc.getLength(), info + "\n", TIPATTR);
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void error(Exception e) {
        stopWatchWithError("出错" + e.toString());
        JOptionPane.showMessageDialog(null, "出错" + e.toString());
    }

    public void stopWatch(String info) {
        stopWatch(info, TIPATTR);
    }

    public void stopWatchWithError(String info) {
        stopWatch(info, REDATTR);
    }

    public void stopWatch(String info, SimpleAttributeSet attr) {
        if (doc == null) {
            return;
        }
        long end = System.currentTimeMillis();
        try {
            doc.insertString(doc.getLength(), Dates.format(end) + " 用时" + (end - start) / 1000. + "秒。 ", FAMATTR);
            doc.insertString(doc.getLength(), info + "\n\n", attr);
            pane.setCaretPosition(doc.getLength());
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void watch(String info) {
        if (doc == null) {
            return;
        }
        long end = System.currentTimeMillis();
        try {
            doc.insertString(doc.getLength(), Dates.format(end), FAMATTR);
            doc.insertString(doc.getLength(), " " + info + "\n", TIPATTR);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }

    }
}
