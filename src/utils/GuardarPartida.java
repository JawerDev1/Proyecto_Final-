package utils;

import controller.ControladorJuego;
import model.Enemigo;
import model.Jugador;

import java.io.*;
import java.util.ArrayList;

public class GuardarPartida {

    public static final String RUTA_CARPETA = "partidas";

    public static class PartidaCargada {
        private final ControladorJuego controlador;
        private final int turno;

        public PartidaCargada(ControladorJuego controlador, int turno) {
            this.controlador = controlador;
            this.turno = turno;
        }

        public ControladorJuego getControlador() {
            return controlador;
        }

        public int getTurno() {
            return turno;
        }
    }

    // GUARDAR PARTIDA
    public static void guardar(ControladorJuego ctrl, int turno, String nombreArchivo) throws Exception {

        File carpeta = new File(RUTA_CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        File archivo = new File(carpeta, nombreArchivo + ".sav");

        PrintWriter pw = new PrintWriter(new FileWriter(archivo));

        pw.println("TURNO=" + turno);

        pw.println("[HEROES]");
        for (Jugador h : ctrl.getHeroes()) {
            pw.println(h.serializar());
        }

        pw.println("[ENEMIGOS]");
        for (Enemigo e : ctrl.getEnemigos()) {
            pw.println(e.serializar());
        }

        pw.close();
    }

    // CARGAR PARTIDA
    public static PartidaCargada cargar(String nombreArchivo) {

        try {
            File archivo = new File(RUTA_CARPETA, nombreArchivo);
            if (!archivo.exists()) return null;

            BufferedReader br = new BufferedReader(new FileReader(archivo));

            String linea;
            int turno = 0;

            ArrayList<Jugador> heroes = new ArrayList<>();
            ArrayList<Enemigo> enemigos = new ArrayList<>();

            boolean leyendoHeroes = false;
            boolean leyendoEnemigos = false;

            while ((linea = br.readLine()) != null) {

                if (linea.startsWith("TURNO=")) {
                    turno = Integer.parseInt(linea.replace("TURNO=", "").trim());
                    continue;
                }

                if (linea.equals("[HEROES]")) {
                    leyendoHeroes = true;
                    leyendoEnemigos = false;
                    continue;
                }

                if (linea.equals("[ENEMIGOS]")) {
                    leyendoHeroes = false;
                    leyendoEnemigos = true;
                    continue;
                }

                if (linea.isBlank()) continue;

                if (leyendoHeroes) {
                    heroes.add(Jugador.deserializar(linea));
                } else if (leyendoEnemigos) {
                    enemigos.add(Enemigo.deserializar(linea));
                }
            }

            br.close();

            ControladorJuego ctrl = new ControladorJuego(heroes, enemigos);
            return new PartidaCargada(ctrl, turno);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
