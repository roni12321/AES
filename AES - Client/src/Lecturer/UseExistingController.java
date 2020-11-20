package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Course.Course;
import Exam.Exam;
import Exam.ExamController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * this class is a controller for the UseExisting screen
 * in this screen courses ID and exam ID will showed to the lecturer for selection
 * @author ronyalensky
 *
 */
public class UseExistingController implements Initializable {

	@FXML
	private ComboBox<Course> ExistingCourseCombo;

	@FXML
	private ComboBox<Exam> ExistingExamCombo;

	@FXML
	private Button ExistingShowExam;

	/**
	 *  Variables decelerations
	 */
	public static Stage useexistingstage;
	public static String course, exid;
	/**
	 *  public Exam viewSelectedExam;
	 */

	/**
	 *  course list 
	 */
	private ArrayList<Course> courselist;

	/**
	 *  exams list 
	 */
	private ArrayList<Exam> examslist;


	public void start() {
		useexistingstage = new Stage();

		String title = "Use Existing Exam";
		String srcFXML = "/Lecturer/Lecturer-Use-Existing-Exam.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			useexistingstage.setTitle(title);
			useexistingstage.setScene(scene);
			useexistingstage.setResizable(false);
			useexistingstage.show();

			useexistingstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(useexistingstage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						useexistingstage.close();
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
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * a method that Initialize the combo box with all the exams regards the
	 * specific lecturers via a query that asks for the lecturer ID and finds the
	 * matching exams to the specific lecturer
	 */

	private void initComboBox() {

		Packet packet = new Packet();
		/**
		 * adding command
		 */
		packet.addCommand(Command.getCoursesbyLid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((Lecturer) LoginController.userLogged));

		packet.setParametersForCommand(Command.getCoursesbyLid, param);

		/**
		 * sending the packet
		 */
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {
					/**
					 * building new packet
					 */
					ObservableList<Course> observelistCourse;
					/**
					 * filling the returned information from the server
					 */
					courselist = p.<Course>convertedResultListForCommand(Command.getCoursesbyLid);

					/**
					 * checking the list
					 */
					if (courselist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					/**
					 * filling the information in the combobox
					 */
					observelistCourse = FXCollections.observableArrayList(courselist);
					ExistingCourseCombo.setItems(observelistCourse);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initComboBox();
		getallExam();

	}

	/**
	 * get all the exam from the DB
	 */

	private void getallExam() {

		Packet packet = new Packet();
		/**
		 * adding command
		 */
		packet.addCommand(Command.getExams);

		/**
		 * sending the packet
		 */
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {

					ArrayList<Exam> listExam;
					/**
					 * filling the information in the combobox
					 */
					listExam = p.<Exam>convertedResultListForCommand(Command.getExams);
					examslist = listExam;
				}
			}
		});

		send.start();
	}

	/**
	 * action on Exam combo box - if the user choose something in the Combo Box the
	 * method matches what the user seleceted and compare it inside the exams list
	 * array
	 */

	public void courseOnAction() {

		Course SelectedExam = ExistingCourseCombo.getValue();
		ArrayList<Exam> examcombo = new ArrayList<Exam>();

		for (Exam ex : examslist) {
			if (ex.getCourseId() == SelectedExam.getcId()) {
				examcombo.add(ex);

			}

		}

		/**
		 * building new packet
		 */
		ObservableList<Exam> observelistEXam;

		/**
		 * filling the information in the combobox
		 */
		observelistEXam = FXCollections.observableArrayList(examcombo);
		ExistingExamCombo.setItems(observelistEXam);

	}

	/**
	 * if the user pressed on button execution - open the next Stage
	 */
	public void executionCode() {

		Course Selectedcourse = ExistingCourseCombo.getValue();
		Exam SelectedExam = ExistingExamCombo.getValue();

		if (Selectedcourse == null || SelectedExam == null) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
		}

		else {
			course = Selectedcourse.getcName();
			exid = SelectedExam.getExamId();

			useexistingstage.hide();
			ExecutionCodeController exec = new ExecutionCodeController();
			exec.start();

		}
	}

	/*
	 * function that open the next Stage - "Exam View Stage" also the method checks
	 * if one of the combo boxes is empty, if so - an alert would pop
	 */
	public void showExam() {
		Course selectedCourse = ExistingCourseCombo.getValue();
		Exam selectedExam = ExistingExamCombo.getValue();

		if (selectedCourse == null || selectedExam == null) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
		}

		else {
			exid = ExistingExamCombo.getValue().getExamId();
			useexistingstage.hide();
			ExamController exViewEx = new ExamController();
			exViewEx.start();
		}

	}

	/**
	 * function that hide the current Stage
	 * @param s - stage
	 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}

	/**
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {
			lecturerMenuController.hidefunc(useexistingstage);
			lecturerMenuController.lecturermenu.show();
		} catch (Exception e) {
			useexistingstage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
}
