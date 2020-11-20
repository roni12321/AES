package Question;

import java.io.Serializable;
/**
 * A class in the Question package, An entity for finished questions by the student
 * @author Andrey
 *
 */
public class FinishedQuestion implements Serializable {
	/**
	 * variables 
	 */
	private int Sid;
	private int Cid;
	private String exID;
	private String qId;
	private int ansStId;
	private int corAns;
	private String Notes;
	private int Grade;

	/**
	 * Constructor for Finished question
	 * @param Sid -Student ID (integer)
	 * @param Cid - Course ID (integer)
	 * @param exID - Exam ID (String)
	 * @param Grade - Grade (integer)
	 * @param Notes - Notes (String)
	 */
	public FinishedQuestion(int Sid, int Cid, String exID, int Grade, String Notes) {
		this.Sid = Sid;
		this.Cid = Cid;
		this.exID = exID;
		this.Grade = Grade;
		this.Notes = Notes;
	}
/**
 * Constructor for Finished question
	 * @param exID - Exam ID (String)
	 * @param grade - Grade (integer)
 */
	public FinishedQuestion(String exID, int grade) {
		this.exID = exID;
		this.Grade = grade;
	}
/**
 * Constructor for Finished question
 * @param studentID - Student ID (integer)
 * @param cID	- Course ID (integer)
 * @param exID	- Exam ID (String)
 * @param qid	- Question ID (String)
 * @param answer	- Student answer (integer)
 * @param grade - Grade (integer)
 */
	public FinishedQuestion(int studentID, int cID, String exID, String qid, int answer, int grade) {
		this.Sid = studentID;
		this.Cid = cID;
		this.exID = exID;
		this.qId = qid;
		this.ansStId = answer;
		this.Grade = grade;
	}
/**
 * Constructor for Finished question
 * @param qId2 - Question ID (String)
 * @param ansStId2 - Student answer (integer)
 * @param notes2 - Notes (String)
 * @param grade2 - Grade (integer)
 */
	public FinishedQuestion(String qId2, int ansStId2, String notes2, int grade2) {
		this.qId = qId2;
		this.ansStId = ansStId2;
		this.Notes = notes2;
		this.Grade = grade2;
	}
/**
 * Constructor for Finished question
 * @param Notes - Notes (String)
 * @param exID - Exam ID (String)
 * @param qId - Question ID (String)
 * @param Sid- Student ID (integer)
 */
	public FinishedQuestion(String Notes, String exID, String qId, int Sid) {
		this.Notes = Notes;
		this.exID = exID;
		this.qId = qId;
		this.Sid = Sid;
	}
/**
 * Constructor for Finished question
 * @param qId - Question ID (String)
 * @param ansStId - Student answer (integer)
 * @param corAns - correct answer (integer)
 * @param notes - Notes (String)
 * @param grade - Grade (integer)
 */
	public FinishedQuestion(String qId, int ansStId, int corAns, String notes, int grade) {

		this.qId = qId;
		this.ansStId = ansStId;
		this.corAns = corAns;
		this.Notes = notes;
		this.Grade = grade;
	}
	/**
	 * Get question ID
	 * @return qId (String)
	 */
	public String getqId() {
		return qId;
	}
/**
 * Set question ID
 * @param qID (String)
 */
	public void setqId(String qID) {
		this.qId = qID;
	}
/**
 * Get correct answer
 * @return corAns (integer)
 */
	public int getcorAns() {
		return corAns;
	}
/**
 * Set correct answer
 * @param corAns (integer)
 */
	public void setcorAns(int corAns) {
		this.corAns = corAns;
	}
/**
 * Get student answer
 * @return ansStId
 */
	public int getansStId() {
		return ansStId;
	}
/**
 * Set student answer
 * @param ansStId (integer)
 */
	public void setansStId(int ansStId) {
		this.ansStId = ansStId;
	}
/**
 * Get student ID
 * @return Sid (integer)
 */
	public int getSid() {
		return Sid;
	}
/**
 * Set student ID
 * @param sid (integer)
 */
	public void setSid(int sid) {
		this.Sid = sid;
	}
/**
 * Get course ID
 * @return Cid (integer)
 */
	public int getCid() {
		return Cid;
	}
/**
 * Set course ID
 * @param cid (integer)
 */
	public void setCid(int cid) {
		this.Cid = cid;
	}
/**
 * Get exam ID
 * @return exID (String)
 */
	public String getexID() {
		return exID;
	}
/**
 * Set exam ID
 * @param exid (String)
 */
	public void setexID(String exid) {
		this.exID = exid;
	}
/**
 * Get grade
 * @return Grade (integer)
 */
	public int getGrade() {
		return Grade;
	}
/**
 * Set grade
 * @param grade (integer)
 */
	public void setGrade(int grade) {
		this.Grade = grade;
	}
/**
 * Get notes
 * @return Notes (String)
 */
	public String getNotes() {
		return Notes;
	}
/**
 * Set notes
 * @param notes (String)
 */
	public void setNotes(String notes) {
		this.Notes = notes;
	}

}
