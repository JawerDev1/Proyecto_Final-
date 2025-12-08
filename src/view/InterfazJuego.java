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

    private JPanel panelCampoBatalla;

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
        panelCampoBatalla = new JPanel();
        panelCampoBatalla.setPreferredSize(new Dimension(900, 350));
        panelCampoBatalla.setBackground(new Color(220, 220, 220));
        panelCampoBatalla.setBorder(BorderFactory.createTitledBorder("Campo de batalla"));
        panelCampoBatalla.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 30));
        panelCentral.add(panelCampoBatalla, BorderLayout.NORTH);

        // Consola
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaTexto.setBackground(new Color(245, 245, 235));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(900, 180));
        scroll.setBorder(BorderFactory.createTitledBorder("Registro de batalla"));

        JPanel panelConsola = new JPanel(new BorderLayout());
        panelConsola.add(scroll, BorderLayout.CENTER);

        panelCentral.add(panelConsola, BorderLayout.SOUTH);

        add(panelCentral, BorderLayout.CENTER);

        // Panel derecho
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new GridLayout(10, 1, 5, 10));
        panelDerecho.setPreferredSize(new Dimension(180, 600));
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Opciones"));

        btnDeshacer = new JButton("DESHACER");
        btnRehacer = new JButton("REHACER");
        btnGuardar = new JButton("GUARDAR");
        JButton btnSalir = new JButton("SALIR");

        panelDerecho.add(btnDeshacer);
        panelDerecho.add(btnRehacer);
        panelDerecho.add(btnGuardar);
        panelDerecho.add(btnSalir);

        add(panelDerecho, BorderLayout.EAST);

        // Panel de acciones
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
        panelAcciones.setPreferredSize(new Dimension(900, 80));
        panelAcciones.setBorder(BorderFactory.createTitledBorder("Acciones"));

        btnAtacar = new JButton("ATACAR");
        btnDefender = new JButton("DEFENDER");
        btnHabilidad = new JButton("HABILIDAD");
        btnObjeto = new JButton("OBJETO");

        panelAcciones.add(btnAtacar);
        panelAcciones.add(btnDefender);
        panelAcciones.add(btnHabilidad);
        panelAcciones.add(btnObjeto);

        add(panelAcciones, BorderLayout.SOUTH);

        // Eventos
        btnAtacar.addActionListener(e -> realizarAtaque(false));
        btnHabilidad.addActionListener(e -> realizarAtaque(true));
        btnDefender.addActionListener(e -> manejarDefensa());
        btnObjeto.addActionListener(e -> mostrarMenuObjetos());
        btnDeshacer.addActionListener(e -> manejarDeshacer());
        btnRehacer.addActionListener(e -> manejarRehacer());
        btnGuardar.addActionListener(e -> guardarPartida());
        btnSalir.addActionListener(e -> dispose());
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

                JButton btn = new JButton("<html>" + e.getNombre() +
                        "<br>HP: " + e.getHp() + "</html>");
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

    // GUARDAR PARTIDA
    private void guardarPartida() {

        String nombre = JOptionPane.showInputDialog(this, "Nombre para el guardado:");

        if (nombre == null || nombre.trim().isEmpty()) return;

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

        if (r.toLowerCase().contains("no hay accion")) return;

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

            if (heroes.get(indiceHeroeActual).estaVivo()) break;

            intentos++;
            if (intentos > heroes.size()) break;
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
            dispose();
        });

        JPanel panel = new JPanel();
        panel.add(volver);
        add(panel, BorderLayout.NORTH);

        revalidate();
    }
}
