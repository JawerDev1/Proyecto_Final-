package view;

import controller.ControladorJuego;
import utils.GuardarPartida;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class MenuPrincipal extends JFrame {

    private int indiceSeleccionado = 0;
    private String[] opciones = {
            "Iniciar Batalla",
            "Cargar Partida",
            "Historial de Batallas",
            "Gremio de Aventureros",
            "Salir"
    };

    public MenuPrincipal() {

        // ================================================
        // CONFIGURACIÓN DE LA VENTANA
        // ================================================
        setTitle("DRAGON QUEST");
        setSize(600, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        BackgroundPanel fondo = new BackgroundPanel("img/menu1.jpg");
        setContentPane(fondo);


        MenuPanel menuPanel = new MenuPanel();
        menuPanel.setOpaque(false);
        setLayout(null);

        menuPanel.setBounds(180, 120, 350, 300); 
        add(menuPanel);

        configurarControles(menuPanel);

        setVisible(true);
    }

    // ================================================
    // PANEL QUE DIBUJA EL MENÚ TIPO MARIO
    // ================================================
    private class MenuPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setFont(new Font("Press Start 2P", Font.PLAIN, 22));

            for (int i = 0; i < opciones.length; i++) {

                int y = 40 + (i * 50);

                // Sombra negra
                g.setColor(Color.BLACK);
                g.drawString(opciones[i], 42, y + 2);

                // Texto principal
                if (i == indiceSeleccionado)
                    g.setColor(Color.YELLOW);
                else
                    g.setColor(Color.WHITE);

                g.drawString(opciones[i], 40, y);
            }

        }
    }

    // ================================================
    // CONTROLES DEL MENU CON LAS FLECHAS
    // ================================================
    private void configurarControles(JPanel panel) {

        panel.setFocusable(true);
        panel.requestFocusInWindow();

        panel.addKeyListener(new java.awt.event.KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    indiceSeleccionado = (indiceSeleccionado + 1) % opciones.length;
                    panel.repaint();
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    indiceSeleccionado = (indiceSeleccionado - 1 + opciones.length) % opciones.length;
                    panel.repaint();
                }

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ejecutarOpcion();
                }
            }
        });
    }

    // ================================================
    // ACCIONES DEL MENU
    // ================================================
    private void ejecutarOpcion() {

        switch (indiceSeleccionado) {

            case 0:
                new InterfazJuego().setVisible(true);
                dispose();
                break;

            case 1:
                mostrarVentanaCargar();
                break;

            case 2:
                new VentanaHistorial().setVisible(true);
                break;

            case 3:
                new VentanaGremio().setVisible(true);
                break;

            case 4:
                System.exit(0);
                break;
        }
    }

    // ================================================
    // CARGAR PARTIDA (NO SE TOCA)
    // ================================================
    private void mostrarVentanaCargar() {

        JDialog d = new JDialog(this, "Cargar Partida", true);
        d.setSize(350, 300);
        d.setLayout(new BorderLayout());

        DefaultListModel<String> modelo = new DefaultListModel<>();
        JList<String> lista = new JList<>(modelo);

        File carpeta = new File(GuardarPartida.RUTA_CARPETA);
        if (!carpeta.exists())
            carpeta.mkdirs();

        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".sav"));
        if (archivos != null) {
            for (File f : archivos)
                modelo.addElement(f.getName());
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
                JOptionPane.showMessageDialog(d, "Selecciona una partida.");
                return;
            }

            GuardarPartida.PartidaCargada partida = GuardarPartida.cargar(seleccionado);

            if (partida == null) {
                JOptionPane.showMessageDialog(d, "Error al cargar partida.");
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
