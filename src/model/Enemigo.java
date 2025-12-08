package model;

import java.util.Random;

public class Enemigo extends Personaje {

    private TipoEnemigo tipoEnemigo;
    private Random random = new Random();

    // Debuff de ataque (para Agua sagrada)
    private boolean debilitado = false;
    private int turnosDebilitado = 0;
    private int ataqueOriginal = 0;

    public Enemigo(TipoEnemigo tipoEnemigo, String nombre,
                   int hp, int mp, int ataque, int defensa, int velocidad) {
        super(TipoPersonaje.ENEMIGO, nombre, hp, mp, ataque, defensa, velocidad);
        this.tipoEnemigo = tipoEnemigo;
    }

    public TipoEnemigo getTipoEnemigo() {
        return tipoEnemigo;
    }

    // ======================================================
    // ATAQUE NORMAL SEGUN TIPO
    // ======================================================
    public String ataqueNormal(Personaje objetivo) {

        if (!estaVivo())
            return getNombre() + " esta muerto y no puede atacar.\n";

        // Reducir turnos de debuff de ataque (si lo tiene)
        reducirTurnoDebuff();

        if (getEstado() == Estado.DORMIDO) {
            reducirTurnosEstado();
            return getNombre() + " esta dormido y pierde el turno.\n";
        }

        return switch (tipoEnemigo) {
            case VENOM_SLIME -> ataqueVeneno(objetivo);
            case PATYPUNK    -> ataqueGarrote(objetivo);
            case SPIKED_HARE -> ataquePatadaGiratoria(objetivo);
            case TERROR_TABBY-> ataqueSleep(objetivo);
            default          -> ataqueBasico(objetivo);
        };
    }

    // Ataque generico
    private String ataqueBasico(Personaje objetivo) {
        int dano = atacar(objetivo);
        int hpRestante = objetivo.getHp();
        return getNombre() + " ataca a " + objetivo.getNombre() +
                " causando " + dano + " de dano. " +
                objetivo.getNombre() + " HP restante: " + hpRestante + ".\n";
    }

    // VENOM SLIME – escupe veneno
    private String ataqueVeneno(Personaje objetivo) {

        int dano = Math.max(1, (getAtaque() / 2) - (objetivo.getDefensa() / 3));
        objetivo.recibirDanio(dano);

        StringBuilder sb = new StringBuilder();
        sb.append(getNombre())
          .append(" escupe veneno a ")
          .append(objetivo.getNombre())
          .append(" causando ")
          .append(dano)
          .append(" de dano. HP restante: ")
          .append(objetivo.getHp())
          .append(".\n");

        // 60% de probabilidad de envenenar
        if (random.nextDouble() <= 0.6 && objetivo.getEstado() != Estado.VENENO) {
            objetivo.aplicarEstado(Estado.VENENO, 0); // dura hasta que lo curen
            sb.append(objetivo.getNombre())
              .append(" ha sido envenenado.\n");
        }
        return sb.toString();
    }

    // PATYPUNK – golpe con garrote
    private String ataqueGarrote(Personaje objetivo) {

        StringBuilder sb = new StringBuilder();

        if (random.nextDouble() <= 0.4) { // 40% golpe critico
            int danoBase = Math.max(1, getAtaque() - objetivo.getDefensa());
            int danoTotal = (int) (danoBase * 1.7);
            objetivo.recibirDanio(danoTotal);

            sb.append(getNombre())
              .append(" da un golpe brutal con su garrote a ")
              .append(objetivo.getNombre())
              .append(" causando ")
              .append(danoTotal)
              .append(" de dano. HP restante: ")
              .append(objetivo.getHp())
              .append(".\n");
        } else {
            int dano = atacar(objetivo);
            sb.append(getNombre())
              .append(" golpea con su garrote a ")
              .append(objetivo.getNombre())
              .append(" causando ")
              .append(dano)
              .append(" de dano. HP restante: ")
              .append(objetivo.getHp())
              .append(".\n");
        }

        return sb.toString();
    }

    // SPIKED HARE – patada giratoria
    private String ataquePatadaGiratoria(Personaje objetivo) {

        int danoBase = Math.max(1, getAtaque() - (objetivo.getDefensa() / 2));
        int danoTotal = (int) (danoBase * 1.3);
        objetivo.recibirDanio(danoTotal);

        StringBuilder sb = new StringBuilder();
        sb.append(getNombre())
          .append(" usa Patada giratoria contra ")
          .append(objetivo.getNombre())
          .append(" causando ")
          .append(danoTotal)
          .append(" de dano. HP restante: ")
          .append(objetivo.getHp())
          .append(".\n");

        return sb.toString();
    }

    // TERROR TABBY – sleep attack
    private String ataqueSleep(Personaje objetivo) {

        int dano = atacar(objetivo);
        StringBuilder sb = new StringBuilder();

        sb.append(getNombre())
          .append(" ataca a ")
          .append(objetivo.getNombre())
          .append(" causando ")
          .append(dano)
          .append(" de dano. HP restante: ")
          .append(objetivo.getHp())
          .append(".\n");

        // 50% probabilidad de dormir
        if (random.nextDouble() <= 0.5 && objetivo.getEstado() != Estado.DORMIDO) {
            objetivo.aplicarEstado(Estado.DORMIDO, 2); // duerme 2 turnos del heroe
            sb.append(objetivo.getNombre())
              .append(" se queda dormido por el ataque.\n");
        }

        return sb.toString();
    }

    // ======================================================
    // DEBUFF DE ATAQUE (Agua sagrada)
    // ======================================================
    public void aplicarDebuffAtaque() {
        if (!debilitado) {
            debilitado = true;
            turnosDebilitado = 3;          // dura 3 turnos del enemigo
            ataqueOriginal = getAtaque();
            int nuevoAtaque = Math.max(1, (int) (ataqueOriginal * 0.6)); // -40%
            setAtaque(nuevoAtaque);
        } else {
            // si ya estaba debilitado, refrescamos la duracion
            turnosDebilitado = 3;
        }
    }

    public void reducirTurnoDebuff() {
        if (debilitado) {
            turnosDebilitado--;
            if (turnosDebilitado <= 0) {
                debilitado = false;
                setAtaque(ataqueOriginal);
            }
        }
    }

    // ======================================================
    // SERIALIZAR / DESERIALIZAR
    // ======================================================
    public String serializar() {
        // tipoEnemigo;nombre;hp;mp;ataque;defensa;velocidad;estado
        return tipoEnemigo.name() + ";" +
                getNombre() + ";" +
                getHp() + ";" +
                getMp() + ";" +
                getAtaque() + ";" +
                getDefensa() + ";" +
                getVelocidad() + ";" +
                getEstado().name();
    }

    public static Enemigo deserializar(String linea) {
        String[] d = linea.split(";");
        TipoEnemigo tipo = TipoEnemigo.valueOf(d[0]);
        String nombre = d[1];
        int hp = Integer.parseInt(d[2]);
        int mp = Integer.parseInt(d[3]);
        int ataque = Integer.parseInt(d[4]);
        int defensa = Integer.parseInt(d[5]);
        int velocidad = Integer.parseInt(d[6]);
        Estado estado = Estado.valueOf(d[7]);

        Enemigo e = new Enemigo(tipo, nombre, hp, mp, ataque, defensa, velocidad);
        e.setEstadoCargado(estado);
        return e;
    }
}
