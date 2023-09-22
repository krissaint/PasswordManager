package application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Message {
	private static Message message = new Message();
	private Message() {
	}
	public static Message getMessage() {
		return message;
	}
	public static void showMessage(String msg) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information Dialog");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static void showWarning(String msg) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning Dialog");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	public static void showError(String msg) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error Dialog");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}

		
}