package utils;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AudioPlayer {

    private static AudioPlayer instance;
    private Clip clip;

    private AudioPlayer() {}

    public static synchronized AudioPlayer getInstance() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    public synchronized void playLoop(String ruta, boolean loadFromClasspath) {
        stop();
        try {
            AudioInputStream audioIn;
            if (!loadFromClasspath) {
                File file = new File(ruta);
                if (!file.exists()) {
                    System.err.println("Archivo de audio no encontrado: " + file.getAbsolutePath());
                    return;
                }
                audioIn = AudioSystem.getAudioInputStream(file);
            } else {
                URL resource = getClass().getResource(ruta);
                if (resource == null) {
                    System.err.println("Audio no encontrado en classpath: " + ruta);
                    return;
                }
                audioIn = AudioSystem.getAudioInputStream(resource);
            }

            AudioFormat baseFormat = audioIn.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false
            );

            AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, audioIn);

            clip = AudioSystem.getClip();
            clip.open(din);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            din.close();
            audioIn.close();

            System.out.println("Reproduciendo música: " + ruta);

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Formato de audio no soportado: " + ruta);
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de audio: " + ruta);
        } catch (LineUnavailableException e) {
            System.err.println("No se pudo acceder al dispositivo de audio.");
        }
    }

    public synchronized void stop() {
        try {
            if (clip != null) {
                if (clip.isRunning()) clip.stop();
                clip.close();
                clip = null;
                System.out.println("Música detenida.");
            }
        } catch (Exception e) {
            System.err.println("Error al detener la música: " + e.getMessage());
        }
    }

    public synchronized void restart() {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        System.out.println("Música reiniciada desde el inicio.");
    }

    public synchronized boolean isPlaying() {
        return clip != null && clip.isRunning();
    }
}
