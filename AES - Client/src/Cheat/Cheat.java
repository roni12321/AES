package Cheat;

import java.io.Serializable;

/**
 * A class in lecturer package, Entity for cheat checking.
 * 
 * @author Andrey
 *
 */
public class Cheat implements Serializable {
	/**
	 * Variables decelerations
	 */
	private int sID;
	private String Answers;
	private int Count;
/**
 *  Constructor for the Cheat entity
 * @param SID - Student ID (Integer)
 * @param answers - The answers the student selected in the exam (String)
 */
	public Cheat(int SID, String answers) {
		this.sID = SID;
		this.Answers = answers;
	}
/**
 * Constructor for the Cheat entity
 * @param parseInt- Students ID (Integer)
 * @param string - The answers the student selected in the exam (String)
 * @param i - Counts how many students answered the same way (Integer)
 */
	public Cheat(int parseInt, String string, int i) {
		this.sID = parseInt;
		this.Answers = string;
		this.Count = i;
	}

	/**
	 * Get count
	 * @return count (Integer)
	 */
	public int getCount() {
		return Count;
	}
/**
 * Set count
 * @param count (Integer)
 */
	public void setCount(int count) {
		Count = count;
	}
/**
 * Get Student ID
 * @return sID (Integer)
 */
	public int getsID() {
		return sID;
	}
/**
 * Set student ID
 * @param sID (Integer)
 */
	public void setsID(int sID) {
		this.sID = sID;
	}
/**
 * Gets the Answers
 * @return Answers (String)
 */
	public String getAnswers() {
		return Answers;
	}
/**
 * Sets the Answers
 * @param answers (string)
 */
	public void setAnswers(String answers) {
		Answers = answers;
	}
}
