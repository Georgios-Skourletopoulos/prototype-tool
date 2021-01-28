package com.example.model;

import java.io.Serializable;
import java.sql.Date;
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
 * Project is the persistent class for the project database table.
 * @author Georgios Skourletopoulos
 * @version 4 August 2013
 */
@Entity
@Table(name = "project")
public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "userId")
	private UserLogin userId;

	private String projectName;
	private String projectCategory;
	private String projectGoals;
	private Date projectStart;
	private Date projectEnd;

	@OneToMany(mappedBy="projectId", cascade=CascadeType.ALL)
	private Set<Scenario> scenarios = new HashSet<Scenario>();    //all the scenarios attached to a project are stored to a HashSet

	public Project() {}    //implicit constructor

	/**
	 * The explicit contructor that is used to create the project object.
	 * @param userId each user's ID (each user has a unique ID)
	 * @param projectName the project name
	 * @param projectCategory the project category
	 * @param projectGoals the project goals
	 * @param projectStart the project start date
	 * @param projectEnd the project end date
	 */
	public Project(UserLogin userId, String projectName, String projectCategory, String projectGoals,
			Date projectStart, Date projectEnd) {
		this.userId = userId;
		this.projectName = projectName;
		this.projectCategory = projectCategory;
		this.projectGoals = projectGoals;
		this.projectStart = projectStart;
		this.projectEnd = projectEnd;
	}

	/**
	 * Getter for the project's ID
	 * @return id as Long
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Getter for the project's name
	 * @return projectName as a String
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * Setter for the project's name
	 * @param projectName the new project name to set
	 */
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Getter for the project's category
	 * @return projectCategory as a String
	 */
	public String getProjectCategory() {
		return projectCategory;
	}

	/**
	 * Setter for the project's category
	 * @param projectCategory the new project category to set
	 */
	public void setProjectCategory(String projectCategory) {
		this.projectCategory = projectCategory;
	}

	/**
	 * Getter for the project goals
	 * @return projectGoals as a String
	 */
	public String getProjectGoals() {
		return projectGoals;
	}

	/**
	 * Setter for the project goals
	 * @param projectGoals the new project goals to set
	 */
	public void setProjectGoals(String projectGoals) {
		this.projectGoals = projectGoals;
	}

	/**
	 * Getter for the project's start date
	 * @return projectStart as a Date object
	 */
	public Date getProjectStart() {
		return projectStart;
	}

	/**
	 * Setter for the project's start date
	 * @param projectStart the new project start date to set
	 */
	public void setProjectStart(Date projectStart) {
		this.projectStart = projectStart;
	}

	/**
	 * Getter for the project's end date
	 * @return projectEnd as a Date object
	 */
	public Date getProjectEnd() {
		return projectEnd;
	}

	/**
	 * Setter for the project's end date
	 * @param projectEnd the new project end date to set
	 */
	public void setProjectEnd(Date projectEnd) {
		this.projectEnd = projectEnd;
	}

	/**
	 * Getter for the attached scenarios of a project
	 * @return scenarios as a Set
	 */
	public Set<Scenario> getScenarios() {
		return scenarios;
	}

	/**
	 * Setter for the attached scenarios of a project
	 * @param scenarios the new scenarios to set
	 */
	public void setScenarios(Set<Scenario> scenarios) {
		this.scenarios = scenarios;
	}

	/**
	 * Getter for the user that owns that project by his ID
	 * @return userId as a UserLogin object
	 */
	public UserLogin getUserId() {
		return userId;
	}

	/**
	 * Redefining the equality between two Project objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Project)) {
			return false;
		}
		final Project project = (Project) o;
		if (projectName == null) {
			return project.projectName == null;
		}
		else if (!userId.equals(project.getUserId()))
			return false;

		return projectName.equalsIgnoreCase(project.projectName);
	}

	/**
	 * Redefining the hash code of a Project object.
	 * @return projectName.hashCode() + userId.hashCode(), the hash code of the object
	 */
	@Override
	public int hashCode() {
		if (projectName != null) {
			return (projectName.hashCode() + userId.hashCode());
		}
		return 0;
	}
}
