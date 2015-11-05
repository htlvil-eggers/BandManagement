package pkgController;

import java.io.IOException;
import java.sql.SQLException;
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

import pkgModel.OracleDatabaseManager;

public class LoginController {
	private OracleDatabaseManager databaseManager;
	
	@FXML
	private ResourceBundle resources;
	
	@FXML
	private void initialize() {
		databaseManager = (OracleDatabaseManager) resources.getObject("databaseManager");
	}
	
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
    	
    	try {
			if (databaseManager.checkCredentials (username, password, bandname) == true) {				
				resources = new ListResourceBundle() { 
					protected Object[][] getContents() {
						return new Object[][] {
							{"databaseManager", databaseManager},
							{"username", username}
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
		} catch (SQLException e) {
			Alert messageWindow = new Alert (Alert.AlertType.ERROR);
			
			messageWindow.setContentText(e.getMessage());
			
			messageWindow.show();
		}
    }

    @FXML
    private void btnClose_onAction() {
    	btnClose.getScene().getWindow().hide();
    }
}
