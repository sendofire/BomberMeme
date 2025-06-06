package fr.amu.iut;

import fr.amu.iut.Personnages.Joueurs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.text.JTextComponent;
import java.util.Scanner;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class JeuMain extends Application {
    public static void main(String[] args) {Application.launch(args);}

    @Override
    public void start(Stage stage) throws IOException {

        Joueurs j1 = new Joueurs(1, 10, 10);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Level1.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("BOMBOCLAT");
        stage.show();
    }

    //public void deplacement(Joueurs j1) {}
}
