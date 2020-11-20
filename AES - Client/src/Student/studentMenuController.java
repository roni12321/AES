package Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Login.LoginController;
import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * Class in the Student package
 * Handles the student menu presentation
 * @author Andrey
 *
 */
public class studentMenuController implements Initializable  {
	 @FXML
	 private Text UsernameTXT;
   
	/**
	 * current stage
	 */
	public static Stage StudentMenu;

	/**
	 * Contains login details
	 */
	private static LoginController loginController;
	
	
/**
 * Starts the menu window
 * @param primaryStage - stage
 * @throws Exception - exception
 */
	public void start(Stage primaryStage) throws Exception {
		this.StudentMenu = primaryStage;
		String title = "student";
		String srcFXML = "/Student/Student-menu.fxml";
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
		      		        if (type == okButton)
		      		        {
		      		        	loginController.performLoggedOut(LoginController.userLogged);
		      		        	System.exit(0);
		      		        } 
		      		        else
		      		        {
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
 * Sets the logged-in user, in this case student, to a variable
 * @param login - the logged in user
 */
	public void setLoginController(LoginController login) {
		loginController = login;
	}
	
	/**
	 * Show the computerized exam window
	 */
	public void compExam() {
		StudentMenu.hide();
		CompExamController CompExam=new CompExamController();
		CompExam.start();
	}
	
	/**
	 * Show the manual exam window
	 */
	public void manExam() {
		StudentMenu.hide();
		ManExamController manExam=new ManExamController();
		manExam.start();
	}
	
	
	
	
		
	
	/**
	 * Show student grades and previously taken exams
	 */
	public void checkgrade() {
		StudentMenu.hide();
		CheckGradeController CheckGrade=new CheckGradeController();
		CheckGrade.start();
	}
	
	/**
	 * Show student exam copy window
	 */
	public void getExamCopy() {
		StudentMenu.hide();
		ExamCopyController examCopy=new ExamCopyController();
		examCopy.start();
	}
	
	/**
	 * A function that hides the current stage
	 * @param stage- the stage that must be hidden
	 */
	public static void hidefunc(Stage stage) {
		stage.hide();
		return;
	}

	/**
	 * Handle the log-out event
	 */
	public void performLoggedOutHandler()
	{
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Logged Out");
		alert.setContentText("Are you Sure?");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		
		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
		        if (type == okButton)
		        {
		        	loginController.performLoggedOut(LoginController.userLogged);
		        } 
		});
	}

	/**
	 * Set the users name at the bottom of the screen
	 */
	public void initialize(URL location, ResourceBundle resources) {
		UsernameTXT.setText("Welcome back"+" " +LoginController.userLogged.getUsername());
	}
	
}