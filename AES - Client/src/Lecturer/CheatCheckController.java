package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Cheat.Cheat;
import Course.Course;
import Exam.Exam;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * A class in lecturer package, that handles the cheat checking feature of the
 * AES
 * 
 * @author Andrey
 *
 */
public class CheatCheckController implements Initializable {
	/**
	 * Variables decelerations
	 */
	private static Stage cheatcheckstage;
	private static ArrayList<Course> CourseList;
	private static ArrayList<Exam> ExamList;
	private static ArrayList<Cheat> QuestionList;
	private static ArrayList<Cheat> Checkarr = new ArrayList();
	private static ArrayList<Cheat> FinalCheck = new ArrayList();
	@FXML
	private Label allGoodLabel;
	@FXML
	private Label foundCheatLabel;
	@FXML
	private ComboBox<Course> courseCombo;
	@FXML
	private ComboBox<Exam> exmaCombo;
	@FXML
	private Button startBtn;
	@FXML
	private TextArea CheattxtArea;

	/**
	 * Runs stage
	 */
	public void start() {

		cheatcheckstage = new Stage();

		String title = "Cheat Check";
		String srcFXML = "/Lecturer/CheatingMenu.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			cheatcheckstage.setTitle(title);
			cheatcheckstage.setScene(scene);
			cheatcheckstage.setResizable(false);
			cheatcheckstage.show();

			cheatcheckstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(cheatcheckstage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						cheatcheckstage.close();
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
	 * init course combo of the logged lecturer, it will only show the exams that
	 * are done (active exam flag=0)
	 */
	public void initCombo() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getLockedExamCourses);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(""));

		packet.setParametersForCommand(Command.getLockedExamCourses, param);
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
					CourseList = p.<Course>convertedResultListForCommand(Command.getLockedExamCourses);
					// checking the list
					if (CourseList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					// check if the student has the same department code as the exam//

					observelistCourse = FXCollections.observableArrayList(CourseList);
					courseCombo.setItems(observelistCourse);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
	

	/**
	 * Loads the exam combo after course selection
	 */
	public void onCourseSelect() {
		Course SelectedCourse = new Course(courseCombo.getValue().getcId(), courseCombo.getValue().getcName());

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamByCourseID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(SelectedCourse.getcId()));

		packet.setParametersForCommand(Command.getExamByCourseID, param);
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
					ObservableList<Exam> observelistExam;
					// filling the returned information from the server
					ExamList = p.<Exam>convertedResultListForCommand(Command.getExamByCourseID);
					// checking the list
					if (ExamList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No exams to show");
						return;
					}
					// check if the student has the same department code as the exam//

					observelistExam = FXCollections.observableArrayList(ExamList);
					exmaCombo.setItems(observelistExam);
					hideStartBTN();
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}
	

	/**
	 * Hides the start button until the lecturer chooses the correct exam
	 */
	public void hideStartBTN() {
		startBtn.setVisible(false);
	}
	

	/**
	 * Show the start button after exam selection
	 */
	public void initStartBTN() {
		startBtn.setVisible(true);
	}

	/**
	 * A method that handles the cheat checking of the selected course
	 */
	public void CheatCheck() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getFinishedQuestionsByExID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exmaCombo.getValue().getExamId()));

		packet.setParametersForCommand(Command.getFinishedQuestionsByExID, param);
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

					QuestionList = p.<Cheat>convertedResultListForCommand(Command.getFinishedQuestionsByExID);
					// checking the list
					if (QuestionList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}

					String temp = new String();
					for (int i = 0, j = 0; j <= QuestionList.size(); j++) {
						if (Checkarr.isEmpty()) {
							Cheat tempcheat = new Cheat(QuestionList.get(j).getsID(), QuestionList.get(j).getAnswers());
							Checkarr.add(tempcheat);
							temp = Checkarr.get(i).getAnswers();

						} else if (j == QuestionList.size()) {
							Checkarr.get(i).setAnswers(temp);
							break;
						} else if (Checkarr.get(i).getsID() == QuestionList.get(j).getsID()) {
							temp = temp + QuestionList.get(j).getAnswers();

						} else if (Checkarr.get(i).getsID() != QuestionList.get(j).getsID()) {
							Checkarr.get(i).setAnswers(temp);
							temp = null;
							i++;
							Cheat tempcheat = new Cheat(QuestionList.get(j).getsID(), QuestionList.get(j).getAnswers());
							Checkarr.add(tempcheat);
							temp = Checkarr.get(i).getAnswers();

						}

					}
					temp = "Students cheated between one another: ID-";
					for (int i = 0; i < Checkarr.size(); i++) {
						for (int j = 0; j <= FinalCheck.size(); j++) {
							if (FinalCheck.isEmpty()) {
								Cheat tempcheat = new Cheat(Integer.parseInt(Checkarr.get(i).getAnswers()),
										temp + Checkarr.get(i).getsID(), 1);
								FinalCheck.add(tempcheat);
								temp = temp + Checkarr.get(i).getsID();
								break;
							}

							else if (FinalCheck.get(j).getsID() == Integer.parseInt(Checkarr.get(i).getAnswers())) {
								temp = temp + ", ID-" + Checkarr.get(i).getsID();
								FinalCheck.get(j).setCount(FinalCheck.get(j).getCount() + 1);
								FinalCheck.get(j).setAnswers(temp);
								break;
							} else if (FinalCheck.get(j).getsID() != Integer.parseInt(Checkarr.get(i).getAnswers())) {
								FinalCheck.get(j).setAnswers(temp);
								temp = "Students cheated between one another: ID-";
								Cheat tempcheat = new Cheat(Integer.parseInt(Checkarr.get(i).getAnswers()),
										temp + Checkarr.get(i).getsID(), 1);
								FinalCheck.add(tempcheat);
								temp = temp + Checkarr.get(i).getsID();
								break;
							} else if (i == Checkarr.size()) {
								FinalCheck.get(j).setAnswers(temp);
								break;
							}
						}

					}

					for (int i = 0; i < FinalCheck.size(); i++) {
						if (FinalCheck.get(i).getCount() > 1) {
							CheattxtArea.setText(FinalCheck.get(i).getAnswers() + "\r\n");
						}
					}
					if (CheattxtArea.getText().isEmpty())
						allGoodLabel.setVisible(true);
					else
						foundCheatLabel.setVisible(true);

					FinalCheck.clear();
					Checkarr.clear();
				}

				else {
					displayAlert(AlertType.ERROR, "Error",
							"No student finished the exam Or all of them chose to submit a blank exam", null);
				}
			}
		});

		send.start();
	}

	/**
	 * A function that displays the alerts
	 * 
	 * @param type-
	 *            what type of error
	 * @param title-
	 *            set the title of the alert
	 * @param header-
	 *            header for the alert
	 * @param content-
	 *            what error happened
	 */
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {

			lecturerMenuController.hidefunc(cheatcheckstage);
			lecturerMenuController.lecturermenu.show();

		} catch (Exception e) {
			cheatcheckstage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * Initializes startup functions
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		allGoodLabel.setVisible(false);
		foundCheatLabel.setVisible(false);
		startBtn.setVisible(false);
		initCombo();
	}

}
