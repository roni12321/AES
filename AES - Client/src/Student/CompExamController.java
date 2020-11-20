package Student;

import java.util.ArrayList;
import java.util.Arrays;
import Exam.Exam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * Class in Student package, Handles the taking ofthe computerized exam by the student
 * @author Andrey
 *
 */
public class CompExamController {
	private static boolean Done=false; // If the exam was done by the student already
	private static String Code; //The exam code entered by the user 
	public static Stage CompExam;
	private static ArrayList<Exam> Examlist; // has the exam from the DB
	private static ArrayList<String> ExamDonelist; // Has the exams that are already done by the user
	private ArrayList<Integer> Deplist; // get the department code of the student
	@FXML
	private TextField codeTXT;
	@FXML
	private Text test;
	private static int SID; // Student ID
	private static int Dep;// Department ID
	private String examID; // Exam ID
	
/**
 * Present the Computerized exam gui
 */
	public void start() {
		CompExam = new Stage();

		String title = "Computerized Exam";
		String srcFXML = "/Student/Computerized-Menu.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			CompExam.setTitle(title);
			CompExam.setScene(scene);
			CompExam.setResizable(false);
			CompExam.show();

			CompExam.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						studentMenuController.hidefunc(CompExam);
						studentMenuController.StudentMenu.show();
					} catch (Exception e) {
						CompExam.close();
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
	 * Back button function
	 */
	public void back() {

		try {
			studentMenuController.hidefunc(CompExam);
			studentMenuController.StudentMenu.show();
		} catch (Exception e) {
			CompExam.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * A function that checks if there is an exam with the code provided by the
	 * student
	 */
	public void Submit() {
		Done=true;
		Code = codeTXT.getText();
		getDID();
		SID=((student) LoginController.userLogged).getsId();
		CheckIfExamDoneAlready();
		if (Code.equals("")) {
			displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No code was entered");
			return;
		}
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamByExCode);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(Code));

		packet.setParametersForCommand(Command.getExamByExCode, param);
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

					// building new packet

					// filling the returned information from the server
					Examlist = p.<Exam>convertedResultListForCommand(Command.getExamByExCode);
					// checking the list
					
					if (Examlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No active exams with that code");
						return;
					}

					if (Done == false) { // If the student didnt do the exam already continue
					
					// check if the student has the same department code as the exam//
					if (Dep == Examlist.get(0).getDepartmentId()) {
						examID = Examlist.get(0).getExamId();
						CompExam.hide();
						ExamIDEntranceController ExamIDEntranceController = new ExamIDEntranceController();
						ExamIDEntranceController.start(examID);
					} else {
						displayAlert(AlertType.ERROR, "Error", "You cannot take exams outside of your department",
								p.getExceptionMessage());
					}
					}
					else displayAlert(AlertType.ERROR, "Error", "",
							"Cannot do the same exam twice");
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

	/**
	 * A function that retrieves the department ID of the logged student
	 */
	public void getDID() {

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getDepartmentIDByUid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(LoginController.userLogged.getuId()));

		packet.setParametersForCommand(Command.getDepartmentIDByUid, param);
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

					// building new packet

					// filling the returned information from the server
					Deplist = p.<Integer>convertedResultListForCommand(Command.getDepartmentIDByUid);
					// checking the list
					if (Deplist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}
					Dep = Deplist.get(0);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "No exams with that code", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
	
/**
 * A function that checks if the student did the exam already
 */
	public void CheckIfExamDoneAlready() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamByExamIdAndSid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(Code,SID));

		packet.setParametersForCommand(Command.getExamByExamIdAndSid, param);
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

					// building new packet

					// filling the returned information from the server
					ExamDonelist = p.<String>convertedResultListForCommand(Command.getExamByExamIdAndSid);
					if ((ExamDonelist.isEmpty())) Done=false;
					return;
				}

				else {
					Done=true;
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
				
			}
		});

		send.start();
	}
}
