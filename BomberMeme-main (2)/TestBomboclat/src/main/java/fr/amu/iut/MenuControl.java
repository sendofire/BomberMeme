package fr.amu.iut;

import fr.amu.iut.Personnages.Joueurs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class MenuControl {

    public Stage stage;
    public Scene scene;
    public Parent root;

    Joueurs j1 = new Joueurs(1, 10, 10);

    @FXML
    public void switchScene(javafx.event.ActionEvent event) throws IOException {
        try {
            // Charger la scène de jeu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/Jeu.fxml"));
            AnchorPane root = loader.load();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            // Demander le focus pour les contrôles clavier
            root.requestFocus();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du jeu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void switchHome(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/Menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("FXML/MenuCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchCredits(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/Credits.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("FXML/CreditsCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchMaintenance(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/Maintenance.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("FXML/MaintenanceCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void quit(javafx.event.ActionEvent event) throws IOException {
        System.exit(0);
    }
    @FXML
    public void switchStartMenu(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("FXML/startMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("FXML/MenuCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
    public void switchDrapeau(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/Jeu.fxml"));
        loader.setController(new CaptureDeDrapeau());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("FXML/MaintenanceCSS.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
