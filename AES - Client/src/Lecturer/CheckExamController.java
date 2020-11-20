package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import Course.Course;
import Exam.Exam;
import Exam.ExamController;
import Exam.FinishedExam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.FinishedQuestion;
import Student.student;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CheckExamController implements Initializable {
	public static Stage checkexamstage;
	/** course list */
	private ArrayList<Course> courselist;
	public static String exid;
	public static int sId;
	private ArrayList<Exam> examlist;
	public static int cId;
	private static int finalGrade;
	public static Float masterAvg,masterMed;

	private ArrayList<Exam> exListAvg ;
	private ArrayList<Exam> exListMed ;

	private ArrayList<student> studentlist;
	public static ArrayList<Integer> TimeList = new ArrayList<Integer>();
	public static ArrayList<Exam> AvgList = new ArrayList<Exam>();
    @FXML
    private TextField newGradeField;

	@FXML
	private Button BackBtn3;


    @FXML
    private RadioButton saveCurrentGradeRadio;

    @FXML
    private RadioButton changeNewGradeRadio;

    @FXML
    private TextArea reasonsNewGrade;
    
	@FXML
	private Button ViewExamBtn;

	@FXML
	private ComboBox<student> checkStudentCombo;

	@FXML
	private ComboBox<Course> checkCourseCombo;

	@FXML
	private TextField CurrentGrade;

	@FXML
	private Button updateGradeBtn;

	@FXML
	private ComboBox<Exam> checkExamCombo;

	@FXML
	private TextArea GradeChangeReasons;

	public void start() {
		checkexamstage = new Stage();

		String title = "Check Exam";
		String srcFXML = "/Lecturer/Lecturer-Check-Exam.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			checkexamstage.setTitle(title);
			checkexamstage.setScene(scene);
			checkexamstage.setResizable(false);
			checkexamstage.show();

			checkexamstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						lecturerMenuController.hidefunc(checkexamstage);
						lecturerMenuController.lecturermenu.show();
					} catch (Exception e) {
						checkexamstage.close();
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

	public void back() {

		try {
			lecturerMenuController.hidefunc(checkexamstage);
			lecturerMenuController.lecturermenu.show();
		} catch (Exception e) {
			checkexamstage.close();
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
					checkCourseCombo.setItems(observelistCourse);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * get a student details by course id in action
	 */
	public void  GetStudentByAction() {
		// TODO Auto-generated method stub
		
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getStudentByCid);
		ArrayList<Object> params = new ArrayList<>(Arrays.asList(checkCourseCombo.getValue().getcId()));
		cId = checkCourseCombo.getValue().getcId();
		packet.setParametersForCommand(Command.getStudentByCid, params);
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
					ObservableList<student> observelistStudent;
					ArrayList<student> listStudent;
					// filling the returned information from the server
					listStudent = p.<student>convertedResultListForCommand(Command.getStudentByCid);
					studentlist = listStudent;
					observelistStudent= FXCollections.observableArrayList(studentlist);
					checkStudentCombo.setItems(observelistStudent);
					
				}
				
			}
		});

		send.start();
	}
	/**
	 * get details of exam by student ID
	 */
	public void  GetExamByAction()  {
		// TODO Auto-generated method stub
		


		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamBySidCheckExam);
		ArrayList<Object> params = new ArrayList<>(Arrays.asList(checkCourseCombo.getValue(),checkStudentCombo.getValue()));

		packet.setParametersForCommand(Command.getExamBySidCheckExam, params);
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
					// filling the returned information from the server
					examlist =p.<Exam>convertedResultListForCommand(Command.getExamBySidCheckExam);
					observelistExam= FXCollections.observableArrayList(examlist);
					checkExamCombo.setItems(observelistExam);
					
				}
				
			}
		});

		send.start();
	}
	/**
	 * when the lecturer finish to check exam and press on 'update' button the function update the grade in DB
	 */
	public void updateGradeONACTION() {
		if(saveCurrentGradeRadio.isSelected()) {
			int newStudentID = checkStudentCombo.getValue().getuId();
			Packet packet = new Packet();
			FinishedExam currentGrade = new FinishedExam(reasonsNewGrade.getText(),checkExamCombo.getValue().getExamId(),newStudentID);
			finalGrade = Integer.parseInt(CurrentGrade.getText());
			Exam SelectedExam = checkExamCombo.getValue();
			exid = SelectedExam.getExamId();

			packet.addCommand(Command.saveCurrentGrade);
			ArrayList<Object> newlist;
			newlist = new ArrayList<>();
			newlist.add(currentGrade);
			packet.setParametersForCommand(Command.saveCurrentGrade, newlist);

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
						displayAlert(AlertType.INFORMATION, "Current grade saved!", "The Current grade has been saved", null);
						getAvg();
						

					} else {

						displayAlert(AlertType.ERROR, "Error", "Grade has not been updated , Please Try Again Later",p.getExceptionMessage());

					}
				}


			});
			send.start();
		}
		if(checkStudentCombo.getSelectionModel().isEmpty()){
			displayAlert(AlertType.ERROR, "ERROR!", "No student selected", null);
			return;
		}
		if(checkExamCombo.getSelectionModel().isEmpty()) {
			displayAlert(AlertType.ERROR, "ERROR!", "No exam selected", null);
			return;
		}
		if(!newGradeField.getText().matches("[0-9]*")){
			displayAlert(AlertType.ERROR, "ERROR!", "Grade must be numbers", null);
			return;
		}
		
		
		if(changeNewGradeRadio.isSelected()) {
			if(reasonsNewGrade.getText().isEmpty()) {
				displayAlert(AlertType.ERROR, "ERROR!", "You must enter reasons", null);
				return;
			}
		String newGrade = newGradeField.getText();
		if(newGrade.isEmpty()) {
			displayAlert(AlertType.ERROR, "Error", "No exams", null);
			return;
		}
		
		
		int newGradeINT = Integer.parseInt(newGrade);
		
		if(newGradeINT > 100 || newGradeINT < 0) {
			displayAlert(AlertType.ERROR, "Error", "Enter Grade between 0-100", null);
			return;
		}
		int newStudentID = checkStudentCombo.getValue().getuId();
		FinishedExam newCodeExam = new FinishedExam(newGradeINT,checkExamCombo.getValue().getExamId(),newStudentID);
		finalGrade = Integer.parseInt(newGradeField.getText());

		
		
		Packet packet = new Packet();
		packet.addCommand(Command.updateStudentGrade);
		ArrayList<Object> newlist;
		newlist = new ArrayList<>();
		newlist.add(newCodeExam);
		packet.setParametersForCommand(Command.updateStudentGrade, newlist);
		//packet.setParametersForCommand(Command.updateStudentGradeInExam, newlist);
		FinishedExam newReasonList = new FinishedExam(reasonsNewGrade.getText(),checkExamCombo.getValue().getExamId(),newStudentID);
		ArrayList<Object> newReason;
		newReason = new ArrayList<>();
		newReason.add(newReasonList);
		packet.setParametersForCommand(Command.updateChangeGradeReason, newReason);
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
					displayAlert(AlertType.INFORMATION, "Success", "Grade has been updated", null);
					getAvg();
					
					

				} else {

					displayAlert(AlertType.ERROR, "Error", "Grade has not been updated , Please Try Again Later",p.getExceptionMessage());

				}
			}

		
		});
		// sending the package
		send.start();
	}
	}

	
	/**
	 * update the students grade in DB
	 */
	public void updateGradeStudentInExam() {

		Packet packet = new Packet();
		packet.addCommand(Command.updateStudentGrade);
		ArrayList<Object> newlist;
		newlist = new ArrayList<>();
	//	newlist.add(newCodeExam);
		packet.setParametersForCommand(Command.updateStudentGrade, newlist);

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
					displayAlert(AlertType.INFORMATION, "Success", "Grade has been updated", null);
					getAvg();
					back();
					

				} else {

					displayAlert(AlertType.ERROR, "Error", "Grade has not been updated , Please Try Again Later",p.getExceptionMessage());

				}
			}


		});
	}
	


	/**
	 * get the Average of student
	 */
	private void getAvg(){
		// TODO Auto-generated method stub
		


		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getStudentAvg);
		ArrayList<Object> params = new ArrayList<>(Arrays.asList(checkStudentCombo.getValue().getuId()));

		packet.setParametersForCommand(Command.getStudentAvg, params);
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
					AvgList =p.<Exam>convertedResultListForCommand(Command.getStudentAvg);
					
					if(AvgList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error, Please try again","", " no grade for this exam");
						return;
					}
					
					Exam toFloat = AvgList.get(0);
					String newFloat = toFloat.getExamId();
					Float f = Float.parseFloat(newFloat);
					
					
					
					updateAverage(f);
					
					
					
					
											
				
				}
				
			}


		});

		send.start();
	}

