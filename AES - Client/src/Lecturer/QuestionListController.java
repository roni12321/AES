package Lecturer;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Question.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class QuestionListController implements Initializable {

	@FXML
	private TableColumn<Question, String> questionIdColumm = new TableColumn<>();

	@FXML
	private TableView<Question> QuestionTable;

	@FXML
	private TableColumn<Question, String> questionTextColumn = new TableColumn<>();

	@FXML
	private TableColumn<Question, String> questionAuthorColumn = new TableColumn<>();
	@FXML
	private Button addButton;

	/** Question list */
	public static ArrayList<Question> questionAddList = new ArrayList<Question>();

	public static Stage questionliststage;


	public void start() {
		questionliststage = new Stage();

		String title = "Question List";
		String srcFXML = "/Lecturer/Questionlist.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			questionliststage.setTitle(title);
			questionliststage.setScene(scene);
			questionliststage.setResizable(false);
			questionliststage.show();

			questionliststage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {

					try {
						BuildExamController.hidefunc(questionliststage);
						BuildExamController.buildexamstage.show();
					} catch (Exception e) {
						questionliststage.close();
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
	 * initialize the window
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		// initialize the table
		QuestionTable.setItems(initTable());
		questionIdColumm.setCellValueFactory(new PropertyValueFactory<Question, String>("Id"));
		questionTextColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("Ques"));
		questionAuthorColumn.setCellValueFactory(new PropertyValueFactory<Question, String>("Author"));

	}

	/**
	 * add the data to table view
	 * 
	 * @return - list
	 */
	public ObservableList<Question> initTable() {
		ObservableList<Question> observelistQuestion;
		observelistQuestion = FXCollections.observableArrayList(BuildExamController.questionlist);
		return observelistQuestion;

	}

	/**
	 * add - case (if the user pressed on add button)
	 */
	public void addAction() {

		Question addQuestion;

		addQuestion = QuestionTable.getSelectionModel().getSelectedItem();
		if (addQuestion == null) {
			displayAlert(AlertType.ERROR, "Error", "not select question", null);
			return;

		}
		
		if(!questionAddList.contains(addQuestion))
		{
	
			questionAddList.add(addQuestion);
			BuildExamController.hidefunc(questionliststage);
			BuildExamController.buildexamstage.show();
			BuildExamController.exeCon.initComboBoxQuestion();

		}
		
		else displayAlert(AlertType.ERROR, "Error", "the question is allreay exists", null);

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
	 * action of button back
	 */
	public void back() {

		try {
			BuildExamController.hidefunc(questionliststage);
			BuildExamController.buildexamstage.show();
		} catch (Exception e) {
			questionliststage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}

}
