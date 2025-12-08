package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Historial {

    private static final String RUTA = "historial_batallas.txt";

    public static void guardar(String reporte) {
        try (FileWriter fw = new FileWriter(RUTA, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(reporte);
            out.println("------------------------------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String cargar() {
        try {
            Path path = Paths.get(RUTA);
            if (!Files.exists(path)) {
                return "No hay batallas registradas todavia.\n";
            }
            return Files.readString(path);
        } catch (IOException e) {
            return "Error al leer historial.\n";
        }
    }
}
