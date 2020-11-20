package Principal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Answer.Answer;
import Exam.Exam;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;
import Student.student;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

/**
 * this class responsible on student histogram window
 */
public class PrincipalStudentHistogramController implements Initializable {
	/** grades list */
	private ArrayList<Exam> avglist;
	private ArrayList<Exam> medianlist;
	private ArrayList<Exam> examGradeList;

	/**
	 * current stage
	 */
	public static Stage studentReportStage;
	/**
	 * Contains login details
	 */
	private static LoginController loginController;

	@FXML
	private TextField studentHisto;

	@FXML
	private Button BackBtn11;

	@FXML
	private TextField averageField;

	@FXML
	private TextField medianField;

	@FXML
	private BarChart<String, Integer> studentGraph;

	public void start() throws Exception {
		studentReportStage = new Stage();

		String title = "Student Histogram";
		String srcFXML = "/Principal/principal-student-histogram.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			studentReportStage.setTitle(title);
			studentReportStage.setScene(scene);
			studentReportStage.setResizable(false);
			studentReportStage.show();

			studentReportStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
			PrincipalMenuController.hidefunc(studentReportStage);
			GetReportsController.reportPrin.show();
		} catch (Exception e) {
			studentReportStage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	public void setLoginController(LoginController login) {
		loginController = login;
	}

	/**
	 * function to get the next window and close the current window
	 */
	public void GetReports() {
		studentReportStage.close();
		GetReportsController getReports = new GetReportsController();
		getReports.start();
	}

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		// set the student name
		initNameAndAvg();
		initMedian();
		getData();

	}

	/**
	 * initialize average and the name of student
	 */
	private void initNameAndAvg() {
		studentHisto.setText(GetReportsController.principalComboChoice.getUsername()); // set the students name on
		int sId = GetReportsController.principalComboChoice.getuId();
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getStudentAvg);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(sId));

		packet.setParametersForCommand(Command.getStudentAvg, param);
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
					avglist = p.<Exam>convertedResultListForCommand(Command.getStudentAvg);
					// checking the list
					if (avglist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "", "No grades in this exams");
						return;
					}

					String studentAvg;
					studentAvg = avglist.get(0).getExamId();
					averageField.setText(studentAvg);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}

	/**
	 * init the median of student and set it on text field
	 */
	private void initMedian() {
		int sId = GetReportsController.principalComboChoice.getuId();
		// get by query and set the average grade for current student

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getStudentMedian);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(sId));

		packet.setParametersForCommand(Command.getStudentMedian, param);
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
					medianlist = p.<Exam>convertedResultListForCommand(Command.getStudentMedian);
					// checking the list
					if (medianlist.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the student should have at least one exam");
						return;
					}

					String studentMedian;
					studentMedian = medianlist.get(0).getExamId();
					medianField.setText(studentMedian);

				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}

	/**
	 * get the information of students exams
	 */
	private void getData() {

		int sId = GetReportsController.principalComboChoice.getuId();

		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getDataStudentBySid);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(sId));

		packet.setParametersForCommand(Command.getDataStudentBySid, param);
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
					examGradeList = p.<Exam>convertedResultListForCommand(Command.getDataStudentBySid);
					// checking the list
					if (examGradeList.isEmpty() == true) {
						displayAlert(AlertType.ERROR, "Error , Please Try Again", "",
								"For get median the student should have at least one exam");
						return;
					}

					BuildBarChartForComplain(studentGraph, examGradeList);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "", p.getExceptionMessage());
				}
			}

		});

		send.start();

	}

	/**
	 * build the histogram graph of student grades
	 * 
	 * @param studentGraph
	 * @param examGradeList
	 */
	@SuppressWarnings("unchecked")
	private void BuildBarChartForComplain(BarChart<String, Integer> studentGraph, ArrayList<Exam> examGradeList) {
		// TODO Auto-generated method stub

		// building the bars and adding it to the chart
		int grade;
		studentGraph.setVisible(true);
		studentGraph.getData().clear();
		ArrayList<XYChart.Series<String, Integer>> serlist = new ArrayList<>();
		for (int i = 0; i < examGradeList.size(); i++) {
			serlist.add(new XYChart.Series<>());
			try {
				grade = examGradeList.get(i).getDepartmentId();
			} catch (Exception e) {
				return;
			}
			serlist.get(i).getData().add(new XYChart.Data<>(examGradeList.get(i).getExamId(), grade));
			serlist.get(i).setName(examGradeList.get(i).getExamId());
			// adding the bars to the chart
			studentGraph.getData().add(serlist.get(i));
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