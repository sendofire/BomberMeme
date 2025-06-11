package fr.amu.iut;

import fr.amu.iut.Personnages.Joueurs;
import fr.amu.iut.audio.AudioManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.util.Scanner;

import java.io.IOException;

public class JeuMain extends Application {
    public static void main(String[] args) {Application.launch(args);}

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/Menu.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("FXML/MenuCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("BOMBOCLAT");

        // Au démarrage de votre jeu
        AudioManager.demarrerMusique();
        stage.show();
    }

}