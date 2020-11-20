package Principal;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Exam.Exam;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * this class responsible on the screen of question table
 */
public class QuestionTableController implements Initializable {
	
	public static Stage questionStage;
    @FXML
    private TableColumn<Question, String> QuestionId;

    @FXML
    private Button backBtn;

    @FXML
    private TableView<Question> questionTable;

    @FXML
    private TableColumn<Question, String> QuestionText;

    @FXML
    private TableColumn<Question, String> QuestionAuthor;
    
    private ArrayList<Question> questionlist;

	/**
	 * start the stage of question table for principal 
	 * @throws Exception - exception
	 */
	public void start() throws Exception {
		questionStage = new Stage();

		String title = "Question Table";
		String srcFXML = "/Principal/Principal-question-table.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			questionStage.setTitle(title);
			questionStage.setScene(scene);
			questionStage.setResizable(false);
			questionStage.show();

			questionStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
			QuestionTableController.hidefunc(questionStage);
			PrincipalMenuController.mainStage.show();
		} catch (Exception e) {
			questionStage.close();
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
	 * get all the exam from the DB
	 */

	private void getData() {

		Packet packet = new Packet();
		/**
		 * adding command
		 */
		packet.addCommand(Command.getAllQuestions);

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

					ArrayList<Question> listQuestion;
					/**
					 * filling the information in the combobox
					 */
					listQuestion = p.<Question>convertedResultListForCommand(Command.getAllQuestions);
					questionlist = listQuestion;
					questionTable.setItems(initTable(questionlist));
					QuestionText.setCellValueFactory(new PropertyValueFactory<Question, String>("Ques"));
					QuestionId.setCellValueFactory(new PropertyValueFactory<Question, String>("Id"));
					QuestionAuthor.setCellValueFactory(new PropertyValueFactory<Question, String>("Author"));
				}
			}
		});

		send.start();
	}
	
	
/**
 * 
 * @param questionlist - question list
 * @return - list
 */
	
	public ObservableList<Question> initTable(ArrayList<Question> questionlist ) {
		ObservableList<Question> observelistQuestion;
		observelistQuestion = FXCollections.observableArrayList(questionlist);
		return observelistQuestion;

	}
	
	
	

}
