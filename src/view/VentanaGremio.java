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
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ===== FONDO GENERAL =====
        BackgroundPanel fondo = new BackgroundPanel("/img/gremio.png"); // Ajusta la ruta si es necesario
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        // ===== TÍTULO PRINCIPAL =====
        JLabel titulo = new JLabel("GREMIO DE AVENTUREROS");
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(Color.WHITE);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        add(titulo, BorderLayout.NORTH);

        // ===== ÁREA DE TEXTO =====
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 15));
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setOpaque(false);

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 180), 2, true),
                "Registro del Gremio",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                Color.WHITE
        ));

        scroll.setPreferredSize(new Dimension(580, 300));
        add(scroll, BorderLayout.CENTER);

        // ===== PANEL DE BOTONES (semitransparente) =====
        JPanel panelBotones = new JPanel(new GridLayout(3, 2, 12, 12));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnRegistrar    = crearBotonPrimario("Registrar Aventurero");
        JButton btnAtender      = crearBotonSecundario("Atender Siguiente");
        JButton btnVerSiguiente = crearBotonSecundario("Ver Siguiente");
        JButton btnVerCola      = crearBotonSecundario("Ver Cola Completa");
        JButton btnVaciar       = crearBotonPeligro("Vaciar Cola");
        JButton btnSalir        = crearBotonPeligro("Cerrar");

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnAtender);
        panelBotones.add(btnVerSiguiente);
        panelBotones.add(btnVerCola);
        panelBotones.add(btnVaciar);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);

        // ===== EVENTOS =====
        btnRegistrar.addActionListener(e -> registrarAventurero());
        btnAtender.addActionListener(e -> atenderSiguiente());
        btnVerSiguiente.addActionListener(e -> mostrarSiguiente());
        btnVerCola.addActionListener(e -> mostrarCola());
        btnVaciar.addActionListener(e -> vaciarCola());
        btnSalir.addActionListener(e -> dispose());
    }

    // ============================================
    //        BOTONES ESTILIZADOS
    // ============================================

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(50, 120, 220, 230));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setOpaque(true);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 100, 200, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 120, 220, 230));
            }
        });

        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(255, 255, 255, 180));
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(240, 240, 240, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 255, 255, 180));
            }
        });

        return btn;
    }

    private JButton crearBotonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(200, 40, 40, 230));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btn.setFocusPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(170, 25, 25, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 40, 40, 230));
            }
        });

        return btn;
    }

    // ===================================
    //        LÓGICA DEL GREMIO
    // ===================================

    private void registrarAventurero() {
        JTextField nombreField = new JTextField();
        JTextField nivelField = new JTextField();

        Object[] mensaje = {
                "Nombre del aventurero:", nombreField,
                "Nivel del aventurero:", nivelField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Aventurero",
                JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = nombreField.getText().trim();
                int nivel = Integer.parseInt(nivelField.getText().trim());

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.");
                    return;
                }

                Aventurero av = new Aventurero(nombre, nivel);
                colaAventureros.add(av);

                areaTexto.append("Registrado: " + av + "\n");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nivel inválido.");
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
            areaTexto.append("La cola está vacía.\n");
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
