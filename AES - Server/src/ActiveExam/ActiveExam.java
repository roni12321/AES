package ActiveExam;

import java.io.Serializable;

public class ActiveExam implements Serializable {

	/**
	 * Variables decelerations
	 */

	private String exId;
	private int lId;
	private int activeExam;
	private int lectureRequestFlag;
	private String lectureReques;
	private int principalAnswerFlag;
	private String principalAnswer;
	private int extraTime;
	private int started;
	private int finished;

	/**
	 * constructor for active exam 
	 * @param exId
	 *            - Exam ID (String)
	 * @param lId
	 *            - Lecturer ID (int)
	 * @param activeExam
	 *            - Flag - (1 - active exam , 0 - not active exam) (int)
	 * @param lectureRequestFlag
	 *            - Flag -( 1 - the lecturer send request for adding time to the
	 *            principal, 0 - the lecturer didnt send request for adding time to
	 *            the principal) (int)
	 * @param lectureReques
	 *            - The request from the lecturer (String)
	 * @param principalAnswerFlag
	 *            - Flag - (1 - the principal answered to the lecturer request, 0 -
	 *            the principal didnt answer to the lecturer request) (int)
	 * @param principalAnswer
	 *            - The principal answer (String)
	 * @param extraTime
	 *            - the extra time that the lecturer wants in minutes (int)
	 */
	public ActiveExam(String exId, int lId, int activeExam, int lectureRequestFlag, String lectureReques,
			int principalAnswerFlag, String principalAnswer, int extraTime) {
		this.exId = exId;
		this.lId = lId;
		this.activeExam = activeExam;
		this.lectureRequestFlag = lectureRequestFlag;
		this.lectureReques = lectureReques;
		this.principalAnswerFlag = principalAnswerFlag;
		this.principalAnswer = principalAnswer;
		this.extraTime = extraTime;
	}

	/**
	 * constructor for active exam
	 * @param exId
	 *            - Exam ID (String)
	 * @param lId
	 *            - Lecturer ID (int)
	 * @param activeExam
	 *            - Flag - (1 - active exam , 0 - not active exam) (int)
	 * @param lectureRequestFlag
	 *            - Flag -( 1 - the lecturer send request for adding time to the
	 *            principal, 0 - the lecturer didnt send request for adding time to
	 *            the principal) (int)
	 * @param principalAnswerFlag
	 *            - Flag - (1 - the principal answered to the lecturer request, 0 -
	 *            the principal didnt answer to the lecturer request) (int)
	 * @param extraTime
	 *            - the extra time that the lecturer wants in minutes (int)
	 */
	public ActiveExam(String exId, int lId, int activeExam, int lectureRequestFlag, int principalAnswerFlag,
			int extraTime) {
		this.exId = exId;
		this.lId = lId;
		this.activeExam = activeExam;
		this.lectureRequestFlag = lectureRequestFlag;
		this.principalAnswerFlag = principalAnswerFlag;
		this.extraTime = extraTime;
	}

/**
 * constructor for active exam
 * @param examId -Exam ID (String)
 */
	public ActiveExam(String examId) {
		// TODO Auto-generated constructor stub
		this.exId=examId;
	}
/**
 * constructor for active exam
 * @param examId
 * @param examId2
 */
	public ActiveExam(String examId, String examId2) {
		this.exId=examId;
		this.exId=examId2;
	}
/**
 * constructor for active exam
 * @param started - Started Count (integer)
 * @param finished - Finished count (integer)
 */
	public ActiveExam(int started, int finished) {
		this.started=started;
		this.finished=finished;
	}

	/**
	 * get exam ID
	 * 
	 * @return - exam ID (String)
	 */
	public String getExId() {
		return exId;
	}

	/**
	 * set exam ID
	 * 
	 * @param exId
	 *            - exam ID (String)
	 */
	public void setExId(String exId) {
		this.exId = exId;
	}

	/**
	 * get lecturer ID
	 * 
	 * @return - lecturer ID (int)
	 */
	public int getlId() {
		return lId;
	}

	/**
	 * set lecturer ID
	 * 
	 * @param lId
	 *            - lecturer ID (int)
	 */
	public void setlId(int lId) {
		this.lId = lId;
	}

	/**
	 * get active exam
	 * 
	 * @return - active exam (int)
	 */
	public int getActiveExam() {
		return activeExam;
	}

	/**
	 * set active exam
	 * 
	 * @param activeExam
	 *            - active exam (int)
	 */
	public void setActiveExam(int activeExam) {
		this.activeExam = activeExam;
	}

	/**
	 * get lecturer request flag
	 * 
	 * @return - request flag (int)
	 */
	public int getLectureRequestFlag() {
		return lectureRequestFlag;
	}

	/**
	 * set lecturer request flag
	 * 
	 * @param lectureRequestFlag
	 *            - lecturer request flag (int)
	 */
	public void setLectureRequestFlag(int lectureRequestFlag) {
		this.lectureRequestFlag = lectureRequestFlag;
	}

	/**
	 * get lecturer request
	 * 
	 * @return - lecturer request (String)
	 */
	public String getLectureReques() {
		return lectureReques;
	}

	/**
	 * set lecturer request
	 * 
	 * @param lectureReques
	 *            - lecturer request(String)
	 */
	public void setLectureReques(String lectureReques) {
		this.lectureReques = lectureReques;
	}

	/**
	 * get principal answer flag
	 * 
	 * @return - principal answer flag (int)
	 */
	public int getPrincipalAnswerFlag() {
		return principalAnswerFlag;
	}

	/**
	 * set principal answer flag
	 * 
	 * @param principalAnswerFlag
	 *            - principal answer flag (int)
	 */
	public void setPrincipalAnswerFlag(int principalAnswerFlag) {
		this.principalAnswerFlag = principalAnswerFlag;
	}

	/**
	 * get principal answer
	 * 
	 * @return - principal answer (String)
	 */
	public String getPrincipalAnswer() {
		return principalAnswer;
	}

	/**
	 * set principal answer
	 * 
	 * @param principalAnswer
	 *            - principal answer (String)
	 */
	public void setPrincipalAnswer(String principalAnswer) {
		this.principalAnswer = principalAnswer;
	}

	/**
	 * get extra time in minutes
	 * 
	 * @return - extra time in minutes (int)
	 */
	public int getExtraTime() {
		return extraTime;
	}

	/**
	 * set extra time in minutes
	 * 
	 * @param extraTime
	 *            - extra time in minutes (int)
	 */
	public void setExtraTime(int extraTime) {
		this.extraTime = extraTime;
	}
/**
 * get started count
 * @return started (integer)
 */
	public int getStarted() {
		return started;
	}
/**
 * set started count
 * @param started (integer)
 */
	public void setStarted(int started) {
		this.started = started;
	}
/**
 * get finished count
 * @return finished (integer)
 */
	public int getFinished() {
		return finished;
	}
/**
 * set finished count
 * @param finished (integer)
 */
	public void setFinished(int finished) {
		this.finished = finished;
	}

}
