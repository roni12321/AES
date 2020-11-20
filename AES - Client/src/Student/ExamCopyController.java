package Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Course.Course;
import Exam.Exam;
import Lecturer.Lecturer;
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
 * This class is handling the ExamCopy screen
 * in this screen the student will select this courses and the matching exams
 * and then he could watch his finised exams copy.
 * @author ronyalensky
 *
 */
public class ExamCopyController implements Initializable {

	public static Stage ExamCopyStage;
	public static String course2 = null;
	public static String exid2 = null;
	public static ArrayList<Integer> flagList = new ArrayList<Integer>();

	/** 
	 * course list 
	 */
	private ArrayList<Course> courselist;
	/**
	 *  exams list 
	 */
	private ArrayList<Exam> examslist;

	@FXML
	private ComboBox<Exam> examCombo;

	@FXML
	private Button viewBtn;
	
	@FXML
	private ComboBox<Course> courseCombo;

	@FXML
	private Button BackBtn12;

	@FXML
	private Button downloadBtn;

	public void start() {
		ExamCopyStage = new Stage();

		String title = "View Exam Copy";
		String srcFXML = "/Student/Student-ExamCopy.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ExamCopyStage.setTitle(title);
			ExamCopyStage.setScene(scene);
			ExamCopyStage.setResizable(false);
			ExamCopyStage.show();

			ExamCopyStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				public void handle(WindowEvent we) {

					try {
						studentMenuController.hidefunc(ExamCopyStage);
						studentMenuController.StudentMenu.show();
					} catch (Exception e) {
						ExamCopyStage.close();
						System.out.println(e);
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}
	/**
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {
			studentMenuController.hidefunc(ExamCopyStage);
			studentMenuController.StudentMenu.show();
		} catch (Exception e) {
			ExamCopyStage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
	/**
	 * a method that Initialize the combo box with all the courses regards the
	 * specific student via a query that asks for the student ID and finds the
	 * matching courses to the specific student
	 */
	private void initComboBox() {

		Packet packet = new Packet();
		/**
		 *  adding command
		 */
		packet.addCommand(Command.getCoursesbySid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((student) LoginController.userLogged));

		packet.setParametersForCommand(Command.getCoursesbySid, param);
		/**
		 *  sending the packet
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
					 *  building new packet
					 */
					ObservableList<Course> observelistCourse;

					/**
					 *  filling the returned information from the server
					 */
					courselist = p.<Course>convertedResultListForCommand(Command.getCoursesbySid);

					/**
					 *  checking the list
					 */
					if (courselist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					/**
					 *  filling the information in the combobox
					 */
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
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * A method that get all the exam and from them select the exam that are
	 * connected to the course an to the student id
	 */

	public void getallExams() {

		Packet packet = new Packet();
		/**
		 *  adding command
		 */
		packet.addCommand(Command.getExamsBySidAndCid);
		ArrayList<Object> params = new ArrayList<>(
				Arrays.asList((student) LoginController.userLogged, courseCombo.getValue()));

		packet.setParametersForCommand(Command.getExamsBySidAndCid, params);
		/**
		 *  sending the packet
		 */
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {
					ObservableList<Exam> observelistExam;
					ArrayList<Exam> listExam;
					/**
					 *  filling the returned information from the server into a examList
					 */
					listExam = p.<Exam>convertedResultListForCommand(Command.getExamsBySidAndCid);
					examslist = listExam;
					observelistExam = FXCollections.observableArrayList(examslist);
					if(observelistExam.isEmpty())
					{
						displayAlert(AlertType.ERROR, "Error,","", " no Exam.");
						examCombo.setItems(observelistExam);

						return;
					}
					else
					{
					examCombo.setItems(observelistExam);
					if(listExam.isEmpty())
						examCombo.setDisable(true);
					else
						examCombo.setDisable(false);
					}
				}

			}
		});

		send.start();
	}
	
	/**
	 * method to check the flag of a specific exam, if that exam was chacked
	 */
	public void onExamAction() {
		
		if( examCombo.getSelectionModel().isEmpty())
		{
			return;

		}
		else {
		getFinishedFlag(examCombo.getValue().getExamId());
		}
	}
	private void getFinishedFlag(String examId) {
		 
		
		student studentNAme = ((student) LoginController.userLogged);
		int sid = studentNAme.getsId();
		
		Packet packet = new Packet();
		/**
		 *  adding command
		 */
		
		packet.addCommand(Command.getFinishedExamFlagByExId);
		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId , sid));
		packet.setParametersForCommand(Command.getFinishedExamFlagByExId, param);
		/**
		 *  sending the packet
		 */
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub
				if (p.getResultState()) {
					// filling the returned information from the server
					flagList = p.<Integer>convertedResultListForCommand(Command.getFinishedExamFlagByExId);
					if ( flagList.get(0) == 0) {
						displayAlert(AlertType.ERROR, "Error,","", " The lecturer did not check your exam yet.");
						viewBtn.setDisable(true);

						return;
					}
					else {
						//displayAlert(AlertType.INFORMATION, "Error,","", " The lecturer has checked your exam and you can now watch it");
						viewBtn.setDisable(false);

					}
				}
			}
		});

		send.start();
	}
/**
 *  A method that checks if both combos are filled, then the student can press View Exam and watch his exam
 */
	public void viewExam() {
		Course selectedCourse = courseCombo.getValue();
		Exam selectedExam = examCombo.getValue();

		if (selectedCourse == null || selectedExam == null) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
			
		}

		else {
	
			course2 = courseCombo.getValue().getcName();
			exid2 = examCombo.getValue().getExamId();
			ExamCopyStage.hide();
			StudentExamCopyViewerController exViewEx = new StudentExamCopyViewerController();
			exViewEx.start();
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

	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}

	// function that sets the exams in the exam combo after selecting a course in the first combo

	public void initialize(URL arg0, ResourceBundle arg1) {
		initComboBox();
	
	}
	



		
	
	public ArrayList<Exam> getExamslist() {
		return examslist;
	}

	public void setExamslist(ArrayList<Exam> examslist) {
		this.examslist = examslist;
	}

}
