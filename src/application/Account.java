package application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author baona
 */
public class Account {
    String Accountname,Category,Username,Password,Url,notes;

    public Account(String Accountname, String Category, String Username, String Password, String Url, String notes) {
        this.Accountname = Accountname;
        this.Category = Category;
        this.Username = Username;
        this.Password = Password;
        this.Url = Url;
        this.notes = notes;
    }

    public String getAccountname() {
        return Accountname;
    }

    public void setAccountname(String Accountname) {
        this.Accountname = Accountname;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
