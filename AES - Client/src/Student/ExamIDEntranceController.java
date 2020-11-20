package Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Answer.Answer;
import Exam.FinishedExam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * Class in Student package, Handles the ID submittion for the exam
 * @author Andrey
 *
 */
public class ExamIDEntranceController implements Initializable {

	public static Stage ExamID;
	private int ID;
    @FXML
    private TextField IDtxt;
    @FXML
    private Button startID;
    private static String exID;
    private static ArrayList<Integer> CourseList;
    private static int CID;
/**
 * Loads the Enter-ID window
 * @param ExID - The Exam ID from the previous stage
 */
	public void start(String ExID) {
		exID=ExID;
		ExamID = new Stage();

		String title = "Computerized Exam";
		String srcFXML = "/Student/ExamIDEntrance.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ExamID.setTitle(title);
			ExamID.setScene(scene);
			ExamID.setResizable(false);
			ExamID.show();

			ExamID.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						studentMenuController.hidefunc(ExamID);
						studentMenuController.StudentMenu.show();
					} catch (Exception e) {
						ExamID.close();
						System.out.println(e);
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Back function
	 */
	public void back() {

		try {
			studentMenuController.hidefunc(ExamID);
			CompExamController.CompExam.show();
		} catch (Exception e) {
			ExamID.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * A function that checks the ID of the student and if correct than forward to the exam
	 */
	public void Start() {
		if (IDtxt.getText().equals("")) {
			displayAlert(AlertType.ERROR, "Error", "Please enter your ID",null);
			return;
		}
		if((boolean)LoginController.userLogged.getId().equals(IDtxt.getText())) {
	
		ID=Integer.parseInt(IDtxt.getText());
		intiexam();
		ExamID.hide();
		ExamFillController ExamFillController = new ExamFillController();
		ExamFillController.start(ID,exID);
		}
		else {
			displayAlert(AlertType.ERROR, "Error", "The ID you've entered doesnt match the ID logged in",null);
		}
		
	}
	/**
	 * Initialize the exam details (activates flags for further use)
	 */
	private void intiexam() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.initExam);
		FinishedExam init = new FinishedExam(ID,CID,exID,1,0);
		ArrayList<Object> param = new ArrayList<>(Arrays.asList(init));

		packet.setParametersForCommand(Command.initExam, param);
		// sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState()) {

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
		
	}
	
	/**
	 * Gets the course ID of the exam
	 */
	public void getCID() {

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCourseIDbyExamID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exID));

		packet.setParametersForCommand(Command.getCourseIDbyExamID, param);
		// sending the packet
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState()) {

					// filling the returned information from the server
					CourseList = p.<Integer>convertedResultListForCommand(Command.getCourseIDbyExamID);
					// checking the list
					if (CourseList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "");
						return;
					}
					// check if the student has the same department code as the exam//

					CID = CourseList.get(0);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * A function that displays the alerts 
	 * @param type- what type of error
	 * @param title- set the title of the alert
	 * @param header- header for the alert
	 * @param content- what error happened
	 */
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		getCID();
		
	}
}
