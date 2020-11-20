
package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Course.Course;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BuildQuestionController implements Initializable {

	private String notes, questxt, authorques;
	private int cid, correctans;

	private String[] ans = new String[4];

	@FXML
	private RadioButton BuildQAnswer3;

	@FXML
	private RadioButton BuildQAnswer4;

	@FXML
	private RadioButton BuildQAnswer1;

	@FXML
	private RadioButton BuildQAnswer2;

	@FXML
	private Button BuildQuestionSubmit;

	@FXML
	private TextField BuildThirdField3;

	@FXML
	private TextField BuildQuestionInstruc;

	@FXML
	private ToggleGroup G2;

	@FXML
	private TextField BuildSecondField2;

	@FXML
	private TextField BuildQuestionQField;

	@FXML
	private ComboBox<Course> BuildQuestionCoursesCombo;

	@FXML
	private TextField BuildFirstField1;

	@FXML
	private Button BackBtn16;

	@FXML
	private TextField BuildFourthField4;

	@FXML
	private TextField BuildQuestionLecturerField;

	/** course list */
	private ArrayList<Course> courselist;

	public static Stage buildquestionstage;
	public static int isForExam;


	public void start() {
		buildquestionstage = new Stage();

		String title = "Build Question";
		String srcFXML = "/Lecturer/Lecturer-Build-Question.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			buildquestionstage.setTitle(title);
			buildquestionstage.setScene(scene);
			buildquestionstage.setResizable(false);
			buildquestionstage.show();

			buildquestionstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(buildquestionstage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						buildquestionstage.close();
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
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {
			if (BuildExamController.examflag == 1) {
				BuildExamController.examflag = 0;
				BuildExamController.hidefunc(buildquestionstage);
				BuildExamController.buildexamstage.show();
			} else {

				lecturerMenuController.hidefunc(buildquestionstage);
				lecturerMenuController.lecturermenu.show();
			}

		} catch (Exception e) {
			buildquestionstage.close();
			System.out.println(e);
			e.printStackTrace();
		}

	}

	/**
	 * event if button submit
	 */
	public void submit() {
		// check if one of fields dont filled correctly
		if (BuildQuestionLecturerField.getText().toString().isEmpty() || BuildQuestionInstruc.getText().isEmpty()
				|| BuildQuestionQField.getText().isEmpty() || BuildFirstField1.getText().isEmpty()
				|| BuildFirstField1.getText().toString().isEmpty() || BuildSecondField2.getText().toString().isEmpty()
				|| BuildThirdField3.getText().toString().isEmpty()
				|| BuildFourthField4.getText().toString().isEmpty()) {
			// if one of the fields dont filled correctly -> pop error window and return
			displayAlert(AlertType.ERROR, "Error", "Please Fill All Information", null);
			return;
		}
		// check if the lecturer choose one of the answers to be the correct one, if not
		// -> pop message and return
		if (G2.getSelectedToggle() == null) {
			displayAlert(AlertType.ERROR, "Error", "Please coorect answer ", null);
			return;
		}
		// get the course that the lecturer select in the comboBox and put into type
		// Course
		Course SelectedCourse = BuildQuestionCoursesCombo.getValue();

		// getting other information from the window
		notes = BuildQuestionInstruc.getText();
		questxt = BuildQuestionQField.getText();
		ans[0] = BuildFirstField1.getText();
		ans[1] = BuildSecondField2.getText();
		ans[2] = BuildThirdField3.getText();
		ans[3] = BuildFourthField4.getText();
		authorques = BuildQuestionLecturerField.getText();
		correctans = getCorrectAnswer();
		cid = SelectedCourse.getcId(); // get for specific course the id of the course
		// create new question with all details of question (with course ID)
		Question newQues = new Question(cid, questxt, notes, authorques, ans, correctans);
		// send to DB the Course and the new question for saving the new question
		saveQuestion(SelectedCourse, newQues);

	}

	/**
	 * crate new question id and save to DB
	 * 
	 * @param SelectedCourse
	 *            the course select in combo box
	 */
	private void saveQuestion(Course SelectedCourse, Question newQues) {
		// TODO Auto-generated method stub

		int cid;
		// get the ID of specific course
		cid = SelectedCourse.getcId();

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getQuestion);

		// sending the packet to system
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {
				// TODO Auto-generated method stub

			}

			// when get the result from DB
			@Override
			public void onReceivingResult(Packet p) {
				int countQues;
				String Quesid;
				// TODO Auto-generated method stub
				if (p.getResultState()) {
					// building new packet
					ArrayList<Integer> listCountCourse;
					// filling the returned information from the server
					listCountCourse = p.<Integer>convertedResultListForCommand(Command.getQuestion);
					countQues = listCountCourse.get(0);
					countQues++; // add one for the next empty raw
					Quesid = getNewQuestionId(cid, countQues);
					newQues.setId(Quesid);
					insertQues(newQues);

				}

				else { // if something wrong -> print error message
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});
		// send the request by thread 'start'
		send.start();
	}

	/**
	 * function insert the new Question with id
	 * 
	 * @param newQues - new question
	 */
	public void insertQues(Question newQues) {

		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.addQuestion);
		ArrayList<Object> queslist;
		queslist = new ArrayList<>();
		queslist.add(newQues);
		packet.setParametersForCommand(Command.addQuestion, queslist);

		SystemSender send = new SystemSender(packet);
		// register the handler that occurs when the data arrived from the server
		send.registerHandler(new IResultHandler() {
			/**
			 * waiting for the server result
			 */
			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				// TODO Auto-generated method stub

				if (p.getResultState()) {
					displayAlert(AlertType.INFORMATION, "Success", "The Question Has Been Added", null);

					back();

				} else {

					displayAlert(AlertType.ERROR, "Error", "Error Question Information , Please Try Again Later",
							p.getExceptionMessage());

				}
			}
		});
		// sending the package
		send.start();
	}

	/**
	 * Create the question ID - concatenate two numbers
	 * 
	 * @param cid - course ID
	 *            
	 * @param countQues - number of questions +1 (for the new question)
	 *            
	 * @return - return
	 */
	// concatenation two numbers (with all cases)
	public String getNewQuestionId(int cid, int countQues) {

		String newId = "";
		if (cid < 10) {
			newId += "0" + cid;
		}

		else
			newId += "" + cid;

		if (countQues < 10) {
			newId += "00" + countQues;
		}

		else if (countQues < 100) {
			newId += "0" + countQues;
		}

		else
			newId += "" + countQues;

		return newId;

	}

	/**
	 * function that gets the correct selected answer
	 * 
	 * @return correct answer
	 */
	// get the correct answer from the 4 answers
	public int getCorrectAnswer() {
		int correctans = -1;

		if (BuildQAnswer1.isSelected())
			correctans = 1;
		else if (BuildQAnswer2.isSelected())
			correctans = 2;
		else if (BuildQAnswer3.isSelected())
			correctans = 3;
		else if (BuildQAnswer4.isSelected())
			correctans = 4;

		return correctans;
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		BuildQuestionLecturerField.setText(LoginController.userLogged.getUsername());
		// initialize the combo boxes
		initComboBox();

	}

	/**
	 * initialize the combo box appropriate courses
	 */
	private void initComboBox() {
		// TODO Auto-generated method stub

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCoursesbyLid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((Lecturer) LoginController.userLogged));

		packet.setParametersForCommand(Command.getCoursesbyLid, param);
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
					courselist = p.<Course>convertedResultListForCommand(Command.getCoursesbyLid);

					// checking the list
					if (courselist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}

					// filling the information in the combobox
					observelistCourse = FXCollections.observableArrayList(courselist);
					BuildQuestionCoursesCombo.setItems(observelistCourse);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

}