/*
* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * FXML Controller class
 * TableView: Display the list of selected account records
 * Text fields:  to inputs data
 * Buttons: Add, Edit, Delete to modify AccountList table
 * Link Url button: to open the website
 * Clear button: clear all text fields to input date for adding
 * Search fields: filter data based on text input immediately
 * @author baona
 */
public class AccountController implements Initializable {
	ObservableList<String> list = FXCollections.observableArrayList();
	
    @FXML
    private TextField txt_acc;
    @FXML
    private ComboBox<String> cb_cat;    
    @FXML
    private TextField txt_user;
    @FXML
    private TextField txt_pass;
    @FXML
    private TextField txt_url;
    @FXML
    private TextField txt_note;
    @FXML
    private TextField txtSearch;    
    @FXML
    private TableView<Account> table;
    @FXML
    private TableColumn<Account, String> col_acc;
    @FXML
    private TableColumn<Account, String> col_cat;
    @FXML
    private TableColumn<Account, String> col_user;
    @FXML
    private TableColumn<Account, String> col_pass;
    @FXML
    private TableColumn<Account, String> col_url;
    @FXML
    private TableColumn<Account, String> col_note;
    @FXML
    private Scene scenceData;
    @FXML
    private Button close;
    @FXML
    private Button btnImport;

    private List<String> lstFile;
	private Connection conn = SqLiteConnection.connector();

    ObservableList<Account> oblist = FXCollections.observableArrayList();
    ObservableList<Account> dataList; // = FXCollections.observableArrayList();
    private PreparedStatement pst = null;
    
    int index = -1;
    
