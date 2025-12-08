package view;

import model.Aventurero;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;

public class VentanaGremio extends JFrame {

    private Queue<Aventurero> colaAventureros = new LinkedList<>();
    private JTextArea areaTexto;

    public VentanaGremio() {

        setTitle("Gremio de Aventureros");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton btnRegistrar = new JButton("Registrar aventurero");
        JButton btnAtender = new JButton("Atender siguiente");
        JButton btnVerSiguiente = new JButton("Ver siguiente");
        JButton btnVerCola = new JButton("Ver cola completa");
        JButton btnVaciar = new JButton("Vaciar cola");
        JButton btnSalir = new JButton("Cerrar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnAtender);
        panelBotones.add(btnVerSiguiente);
        panelBotones.add(btnVerCola);
        panelBotones.add(btnVaciar);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> registrarAventurero());
        btnAtender.addActionListener(e -> atenderSiguiente());
        btnVerSiguiente.addActionListener(e -> mostrarSiguiente());
        btnVerCola.addActionListener(e -> mostrarCola());
        btnVaciar.addActionListener(e -> vaciarCola());
        btnSalir.addActionListener(e -> dispose());
    }

    private void registrarAventurero() {
        JTextField nombreField = new JTextField();
        JTextField nivelField = new JTextField();

        Object[] mensaje = {
                "Nombre del aventurero:", nombreField,
                "Nivel del aventurero:", nivelField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar aventurero",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                int nivel = Integer.parseInt(nivelField.getText().trim());

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio.");
                    return;
                }

                Aventurero av = new Aventurero(nombre, nivel);
                colaAventureros.add(av);

                areaTexto.append("Registrado: " + av + "\n");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nivel invalido.");
            }
        }
    }

    private void atenderSiguiente() {
        Aventurero atendido = colaAventureros.poll();

        if (atendido == null) {
            areaTexto.append("No hay aventureros en espera.\n");
        } else {
            areaTexto.append("Atendiendo a: " + atendido + "\n");
        }
    }

    private void mostrarSiguiente() {
        Aventurero siguiente = colaAventureros.peek();

        if (siguiente == null) {
            areaTexto.append("No hay aventureros en la fila.\n");
        } else {
            areaTexto.append("Siguiente en turno: " + siguiente + "\n");
        }
    }

    private void mostrarCola() {
        if (colaAventureros.isEmpty()) {
            areaTexto.append("La cola esta vacia.\n");
        } else {
            areaTexto.append("Aventureros en espera:\n");
            for (Aventurero av : colaAventureros) {
                areaTexto.append("- " + av + "\n");
            }
        }
    }

    private void vaciarCola() {
        colaAventureros.clear();
        areaTexto.append("La cola ha sido vaciada.\n");
    }
}
