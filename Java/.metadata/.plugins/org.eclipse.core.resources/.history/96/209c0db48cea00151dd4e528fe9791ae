package pkgController;

import java.io.IOException;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class LoginController {
	@FXML
	private ResourceBundle resources;
    @FXML
    private Button btnClose;
    @FXML
    private TextField txtUsername;
    @FXML
    private Button btnLogin;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private TextField txtBand;
    
    @FXML
    private void btnLogin_onAction() throws IOException{
    	String username = txtUsername.getText();
    	String password = pwdPassword.getText();
    	String bandname = txtBand.getText();
    	
		if (/*databaseManager.checkCredentials (username, password, bandname) == */true) {				
			resources = new ListResourceBundle() { 
				protected Object[][] getContents() {
					return new Object[][] {
						{"username", username},
						{"password", password},
						{"bandname", bandname}
					};
				}
			};
			
			Scene scene = new Scene (FXMLLoader.load (Thread.currentThread().getContextClassLoader().getResource("pkgGui/Main.fxml"), resources));
			Stage stage = new Stage();
		
			stage.setScene (scene);
			stage.show();
			
			btnLogin.getScene().getWindow().hide();
		}
		else {
			Alert messageWindow = new Alert (Alert.AlertType.ERROR);
			
			messageWindow.setContentText("Credentials are not correct!");
			
			messageWindow.show();
		}
    }

    @FXML
    private void btnClose_onAction() {
    	btnClose.getScene().getWindow().hide();
    }
}
