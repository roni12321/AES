package Course;

import java.io.Serializable;
import java.util.ArrayList;

import Lecturer.Lecturer;
import Student.student;

public class Course implements Serializable {

	/**
	 * Variables decelerations
	 */
	private int cid;
	private String cName;
	/** Contains all students */
	private ArrayList<student> studentList;
	/** Contains all lecturers */
	private ArrayList<Lecturer> lecturerList;
	private int  courseReport[] = new int[10];

	/**
	 * Constructor to initialize all instance attributes
	 * 
	 * @param id
	 *            branch id
	 * @param name
	 *            branch name (int,String)
	 */
	public Course(int cid, String cName) {
		this.cid = cid;
		this.cName = cName;
	}
	/**
	 * Constructor to initialize all grades(divide for 10 groups)
	 * @param arr
	 */
	 
	public Course (int arr[])
	{
		this.courseReport = arr;
	}
	
	
	/**
	 * Getter for get the all grades 
	 * @return
	 */
	public int[] getCourseReport()
	{
		return courseReport;
	}
	
	/**
	 * Setter for get the all grades 
	 * @return
	 */
	
	public void setCourseReport(int arr[])
	{
		this.courseReport = arr;

	}
	

	/**
	 * Getter for get the Course name
	 * 
	 * @return dName (String)
	 */
	public String getcName() {
		return cName;
	}

	/**
	 * Setter for set the Course name
	 * 
	 * @param name
	 *            The Course name (String)
	 */
	public void setcName(String cName) {
		this.cName = cName;
	}

	/**
	 * Get The student list of the Course
	 * 
	 * @return list of student (ArrayList<student>)
	 */
	public ArrayList<student> getstudentList() {
		return studentList;
	}

	/**
	 * Set The student list in The Course
	 * 
	 * @param studentList
	 *            The student list (ArrayList<student>)
	 */
	public void setstudentList(ArrayList<student> studentList) {
		this.studentList = studentList;
	}

	/**
	 * Get The lecturer list of the Course
	 * 
	 * @return list of lecturer (ArrayList<lecturer>)
	 */
	public ArrayList<Lecturer> getlecturerList() {
		return lecturerList;
	}

	/**
	 * Set The lecturer list in The Course
	 * 
	 * @param lecturerList
	 *            The lecturer list (ArrayList<lecturer>)
	 */
	public void setlecturerList(ArrayList<Lecturer> lecturerList) {
		this.lecturerList = lecturerList;
	}

	/**
	 * Getter for get the course id
	 * 
	 * @return course number (int)
	 */
	public int getcId() {
		return cid;
	}

	/**
	 * Setter for set the course id
	 * 
	 * @param id
	 *            course id (int)
	 */
	public void setdId(int cid) {
		this.cid = cid;
	}

	/**
	 * toString function
	 */
	@Override
	public String toString() {
		return cName;
	}

}