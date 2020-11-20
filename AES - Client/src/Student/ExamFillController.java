package Student;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import Answer.Answer;
import Exam.FinishedExam;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.FinishedQuestion;
import Question.Question;
import javafx.application.Platform;
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
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Class on student package, Handles the filling of the computerized exam
 * 
 * @author Andrey
 *
 */
public class ExamFillController extends Thread implements Initializable {
	@FXML
	private ComboBox<Question> ques_box;
	@FXML
	private TextField qus_textbox;
	@FXML
	private RadioButton ans1_radio;
	@FXML
	private RadioButton ans2_radio;
	@FXML
	private RadioButton ans3_radio;
	@FXML
	private RadioButton ans4_radio;
	@FXML
	private TextField Guidelines_Quest;
	@FXML
	private Text LockedSubmit;
	@FXML
	private Text Timefield;
	@FXML
	private Text TimeExtendTXT;
	@FXML
	private Button nextBtn;
	@FXML
	private Button prevBtn;
	
    @FXML
    private TextField studentInstructions;
    
	private static Timer ServerCheck;
	private static Timer Timer;
	private static int CID;
	public static Stage ExamFill;
	private static String ExID;
	private static int StudentID;
	private static ArrayList<Question> QuestionList;
	private static ArrayList<Answer> StudentAnswers;
	private static ArrayList<Integer> CourseList;
	private static ArrayList<Integer> TimeList = new ArrayList<Integer>();
	private static ArrayList<Integer> ExtraTime;
	private static int Started;
	private static int Finished = 0;
	private static long starttime;
	private static long finishtime;
	private static volatile int ExamTime;
	private static boolean Locked = false;
	private static boolean AddedTime = false;
	private static int timeflag = 0;
	private static int lockflag = 0;
	private static String nextQuestionID;
	private static String prevQuestionID;
	private static String currentQuestionID;

