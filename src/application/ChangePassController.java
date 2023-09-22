package application;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
/**
* FXML Controller class
* Called from MainPrgController, Button btnChangePass, Method stageChangePsw()
* Prompt to input a new password and confirm the new password
* Check the new password and confirmed password are matched
* If matched, save the new password and bask to Login method
*/
public class ChangePassController implements Initializable {
		private Connection conn = SqLiteConnection.connector();
	    @FXML
	    private PasswordField pswNew;
	    @FXML
	    private PasswordField pswConfirm;
	    @FXML
	    private Button btnOK;    
	    @FXML
	    private Button btnCancel;    
	    @FXML
	    private String User;
	    @FXML
	    private String oldPass;
	    @FXML

	    int chk_confirm() {
	    	return pswNew.getText().compareTo(pswConfirm.getText());
	    }

	    @FXML
	    // Click OK button to save the new password
	    void changePassword() {
	    	if (chk_confirm() == 0) {
		    	String pass = pswNew.getText();
		    	String sql = "update Users set Password = '" + pass + "' where username = '" + User +"'";
		        try {
		        	PreparedStatement pst = conn.prepareStatement(sql);
		            pst.execute();
		            Message.showMessage("Password has been changed");
		        } catch (SQLException e) {
		            Message.showError(e.toString());
		        } 	
	  	      	Stage stage = (Stage) btnOK.getScene().getWindow();
	  	      	stage.close();
	    	} else {
	            Message.showMessage("Password confirmed does not match, Try again");
	    	}
	    }

	    @FXML
	 // Click Cancel button not to change password
	    public void cancelButtonAction() {
	      Stage stage = (Stage) btnCancel.getScene().getWindow();
	      stage.close();
	    }

		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			// TODO Auto-generated method stub
			
		}
	    // Get username and old password from MainPrgController 
	    public void getOldUser(String user, String pass) {
	    	User = user;
	    	oldPass = pass;
	    }

}
