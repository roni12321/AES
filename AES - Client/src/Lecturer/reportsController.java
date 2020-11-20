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
import Principal.GetReportsController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class reportsController implements Initializable  {
public static Stage reportstage;


@FXML
private TextField LecMedian;

@FXML
private TextField LecAverage;

@FXML
private BarChart<String, Integer> lecturerGraph;


@FXML
private Button BackBtn9;

private ArrayList<Exam> avglist;
private ArrayList<Exam> medianlist;
		

/**
 * start - start the new stage
 * 
 */


	public void start() {
		reportstage = new Stage();

		String title = "Reports";
		String srcFXML = "/Lecturer/Lecturer-Report.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			reportstage.setTitle(title);
			reportstage.setScene(scene);
			reportstage.setResizable(false);
			reportstage.show();

			reportstage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				 public void handle(WindowEvent we) {

						try {
							lecturerMenuController.hidefunc(reportstage);
							lecturerMenuController.lecturermenu.show();
						} catch (Exception e) {
							reportstage.close();
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
			lecturerMenuController.hidefunc(reportstage);
			lecturerMenuController.lecturermenu.show();
		} catch (Exception e) {
			reportstage.close();
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
		initMedian();
		initNameAndAvg();
		getLid();
	}


	/**
	 * initialize the text field for this lecturer
	 */
	private void initNameAndAvg() {
		
		int lId = LoginController.userLogged.getuId();
		// get by query and set the average grade for current course

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAvgOfLecturer);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(lId));

		packet.setParametersForCommand(Command.getAvgOfLecturer, param);
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
					avglist = p.<Exam>convertedResultListForCommand(Command.getAvgOfLecturer);
					// checking the list
					if (avglist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No grades in this exams");
						return;
					}

					String studentAvg;
					studentAvg = avglist.get(0).getExamId();
					LecAverage.setText(studentAvg);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
		
		/**
		 * init and set the median value of this lecturer
		 */
	}

	private void initMedian() {
		int lId = LoginController.userLogged.getuId();
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getMedianOfLecturer);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(lId));

		packet.setParametersForCommand(Command.getMedianOfLecturer, param);
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
					medianlist = p.<Exam>convertedResultListForCommand(Command.getMedianOfLecturer);
					// checking the list
					if (medianlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the lecturer should have at least one exam");
						return;
					}

					String lecturerMedian;
					lecturerMedian = medianlist.get(0).getExamId();
					LecMedian.setText(lecturerMedian);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}
		
	
	
	/**
	 * get the lecturer ID by query that sending user ID and get lecturer ID
	 */
	private void getLid() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getlecturerByUid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(LoginController.userLogged.getuId()));

		packet.setParametersForCommand(Command.getlecturerByUid, param);
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

					ArrayList<Lecturer> lecturerList;

					// filling the returned information from the server
					lecturerList = p.<Lecturer>convertedResultListForCommand(Command.getlecturerByUid);

					// checking the list
					if (lecturerList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the course should have at least one exam");
						return;
					}

					Lecturer lecturer = lecturerList.get(0);
					int lid = lecturer.getlId();

					getData(lid);

				}

			}
		});

		send.start();

	}


	/**
	 * get by query and set the count of  students
	 * 
	 * @param lid
	 */
	
	private void getData(int lid) {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getGradeLecturerBylId);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(lid));

		packet.setParametersForCommand(Command.getGradeLecturerBylId, param);
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

					ArrayList<Course> countList;

					// filling the returned information from the server
					countList = p.<Course>convertedResultListForCommand(Command.getGradeLecturerBylId);

					// checking the list
					if (countList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the course should have at least one exam");
						return;
					}

					int courseReport[] = new int[10];

					courseReport = countList.get(0).getCourseReport();
					intData(lecturerGraph, courseReport);

				}

			}
		});

		send.start();

	}

	/**
	 * init the data of lecturer students on the graph
	 * 
	 * @param lecturerGraphLec
	 * @param courseReport
	 */
	private void intData(BarChart<String, Integer> lecturerGraphLec, int[] courseReport) {

		// TODO Auto-generated method stub
		lecturerGraphLec.setVisible(true);
		lecturerGraphLec.getData().clear();

		ArrayList<XYChart.Series<String, Integer>> serlist = new ArrayList<>();

		for (int i = 0; i < 10; i++) {

			serlist.add(new XYChart.Series<>());

			serlist.get(i).getData().add(new XYChart.Data<>("", courseReport[i]));
			serlist.get(i).setName(String.format("(%d0) - (%d0)", i, i + 1));
			// adding the bars to the chart
			lecturerGraphLec.getData().add(serlist.get(i));
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




