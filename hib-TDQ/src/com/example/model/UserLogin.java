package com.example.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * UserLogin is the persistent class for the userlogin database table.
 * @author Georgios Skourletopoulos
 * @version 2 August 2013
 */
@Entity
@Table(name = "userLogin")
public class UserLogin implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	private String nickname;	
	private String email;
	private String title;
	private String firstName;
	private String lastName;

	@ManyToOne
	@JoinColumn(name="jobId", nullable=true)
	private JobTitle jobId;

	private String companyName;
	private String location;
	private Timestamp lastLog;

	@OneToMany(mappedBy="userId", cascade=CascadeType.ALL)
	private Set<Project> projects = new HashSet<Project>();

	@OneToMany(mappedBy="userId", cascade=CascadeType.ALL)
	private Set<Cocomo> cocomoSet = new HashSet<Cocomo>();

	@OneToMany(mappedBy="userId", cascade=CascadeType.ALL)
	private Set<CostForImplementing> costImplementingSet = new HashSet<CostForImplementing>();

	@OneToMany(mappedBy="userId", cascade=CascadeType.ALL)
	private Set<TDinBuying> TDBuyingSet = new HashSet<TDinBuying>();

	public UserLogin() {}    //implicit constructor

	/**
	 * The explicit contructor that is used to create the user login object.
	 * @param nickname the user's nickname according to Google credentials
	 * @param email the user's email
	 * @param title the user's title
	 * @param firstName the user's first name
	 * @param lastName the user's last name
	 * @param jobTitle_id the user's job position ID
	 * @param companyName the user's company name
	 * @param location the user's location
	 * @param lastLog the user's last login
	 */
	public UserLogin (String nickname, String email, String title, String firstName,
			String lastName, JobTitle jobTitle_id, String companyName, String location, 
			Timestamp lastLog) {
		this.nickname = nickname;
		this.email = email;
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.jobId = jobTitle_id;		
		this.companyName = companyName;
		this.location = location;
		this.lastLog = lastLog;		
	}

	/**
	 * The explicit contructor that is used when a new user accesses the application for the
	 * first time.
	 * @param nickname the user's nickname according to Google credentials
	 * @param email the user's email
	 * @param lastLog the user's last login
	 */
	public UserLogin (String nickname, String email, Timestamp lastLog) {
		this.nickname = nickname;
		this.email = email;
		this.lastLog = lastLog;	
	}

	/**
	 * Getter for the database ID relating to the user
	 * @return this.id as Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Getter for the user's nickname
	 * @return nickname as a String
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Setter for the user's nickname
	 * @param nickname the new nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * Getter for the user's email
	 * @return email as a String
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Setter for the user's email
	 * @param email the new email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Getter for the user's title
	 * @return title as a String
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for the user's title
	 * @param title the new title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Getter for the user's first name
	 * @return firstName as a String
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Setter for the user's first name
	 * @param firstName the new first name to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Getter for the user's last name
	 * @return lastName as a String
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Setter for the user's last name
	 * @param lastName the new last name to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Getter for the user's job position
	 * @return jobTitle as a JobTitle object
	 */
	public JobTitle getJobId() {
		return jobId;
	}

	/**
	 * Setter for the user's job position
	 * @param jobTitle_id the new job position to set by ID
	 */
	public void setJobId(JobTitle jobTitle_id) {
		this.jobId = jobTitle_id;
	}

	/**
	 * Getter for the user's company name
	 * @return companyName as a String
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * Setter for the user's company name
	 * @param companyName the new company name to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * Getter for the user's location
	 * @return location as a String
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Setter for the user's location
	 * @param location the new location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Getter for the user's last login
	 * @return lastLog as a Timestamp object
	 */
	public Timestamp getLastLog() {
		return lastLog;
	}

	/**
	 * Setter for the user's last login
	 * @param lastLog the new last login to set
	 */
	public void setLastLog(Timestamp lastLog) {
		this.lastLog = lastLog;
	}

	/**
	 * Getter for the projects that the user owns
	 * @return projects as a Set
	 */
	public Set<Project> getProjects() {
		return projects;
	}

	/**
	 * Setter for the projects that the user owns
	 * @param projects the new projects to set
	 */
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}

	/**
	 * Redefining the equality between two UserLogin objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof UserLogin)) {
			return false;
		}
		final UserLogin user = (UserLogin) o;
		if (email == null) {
			return user.email == null;
		}
		return email.equalsIgnoreCase(user.email);
	}

	/**
	 * Redefining the hash code of a UserLogin object.
	 * @return email.hashCode(), the hash code of the email
	 */
	@Override
	public int hashCode() {
		if (email != null) {
			return email.hashCode();
		}
		return 0;
	}
}
