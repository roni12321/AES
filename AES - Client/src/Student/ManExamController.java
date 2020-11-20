package Student;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import Exam.Exam;
import Exam.FinishedExam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.FileSystem;
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
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class ManExamController implements Initializable  {
	
	public String ExCode;
	
	private static ArrayList<Exam> Examlist;
	
	@FXML
	private TextField execCode;
	
    @FXML
    private TextField pathFile;
    
    private String path;
    @FXML
    private Text TimeTXT;
    @FXML
    private Button subBtn;
    @FXML
    private Button uploadFile;
    @FXML
    private Text LockedLabel;
    @FXML
    private Button importFile;
    @FXML
    private Text TimeExtendTXT;
	
    public static Stage ManExam;
	public static String examId;
	private static Timer ServerCheck;
	private static Timer Timer;
	private static boolean Locked = false;
	private static boolean AddedTime = false;
	private static int timeflag = 0;
	private static int lockflag = 0;
	private static ArrayList<Integer> ExtraTime;
	private static ArrayList<Integer> TimeList = new ArrayList<Integer>();
	private static volatile int ExamTime;
	private static int SID; // Student ID
	private static ArrayList<String> ExamDonelist; // Has the exams that are already done by the user
	private static boolean Done=false; // If the exam was done by the student already
	private static long starttime;
	private static long finishtime;
	private static int CID;
	private static int Started=0;
	private static int Finished = 0;
	private static ArrayList<Integer> CourseList;




	/**
	 * start method to open a new stage
	 */

	public void start() {
		ManExam = new Stage();
		String title = "Manual Exam";
		String srcFXML = "/Student/Manual-Exam-Menu.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ManExam.setTitle(title);
			ManExam.setScene(scene);
			ManExam.setResizable(false);
			ManExam.show();

			ManExam.setOnCloseRequest(new EventHandler<WindowEvent>() {
		          public void handle(WindowEvent we) {

		        	  try {
		      			Exit();
		      		} catch (Exception e) {
		      			ManExam.close();
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
		if(Finished==1) Exit();
		if (Started==0) {
			studentMenuController.hidefunc(ManExam);
			studentMenuController.StudentMenu.show();
			return;
		}
		try {
			studentMenuController.hidefunc(ManExam);
			Timer.cancel();
			ServerCheck.cancel();
			studentMenuController.StudentMenu.show();
		} catch (Exception e) {
			ManExam.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * saveCode method - save the code for the Exam
	 */
	
	public void saveCode() {
		ExCode=execCode.getText();

	}
	
	
	/**
	 * close method - Lecturer locked the Exam or the Time is over
	 */
	
	
	public void close() {
		try {
			studentMenuController.hidefunc(ManExam);
			Timer.cancel();
			ServerCheck.cancel();
			studentMenuController.StudentMenu.show();
		} catch (Exception e) {
			ManExam.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Action on button Submit 
	 */
	
	public void Submit()
	{
		String excode;
		excode = execCode.getText();
		SID=((student) LoginController.userLogged).getsId();

		getExid(excode);
	}

	/**
	 *  the function get the question for the correct exam
	 * @param exid the exam id
	 */
	
	public void examQuestion(String exid)
	{
	
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getQuestionsByExID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exid));

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
					 ArrayList<Question> QueslistInExam;

					
					// filling the returned information from the server
					 QueslistInExam = p.<Question>convertedResultListForCommand(Command.getQuestionsByExID);
					// checking the list
					if (QueslistInExam.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No questions in this exam");
						return;
					}
					
					createWord(QueslistInExam , exid);
				}
			
			}
		});

		send.start();
	}

		
	/**
	 *  the function take the exam code and return the exam id
	 * @param excode - the function get exam code for the Exam 
	 */
	
	public void getExid(String excode)
	{
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamByExCode);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(excode));

		packet.setParametersForCommand(Command.getExamByExCode, param);
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

					// filling the returned information from the server
					Examlist = p.<Exam>convertedResultListForCommand(Command.getExamByExCode);
					// checking the list
					
					if (Examlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No active exams with that code");
						return;
					}
					
					
					String exid = Examlist.get(0).getExamId();
					  checkExamDone(exid);
			
				}
			}
		});

		send.start();
	}
		
	/**
	 *  the function check if the student finished the exam 
	 * @param exid - the exam id 
	 */
	
	private void checkExamDone(String exid) {
		// TODO Auto-generated method stub
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamByExamIdAndSidManual);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(exid,SID));

		packet.setParametersForCommand(Command.getExamByExamIdAndSidManual, param);
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

					// filling the returned information from the server
					ExamDonelist = p.<String>convertedResultListForCommand(Command.getExamByExamIdAndSidManual);
					if (ExamDonelist.isEmpty()== false)
					{
						
						displayAlert(AlertType.ERROR, "Error", " Cannot do the same exam twice",null);
						return;
						
				}
					
				else {
					checkActiveExam(exid);		
					}
				}
			}
		});

		send.start();
	}
	
	/**
	 * the function check if the exam is active
	 * @param exid - the Exam id
	 */
		
	private void checkActiveExam(String exid) {
		
			Packet packet = new Packet();
			// adding command
			packet.addCommand(Command.checkLockedExam);

			ArrayList<Object> param = new ArrayList<>(Arrays.asList(exid));

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
						ArrayList<Integer> lockedexam;
						// filling the returned information from the server
						lockedexam = p.<Integer>convertedResultListForCommand(Command.checkLockedExam);
						// checking the list
						if (lockedexam.isEmpty() == true) {
							displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());

							return;
						}
						// check if the student has the same department code as the exam//
						
						if(lockedexam.get(0) == 0)
						{
							displayAlert(AlertType.ERROR, "Error", "The Exam not Active", null);

						}
						
						examId = exid;
						examQuestion(exid);
						
						
					}

				}
			});

			send.start();
		}
		
	
	/**
	 * the function create the new file word 
	 * @param queslistInExam - the question for this EXam 
	 * @param exid - the exam id
	 */

	public void createWord(ArrayList<Question> queslistInExam, String exid)
	{
		
		
		try {	
			
			File statText = new File( String.format("c://Downloaded_Exams//%s.doc" ,exid));
			
		    FileOutputStream is = new FileOutputStream(statText);
		    OutputStreamWriter osw = new OutputStreamWriter(is);
		    Writer w = new BufferedWriter(osw);
		   
		    String Questext;
		    String ans1,ans2,ans3,ans4;
		    String posans[];
		    
			for (int i = 0; i < queslistInExam.size(); i++)
			{
				Questext = queslistInExam.get(i).getQues();
				posans = queslistInExam.get(i).getpos_ans();
				ans1 = posans[0];
				ans2 = posans[1];
				ans3 = posans[2];
				ans4 = posans[3];
				 w.write(String.format("The Question is: %s ", Questext));
				((BufferedWriter) w).newLine();
				w.write(ans1);
				((BufferedWriter) w).newLine();
				w.write(ans2);
				((BufferedWriter) w).newLine();
				w.write(ans3);
				((BufferedWriter) w).newLine();
				w.write(ans4);
				((BufferedWriter) w).newLine();
				((BufferedWriter) w).newLine();
		    }

			  w.close();	
			  importFile.setDisable(false);
			  uploadFile.setDisable(false);
			  Started = 1;
			displayAlert(AlertType.INFORMATION, "Success", "The menual exam in c://Downloaded_Exams",null);
			getCID();
			timeStart();
			subBtn.setVisible(false);
				
			 
		}
		
		catch (IOException e) {
			displayAlert(AlertType.ERROR, "Error", "you need open a new folder c://Downloaded_Exams ", null);
        }
		
		
	}
	
	/**
	 * the function start the time for the exam
	 */
	public void timeStart()
	{
		starttime = System.currentTimeMillis();

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExamTime);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId));

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
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "No examtime configured",null );
						return;
					}
			
					Timer = new Timer();
					ExamTime = TimeList.get(0);
					String str = String.format("Exam time: %d minutes left", ExamTime);
					TimeTXT.setText(str);
					TimeTXT.setVisible(true);
					Timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							Platform.runLater(new Runnable() {
							       public void run() {
							    	   ExamTime--;
							    	   String time = String.format("Exam time: %d minutes left", ExamTime);
							    	   TimeTXT.setText(time);
										if (ExamTime == 0) {
											
											  uploadFile.setDisable(true);
											  importFile.setDisable(true);
											  UploadFile();
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
									TimeTXT.setText(String.format("Exam time: %d minutes left", ExamTime));
									timeflag = 1;
								}
							}
							if (Locked == false) {
								checkLock();
							}
							if (Locked == true) {
								if (lockflag == 0) {
									lockflag = 1;
									 uploadFile.setDisable(true);
									 importFile.setDisable(true);
									 LockedLabel.setVisible(true);
									 UploadFile();
									 
								}
							}
						}
					});
					}
					}, 1000, 1000);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
		
		/**
		 * the function that performs an exit after the student started the exam
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
					studentMenuController.hidefunc(ManExam);
					studentMenuController.StudentMenu.show();
					UploadFile();
				} catch (Exception e) {
					ManExam.close();
					System.out.println(e);
					e.printStackTrace();
				}

			}
		});

	}


	/**
	 * the function for the button upload file
	 */
	public void saveFileOnServer()
	{
		pathFile.getText();
		if(pathFile.getText().equals(""))
		
		{
			displayAlert(AlertType.ERROR, "Error", "need to select file", null);
			return;

		}
		
		Finished = 1;
		UploadFile();
		student std = (student)LoginController.userLogged;
	
		
		FileSystem file = new FileSystem(path, std.getsId(), examId);
		
		ArrayList<Object> fileparams = new ArrayList<>();
		fileparams.add(file);
		
		
		Packet packet = new Packet();
		packet.addCommand(Command.uploadFile);

		packet.setParametersForCommand(Command.uploadFile, fileparams);

		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {
			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {
					displayAlert(AlertType.INFORMATION, "Success", "file was uploaded successfull", null);
					back();
				}
				else {
					displayAlert(AlertType.ERROR, "Error", "Failed To upload this file", null);
					
				}
			}
		});
		send.start();
	}
		
	
	
	/**
	 * Handler event on pressing the 'Import Image' Button
	 */
	public void pressedImportFileButton() {
		FileChooser fileChooser = new FileChooser();

		// Set extension filter
		FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("doc files (*.doc)", "*.DOC");
		FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("docx files (*.docx)", "*.DOCX");
		fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

		File file = fileChooser.showOpenDialog(ManExam);

		if (file != null) {
			
			// set path to global
			 this.path = file.getPath();
			
			// set path to textfield
			 pathFile.setText(file.getPath());
			
		}
	}
	
	
	
	
	/**
	 * Submit the Exam stats to the DB
	 */
	public void UploadFile() {
		Packet packet = new Packet();
		// adding command

		finishtime = System.currentTimeMillis() - starttime;
		finishtime = (finishtime / 1000);
		FinishedExam newfinished = new FinishedExam(SID, CID, examId, Started, Finished,Long.toString(finishtime));

		packet.addCommand(Command.AddFinishedExamManual);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(newfinished));

		packet.setParametersForCommand(Command.AddFinishedExamManual, param);
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
					
					SetUidAndCname();
					if(lockflag==1) displayAlert(AlertType.INFORMATION, "Locked Exam", "The exam was locked by the lecturer", p.getExceptionMessage());
					close();
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Couldn't upload the exam", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
	
	
	
	
	
	
	
	/**
	 * A method that sets the User ID and course name of the finished exam for further use
	 */
	public void SetUidAndCname() {
		Packet packet = new Packet();
		// adding question

		packet.addCommand(Command.getLecturerUidAndCourseNamebyExamID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId));

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
	 * the function get the course id by the exam id
	 */
	public void getCID() {

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getCourseIDbyExamID);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId));

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
	 * the function check if the lecturer gave extra time for the exam
	 */
	private void CheckExtraTime() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getExtraTime);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId));

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
	 * 	 * the function check if the lecturer locked  the exam

	 */
	public void checkLock() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.checkLockedExam);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examId));

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
					ArrayList<Integer> lockedexam;
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
	 * initialize the window
	 */
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		TimeExtendTXT.setVisible(false);
		TimeTXT.setVisible(false);
		LockedLabel.setVisible(false);
		 uploadFile.setDisable(true);
		 importFile.setDisable(true);
	
	}
	

	}
	
	
	

