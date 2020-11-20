package Lecturer;

import java.io.Serializable;

import Department.Department;
import Users.User;

/**
 * Entity class that contains the lecturer details
 *
 */
public class Lecturer extends User implements Serializable {
	/** lecturer id */
	private int lId;

	/** The department of lecturer */
	private int dId;

	/**
	 * constructor
	 * 
	 * @param uId
	 *            - user ID
	 * @param name
	 *            - mame of the user (int,String)
	 */
	public Lecturer(int uId, String name) {
		super(uId, name);
	}

	/**
	 * Constructor that initialize instance attributes of lecturer
	 * 
	 * @param uId - user Id
	 *            
	 * @param id - id
	 * @param username - user name
	 *            
	 * @param password - password
	 *            
	 * @param isLogged - user is loged
	 *            
	 * @param permission - user permission
	 *            
	 * @param lId - lecturer id
	 * 
	 * @param did - did
	 *            
	 *            
	 */
	public Lecturer(int uId, String id, String username, String password, boolean isLogged, Users.Permission permission,
			int lId, int did) {
		super(uId, id, username, password, isLogged, permission);
		// TODO Auto-generated constructor stub
		setlId(lId);
		setdId(did);

	}

	/**
	 * Constructor that initialize instance attributes of lecturer
	 * 
	 * @param user
	 *            user name
	 * @param lId
	 *            lecturer id (User,int)
	 */
	public Lecturer(User user, int lId) {
		super(user);
		setlId(lId);

	}

	/**
	 * Constructor that initialize instance attributes of lecturer
	 * 
	 * @param uId
	 *            user id
	 * @param lId
	 *            lecturer id (int,int)
	 */
	public Lecturer(int uId, int lId) {
		super(uId);
		setlId(lId);

	}

	/**
	 * Getter for get the department id
	 * 
	 * @return department number (int)
	 */
	public int getdId() {
		return dId;
	}

	/**
	 * Setter for set the department ID
	 * 
	 * @param dId
	 *            department id (int)
	 */
	public void setdId(int dId) {
		this.dId = dId;
	}

	/**
	 * Getter of get the lecturer id
	 * 
	 * @return lecturer ID (int)
	 */
	public int getlId() {
		return lId;
	}

	/**
	 * Setter for set the lecturer ID
	 * 
	 * @param lId
	 *            to set lecturer ID (int)
	 */
	public void setlId(int lId) {
		this.lId = lId;
	}

	/**
	 * toString - return the format - "(%s) %d"
	 */
	public String toString() {
		return String.format("(%s) %d", getUsername(), getuId());
	}

}