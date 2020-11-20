package Lecturer;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import ActiveExam.ActiveExam;
import Exam.Exam;
import Exam.FinishedExam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.TextField;

import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ExecutionCodeController implements Initializable {
	   @FXML
	    private TextField execCourseField;
	   
	    @FXML
	    private TextField execExamField;
	    public static ArrayList<ActiveExam> TimeList = new ArrayList<ActiveExam>();
	    @FXML
	    private TextField SetExecCodeField;
	    /**
	     * Examflag - to check if the exam is locked when all the students that entered the exam finished it to lock the exam.
	     * newExamTime - the exam time.
	     */
	    private static int Examflag=0;
	    private static int newExamTime;
	    /**
	     * TimerToAccess - set available enter to an exam to 1800 seconds - 30Minutes.
	     **/
	    private static int TimerToAccess=1800;//It is a real time check, for a demo check change to 180 for 3 minutes.
	    public static Stage executioncodestage;
	    private static int lecid;
	
		
	public void start() {
		executioncodestage = new Stage();

		String title = "Execution Code";
		String srcFXML = "/Lecturer/Lecturer-Execution-Code.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			executioncodestage.setTitle(title);
			executioncodestage.setScene(scene);
			executioncodestage.setResizable(false);
			executioncodestage.show();
		
			executioncodestage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				 public void handle(WindowEvent we) {

					 try {
							lecturerMenuController.hidefunc(executioncodestage);
							lecturerMenuController.lecturermenu.show();
						} catch (Exception e) {
							executioncodestage.close();
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
			UseExistingController.hidefunc(executioncodestage);
			UseExistingController.useexistingstage.show();
		} catch (Exception e) {
			executioncodestage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
	

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
				 //initialize the combo boxes
		execCourseField.setText(UseExistingController.course);
		execExamField.setText(UseExistingController.exid);

	}

	/**
	 * execCode - if submit is pressed, checking that all the fields are filled up
	 * code lenght, if matches the correct characters
	 * and pop up the alert error if needed.
	 */
	
	
	public void execCode()
	{
		String code = SetExecCodeField.getText();
		// check if code is valid
		if(code.length() != 4)
		{
			displayAlert(AlertType.ERROR, "Error", "The code need only 4 digits",null);
			return;
		}
		else if(!code.matches("[a-zA-Z0-9]*"))
				{
			displayAlert(AlertType.ERROR, "Error", "The code need only numbres or letters",null);
			return;

				}
	
			checkCodeDb(code,UseExistingController.exid);

	}
/**
 * 
 * @param code - check if code exists in DB by sending the examid
	 * and get the exam specific time.
 * @param exid - the exam id
 */
        public void checkCodeDb(String code,String exid)
        {
        	Packet packet = new Packet();
    		// adding question
    		packet.addCommand(Command.CheckCodeExam);
    		packet.addCommand(Command.getExamTime);
    		ArrayList<Object> examlist;
    		examlist = new ArrayList<>();
    		examlist.add(code);
    		ArrayList<Object> timelist;
    		timelist = new ArrayList<>();
    		timelist.add(exid);
    		packet.setParametersForCommand(Command.CheckCodeExam, examlist);
    		packet.setParametersForCommand(Command.getExamTime, timelist);

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
    					 ArrayList<Integer> checkCode;
    					 ArrayList<Integer> examTime;
    					 checkCode = p.<Integer>convertedResultListForCommand(Command.CheckCodeExam);
    					 examTime=p.<Integer>convertedResultListForCommand(Command.getExamTime);
    					 /**
    					  * saves the current exam time to newExamTime
    					  */
    					  newExamTime=examTime.get(0);
    					  newExamTime=14400; // This is a real time check, for demo check it needs to be 120 for 2 minutes
    					  /**
    					   * if code is ok, update the new exam code.
    					   */
    					 if (checkCode.get(0) == 0)
    					 {
    						 
    						 Exam newCodeExam = new Exam(UseExistingController.exid,code);
    						 updateCode(newCodeExam);

    				} else {

    					displayAlert(AlertType.ERROR, "Error", "The Code Not Change change the code , Please Try Again Later",null);

    				}
    			}
    			}

    		});
    		// sending the package
    		send.start();

   	
        }
        
		/**
		 * 
		 * @param newCodeExam - gets the new code exam to send to the UpdateCodeExam Command for update in the database.
		 */
        public void updateCode(Exam newCodeExam)
        {
		
		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.UpdateCodeExam);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(newCodeExam);
		packet.setParametersForCommand(Command.UpdateCodeExam, examlist);

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
					activeExam(newCodeExam);
					

				} else {

					displayAlert(AlertType.ERROR, "Error", "The Code Not Change , Please Try Again Later",p.getExceptionMessage());

				}
			}
		});
		// sending the package
		send.start();
		
		
	}
	/**
	 * Insert the exam to DB - active exam table with column active exam with value - 1
	 * @param newCodeExam - gets the new code exam.
	 */
	public void  activeExam(Exam newCodeExam)
	{
		/**
		 * active = 1 , active exam
		 * lecturerflag = 0, didnt ask for time extension,
		 * principalflag= 0 , no request to the principal
		 * extraTime=0, no extraTime asked yet.
		 */
		int lid , active = 1 , lecturerflag = 0 , principalFlag = 0 , extraTime = 0;
		String examId;
		Lecturer lecturer = (Lecturer) LoginController.userLogged;
		lid = lecturer.getlId();
		lecid =lid;
		examId = newCodeExam.getExamId();
		
		ActiveExam newExamActive = new ActiveExam (examId , lid , active , lecturerflag , principalFlag , extraTime);
		
		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.SetActiveExam);
		ArrayList<Object> activelist;
		activelist = new ArrayList<>();
		activelist.add(newExamActive);
		packet.setParametersForCommand(Command.SetActiveExam, activelist);

		
		ActiveExam newExamDate = new ActiveExam (examId);
		packet.addCommand(Command.updateDateExam);
		ArrayList<Object> updateDatelist;
		updateDatelist = new ArrayList<>();
		updateDatelist.add(newExamDate);
		packet.setParametersForCommand(Command.updateDateExam, updateDatelist);
		
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
					changeExamTime();
					reportExam();

					
				} else {

					displayAlert(AlertType.ERROR, "Error", "Please create a new exam, this exam id is already in use. , Please Try Again Later",null);
					return;
				}
			}
		});
		// sending the package
		send.start();
		
	
	}

	
	
	/**
	 * a function the change the exam time of the selected examid(exid)
	 * getting courseid by exam id and keeps to insertChangeTime func to change the time in the student exam.
	 */
	public void changeExamTime()
	{
		String exid;
		
		
		exid = UseExistingController.exid;

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCourseIDbyExamID);
		

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exid));

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
					 ArrayList<Integer> examCourse;
					 int courseId;

					// filling the returned information from the server
					 examCourse = p.<Integer>convertedResultListForCommand(Command.getCourseIDbyExamID);

					// checking the list
					if (examCourse.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error", "not insert to change time table  ",null);
						return;
					}
					courseId = examCourse.get(0);
					Exam examChangeTime = new Exam(exid , lecid , courseId );
					insertChangeTime(examChangeTime);
					
					
				}

				
			}
		});

		send.start();

	}
	/**
	 * inserting the needed time to the command addExamToChangeTime, to update the database.
	 * @param examChangeTime - the time to be changed
	 */

	public void insertChangeTime(Exam examChangeTime)
	{
		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.addExamToChangeTime);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(examChangeTime);
		packet.setParametersForCommand(Command.addExamToChangeTime, examlist);

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

				if (p.getResultState())
					
			

			
				startCountTime();
				
			}
		});
		// sending the package
		send.start();

	}
	/**
	 * A timer that start when the lecturer make execution code for the students.
	 * TimerToAccess parameter counts dont 30 minutes until students can enter the exam and uses ableToAccess() function to disable entrance.
	 * newExamTime parameter to count down 4 hours(The maximum time available including extended time and then lock the exam for good.)
	 */
	public void startCountTime() {
		Timer newone= new Timer();
		
		newone.schedule(new TimerTask() {
			
			public void run() {
				
				Platform.runLater(new Runnable() {
					public void run() {
						TimerToAccess--;
						newExamTime--;
						if (TimerToAccess == 0) {// cannot access after 30 minutes.
							ableToAccess();
						}
						
						if(TimerToAccess<0) {
							
							checkAllFinished();
							if(Examflag==1) {
								newone.cancel();
								newone.purge();
							}
							
						}
						
						if(newExamTime==0) {
							unActiveExam();
							newone.cancel();
							newone.purge();
							
						}

						}

			
					

				
				});
			}
		}, 1000, 1000);
		
	}
	
	
	public void checkAllFinished() { // TODO Auto-generated method stub
	


	Packet packet = new Packet();
	// adding command
	packet.addCommand(Command.checkStatistics);
	ArrayList<Object> params = new ArrayList<>(Arrays.asList(UseExistingController.exid,UseExistingController.exid));

	packet.setParametersForCommand(Command.checkStatistics, params);
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
				TimeList =p.<ActiveExam>convertedResultListForCommand(Command.checkStatistics);
				int ongoing = TimeList.get(0).getStarted();					
				if(ongoing==0 && Examflag == 0) {
					Examflag=1;
				unActiveExam();
				
					}
				}
		
				return;
				
			}
	
		
	});

	send.start();
}

	
	
	/**
	 * This function uses
	 * updateIsLocked
	 * updateTotalTime and updateExamStatistics commands to save the exam statistics when an exam ends(if all started students equals all finished students)
	 * or if the exam has passed the limit of the exam - 4 hours.
	 */
	public void unActiveExam() {
		Packet packet = new Packet();
		Exam updateAcess = new Exam(UseExistingController.exid);
		packet.addCommand(Command.updateIsLocked);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(updateAcess);
		packet.setParametersForCommand(Command.updateIsLocked, examlist);
		/////////////////////////////////////////////////////////////////////////////////////////
		ActiveExam updateTotalTime = new ActiveExam(UseExistingController.exid,UseExistingController.exid);
		packet.addCommand(Command.updateTotalTime);
		ArrayList<Object> totalList;
		totalList = new ArrayList<>();
		totalList.add(updateTotalTime);
		packet.setParametersForCommand(Command.updateTotalTime, totalList);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		ActiveExam updateStatisticsExam = new ActiveExam(UseExistingController.exid);
		packet.addCommand(Command.updateExamStatistics);
		ArrayList<Object> studStats;
		studStats = new ArrayList<>();
		studStats.add(updateStatisticsExam);
		packet.setParametersForCommand(Command.updateExamStatistics, studStats);
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
					displayAlert(AlertType.INFORMATION, "Success", "The exam " +UseExistingController.exid + " is now locked!",p.getExceptionMessage());
					return;

				} else {

					displayAlert(AlertType.ERROR, "Error", "The Code Not Change , Please Try Again Later",p.getExceptionMessage());
					return;
				}
			}
		});
		// sending the package
		send.start();
	
		
	}
	
	
	/**
	 * ableToAccess - changes able to access flag in the database so after 30 minutes of running exam new students can not enter.
	 */
	public void ableToAccess(){
		Packet packet = new Packet();
		ActiveExam updateAcess = new ActiveExam(UseExistingController.exid);
		packet.addCommand(Command.updateAccess);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(updateAcess);
		packet.setParametersForCommand(Command.updateAccess, examlist);

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
					displayAlert(AlertType.INFORMATION, "Success", "The exam " +UseExistingController.exid + " is not available to new students",p.getExceptionMessage());
					return;

				} else {

					displayAlert(AlertType.ERROR, "Error", "The Code Not Change , Please Try Again Later",p.getExceptionMessage());
					return;
				}
			}
		});
		// sending the package
		send.start();
	}
	
	
	/**
	 * inserts the exam id to the database reportexam to later use.
	 */
	public void reportExam()
	{
		String exid;
		Exam examId;
		exid = UseExistingController.exid;
		examId = new Exam(exid);
		Packet packet = new Packet();
		// adding question
		packet.addCommand(Command.addExamIdToReportExamTime);
		ArrayList<Object> examlist;
		examlist = new ArrayList<>();
		examlist.add(examId);
		packet.setParametersForCommand(Command.addExamIdToReportExamTime, examlist);

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

				if (p.getResultState())
				{
					displayAlert(AlertType.INFORMATION, "Success", "Successfully added.",p.getExceptionMessage());

	
			}
			}
		});
		// sending the package
		send.start();
		
		
		
		
		
	}


	/**
	 * Show an Alert dialog with custom info
	 * @param type type alert
	 * @param title title window
	 * @param header header of the message
	 * @param content message
	 */
	public static void displayAlert(AlertType type , String title , String header , String content)
	{
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
}


