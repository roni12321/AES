package Exam;

import java.io.Serializable;
import java.util.ArrayList;

import Question.Question;

public class Exam implements Serializable {

	/**
	 * Variables decelerations
	 */

	private String examId;
	private int courseId;
	private int departmentId;
	private String examCode;
	private String timeInMin;
	private String lectureInstructions;
	private String studentInstructions;
	private String author;
	private int examDone;
	private int lecturerId;
	ArrayList<Question> questionArr;
	private float avg;
	private float median;

	/**
	 * constructor
	 * 
	 * @param examId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 * @param courseId
	 *            - int that contains 2 digit for the course
	 * @param departmentId
	 *            - int that contains 2 digit of department
	 * @param examCode
	 *            - String that contain s 4 digit code for the exam
	 * @param timeInMin
	 *            - int the contains time in minutes
	 * @param lectureInstructions
	 *            - string that contains the instruction for the lecturer
	 * @param studentInstructions
	 *            - string that contains the instruction for the student
	 * @param author
	 *            - string that contains the author's name
	 * @param examDone
	 *            - int that contains if the exam had been done or not
	 *            (String,int,int,String,String,String,String,String,int)
	 */
	public Exam(String examId, int courseId, int departmentId, String examCode, String timeInMin,
			String lectureInstructions, String studentInstructions, String author, int examDone) {
		this.examId = examId;
		this.courseId = courseId;
		this.departmentId = departmentId;
		this.examCode = examCode;
		this.timeInMin = timeInMin;
		this.lectureInstructions = lectureInstructions;
		this.studentInstructions = studentInstructions;
		this.author = author;
		this.examDone = examDone;
		this.questionArr = new ArrayList<>();

	}

	/**
	 * constructor
	 * 
	 * @param examId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 * @param courseId
	 *            - int that contains 2 digit for the course
	 * @param author
	 *            - string that contains the author's name (String,int,String)
	 */
	public Exam(String examId, int courseId, String author) {
		this.examId = examId;
		this.courseId = courseId;
		this.author = author;

	}

	/**
	 * constructor
	 * 
	 * @param examId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 * @param courseId
	 *            - int that contains 2 digit for the course
	 * @param author
	 *            - string that contains the author's name
	 * @param depId
	 *            - int that contains 2 digit of department
	 *           
	 */
	
	public Exam(String examId, int courseId, String author, String depId) {
		this.examId = examId;
		this.courseId = courseId;
		this.author = author;
		String DepId = depId;

	}

	/**
	 * constructor
	 * 
	 * @param examId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 * @param examCode
	 *            - code of Exam (String,String)
	 */

	public Exam(String examId, String examCode) {
		this.examId = examId;
		this.examCode = examCode;

	}

	/**
	 * constructor
	 * 
	 * @param courseId
	 *            - int that contains 2 digit for the course
	 * @param departmentId
	 *            - int that contains 2 digit of department
	 * @param lecturerId - lecturerId
	 * @param author
	 *            - string that contains the author's name
	 * @param examCode
	 *            - String that contain s 4 digit code for the exam
	 * @param timeInMin
	 *            - int the contains time in minutes
	 * @param lectureInstructions
	 *            - string that contains the instruction for the lecturer
	 * @param studentInstructions
	 *            - string that contains the instruction for the student
	 *            (int,int,String,String,String,String,String)
	 */

	public Exam(int courseId, int departmentId, int lecturerId ,String author, String examCode, String timeInMin,
			String lectureInstructions, String studentInstructions) {
		this.courseId = courseId;
		this.departmentId = departmentId;
		this.lecturerId = lecturerId;
		this.author = author;
		this.examCode = examCode;
		this.timeInMin = timeInMin;
		this.lectureInstructions = lectureInstructions;
		this.studentInstructions = studentInstructions;

	}

	/**
	 * constructor
	 * 
	 * @param examId
	 *            - String that contain s 4 digit code for the exam
	 * @param DepID
	 *            - int that contains 2 digit of department (String,int)
	 */
	public Exam(String examId, int DepID) {
		this.examId = examId;
		this.departmentId = DepID;
	}

	/**
	 * constructor
	 * 
	 * @param author
	 *            - string that contains the author's name
	 * @param exId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 * @param flag
	 *            - let to the sent information enter to this constructor
	 *            (String,String,int)
	 */
	public Exam(String author, String exId, int flag) {
		this.author = author;
		this.examId = exId;
		int Flag = flag;
	}

	/**
	 * constructor
	 * 
	 * @param exId
	 *            - is a string that contains the id and build from: 2 - digit of
	 *            department 2 - digit for the course 3 - digit for the exam number
	 *            (String)
	 */
	public Exam(String exId) {
		this.examId = exId;
		this.author = " ";
	}
	/**
	 * constructor
	 *
	 * @param avg - avg
	 */
	public Exam(Float avg) {
		this.examId = Float.toString(avg);
		this.author = " ";
	}

	
	
	
	public Exam(String examId , int lecturerId , int courseId)
	{
		this.examId = examId;
		this.lecturerId = lecturerId;
		this.courseId = courseId;
	}
	
