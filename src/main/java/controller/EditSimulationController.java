package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.LoginService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditSimulationController implements Initializable {
    /**
     * declaring variables
     */
    @FXML
    private ComboBox <String> rooms;
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * This function loads the login info page(scene) into the window(stage)
     * @param event The event that called this function
     * @throws IOException
     */
    public void goToLoginInfo(ActionEvent event) throws IOException {
        Parent loginInfo = FXMLLoader.load(getClass().getResource("/view/loginInfo.fxml"));
        Scene loginInfoScene = new Scene(loginInfo);

        // stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(loginInfoScene);window.show();
    }

    /**
     * This function will close the application
     * @param event The event that called this function
     * @throws IOException
     */
    public void close(MouseEvent event) throws IOException {
        System.exit(0);
    }

    /**
     * Gets the location of a mouse.
     * @param event
     */
    public void getLocation(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Changes the location of the window(stage) based on the mouse location..
     * @param event The event that called this function
     */
    public void move(MouseEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setX(event.getScreenX() - xOffset);
        window.setY(event.getScreenY() - yOffset);
    }

    /**
     * This function will add the rooms to the combobox(
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources){
        rooms.getItems().add("Living Room");
        rooms.getItems().add("Bedroom 1");
        rooms.getItems().add("Bedroom 2");
        rooms.getItems().add("Bathroom");
        rooms.getItems().add("Kitchen");
    }

    /**
     * This function loads the change location page(scene) into the window(stage)
     * @param event The event that called this function
     * @throws IOException
     */
    public void changeLocation(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/loginInfo.fxml"));
        Parent loginInfo = loader.load();
        Scene loginInfoScene = new Scene(loginInfo);
        LoginInfoController controller = loader.getController();

        String choice = rooms.getSelectionModel().getSelectedItem();
        controller.setLoc(choice);

        // stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(loginInfoScene);
        window.show();
    }

}
