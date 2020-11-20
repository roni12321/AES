package Server;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import javax.swing.JOptionPane;

import ActiveExam.ActiveExam;
import Cheat.Cheat;
import Course.Course;
import Exam.Exam;
import Exam.FinishedExam;
import Lecturer.Lecturer;
import Lecturer.QuestionInExam;
import Logic.DbGetter;
import Logic.DbQuery;
import Logic.DbUpdater;
import Logic.ISelect;
import Logic.IUpdate;
import PacketSender.Command;
import PacketSender.FileSystem;
import PacketSender.Packet;
import Question.FinishedQuestion;
import Question.Question;
import Student.student;
import Users.Permission;
import Users.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class contain GUI and buttons behavior on the server side Available to
 * set up connection to the database and selecting port to listen Also contains
 * main function to get command from client and send it to relevant function in
 * the class
 */

public class SystemServer extends AbstractServer implements Initializable {

	public SystemServer(int port) {
		super(port);
	}

	/**
	 * default user
	 */
	private String user = "root";
	/**
	 * default password
	 */
	private String password = "123123";
	private String database;
	private Timer timer = new Timer();
	/**
	 * default port
	 */
	private static final int DEFAULT_PORT = 5555;
	private DbQuery dbConnection;

	@FXML
	private Text IPTXT;
	@FXML
	private MenuItem btnDelete;
	@FXML
	private TextField txtPort;
	/**
	 * Run/Stop server
	 */
	@FXML
	private Button btnSubmit;
	@FXML
	private TextArea txtLog;
	@FXML
	private TextField txtDb;
	@FXML
	private TextField txtUser;
	@FXML
	private PasswordField txtPass;
	/**
	 * clear log textField
	 */
	@FXML
	private Button btnClear;
	@FXML
	private Pane paneDetails;
	int port = 0; // Port to listen on

	public SystemServer() {
		super(DEFAULT_PORT);
	}

