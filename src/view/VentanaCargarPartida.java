package view;

import controller.ControladorJuego;
import utils.GuardarPartida;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VentanaCargarPartida extends JDialog {

    private JList<String> listaPartidas;
    private DefaultListModel<String> modeloLista;

    public VentanaCargarPartida(JFrame parent) {
        super(parent, "Cargar Partida", true);

        setSize(400, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        modeloLista = new DefaultListModel<>();
        listaPartidas = new JList<>(modeloLista);

        cargarPartidasDisponibles();

        JScrollPane scroll = new JScrollPane(listaPartidas);
        add(scroll, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnCargar = new JButton("Cargar");
        JButton btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnCargar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());

        btnCargar.addActionListener(e -> cargarSeleccionada());
    }

    // ============================================================
    // CARGAR ARCHIVOS DE PARTIDA DESDE /partidas
    // ============================================================
    private void cargarPartidasDisponibles() {

        File carpeta = new File("partidas");

        if (!carpeta.exists()) carpeta.mkdirs();

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));

        if (archivos != null) {
            for (File f : archivos) {
                modeloLista.addElement(f.getName());
            }
        }
    }

    // ============================================================
    // CARGAR PARTIDA SELECCIONADA
    // ============================================================
    private void cargarSeleccionada() {

        String sel = listaPartidas.getSelectedValue();

        if (sel == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una partida.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        GuardarPartida.PartidaCargada partida = GuardarPartida.cargar(sel);

        if (partida == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo cargar la partida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        ControladorJuego ctrl = partida.getControlador();
        int turno = partida.getTurno();

        // Abrir interfaz del juego con la partida restaurada
        new InterfazJuego(ctrl, turno).setVisible(true);

        dispose(); // cerrar esta ventana
    }
}
