package model;

import controller.ControladorJuego;

import java.util.HashMap;
import java.util.Random;

public class Jugador extends Personaje {

    private TipoHeroe tipoHeroe;
    private Random random = new Random();

    // Defensa
    private boolean defendiendo = false;

    // Inventario
    private HashMap<String, Integer> inventario = new HashMap<>();

    // Limite global de usos de objetos por batalla
    private int usosObjetosRestantes = 2;

    // Control deshacer (si lo usas)
    private boolean puedeDeshacer = true;

    public Jugador(TipoHeroe tipoHeroe, String nombre, int hp, int mp,
                   int ataque, int defensa, int velocidad) {
        super(TipoPersonaje.HEROE, nombre, hp, mp, ataque, defensa, velocidad);
        this.tipoHeroe = tipoHeroe;

        inventario.put("Hierba medicinal", 3);
        inventario.put("Hierba curativa", 1);
        inventario.put("Antidoto", 1);
        inventario.put("Vial MP", 1);
        inventario.put("Agua sagrada", 1);
    }

    // GETTERS
    public TipoHeroe getTipoHeroe() { return tipoHeroe; }
    public HashMap<String, Integer> getInventario() { return inventario; }
    public int getUsosObjetosRestantes() { return usosObjetosRestantes; }

    // DESHACER
    public boolean puedeDeshacer() { return puedeDeshacer; }
    public void bloquearDeshacer() { puedeDeshacer = false; }
    public void reiniciarDeshacer() { puedeDeshacer = true; }

    // DEFENSA
    public void activarDefensa() { this.defendiendo = true; }
    public void desactivarDefensa() { this.defendiendo = false; }
    public boolean estaDefendiendo() { return defendiendo; }

    @Override
    public int recibirDanio(int danio) {
        if (defendiendo) {
            // Bloquea el dano de ataques enemigos si se defendio este turno
            return getHp();
        }
        return super.recibirDanio(danio);
    }

    // ======================================================
    // ATAQUE
    // ======================================================
    public String ataqueNormal(Personaje objetivo) {
        if (!estaVivo())
            return getNombre() + " esta muerto y no puede atacar.\n";
        if (getEstado() == Estado.DORMIDO)
            return getNombre() + " esta dormido y no puede atacar.\n";

        int dano = atacar(objetivo);
        return getNombre() + " ataca a " + objetivo.getNombre() +
                " causando " + dano + " de dano. HP restante: " +
                objetivo.getHp() + ".\n";
    }

    // ======================================================
    // HABILIDADES
    // ======================================================
    public String cura12() {
        if (getMp() >= 3) {
            gastarMp(3);
            int hp = curar(12);
            return getNombre() + " usa Cura y recupera 12 HP. HP actual: " + hp + ".\n";
        }
        return getNombre() + " no tiene suficiente MP para usar Cura.\n";
    }

    public String cura15() {
        if (getMp() >= 4) {
            gastarMp(4);
            int hp = curar(15);
            return getNombre() + " usa Cura avanzada y recupera 15 HP. HP actual: " + hp + ".\n";
        }
        return getNombre() + " no tiene suficiente MP para usar Cura avanzada.\n";
    }

    public String frizz(Personaje objetivo) {
        if (getMp() >= 4) {
            gastarMp(4);
            int dano = random.nextInt(6) + 10;
            objetivo.recibirDanio(dano);
            return getNombre() + " lanza Frizz e inflige " + dano +
                    " de dano a " + objetivo.getNombre() +
                    ". HP restante: " + objetivo.getHp() + ".\n";
        }
        return getNombre() + " no tiene suficiente MP para lanzar Frizz.\n";
    }

    public String golpePoderoso(Personaje objetivo) {
        int prob = random.nextInt(100);
        if (prob <= 30) {
            int danoBase = Math.max(1, getAtaque() - objetivo.getDefensa());
            int danoTotal = (int) (danoBase * 1.5);
            objetivo.recibirDanio(danoTotal);
            return getNombre() + " usa Golpe Poderoso e inflige " +
                    danoTotal + " de dano. HP restante: " +
                    objetivo.getHp() + ".\n";
        }
        return getNombre() + " intenta Golpe Poderoso y falla.\n";
    }

