package view;

import controller.ControladorJuego;
import model.*;
import utils.GuardarPartida;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class InterfazJuego extends JFrame {

    private JTextArea areaTexto;
    private JButton btnAtacar, btnHabilidad, btnDefender, btnObjeto,
            btnDeshacer, btnRehacer, btnGuardar;

    private JComboBox<String> listaEnemigos = new JComboBox<>();

    private ControladorJuego controlador;

    private ArrayList<Jugador> heroes;
    private ArrayList<Enemigo> enemigos;
    private ArrayList<Enemigo> enemigosVivos = new ArrayList<>();

    private int indiceHeroeActual = 0;

    private BackgroundPanel panelCampoBatalla;

    private Musica musicaInicio = new Musica();

    private void iniciarMusicaBatalla() {
        musicaInicio.reproducirLoop("/sonidos/intro.wav");
    }

    private void detenerMusica() {
        musicaInicio.parar();
    }

    // NUEVA PARTIDA
    public InterfazJuego() {

        setTitle("DRAGON QUEST - BATALLA");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarDatos();
        inicializarInterfaz();

        actualizarListaEnemigos();
        actualizarBotonesEnemigos();
        actualizarTurnoActual();

        iniciarMusicaBatalla();
    }

    // PARTIDA CARGADA
    public InterfazJuego(ControladorJuego controlador, int turnoInicial) {

        this.controlador = controlador;
        this.heroes = controlador.getHeroes();
        this.enemigos = controlador.getEnemigos();
        this.indiceHeroeActual = turnoInicial;

        setTitle("DRAGON QUEST - BATALLA");
        setSize(1100, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        inicializarInterfaz();
        actualizarListaEnemigos();
        actualizarBotonesEnemigos();
        actualizarTurnoActual();

        iniciarMusicaBatalla();
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(50, 120, 220));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Efecto hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(30, 100, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(50, 120, 220));
            }
        });
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(new Color(230, 230, 230));
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(210, 210, 210));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
        });

        return btn;
    }

    private JButton crearBotonPeligro(String texto) {
        JButton btn = new JButton(texto);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(new Color(200, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(170, 30, 30));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(200, 50, 50));
            }
        });

        return btn;
    }

    // DATOS INICIALES
    private void inicializarDatos() {

        heroes = new ArrayList<>();
        enemigos = new ArrayList<>();

        heroes.add(new Jugador(TipoHeroe.HEROE, "Heroe", 45, 8, 8, 6, 6));
        heroes.add(new Jugador(TipoHeroe.YANGUS, "Yangus", 50, 10, 10, 8, 3));
        heroes.add(new Jugador(TipoHeroe.YESSICA, "Jessica", 40, 14, 7, 5, 7));
        heroes.add(new Jugador(TipoHeroe.ANGELO, "Angelo", 42, 12, 8, 6, 6));

        enemigos.add(new Enemigo(TipoEnemigo.SPIKED_HARE, "Spiked Hare", 30, 6, 6, 5, 7));
        enemigos.add(new Enemigo(TipoEnemigo.VENOM_SLIME, "Venom Slime", 38, 10, 8, 6, 5));
        enemigos.add(new Enemigo(TipoEnemigo.PATYPUNK, "PatyPunk", 40, 4, 9, 7, 5));
        enemigos.add(new Enemigo(TipoEnemigo.TERROR_TABBY, "Terror Tabby", 42, 8, 9, 6, 7));

        controlador = new ControladorJuego(heroes, enemigos);
    }

    // INTERFAZ
    private void inicializarInterfaz() {

        JPanel panelCentral = new JPanel(new BorderLayout());

        // Campo de batalla
        panelCampoBatalla = new BackgroundPanel("/img/campo-batalla.png");
        panelCampoBatalla.setPreferredSize(new Dimension(900, 350));
        panelCampoBatalla.setBorder(BorderFactory.createTitledBorder("Campo de batalla"));
        panelCampoBatalla.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 30));
        panelCentral.add(panelCampoBatalla, BorderLayout.NORTH);

        // ======= CONSOLA =======
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 15));
        areaTexto.setBackground(new Color(250, 250, 245));
        areaTexto.setForeground(new Color(60, 60, 60));
        areaTexto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(900, 180));
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                "Registro de batalla"));

        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.setBackground(new Color(240, 240, 240));
        panelConsola.add(scroll, BorderLayout.CENTER);

        panelCentral.add(panelConsola, BorderLayout.SOUTH);
        add(panelCentral, BorderLayout.CENTER);

        // ======= PANEL DERECHO =======
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new GridLayout(10, 1, 5, 10));
        panelDerecho.setPreferredSize(new Dimension(180, 600));
        panelDerecho.setBackground(new Color(245, 245, 245));
        panelDerecho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(160, 160, 160), 2, true),
                "Opciones"));

        // Botones del panel derecho
        btnDeshacer = crearBotonSecundario("DESHACER");
        btnRehacer = crearBotonSecundario("REHACER");
        btnGuardar = crearBotonSecundario("GUARDAR");
        JButton btnSalir = crearBotonPeligro("SALIR");

        panelDerecho.add(btnDeshacer);
        panelDerecho.add(btnRehacer);
        panelDerecho.add(btnGuardar);
        panelDerecho.add(btnSalir);

        add(panelDerecho, BorderLayout.EAST);

        // ======= PANEL DE ACCIONES =======
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panelAcciones.setPreferredSize(new Dimension(900, 80));
        panelAcciones.setBackground(new Color(250, 250, 250));
        panelAcciones.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                "Acciones"));

        // Botones principales
        btnAtacar = crearBotonPrincipal("ATACAR");
        btnDefender = crearBotonPrincipal("DEFENDER");
        btnHabilidad = crearBotonPrincipal("HABILIDAD");
        btnObjeto = crearBotonPrincipal("OBJETO");

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnDefender);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnObjeto);

        add(panelAcciones, BorderLayout.SOUTH);

        // ======= EVENTOS =======
        btnAtacar.addActionListener(e -> realizarAtaque(false));
        btnHabilidad.addActionListener(e -> realizarAtaque(true));
        btnDefender.addActionListener(e -> manejarDefensa());
        btnObjeto.addActionListener(e -> mostrarMenuObjetos());
        btnDeshacer.addActionListener(e -> manejarDeshacer());
        btnRehacer.addActionListener(e -> manejarRehacer());
        btnGuardar.addActionListener(e -> guardarPartida());

        btnSalir.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(
                    this,
                    "¿Quieres guardar antes de salir?",
                    "Guardar y salir",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (op == JOptionPane.CANCEL_OPTION)
                return;
            if (op == JOptionPane.YES_OPTION)
                guardarPartida();

            detenerMusica();
            System.exit(0);
        });

    }

    // BOTONES ENEMIGOS
    private void actualizarBotonesEnemigos() {

        panelCampoBatalla.removeAll();
        enemigosVivos.clear();

        int idxVisible = 0;

        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {

                enemigosVivos.add(e);
                final int index = idxVisible;

                String ruta = obtenerRutaImagenEnemigo(e.getTipoEnemigo());

                ImageIcon icono = null;
                try {
                    icono = new ImageIcon(getClass().getResource(ruta));

                    Image imagenEscalada = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    icono = new ImageIcon(imagenEscalada);
                } catch (Exception ex) {
                    System.err.println("Error al cargar imagen para " + e.getNombre() + ": " + ex.getMessage());
                }

                JButton btn = new JButton("<html>HP: " + e.getHp() + "</html>", icono);

                btn.setVerticalTextPosition(SwingConstants.BOTTOM);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);

                btn.setPreferredSize(new Dimension(130, 130));

                btn.addActionListener(ev -> listaEnemigos.setSelectedIndex(index));

                panelCampoBatalla.add(btn);
                idxVisible++;
            }
        }

        listaEnemigos.setSelectedIndex(-1);

        panelCampoBatalla.revalidate();
        panelCampoBatalla.repaint();
    }

    private String obtenerRutaImagenEnemigo(TipoEnemigo tipo) {
        switch (tipo) {
            case SPIKED_HARE:
                return "/img/skiped.png";
            case VENOM_SLIME:
                return "/img/slime.png";
            case PATYPUNK:
                return "/img/paty-punk.png";
            case TERROR_TABBY:
                return "/img/terror.png";
            default:
                return "/img/default.png";
        }
    }

    // GUARDAR PARTIDA
    private void guardarPartida() {

        String nombre = JOptionPane.showInputDialog(this, "Nombre para el guardado:");

        if (nombre == null || nombre.trim().isEmpty())
            return;

        try {
            GuardarPartida.guardar(controlador, indiceHeroeActual, nombre.trim());
            JOptionPane.showMessageDialog(this, "Partida guardada con exito!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }
    }

    // ATAQUE / HABILIDAD
    private void realizarAtaque(boolean habilidad) {

        if (enemigosVivos.isEmpty()) {
            areaTexto.append("No hay enemigos que atacar.\n");
            return;
        }

        int idx = listaEnemigos.getSelectedIndex();

        if (idx == -1) {
            areaTexto.append("Debes seleccionar un enemigo haciendo click en el.\n");
            return;
        }

        Enemigo objetivo = enemigosVivos.get(idx);
        int realIndex = enemigos.indexOf(objetivo);

        try {
            String r = controlador.accionHeroe(indiceHeroeActual, habilidad, realIndex);
            areaTexto.append(r);

            actualizarListaEnemigos();
            actualizarBotonesEnemigos();

            if (!controlador.hayEnemigosVivos()) {
                finDeBatalla();
                return;
            }

            siguienteTurno();

        } catch (Exception ex) {
            areaTexto.append("Error: " + ex.getMessage() + "\n");
        }
    }

    // DEFENSA
    private void manejarDefensa() {
        try {
            areaTexto.append(controlador.defender(indiceHeroeActual));
            siguienteTurno();
        } catch (Exception ex) {
            areaTexto.append("Error: " + ex.getMessage() + "\n");
        }
    }

    // OBJETOS
    private void mostrarMenuObjetos() {

        Jugador h = heroes.get(indiceHeroeActual);

        JDialog d = new JDialog(this, "Objetos", true);
        d.setSize(300, 250);
        d.setLayout(new GridLayout(6, 1));

        String[] items = {
                "Hierba medicinal", "Hierba curativa",
                "Antidoto", "Vial MP", "Agua sagrada",
                "Cancelar"
        };

        for (String item : items) {

            JButton b = new JButton(item);

            b.addActionListener(e -> {

                if (!item.equals("Cancelar")) {
                    String r = controlador.usarObjeto(indiceHeroeActual, item);
                    areaTexto.append(r);

                    if (!controlador.hayEnemigosVivos()) {
                        finDeBatalla();
                    } else {
                        siguienteTurno();
                    }
                }

                d.dispose();
            });

            d.add(b);
        }

        d.setLocationRelativeTo(this);
        d.setVisible(true);
    }

    // DESHACER / REHACER
    private void manejarDeshacer() {

        String r = controlador.deshacerAccion();
        areaTexto.append(r);

        actualizarListaEnemigos();
        actualizarBotonesEnemigos();
        actualizarTurnoActual();
    }

    private void manejarRehacer() {

        String r = controlador.rehacerAccion();
        areaTexto.append(r);

        if (r.toLowerCase().contains("no hay accion"))
            return;

        actualizarListaEnemigos();
        actualizarBotonesEnemigos();

        if (!controlador.hayEnemigosVivos()) {
            finDeBatalla();
        } else {
            siguienteTurno();
        }
    }

    // TURNOS
    private void siguienteTurno() {

        int intentos = 0;

        while (true) {

            indiceHeroeActual++;

            if (indiceHeroeActual >= heroes.size()) {

                areaTexto.append("\n--- Turno de los enemigos ---\n");
                areaTexto.append(controlador.turnoEnemigos());

                if (!controlador.hayHeroesVivos()) {
                    finDeBatalla();
                    return;
                }

                indiceHeroeActual = 0;
            }

            if (heroes.get(indiceHeroeActual).estaVivo())
                break;

            intentos++;
            if (intentos > heroes.size())
                break;
        }

        actualizarListaEnemigos();
        actualizarBotonesEnemigos();
        actualizarTurnoActual();
    }

    // ACTUALIZACIONES
    private void actualizarTurnoActual() {

        Jugador h = heroes.get(indiceHeroeActual);

        areaTexto.append("\nTurno de "
                + h.getNombre()
                + " (HP: " + h.getHp()
                + " MP: " + h.getMp() + ")\n");
    }

    private void actualizarListaEnemigos() {

        listaEnemigos.removeAllItems();
        enemigosVivos.clear();

        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                enemigosVivos.add(e);
                listaEnemigos.addItem(e.getNombre());
            }
        }

        listaEnemigos.setSelectedIndex(-1);
    }

    // FIN DE BATALLA
    private void finDeBatalla() {

        areaTexto.append("\n=============================\n");

        boolean victoria = controlador.hayHeroesVivos();

        if (victoria) {
            areaTexto.append("¡Los heroes han ganado la batalla!\n");
        } else {
            areaTexto.append("Los enemigos han triunfado...\n");
        }

        controlador.generarReporteFinal(victoria);

        areaTexto.append("=============================\n");

        btnAtacar.setEnabled(false);
        btnHabilidad.setEnabled(false);
        btnDefender.setEnabled(false);
        btnObjeto.setEnabled(false);
        btnDeshacer.setEnabled(false);
        btnRehacer.setEnabled(false);
        btnGuardar.setEnabled(false);

        JButton volver = new JButton("Volver al menu principal");
        volver.addActionListener(e -> {
            new MenuPrincipal().setVisible(true);
            detenerMusica();
            dispose();
        });

        JPanel panel = new JPanel();
        panel.add(volver);
        add(panel, BorderLayout.NORTH);

        revalidate();
    }
}
