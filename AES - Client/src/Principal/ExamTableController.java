package Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Exam.Exam;
import Exam.ExamController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import Question.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ExamTableController implements Initializable {

	public static Stage examStage;
	private ArrayList<Exam> examslist;
	public static String showExamId;

	@FXML
	private TableColumn<Exam, String> ExamAuthor;

	@FXML
	private TableColumn<Exam, String> ExamId;
	@FXML
	private TableView<Exam> ExamTable;

	public void start() throws Exception {
		examStage = new Stage();

		String title = "Exam Table";
		String srcFXML = "/Principal/Principal-exam-table.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			examStage.setTitle(title);
			examStage.setScene(scene);
			examStage.setResizable(false);
			examStage.show();

			examStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
			ExamTableController.hidefunc(examStage);
			PrincipalMenuController.mainStage.show();
		} catch (Exception e) {
			examStage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

	/**
	 * function that hide the Stages that sent to her
	 * 
	 * @param s - s for stage
	 */
	public static void hidefunc(Stage s) {
		s.hide();
		return;
	}

	/**
	 * initialize the window
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		// set the student name

		getData();

	}

	/**
	 * get the all exams from DB to principal watch
	 */
	public void getData() {
		Packet packet = new Packet();
		/**
		 * adding command
		 */
		packet.addCommand(Command.getExams);

		/**
		 * sending the packet
		 */
		SystemSender send = new SystemSender(packet);
		send.registerHandler(new IResultHandler() {

			@Override
			public void onWaitingForResult() {

			}

			@Override
			public void onReceivingResult(Packet p) {
				if (p.getResultState()) {

					ArrayList<Exam> listExam;
					/**
					 * filling the information in the combobox
					 */
					listExam = p.<Exam>convertedResultListForCommand(Command.getExams);
					examslist = listExam;
					ExamTable.setItems(initTable(examslist));
					ExamAuthor.setCellValueFactory(new PropertyValueFactory<Exam, String>("Author"));
					ExamId.setCellValueFactory(new PropertyValueFactory<Exam, String>("ExamId"));

				}
			}
		});

		send.start();

	}

	/**
	 * add the data to table view
	 * 
	 * @param examslist - list
	 * 
	 * @return - ObservableList
	 */
	public ObservableList<Exam> initTable(ArrayList<Exam> examslist) {
		ObservableList<Exam> observelistExam;
		observelistExam = FXCollections.observableArrayList(examslist);
		return observelistExam;

	}

	/**
	 * show the exam when the principal pressing on Show button
	 */
	public void showExam() {
		Exam showExam;

		showExam = ExamTable.getSelectionModel().getSelectedItem();
		if (showExam == null) {
			displayAlert(AlertType.ERROR, "Error", "not select exam", null);
			return;

		}

		showExamId = showExam.getExamId();
		examStage.hide();
		ShowExamController exViewEx = new ShowExamController();
		exViewEx.start();

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
