package com.example.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * JobTitle is the persistent class for the jobtitle database table.
 * @author Georgios Skourletopoulos
 * @version 4 August 2013
 */
@Entity
@Table(name = "jobTitle")
public class JobTitle implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;

	@OneToMany(mappedBy="jobId", cascade=CascadeType.ALL)
	private Set<UserLogin> users = new HashSet<UserLogin>();    //the users with a specific job position are strored in a HashSet

	public JobTitle() {}    //the implicit constructor

	/**
	 * Getter for the job's name
	 * @return name as a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter for the job's name
	 * @param name the new name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the job's ID
	 * @return id as Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Getter for the users with the same job position
	 * @return users as a Set
	 */
	public Set<UserLogin> getUsers() {
		return users;
	}

	/**
	 * Setter for the users with the same job position
	 * @param users the new users to set
	 */
	public void setUsers(Set<UserLogin> users) {
		this.users = users;
	}

	/**
	 * Redefining the equality between two JobTitle objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JobTitle)) {
			return false;
		}
		final JobTitle jobTitle = (JobTitle) o;
		if (name == null) {
			return jobTitle.name == null;
		}

		return name.equalsIgnoreCase(jobTitle.name);
	}

	/**
	 * Redefining the hash code of a JobTitle object.
	 * @return name.hashCode(), the hash code of the object
	 */
	@Override
	public int hashCode() {
		if (name != null) {
			return (name.hashCode());
		}
		return 0;
	}
}
