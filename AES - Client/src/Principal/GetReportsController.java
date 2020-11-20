package Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Course.Course;
import Lecturer.Lecturer;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Student.student;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 *this class responsible on reports windows of the principal 
 *this window can show all students/lecturers/courses
 *
 */
public class GetReportsController implements Initializable {
	public static Stage reportPrin;
	/** course list */
	private ArrayList<Course> courselist;
	/** lectures list */
	private ArrayList<Lecturer> lecturerlist;
	/** student list */
	private ArrayList<student> studentlist;
	public static student principalComboChoice;
	public static Course principalComboChoiceForCourse;
	public static Lecturer principalComboChoiceforLecturer;

	@FXML
	private ComboBox<Lecturer> lectCombo;

	@FXML
	private Button submit3;

	@FXML
	private RadioButton studBtn;

	@FXML
	private ComboBox<Course> courseCombo;

	@FXML
	private ComboBox<student> studentCombo;

	@FXML
	private RadioButton lecBtn;

	@FXML
	private ToggleGroup G1;

	@FXML
	private Button BackBtn10;

	@FXML
	private RadioButton courseBtn;
	public static int cid;
	public static int uid;

	


	public void start() {
		reportPrin = new Stage();

		String title = "Get Reports";
		String srcFXML = "/Principal/Principal-Reports.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			reportPrin.setTitle(title);
			reportPrin.setScene(scene);
			reportPrin.setResizable(false);
			reportPrin.show();

			reportPrin.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					back();
				}
			});
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			e.printStackTrace();
		}

	}

	/**
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {
			PrincipalMenuController.hidefunc(reportPrin);
			PrincipalMenuController.mainStage.show();
		} catch (Exception e) {
			reportPrin.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * lock other comboBoxes and let only work with selected radioBTN (case lecturer
	 * pressed)
	 */
	public void lecturerRadioBTN() {
		lectCombo.setDisable(false);
		studentCombo.getSelectionModel().clearSelection();
		studentCombo.setDisable(true);
		courseCombo.getSelectionModel().clearSelection();
		courseCombo.setDisable(true);
	}

	/**
	 * lock other comboBoxes and let only work with selected radioBTN (case course
	 * pressed)
	 */
	public void coursesRadioBTN() {
		courseCombo.setDisable(false);
		studentCombo.getSelectionModel().clearSelection();
		studentCombo.setDisable(true);
		lectCombo.getSelectionModel().clearSelection();
		lectCombo.setDisable(true);
	}

	/**
	 * /lock other comboBoxes and let only work with selected radioBTN (case student
	 * pressed)
	 */
	public void studentRadionBTN() {
		studentCombo.setDisable(false);
		courseCombo.getSelectionModel().clearSelection();
		courseCombo.setDisable(true);
		lectCombo.getSelectionModel().clearSelection();
		lectCombo.setDisable(true);
	}

	/**
	 * Show an Alert dialog with custom info
	 * 
	 * @param type
	 *            type alert
	 * @param title
	 *            title window
	 * @param header
	 *            header of the message
	 * @param content
	 *            message
	 */
	// function for alert windows -> get the details and know what print
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * initialize the window
	 */
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		// initialize the combo boxes
		initComboBoxLecturers();
		initComboBoxCourses();
		initComboBoxStudents();
		setLocks();

	}

	/**
	 * In initialize lock the comboBoxes that not selected
	 */
	private void setLocks() {
		courseCombo.setDisable(true);
		studentCombo.setDisable(true);
	}

	/**
	 * initialize the combo box for all lecturers
	 */
	private void initComboBoxLecturers() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAllLecturers);

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
					ObservableList<Lecturer> observelistLecturer;

					// filling the returned information from the server
					lecturerlist = p.<Lecturer>convertedResultListForCommand(Command.getAllLecturers);

					// checking the list
					if (lecturerlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					// filling the information in the combobox
					observelistLecturer = FXCollections.observableArrayList(lecturerlist);
					lectCombo.setItems(observelistLecturer);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * initialize the combo box for all courses
	 */
	private void initComboBoxCourses() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAllCourses);

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
					ObservableList<Course> observelistCourse;

					// filling the returned information from the server
					courselist = p.<Course>convertedResultListForCommand(Command.getAllCourses);

					// checking the list
					if (lecturerlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					// filling the information in the combobox
					observelistCourse = FXCollections.observableArrayList(courselist);
					courseCombo.setItems(observelistCourse);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * initialize the combo box for all students
	 */
	private void initComboBoxStudents() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAllStudents);

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
					ObservableList<student> observelistStudents;

					// filling the returned information from the server
					studentlist = p.<student>convertedResultListForCommand(Command.getAllStudents);

					// checking the list
					if (lecturerlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					// filling the information in the combobox
					observelistStudents = FXCollections.observableArrayList(studentlist);
					studentCombo.setItems(observelistStudents);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
/**
 * function for case if the principal wants to see some reports
 */
	public void showBtnPressed() {

		try {// if the principal wants student report
			if (studBtn.isSelected()) {
				principalComboChoice = studentCombo.getValue(); // save the choise
				reportPrin.hide();
				PrincipalStudentHistogramController studentReport = new PrincipalStudentHistogramController();
				studentReport.start();
			}
		} catch (Exception e) {
			reportPrin.close();
			System.out.println(e);
			e.printStackTrace();
		}

		try {// if the principal wants course report
			if (courseBtn.isSelected()) {
				principalComboChoiceForCourse = courseCombo.getValue(); // save the choise
				cid = courseCombo.getValue().getcId();
				reportPrin.hide();
				PrincipalCourseHistogramController courseReport = new PrincipalCourseHistogramController();
				courseReport.start();
			}
		} catch (Exception e) {
			reportPrin.close();
			System.out.println(e);
			e.printStackTrace();
		}
		try {// if the principal wants lecturer report
			if (lecBtn.isSelected()) {
				principalComboChoiceforLecturer = lectCombo.getValue(); // save the choise
				 
				   Lecturer lecturerNAme = lectCombo.getValue();
					  uid = lecturerNAme.getuId();
				reportPrin.hide();
				PrincipalLecturerHistogramController lecturerReport = new PrincipalLecturerHistogramController();
				lecturerReport.start();
			}
		} catch (Exception e) {
			reportPrin.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
}