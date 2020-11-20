package Users;

import java.io.Serializable;

/***
 * 
 * Entity class to define a user in the system
 *
 */
public class User implements Serializable {

	/**
	 * user id
	 */
	private int uId;
	/**
	 * user name for log in
	 */
	private String username;
	/**
	 * password for log in
	 */
	private String password;
	/**
	 * state is logged in
	 */
	private boolean isLogged;
	/**
	 * permission of the user
	 */
	private Permission permission;
	/**
	 * id for user
	 */
	private String id;

	/***
	 * 
	 * @return user's permission
	 */
	public Permission getPermission() {
		return permission;
	}

	/***
	 * 
	 * @param permission
	 *            to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	/***
	 * 
	 * @return user id
	 */
	public int getuId() {
		return uId;
	}

	/***
	 * 
	 * @param uId
	 *            to set
	 */
	public void setuId(int uId) {
		this.uId = uId;
	}

	/***
	 * 
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/***
	 * 
	 * @param id
	 *            to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/***
	 * 
	 * @return user name
	 */
	public String getUsername() {
		return username;
	}

	/***
	 * 
	 * @param username -  to set
	 *            
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/***
	 * 
	 * @return user's password
	 */
	public String getPassword() {
		return password;
	}

	/***
	 * 
	 * @param password
	 *            to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/***
	 * 
	 * @return the logged state
	 */
	public boolean isLogged() {
		return isLogged;
	}

	/***
	 * 
	 * @param isLogged
	 *            to set
	 */
	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}

	/***
	 * Constructor for the server side use
	 * 
	 * @param uId
	 *            user id
	 * @param id - id
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @param isLogged
	 *            is online
	 * @param permission
	 *            permission
	 */
	public User(int uId, String id, String username, String password, boolean isLogged, Permission permission) {
		super();
		this.uId = uId;
		this.id = id;
		this.username = username;
		this.password = password;
		this.isLogged = isLogged;
		this.permission = permission;
	}

	/***
	 * Constructor for the client side use
	 * 
	 * @param id - id
	 * @param username
	 *            user name
	 * @param password
	 *            password
	 * @param isLogged
	 *            is online
	 * @param permission
	 *            permission
	 */
	public User(String id, String username, String password, boolean isLogged, Permission permission) {
		super();
		this.username = username;
		this.password = password;
		this.isLogged = isLogged;
		this.permission = permission;
	}

	/***
	 * Minimal constructor
	 * 
	 * @param id - id
	 * @param password
	 *            password
	 */
	public User(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}

	/***
	 * Initialize only uId
	 * 
	 * @param uId
	 *            user id
	 */
	public User(int uId) {
		super();
		this.uId = uId;
	}

	/***
	 * Copy Constructor
	 * 
	 * @param user
	 *            user name
	 */
	public User(User user) {
		this(user.uId, user.id, user.username, user.password, user.isLogged, user.permission);
	}

	public User(int uId, String username) {
		this.uId = uId;
		this.username = username;
	}
}