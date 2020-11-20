package Answer;

import java.io.Serializable;

public class Answer implements Serializable {
	
	/**
	 * Variables decelerations
	 */
	
private String Qid;
private int Answer;
private int Grade;





/**
 * constructor for  answer
 * @param Qid - the question id (string)
 * @param Answer -  the answer provided by the user(integer)
 */
public Answer(String Qid,int Answer) {
	this.Qid=Qid;
	this.Answer=Answer;
}
/**
 * Constructor for answer
 * @param Qid- the question id (string)
 */
public Answer(String Qid) {
	this.Qid=Qid;
}



/**
 * Get the Grade
 * @return grade (integer)
 */
public int getGrade() {
	return Grade;
}
/**
 * Sets the grade
 * @param grade (integer)
 */
public void setGrade(int grade) {
	this.Grade=grade;
}
/**
 * Gets the questions ID
 * @return Questions ID (String)
 */
public String getQid() {
	return Qid;
}
/**
 * Sets the question ID
 * @param qid (String)
 */
public void setQid(String qid) {
	this.Qid=qid;
}
/**
 * Gets the answer
 * @return answer (integer)
 */
public int getAnswer() {
	return Answer;
}
/**
 * Sets teh answer
 * @param answer (integer)
 */
public void setAnswer(int answer){
	this.Answer=answer;
	}
}
