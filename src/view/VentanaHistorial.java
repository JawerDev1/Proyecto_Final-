package view;

import utils.Historial;

import javax.swing.*;
import java.awt.*;

public class VentanaHistorial extends JFrame {

    private JTextArea areaTexto;

    public VentanaHistorial() {

        setTitle("Historial de Batallas");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        inicializar();
        cargarHistorial();
    }

    private void inicializar() {

        // Usamos BackgroundPanel para la imagen de fondo
        BackgroundPanel panelFondo = new BackgroundPanel("/img/history.png");
        panelFondo.setLayout(new BorderLayout());
        add(panelFondo);

        // JTextArea
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaTexto.setOpaque(false); // Para que se vea el fondo
        areaTexto.setForeground(Color.BLACK); // Color del texto

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false); // Hace transparente el viewport del JScrollPane

        panelFondo.add(scroll, BorderLayout.CENTER);
    }

    private void cargarHistorial() {
        String contenido = Historial.cargar();
        areaTexto.setText(contenido);
    }
}
