package Exam;

import java.io.Serializable;
/**
 * A class in Exam package, An entity for finished exams
 * @author Andrey
 *
 */
public class FinishedExam implements Serializable{
	/**
	 * Variables decelerations
	 */
	private int Sid;
	private int Cid;
	private String exID;
	private int Grade;
	private int Started;
	private int Finished;
	private String Time;
	private String reason;
	private int View;
	
/**
 * Constructor for FinishedExam
 * @param sid -Student ID (integer)
 * @param cid - Course ID (integer)
 * @param exid - Exam ID (String)
 * @param grade - Grade (integer)
 * @param started - If the student started the exam (integer)
 * @param finished- If the students finished the exam alone (integer)
 * @param time - Exam duration (String)
 */
	public FinishedExam(int sid,int cid,String exid,int grade,int started,int finished, String time) {
		this.Sid=sid;
		this.Cid=cid;
		this.exID=exid;
		this.Grade=grade;
		this.Started=started;
		this.Finished=finished;
		this.Time=time;
	}
	
	/**
	 * Constructor for FinishedExam
	 * @param sid -Student ID (integer)
	 * @param cid - Course ID (integer)
	 * @param exid - Exam ID (String)
	 * @param started - If the student started the exam (integer)
	 * @param finished- If the students finished the exam alone (integer)
	 * @param time - Exam duration (String)
	 */
	public FinishedExam(int sid,int cid,String exid,int started,int finished, String time) {
		this.Sid=sid;
		this.Cid=cid;
		this.exID=exid;
		this.Started=started;
		this.Finished=finished;
		this.Time=time;
	}
	
	/**
	 * Constructor for FinishedExam
	 * @param newGradeINT - Students grade in the exam (integer)
	 * @param exid2 - Exam ID (String)
	 * @param sId2 - Student ID (integer)
	 */
	public FinishedExam(int newGradeINT, String exid2, int sId2) {
		this.Grade=newGradeINT;
		this.exID = exid2;
		this.Sid=sId2;
	}
	
	
/**
 * Constructor for FinishedExam
 * @param grade - Students grade in the exam (integer)
 */
	public FinishedExam(int grade)
	{
		this.Grade=grade;
	}
	
	
	/**
	 * Constructor for FinishedExam
	 * @param reason - The reason for grade change (String)
	 * @param examId - Exam ID (String)
	 * @param newStudentID - Student ID (integer)
	 */
	public FinishedExam(String reason, String examId, int newStudentID) {
		this.reason=reason;
		this.exID = examId;
		this.Sid=newStudentID;
	}

	/**
	 * Constructor for FinishedExam
	 * @param exID2- Exam ID (String)
	 * @param grade2 - Grade (integer)
	 * @param view2 - View flag for student (integer)
	 */
	public FinishedExam(String exID2, int grade2, int view2) {
		this.exID=exID2;
		this.Grade=grade2;
		this.View=view2;
	}
public FinishedExam(int iD,int Cid, String exID, int start, int finish) {
		this.Sid=iD;
		this.Cid=Cid;
		this.exID=exID;
		this.Started=start;
		this.Finished=finish;
	}

/**
 * Get View flag
 * @return View (integer)
 */
	public int getView() {
		return View;
	}
/**
 * Set view flag
 * @param view - View flag (integer)
 */
	public void setView(int view) {
		View = view;
	}
/**
 * Gets the Time
 * @return Time (String)
 */
	public String getTime() {
		return Time;
	}
/**
 * Sets the time
 * @param time - Time (String)
 */
	public void setTime(String time) {
		Time = time;
	}
/**
 * Gets Started flag
 * @return Started (integer)
 */
	public int getStarted() {
		return Started;
	}
/**
 * Sets started flag
 * @param started -Started flag (integer)
 */
	public void setStarted(int started) {
		Started = started;
	}
/**
 * Get finished flag
 * @return Finished (integer)
 */
	public int getFinished() {
		return Finished;
	}
/**
 * Sets finished flag
 * @param finished - Finished flag (integer)
 */
	public void setFinished(int finished) {
		Finished = finished;
	}
/**
 * Get student ID
 * @return SID (integer)
 */
	public int getSid() {
		return Sid;
	}
/**
 * Set Student ID 
 * @param sid -Student id (integer)
 */
	public void setSid(int sid) {
		Sid = sid;
	}
/**
 * Gets Course ID
 * @return Cid (integer)
 */
	public int getCid() {
		return Cid;
	}
/**
 * Sets Course ID
 * @param cid- Course ID (integer)
 */
	public void setCid(int cid) {
		Cid = cid;
	}
/**
 * Gets Exam ID
 * @return ExId (String)
 */
	public String getExID() {
		return exID;
	}
/**
 * Sets exam ID
 * @param exID- Exam ID (String)
 */
	public void setExID(String exID) {
		this.exID = exID;
	}
/**
 * Gets grade
 * @return Grade (integer)
 */
	public int getGrade() {
		return Grade;
	}
/**
 * Sets grade
 * @param grade - Grade (integer)
 */
	public void setGrade(int grade) {
		Grade = grade;
	}
/**
 * Gets reason
 * @return Reason (String)
 */
	public String getReason() {
		return reason;
	}
/**
 * Sets reason
 * @param reason - Reason (String)
 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}