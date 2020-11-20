package Principal;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Answer.Answer;
import Course.Course;
import Exam.Exam;
import Lecturer.Lecturer;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;
import Student.student;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

/**
 * this class responsible on the lecturer histogram window
 */
public class PrincipalLecturerHistogramController implements Initializable {

	/**
	 * current stage
	 */
	public static Stage lecturerReportStage;
	private ArrayList<Exam> avglist;
	private ArrayList<Exam> medianlist;

	@FXML
	private TextField LecMedian;

	@FXML
	private TextField LecAverage;

	@FXML
	private Button BackBtn9;

	@FXML
	private TextField lectName;
	@FXML
	private BarChart<String, Integer> lecturerGraph;

	/**
	 * start - start the new stage
	 * 
	 * @throws Exception - throw
	 */
	public void start() throws Exception {
		lecturerReportStage = new Stage();

		String title = "Student Histogram";
		String srcFXML = "/Principal/principal-lecturer-histogram.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			lecturerReportStage.setTitle(title);
			lecturerReportStage.setScene(scene);
			lecturerReportStage.setResizable(false);
			lecturerReportStage.show();

			lecturerReportStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					back();
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
			PrincipalMenuController.hidefunc(lecturerReportStage);
			GetReportsController.reportPrin.show();
		} catch (Exception e) {
			lecturerReportStage.close();
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
		initNameAndAvg();
		initMedian();
		getLid();

	}

	/**
	 * initialize the text field for all lecturers
	 */
	private void initNameAndAvg() {
		// principalComboChoiceforLecturer
		lectName.setText(GetReportsController.principalComboChoiceforLecturer.getUsername());
		int lId = GetReportsController.principalComboChoiceforLecturer.getuId();
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
		 * init and set the median value of current lecturer
		 */
	}

	private void initMedian() {
		int authorId = GetReportsController.principalComboChoiceforLecturer.getuId();
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getMedianOfLecturer);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(authorId));

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

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(GetReportsController.uid));

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
	 * @param lecturerGraph
	 * @param courseReport
	 */
	private void intData(BarChart<String, Integer> lecturerGraph, int[] courseReport) {

		// TODO Auto-generated method stub
		lecturerGraph.setVisible(true);
		lecturerGraph.getData().clear();

		ArrayList<XYChart.Series<String, Integer>> serlist = new ArrayList<>();

		for (int i = 0; i < 10; i++) {

			serlist.add(new XYChart.Series<>());

			serlist.get(i).getData().add(new XYChart.Data<>("", courseReport[i]));
			serlist.get(i).setName(String.format("(%d0) - (%d0)", i, i + 1));
			// adding the bars to the chart
			lecturerGraph.getData().add(serlist.get(i));
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
