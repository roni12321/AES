package Lecturer;

import java.io.Serializable;

public class QuestionInExam implements Serializable {

	/**
	 * variables declaration
	 */

	private String queId;
	private String examId;
	private int score;

	/**
	 * constructor
	 * 
	 * @param queId
	 *            - question ID
	 * @param examId
	 *            - exam ID
	 * @param score
	 *            - the question score (String, String, int)
	 */
	public QuestionInExam(String queId, String examId, int score) {
		this.queId = queId;
		this.examId = examId;
		this.score = score;
	}

	/**
	 * constructor
	 * 
	 * @param queId
	 *            - question ID
	 * @param score
	 *            - the question score (String, int)
	 */
	public QuestionInExam(String queId, int score) {
		this.queId = queId;
		this.score = score;
	}

	/**
	 * get the question ID
	 * 
	 * @return - the question ID (String)
	 */
	public String getQueId() {
		return queId;
	}

	/**
	 * set - question ID
	 * 
	 * @param queId
	 *            - the question ID (String)
	 */
	public void setQueId(String queId) {
		this.queId = queId;
	}

	/**
	 * get the exam ID
	 * 
	 * @return - the exam ID (String)
	 */
	public String getExamId() {
		return examId;
	}

	/**
	 * set - exam ID
	 * 
	 * @param examId
	 *            - the exam ID (String)
	 */
	public void setExamId(String examId) {
		this.examId = examId;
	}

	/**
	 * get - the score of the question
	 * 
	 * @return - the score of the question (int)
	 */
	public int getScore() {
		return score;
	}

	/**
	 * set - the score of the question
	 * 
	 * @param  score - the score of the question (int)
	 */
	public void setScore(int score) {
		this.score = score;
	}

}