    public String usarHabilidadEspecial(Personaje objetivo) {
        switch (tipoHeroe) {
            case HEROE -> {
                return cura12();
            }
            case YANGUS -> {
                return (objetivo != null && objetivo.estaVivo())
                        ? golpePoderoso(objetivo)
                        : getNombre() + " no tiene objetivo valido.\n";
            }
            case YESSICA -> {
                return (objetivo != null && objetivo.estaVivo())
                        ? frizz(objetivo)
                        : getNombre() + " no tiene objetivo valido.\n";
            }
            case ANGELO -> {
                return cura15();
            }
            default -> {
                return getNombre() + " no tiene habilidad especial.\n";
            }
        }
    }

    // ======================================================
    // OBJETOS
    // ======================================================

    // Sobrecarga simple (por si algo llama sin controlador)
    public String usarObjeto(String item) {
        return usarObjeto(item, null);
    }

    // Version que permite que Agua sagrada afecte a los enemigos via controlador
    public String usarObjeto(String item, ControladorJuego controlador) {

        if (usosObjetosRestantes <= 0) {
            return getNombre() + " ya no puede usar mas objetos en esta batalla.\n";
        }

        if (!inventario.containsKey(item) || inventario.get(item) <= 0)
            return "No tienes " + item + ".\n";

        inventario.put(item, inventario.get(item) - 1);
        usosObjetosRestantes--;

        return switch (item) {
            case "Hierba medicinal" -> {
                curar(20);
                yield getNombre() + " uso Hierba medicinal y recupero 20 HP. (Usos de objetos restantes: "
                        + usosObjetosRestantes + ")\n";
            }
            case "Hierba curativa" -> {
                curar(50);
                yield getNombre() + " uso Hierba curativa y recupero 50 HP. (Usos de objetos restantes: "
                        + usosObjetosRestantes + ")\n";
            }
            case "Antidoto" -> {
                if (getEstado() != Estado.NORMAL) {
                    setEstado(Estado.NORMAL, 0);
                    yield getNombre() + " curo su estado alterado. (Usos de objetos restantes: "
                            + usosObjetosRestantes + ")\n";
                }
                yield getNombre() + " no tiene estado alterado. (Usos de objetos restantes: "
                        + usosObjetosRestantes + ")\n";
            }
            case "Vial MP" -> {
                setMp(getMp() + 10);
                yield getNombre() + " recupero 10 MP. (Usos de objetos restantes: "
                        + usosObjetosRestantes + ")\n";
            }
            case "Agua sagrada" -> {
                StringBuilder sb = new StringBuilder();
                sb.append(getNombre())
                  .append(" rocia Agua sagrada sobre el campo de batalla.\n");

                if (controlador != null) {
                    for (Enemigo e : controlador.getEnemigosParaAguaSagrada()) {
                        e.aplicarDebuffAtaque();
                        sb.append("El ataque de ")
                          .append(e.getNombre())
                          .append(" se debilita.\n");
                    }
                } else {
                    sb.append("Pero no parece tener efecto especial...\n");
                }

                sb.append("(Usos de objetos restantes: ")
                  .append(usosObjetosRestantes)
                  .append(")\n");

                yield sb.toString();
            }
            default -> "El objeto no tiene efecto.\n";
        };
    }

    // ======================================================
    // SERIALIZAR / DESERIALIZAR
    // ======================================================
    public String serializar() {
        // tipoHeroe;nombre;hp;mp;ataque;defensa;velocidad;estado
        return tipoHeroe.name() + ";" +
                getNombre() + ";" +
                getHp() + ";" +
                getMp() + ";" +
                getAtaque() + ";" +
                getDefensa() + ";" +
                getVelocidad() + ";" +
                getEstado().name();
    }

    public static Jugador deserializar(String linea) {
        String[] d = linea.split(";");
        TipoHeroe tipo = TipoHeroe.valueOf(d[0]);
        String nombre = d[1];
        int hp = Integer.parseInt(d[2]);
        int mp = Integer.parseInt(d[3]);
        int ataque = Integer.parseInt(d[4]);
        int defensa = Integer.parseInt(d[5]);
        int velocidad = Integer.parseInt(d[6]);
        Estado estado = Estado.valueOf(d[7]);

        Jugador j = new Jugador(tipo, nombre, hp, mp, ataque, defensa, velocidad);
        j.setEstadoCargado(estado);
        return j;
    }
}
