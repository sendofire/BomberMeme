package fr.amu.iut.audio;

import javafx.animation.Animation;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.sound.sampled.*;
import java.net.URL;

public class AudioManager {
    private static Clip musique;
    
    public static void demarrerMusique() {

        URL resource = null;
        try {
            resource = AudioManager.class.getResource("/fr/amu/iut/audio/musicTitle.mp3");
            System.out.println("Classe : " + AudioManager.class);
            System.out.println("ClassLoader : " + AudioManager.class.getClassLoader());

            if (resource == null) {
                System.err.println("Fichier audio non trouvé !");
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(resource);
            musique = AudioSystem.getClip();
            musique.open(audio);
            musique.loop(Clip.LOOP_CONTINUOUSLY);
            musique.start();

        } catch (Exception e) {
            System.err.println("Erreur audio : " + e.getMessage());
        }

        // Création de l'objet Media et MediaPlayer
        Media media = new Media(resource.toString());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.01);

        // Lecture en boucle
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
    
    public static void arreterMusique() {
        if (musique != null) {
            musique.stop();
            musique.close();
        }
    }
    //button.setOnMouseEnter(e-> demarrerMusique("/fr/amu/iut/audio/Boutton.mp3));


}