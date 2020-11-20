package Question;

import java.io.Serializable;

public class Question implements  Serializable {


	private String id ;
	private String  ques;
	private String author;
	private String notes;
	private int corrent_ans;
	private String[] pos_ans = new String [4];
	private int cid;
	private int score;
	private int ansStId;
	
	/**
	 * Constructor for a question
	 * @param id- Question id
	 * @param ques - The question itself
	 * @param author - Question author
	 * @param pos_ans - - 4 Possible answers
	 * @param corrent_ans - - 1 Correct answer
	 * (String,int ,String,String,String,String[],int)
	 */
	public Question (String id, int cid, String ques, String notes, String author, String[] pos_ans, int corrent_ans) {
		this.id = id;
		this.cid = cid;
		this.ques = ques;
		this.notes = notes;
		this.author = author;
		this.pos_ans = pos_ans;
		this.corrent_ans = corrent_ans;
	}
	
	
	/**
	 * constructor
	 * @param ques - The question itself
	 * @param author - Question author
	 * @param pos_ans - - 4 Possible answers
	 * @param corrent_ans - - 1 Correct answer
	 * (int,String,String,String,String[],int)
	 */
	public Question (int cid ,String ques, String notes, String author, String[] pos_ans, int corrent_ans) {
		
		this.cid = cid;
		this.ques = ques;
		this.notes = notes;
		this.author = author;
		this.pos_ans = pos_ans;
		this.corrent_ans = corrent_ans;
	}
	/**
	 * constructor
	  * @param id- Question id
	 * @param ques - The question itself
	 * @param author - Question author
	 */
	public Question(String id ,String ques,  String author) 
	{
		this.id = id;
		this.ques = ques;
		this.author = author;
	}
	/**
	 * constructor
	 * @param quesText - the question box 
	 * @param ans1 - first answer
	 * @param ans2 - second answer
	 * @param ans3 - third answer
	 * @param ans4 - fourth answer
	 * @param correctAns - correct answer
	 * @param score - score of question
	 */
	public Question(String quesText, String ans1, String ans2, String ans3, String ans4, int correctAns, int score, String Notes, String qid) {
		this.ques=quesText;
		this.pos_ans[0]=ans1;
		this.pos_ans[1]=ans2;
		this.pos_ans[2]=ans3;
		this.pos_ans[3]=ans4;
		this.corrent_ans=correctAns;
		this.score=score;
		this.notes=Notes;
		this.id=qid;
	}
	public Question(String quesText, String ans1, String ans2, String ans3, String ans4, int correctAns, String notes2,
			String qid, int ansStId,int score) {
		this.ques=quesText;
		this.pos_ans[0]=ans1;
		this.pos_ans[1]=ans2;
		this.pos_ans[2]=ans3;
		this.pos_ans[3]=ans4;
		this.corrent_ans=correctAns;
		this.notes=notes2;
		this.id=qid;
		this.setAnsStId(ansStId);
		this.score = score;
	}
	
	public int getAnsStId() {
		return ansStId;
	}
	
	
	/**
	 * constructor
	   * @param id- Question id
	  * @param score - score of question
	 */
	public Question(String id , int score)
	{
		
		this.id = id ;
		this.score = score;
		
	}
	
	/**
	 * get Score of question
	 * @return - score of question (int)
	 */
	public int getScore() {
		return score;
	}
	/**
	 * set Score of question
	 *@param Score (int)
	 */
	public void setScore(int Score) {
		this.score=Score;
	}
	/**
	 * set ID of question
	 * @param id (String)
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	/**
	 * get ID of question
	 * @return ID of question (String)
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * get course ID of question
	 * @return ID of question (int)
	 */
	public int getcid()
	{
		return cid;
	}
	/**
	 * set 4 options to answer as array of question
	 * @return pos_ans (String[])
	 */
	public String[] getpos_ans()
	{
		
		return pos_ans;
	}
	
	
	/**
	 * get the question text
	 * @return - the question text (String)
	 */
	public String getQues() {
		return ques;
	}
	/**
	 * set the question text
	 * @param ques the question text (String)
	 */
	public void setques(String ques) {
		this.ques = ques;
	}
	/**
	 * get the question author
	 * @return - the question author (String)
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * set the question author
	 * @param - the question author (String)
	 */
	public void setauthor(String author) {
		this.author = author;
	}
	/**
	 * get - the correct answer
	 * @return  the correct answer (int)
	 */
	public int getcurrent_ans() {
		return corrent_ans ;
	}
	/**
	 * set - the correct answer
	 * @param corrent_ans -  the correct answer (int)
	 */
	public void setTcurrent_ans(int corrent_ans) {
		this.corrent_ans = corrent_ans;
	}
	/**
	 * get - notes from the author to the student that answers to the question
	 * @return - notes from the author to the student that answers to the question (String)
	 */
	public String getnotes() {
		return notes;
	}
	/**
	 * set - notes from the author to the student that answers to the question
	 * @param notes - notes from the author to the student that answers to the question (String)
	 */
	public void setnotes(String notes) {
		this.notes = notes;
	}

	@Override
	public boolean equals(Object other)
	{
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof Question)) return false;
	    Question otherMyClass = (Question)other;
	    return otherMyClass.getId().equals(this.id);
	}
	
	
	
	
	/**
	 * The format inside the combobox : ID Question
	 */
	@Override
	public String toString() {
		return String.format("(%s) %s",id,ques);
	}

	
	public void setAnsStId(int ansStId) {
		this.ansStId = ansStId;
	}
}
