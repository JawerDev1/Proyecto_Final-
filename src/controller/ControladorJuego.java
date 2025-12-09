package controller;

import model.*;
import model.exceptions.*;
import utils.Historial;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ControladorJuego {

    private ArrayList<Jugador> heroes;
    private ArrayList<Enemigo> enemigos;
    private Random random = new Random();

    private Accion ultimaAccion = null;
    private Accion accionDeshecha = null;
    private int indiceHeroeDeshacer = 0;

    private LinkedList<String> historialBatallas = new LinkedList<>();

    private int turnos = 0;
    private int danoTotalHeroes = 0;
    private int danoTotalEnemigos = 0;

    public ControladorJuego(ArrayList<Jugador> heroes, ArrayList<Enemigo> enemigos) {
        this.heroes = heroes;
        this.enemigos = enemigos;
    }

    public ArrayList<Jugador> getHeroes() { return heroes; }
    public ArrayList<Enemigo> getEnemigos() { return enemigos; }
    public List<String> getHistorial() { return historialBatallas; }
    public int getIndiceHeroeDeshacer() { return indiceHeroeDeshacer; }

    private void registrarDanoHeroe(int dano) { danoTotalHeroes += dano; }
    private void registrarDanoEnemigo(int dano) { danoTotalEnemigos += dano; }

    // ============================================================
    // ACCIONES HEROES
    // ============================================================
    public String accionHeroe(int iHeroe, boolean habilidad, int iObjetivo)
            throws PersonajeMuertoException, ObjetivoInvalidoException, SinMPException {

        turnos++;
        

        if (iHeroe < 0 || iHeroe >= heroes.size())
            throw new ObjetivoInvalidoException("Indice de heroe invalido.");

        Jugador h = heroes.get(iHeroe);

        StringBuilder salida = new StringBuilder();

        // Dano de veneno al inicio del turno
        if (h.getEstado() == Estado.VENENO && h.estaVivo()) {
            int danoVeneno = 4;
            h.recibirDanio(danoVeneno);
            registrarDanoEnemigo(danoVeneno);

            salida.append(h.getNombre())
                    .append(" sufre ")
                    .append(danoVeneno)
                    .append(" de dano por veneno. HP restante: ")
                    .append(h.getHp())
                    .append(".\n");

            if (!h.estaVivo()) {
                salida.append(h.getNombre())
                        .append(" ha muerto por el veneno.\n");
                ultimaAccion = null;
                accionDeshecha = null;
                return salida.toString();
            }
        }

        if (!h.estaVivo())
            throw new PersonajeMuertoException(h.getNombre() + " esta muerto y no puede actuar.");

        if (iObjetivo < 0 || iObjetivo >= enemigos.size())
            throw new ObjetivoInvalidoException("Indice de enemigo invalido.");

        Enemigo enemigo = enemigos.get(iObjetivo);

        if (h.getEstado() == Estado.DORMIDO) {
            h.reducirTurnosEstado();
            salida.append(h.getNombre())
                    .append(" esta dormido y pierde el turno.\n");
            return salida.toString();
        }

        Accion acc = new Accion(h, enemigo, iHeroe);
        acc.hpHeroeAntes = h.getHp();
        acc.mpHeroeAntes = h.getMp();
        acc.hpEnemigoAntes = enemigo.getHp();
        acc.tipoAccion = habilidad ? "HABILIDAD" : "ATAQUE";

        String msg;

        if (habilidad) {
            msg = h.usarHabilidadEspecial(enemigo);
            if (msg.contains("no tiene suficiente MP"))
                throw new SinMPException(h.getNombre() + " no tiene MP.");
        } else {
            msg = h.ataqueNormal(enemigo);
        }

        salida.append(msg);

        int dano = Math.max(0, acc.hpEnemigoAntes - enemigo.getHp());
        registrarDanoHeroe(dano);

        ultimaAccion = acc;
        accionDeshecha = null;
        indiceHeroeDeshacer = iHeroe;

        if (!enemigo.estaVivo()) {
            salida.append(enemigo.getNombre()).append(" ha sido derrotado.\n");
            enemigos.remove(enemigo);
        }
        return salida.toString();
    }

    public String defender(int iHeroe)
            throws ObjetivoInvalidoException, PersonajeMuertoException {

        if (iHeroe < 0 || iHeroe >= heroes.size())
            throw new ObjetivoInvalidoException("Indice invalido de heroe.");

        Jugador h = heroes.get(iHeroe);

        if (!h.estaVivo())
            throw new PersonajeMuertoException("No puedes defender con un heroe muerto.");

        Accion acc = new Accion(h, null, iHeroe);
        acc.hpHeroeAntes = h.getHp();
        acc.mpHeroeAntes = h.getMp();
        acc.tipoAccion = "DEFENSA";
        acc.defensaActivada = true;

        h.activarDefensa();

        ultimaAccion = acc;
        accionDeshecha = null;
        indiceHeroeDeshacer = iHeroe;

        return h.getNombre() + " adopta postura defensiva.\n";
    }

    public String usarObjeto(int iHeroe, String item) {

        Jugador h = heroes.get(iHeroe);

        Accion acc = new Accion(h, null, iHeroe);
        acc.hpHeroeAntes = h.getHp();
        acc.mpHeroeAntes = h.getMp();
        acc.tipoAccion = "OBJETO";
        acc.objetoUsado = item;
        acc.cantidadItemAntes = h.getInventario().getOrDefault(item, 0);

        String resultado = h.usarObjeto(item, this);

        ultimaAccion = acc;
        accionDeshecha = null;
        indiceHeroeDeshacer = iHeroe;

        return resultado;
    }

    // ============================================================
    // DESHACER / REHACER
    // ============================================================
    public String deshacerAccion() {
        if (ultimaAccion == null)
            return "No hay accion para deshacer.\n";

        Accion acc = ultimaAccion;
        ultimaAccion = null;
        accionDeshecha = acc;

        Jugador h = acc.heroe;

        h.setHp(acc.hpHeroeAntes);
        h.setMp(acc.mpHeroeAntes);

        if (acc.enemigo != null) {
            acc.enemigo.setHp(acc.hpEnemigoAntes);
            if (!enemigos.contains(acc.enemigo) && acc.hpEnemigoAntes > 0)
                enemigos.add(acc.enemigo);
        }

        if (acc.objetoUsado != null)
            h.getInventario().put(acc.objetoUsado, acc.cantidadItemAntes);

        if (acc.defensaActivada)
            h.desactivarDefensa();

        return "La accion fue revertida.\n";
    }

    public String rehacerAccion() {
        if (accionDeshecha == null)
            return "No hay accion para rehacer.\n";

        Accion acc = accionDeshecha;
        accionDeshecha = null;

        Jugador h = acc.heroe;
        String msg = "";

        switch (acc.tipoAccion) {
            case "ATAQUE" -> msg = h.ataqueNormal(acc.enemigo);
            case "HABILIDAD" -> msg = h.usarHabilidadEspecial(acc.enemigo);
            case "DEFENSA" -> {
                h.activarDefensa();
                msg = h.getNombre() + " adopta postura defensiva.\n";
            }
            case "OBJETO" -> msg = h.usarObjeto(acc.objetoUsado, this);
        }

        ultimaAccion = acc;
        indiceHeroeDeshacer = acc.indiceHeroe;

        return msg;
    }

    // ============================================================
    // TURNO ENEMIGOS
    // ============================================================
    public String turnoEnemigos() {

        StringBuilder resultado = new StringBuilder();

        for (Enemigo e : new ArrayList<>(enemigos)) {
            if (!e.estaVivo()) continue;

            Jugador objetivo = obtenerHeroeVivoAleatorio();
            if (objetivo == null) break;

            int hpAntes = objetivo.getHp();
            String msg = e.ataqueNormal(objetivo);

            resultado.append(msg);

            int dano = Math.max(0, hpAntes - objetivo.getHp());
            registrarDanoEnemigo(dano);
        }

        return resultado.toString();
    }

    // ============================================================
    // UTILIDADES
    // ============================================================
    private Jugador obtenerHeroeVivoAleatorio() {
        ArrayList<Jugador> vivos = new ArrayList<>();
        for (Jugador h : heroes) if (h.estaVivo()) vivos.add(h);
        if (vivos.isEmpty()) return null;
        return vivos.get(random.nextInt(vivos.size()));
    }

    public boolean hayHeroesVivos() {
        for (Jugador h : heroes) if (h.estaVivo()) return true;
        return false;
    }

    public boolean hayEnemigosVivos() {
        for (Enemigo e : enemigos) if (e.estaVivo()) return true;
        return false;
    }

    // Lista de enemigos vivos para Agua sagrada
    public ArrayList<Enemigo> getEnemigosParaAguaSagrada() {
        ArrayList<Enemigo> lista = new ArrayList<>();
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                lista.add(e);
            }
        }
        return lista;
    }

    // ============================================================
    // REPORTE FINAL
    // ============================================================
    public void generarReporteFinal(boolean heroesGanan) {

        StringBuilder sb = new StringBuilder();

        sb.append("===== REPORTE DE BATALLA =====\n");
        sb.append("Resultado: ").append(heroesGanan ? "Victoria" : "Derrota").append("\n");
        sb.append("Fecha: ").append(LocalDateTime.now()).append("\n");
        sb.append("Turnos: ").append(turnos).append("\n");
        sb.append("Dano total causado por heroes: ").append(danoTotalHeroes).append("\n");
        sb.append("Dano total recibido: ").append(danoTotalEnemigos).append("\n");

        sb.append("Heroes vivos: ");
        for (Jugador h : heroes)
            if (h.estaVivo()) sb.append(h.getNombre()).append(" ");
        sb.append("\n=============================\n");

        historialBatallas.add(sb.toString());
        Historial.guardar(sb.toString());

        turnos = 0;
        danoTotalHeroes = 0;
        danoTotalEnemigos = 0;
        ultimaAccion = null;
        accionDeshecha = null;
    }
}