/**
 * update in DB the average grade of current student
 * @param Avg
 */
	private void updateAverage(Float Avg) {
		int newStudentID = checkStudentCombo.getValue().getuId();
		
		student newCodeExam = new student(Avg,newStudentID);
		Packet packet = new Packet();
		packet.addCommand(Command.updateStudentAvg);
		ArrayList<Object> newlist;
		newlist = new ArrayList<>();
		newlist.add(newCodeExam);
		packet.setParametersForCommand(Command.updateStudentAvg, newlist);

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
					displayAlert(AlertType.INFORMATION, "Success", "Average has been updated", null);
					
					reportCourse();


				} else {

					displayAlert(AlertType.ERROR, "Error", "Average hasnt been updated, Please Try Again Later",p.getExceptionMessage());

				}
			}


		});
		// sending the package
		send.start();
	
	}
	
	/**
	 * get the grade of student when the student ID exist
	 */
	public void getExamGradeONACTION(){
		// TODO Auto-generated method stub
		


				Packet packet = new Packet();
				// adding command
				packet.addCommand(Command.getGradesbySid);
				ArrayList<Object> params = new ArrayList<>(Arrays.asList(checkExamCombo.getValue(),checkStudentCombo.getValue()));

				packet.setParametersForCommand(Command.getGradesbySid, params);
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
							TimeList =p.<Integer>convertedResultListForCommand(Command.getGradesbySid);
							
							if(TimeList.isEmpty() == true) {
								displayAlert(AlertType.ERROR, "Error, Please try again","", " no grade for this exam");
								return;
							}
							int Grade = TimeList.get(0);
							
							CurrentGrade.setText(Integer.toString(Grade));
						}
						
					}
				});

				send.start();
			}
	
	/**
	 * unlock the radio buttons
	 */
	public void unlock() {
		if(changeNewGradeRadio.isSelected()) {
			newGradeField.setEditable(true);
			reasonsNewGrade.setEditable(true);
			
		}
	}
	/**
	 * lock the radio buttons
	 */
	public void lock() {
		if(saveCurrentGradeRadio.isSelected()) {
			newGradeField.clear();
			reasonsNewGrade.clear();
			newGradeField.setEditable(false);
			reasonsNewGrade.setEditable(false);
			
			
		}
	}
	/**
	 * show the exam on the Stage
	 */
	public void showExam() {

		
		if (checkCourseCombo.getSelectionModel().isEmpty() || checkExamCombo.getSelectionModel().isEmpty()  || checkStudentCombo.getSelectionModel().isEmpty()) {
			displayAlert(AlertType.ERROR, null, "Error", "One or more fields are empty");
			return;
		} else {
			checkexamstage.hide();
			Course Selectedcourse = checkCourseCombo.getValue();
			Exam SelectedExam = checkExamCombo.getValue();
			student SelectedStudent = checkStudentCombo.getValue();
			
			exid = SelectedExam.getExamId();
			sId = SelectedStudent.getuId();
			LecturerSetNotesController exViewEx = new LecturerSetNotesController();
			exViewEx.start(exid,sId);
		}
		// TODO Auto-generated method stub
		

		

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getGradesbySid);
		ArrayList<Object> params = new ArrayList<>(Arrays.asList(checkExamCombo.getValue(),checkStudentCombo.getValue()));

		packet.setParametersForCommand(Command.getGradesbySid, params);
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
					TimeList =p.<Integer>convertedResultListForCommand(Command.getGradesbySid);
					
					if(TimeList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error, Please try again","", " no grade for this exam");
						return;
					}
					int Grade = TimeList.get(0);
											
					CurrentGrade.setText(Integer.toString(Grade));
				}
				
			}
		});
		
	}
	
	/**
	 * the function check where grades need to be insert to DB in the specific column for student
	 */
	private void reportCourse()
	{
		int countGrade[] = new int[10];
		String query="";
		for (int i = 0; i < 10; i++) {
			countGrade[i] = 0;
		}
		
		// TODO Auto-generated method stub

			if (finalGrade < 11) {

				countGrade[0] = ++countGrade[0];
				

			} else if (finalGrade < 21) {

				countGrade[1] = ++countGrade[1];

			} else if (finalGrade < 31) {

				countGrade[2] = ++countGrade[2];

			}

			else if (finalGrade < 41) {
				countGrade[3] = ++countGrade[3];

			} else if (finalGrade < 51) {
				countGrade[4] = ++countGrade[4];

			} else if (finalGrade < 61) {
				countGrade[5] = ++countGrade[5];

			} else if (finalGrade < 71) {

				countGrade[6] = ++countGrade[6];

			}

			else if (finalGrade < 81) {

				countGrade[7] = ++countGrade[7];

			} else if (finalGrade < 91) {
				countGrade[8] = ++countGrade[8];

			} else if (finalGrade < 101) {
				countGrade[9] = ++countGrade[9];
			}
			
				 if(countGrade[0] != 0)
				 {
					 query = "a";
				 }
				 
				 else if(countGrade[1] != 0)
				 {
					 query = "b";
					 
				 }
				 else if(countGrade[2] != 0)
				 {
					 query = "c";
					 
				 }
				 else if(countGrade[3] != 0)
				 {
					 query = "d";
					 
				 }
				 else if(countGrade[4] != 0)
				 {
					 query = "e";
					 
				 }
				 
				 else if(countGrade[5] != 0)
				 {
					 query = "f";
					 
				 }
				 else if(countGrade[6] != 0)
				 {
					 query = "g";
					 
				 }
				
				 else if(countGrade[7] != 0)
				 {
					 query = "h";
					 
				 }
				 else if(countGrade[8] != 0)
				 {
					 query = "i";
					 
				 }
				
				 else if(countGrade[9] != 0)
				 {
					 query = "j";
					 
				 }
				 
			FinishedExam gradeReport = new	FinishedExam(query,query,cId);
			
			Packet packet = new Packet();
			// adding question
			packet.addCommand(Command.UpdateCountReportCourse);
			ArrayList<Object> reportlist;
			reportlist = new ArrayList<>();
			reportlist.add(gradeReport);
			packet.setParametersForCommand(Command.UpdateCountReportCourse, reportlist);
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
						
						reportLecturer();
					}
					else {

					}
				}
			});
			// sending the package
			send.start();
			
			
		}
	
	/**
	 * the function check where grades need to be insert to DB in the specific column for lecturer
	 */
	public void reportLecturer()
	{
		
		int countGrade[] = new int[10];
		String query="";
		for (int i = 0; i < 10; i++) {
			countGrade[i] = 0;
		}
		
		// TODO Auto-generated method stub

			if (finalGrade < 11) {

				countGrade[0] = ++countGrade[0];
				

			} else if (finalGrade < 21) {

				countGrade[1] = ++countGrade[1];

			} else if (finalGrade < 31) {

				countGrade[2] = ++countGrade[2];

			}

			else if (finalGrade < 41) {
				countGrade[3] = ++countGrade[3];

			} else if (finalGrade < 51) {
				countGrade[4] = ++countGrade[4];

			} else if (finalGrade < 61) {
				countGrade[5] = ++countGrade[5];

			} else if (finalGrade < 71) {

				countGrade[6] = ++countGrade[6];

			}

			else if (finalGrade < 81) {

				countGrade[7] = ++countGrade[7];

			} else if (finalGrade < 91) {
				countGrade[8] = ++countGrade[8];

			} else if (finalGrade < 101) {
				countGrade[9] = ++countGrade[9];
			}
			
				 if(countGrade[0] != 0)
				 {
					 query = "a";
				 }
				 
				 else if(countGrade[1] != 0)
				 {
					 query = "b";
					 
				 }
				 else if(countGrade[2] != 0)
				 {
					 query = "c";
					 
				 }
				 else if(countGrade[3] != 0)
				 {
					 query = "d";
					 
				 }
				 else if(countGrade[4] != 0)
				 {
					 query = "e";
					 
				 }
				 
				 else if(countGrade[5] != 0)
				 {
					 query = "f";
					 
				 }
				 else if(countGrade[6] != 0)
				 {
					 query = "g";
					 
				 }
				
				 else if(countGrade[7] != 0)
				 {
					 query = "h";
					 
				 }
				 else if(countGrade[8] != 0)
				 {
					 query = "i";
					 
				 }
				
				 else if(countGrade[9] != 0)
				 {
					 query = "j";
					 
				 }
				 
			Lecturer lecturerNAme = ((Lecturer) LoginController.userLogged);
			 int lecturerId = lecturerNAme.getlId();
	
				 
			FinishedExam gradeReport = new	FinishedExam(query,query,lecturerId);
			
			Packet packet = new Packet();
			// adding question
			packet.addCommand(Command.UpdateLecturercReport);
			ArrayList<Object> reportlist;
			reportlist = new ArrayList<>();
			reportlist.add(gradeReport);
			packet.setParametersForCommand(Command.UpdateLecturercReport, reportlist);
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
						
						
						setAvgForExam(exid);
						
						
					}
					else {

					}
				}
			});
			// sending the package
			send.start();
			
			
		}
		
		
	
	
	
	
	public void setAvgForExam(String exId) {
		   // TODO Auto-generated method stub



		   Packet packet = new Packet();
		   // adding command
		   packet.addCommand(Command.getAvgForExamByExId);
		   ArrayList<Object> param = new ArrayList<>(Arrays.asList(exId));

		   packet.setParametersForCommand(Command.getAvgForExamByExId, param);
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
		         //ObservableList<Exam> observelistExam;
		         String exId;
		         // filling the returned information from the server
		         exListAvg = p.<Exam>convertedResultListForCommand(Command.getAvgForExamByExId);
		         masterAvg = (float)exListAvg.get(0).getAvg();
		         exId = exListAvg.get(0).getExamId();
		         updateAvgData(exId, masterAvg);

		       }

		     }

		   });

		   send.start();
		 }


		  private void updateAvgData(String exId, Float masterAvg) {

		     Packet packet = new Packet();
		     // adding command
		     packet.addCommand(Command.updateAvgForExamIdByExId);
		     Exam newExam = new Exam (exId, masterAvg);

		     ArrayList<Object> activelist;
		     activelist = new ArrayList<>();
		     activelist.add(newExam);

		     packet.setParametersForCommand(Command.updateAvgForExamIdByExId, activelist);
		     // sending the packet
		     SystemSender send = new SystemSender(packet);
		     send.registerHandler(new IResultHandler() {

		       @Override
		       public void onWaitingForResult() {
		       }

		       @Override
		       public void onReceivingResult(Packet p) {
		         setMedForExam(exId);


		       }
		     });

		     send.start();

		  }

		  public void setMedForExam(String exId) {
		      // TODO Auto-generated method stub



		      Packet packet = new Packet();
		      // adding command
		      packet.addCommand(Command.getMedForExamByExId);
		      ArrayList<Object> param = new ArrayList<>(Arrays.asList(exId));

		      packet.setParametersForCommand(Command.getMedForExamByExId, param);
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
		            String exId;
		            // filling the returned information from the server
		            exListMed = p.<Exam>convertedResultListForCommand(Command.getMedForExamByExId);
		            masterMed = (float)exListMed.get(0).getMedian();
		            exId = exListMed.get(0).getExamId();
		            updateMedData(exId, masterMed);
		          }

		        }

		      });

		      send.start();
		    }


		    private void updateMedData(String exId, Float masterMed) {

		       Packet packet = new Packet();
		       // adding command
		       packet.addCommand(Command.updateMedForExamIdByExId);

		       Exam newExam = new Exam (masterMed, exId);
		       ArrayList<Object> activelist;
		       activelist = new ArrayList<>();
		       activelist.add(newExam);
		       packet.setParametersForCommand(Command.updateMedForExamIdByExId, activelist);

		       // sending the packet
		       SystemSender send = new SystemSender(packet);
		       send.registerHandler(new IResultHandler() {

		         @Override
		         public void onWaitingForResult() {
		         }

		         @Override
		         public void onReceivingResult(Packet p) {
		         }
		       });

		       send.start();

		    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
				 

			
			
			
			
		
		
		
	
	
	
	
	



