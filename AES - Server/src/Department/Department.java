package Department;


import java.io.Serializable;
import java.util.ArrayList;

import Lecturer.Lecturer;
import Student.student;

/**
 * Entity Class Contain department Details
 */
public class Department implements Serializable {
	/**
	 * Variable decelerations
	 */
	private int did;
	private String dName;
	/** Contains all students */
	private ArrayList<student> studentList;
	/** Contains all lecturers */
	private ArrayList<Lecturer> lecturerList;

	/**
	 * Constructor to initialize all instance attributes
	 * 
	 * @param id
	 *            branch id
	 * @param name
	 *            branch name (int,String)
	 */
	public Department(int did, String dName) {
		this.did = did;
		this.dName = dName;
	}

	/**
	 * Getter for get the department name
	 * 
	 * @return dName (String)
	 */
	public String getdName() {
		return dName;
	}

	/**
	 * Setter for set the department name
	 * 
	 * @param name
	 *            The department name (String)
	 */
	public void setdName(String dName) {
		this.dName = dName;
	}

	/**
	 * Get The student list of the department
	 * 
	 * @return list of student (ArrayList<student>)
	 */
	public ArrayList<student> getstudentList() {
		return studentList;
	}

	/**
	 * Set The student list in The department
	 * 
	 * @param studentList
	 *            The student list (ArrayList<student>)
	 */
	public void setstudentList(ArrayList<student> studentList) {
		this.studentList = studentList;
	}

	/**
	 * Get The lecturer list of the department
	 * 
	 * @return list of lecturer (ArrayList<lecturer>)
	 */
	public ArrayList<Lecturer> getlecturerList() {
		return lecturerList;
	}

	/**
	 * Set The lecturer list in The department
	 * 
	 * @param lecturerList
	 *            The lecturer list (ArrayList<lecturer>)
	 */
	public void setlecturerList(ArrayList<Lecturer> lecturerList) {
		this.lecturerList = lecturerList;
	}

	/**
	 * Getter for get the department id
	 * 
	 * @return department number (int)
	 */
	public int getdId() {
		return did;
	}

	/**
	 * Setter for set the department id
	 * 
	 * @param id
	 *            department id (int)
	 */
	public void setdId(int did) {
		this.did = did;
	}

	@Override
	public String toString() {
		return dName;
	}

}
