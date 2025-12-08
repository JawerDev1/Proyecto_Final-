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

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);
    }

    private void cargarHistorial() {
        String contenido = Historial.cargar();
        areaTexto.setText(contenido);
    }
}