	public Exam(String examId, Float avg) {
		this.examId = examId;
		this.avg = avg;
	}
	
	public Exam(Float median, String examId) {
		this.median = median;
		this.examId = examId;
	}
	/**
	 * getExamId - get the exam ID
	 * 
	 * @return - exam ID (String)
	 */
	public String getExamId() {
		return examId;
	}

	/**
	 * set -the exam ID
	 * 
	 * @param examId
	 *            - exam ID (String)
	 */
	public void setExamId(String examId) {
		this.examId = examId;
	}

	/**
	 * get - course ID
	 * 
	 * @return - course ID (int)
	 */
	public int getCourseId() {
		return courseId;
	}

	/**
	 * set - course ID
	 * 
	 * @param courseId
	 *            - course ID (int)
	 */
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	/**
	 * get - department ID
	 * 
	 * @return - department ID (int)
	 */
	public int getDepartmentId() {
		return departmentId;
	}

	/**
	 * set - department ID
	 * 
	 * @param departmentId
	 *            - department ID (int)
	 */
	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	
	/**
	 * get - lecturer ID
	 * 
	 * @return - lecturer ID (int)
	 */
	public int getLecturerId() {
		return lecturerId;
	}

	/**
	 * set - lecturer ID
	 * 
	 * @param lecturerId
	 *            - lecturer ID (int)
	 */
	public void setLecturerId(int lecturerId) {
		this.lecturerId = lecturerId;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * get exam execution code
	 * 
	 * @return - execution code (String)
	 */
	public String getExamCode() {
		return examCode;
	}

	/**
	 * set - exam execution code
	 * 
	 * @param examCode
	 *            - exam execution code (String)
	 */
	public void setExamCode(String examCode) {
		this.examCode = examCode;
	}

	/**
	 * get - Time in minutes of exam
	 * 
	 * @return - Time in minutes of exam (String)
	 */
	public String getTimeInMin() {
		return timeInMin;
	}

	/**
	 * set - Time in minutes of exam
	 * 
	 * @param timeInMin
	 *            - Time in minutes of exam (String)
	 */
	public void setTimeInMin(String timeInMin) {
		this.timeInMin = timeInMin;
	}

	/**
	 * get - lecturer instuction to the exam (for the lecturer)
	 * 
	 * @return - lecturer instuction to the exam (String)
	 */
	public String getLectureInstructions() {
		return lectureInstructions;
	}

	/**
	 * set - lecturer instuction to the exam (for the lecturer)
	 * 
	 * @param lectureInstructions
	 *            - lecturer instuction to the exam (String)
	 */
	public void setLectureInstructions(String lectureInstructions) {
		this.lectureInstructions = lectureInstructions;
	}

	/**
	 * get - lecturer instuction to the exam (for the students)
	 * 
	 * @return - lecturer instuction to the exam (for the students) (String)
	 */
	public String getStudentInstructions() {
		return studentInstructions;
	}

	/**
	 * set - lecturer instuction to the exam (for the students)
	 * 
	 * @param studentInstructions
	 *            - lecturer instuction to the exam (for the students) (String)
	 */
	public void setStudentInstructions(String studentInstructions) {
		this.studentInstructions = studentInstructions;
	}

	/**
	 * get - Author name
	 * 
	 * @return - Author name (String)
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * set - Author name
	 * 
	 * @param author
	 *            - Author name (String)
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * get Flag - if the exam has been done or not
	 * 
	 * @return -Flag - if the exam has been done or not (int)
	 */
	public int getExamDone() {
		return examDone;
	}

	/**
	 * set - Flag - if the exam has been done or not
	 * 
	 * @param examDone
	 *            - Flag - if the exam has been done or not (int)
	 */
	public void setExamDone(int examDone) {
		this.examDone = examDone;
	}

	/**
	 * get all the questions in the exam
	 * 
	 * @return - all the question in the exam 
	 */
	public ArrayList<Question> getQuestionArr() {
		return questionArr;
	}

	/**
	 * all the questions in the exam
	 * 
	 * @param questionArr
	 *            - all the question in the exam 
	 */
	public void setQuestionArr(ArrayList<Question> questionArr) {
		this.questionArr = questionArr;
	}

	/**
	 * The format inside the combobox : Exam ID and author
	 */
	@Override
	public String toString() {

		return String.format("%s %s", examId, author);
	}
	/**
	 * 
	 * @return get avarage.
	 */
	public float getAvg() {
		return avg;
	}
	/**
	 * set avarage
	 * @param avg - avg
	 */
	public void setAvg(float avg) {
		this.avg = avg;
	}
	/**
	 * getMedian
	 * @return median - median
	 */
	public float getMedian() {
		return median;
	}
	/**
	 * set median
	 * @param median - median
	 */
	public void setMedian(float median) {
		this.median = median;
	}
}
