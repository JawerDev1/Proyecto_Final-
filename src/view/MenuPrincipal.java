package view;

import controller.ControladorJuego;
import utils.GuardarPartida;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {

        setTitle("DRAGON QUEST");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(25, 25, 112));

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(25, 25, 112));
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JLabel titulo = new JLabel("DRAGON QUEST");
        titulo.setFont(new Font("Serif", Font.BOLD, 32));
        titulo.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(titulo, gbc);

        JButton btnIniciar = new JButton("Iniciar Batalla");
        btnIniciar.setPreferredSize(new Dimension(230, 45));
        btnIniciar.setBackground(new Color(72, 61, 139));
        btnIniciar.setForeground(Color.WHITE);

        gbc.gridy = 1;
        panel.add(btnIniciar, gbc);

        JButton btnCargar = new JButton("Cargar Partida");
        btnCargar.setPreferredSize(new Dimension(230, 45));
        btnCargar.setBackground(new Color(72, 61, 139));
        btnCargar.setForeground(Color.WHITE);

        gbc.gridy = 2;
        panel.add(btnCargar, gbc);

        JButton btnHistorial = new JButton("Historial de Batallas");
        btnHistorial.setPreferredSize(new Dimension(230, 45));
        btnHistorial.setBackground(new Color(72, 61, 139));
        btnHistorial.setForeground(Color.WHITE);

        gbc.gridy = 3;
        panel.add(btnHistorial, gbc);

        JButton btnGremio = new JButton("Gremio de Aventureros");
        btnGremio.setPreferredSize(new Dimension(230, 45));
        btnGremio.setBackground(new Color(72, 61, 139));
        btnGremio.setForeground(Color.WHITE);

        gbc.gridy = 4;
        panel.add(btnGremio, gbc);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setPreferredSize(new Dimension(230, 45));
        btnSalir.setBackground(new Color(72, 61, 139));
        btnSalir.setForeground(Color.WHITE);

        gbc.gridy = 5;
        panel.add(btnSalir, gbc);

        // Nueva partida
        btnIniciar.addActionListener(e -> {
            new InterfazJuego().setVisible(true);
            dispose();
        });

        // Cargar partida
        btnCargar.addActionListener(e -> mostrarVentanaCargar());

        // Historial
        btnHistorial.addActionListener(e -> new VentanaHistorial().setVisible(true));

        // Gremio
        btnGremio.addActionListener(e -> new VentanaGremio().setVisible(true));

        // Salir
        btnSalir.addActionListener(e -> System.exit(0));
    }

    private void mostrarVentanaCargar() {

        JDialog d = new JDialog(this, "Cargar Partida", true);
        d.setSize(350, 300);
        d.setLayout(new BorderLayout());

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = new JList<>(modelo);

        File carpeta = new File(GuardarPartida.RUTA_CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".sav"));
        if (archivos != null) {
            for (File f : archivos) {
                modelo.addElement(f.getName());
            }
        }

        d.add(new JScrollPane(lista), BorderLayout.CENTER);

        JButton btnCargar = new JButton("Cargar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel p = new JPanel();
        p.add(btnCargar);
        p.add(btnCancelar);

        d.add(p, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> {
            String seleccionado = lista.getSelectedValue();
            if (seleccionado == null) {
                JOptionPane.showMessageDialog(d,
                        "Selecciona una partida.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            GuardarPartida.PartidaCargada partida = GuardarPartida.cargar(seleccionado);
            if (partida == null) {
                JOptionPane.showMessageDialog(d,
                        "No se pudo cargar la partida.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ControladorJuego ctrl = partida.getControlador();
            int turno = partida.getTurno();

            new InterfazJuego(ctrl, turno).setVisible(true);
            d.dispose();
            dispose();
        });

        btnCancelar.addActionListener(e -> d.dispose());

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }
}
