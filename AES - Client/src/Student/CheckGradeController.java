package Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Course.Course;
import Exam.FinishedExam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.FinishedQuestion;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * A class in student package, Handles the grade and exam viewing by the student
 * @author Andrey
 *
 */
public class CheckGradeController implements Initializable {
	/**
	 * Variables decelerations
	 */
	public static Stage CheckGrade;
	private ArrayList<Course> courselist;
	private ArrayList<FinishedExam> examlist;
	private ArrayList<Float> gradelist;

	@FXML
	private ComboBox<Course> courseCombo;
    @FXML
    private TextArea GradeFields;
	@FXML
	private TextField avgLabel;
/**
 * Loads the grade viewing window
 */
	public void start() {
		CheckGrade = new Stage();

		String title = "Grades";
		String srcFXML = "/Student/Student-Grades.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			CheckGrade.setTitle(title);
			CheckGrade.setScene(scene);
			CheckGrade.setResizable(false);
			CheckGrade.show();

			CheckGrade.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						studentMenuController.hidefunc(CheckGrade);
						studentMenuController.StudentMenu.show();
					} catch (Exception e) {
						CheckGrade.close();
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
	 * Handles Back button
	 */
	public void back() {

		try {
			studentMenuController.hidefunc(CheckGrade);
			studentMenuController.StudentMenu.show();
		} catch (Exception e) {
			CheckGrade.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * Build the course combobox
	 */
	private void initComboBox() {
		// TODO Auto-generated method stub

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCoursesbySid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((student) LoginController.userLogged));

		packet.setParametersForCommand(Command.getCoursesbySid, param);
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
					courselist = p.<Course>convertedResultListForCommand(Command.getCoursesbySid);

					// checking the list
					if (courselist.isEmpty() == true) {
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
	 * Get the course selected in the combobox
	 */
	public void onCourseSelect() {
		GradeFields.clear();
		Course cr = courseCombo.getValue();

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamBySid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((student) LoginController.userLogged, cr));

		packet.setParametersForCommand(Command.getExamBySid, param);
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
					examlist = p.<FinishedExam>convertedResultListForCommand(Command.getExamBySid);

					// checking the list
					if (examlist.isEmpty() == true) {
						GradeFields.setText("No exams in this course to show");
						return;
					}
					String Examlist=new String();
					for(int i=0;i<examlist.size();i++) {
						if(examlist.get(i).getView()==1) {
					String str = String.format("Exam Code:" + "%s" + "   " + "Grade:" + "%d" + "\r\n", examlist.get(i).getExID(),
							examlist.get(i).getGrade());
					Examlist=Examlist+str;
						}
					}
					if (Examlist.isEmpty()) GradeFields.setText("No exams in this course to show");
					else GradeFields.setText(Examlist);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}
	
	/**
	 * Get the avg number from the student
	 */
	private void initAVG() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAVGBySid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((student) LoginController.userLogged));

		packet.setParametersForCommand(Command.getAVGBySid, param);
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
					gradelist = p.<Float>convertedResultListForCommand(Command.getAVGBySid);

					// checking the list
					if (gradelist.isEmpty() == true) {
						avgLabel.setText("-");
						return;
					}
					String str = String.format("%.2f", gradelist.get(0));
					avgLabel.setText(str);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}

	/**
	 * Functions that must run on window open
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		initComboBox();
		initAVG();
	}
}