	/**
	 * Open the exam window
	 * 
	 * @param studentID
	 *            - Student ID from previous stage
	 * @param exID
	 *            - Exam ID from previous stage
	 */
	public void start(int studentID, String exID) {
		Started = 1;
		ExID = exID;
		StudentID = studentID;
		ExamFill = new Stage();
		String title = "Computerized Exam";
		String srcFXML = "/Student/ExamFill.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ExamFill.setTitle(title);
			ExamFill.setScene(scene);
			ExamFill.setResizable(false);
			ExamFill.show();

			ExamFill.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						Exit();
					} catch (Exception e) {
						ExamFill.close();
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
	 * Initialize some values on exam start: Exam time, Course ID, Start time, Fetch
	 * exam questions.
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		getStudentInstructions();
		getExamTime();
		getQuestions(ExID);
		getCID();
		TimeExtendTXT.setVisible(false);
		LockedSubmit.setVisible(false);
		starttime = System.currentTimeMillis();
		resetButtons();
		FirstRadios();

	}

	
	private void getStudentInstructions()
	{
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getStudentInstructions);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.getStudentInstructions, param);
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
					 ArrayList<String> StudentInstructions;

					// filling the returned information from the server
					 StudentInstructions = p.<String>convertedResultListForCommand(Command.getStudentInstructions);
					// checking the list
					if (StudentInstructions.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No Student Instructions in this exam");
						return;
					}
					// check if the student has the same department code as the exam//
					String Instructions = String.format("%s",StudentInstructions.get(0));
					studentInstructions.setText(Instructions);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
		
		
	}

	
	
	/**
	 * init the radio buttons at start
	 */
	private void FirstRadios() {
		ans1_radio.setDisable(true);
		ans2_radio.setDisable(true);
		ans3_radio.setDisable(true);
		ans4_radio.setDisable(true);
		
	}

	/**
	 * A function that fetches the questions from the DB based on the Exam ID
	 * 
	 * @param exID-
	 *            Exam ID
	 */
	private void getQuestions(String exID) {
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
					QuestionList = p.<Question>convertedResultListForCommand(Command.getQuestionsByExID);
					// checking the list
					if (QuestionList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					// check if the student has the same department code as the exam//
					ArrayList<Answer> StudentAnswersTemp = new ArrayList<Answer>();
					for (int i = 0; i < QuestionList.size(); i++) {
						String Qid = QuestionList.get(i).getId();
						Answer answer = new Answer(Qid);
						StudentAnswersTemp.add(answer);
					}
					StudentAnswers = StudentAnswersTemp;
					observelistQuestion = FXCollections.observableArrayList(QuestionList);
					ques_box.setItems(observelistQuestion);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
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
	 * Load the question details in the correct fields, and saves the students
	 * answers
	 */
	public void OnQuestionSelect() {
		initRadios();
		String[] Answers = new String[4];
		Answers = ques_box.getValue().getpos_ans();
		String Question = String.format("%s" + "  " + "(%d points)", ques_box.getValue().getQues(),
				ques_box.getValue().getScore());
		qus_textbox.setText(Question);
		Guidelines_Quest.setText(ques_box.getValue().getnotes());
		ans1_radio.setText(Answers[0]);
		ans2_radio.setText(Answers[1]);
		ans3_radio.setText(Answers[2]);
		ans4_radio.setText(Answers[3]);
		ans1_radio.setSelected(false);
		ans2_radio.setSelected(false);
		ans3_radio.setSelected(false);
		ans4_radio.setSelected(false);
		for (int i = 0; i < StudentAnswers.size(); i++) {
			if (ques_box.getValue().getId().equals(StudentAnswers.get(i).getQid())) {
				if (StudentAnswers.get(i).getAnswer() == 1)
					ans1_radio.setSelected(true);
				else if (StudentAnswers.get(i).getAnswer() == 2)
					ans2_radio.setSelected(true);
				else if (StudentAnswers.get(i).getAnswer() == 3)
					ans3_radio.setSelected(true);
				else if (StudentAnswers.get(i).getAnswer() == 4)
					ans4_radio.setSelected(true);
			}
		}
		currentQuestionID = ques_box.getValue().getId();
		buttonsController();
	}
/**
 * enable the radio buttons
 */
	private void initRadios() {
		ans1_radio.setDisable(false);
		ans2_radio.setDisable(false);
		ans3_radio.setDisable(false);
		ans4_radio.setDisable(false);
		
	}

	/**
	 * Handles the Exit button, aborting the exam
	 */
	public void Exit() {

		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Abort exam");
		alert.setContentText("Are you Sure?" + "\n" + "(Leaving without submitting will result in a 0 grade)");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);

		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
			if (type == okButton) {
				try {
					Timer.cancel();
					studentMenuController.hidefunc(ExamFill);
					CompExamController.CompExam.show();
					UploadExam();
				} catch (Exception e) {
					ExamFill.close();
					System.out.println(e);
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * Back function
	 */
	public void back() {

		try {
			studentMenuController.hidefunc(ExamFill);
			Timer.cancel();
			ServerCheck.cancel();
			CompExamController.CompExam.show();
		} catch (Exception e) {
			ExamFill.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void locked() {
		displayAlert(AlertType.INFORMATION, "Exam Locked", "The exam was locked by the lecturer", null);
		UploadExam();
		
	}

	/**
	 * Saves the students answers, and if he chose the correct answer, save the
	 * question score if chose the wrong answer set score to 0
	 */
	public void SaveAnswer() {
		if (ans1_radio.isSelected()) {
			for (int i = 0; i < QuestionList.size(); i++) {
				if (ques_box.getValue().getId().equals(StudentAnswers.get(i).getQid())) {
					StudentAnswers.get(i).setAnswer(1);

					if (ques_box.getValue().getcurrent_ans() == StudentAnswers.get(i).getAnswer()) {
						StudentAnswers.get(i).setGrade(ques_box.getValue().getScore());
					} else {
						StudentAnswers.get(i).setGrade(0);
					}
				}
			}

		} else if (ans2_radio.isSelected()) {
			for (int i = 0; i < QuestionList.size(); i++) {
				if (ques_box.getValue().getId().equals(StudentAnswers.get(i).getQid())) {
					StudentAnswers.get(i).setAnswer(2);
					if (ques_box.getValue().getcurrent_ans() == StudentAnswers.get(i).getAnswer()) {
						StudentAnswers.get(i).setGrade(ques_box.getValue().getScore());
					} else {
						StudentAnswers.get(i).setGrade(0);
					}
				}
			}

		} else if (ans3_radio.isSelected()) {
			for (int i = 0; i < QuestionList.size(); i++) {
				if (ques_box.getValue().getId().equals(StudentAnswers.get(i).getQid())) {
					StudentAnswers.get(i).setAnswer(3);
					if (ques_box.getValue().getcurrent_ans() == StudentAnswers.get(i).getAnswer()) {
						StudentAnswers.get(i).setGrade(ques_box.getValue().getScore());
					} else {
						StudentAnswers.get(i).setGrade(0);
					}
				}
			}

		} else if (ans4_radio.isSelected()) {
			for (int i = 0; i < QuestionList.size(); i++) {
				if (ques_box.getValue().getId().equals(StudentAnswers.get(i).getQid())) {
					StudentAnswers.get(i).setAnswer(4);
					if (ques_box.getValue().getcurrent_ans() == StudentAnswers.get(i).getAnswer()) {
						StudentAnswers.get(i).setGrade(ques_box.getValue().getScore());
					} else {
						StudentAnswers.get(i).setGrade(0);
					}
				}
			}
		}
	}

	/**
	 * Submit the questions of the exam to the DB
	 */
	public void SubmitQuestions() {

		
		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Abort exam");
		alert.setContentText("Are you Sure?");
		ButtonType okButton = new ButtonType("Yes", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);

		alert.getButtonTypes().setAll(okButton, noButton);
		alert.showAndWait().ifPresent(type -> {
			if (type == okButton) {
				ArrayList<FinishedQuestion> Examcopy = new ArrayList<FinishedQuestion>();
				for (int i = 0; i < QuestionList.size(); i++) {
					FinishedQuestion copy = new FinishedQuestion(StudentID, CID, ExID, StudentAnswers.get(i).getQid(),
							StudentAnswers.get(i).getAnswer(), StudentAnswers.get(i).getGrade());
					Examcopy.add(copy);
				}

				for (int i = 0; i < Examcopy.size(); i++) {
					Packet packet = new Packet();
					// adding question

					packet.addCommand(Command.AddcheckedQuestions);

					ArrayList<Object> param = new ArrayList<>(Arrays.asList(Examcopy.get(i)));

					packet.setParametersForCommand(Command.AddcheckedQuestions, param);

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

							} else {

								displayAlert(AlertType.ERROR, "Error", "Error Exam Information , Please Try Again Later",
										p.getExceptionMessage());

							}
						}
					});
					// sending the package
					send.start();
				}

				Finished = 1;
				UploadExam();

			}
		});
		
		
		
		
		
		
		
	}

	/**
	 * Submit the Exam stats to the DB
	 */
	public void UploadExam() {
		int Grade = 0;
		for (int i = 0; i < StudentAnswers.size(); i++)
			Grade += StudentAnswers.get(i).getGrade();
		Packet packet = new Packet();
		// adding command

		finishtime = System.currentTimeMillis() - starttime;
		if (lockflag ==1)
		{
			Grade = 0;
		}
		finishtime = (finishtime / 1000);
		FinishedExam newfinished = new FinishedExam(StudentID, CID, ExID, Grade, Started, Finished,
				Long.toString(finishtime));

		packet.addCommand(Command.AddFinishedExam);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(newfinished));

		packet.setParametersForCommand(Command.AddFinishedExam, param);
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
					displayAlert(AlertType.INFORMATION, "Success", "Exam Uploaded", null);
					SetUidAndCname();
					back();
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Couldn't upload the exam", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * A function that fetches the course ID of the Exam the student took
	 */
	public void getCID() {

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCourseIDbyExamID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.getCourseIDbyExamID, param);
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
					CourseList = p.<Integer>convertedResultListForCommand(Command.getCourseIDbyExamID);
					// checking the list
					if (CourseList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "");
						return;
					}
					// check if the student has the same department code as the exam//

					CID = CourseList.get(0);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * A function that fetches the Exam Time set the by teacher
	 */
	public void getExamTime() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamTime);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.getExamTime, param);
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
					TimeList = p.<Integer>convertedResultListForCommand(Command.getExamTime);
					// checking the list
					if (TimeList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "No examtime configured", null);
						return;
					}
					ExamTime = TimeList.get(0);
					String str = String.format("Exam time: %d minutes left", ExamTime);
					Timefield.setText(str);

					Timer = new Timer();// Counts the timer of the exam
					Timer.schedule(new TimerTask() {
						public void run() {
							Platform.runLater(new Runnable() {
								public void run() {
									ExamTime--;
									String time = String.format("Exam time: %d minutes left", ExamTime);
									Timefield.setText(time);
									if (ExamTime == 0) {
										SubmitQuestions();
									}
								}
							});
						}
					}, 60000, 60000);
					ServerCheck = new Timer(); // checks each second if either tiem was added to the exam or it was
												// locked
					ServerCheck.schedule(new TimerTask() {
						public void run() {
							Platform.runLater(new Runnable() {
								public void run() {
									if (AddedTime == false) {
										CheckExtraTime();
									}
									if (AddedTime == true) {
										if (timeflag == 0) {
											ExamTime += ExtraTime.get(0);
											String timeExtend = String.format("%d minutes were extended",
													ExtraTime.get(0));
											TimeExtendTXT.setText(timeExtend);
											TimeExtendTXT.setVisible(true);
											Timefield.setText(String.format("Exam time: %d minutes left", ExamTime));
											timeflag = 1;
										}
									}
									if (Locked == false) {
										checkLock();
									}
									if (Locked == true) {
										if (lockflag == 0) {
											lockflag = 1;
											locked();
										}
									}
								}
							});
						}
					}, 1000, 1000);

				} else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * A function that checks the amount of time approved for extension
	 */
	private void CheckExtraTime() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExtraTime);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.getExtraTime, param);
		// sending the packet
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
					ExtraTime = p.<Integer>convertedResultListForCommand(Command.getExtraTime);
					// checking the list
					if (ExtraTime.isEmpty() == true) {
						return;
					}

					AddedTime = true;
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});
		send.start();

	}

	/**
	 * A method that sets the User ID and course name of the finished exam for
	 * further use
	 */
	public void SetUidAndCname() {
		Packet packet = new Packet();
		// adding question

		packet.addCommand(Command.getLecturerUidAndCourseNamebyExamID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.getLecturerUidAndCourseNamebyExamID, param);

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

				} else {

				}
			}
		});
		// sending the package
		send.start();
	}

	/**
	 * a method that checks if the exam is locked
	 */
	public void checkLock() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.checkLockedExam);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(ExID));

		packet.setParametersForCommand(Command.checkLockedExam, param);
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
					ArrayList<Integer> lockedexam = new ArrayList();
					// filling the returned information from the server
					lockedexam = p.<Integer>convertedResultListForCommand(Command.checkLockedExam);
					// checking the list
					if (lockedexam.get(0) == 1) {
						return;
					}
					// check if the student has the same department code as the exam//

					Locked = true;
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * a method that handlers the next button
	 */
	public void nextPress() {
		currentQuestionID = ques_box.getValue().getId();
		int i=0,index = 0;
		for(i=0;i<QuestionList.size();i++) {
			if(currentQuestionID ==QuestionList.get(i).getId() ) {
				nextQuestionID= QuestionList.get(i+ 1).getId();
				if(nextQuestionID == QuestionList.get(QuestionList.size() - 1).getId() ) {
					nextBtn.setVisible(false);
					index=i+1;
					ques_box.setValue(QuestionList.get(index));
					buttonsController();
					OnQuestionSelect();				
					break;
				}
				else {
					nextBtn.setVisible(true);
					index=i+1;
					ques_box.setValue(QuestionList.get(index));
					buttonsController();
					OnQuestionSelect();				
					break;
				}
			}
		}
		
	
	}
	/**
	 * a method that handlers the prev button
	 * if(nextQuestionID == QuestionList.get(QuestionList.size() - 1).getId() ) {
					nextBtn.setVisible(false);
					return;
	 */
	public void prevPress() {
		currentQuestionID = ques_box.getValue().getId();
		buttonsController();
		int j = 0,jIndex=0;
		for (j = 0; j < QuestionList.size(); j++) {
			if (currentQuestionID == QuestionList.get(j).getId()) {
				prevQuestionID = QuestionList.get(j - 1).getId();
				if (prevQuestionID == QuestionList.get(0).getId()) {
					prevBtn.setVisible(false);
					jIndex=j-1;
					ques_box.setValue(QuestionList.get(jIndex));
					buttonsController();
					OnQuestionSelect();				
					break;
				} else {
					prevBtn.setVisible(true);
					jIndex=j-1;
					ques_box.setValue(QuestionList.get(jIndex));
					buttonsController();
					OnQuestionSelect();				
					break;
				}
			}
		}
	}

	/**
	 * decide if hide the buttons - prev/next
	 */
	public void buttonsController() {
		if (QuestionList.size() == 0 || QuestionList.size() == 1) { // if the exam consist only one question
			prevBtn.setVisible(false);
			nextBtn.setVisible(false);
			return;
		}
		if (currentQuestionID == QuestionList.get(0).getId()) {// if we on the first question
			prevBtn.setVisible(false);
			nextBtn.setVisible(true);
			return;
		}
		if (currentQuestionID == QuestionList.get(QuestionList.size() - 1).getId()) {// if we on the last question
			prevBtn.setVisible(true);
			nextBtn.setVisible(false);
			return;
		} else {
			prevBtn.setVisible(true);
			nextBtn.setVisible(true);
			return;
		}

	}

	/**
	 * reset button visibility
	 */
	public void resetButtons() {
		prevBtn.setVisible(false);
		nextBtn.setVisible(false);
	}
}