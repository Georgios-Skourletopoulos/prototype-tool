package com.example.model;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * SharedProject is the persistent class for the sharedproject database table.
 * @author Georgios Skourletopoulos
 * @version 6 August 2013
 */
@Entity
@Table(name="sharedProject")
public class SharedProject {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	@ManyToOne
	@JoinColumn(name = "fromUserId")
	private UserLogin fromUserId;

	@ManyToOne
	@JoinColumn(name = "toUserId")
	private UserLogin toUserId;

	@ManyToOne
	@JoinColumn(name = "projectId")
	private Project projectId;

	private Timestamp dateShared;

	public SharedProject() {}    //implicit constructor

	/**
	 * The explicit contructor that is used to create the shared project object.
	 * @param fromUserId the sender's ID
	 * @param toUserId the receiver's ID
	 * @param projectId the project ID
	 * @param dateShared the sharing date
	 */
	public SharedProject(UserLogin fromUserId, UserLogin toUserId, Project projectId, Timestamp dateShared) {
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.projectId = projectId;
		this.dateShared = dateShared;
	}

	/**
	 * Getter for the project's sharing date
	 * @return dateShared as a Timestamp object
	 */
	public Timestamp getDateShared() {
		return dateShared;
	}

	/**
	 * Getter for the receiver's ID
	 * @return toUserId as a UserLogin object
	 */
	public UserLogin getToUserId() {
		return toUserId;
	}

	/**
	 * Getter for the sender's ID
	 * @return fromUserId as a UserLogin object
	 */
	public UserLogin getFromUserId() {
		return fromUserId;
	}

	/**
	 * Getter for the project's ID
	 * @return projectId as a Project object
	 */
	public Project getProjectId() {
		return projectId;
	}

	/**
	 * Redefining the equality between two SharedProject objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SharedProject)) {
			return false;
		}
		final SharedProject sp = (SharedProject) o;
		if (!fromUserId.equals(sp.getFromUserId())) {
			return false;
		}
		else if (!toUserId.equals(sp.getFromUserId()))
			return false;
		else if (!projectId.equals(sp.getProjectId()))
			return false;
		return true;
	}

	/**
	 * Redefining the hash code of a SharedProject object.
	 * @return projectId.hashCode() + fromUserId.hashCode() + toUserId.hashCode(), the hash code of the object
	 */
	@Override
	public int hashCode() {
		if (projectId != null) {
			return (projectId.hashCode() + fromUserId.hashCode() + toUserId.hashCode());
		}
		return 0;
	}
}
