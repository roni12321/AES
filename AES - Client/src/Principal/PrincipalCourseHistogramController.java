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
import Exam.FinishedExam;
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
 * this class responsible on the average,median,histogram graph of specific
 * course
 */
public class PrincipalCourseHistogramController implements Initializable {
	/**
	 * current stage
	 */
	public static Stage courseReportStage;
	private ArrayList<Exam> avglist;
	private ArrayList<Exam> medianlist;

	@FXML
	private TextField courseName;

	@FXML
	private TextField courseAvg;

	@FXML
	private Button BackBtn6;

	@FXML
	private TextField courseMdian;

	@FXML
	private BarChart<String, Integer> graph;

	public void start() throws Exception {
		courseReportStage = new Stage();

		String title = "Course Histogram";
		String srcFXML = "/Principal/Principal-Course-Histogram.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			courseReportStage.setTitle(title);
			courseReportStage.setScene(scene);
			courseReportStage.setResizable(false);
			courseReportStage.show();

			courseReportStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
			PrincipalMenuController.hidefunc(courseReportStage);
			GetReportsController.reportPrin.show();
		} catch (Exception e) {
			courseReportStage.close();
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
		getData();

	}

	/**
	 * this function show the name on the stage and send to the query to get the
	 * average of specific student
	 */
	private void initNameAndAvg() {
		courseName.setText(GetReportsController.principalComboChoiceForCourse.getcName()); // set the name of course
		String cName = GetReportsController.principalComboChoiceForCourse.getcName();
		// get by query and set the average grade for current course

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getAvgbyCourseName);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(cName));

		packet.setParametersForCommand(Command.getAvgbyCourseName, param);
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
					avglist = p.<Exam>convertedResultListForCommand(Command.getAvgbyCourseName);
					// checking the list
					if (avglist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No grades in this exams");
						return;
					}

					String studentAvg;
					studentAvg = avglist.get(0).getExamId();
					courseAvg.setText(studentAvg);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}

	/**
	 * this function send query and init the specific course median
	 */
	private void initMedian() {
		String cName = GetReportsController.principalComboChoiceForCourse.getcName();
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getMedianbyCourseName);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(cName));

		packet.setParametersForCommand(Command.getMedianbyCourseName, param);
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
					medianlist = p.<Exam>convertedResultListForCommand(Command.getMedianbyCourseName);
					// checking the list
					if (medianlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the course should have at least one exam");
						return;
					}

					String courseMedian;
					courseMedian = medianlist.get(0).getExamId();
					courseMdian.setText(courseMedian);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}

	/**
	 * this function get the dana (grades in course) and set the grades on graph
	 */
	private void getData() {
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getGradeCourseByCId);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(GetReportsController.cid));

		packet.setParametersForCommand(Command.getGradeCourseByCId, param);
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
					countList = p.<Course>convertedResultListForCommand(Command.getGradeCourseByCId);
					
					// checking the list
					if (countList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the course should have at least one exam");
						return;
					}

				int courseReport[] = new int[10];
				
				courseReport = 	countList.get(0).getCourseReport();
					intData(graph, courseReport);

				}

			}
		});

		send.start();

	}

	/**
	 * init the information on the graph
	 * 
	 * @param graph
	 * @param courseReport
	 */
	private void intData(BarChart<String, Integer> graph, int[] courseReport) {

		// TODO Auto-generated method stub
		graph.setVisible(true);
		graph.getData().clear();

		ArrayList<XYChart.Series<String, Integer>> serlist = new ArrayList<>();

		for (int i = 0; i < 10; i++) {

			serlist.add(new XYChart.Series<>());

			serlist.get(i).getData().add(new XYChart.Data<>("", courseReport[i]));
			serlist.get(i).setName(String.format("(%d0) - (%d0)", i, i + 1));
			// adding the bars to the chart
			graph.getData().add(serlist.get(i));
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
