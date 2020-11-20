package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Course.Course;
import Exam.Exam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BuildExamController implements Initializable {
	public static Stage buildexamstage;

	@FXML
	private Button BuildExamOriginDrawer;

	@FXML
	private ComboBox<Course> BuildExamCoursesCombo1;

	@FXML
	private Button BuildExamFinishBtn;

	@FXML
	private TextField BuildExamTotalScore;

	@FXML
	private ComboBox<Question> BuildExamQuestionNumberCombo;

	@FXML
	private TextField BuildExamQuestionScoreField;

	@FXML
	private TextField BuildExamLecturerDepartment;

	@FXML
	private TextArea BuildExamLecturerInstruction;

	@FXML
	private Button BackBtn17;

	@FXML
	private TextArea BuildExamStudentInstruc;

	@FXML
	private Button BuildExamOriginNew;

	@FXML
	private TextField BuildExamLecturerField;

	@FXML
	private Button BuildExamInsertQuestionButton;

	@FXML
	private TextField TimeTextField;

	/** question list */
	public static ArrayList<Question> questionlist;

	public static BuildExamController exeCon;

	/** course list */
	private ArrayList<Course> courselist;
	private ArrayList<String> dname;

	public static int Department;

	public static int examflag = 0;

	public static Stage buildquestionstage;

	public static ArrayList<QuestionInExam> questionInExamList = new ArrayList<QuestionInExam>();


	public void start() {

		buildexamstage = new Stage();

		String title = "Build Exam";
		String srcFXML = "/Lecturer/Lecturer-Build-Exam.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			buildexamstage.setTitle(title);
			buildexamstage.setScene(scene);
			buildexamstage.setResizable(false);
			buildexamstage.show();

			buildexamstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(buildexamstage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						buildexamstage.close();
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

			lecturerMenuController.hidefunc(buildexamstage);
			lecturerMenuController.lecturermenu.show();
			QuestionListController.questionAddList.clear();
			BuildExamQuestionNumberCombo.setItems(null);

		} catch (Exception e) {
			buildexamstage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * set the Stage of the previus screen
	 * 
	 * @param exeCon
	 *            - the Stage
	 */
	public void setExecController(BuildExamController exeCon) {
		this.exeCon = exeCon;
	}

	/**
	 * method that hide the current Stage
	 * 
	 * @param s - stage
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
	// function for alert windows -> get the details and know what print
	public static void displayAlert(AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		BuildExamLecturerField.setText(LoginController.userLogged.getUsername());
		// initialize the combo boxes
		initComboBox();
		initDname();
		takeDepartmentId();
		BuildExamTotalScore.setText("100");

		exeCon = this;

	}

	/**
	 * charge the questions to the ComboBox from the drawer
	 */
	public void initComboBoxQuestion() {

		if (!QuestionListController.questionAddList.isEmpty()) {
			ObservableList<Question> observelistQuestion;
			observelistQuestion = FXCollections.observableArrayList(QuestionListController.questionAddList);
			BuildExamQuestionNumberCombo.setItems(observelistQuestion);
			BuildExamCoursesCombo1.setDisable(true);
			BuildExamQuestionScoreField.clear();

		}

	}

	/**
	 * put the question to ArrayList of exam
	 */
	public void insertToExam() {

		Question quesExam;
		QuestionInExam quesToExam;
		String idQues, examready = "the exam ready";
		String questionPoint, exampoint;
		int scoreques, scoreexam;

		quesExam = BuildExamQuestionNumberCombo.getValue();

		if (quesExam == null) {
			displayAlert(AlertType.ERROR, "Error", "no selected question", null);
		} else {
			idQues = quesExam.getId();
			questionPoint = BuildExamQuestionScoreField.getText();
			if (!questionPoint.matches("[0-9]*")) {
				displayAlert(AlertType.ERROR, "Error", "The score need only integer number!", null);

			} else {
				exampoint = BuildExamTotalScore.getText();
				scoreques = Integer.parseInt(questionPoint);
				scoreexam = Integer.parseInt(exampoint);

				if (scoreques > scoreexam) {
					displayAlert(AlertType.ERROR, "Error", "The question score only not valid!", null);
				}

				else {
					scoreexam = scoreexam - scoreques;
					exampoint = String.valueOf(scoreexam);

					if (scoreexam == 0) {
						BuildExamTotalScore.setText(examready);
						BuildExamInsertQuestionButton.setDisable(true);

					} else {
						BuildExamTotalScore.setText(exampoint);
					}

					quesToExam = new QuestionInExam(idQues, scoreques);
					questionInExamList.add(quesToExam);

				}

			}

		}

	}

	/**
	 * charge the department name to the screen
	 */
	private void initDname() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getDepartmentByLid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList((Lecturer) LoginController.userLogged));

		packet.setParametersForCommand(Command.getDepartmentByLid, param);
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
					dname = p.<String>convertedResultListForCommand(Command.getDepartmentByLid);

					// checking the list
					if (dname.isEmpty() == true) {
						BuildExamLecturerDepartment.setText("-");
						return;
					}
					String str = String.format("%s", dname.get(0));
					BuildExamLecturerDepartment.setText(str);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();

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
					BuildExamCoursesCombo1.setItems(observelistCourse);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * open the screen of "Build new question"
	 */
	public void CreateNewQuestion() {

		examflag = 1;
		buildexamstage.hide();
		BuildQuestionController exec = new BuildQuestionController();
		exec.start();

	}

	/**
	 * Action button From The Drawer take the course id and get all the question
	 */
	public void fromTheDrawer() {

		Course course;
		int courseId;
		course = BuildExamCoursesCombo1.getValue();
		if (course == null) {
			displayAlert(AlertType.ERROR, "Error", "you are not select course , please retry!", null);

		} else {
			courseId = course.getcId();
			Packet packet = new Packet();
			// adding command
			packet.addCommand(Command.getQuestionByCid);

			ArrayList<Object> param = new ArrayList<>(Arrays.asList(courseId));

			packet.setParametersForCommand(Command.getQuestionByCid, param);
			// sending the packet
			SystemSender send = new SystemSender(packet);
			send.registerHandler(new IResultHandler() {

				@Override
				public void onWaitingForResult() {
					// TODO Auto-generated method stub

				}

				public void onReceivingResult(Packet p) {
					// TODO Auto-generated method stub

					{
						if (p.getResultState()) {

							ArrayList<Question> listQuestion;

							// filling the returned information from the server
							listQuestion = p.<Question>convertedResultListForCommand(Command.getQuestionByCid);
							questionlist = listQuestion;

							buildexamstage.hide();
							QuestionListController exec = new QuestionListController();
							exec.start();

						}
					}
				}
			});

			send.start();

		}

	}

	/**
	 * press on make exam - create the exam and send it to the DB
	 */
	public void CreateNewExam() {

		Course course;
		int lecturerId, courseId, departmentId;
		String nameLecturer, studentInstrc, lecturerInstrc, examCode, timeExam, author;
		Lecturer lecturerNAme = ((Lecturer) LoginController.userLogged);

		author = lecturerNAme.getUsername();
		lecturerId = lecturerNAme.getlId();

		departmentId = Department;
		course = BuildExamCoursesCombo1.getValue();
		nameLecturer = LoginController.userLogged.getUsername();
		studentInstrc = BuildExamStudentInstruc.getText();
		lecturerInstrc = BuildExamLecturerInstruction.getText();
		examCode = "----";
		timeExam = TimeTextField.getText();

		// check if the page is filled
		if (lecturerId == 0 || course == null || nameLecturer == null || studentInstrc == null || lecturerInstrc == null
				|| checkTime(timeExam) == false) {
			displayAlert(AlertType.ERROR, "Error", "The Build Page didn't fill corectly , please retry!", null);

		} else {
			courseId = course.getcId();

			Exam newExam = new Exam(courseId, departmentId, lecturerId ,author, examCode, timeExam, lecturerInstrc, studentInstrc);
			// save the exam without examID
			saveExam(newExam);

		}
	}

	/**
	 * create the ID to the Exam that send to the function
	 * 
	 * @param newExam
	 *            (Exam - without ID)
	 */
	private void saveExam(Exam newExam) {
		// TODO Auto-generated method stub

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCountExamByCid);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(newExam);
		packet.setParametersForCommand(Command.getCountExamByCid, examlist);

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
				int countExamCid;
				String examId;
				// TODO Auto-generated method stub
				if (p.getResultState()) {
					// building new packet
					ArrayList<Integer> listCountCourse;
					// filling the returned information from the server
					listCountCourse = p.<Integer>convertedResultListForCommand(Command.getCountExamByCid);
					countExamCid = listCountCourse.get(0);
					countExamCid++; // add one for the next empty raw
					examId = getNewExamId(newExam.getCourseId(), newExam.getDepartmentId(), countExamCid);
					newExam.setExamId(examId);
					insertExam(newExam); // put the exam to DB - Exam table
					insertQuesionInExam(newExam); // put the questions to DB - question in Exam table
					insertDoneExamDetails(newExam);
					
				}

				else { // if something wrong -> print error message
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}

			
		});

		send.start();

	}

	/**
	 * create the exam ID code
	 * 
	 * @param cId
	 *            - course ID
	 * @param dId
	 *            - department ID
	 * @param countExamCid
	 *            - count of existing exams +1 (for the new one)
	 * @return - new exam ID (6 digits)
	 */
	public String getNewExamId(int cId, int dId, int countExamCid) {

		String newExamId = "";

		if (dId < 10) {
			newExamId += "0" + dId;
		}

		else
			newExamId += "" + dId;

		if (cId < 10) {
			newExamId += "0" + cId;
		}

		else
			newExamId += "" + cId;

		if (countExamCid < 10) {
			newExamId += "0" + countExamCid;
		}

		else
			newExamId += "" + countExamCid;

		return newExamId;

	}

	/**
	 * method that put the exam to the DB
	 * 
	 * @param newExam - new exam
	 */
	public void insertExam(Exam newExam) {

		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.addExam);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(newExam);
		packet.setParametersForCommand(Command.addExam, examlist);

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

				if (!p.getResultState())

					displayAlert(AlertType.ERROR, "Error", "Error Exam Informationss , Please Try Again Later",
							p.getExceptionMessage());

			}
		});
		// sending the package
		send.start();
	}

	/**
	 * method - insert to DB - quesetion in exam table
	 * 
	 * @param newExam
	 *            (Exam with exam ID)
	 */
	public void insertQuesionInExam(Exam newExam) {
		String idExam;
		idExam = newExam.getExamId();
		for (int i = 0; i < questionInExamList.size(); i++) {
			questionInExamList.get(i).setExamId(idExam);
		}

		for (int i = 0; i < questionInExamList.size(); i++) {

			Packet packet = new Packet();
			// adding question
			packet.addCommand(Command.addQuestionInExam);
			ArrayList<Object> param = new ArrayList<>(Arrays.asList(questionInExamList.get(i)));
			;
			packet.setParametersForCommand(Command.addQuestionInExam, param);

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

				}
			});
			// sending the package
			send.start();
		}
		
	}
	
	
	/**
	 *  update the DB with the new exam details
	 * @param newExam
	 */
	private void insertDoneExamDetails(Exam newExam) {
		// TODO Auto-generated method stub
		
	
			Packet packet = new Packet();
			// adding question
			packet.addCommand(Command.addDoneExamDetails);
			ArrayList<Object> examlist;
			examlist = new ArrayList<>();
			examlist.add(newExam);
	
			
			packet.setParametersForCommand(Command.addDoneExamDetails,examlist);


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

				}
			});
			// sending the package
			send.start();
		

		displayAlert(AlertType.INFORMATION, "Success", "The Exam Has Been Added", null);

		back();
		
	}

	/**
	 * bring from DB the department ID by User ID
	 */
	public void takeDepartmentId() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getDepartmentIDByUidLecturer);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(LoginController.userLogged.getuId()));

		packet.setParametersForCommand(Command.getDepartmentIDByUidLecturer, param);
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
					ArrayList<Integer> Deplistt;

					// filling the returned information from the server
					Deplistt = p.<Integer>convertedResultListForCommand(Command.getDepartmentIDByUidLecturer);
					// checking the list
					if (Deplistt.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", null);
						return;
					}
					Department = Deplistt.get(0);
				}

			}
		});

		send.start();
	}

	/**
	 * method - check if the input of exam length is valid
	 * 
	 * @param timeExam
	 *            - the time from the lecturer input
	 * @return - true/false
	 */
	public Boolean checkTime(String timeExam) {
		// check if code is valid
		if (!timeExam.matches("[0-9]*"))
			return false;
		return true;

	}

}
