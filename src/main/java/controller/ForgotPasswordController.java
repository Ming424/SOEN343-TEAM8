package controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ForgotPasswordController {
	public void goToLoginPage(ActionEvent event) throws IOException {
		Parent forgotPassword = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
		Scene forgotPasswordScene = new Scene(forgotPassword);
		
		// stage info
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
		window.setScene(forgotPasswordScene);
		window.show();
	}
}