	/**
	 * print message to textField with date and time
	 * 
	 * @param msg
	 *            message to write to the log
	 */
	public void printlogMsg(String msg) {
		String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());// get datetime for log print
		txtLog.setText(time + "---" + msg + "\n\r" + txtLog.getText());
	}

	/**
	 * if button pressed check the function check if server already listen to port
	 * if yes then stop to listen otherwise start listen and update button text
	 * 
	 * @param event
	 *            actual event
	 */
	public void onSubmitClicked(ActionEvent event) {
		if (!isListening())// check if not listen
		{
			try {
				port = Integer.parseInt(txtPort.getText()); // Get port from command line
				this.setPort(port);
			} catch (Throwable t) {// if port is wrong or listening already
				printlogMsg("ERROR - Could not listen for clients from this port! Using default port");
				this.setPort(DEFAULT_PORT);
				paneDetails.setDisable(false);
				return;
			}
		}
		if (changeListening(txtDb.getText(), txtUser.getText(), txtPass.getText()))// check if switch listening is
																					// complete
		{
			if (btnSubmit.getText().equals("Start service")) {// if it wasn't listening
				database = txtDb.getText();
				user = txtUser.getText();
				password = txtPass.getText();
				printlogMsg("Server has started listening on port:" + port);// write to log
				dbConnection = new DbQuery(user, password, database);
				paneDetails.setDisable(true);
				btnSubmit.setText("Stop service");// update button

			} else// if it was listen
			{
				printlogMsg("Server has finished listening on port:" + port);
				paneDetails.setDisable(false);
				btnSubmit.setText("Start service");/// update button
			}
		}
	}

	/**
	 * printing error from scheduling
	 * 
	 * @param msgError
	 *            error message from scheduling
	 */
	public void logErrorSchedule(String msgError) {
		printlogMsg("Failed: " + msgError);
	}

	/***
	 * clear log text area
	 * 
	 * @param event
	 *            actual event
	 */
	public void onClearClicked(ActionEvent event) {
		txtLog.clear();
	}

	/**
	 * Show the scene view of the server *
	 * 
	 * @param arg0
	 *            current stage to build
	 * @throws Exception
	 *             if failed to display
	 */
	public void start(Stage arg0) throws Exception {

		String title = "Server";
		String srcFXML = "/Server/App.fxml";
		String srcCSS = "/Server/application.css";
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(srcFXML));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource(srcCSS).toExternalForm());
			arg0.setTitle(title);
			arg0.setScene(scene);
			arg0.show();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}

		arg0.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				// cancel the scheduling
				timer.cancel();

				System.exit(0);
			}
		});
	}

	/**
	 * turn on/off listening print the result to the log
	 * 
	 * @param database
	 *            to check the connection before starting to listening
	 * @param user
	 *            user to connect to the database
	 * @param password
	 *            password to connect to the database
	 * @return true if success else false
	 */
	public boolean changeListening(String database, String user, String password) {
		if (!isListening())// if start service has been pressed
		{
			if (database.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please Fill DataBase name", "Error", JOptionPane.ERROR_MESSAGE);
				printlogMsg("database name missing\n\r");
				return false;
			}
			if (user.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please Fill user name", "Error", JOptionPane.ERROR_MESSAGE);
				printlogMsg("user name missing");
				return false;
			}
			try {
				DbQuery db = new DbQuery(user, password, database);// check connection to database
				db.connectToDB();
				db.connectionClose();
				listen(); // Start listening for connections
			} catch (Exception e) {
				printlogMsg(e.getMessage());
				return false;
			}
		} else// if stop service has been pressed
		{
			try {
				stopListening();
				close();
			} catch (IOException e) {
				printlogMsg(e.getMessage());
			}
		}
		return true;
	}

	/**
	 * getting Student by user id
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void getStudentKeyByuIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			/**
			 * Perform a Select query to get Student
			 */
			@Override
			public String getQuery() {
				return "SELECT sId,uId FROM student where uId=?";
			}

			/**
			 * Parse the result set in to a Student object
			 */
			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int sId = rs.getInt(1);
				int uId = rs.getInt(2);
				student stu;
				stu = new student(sId, uId);
				return (Object) stu;
			}

			/**
			 * adding Student user id field for this query
			 */
			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				Integer stu = (Integer) packet.getParameterForCommand(Command.getStudentByuId).get(0);
				stmt.setInt(1, stu);
			}
		});
	}

	/**
	 * getting Lecturer by user id
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void getlecturerByuIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			/**
			 * Perform a Select query to get all the Lecturers by user id
			 */
			@Override
			public String getQuery() {
				return "SELECT uId, lId, dId FROM lecturer where uId=?";
			}

			/**
			 * Parse the result set in to a Lecturer object
			 */
			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int lId = rs.getInt(1);
				int uId = rs.getInt(2);
				int dId = rs.getInt(3);

				return new Lecturer(lId, uId);
			}

			/**
			 * setting user id field for this query
			 */
			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				Integer uId = (Integer) packet.getParameterForCommand(Command.getlecturerByUid).get(0);
				stmt.setInt(1, uId);
			}
		});
	}

	/**
	 * getting user by user id
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void getUserByuIdHandler(DbQuery db, Command key) {

		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			/**
			 * adding user id field for this query
			 */
			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				stmt.setInt(1, (Integer) packet.getParameterForCommand(Command.getUserByuId).get(0));
			}

			/**
			 * Perform a Select query to get user
			 */
			@Override
			public String getQuery() {
				// TODO Auto-generated method stub
				return "SELECT * " + "FROM User u where uId=?";
			}

			/**
			 * Parse the result set in to a User object
			 */
			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				// TODO Auto-generated method stub
				int uId = rs.getInt(1);
				String id = rs.getString(2);
				String username = rs.getString(3);
				String password = rs.getString(4);
				int islogged = rs.getInt(5);
				String perm = rs.getString(6);
				Permission permission = null;
				boolean isloggedbool = (islogged == 1);
				User newuser;
				/**
				 * checking the permission
				 */
				if (perm.equals((Permission.Administrator).toString()))
					permission = Permission.Administrator;
				else if (perm.equals((Permission.Blocked).toString()))
					permission = Permission.Blocked;
				else if (perm.equals((Permission.Limited).toString()))
					permission = Permission.Limited;
				/**
				 * building the user information
				 */
				newuser = new User(uId, id, username, password, isloggedbool, permission);
				return (Object) newuser;
			}
		});

	}

	/**
	 * Get User instance by it's username and password
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void getUserByIdAndPassHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT uId,  id ,username, password, isLogged, permission FROM user where id=? AND password=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int uId = rs.getInt(1);
				String id = rs.getString(2);
				String username = rs.getString(3);
				String password = rs.getString(4);
				int islogged = rs.getInt(5);
				String perm = rs.getString(6);
				Permission permission = null;
				boolean isloggedbool = (islogged == 1);

				if (perm.equals((Permission.Administrator).toString()))
					permission = Permission.Administrator;
				else if (perm.equals((Permission.Blocked).toString()))
					permission = Permission.Blocked;
				else if (perm.equals((Permission.Limited).toString()))
					permission = Permission.Limited;

				return new User(uId, id, username, password, isloggedbool, permission);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getUserByIdAndPass);
				User user = (User) params.get(0);

				stmt.setString(1, user.getId());
				stmt.setString(2, user.getPassword());
			}
		});
	}

	/**
	 * Set user logged in state
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void updateUserIsLoggedHandler(DbQuery db, Command key) {
		DbUpdater<User> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<User>() {

			@Override
			public String getQuery() {
				return "UPDATE user SET isLogged=? WHERE uId=?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, User obj) throws SQLException {
				int logged = (obj.isLogged() ? 1 : 0);
				stmt.setInt(1, logged);
				stmt.setInt(2, obj.getuId());
			}
		});
	}

	/**
	 * Get courses by lecturer ID
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	public void getCoursesNamebyLidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT distinct C.cId,C.cName "
						+ "FROM lecturerincourse LIC INNER JOIN course C ON LIC.cId=C.cId " + "WHERE LIC.lId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int cId = rs.getInt(1);
				String cName = rs.getString(2);

				return new Course(cId, cName);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getCoursesbyLid);
				Lecturer lecturer = (Lecturer) params.get(0);

				stmt.setInt(1, lecturer.getlId());

			}
		});
	}

	/**
	 * sql query for get the count of raws
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	public void getQuestionHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT COUNT(*)" + "FROM question";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				return rs.getInt(1);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}

	/**
	 * Insert new question to the DB
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	public void addQuestionHandler(DbQuery db, Command key) {

		DbUpdater<Question> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Question>() {
			@Override
			public String getQuery() {
				return "Insert question(qId,cId,authorQues,notes,quesText,ans1,ans2,ans3,ans4,correctAns) values(?,?,?,?,?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Question obj) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, obj.getId());
				stmt.setInt(2, obj.getcid());
				stmt.setString(3, obj.getAuthor());
				stmt.setString(4, obj.getnotes());
				stmt.setString(5, obj.getQues());
				String[] pos_ans = obj.getpos_ans();
				stmt.setString(6, pos_ans[0]);
				stmt.setString(7, pos_ans[1]);
				stmt.setString(8, pos_ans[2]);
				stmt.setString(9, pos_ans[3]);
				stmt.setInt(10, obj.getcurrent_ans());

			}
		});

	}

	/**
	 * Get exams by student ID
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	private void getExambySidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT FE.exId,FE.grade,FE.readyToView   "
						+ "FROM finishedexams FE INNER JOIN course C ON FE.cId=C.cId  " + "WHERE FE.sId=? AND C.cid=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exID = rs.getString(1);
				int grade = rs.getInt(2);
				int View = rs.getInt(3);

				return new FinishedExam(exID, grade, View);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamBySid);
				student student = (student) params.get(0);
				Course course = (Course) params.get(1);
				stmt.setInt(1, student.getsId());
				stmt.setInt(2, course.getcId());

			}
		});

	}

	/**
	 * Get courses by student ID
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	private void getCoursesNamebySidHandler(DbQuery db, Command key) {

		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT C.cId,C.cName  " + "FROM studentincourse SIC INNER JOIN course C ON SIC.cId=C.cId "
						+ "WHERE SIC.sId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int cId = rs.getInt(1);
				String cName = rs.getString(2);

				return new Course(cId, cName);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getCoursesbySid);
				student student = (student) params.get(0);

				stmt.setInt(1, student.getsId());

			}
		});
	}

	/**
	 * sql query for get all the exam
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	private void getExamHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT* FROM exam";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String exid = rs.getString(1);
				int cId = rs.getInt(2);
				String author = rs.getString(9);

				return new Exam(exid, cId, author);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}

	/**
	 * sql query for avg of the student
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	private void getAVGBySidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT S.avg   " + "FROM student S  " + "WHERE S.sId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float Grade = rs.getFloat(1);
				return Grade;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getAVGBySid);
				student student = (student) params.get(0);
				stmt.setInt(1, student.getsId());

			}
		});
	}

	/**
	 * sql query for department of the lecturer
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	private void getDepartmentByLidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT D.dname   " + "FROM department D INNER JOIN lecturer L ON D.did=L.did  "
						+ "WHERE L.lid=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String dname = rs.getString(1);
				return dname;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getDepartmentByLid);
				Lecturer lecturer = (Lecturer) params.get(0);
				stmt.setInt(1, lecturer.getlId());

			}
		});
	}

	/**
	 * sql query for Update Exam Code
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	private void UpdateExamCOdeHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "UPDATE exam SET exCode = ? WHERE exId = ?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam obj) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, obj.getExamCode());
				stmt.setString(2, obj.getExamId());
			}
		});
	}

	/**
	 * sql query for get question with cid
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */

	private void getQuestionByCidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT qId,quesText ,authorQues  FROM question where cId=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String qid = rs.getString(1);
				String quesText = rs.getString(2);
				String authorQues = rs.getString(3);
				return new Question(qid, quesText, authorQues);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getQuestionByCid);
				int cid = (int) params.get(0);
				stmt.setInt(1, cid);

			}
		});
	}

	/**
	 * Loads the exam by the code the user entered
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	private void getExamByExCodeHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT E.exId,E.dId   " + "FROM exam E INNER JOIN activeexams AE ON E.exId=AE.exID   "
						+ "WHERE E.exCode=? AND AE.ableToAccess=1 AND activeExam=1 ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String examcode = rs.getString(1);
				int Did = rs.getInt(2);
				Exam exam = new Exam(examcode, Did);
				return exam;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamByExCode);
				String examcode = (String) params.get(0);
				stmt.setString(1, examcode);
			}
		});
	}

	/**
	 * Retrieves the department ID of the student that is logged in
	 * 
	 * @param db
	 *            -Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	private void getDepartmentIDByUidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT D.did   "
						+ "FROM department D INNER JOIN student S ON D.did=S.did INNER JOIN user U ON U.Id=S.uId   "
						+ "WHERE U.Id=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int did = rs.getInt(1);
				return did;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getDepartmentIDByUid);
				int Uid = (int) params.get(0);
				stmt.setInt(1, Uid);

			}
		});
	}

  /**
 * Retrieves the lecturer ID of the user that is logged in
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
 
	private void getAllLecturersHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT L.uId,U.username FROM lecturer L INNER JOIN user U ON L.uId=U.uId";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int uId = rs.getInt(1);
				String username = rs.getString(2);

				return new Lecturer(uId, username);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});

	}
  /**
  * Retrieves the courses ID and name
  *
  * @param db - Stores database information
  *
  * @param key - Command operation which is performed
  *
  */
	private void getAllCoursesHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT * FROM course";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int cId = rs.getInt(1);
				String cName = rs.getString(2);

				return new Course(cId, cName);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}
  /**
 * Retrieves the student user ID and the username
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
	private void getAllStudentsHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT S.uId,U.username FROM student S INNER JOIN user U ON S.uId=U.uId";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int uId = rs.getInt(1);
				String username = rs.getString(2);

				return new student(uId, username);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}
  /**
 * A query that adds an exam to the DB and containing all the exam fields
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
	private void addExamIDHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "Insert exam(exId,cId,dId,lId ,exCode,timeInmin,lecInst,studInst,author,exDone) values(?,?,?,?,?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {

				stmt.setString(1, ans.getExamId());
				stmt.setInt(2, ans.getCourseId());
				stmt.setInt(3, ans.getDepartmentId());
				stmt.setInt(4, ans.getLecturerId());
				stmt.setString(5, ans.getExamCode());
				stmt.setString(6, ans.getTimeInMin());
				stmt.setString(7, ans.getLectureInstructions());
				stmt.setString(8, ans.getStudentInstructions());
				stmt.setString(9, ans.getAuthor());
				stmt.setString(10, "0");

			}
		});

	}
  /**
 * A query that updates the DB and adds a question to a question in exam table
 * this table connects the question to a specific exam
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
	private void addQuestionInExamIDHandler(DbQuery db, Command key) {
		DbUpdater<QuestionInExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<QuestionInExam>() {
			@Override
			public String getQuery() {
				return "Insert questioninexam(qid,exId,score) values(?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, QuestionInExam ans) throws SQLException {

				stmt.setString(1, ans.getQueId());
				stmt.setString(2, ans.getExamId());
				stmt.setInt(3, ans.getScore());

			}
		});

	}
  /**
 * Retrieves the number of exams of a specific course
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
	public void getCountExamByCidIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT COUNT(*) FROM exam where cId = ?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				return rs.getInt(1);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getCountExamByCid);
				Exam exam = (Exam) params.get(0);
				stmt.setInt(1, exam.getCourseId());
			}
		});
	}

  /**
 * request get all live lecturer request for add time from principal
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
 	private void getgetALLlecturersRequestsHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT U.username,AE.exId,AE.lId "
						+ " FROM user U,lecturer L INNER JOIN activeexams AE ON L.lId=AE.lId AND AE.lecturerRequestFlag=1 AND AE.activeExam=1 "
						+ " WHERE L.uId=U.uID ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String username = rs.getString(1);
				String exId = rs.getString(2);
				int lId = rs.getInt(3);

				return new Exam(username, exId, lId);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}

  /**
 * request to bring from the db of a specific exam
 *
 * @param db - Stores database information
 *
 * @param key - Command operation which is performed
 *
 */
 	private void getLecturerReasonHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " SELECT AE.lecturerRequest" + " FROM activeexams AE WHERE AE.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String reason = rs.getString(1);
				return reason;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getLecturerReason);
				String exId = (String) params.get(0);
				stmt.setString(1, exId);

			}
		});
	}
  /**
	 * Request to get all the active exams by a specific lecturer
	 *
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 *
	 */
	private void getOnlineExamByLecturerHandler(DbQuery db, Command key) {

		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " SELECT E.exId,E.author,E.cId "
						+ " FROM exam E INNER JOIN changeexamtime CT ON E.exId=CT.exId INNER JOIN activeexams AE ON AE.exId=E.exId INNER JOIN course C ON C.cId=CT.cId "
						+ " WHERE CT.lId=? AND CT.alreadyChecked=0 AND AE.activeExam=1 AND E.cId =? ";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String exid = rs.getString(1);
				int cId = rs.getInt(3);
				String author = rs.getString(2);
				String flag = " ";

				return new Exam(exid, cId, author, flag);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getOnlineExamByLecturer);
				Course course = (Course) params.get(1);
				Lecturer lecturer = (Lecturer) params.get(0);
				stmt.setInt(1, lecturer.getlId());
				stmt.setInt(2, course.getcId());
			}
		});

	}
  
  /**
   * Request to get the department is of a specific lecturer by his user ID
   *
   * @param db - Stores database information
   *
   * @param key - Command operation which is performed
   *
   */
	private void getDepartmentIDByUidLecturerHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT D.did   "
						+ "FROM department D INNER JOIN lecturer L ON D.did=L.did INNER JOIN user U ON U.Id=L.uId   "
						+ "WHERE U.Id=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				return rs.getInt(1);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getDepartmentIDByUidLecturer);
				int Uid = (int) params.get(0);
				stmt.setInt(1, Uid);

			}
		});
	}
  /**
   * updates the principal answer for decline
   *
   * @param db - Stores database information
   *
   * @param key - Command operation which is performed
   *
   */
	private void addDeclineAnswerFromPrincipalHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return " update activeexams " + " SET principalAnswerFlag=-1,principalAnswer=?,lecturerRequestFlag=0 "
						+ " WHERE exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {
				// TODO Auto-generated method stub
				stmt.setString(1, ans.getExamId());
				stmt.setString(2, ans.getExamCode());

			}
		});
	}

  /**
   * updates the principal answer for approval  lecturer add time request
   *
   * @param db - Stores database information
   *
   * @param key - Command operation which is performed
   *
   */
   	private void addApproveAnswerFromPrincipalHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return " update activeexams " + " SET principalAnswerFlag=1,principalAnswer=?,lecturerRequestFlag=0 "
						+ " WHERE exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {
				// TODO Auto-generated method stub
				stmt.setString(1, ans.getExamId());
				stmt.setString(2, ans.getExamCode());

			}
		});
	}
  
  	/**
  	 * A query that retrieves all the finished exams of a specific student in a specific course
  	 *
  	 * @param db - Stores database information
  	 *
  	 * @param key - Command operation which is performed
  	 *
  	 */
     
	private void getExamsByCidAndSidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT distinct EXC.exId " + " FROM finishedquestions EXC " + " WHERE EXC.sId=? and EXC.cId=? ";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exID = rs.getString(1);

				return new Exam(exID);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamsBySidAndCid);
				student student = (student) params.get(0);
				Course course = (Course) params.get(1);
				stmt.setInt(1, student.getsId());
				stmt.setInt(2, course.getcId());

			}
		});

	}
  
  	/**
  	 * Updates the status of a specific exam
  	 *
  	 * @param db - Stores database information
  	 *
  	 * @param key - Command operation which is performed
  	 *
  	 */
	private void SetActiveExamHandler(DbQuery db, Command key) {
		DbUpdater<ActiveExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<ActiveExam>() {
			@Override
			public String getQuery() {
				return "Insert activeexams(exId,lId,activeExam,lecturerRequestFlag,principalAnswerFlag,extraTime) values(?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, ActiveExam ans) throws SQLException {

				stmt.setString(1, ans.getExId());
				stmt.setInt(2, ans.getlId());
				stmt.setInt(3, ans.getActiveExam());
				stmt.setInt(4, ans.getLectureRequestFlag());
				stmt.setInt(5, ans.getPrincipalAnswerFlag());
				stmt.setInt(6, ans.getExtraTime());

			}
		});

	}
  
  	/**
  	 * updates the extra time in an active exam
  	 *
  	 * @param db - Stores database information
  	 *
  	 * @param key - Command operation which is performed
  	 *
  	 */
	private void updateExtraTimeHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "UPDATE activeexams SET lecturerRequest=?,extraTime=?,lecturerRequestFlag=1 WHERE exId =?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {
				// TODO Auto-generated method stub
				stmt.setString(3, ans.getExamId());
				stmt.setInt(2, ans.getCourseId());
				stmt.setString(1, ans.getAuthor());

			}
		});
	}
  
  	/**
  	 * A query to lock a specific exam.
  	 *
  	 * @param db - Stores database information
  	 *
  	 * @param key - Command operation which is performed
  	 *
  	 */
	private void updateIsLockedHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "UPDATE activeexams SET activeExam = 0 WHERE exId = ?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam obj) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, obj.getExamId());
			}
		});
	}
  
  	/**
  	 * updates the principal anser for approval  lecturer add time request
  	 *
  	 * @param db - Stores database information
  	 *
  	 * @param key - Command operation which is performed
  	 *
  	 */
	private void getExamTimeHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT E.timeInMin   " + "FROM exam E " + "WHERE E.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int Examtime = rs.getInt(1);
				return Examtime;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamTime);
				String ExID = (String) params.get(0);
				stmt.setString(1, ExID);

			}
		});

	}
  /**
   * A query adds a finished exam with all its details to the finished exams table
   *
   * @param db - Stores database information
   *
   * @param key - Command operation which is performed
   *
   */
	private void AddFinishedExamHandler(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return "UPDATE ignore finishedexams  SET sId=?,cId=?,exId=?,Grade=?,started=?,finished=?,time=?,authorId=?,courseName=?,readyToView=?,changeGradeReason=?,ongoing=? WHERE exId =?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {

				stmt.setInt(1, ans.getSid());
				stmt.setInt(2, ans.getCid());
				stmt.setString(3, ans.getExID());
				stmt.setInt(4, ans.getGrade());
				stmt.setInt(5, ans.getStarted());
				stmt.setInt(6, ans.getFinished());
				stmt.setString(7, ans.getTime());
				stmt.setString(8, "0");
				stmt.setString(9, "0");
				stmt.setString(10, "0");
				stmt.setString(11, null);
				stmt.setInt(12, 0);
				stmt.setString(13, ans.getExID());

			}
		});

	}
  /**
	 * A query that retrieves the exam ID by a specific student ID and the exam execution code
	 *
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 *
	 */
	private void getExamByExamIdAndSidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT distinct E.exId   "
						+ "FROM exam E INNER JOIN finishedquestions EC ON E.exId=EC.exID INNER JOIN student S ON S.sId=EC.sId "
						+ "WHERE E.exCode=? AND S.sId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String ExamID = rs.getString(1);
				return ExamID;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamByExamIdAndSid);
				String ExID = (String) params.get(0);
				int Sid = (int) params.get(1);
				stmt.setString(1, ExID);
				stmt.setInt(2, Sid);

			}
		});

	}
	  /**
		 * A query that retrieves the exam's course id by an specific exam ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */

	private void getCourseIDbyExamIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT  E.cid  " + "FROM Exam E " + "WHERE E.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int Cid = rs.getInt(1);

				return Cid;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getCourseIDbyExamID);
				String ExID = (String) params.get(0);
				stmt.setString(1, ExID);

			}
		});

	}
	  /**
		 * A query that insert a question details to the finished exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void AddcheckedQuestionsHandler(DbQuery db, Command key) {
		DbUpdater<FinishedQuestion> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedQuestion>() {
			@Override
			public String getQuery() {
				return "Insert finishedquestions(sId,cId,exId,qId,ansStId,Grade) values(?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedQuestion ans) throws SQLException {

				stmt.setInt(1, ans.getSid());
				stmt.setInt(2, ans.getCid());
				stmt.setString(3, ans.getexID());
				stmt.setString(4, ans.getqId());
				stmt.setInt(5, ans.getansStId());
				stmt.setInt(6, ans.getGrade());
			}
		});

	}
	  /**
		 * A query that retrieves all the question and their details of a specific exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getQuestionsByExIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT  Q.quesText,Q.ans1,Q.ans2,Q.ans3,Q.ans4,Q.correctAns,QIE.score,Q.notes,Q.qid  "
						+ "FROM questioninexam QIE INNER JOIN exam E ON QIE.exId=E.exId INNER JOIN question Q ON QIE.qid=Q.qId "
						+ "WHERE E.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String quesText = rs.getString(1);
				String ans1 = rs.getString(2);
				String ans2 = rs.getString(3);
				String ans3 = rs.getString(4);
				String ans4 = rs.getString(5);
				int correctAns = rs.getInt(6);
				int score = rs.getInt(7);
				String notes = rs.getString(8);
				String Qid = rs.getString(9);
				return new Question(quesText, ans1, ans2, ans3, ans4, correctAns, score, notes, Qid);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getQuestionsByExID);
				String ExID = (String) params.get(0);
				stmt.setString(1, ExID);

			}
		});

	}
	  /**
		 * A query that retrieves the student avg from the finished exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getStudentAvgHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "Select avg(FE.grade) FROM finishedexams FE where FE.sId =?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float average = rs.getFloat(1);

				return new Exam(average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getStudentAvg);
				int sId = (int) params.get(0);
				stmt.setInt(1, sId);

			}
		});
	}
	  /**
		 * A query that retrieves the student median from the finished exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getStudentMedianHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " select avg(grade) as median from ( SELECT @counter:=@counter+1 as 'row_id' , grade FROM finishedexams ,(select @counter:=0) r where sId=? order by 2) o1 INNER join ( select count(*) as total_rows from finishedexams FE where FE.sId=? ) o2 where o1.row_id in (floor((o2.total_rows + 1)/2), floor((o2.total_rows + 2)/2))  ";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float median = rs.getFloat(1);

				return new Exam(median);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getStudentMedian);
				int sId = (int) params.get(0);
				stmt.setInt(1, sId);
				stmt.setInt(2, sId);

			}
		});
	}
	  /**
		 * A query that retrieves the average of a specific course  
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getAvgbyCourseNameHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "Select avg(FE.grade) FROM finishedexams FE where FE.courseName=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float average = rs.getFloat(1);

				return new Exam(average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getAvgbyCourseName);
				String cName = (String) params.get(0);
				stmt.setString(1, cName);

			}
		});

	}

	  /**
		 * A query that retrieves the median of a specific course by the course name
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getMedianbyCourseNameHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "select avg(grade) as median from ( SELECT @counter:=@counter+1 as 'row_id' , grade FROM finishedexams ,(select @counter:=0) r where courseName=? order by 2) o1 INNER join ( select count(*) as total_rows from finishedexams FE where FE.courseName=? ) o2 where o1.row_id in (floor((o2.total_rows + 1)/2), floor((o2.total_rows + 2)/2))  ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float average = rs.getFloat(1);

				return new Exam(average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getMedianbyCourseName);
				String cName = (String) params.get(0);
				stmt.setString(1, cName);
				stmt.setString(2, cName);
			}
		});
	}
	  /**
		 * A query that retrieves the average of all the exams of a specific lecturer
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getAvgOfLecturerHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " Select avg(FE.grade) FROM finishedexams FE where FE.authorId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float average = rs.getFloat(1);

				return new Exam(average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getAvgOfLecturer);
				int lId = (int) params.get(0);
				stmt.setInt(1, lId);

			}
		});

	}
	  /**
		 * A query that retrieves the median  of all the exams of a specific lecturer
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getMedianOfLecturerHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "select avg(grade) as median from ( SELECT @counter:=@counter+1 as 'row_id' , grade FROM finishedexams ,(select @counter:=0) r where authorId=? order by 2) o1 INNER join ( select count(*) as total_rows from finishedexams FE where FE.authorId=? ) o2 where o1.row_id in (floor((o2.total_rows + 1)/2), floor((o2.total_rows + 2)/2))  ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				Float average = rs.getFloat(1);

				return new Exam(average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getMedianOfLecturer);
				int authorId = (int) params.get(0);
				stmt.setInt(1, authorId);
				stmt.setInt(2, authorId);
			}
		});
	}
	  /**
		 * A query that checks if there is more than one exam with the same execution code
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void checkCodeHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT COUNT(*) FROM exam WHERE exCode = ?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				return rs.getInt(1);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.CheckCodeExam);
				String examCode = (String) params.get(0);
				stmt.setString(1, examCode);

			}
		});

	}
	  /**
		 * A query that retrieves the exam ID of a specific lecturer
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExamByLecturerIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT exId" + " FROM exam  WHERE lId = ?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exId = rs.getString(1);

				return new Exam(exId);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamByLecturerID);
				int lid = (Integer) params.get(0);
				stmt.setInt(1, lid);
			}
		});
	}
	  /**
		 * A query that insert to the change exam time table the exam detail when 
		 * time extension is needed
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void addExamToChangeTimeHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "Insert changeexamtime(lId,cId,exId,lReason,pAnswer,approveOrDecline,alreadyChecked) values(?,?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {

				stmt.setInt(1, ans.getLecturerId());
				stmt.setInt(2, ans.getCourseId());
				stmt.setString(3, ans.getExamId());
				stmt.setString(4, "reason");
				stmt.setString(5, null);
				stmt.setInt(6, 0);
				stmt.setInt(7, 0);

			}
		});

	}
	  /**
		 * A query that retrieves the student grade of a specific exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getGradesbySidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT SIE.grade   " + "FROM finishedexams SIE " + " WHERE SIE.sId=? AND SIE.exId=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int grade = rs.getInt(1);

				return grade;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getGradesbySid);
				Exam exam = (Exam) params.get(0);
				student student = (student) params.get(1);
				stmt.setInt(1, student.getuId());
				stmt.setString(2, exam.getExamId());

			}
		});

	}
	  /**
		 * A query that inserts the lecturer question note of a specific question
		 *  for the student to watch when he is reviewing his exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateQuestionNoteUpdater(DbQuery db, Command key) {
		DbUpdater<FinishedQuestion> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedQuestion>() {
			@Override
			public String getQuery() {
				return " UPDATE finishedquestions SET notes=? WHERE exId=? and qId=? and sId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedQuestion ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, ans.getNotes());
				stmt.setString(2, ans.getexID());
				stmt.setString(3, ans.getqId());
				stmt.setInt(4, ans.getSid());
			}
		});

	}
	  /**
		 * A query that retrieves the student answers for a specific question in a specific exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getAnsweredQuestionSidAndQidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " SELECT FQ.qId, FQ.ansStId, FQ.notes, FQ.Grade " + " FROM finishedquestions FQ "
						+ " WHERE FQ.sId=? AND FQ.exId=? ";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String qId = rs.getString(1);
				int ansStId = rs.getInt(2);
				// int corAns = rs.getInt(3);
				String notes = rs.getString(3);
				int Grade = rs.getInt(4);

				return new FinishedQuestion(qId, ansStId, notes, Grade);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getAnsweredQuestionSidAndQid);
				int sId = (int) params.get(0);
				String exId = (String) params.get(1);
				stmt.setInt(1, sId);
				stmt.setString(2, exId);

			}
		});

	}
	  /**
		 * A query that retrieves the exam ID of a specific exam in a specific course
		 * of a specific student
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExamBySidCheckExHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT SIE.exId   " + "FROM finishedexams SIE INNER JOIN course C ON SIE.cId=C.cId "
						+ "WHERE SIE.sId=? AND C.cid=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exID = rs.getString(1);

				return new Exam(exID);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamBySidCheckExam);
				Course course = (Course) params.get(0);
				student student = (student) params.get(1);
				stmt.setInt(1, student.getuId());
				stmt.setInt(2, course.getcId());

			}
		});
	}
	  /**
		 * A query that retrieves the student finished exam and returns the exam questions and information
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getQuestionsByExIDCheckExamHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT  DISTINCT Q.quesText,Q.ans1,Q.ans2,Q.ans3,Q.ans4,Q.correctAns,Q.notes,Q.qid,FQ.ansStId,QIE.score  "
						+ "FROM finishedquestions FQ INNER JOIN question Q INNER JOIN finishedexams FE INNER JOIN questioninexam QIE  "
						+ "WHERE FQ.exId=FE.exId AND FQ.sId=FE.sId AND FQ.sId=? AND FQ.qId=Q.qId AND FE.exId=? AND FQ.exId=QIE.exId AND Q.qId=QIE.qid ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String quesText = rs.getString(1);
				String ans1 = rs.getString(2);
				String ans2 = rs.getString(3);
				String ans3 = rs.getString(4);
				String ans4 = rs.getString(5);
				int correctAns = rs.getInt(6);
				String notes = rs.getString(7);
				String id = rs.getString(8);
				int ansStId = rs.getInt(9);
				int score = rs.getInt(10);
				return new Question(quesText, ans1, ans2, ans3, ans4, correctAns, notes, id, ansStId, score);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getQuestionsByExIDCheckExam);
				int sId = (int) params.get(1);
				String ExID = (String) params.get(0);
				stmt.setInt(1, sId);
				stmt.setString(2, ExID);

			}
		});

	}
	  /**
		 * A query that retrieves the student username and ID in a specific course
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getStudentByCidHandler(DbQuery db, Command key) {

		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT  U.username,U.Id   "
						+ "FROM user U INNER JOIN student S ON U.Id=S.uId INNER JOIN studentincourse SIC ON SIC.sId=S.sId INNER JOIN course C ON SIC.cId=C.cId "
						+ "WHERE C.cId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String username = rs.getString(1);
				int uId = rs.getInt(2);
				return new student(username, uId);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getStudentByCid);
				int Cid = (int) params.get(0);
				stmt.setInt(1, Cid);

			}
		});
	}
	  /**
		 * A query that retrieves the exam ID of a finished exam by the student ID and the course ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExamBySidCheckExamHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT SIE.exId   " + "FROM finishedexams SIE INNER JOIN course C ON SIE.cId=C.cId "
						+ "WHERE SIE.sId=? AND C.cid=?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exID = rs.getString(1);

				return new Exam(exID);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamBySidCheckExam);
				Course course = (Course) params.get(0);
				student student = (student) params.get(1);
				stmt.setInt(1, student.getuId());
				stmt.setInt(2, course.getcId());

			}
		});
	}
	  /**
		 * A query that retrieves grade and course name of a finished exam by the student ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getDataStudentBySidHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "select grade , courseName from finishedexams where sId = ?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int grade = rs.getInt(1);
				String courseName = rs.getString(2);

				return new Exam(courseName, grade);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getDataStudentBySid);
				int lid = (Integer) params.get(0);
				stmt.setInt(1, lid);

			}
		});
	}
	  /**
		 * A query that retrieves grade of a finished exam by the course name
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getGradeCourseNameHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "select grade from finishedexams where courseName = ?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				int grade = rs.getInt(1);

				return new FinishedExam(grade);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getGradeCourseName);
				String courseName = (String) params.get(0);
				stmt.setString(1, courseName);

			}
		});
	}
	  /**
		 * A query that sets the lecturer  details in the exam by a specific exam ID 
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getLecturerUidAndCourseNamebyExamIDHandler(DbQuery db, Command key) {
		DbUpdater<String> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<String>() {
			@Override
			public String getQuery() {
				return "update finishedexams "
						+ "SET authorId=(Select L.uId FROM exam E INNER JOIN lecturer L ON E.lId=L.lId WHERE E.exId=? ) ,courseName=(Select C.cName FROM exam E INNER JOIN course C ON E.cId=C.cId WHERE E.exId=? ) "
						+ "WHERE exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, String ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, ans);
				stmt.setString(2, ans);
				stmt.setString(3, ans);
			}
		});

	}
	  /**
		 * A query that sets a reason for grade changing of a specific student is a specific exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateChangeGradeReasonUpdater(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return " UPDATE finishedexams SET changeGradeReason=? WHERE exId=? and sId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, ans.getReason());
				stmt.setString(2, ans.getExID());
				stmt.setInt(3, ans.getSid());
			}
		});

	}
	  /**
		 * A query that updates a specific student average
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateStudentAvgUpdater(DbQuery db, Command key) {
		DbUpdater<student> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<student>() {
			@Override
			public String getQuery() {
				return " UPDATE student SET avg=? WHERE sId=?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, student ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setFloat(1, ans.getavg());
				stmt.setInt(2, ans.getuId());
			}
		});

	}
	  /**
		 * A query that updates the grade  and makes the exam ready to view by student
		 * of a specific exam and student ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateStudentGradeUpdater(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return " UPDATE finishedexams SET grade=?, readyToView=1 WHERE exId=? and sId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setInt(1, ans.getGrade());
				stmt.setString(2, ans.getExID());
				stmt.setInt(3, ans.getSid());
			}
		});

	}
	  /**
		 * A query that updates the student in exam table the grade of a specific exam ID and student ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateStudentGradeInExamHandler(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return " UPDATE studentinexam SET grade=? WHERE exId=? and sId=?";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setInt(1, ans.getGrade());
				stmt.setString(2, ans.getExID());
				stmt.setInt(3, ans.getSid());
			}
		});

	}
	  /**
		 * A query that retrieves the extra time of a specific active exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExtraTimeHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return "Select AE.extraTime  " + "FROM activeexams AE "
						+ "WHERE AE.principalAnswerFlag=1 AND AE.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int time = rs.getInt(1);
				return time;

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExtraTime);
				String exID = (String) params.get(0);
				stmt.setString(1, exID);

			}
		});

	}

	/**
	 * query that gets the flag from finishedexams table that says if the wanted
	 * exam has been checked by the lecturer.
	 * 
	 * @param db
	 *            - Stores database information
	 * @param key
	 *            - Command operation which is performed
	 */
	private void getFinishedExamFlagByExIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return " SELECT readyToView " + " FROM finishedexams " + "WHERE exId=? and sId = ? ";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int flag = rs.getInt(1);
				return flag;

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getFinishedExamFlagByExId);
				String exID = (String) params.get(0);
				int sid = (int) params.get(1);

				stmt.setString(1, exID);
				stmt.setInt(2, sid);

			}
		});

	}
	  /**
		 * A method thats create a new File System and uploads it
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void uploadFileHandler(DbQuery db, Command key) {

		Packet packet = db.getPacket();
		try {
			// get all parameters

			FileSystem file = (FileSystem) packet.getParameterForCommand(Command.uploadFile).get(0);

			file.saveFileOnServer();
		} catch (Exception e) {
			packet.setExceptionMessage(e.getMessage());
		}
	}
	  /**
		 * A query that updates the exam details of an exam that has been done
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void addDoneExamDetailsHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "INSERT  doneexamdetails (`exid`) VALUES (?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {

				stmt.setString(1, ans.getExamId());

			}
		});

	}
	  /**
		 * A query that retrieves if a specific exam is lock by the exam ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void checkLockedExamHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return " SELECT AE.activeExam " + " FROM activeexams AE  " + "WHERE AE.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int locked = rs.getInt(1);
				return locked;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.checkLockedExam);
				String exID = (String) params.get(0);
				stmt.setString(1, exID);

			}
		});

	}
	  /**
		 * A query that retrieves the exam ID of an active exam by the exam ID and course ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExamByCourseIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return " select AE.exId FROM activeexams AE INNER JOIN exam E ON AE.exId=E.exId where E.cId=? AND AE.activeExam=0";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String exID = rs.getString(1);
				return new Exam(exID);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamByCourseID);
				int Cid = (int) params.get(0);
				stmt.setInt(1, Cid);

			}

		});

	}
	  /**
		 * A query that retrieves the exams that are locked
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getLockedExamCoursesHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return " SELECT distinct FE.cId, FE.courseName "
						+ " FROM activeexams AE INNER JOIN finishedexams FE ON AE.exId=FE.exId   "
						+ "Where AE.activeExam=0 ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int cid = rs.getInt(1);
				String cname = rs.getString(2);
				return new Course(cid, cname);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
			}

		});

	}
	  /**
		 * A query that sets the grade of a specific finished exam by exam ID and student ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void saveCurrentGradeUpdater(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return " UPDATE finishedexams SET readyToView=1 WHERE exId=? and sId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setString(1, ans.getExID());
				stmt.setInt(2, ans.getSid());
			}
		});

	}
	  /**
		 * A query that updates the statistics on the specific exam
		 * updates how many studnets finished alone, how many finished not alone and how many started
		 * also updates the start date and the total exam time.
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateExamStatisticsUpdater(DbQuery db, Command key) {
		{
			{
				DbUpdater<ActiveExam> dbUpdate = new DbUpdater<>(db, key);

				dbUpdate.performAction(new IUpdate<ActiveExam>() {
					@Override
					public String getQuery() {
						return " update doneexamdetails "
								+ " SET studentStart=(select count(*) FROM finishedexams FE  where FE.exId=? AND FE.started=1),  "
								+ " studentFinishAlone=(select count(*) FROM finishedexams FE  where FE.exId=? AND FE.finished=1), "
								+ " studentFinishNotAlone=(studentStart-studentFinishAlone) " + " WHERE exId=?";
					}

					@Override
					public void setStatements(PreparedStatement stmt, ActiveExam ans) throws SQLException {

						stmt.setString(1, ans.getExId());
						stmt.setString(2, ans.getExId());
						stmt.setString(3, ans.getExId());

					}
				});

			}

		}

	}
	  /**
		 * A query that updates the total time of the exam for the exam statistics
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateTotalTimeUpdater(DbQuery db, Command key) {
		{
			DbUpdater<ActiveExam> dbUpdate = new DbUpdater<>(db, key);

			dbUpdate.performAction(new IUpdate<ActiveExam>() {
				@Override
				public String getQuery() {
					return " update doneexamdetails "
							+ " SET lengthExam=(select (AE.extraTime+E.timeInMin) FROM activeexams AE INNER JOIN exam E ON AE.exId=E.exId where AE.exId=? )  "
							+ " WHERE exId=? ";
				}

				@Override
				public void setStatements(PreparedStatement stmt, ActiveExam ans) throws SQLException {

					stmt.setString(1, ans.getExId());
					stmt.setString(2, ans.getExId());

				}
			});

		}

	}
	  /**
		 * A query that updates the total exam time(Normal time + time extended)
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateDateExamUpdater(DbQuery db, Command key) {
		DbUpdater<ActiveExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<ActiveExam>() {
			@Override
			public String getQuery() {
				return " UPDATE doneexamdetails SET date=? WHERE exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, ActiveExam ans) throws SQLException {

				String cal = new Date().toString();
				stmt.setString(1, cal);
				stmt.setString(2, ans.getExId());

			}
		});

	}
	  /**
		 * A query that retrieves the exam ID of a finished exam by a specific exam of a specific student
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getExamByExamIdAndSidManualHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return "SELECT  exId FROM finishedexams WHERE exId=? AND sId =?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String exID = rs.getString(1);
				return exID;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getExamByExamIdAndSidManual);
				String exID = (String) params.get(0);
				int Sid = (int) params.get(1);

				stmt.setString(1, exID);
				stmt.setInt(2, Sid);

			}
		});

	}
	  /**
		 * A query that insert a manual exam details to the finished exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void AddFinishedExamManualHandler(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return "Insert finishedexams(sId,cId,exId,started,finished,time,authorId,courseName,readyToView,changeGradeReason) values(?,?,?,?,?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {

				stmt.setInt(1, ans.getSid());
				stmt.setInt(2, ans.getCid());
				stmt.setString(3, ans.getExID());
				stmt.setInt(4, ans.getStarted());
				stmt.setInt(5, ans.getFinished());
				stmt.setString(6, ans.getTime());
				stmt.setString(7, "0");
				stmt.setString(8, "0");
				stmt.setString(9, "0");
				stmt.setString(10, null);

			}
		});

	}
	  /**
		 * A query that retrieves question detail of a specific finished exam by the exam ID 
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getFinishedQuestionsByExIDHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return "select distinct FE.sid, FQ.qId,FQ.ansStId from finishedexams FE,finishedquestions FQ where FE.grade!=0 AND FQ.sId=FE.sId AND FE.exId=? AND FQ.exId=FE.exId  ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int sid = rs.getInt(1);
				String qid = rs.getString(2);
				String ans = rs.getString(3);
				return new Cheat(sid, ans);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

				ArrayList<Object> params = packet.getParameterForCommand(Command.getFinishedQuestionsByExID);
				String ExID = (String) params.get(0);
				stmt.setString(1, ExID);
			}

		});

	}
	  /**
		 * A query that sets the number of students in the same grade range of a specific course
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void UpdateCountReportCourseHandler(DbQuery db, Command key) {
		Packet packet = db.getPacket();

		FinishedExam query = (FinishedExam) packet.getParameterForCommand(Command.UpdateCountReportCourse).get(0);

		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {

				return String.format("UPDATE coursestatistics SET %s=%s+1 WHERE cId = ?", query.getReason(),
						query.getReason());

			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam obj) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setInt(1, obj.getSid());

			}
		});
	}
	  /**
		 * A query that retrieves students grade for the course statistics by the course id
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getGradeCourseByCIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return "SELECT  a,b,c,d,e,f,g,h,i,j FROM coursestatistics WHERE cId =?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int all_Sections[] = new int[10];

				all_Sections[0] = rs.getInt(1);
				all_Sections[1] = rs.getInt(2);
				all_Sections[2] = rs.getInt(3);
				all_Sections[3] = rs.getInt(4);
				all_Sections[4] = rs.getInt(5);
				all_Sections[5] = rs.getInt(6);
				all_Sections[6] = rs.getInt(7);
				all_Sections[7] = rs.getInt(8);
				all_Sections[8] = rs.getInt(9);
				all_Sections[9] = rs.getInt(10);

				return new Course(all_Sections);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getGradeCourseByCId);
				int courseGrade = (int) params.get(0);

				stmt.setInt(1, courseGrade);

			}
		});

	}
	  /**
		 * A query that update the statistics of the lecturer exams by range of the grades
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void UpdateLecturercReportHandler(DbQuery db, Command key) {
		Packet packet = db.getPacket();

		FinishedExam query = (FinishedExam) packet.getParameterForCommand(Command.UpdateLecturercReport).get(0);

		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {

				return String.format("UPDATE lecturerstatistics SET %s=%s+1 WHERE lid = ?", query.getReason(),
						query.getReason());

			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam obj) throws SQLException {
				// TODO Auto-generated method stub

				stmt.setInt(1, obj.getSid());

			}
		});
	}
	  /**
		 * A query that retrieves the lecture statistics by the lecture ID
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getGradeLecturerBylIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override

			public String getQuery() {
				return "SELECT  a,b,c,d,e,f,g,h,i,j FROM lecturerstatistics WHERE lId =?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int all_Sections[] = new int[10];

				all_Sections[0] = rs.getInt(1);
				all_Sections[1] = rs.getInt(2);
				all_Sections[2] = rs.getInt(3);
				all_Sections[3] = rs.getInt(4);
				all_Sections[4] = rs.getInt(5);
				all_Sections[5] = rs.getInt(6);
				all_Sections[6] = rs.getInt(7);
				all_Sections[7] = rs.getInt(8);
				all_Sections[8] = rs.getInt(9);
				all_Sections[9] = rs.getInt(10);

				return new Course(all_Sections);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getGradeLecturerBylId);
				int courseGrade = (int) params.get(0);

				stmt.setInt(1, courseGrade);

			}
		});

	}
	  /**
		 * A query that updates the exam details in the finished exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void initExamHandler(DbQuery db, Command key) {
		DbUpdater<FinishedExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<FinishedExam>() {
			@Override
			public String getQuery() {
				return "Insert finishedexams(sId,cId,exId,started,authorId,courseName,ongoing) values(?,?,?,?,?,?,?)";
			}

			@Override
			public void setStatements(PreparedStatement stmt, FinishedExam ans) throws SQLException {

				stmt.setInt(1, ans.getSid());
				stmt.setInt(2, ans.getCid());
				stmt.setString(3, ans.getExID());
				stmt.setInt(4, 1);
				stmt.setInt(5, 0);
				stmt.setString(6, "0");
				stmt.setInt(7, 1);
			}

		});

	}
	  /**
		 * A query that retrieves all the question from the question table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void getAllQuestionsHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "SELECT* FROM question";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				String qid = rs.getString(1);
				String quesText = rs.getString(5);
				String authorQues = rs.getString(3);
				return new Question(qid, quesText, authorQues);

			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

			}
		});
	}
	  /**
		 * A query that inserts the exam ID to the report exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void addExamIdToReportExamTimeHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return "INSERT reportexam (`exId`) VALUES (?);";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ans) throws SQLException {

				stmt.setString(1, ans.getExamId());

			}
		});

	}

	/**
	 * this query gets the exam ID and calculate the avg of all the students who
	 * took this exam.
	 * 
	 * @param db
	 * @param key
	 */
	private void getAvgForExamByExIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return "Select FE.exId, avg(FE.grade) FROM finishedexams FE where FE.exId =?";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exId = rs.getString(1);
				Float average = rs.getFloat(2);

				return new Exam(exId, average);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getAvgForExamByExId);
				String exId = (String) params.get(0);
				stmt.setString(1, exId);

			}
		});
	}

	/**
	 * this query gets the exam ID and calculate the Medien of all the students who
	 * took this exam.
	 * 
	 * @param db
	 * @param key
	 */
	private void getMedForExamByExIdHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " select exId, avg(grade) as median from ( SELECT exId, @counter:=@counter+1 as 'row_id' , grade FROM finishedexams ,(select @counter:=0) r where exId=? order by 2) o1 INNER join ( select count(*) as total_rows from finishedexams FE where FE.exId=? ) o2 where o1.row_id in (floor((o2.total_rows + 1)/2), floor((o2.total_rows + 2)/2))";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String exId = rs.getString(1);
				Float median = rs.getFloat(2);

				return new Exam(median, exId);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getMedForExamByExId);
				String exId = (String) params.get(0);
				stmt.setString(1, exId);
				stmt.setString(2, exId);

			}
		});
	}
	  /**
		 * A query that updates the average of a specific exam to the report exam table
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateAvgForExamIdByExIdHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return " UPDATE reportexam RP SET RP.avgExam=? WHERE RP.exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ex) throws SQLException {

				stmt.setFloat(1, ex.getAvg());
				stmt.setString(2, ex.getExamId());

			}
		});

	}


	  /**
		 * A query that sets the median for a specific exam
		 *
		 * @param db - Stores database information
		 *
		 * @param key - Command operation which is performed
		 *
		 */
	private void updateMedForExamIdByExIdHandler(DbQuery db, Command key) {
		DbUpdater<Exam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<Exam>() {
			@Override
			public String getQuery() {
				return " UPDATE reportexam RP SET RP.medianExam=? WHERE RP.exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, Exam ex) throws SQLException {

				stmt.setFloat(1, ex.getMedian());
				stmt.setString(2, ex.getExamId());

			}
		});

	}

	/**
	 * this query gets the exam ID and calculate the Medien of all the students who
	 * took this exam.
	 * 
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 */
	
	private void getStudentInstructionsHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " SELECT studInst  FROM exam where exId= ?";

			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String studInst = rs.getString(1);

				return studInst;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getStudentInstructions);
				String exId = (String) params.get(0);
				stmt.setString(1, exId);

			}
		});
	}
	/**
	 * this query gets the lecturer exam instructions by the exam ID
	 * 
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 */
	private void getLecturerInstructionsHandler(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " SELECT E.lecInst" + " FROM exam E WHERE E.exId=? ";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {
				String reason = rs.getString(1);
				return reason;
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {
				ArrayList<Object> params = packet.getParameterForCommand(Command.getLecturerInstructions);
				String exId = (String) params.get(0);
				stmt.setString(1, exId);

			}
		});
	}
	/**
	 * this query gets the statistics of finished exams by the exam ID
	 * 
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 */
	private void checkStatisticsChecker(DbQuery db, Command key) {
		DbGetter dbGet = new DbGetter(db, key);
		dbGet.performAction(new ISelect() {
			@Override
			public String getQuery() {
				return " select "
						+ " (select count(*) FROM finishedexams FE  where FE.exId=? AND FE.ongoing=1) started, "
						+ " (select count(*) FROM finishedexams FE  where FE.exId=? AND FE.finished=1) finished";
			}

			@Override
			public Object createObject(ResultSet rs) throws SQLException {

				int started = rs.getInt(1);
				int finished = rs.getInt(2);
				return new ActiveExam(started, finished);
			}

			@Override
			public void setStatements(PreparedStatement stmt, Packet packet) throws SQLException {

				ArrayList<Object> params = packet.getParameterForCommand(Command.checkStatistics);
				String ExID = (String) params.get(0);
				stmt.setString(1, ExID);
				stmt.setString(2, ExID);
			}

		});

	}
	/**
	 * this query updates a specific exam to be not active
	 * 
	 * @param db - Stores database information
	 *
	 * @param key - Command operation which is performed
	 */
	private void updateAccessUpdater(DbQuery db, Command key) {
		DbUpdater<ActiveExam> dbUpdate = new DbUpdater<>(db, key);

		dbUpdate.performAction(new IUpdate<ActiveExam>() {
			@Override
			public String getQuery() {
				return " UPDATE activeexams SET ableToAccess=0 WHERE exId=? ";
			}

			@Override
			public void setStatements(PreparedStatement stmt, ActiveExam ans) throws SQLException {

				stmt.setString(1, ans.getExId());

			}
		});

	}

	/**
	 * receive the package complete commands return data to the client and status of
	 * execution if failed return exception message
	 * 
	 * @param msg
	 *            - message from client with command and data for the query
	 * @param client
	 *            - from who the message come
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Packet packet = (Packet) msg;
		printlogMsg("from: " + client + " commands: " + packet.getCommands());
		DbQuery db = new DbQuery(user, password, packet, client, database);
		try {
			db.connectToDB();
			for (Command key : packet.getCommands()) {
				switch (key) {

				case getUserByuId:
					getUserByuIdHandler(db, key);
					break;
				case getStudentByuId:
					getStudentKeyByuIdHandler(db, key);
					break;

				case getlecturerByUid:
					getlecturerByuIdHandler(db, key);
					break;

				case getUserByIdAndPass:
					getUserByIdAndPassHandler(db, key);
					break;

				case setUserLoggedInState:
					updateUserIsLoggedHandler(db, key);
					break;

				case getCoursesbyLid:
					getCoursesNamebyLidHandler(db, key);
					break;

				case getQuestion:
					getQuestionHandler(db, key);
					break;

				case addQuestion:
					addQuestionHandler(db, key);
					break;

				case getCoursesbySid:
					getCoursesNamebySidHandler(db, key);
					break;

				case getExamBySid:
					getExambySidHandler(db, key);
					break;

				case getAVGBySid:
					getAVGBySidHandler(db, key);
					break;

				case getDepartmentByLid:
					getDepartmentByLidHandler(db, key);
					break;

				case getExams:
					getExamHandler(db, key);
					break;

				case UpdateCodeExam:
					UpdateExamCOdeHandler(db, key);
					break;

				case getExamByExCode:
					getExamByExCodeHandler(db, key);
					break;

				case getQuestionByCid:
					getQuestionByCidHandler(db, key);
					break;
				case getDepartmentIDByUid:
					getDepartmentIDByUidHandler(db, key);
					break;
				case getQuestionsByExID:
					getQuestionsByExIDHandler(db, key);
					break;
				case getAllLecturers:
					getAllLecturersHandler(db, key);
					break;
				case getAllCourses:
					getAllCoursesHandler(db, key);
					break;
				case getAllStudents:
					getAllStudentsHandler(db, key);
					break;

				case AddcheckedQuestions:
					AddcheckedQuestionsHandler(db, key);
					break;

				case getCourseIDbyExamID:
					getCourseIDbyExamIDHandler(db, key);
					break;
				case addExam:
					addExamIDHandler(db, key);
					break;

				case addQuestionInExam:
					addQuestionInExamIDHandler(db, key);
					break;

				case getCountExamByCid:
					getCountExamByCidIDHandler(db, key);
					break;

				case getALLlecturersRequests:
					getgetALLlecturersRequestsHandler(db, key);
					break;

				case getLecturerReason:
					getLecturerReasonHandler(db, key);
					break;

				case getOnlineExamByLecturer:
					getOnlineExamByLecturerHandler(db, key);
					break;

				case getExamByExamIdAndSid:
					getExamByExamIdAndSidHandler(db, key);
					break;
				case getDepartmentIDByUidLecturer:
					getDepartmentIDByUidLecturerHandler(db, key);
					break;

				case getExamsBySidAndCid:
					getExamsByCidAndSidHandler(db, key);
					break;
				case addDeclineAnswerFromPrincipal:
					addDeclineAnswerFromPrincipalHandler(db, key);
					break;
				case addApproveAnswerFromPrincipal:
					addApproveAnswerFromPrincipalHandler(db, key);
					break;

				case SetActiveExam:
					SetActiveExamHandler(db, key);
					break;

				case updateExtraTime:
					updateExtraTimeHandler(db, key);
					break;

				case updateIsLocked:
					updateIsLockedHandler(db, key);
					break;
				case AddFinishedExam:
					AddFinishedExamHandler(db, key);
					break;
				case getExamTime:
					getExamTimeHandler(db, key);
					break;
				case getStudentAvg:
					getStudentAvgHandler(db, key);
					break;

				case getStudentMedian:
					getStudentMedianHandler(db, key);
					break;
				case getAvgbyCourseName:
					getAvgbyCourseNameHandler(db, key);
					break;
				case getMedianbyCourseName:
					getMedianbyCourseNameHandler(db, key);
					break;
				case getAvgOfLecturer:
					getAvgOfLecturerHandler(db, key);
					break;
				case getMedianOfLecturer:
					getMedianOfLecturerHandler(db, key);
					break;
				case getExamBySidCheckExam:
					getExamBySidCheckExHandler(db, key);
					break;
				case getStudentByCid:
					getStudentByCidHandler(db, key);
					break;

				case CheckCodeExam:
					checkCodeHandler(db, key);
					break;
				case getExtraTime:
					getExtraTimeHandler(db, key);
					break;
				case updateQuestionNote:
					updateQuestionNoteUpdater(db, key);
					break;
				case getQuestionsByExIDCheckExam:
					getQuestionsByExIDCheckExamHandler(db, key);
					break;
				case getAnsweredQuestionSidAndQid:
					getAnsweredQuestionSidAndQidHandler(db, key);
					break;
				case getGradesbySid:
					getGradesbySidHandler(db, key);
					break;

				case getExamByLecturerID:
					getExamByLecturerIDHandler(db, key);
					break;

				case addExamToChangeTime:
					addExamToChangeTimeHandler(db, key);
					break;

				case updateStudentGrade:
					updateStudentGradeUpdater(db, key);
					break;

				case updateStudentAvg:
					updateStudentAvgUpdater(db, key);
					break;

				case getLecturerUidAndCourseNamebyExamID:
					getLecturerUidAndCourseNamebyExamIDHandler(db, key);
					break;

				case updateStudentGradeInExam:
					updateStudentGradeInExamHandler(db, key);
					break;

				case getDataStudentBySid:
					getDataStudentBySidHandler(db, key);
					break;

				case getGradeCourseName:
					getGradeCourseNameHandler(db, key);
					break;

				case updateChangeGradeReason:
					updateChangeGradeReasonUpdater(db, key);
					break;

				case getFinishedExamFlagByExId:
					getFinishedExamFlagByExIdHandler(db, key);
					break;
				case checkLockedExam:
					checkLockedExamHandler(db, key);
					break;

				case uploadFile:
					uploadFileHandler(db, key);
					break;

				case addDoneExamDetails:
					addDoneExamDetailsHandler(db, key);
					break;
				case getLockedExamCourses:
					getLockedExamCoursesHandler(db, key);
					break;
				case getExamByCourseID:
					getExamByCourseIDHandler(db, key);
					break;

				case updateDateExam:
					updateDateExamUpdater(db, key);
					break;

				case updateTotalTime:
					updateTotalTimeUpdater(db, key);
					break;

				case updateExamStatistics:
					updateExamStatisticsUpdater(db, key);
					break;

				case saveCurrentGrade:
					saveCurrentGradeUpdater(db, key);
					break;
				case getFinishedQuestionsByExID:
					getFinishedQuestionsByExIDHandler(db, key);
					break;

				case getExamByExamIdAndSidManual:
					getExamByExamIdAndSidManualHandler(db, key);
					break;

				case AddFinishedExamManual:
					AddFinishedExamManualHandler(db, key);
					break;

				case updateAccess:
					updateAccessUpdater(db, key);
					break;

				case checkStatistics:
					checkStatisticsChecker(db, key);
					break;

				case UpdateCountReportCourse:
					UpdateCountReportCourseHandler(db, key);
					break;

				case getGradeCourseByCId:
					getGradeCourseByCIdHandler(db, key);
					break;

				case UpdateLecturercReport:
					UpdateLecturercReportHandler(db, key);
					break;

				case getGradeLecturerBylId:
					getGradeLecturerBylIdHandler(db, key);
					break;
				case initExam:
					initExamHandler(db, key);
					break;
				case getAllQuestions:
					getAllQuestionsHandler(db, key);
					break;
				case addExamIdToReportExamTime:
					addExamIdToReportExamTimeHandler(db, key);
					break;

				case getAvgForExamByExId:
					getAvgForExamByExIdHandler(db, key);
					break;

				case getMedForExamByExId:
					getMedForExamByExIdHandler(db, key);
					break;

				case updateAvgForExamIdByExId:
					updateAvgForExamIdByExIdHandler(db, key);
					break;

				case updateMedForExamIdByExId:
					updateMedForExamIdByExIdHandler(db, key);
					break;

				case getStudentInstructions:
					getStudentInstructionsHandler(db, key);
					break;

				case getLecturerInstructions:
					getLecturerInstructionsHandler(db, key);
					break;

				}
			}
			db.connectionClose();
		} catch (Exception e) {
			printlogMsg(e.getMessage());
			packet.setExceptionMessage(e.getMessage());
		} finally {
			try {
				db.sendToClient();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				printlogMsg(e.getMessage());
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			IPTXT.setText(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
