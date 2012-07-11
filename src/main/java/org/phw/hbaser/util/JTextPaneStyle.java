package org.phw.hbaser.util;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class JTextPaneStyle {

    public static final SimpleAttributeSet KEYWORDATTR = new SimpleAttributeSet();
    public static final SimpleAttributeSet REDATTR = new SimpleAttributeSet();
    public static final SimpleAttributeSet FAMATTR = new SimpleAttributeSet();
    public static final SimpleAttributeSet LINEATTR = new SimpleAttributeSet();
    public static final SimpleAttributeSet TIPATTR = new SimpleAttributeSet();
    static {
        StyleConstants.setForeground(KEYWORDATTR, Color.BLUE);
        // StyleConstants.setBackground(KEYWORDATTR, Color.YELLOW);

        StyleConstants.setForeground(REDATTR, Color.RED);
        // StyleConstants.setBold(RESYULTATTR, true);

        StyleConstants.setForeground(FAMATTR, Color.ORANGE);
        // StyleConstants.setBold(FAMATTR, true);

        StyleConstants.setForeground(LINEATTR, Color.DARK_GRAY);
        StyleConstants.setForeground(TIPATTR, Color.GRAY);
    }
}
