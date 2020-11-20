package Student;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.FinishedQuestion;
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
 * This class handles the StudentExamCopyViewer
 * in the StudentExamCopyViewer screen the student would be able to see his selected finished exam
 * @author ronyalensky
 *
 */
public class StudentExamCopyViewerController implements Initializable {

	public static Stage showExamCopyViewerStage;
	private ArrayList<Question> questionList;
	public ComboBox<Question> ques_box;
	private ArrayList<FinishedQuestion> finishedQuest;
	private int sId = LoginController.userLogged.getuId();

	@FXML
	private TextField qus_textbox;

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

	public void start() {
		showExamCopyViewerStage = new Stage();
		String title = "Exam Viewer";
		String srcFXML = "/Student/Student-View-Exam-Copy.fxml";

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
						ExamCopyController.hidefunc(showExamCopyViewerStage);
						ExamCopyController.ExamCopyStage.show();
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		getQbyExamID(ExamCopyController.exid2);
		getAnsweredQuestionSidAndQid(sId, ExamCopyController.exid2);


	}

	/**
	 *  get the question by the examID2
	 * @param exID2
	 */
	private void getQbyExamID(String exID2) {
		Packet packet = new Packet();
		/**
		 *  adding command
		 */
		packet.addCommand(Command.getQuestionsByExID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exID2));

		packet.setParametersForCommand(Command.getQuestionsByExID, param);
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

					// building new packet
					ObservableList<Question> observelistQuestion;
					/**
					 *  filling the returned information from the server
					 */
					questionList = p.<Question>convertedResultListForCommand(Command.getQuestionsByExID);
					/**
					 *  checking the list
					 */
					if (questionList.isEmpty()) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					/**
					 *  check if the student has the same department code as the exam//
					 */
					observelistQuestion = FXCollections.observableArrayList(questionList);
					ques_box.setItems(observelistQuestion);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "No question for this exam", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	public void getAnsweredQuestionSidAndQid(int sId, String exID) {

		Packet packet = new Packet();
		/**
		 * adding command
		 */
		packet.addCommand(Command.getAnsweredQuestionSidAndQid);

		ArrayList<Object> params = new ArrayList<>(Arrays.asList(sId, exID));

		packet.setParametersForCommand(Command.getAnsweredQuestionSidAndQid, params);
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
 * setting a new arrayList that hold the returned arraylist from the query
 */
					ArrayList<FinishedQuestion> finishedQuest1;
					
					finishedQuest1 = p.<FinishedQuestion>convertedResultListForCommand(Command.getAnsweredQuestionSidAndQid) ;
					finishedQuest =  finishedQuest1;				
					
						if (finishedQuest.isEmpty()) {
							displayAlert(AlertType.ERROR, "Error", "no questions are in this exam", p.getExceptionMessage());
							return;
						}
					}
				else
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
			}

		});

		send.start();
		// return answeredQuestion;
	}
/**
 * On action selection- the method receives what the user has selected in the question combo
 * and then with for each loop - look in the FinishedQeustion ArrayList that has returned from the query above
 * for the same question , then saves the lecturer notes and the student answer in local variables
 * finally the method sets all the question parameters in the right text fields. 
 * 
 */
	public void OnQuestionSelection() {
		String selectedQid = ques_box.getValue().getId(); 
		String selectedQuestionTest = ques_box.getValue().getQues();
		String selectedQuestionNotes = null;
		int stdAns = 0;
		int selectedGrade = ques_box.getValue().getScore();
		
		for (FinishedQuestion fq : finishedQuest) {
			if(fq.getqId().equals(selectedQid))
				{
					selectedQuestionNotes = fq.getNotes();
					stdAns = fq.getansStId();
				}
				
		}
		if(stdAns==0) {
			displayAlert(AlertType.ERROR, "Error", "Question is empty",null);
			return;
		}

		String[] Answers = new String[4];
		Answers = ques_box.getValue().getpos_ans();
		String Question = String.format("%s" + "  " + "(%d points)",selectedQuestionTest ,selectedGrade);
		qus_textbox.setText(Question);

		ans1_radio.setText(Answers[0]);
		ans2_radio.setText(Answers[1]);
		ans3_radio.setText(Answers[2]);
		ans4_radio.setText(Answers[3]);
		lecturerNotesTextField.setText(selectedQuestionNotes);
		lecturerNotesTextField.setDisable(true);
		
		
		/**
		 *  sets the student answer in the correct radio box
		 */
		switch (stdAns) {
		

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
/**
 * getter for the finished question arrayList
 * @return the array list
 */
	public ArrayList<FinishedQuestion> getFinishedQuest() {
		return finishedQuest;
	}

	public void setFinishedQuest(ArrayList<FinishedQuestion> finishedQuest) {
		this.finishedQuest = finishedQuest;
	}
/**
 * method that hides the current stage and reveal the next stage
 */
	public void backExam() {

		StudentExamCopyViewerController.hidefunc(showExamCopyViewerStage);
		ExamCopyController.ExamCopyStage.show();

	}
/**
 * method that hide the current stage
 * @param s - s for stage
 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
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



}
