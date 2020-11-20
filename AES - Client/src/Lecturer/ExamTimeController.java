package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import ActiveExam.ActiveExam;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import Course.Course;
import Exam.Exam;
import Exam.ExamController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ExamTimeController implements Initializable {
	public static String course, exid;
	public static Stage changetimestage;
	/** course list */
	private ArrayList<Course> courselist;
	/** exams list */
	private ArrayList<Exam> examslist;
	String valueOfTime;
    @FXML
    private TextField TextAreaReason;
	@FXML
	private RadioButton ChangeTimeLockExamRadio;
	int flag =0;
	@FXML
	private Button submitTime;

	@FXML
	private ComboBox<Exam> ChangeExamTimeExamButton;

	@FXML
	private Button backBtn14;

	@FXML
	private TextField changeTimeMinutesField;

	@FXML
	private RadioButton addTimeRadio;

	@FXML
	private ToggleGroup G1;
	int getValue;
	@FXML
	private ComboBox<Course> changeExamTimeCourseCombo;

	@FXML
	private TextField reasonTextField;
	/**
	 * start function to initialize a new stage with the specific fxml file.
	 */
	public void start() {
		changetimestage = new Stage();

		String title = "Exam Time Change And Lock ";
		String srcFXML = "/Lecturer/Lecturer-Change-Time-Lock.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			changetimestage.setTitle(title);
			changetimestage.setScene(scene);
			changetimestage.setResizable(false);
			changetimestage.show();

			changetimestage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(changetimestage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						changetimestage.close();
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
	 * set reasonTeextField to lock and clears the text.
	 */
	public void onPressLock() {

		reasonTextField.setEditable(false);
		reasonTextField.clear();
		/**
		 * When clicking on the add button text become available for writing.
		 */
	}
	public void onPressTimeadd() {
		reasonTextField.setEditable(true);
	}
	/**
	 * back function to hide the current stage and go back for the last stage used.
	 */
	public void back() {

		try {
			lecturerMenuController.hidefunc(changetimestage);
			lecturerMenuController.lecturermenu.show();
		} catch (Exception e) {
			changetimestage.close();
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		// initialize the combo boxes
		initComboBox();
	}

	/**
	 * initialize the combo box appropriate courses
	 * gets all courses by Lecturer Id by sending the current user logged in with (Lecturer) LoginController.userLogged.
	 */
	public void initComboBox() {
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
					changeExamTimeCourseCombo.setItems(observelistCourse);
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
	 * get all the "Now Running" exams by the lexturer id.
	 * sending to the query the params
	 * (Lecturer) LoginController.userLogged - to the user id, and the changeExamTimeCourseCombo.getValue() for the course id selected in the other combo box.
	 */

	public void getallExams() {
		// TODO Auto-generated method stub
		


		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getOnlineExamByLecturer);
		ArrayList<Object> params = new ArrayList<>(Arrays.asList((Lecturer) LoginController.userLogged,changeExamTimeCourseCombo.getValue()));

		packet.setParametersForCommand(Command.getOnlineExamByLecturer, params);
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
					ObservableList<Exam> observelistExam;
					ArrayList<Exam> listExam;
					// filling the returned information from the server
					listExam = p.<Exam>convertedResultListForCommand(Command.getOnlineExamByLecturer);
					examslist = listExam;
					observelistExam= FXCollections.observableArrayList(examslist);
					ChangeExamTimeExamButton.setItems(observelistExam);
				}
				
			}
		});

		send.start();
	}
	
	/**
	 * get the selected course and combo and saves it in a global static parameters (course,exid) to pass it to the other functions.
	 */
	
	public void executionCode() {

		Course Selectedcourse = changeExamTimeCourseCombo.getValue();
		Exam SelectedExam = ChangeExamTimeExamButton.getValue();

		if (Selectedcourse == null || SelectedExam == null) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
		}

		else {
			course = Selectedcourse.getcName();
			exid = SelectedExam.getExamId();

			changetimestage.hide();
			ExecutionCodeController exec = new ExecutionCodeController();
			exec.start();

		}
	}
	/**
	 * show  the selected exam by the courseid and examid selected, checks if one of them is null for the checking.
	 */
	public void showExam() {
		Course Selectedcourse = changeExamTimeCourseCombo.getValue();
		Exam SelectedExam = ChangeExamTimeExamButton.getValue();

		if (Selectedcourse == null || SelectedExam == null) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
		} else {
			changetimestage.hide();
			ExamController exViewEx = new ExamController();
			exViewEx.start();// opens up the exam view window.
		}

	}
	/**
	 * lock the radio button if another is selected.
	 */
	public void lockRadioBtn() {
		TextAreaReason.setEditable(false);
		TextAreaReason.clear();
		changeTimeMinutesField.clear();
		changeTimeMinutesField.setEditable(false);
	}
	/**
	 * change exam time if its selected clearing other text field and set changeTimeMinutesField to true.
	 */
	public void changeExamTime() {
		TextAreaReason.setEditable(true);
		changeTimeMinutesField.setEditable(true);
	}
	
	/**
	 * On-Action function when clicking on submit in the Lecturer-Execution-Code fxml,
	 * checks if on of the combo boxes is empty or all of them are empty and return the right displayAlert.
	 * if the lecturer presses on lock exam - saving the current time, started students, finished students and how much time the exam took in overall.
	 */
	public void submitOnAction() {
		if(ChangeTimeLockExamRadio.isSelected()) {
		if(changeExamTimeCourseCombo.getSelectionModel().isEmpty()) {
			displayAlert(AlertType.ERROR, "ERROR!", "Select a course first!", null);
			return;
		}
		if(ChangeExamTimeExamButton.getSelectionModel().isEmpty()) {
			displayAlert(AlertType.ERROR, "ERROR!", "Select exam!", null);
			return;
		}
		/**
		 * Update the exam to lock.
		 */
			Exam newCodeExam = new Exam(ChangeExamTimeExamButton.getValue().getExamId());
			Packet packet = new Packet();
			packet.addCommand(Command.updateIsLocked);
			ArrayList<Object> examlist;
			examlist = new ArrayList<>();
			examlist.add(newCodeExam);
			packet.setParametersForCommand(Command.updateIsLocked, examlist);
			////////////////////////////////////////////////////////////////////////////////////////////////
			/**
			 * update the total time of the exam, Real time + Extended time.
			 */
			ActiveExam updateTotalTime = new ActiveExam(ChangeExamTimeExamButton.getValue().getExamId(),ChangeExamTimeExamButton.getValue().getExamId());
			packet.addCommand(Command.updateTotalTime);
			ArrayList<Object> totalList;
			totalList = new ArrayList<>();
			totalList.add(updateTotalTime);
			packet.setParametersForCommand(Command.updateTotalTime, totalList);
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			/**
			 * Update all the exam statistics to the Database.
			 */
			ActiveExam updateStatisticsExam = new ActiveExam(ChangeExamTimeExamButton.getValue().getExamId());
			packet.addCommand(Command.updateExamStatistics);
			ArrayList<Object> studStats;
			studStats = new ArrayList<>();
			studStats.add(updateStatisticsExam);
			packet.setParametersForCommand(Command.updateExamStatistics, studStats);
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
						displayAlert(AlertType.INFORMATION, "Success", "The exam is now locked!", null);
						back();

					} else {

						displayAlert(AlertType.ERROR, "Error", "The exam has not changed to locked! , Please Try Again Later",p.getExceptionMessage());

					}
				}
			});
			// sending the package
			send.start();
		
		flag = 1;
		}

		/**
		 *Checks for Unselected course,exam of missing fields.
		 *Flags = 0,1,2 to check which error occurred or which button was selected.
		 */
	    
		if(flag==0) {
		if(addTimeRadio.isSelected()) {
			if(changeExamTimeCourseCombo.getSelectionModel().isEmpty()) {
				displayAlert(AlertType.ERROR, "ERROR!", "Select a course first!", null);
				return;
			}
			if(ChangeExamTimeExamButton.getSelectionModel().isEmpty()) {
				displayAlert(AlertType.ERROR, "ERROR!", "Select exam!", null);
				return;
			}
		}
		if(addTimeRadio.isSelected() && changeTimeMinutesField.getText().toString().isEmpty()){
	    	displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
	    	return;
	    	}
		
		if(addTimeRadio.isSelected())
			flag=2;
	        valueOfTime = changeTimeMinutesField.getText();
	        String valueOfTimeSTR = changeTimeMinutesField.getText().toString();
	        if(valueOfTimeSTR.matches("[a-zA-Z]+\\.?")){
	        displayAlert(AlertType.ERROR, "Error", "The Time need only an integer number between 1-60", null);
	        flag=0;
	        return;
	        }
	        
	        else {
	        int valueOfTimeINT=Integer.parseInt(valueOfTime);
	        if(valueOfTimeINT > 60|| valueOfTimeINT < 1 || !(valueOfTimeSTR.matches("[0-9]*")) ){
	    	displayAlert(AlertType.ERROR, "Error", "The Time need only an integer number between 1-60", null);
	    	flag=0;
	    	return;
	    	}
	        else {
	        	flag=2;
	        }
	        }
	        
	        
		}
	     
			
		/**
		 * if flag =2 ,
		 * checking if a reason for time change is exists.
		 * If true, sending a new command for the server with the exam id and the selected Time-Extended time to the query with the reason.
		 */
		
		if(flag==2) {
			if(TextAreaReason.getText().toString().isEmpty()) {
		    	displayAlert(AlertType.ERROR, "Error", "You must enter a reason for time change.", null);
		    	return;
			}
			String NewExamID = ChangeExamTimeExamButton.getValue().getExamId();
			Packet packet = new Packet();
			packet.addCommand(Command.updateExtraTime);
			String toSendReason = TextAreaReason.getText();
			toSendReason.toString();
			String toSendTime = changeTimeMinutesField.getText();
			int toSendTimeINT = Integer.parseInt(toSendTime);
			Exam newExams = new Exam(NewExamID ,toSendTimeINT , toSendReason );
			
			ArrayList<Object> examlist;
			examlist = new ArrayList<>();
			
			examlist.add(newExams);
			packet.setParametersForCommand(Command.updateExtraTime, examlist);

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
						displayAlert(AlertType.INFORMATION, "Success", "Change exam time has been sent to the Principal", null);
						back();

					} else {

						displayAlert(AlertType.ERROR, "Error", "Could not send to principal , Please Try Again",p.getExceptionMessage());

					}
				}
			});
			// sending the package
			send.start();
		}

		flag=0;
		}
		
	
}
	