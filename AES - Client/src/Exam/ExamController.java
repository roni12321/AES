package Exam;

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

public class ExamController implements Initializable {
	/**showExamStage - the current stage
	 * questionList - contains all the question list of the current exam.
	 * ques_box - Combo box of the questions 
	 */
	public static Stage showExamStage;
	public ArrayList<Question> questionList;
	public ComboBox<Question> ques_box;
	protected int questionArraySize;
	
	
	@FXML
    private TextField qus_textbox;
    @FXML
    private TextField student_Guidelines;
    @FXML
    private RadioButton ans1_radio;
    @FXML
    private RadioButton ans2_radio;
    @FXML
    private RadioButton ans3_radio;
    @FXML
    private RadioButton ans4_radio;
    @FXML
    private Button NextQuestion;
    @FXML
    private Button PreviousQuestion;
    @FXML
    private Button exitExam;
    
    @FXML
    private ToggleGroup G1;
  
    
    /**
     * start function loads up the screen and opens a stage, uses Initializable.
     */
    
	public void start() {
		showExamStage = new Stage();

		String title = "Show Exam";
		String srcFXML = "/Exam/Examview.fxml";

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
						UseExistingController.hidefunc(showExamStage);
						UseExistingController.useexistingstage.show();
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
	 * Initilize function to pass the exam id to the function getAllQuestionsByExamId
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getAllQuestionsByExamId(UseExistingController.exid);
	}
	
	
	
	/**
	 * 
	 * @param s 
	 * stage s get the current stage to hide it. 
	 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}
	
	/**
	 * back function gets the current stage and uses hidefunc from UseExsistingController and shows back the exsiting stage.
	 */
	public void back() {

		try {
			UseExistingController.hidefunc(showExamStage);
			UseExistingController.useexistingstage.show();
		} catch (Exception e) {
			showExamStage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * getAllQuestionsByExamId uses the selected exam id to get back all the questions in the specific exam.
	 * @param exID - exam Id to pass the exam to the server query.
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
						questionArraySize=observelistQuestion.size();
					}

					else {
						displayAlert(AlertType.ERROR, "Error", "No exams with that code", p.getExceptionMessage());
					}
				}
			});

			send.start();
		}

		/**
		 * OnQuestionSelect uses the details that were put in the ques_box(combo box) to fill up the exam screen
		 * Questions - 4 questions
		 * qus_textbox - put the question text.
		 * student_Guidelines - the student guidelines for the specific exam. 
		 */
		public void OnQuestionSelect() {
			String[] Answers=new String[4];
			Answers=ques_box.getValue().getpos_ans();
			String Question=String.format("%s"+"  "+ "(%d points)", ques_box.getValue().getQues(),ques_box.getValue().getScore());
			qus_textbox.setText(Question);
			//lecturer_Guidelines.setText(examList.get(0).getLectureInstructions());
			student_Guidelines.setText(ques_box.getValue().getnotes());
			ans1_radio.setText(Answers[0]);
			ans2_radio.setText(Answers[1]);
			ans3_radio.setText(Answers[2]);
			ans4_radio.setText(Answers[3]);
			// sets the correct answer in the correct radio box
			switch(ques_box.getValue().getcurrent_ans())
			{
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