    public void add_Acc(){
    	if (txt_acc.getText().compareTo("")==0) {
    		Message.showWarning("Account name cannot be blank");
   			return;
    	}
    	// Add a record
        String sql;
        sql = "insert into AccountList (AccountName,Category,UserName,Password,Url,Notes)"
                + "values(?,?,?,?,?,?)";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txt_acc.getText());
            pst.setString(2, cb_cat.getValue());
            pst.setString(3, txt_user.getText());
            pst.setString(4, txt_pass.getText());
            pst.setString(5, txt_url.getText());
            pst.setString(6, txt_note.getText());
            pst.execute();
            updateTable();
            search_Acc();
            
        } catch (SQLException ex) {
        	Message.showError("Duplicate account name");
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void del_Acc(){
    	// delete selected record
        String sql;
        sql = "delete from AccountList where AccountName = ?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txt_acc.getText());
            pst.execute();
            updateTable();
            clear();
            search_Acc();

        } catch (SQLException ex) {
            Message.showError(ex.toString());
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *
     * @param event
     */
    @FXML
    void getSelected(){
    	// display selected record
        index = table.getSelectionModel().getSelectedIndex();
        if (index <= -1) return;
        txt_acc.setText(col_acc.getCellData(index));
        cb_cat.setValue(col_cat.getCellData(index));
        txt_user.setText(col_user.getCellData(index));
        txt_pass.setText(col_pass.getCellData(index));
        txt_url.setText(col_url.getCellData(index));
        txt_note.setText(col_note.getCellData(index));
    }
    
    @FXML
    void clear(){
    	// clear all text field to input new data for adding
        txt_acc.setText("");
        cb_cat.setValue("");
        txt_user.setText("");
        txt_pass.setText("");
        txt_url.setText("");
        txt_note.setText("");
        txtSearch.setText("");
    }
    
    @FXML
    void link(){
    	String URL = txt_url.getText();
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));
        } catch (IOException ex) {
            Message.showError(ex.toString());
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
      }
        catch (IllegalArgumentException ex) {
            Message.showError(ex.toString());
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    @FXML
    void search_Acc() {
    	// filter data
        col_acc.setCellValueFactory(new PropertyValueFactory<>("Accountname"));
        col_cat.setCellValueFactory(new PropertyValueFactory<>("Category"));
        col_user.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_pass.setCellValueFactory(new PropertyValueFactory<>("Password"));
        col_url.setCellValueFactory(new PropertyValueFactory<>("Url"));
        col_note.setCellValueFactory(new PropertyValueFactory<>("Notes"));

        dataList = getDataAcc();
        table.setItems(dataList);
        FilteredList<Account> filteredData = new FilteredList<>(dataList, b -> true);
	txtSearch.textProperty().addListener((oservable, oldValue, newValue) -> {
        filteredData.setPredicate(acc -> {
            if(newValue == null || newValue.isEmpty()) return true;
            String lowerCaseFilter = newValue.toLowerCase();
            if (acc.getAccountname().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Accountname
	  	else if (acc.getCategory().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Category
                    else if (acc.getUsername().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Username
			else if (acc.getPassword().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Password
                            else if (acc.getUrl().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Url
			  	else if (acc.getNotes() != null && acc.getNotes().toLowerCase().indexOf(lowerCaseFilter) != -1) return true; // filter match Notes
                                    else return false;});
	  });
	  SortedList<Account> sortedData = new SortedList<>(filteredData);
	  sortedData.comparatorProperty().bind(table.comparatorProperty());
	  table.setItems(sortedData);
    }
    
    @FXML
    void edit_Acc() {
    	// modify data
        String value1 = txt_acc.getText();
        String value2 = cb_cat.getValue();
        String value3 = txt_user.getText();
        String value4 = txt_pass.getText();
        String value5 = txt_url.getText();
        String value6 = txt_note.getText();
        String sql = "update AccountList set Accountname = '" + value1 +
        "', Category = '" + value2 +
        "', Username = '" + value3 +
        "', Password = '" + value4 +
        "', Url = '" + value5 +
        "', Notes = '" + value6 + 
        "' where Accountname = '" + value1 +"'";
        try {
            pst = conn.prepareStatement(sql);
            pst.execute();
            updateTable();
            search_Acc();
        } catch (SQLException e) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, e);
            Message.showError(e.toString());
	}
    }

    @FXML
    void updateTable(){
    	// refresh the list view
        col_acc.setCellValueFactory(new PropertyValueFactory<>("Accountname"));
        col_cat.setCellValueFactory(new PropertyValueFactory<>("Category"));
        col_user.setCellValueFactory(new PropertyValueFactory<>("Username"));
        col_pass.setCellValueFactory(new PropertyValueFactory<>("Password"));
        col_url.setCellValueFactory(new PropertyValueFactory<>("Url"));
        col_note.setCellValueFactory(new PropertyValueFactory<>("Notes"));
        oblist = getDataAcc();
        table.setItems(oblist);
    }
    
    @FXML
    public void CloseButtonAction() {
      Stage stage = (Stage) close.getScene().getWindow();
      stage.close();
    }
    
    @FXML
    void importCSV() {
    	String csvFile = singleFileChooser();
    	if (csvFile.compareTo("") != 0) {
    		String sql = "insert into AccountList values(?, ?, ?, ?, ?, ?)";
            try {
				pst = conn.prepareStatement(sql);
				ArrayList<Account> listAcc = getListAccFromCSV(csvFile);
				for (int i = 0; i < listAcc.size(); i++) {
					pst.setString(1, listAcc.get(i).getAccountname());
					pst.setString(2, listAcc.get(i).getCategory());
					pst.setString(3, listAcc.get(i).getUsername());
					pst.setString(4, listAcc.get(i).getPassword());
					pst.setString(5, listAcc.get(i).getUrl());
					pst.setString(6, listAcc.get(i).getNotes());
					pst.executeUpdate();
				}
		        updateTable();
		        search_Acc();
				
			} catch (SQLException e) {
	        	Message.showError("Duplicate account name");
	            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, e);
				e.printStackTrace();
			}
    	}
    }
    
    public static ArrayList<Account> getListAccFromCSV (String filePath){
    	BufferedReader bReader = null;
    	InputStreamReader isr = null;
    	FileInputStream fis = null;
		ArrayList<Account> listResult = new ArrayList<>();
		try {
			fis = new FileInputStream(filePath);
			isr = new InputStreamReader(fis);
//			BufferedReader bReader = new BufferedReader(new FileReader(csvFile));
			bReader = new BufferedReader(isr);
			String line = null;
			String[] strAcc = null;
			while(true) {
				// get 1 line
				line = bReader.readLine();
				// check if line get empty, end loop
				if (line == null) break;
				else strAcc = line.split(",");
				listResult.add(new Account(strAcc[0],strAcc[1],strAcc[2],strAcc[3],strAcc[4],strAcc[5]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();	
		} finally {
			try {
				bReader.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listResult;
    }
    
    public String singleFileChooser() {
    	FileChooser fc = new FileChooser();
    	fc.getExtensionFilters().add(new ExtensionFilter("CSV files",lstFile));
    	File f = fc.showOpenDialog(null);
    	if(f != null) {
    		return f.getAbsolutePath();
    	} else {
    		return "";
    	}
    }
    
    public ObservableList<Account> getDataAcc(){
        ObservableList<Account> list = FXCollections.observableArrayList();
                try {
                    PreparedStatement ps = conn.prepareStatement("select * from AccountList");
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()){
                        list.add(new Account(rs.getString("AccountName"), 
                            rs.getString("Category"),rs.getString("Username"),
                            rs.getString("Password"),rs.getString("Url"),rs.getString("Notes")));
                     }
                } catch (SQLException ex) {
                    Logger.getLogger(SqLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
                return list;
    }

    private void loadData() {
    	list.removeAll(list);
    	String a = "Bank";
    	String b = "File";  	
    	String c = "Education";
    	String d = "Email";   	
    	String e = "Equipment";
    	String f = "Shopping";
    	String g = "Social Network";
    	String h = "Others";
    	
    	list.addAll(a,b,c,d,e,f,g,h);
    	cb_cat.setItems(list);
    }
    
    /**
     * Initializes the controller class.
     * @pa
     * @param rbram url
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    	lstFile = new ArrayList<>();
    	lstFile.add("*.csv");
    	lstFile.add("*.CSV");
    	loadData();
        updateTable();
        search_Acc();
    } 
   
    
}
