package model;

public class Accion {

    public Jugador heroe;
    public Enemigo enemigo;

    public int indiceHeroe;

    // Estado antes de la accion
    public int hpHeroeAntes;
    public int mpHeroeAntes;
    public int hpEnemigoAntes;

    // Tipo de accion: "ATAQUE", "HABILIDAD", "DEFENSA", "OBJETO"
    public String tipoAccion;

    // Para acciones especiales
    public boolean defensaActivada = false;

    // Para objetos
    public String objetoUsado = null;
    public int cantidadItemAntes = 0;

    public Accion(Jugador heroe, Enemigo enemigo, int indiceHeroe) {
        this.heroe = heroe;
        this.enemigo = enemigo;
        this.indiceHeroe = indiceHeroe;
    }
}
