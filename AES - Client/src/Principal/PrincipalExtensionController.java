package Principal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import Exam.Exam;
import Lecturer.Lecturer;
import Lecturer.lecturerMenuController;
import Login.LoginController;
import PacketSender.Command;
import PacketSender.IResultHandler;
import PacketSender.Packet;
import PacketSender.SystemSender;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
/**
 * this class responsible on show the extension menu of principal
 */
public class PrincipalExtensionController implements Initializable {
	private String approveNote, declineNote;
	public static Stage ExtenstionStage;
	/** Request list */
	private ArrayList<Exam> requestlist;
	/** Reason list */
	private ArrayList<String> reason;

	@FXML
	private ComboBox<Exam> examsCombo;

	@FXML
	private Button declineBtn;

	@FXML
	private Button approveBtn;

	@FXML
	private TextArea reasonField;

	@FXML
	private Button BackBtn6;

	@FXML
	private TextArea reasonTextArea;
	
	public void start() {
		ExtenstionStage = new Stage();

		String title = "Extension Time Approval";
		String srcFXML = "/Principal/Principal-extension-approval.fxml";

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			ExtenstionStage.setTitle(title);
			ExtenstionStage.setScene(scene);
			ExtenstionStage.setResizable(false);
			ExtenstionStage.show();

			ExtenstionStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
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
			PrincipalMenuController.hidefunc(ExtenstionStage);
			PrincipalMenuController.mainStage.show();
		} catch (Exception e) {
			ExtenstionStage.close();
			System.out.println(e);
			e.printStackTrace();
		}
	}
/**
 * case - if the principal decide to decline the lecturer request
 */
	public void decline() {
		// check if the principal field is filled by him
		if (reasonField.getText().isEmpty()) {
			displayAlert(AlertType.ERROR, "Error", "Please Fill your reason to Approve/Decline", null);
			return;
		}
		// check if the principal decide to approve or decline before
		if (examsCombo.getValue() == null) {
			displayAlert(AlertType.ERROR, "Error", "Please select from the list before  Approve/Decline", null);
			return;
		}
		declineNote = reasonField.getText();
		Exam dexam ,newexam;
		String exid;	
		dexam = examsCombo.getValue();
		exid = dexam.getExamId();
		newexam = new Exam(declineNote , exid );
		
		Packet packet = new Packet();
		packet.addCommand(Command.addDeclineAnswerFromPrincipal);
		ArrayList<Object> declinePrincipal;
		declinePrincipal= new ArrayList<>();
		declinePrincipal.add(newexam);
		packet.setParametersForCommand(Command.addDeclineAnswerFromPrincipal, declinePrincipal);
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
					
					displayAlert(AlertType.INFORMATION, "Success", "Your decision has been added successfully! ", null);							
					back();

				} else {

					displayAlert(AlertType.ERROR, "Error", "Error Exam Information , Please Try Again Later",
							p.getExceptionMessage());


				}
			}
		});
		// sending the package
		send.start();
		

	}

	
	/**
	 * case - if the principal decide to approve the lecturer request
	 */
	public void approve() {
		// check if the principal field is filled by him
				if (reasonField.getText().isEmpty()) {
					displayAlert(AlertType.ERROR, "Error", "Please Fill your reason to Approve/Decline", null);
					return;
				}
				// check if the principal decide to approve or decline before
				if (examsCombo.getValue() == null) {
					displayAlert(AlertType.ERROR, "Error", "Please select from the list before  Approve/Decline", null);
					return;
				}
				approveNote = reasonField.getText();
				Exam dexam ,newexam;
				String exid;	
				dexam = examsCombo.getValue();
				exid = dexam.getExamId();
				newexam = new Exam(approveNote , exid );
				
				Packet packet = new Packet();
				packet.addCommand(Command.addApproveAnswerFromPrincipal);
				ArrayList<Object> approvePrincipal;
				approvePrincipal= new ArrayList<>();
				approvePrincipal.add(newexam);
				packet.setParametersForCommand(Command.addApproveAnswerFromPrincipal, approvePrincipal);
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
							
							displayAlert(AlertType.INFORMATION, "Success", "Your decision has been added successfully! ", null);							
							back();

						} else {

							displayAlert(AlertType.ERROR, "Error", "Error Exam Information , Please Try Again Later",
									p.getExceptionMessage());

						}
					}
				});
				// sending the package
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
 * initialize the window
 */
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		// initialize the combo boxes
		initComboBoxLecturers();

	}

	/**
	 * initialize the combo box for all lecturers
	 */
	private void initComboBoxLecturers() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getALLlecturersRequests);

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
					ObservableList<Exam> observelistRequest;

					// filling the returned information from the server
					requestlist = p.<Exam>convertedResultListForCommand(Command.getALLlecturersRequests);

					// checking the list
					if (requestlist.isEmpty() == true) {
						displayAlert(AlertType.INFORMATION, "Empty request list", "There are no lecturer requests.", null);
						back();
						return;
					}

					// filling the information in the combobox
					observelistRequest = FXCollections.observableArrayList(requestlist);
					examsCombo.setItems(observelistRequest);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();
	}
/**
 * set on text field the lecturer reason for extra time
 */
	public void initReason() {
		Packet packet = new Packet();
		// adding command
		packet.addCommand(Command.getLecturerReason);

		ArrayList<Object> param = new ArrayList<>(Arrays.asList(examsCombo.getValue().getExamId()));

		packet.setParametersForCommand(Command.getLecturerReason, param);
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
					reason = p.<String>convertedResultListForCommand(Command.getLecturerReason);

					// checking the list
					if (reason.isEmpty() == true) {
						reasonTextArea.setText("-");
						return;
					}
					String str = String.format("%s", reason.get(0));
					reasonTextArea.setText(str);
				}

				else {
					displayAlert(AlertType.ERROR, "Error", "Cannot Continue with Validation", p.getExceptionMessage());
				}
			}
		});

		send.start();

	}
}