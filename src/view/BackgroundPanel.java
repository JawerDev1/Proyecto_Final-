package view;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String rutaImagen) {
        background = new ImageIcon(rutaImagen).getImage();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}