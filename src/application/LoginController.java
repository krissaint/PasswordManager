/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * Sign up for the first time to create a new database named account.db with 2 new tables Users and Account list
 * Store username and password created to Users table
 * Then change screen to Login to check the validation of created user
 * If valid, go to next stage by loading account.fxml and AccountController.java
 *
 * @author baona
 */
public class LoginController implements Initializable {
    @FXML
    private TextField txtUser;
    @FXML
    private PasswordField txtPass;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSign;    
    @FXML
    private Button close;    
    @FXML
    private Button btnChangePass;
	private Connection conn;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // For the first time, display Sign up button only
    	// After signing up, display Login button only
        String dir = System.getProperty("user.dir");
        File file = new File(dir+"/"+SqLiteConnection.getDbName());

        File fileMem = new File(dir+"/memory");
        if(fileMem.exists()) {
        	fileMem.delete();
        }

        if (!file.exists()){
            btnLogin.setVisible(false);
            btnSign.setVisible(true);
            btnChangePass.setVisible(false);
        } else{
            btnLogin.setVisible(true);
            btnSign.setVisible(false);
            btnChangePass.setVisible(true);
        }
    }    

    @FXML
    private void login(ActionEvent event){
        // Check the validation of Username and password from Users table
        // if valid, loading Account.fxml and AccountController.java
    	if (chk_user(txtUser.getText(),txtPass.getText())) {
    		screen("Account Manager","Account.fxml");
    	}
    }

    private boolean chk_user(String user, String pass){
        // Check the validation of Username and password from Users table
    	// If valid, return True. Otherwise, return False and displays an error message
        conn = SqLiteConnection.connector();
        String sql = "select * from users where username = ? and password = ?";
        PreparedStatement pst;
        boolean isValid = false;
		try {
			pst = conn.prepareStatement(sql);
	        pst.setString(1, user);
	        pst.setString(2, pass);
	        ResultSet rs = pst.executeQuery();
	        if (conn == null){
	        	Message.showWarning("Connection error");
	        }
	        else{
	        	if (rs.next()) {
	        		rs.close();
	        		isValid = true;
	        	} else {
	            	Message.showWarning("Invalid Username or Password!");
	            	isValid = false;
	        	}
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isValid;
    }
    
    public void screen(String title, String fxml){
    	 // Create a new stage of Account.fxml and AccountController.java.
        try {
        	Scene scene1 = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene1);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void signUp() throws SQLException{

    	// create a new database named account.db with 2 new tables Users and Account list
        if(txtUser.getText() == ""){
        	Message.showWarning("Please input a user name");
            return;
        }
        conn = SqLiteConnection.connector();
        String sql1 = "CREATE TABLE Users (Username TEXT NOT NULL UNIQUE,Password TEXT,Type TEXT)";
        String sql2 = "CREATE TABLE AccountList (AccountName TEXT NOT NULL UNIQUE COLLATE RTRIM," + 
                "Category TEXT COLLATE RTRIM,Username TEXT COLLATE RTRIM," + 
                "Password TEXT COLLATE RTRIM,Url TEXT COLLATE RTRIM,Notes TEXT," +
                "PRIMARY KEY (AccountName))";
        Statement stat = conn.createStatement();
        stat.executeUpdate(sql1);
        stat.executeUpdate(sql2);

        // Store Username and Password to Users Table
        String sql;
        sql = "insert into Users (UserName,Password) values (?,?)";
        PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtUser.getText());
            pst.setString(2, txtPass.getText());
            pst.execute();
        btnLogin.setVisible(true);
        btnSign.setVisible(false);
        btnChangePass.setVisible(true);

        txtUser.setText("");
        txtPass.setText("");
//        Message.showMessage("Sign up successfully, Next is Login to access your account list");
    }

    @FXML
    public void CloseButtonAction() throws SQLException {
      System.exit(0);
    }
    @FXML
    public void stageChangePsw() throws IOException{
    // Check the validation of Username and password from Users table
    // if true, Create a new stage of Change Password
    	if (chk_user(txtUser.getText(),txtPass.getText())) {
        	FXMLLoader scChangPsw = new FXMLLoader(getClass().getResource("ChangePass.fxml"));
        	Parent root = (Parent) scChangPsw.load();
        	ChangePassController changePassController = scChangPsw.getController();
        	changePassController.getOldUser(txtUser.getText(), txtPass.getText());
            Stage stage = new Stage();
            stage.setTitle("Change Password");
            stage.setScene(new Scene(root));
            stage.show();
    	}
   }
}
