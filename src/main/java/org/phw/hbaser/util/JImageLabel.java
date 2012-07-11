package org.phw.hbaser.util;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JLabel;

public class JImageLabel extends JLabel {

    private static final long serialVersionUID = -6558485501793496531L;

    private Image image;
    private String imageFormatName;

    public JImageLabel(Image image, String imageFormatName) {
        this.image = image;
        this.imageFormatName = imageFormatName;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setImageFormatName(String imageFormatName) {
        this.imageFormatName = imageFormatName;
    }

    public String getImageFormatName() {
        return imageFormatName;
    }

}
