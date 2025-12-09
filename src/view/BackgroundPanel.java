package view;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {

    private Image background;

    public BackgroundPanel(String rutaImagen) {
        // Cargar imagen desde recursos dentro de src
        background = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
    }
}
