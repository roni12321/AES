package Lecturer;

import javafx.scene.control.TextField;
import java.awt.TextArea;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Exam.Exam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.FinishedQuestion;
import Question.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LecturerSetNotesController implements Initializable {
/**
 * questionList - arraylist of questions
 * questionCombo - combobox of questions.
 */
	private ArrayList<String> lecInst;
	private String nextQuestionID;
	private String prevQuestionID;
	private String currentQuestionID;
	public static String qIdsaver;
	public static Stage showExamCopyViewerStage;
	@FXML
	private TextField studAnswer;
	@FXML
	private TextField corAnswer;
	public ArrayList<Question> questionList;
	@FXML
	public ComboBox<Question> questionCombo;
	public static String exID;


	@FXML
	private Button btnnnn;
	protected int questionArraySize;


	/**
	 * 
	 * @param questionList setter for questionList.
	 */
	public void setQuestionList(ArrayList<Question> questionList) {
		this.questionList = questionList;
	}

	@FXML
	private TextField lecturerNotesTextArea;
	@FXML
	private Button BackButtonExamViewer;

	@FXML
	private AnchorPane previousQuestion;

    @FXML
    private TextField lecExamInstructions;
	@FXML
	private TextField qus_textbox1;

	@FXML
	private TextField lecturerNotesTextField;

	@FXML
	private RadioButton ans2_radio;

	@FXML
	private RadioButton ans1_radio;

	@FXML
	private RadioButton ans3_radio;

	@FXML
	private ToggleGroup G1;

	@FXML
	private RadioButton ans4_radio;

	@FXML
	private Button exitExam;
	@FXML
	private Button nextBtn;

	@FXML
	private Button prevBtn;
	/**
	 * 
	 * @param exid2 - getting the static global exam id  to pass to the new stage
	 * @param sId2 - getting the static global student id  to pass to the new stage
	 * initializing the new stage showExamCopyViewerStage
	 */
	public void start(String exid2, int sId2) {
		showExamCopyViewerStage = new Stage();
		String title = "Exam Viewer";
		String srcFXML = "/Lecturer/LecturerSetNotes.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			showExamCopyViewerStage.setTitle(title);
			showExamCopyViewerStage.setScene(scene);
			showExamCopyViewerStage.setResizable(false);
			showExamCopyViewerStage.show();
			showExamCopyViewerStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(showExamCopyViewerStage);
						CheckExamController.checkexamstage.show();
					} catch (Exception e) {
						showExamCopyViewerStage.close();
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
	} // start
	/**
	 * Initialize  the stage with exid and sid. sending to the getQbyExamID Function.
	 * Initialize the lecturer instructions to the exam.
	 */
	public void initialize(URL location, ResourceBundle resources) {
		getLectInstructions(CheckExamController.exid);
		getQbyExamID(CheckExamController.exid, CheckExamController.sId);
		resetButtons();
	}
/**
 * function to show the lecturer the instruction during the checking.
 * @param exid2 pass exam id to the function
 */
	private void getLectInstructions(String exid2)  {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getLecturerInstructions);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exid2));

		packet.setParametersForCommand(Command.getLecturerInstructions, param);
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
					lecInst = p.<String>convertedResultListForCommand(Command.getLecturerInstructions);

					// checking the list
					if (lecInst.isEmpty() == true) {
						lecExamInstructions.setText("-");
						return;
					}
					String str = String.format("%s", lecInst.get(0));
					lecExamInstructions.setText(str);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();

	
}

	/**
	 * 
	 * @param exID2 getting the static global exam id to send  to the command
	 * @param sId getting the static global student id  to send to the command
	 */
	public void getQbyExamID(String exID2, int sId) {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getQuestionsByExIDCheckExam);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exID2, sId));

		packet.setParametersForCommand(Command.getQuestionsByExIDCheckExam, param);
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
					questionList = p.<Question>convertedResultListForCommand(Command.getQuestionsByExIDCheckExam);
					// checking the list
					if (questionList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					// check if the student has the same department code as the exam//

					
					observelistQuestion = FXCollections.observableArrayList(questionList);
					questionCombo.setItems(observelistQuestion);
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
	 * getting all the questions answered in the specific exam  to the combobox and uses it to our needs fill up the fxml text and combobox properly.
	 * fill up the correct answers for the lecturer, then the lecturer can set notes for each student questio
	 */
	public void OnQuestionSelection() {
		int correctAnswer = questionCombo.getValue().getAnsStId();
		if(correctAnswer==0) {
			displayAlert(AlertType.ERROR, "Error", "Question is empty",null);
			return;
		}
		String[] Answers = new String[4];
		Answers = questionCombo.getValue().getpos_ans();
		String Question = String.format("%s" + "  " + "(%d points)", questionCombo.getValue().getQues(),
				questionCombo.getValue().getScore());
		qus_textbox1.setText(Question);
		// lecturer_Guidelines.setText(examList.get(0).getLectureInstructions());
		ans1_radio.setText(Answers[0]);
		ans2_radio.setText(Answers[1]);
		ans3_radio.setText(Answers[2]);
		ans4_radio.setText(Answers[3]);
		

		int correctAnsExam = questionCombo.getValue().getcurrent_ans();
		qIdsaver = questionCombo.getValue().getId();

		// sets the correct answer in the correct radio box
		switch (correctAnswer) {
		case 0:
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			corAnswer.setText("The correct answer is : " + correctAnsExam);
			break;

		case 1:

			ans1_radio.setSelected(true);
			ans2_radio.setSelected(false);
			ans3_radio.setSelected(false);
			ans4_radio.setSelected(false);
			ans1_radio.setDisable(true);
			ans2_radio.setDisable(true);
			ans3_radio.setDisable(true);
			ans4_radio.setDisable(true);
			if (correctAnsExam != 1) {
				corAnswer.setText("The correct answer is : " + correctAnsExam);

			} else
				corAnswer.setText("The correct answer is : " + correctAnsExam);
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
			if (correctAnsExam != 2) {
				corAnswer.setText("The correct answer is : " + correctAnsExam);

			} else
				corAnswer.setText("The correct answer is : " + correctAnsExam);
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
			if (correctAnsExam != 3) {
				corAnswer.setText("The correct answer is : " + correctAnsExam);

			} else
				corAnswer.setText("The correct answer is : " + correctAnsExam);
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
			if (correctAnsExam != 4) {
				corAnswer.setText("The correct answer is : " + correctAnsExam);

			} else
				corAnswer.setText("The correct answer is : " + correctAnsExam);
			break;

		}// switch
		currentQuestionID = questionCombo.getValue().getId();
		buttonsController();
	}
	/**
	 * go back to the previous stage.
	 */
	public void backExam() {

		LecturerSetNotesController.hidefunc(showExamCopyViewerStage);
		CheckExamController.checkexamstage.show();

	}
	/**
	 * a function to send the note for the specific question answered by the student to the command updateQuestionNote sending examlist of FinisedQuestion constructor.
	 */
	public void submitNotes() {
		String questNotes = lecturerNotesTextArea.getText();
		Packet packet = new Packet();
		packet.addCommand(Command.updateQuestionNote);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		FinishedQuestion newFQ = new FinishedQuestion(questNotes, CheckExamController.exid, qIdsaver,
				CheckExamController.sId);

		examlist.add(newFQ);
		packet.setParametersForCommand(Command.updateQuestionNote, examlist);
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
					displayAlert(AlertType.INFORMATION, "Success", "The note has been added", null);
					lecturerNotesTextArea.clear();

				} else {

					displayAlert(AlertType.ERROR, "Error", "can not perform update , Please Try Again Later",
							p.getExceptionMessage());

				}

			}
		});

		send.start();
	}
	/**
	 * 
	 * @param s current stage to hide.
	 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
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
	 * next and back to jump between questions in the exam.
	 */
	public void nextPress() {
		currentQuestionID = questionCombo.getValue().getId();
		int i = 0, index = 0;
		for (i = 0; i < questionList.size(); i++) {
			if (currentQuestionID == questionList.get(i).getId()) {
				nextQuestionID = questionList.get(i + 1).getId();
				if (nextQuestionID == questionList.get(questionList.size() - 1).getId()) {
					nextBtn.setVisible(false);
					index = i + 1;
					questionCombo.setValue(questionList.get(index));
					buttonsController();
					OnQuestionSelection();
					break;
				} else {
					nextBtn.setVisible(true);
					index = i + 1;
					questionCombo.setValue(questionList.get(index));
					buttonsController();
					OnQuestionSelection();
					break;
				}
			}
		}
	}

	public void prevPress() {
		currentQuestionID = questionCombo.getValue().getId();
		buttonsController();
		int j = 0, jIndex = 0;
		for (j = 0; j < questionList.size(); j++) {
			if (currentQuestionID == questionList.get(j).getId()) {
				prevQuestionID = questionList.get(j - 1).getId();
				if (prevQuestionID == questionList.get(0).getId()) {
					prevBtn.setVisible(false);
					jIndex = j - 1;
					questionCombo.setValue(questionList.get(jIndex));
					buttonsController();
					OnQuestionSelection();
					break;
				} else {
					prevBtn.setVisible(true);
					jIndex = j - 1;
					questionCombo.setValue(questionList.get(jIndex));
					buttonsController();
					OnQuestionSelection();
					break;
				}
			}
		}
	}

	/**
	 * decide if hide the buttons - prev/next
	 */
	public void buttonsController() {
		if (questionList.size() == 0 || questionList.size() == 1) { // if the exam consist only one question
			prevBtn.setVisible(false);
			nextBtn.setVisible(false);
			return;
		}
		if (currentQuestionID == questionList.get(0).getId()) {// if we on the first question
			prevBtn.setVisible(false);
			nextBtn.setVisible(true);
			return;
		}
		if (currentQuestionID == questionList.get(questionList.size() - 1).getId()) {// if we on the last question
			prevBtn.setVisible(true);
			nextBtn.setVisible(false);
			return;
		} else {
			prevBtn.setVisible(true);
			nextBtn.setVisible(true);
			return;
		}

	}

	public void resetButtons() {
		prevBtn.setVisible(false);
		nextBtn.setVisible(false);
	}
}
