package Principal;

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
import javafx.scene.control.ButtonType;
import javafx.scene.text.Text;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * this class responsible on principal menu
 */
public class PrincipalMenuController implements Initializable {
	@FXML
	private Text UsernameTXT;
	/**
	 * current stage
	 */
	public static Stage mainStage;

	/**
	 * Contains login details
	 */
	private static LoginController loginController;

	/**
	 * @param primaryStage
	 *            current stage to build
	 * @throws Exception
	 *             if there is exception while lunching and working
	 */
	public void start(Stage primaryStage) throws Exception {
		mainStage = primaryStage;

		String title = "principal";
		String srcFXML = "/Principal/principal-menu.fxml";

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
	 * set the user that online as principal
	 * 
	 * @param login - login
	 */
	public void setLoginController(LoginController login) {
		loginController = login;
	}

	/**
	 * function that hide the Stages that sent to her
	 * 
	 * @param s - s for stage
	 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}

	/**
	 * case - if the principal select on "get reports" Stage
	 */
	public void GetReports() {
		mainStage.close();
		GetReportsController getReports = new GetReportsController();
		getReports.start();
	}

	/**
	 * case - if the principal select on "Approve Time Extension" Stage
	 */
	public void Approval() {
		mainStage.close();
		PrincipalExtensionController approval = new PrincipalExtensionController();
		approval.start();
	}
	
	
	
	public void ExamReview() throws Exception
	{
		mainStage.close();
		ExamTableController new1 = new ExamTableController();
		new1.start();
		
	}
	
	
	public void QuestionReview() throws Exception
	{
		mainStage.close();
		QuestionTableController new1 = new QuestionTableController();
		new1.start();
		
	}
	
	

	/**
	 * Event Logged out that occurs when clicking on logout
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