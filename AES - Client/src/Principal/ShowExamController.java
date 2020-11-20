package Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Lecturer.UseExistingController;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * this class responsible on show all exam for principal (of all lecturers)
 */
public class ShowExamController implements Initializable {

	@FXML
	private TextField qus_textbox;

	@FXML
	private RadioButton ans2_radio;

	@FXML
	private RadioButton ans1_radio;

	@FXML
	private ComboBox<Question> ques_box;

	@FXML
	private RadioButton ans3_radio;

	@FXML
	private ToggleGroup G1;

	@FXML
	private RadioButton ans4_radio;

	@FXML
	private Button exitExam;

	@FXML
	private TextField student_Guidelines;
	protected int questionArraySize;

	public static Stage showExamStage;
	public ArrayList<Question> questionList;

	/**
	 * start the exam stage
	 */
	public void start() {
		showExamStage = new Stage();

		String title = "Show Exam";
		String srcFXML = "/Principal/Principal-Show-Exam.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			showExamStage.setTitle(title);
			showExamStage.setScene(scene);
			showExamStage.setResizable(false);
			showExamStage.show();

			showExamStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						ExamTableController.hidefunc(showExamStage);
						ExamTableController.examStage.show();
					} catch (Exception e) {
						showExamStage.close();
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

	}// start()

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getAllQuestionsByExamId(ExamTableController.showExamId);
	}

	/**
	 * get the question by the examID
	 * 
	 * @param exID
	 */
	private void getAllQuestionsByExamId(String exID) {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getQuestionsByExID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exID));

		packet.setParametersForCommand(Command.getQuestionsByExID, param);
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
					ObservableList<Question> observelistQuestion;
					// filling the returned information from the server
					questionList = p.<Question>convertedResultListForCommand(Command.getQuestionsByExID);
					// checking the list
					if (questionList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					// check if the student has the same department code as the exam//
					observelistQuestion = FXCollections.observableArrayList(questionList);
					ques_box.setItems(observelistQuestion);
					questionArraySize = observelistQuestion.size();
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "No exams with that code", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
/**
 * "On action" on comboBox inside the question comboBox - charge all the data of current question
 */
	public void OnQuestionSelect() {
		String[] Answers = new String[4];
		Answers = ques_box.getValue().getpos_ans();
		String Question = String.format("%s" + "  " + "(%d points)", ques_box.getValue().getQues(),
				ques_box.getValue().getScore());
		qus_textbox.setText(Question);
		// lecturer_Guidelines.setText(examList.get(0).getLectureInstructions());
		student_Guidelines.setText(ques_box.getValue().getnotes());
		ans1_radio.setText(Answers[0]);
		ans2_radio.setText(Answers[1]);
		ans3_radio.setText(Answers[2]);
		ans4_radio.setText(Answers[3]);
		// sets the correct answer in the correct radio box
		switch (ques_box.getValue().getcurrent_ans()) {
		case 1:

			ans1_radio.setSelected(true);
			ans2_radio.setSelected(false);
			ans3_radio.setSelected(false);
			ans4_radio.setSelected(false);
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			break;
		case 2:
			ans1_radio.setSelected(false);
			ans2_radio.setSelected(true);
			ans3_radio.setSelected(false);
			ans4_radio.setSelected(false);
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			break;

		case 3:
			ans1_radio.setSelected(false);
			ans2_radio.setSelected(false);
			ans3_radio.setSelected(true);
			ans4_radio.setSelected(false);
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			break;

		case 4:
			ans1_radio.setSelected(false);
			ans2_radio.setSelected(false);
			ans3_radio.setSelected(false);
			ans4_radio.setSelected(true);
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			break;

		}// switch

	}

	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}
	/**
	 * back method - hide the current window and open the previous Stage
	 */
	public void back() {

		try {
			ExamTableController.hidefunc(showExamStage);
			ExamTableController.examStage.show();
		} catch (Exception e) {
			showExamStage.close();
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
	// function for alert windows -> get the details and know what print
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
