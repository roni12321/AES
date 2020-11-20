package Lecturer;

import java.net.URL;
import java.util.ResourceBundle;

import Login.LoginController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class lecturerMenuController implements Initializable {

	/**
	 * current stage
	 */
	public static Stage lecturermenu;

	/**
	 * Contains login details
	 */
	public static LoginController loginController;
	@FXML
	private Text UsernameTXT;


	public void start(Stage primaryStage) throws Exception {
		this.lecturermenu = primaryStage;

		String title = "Lecturer";
		String srcFXML = "/Lecturer/Lecturer-menu.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			primaryStage.setTitle(title);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Logged Out");
					alert.setContentText("Are you Sure?");
					ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
					ButtonType noButton = new ButtonType("No", ButtonData.NO);

					alert.getButtonTypes().setAll(okButton, noButton);
					alert.showAndWait().ifPresent(type -> {
						if (type == okButton) {
							loginController.performLoggedOut(LoginController.userLogged);
							System.exit(0);
						} else {
							we.consume();
						}
					});
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}

	}

	/**
	 * Saves the logged user details
	 * @param login -
	 */
	public void setLoginController(LoginController login) {
		loginController = login;
	}
/**
 * Opens Build question stage
 */
	public void BuildQuestion() {
		lecturermenu.hide();
		BuildQuestionController Buildq = new BuildQuestionController();
		Buildq.start();
	}
/**
 * Opens build exam stage
 */
	public void BuildExam() {
		lecturermenu.hide();
		BuildExamController BuildExam = new BuildExamController();
		BuildExam.start();
	}
/**
 * Opens use existing exams stage
 */
	public void Existing() {
		lecturermenu.hide();
		UseExistingController exist = new UseExistingController();
		exist.start();
	}
/**
 * Oepns check exam stage
 */
	public void checkExam() {
		lecturermenu.hide();
		CheckExamController checkExam = new CheckExamController();
		checkExam.start();
	}
/**
 * Opens reports stage
 */
	public void reports() {
		lecturermenu.hide();
		reportsController reports = new reportsController();
		reports.start();
	}
/**
 * Opens the exam time change / lock exam stage
 */
	public void ExamTimeChange() {
		lecturermenu.hide();
		ExamTimeController ExamTime = new ExamTimeController();
		ExamTime.start();
	}
	/**
	 * Opens the cheat checking stage
	 */
	public void CheatCheck() {
		lecturermenu.hide();
		CheatCheckController CheatCheck = new CheatCheckController();
		CheatCheck.start();
	}
/**
 * Hide method for stages
 * @param s - Stage
 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}

	/**
	 * logout - case - ask the lecturer if he sure and exit from the lecturer user
	 */
	public void performLoggedOutHandler() {
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Logged Out");
		alert.setContentText("Are you Sure?");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);

		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
			if (type == okButton) {
				loginController.performLoggedOut(LoginController.userLogged);

			}
		});
	}

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		UsernameTXT.setText("Welcome back" + " " + LoginController.userLogged.getUsername());

	}

}