package com.example.model;

import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Scenario is the persistent class for the scenario database table.
 * @author Georgios Skourletopoulos
 * @version 4 August 2013
 */
@Entity
@Table(name = "scenario")
public class Scenario implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private BigInteger id;

	@ManyToOne
	@JoinColumn(name = "projectId")
	private Project projectId;

	private String scenarioName;
	private String scenarioType;
	private String requirementType;
	private String priority;

	public Scenario() {}    //implicit constructor

	/**
	 * The explicit contructor that is used to create the scenario object.
	 * @param projectId the project's ID
	 * @param scenarioName the scenario name
	 * @param scenarioType the scenario type
	 * @param requirementType the requirement type
	 * @param priority the priority mode
	 */
	public Scenario(Project projectId, String scenarioName, String scenarioType, 
			String requirementType, String priority) {
		this.setProjectId(projectId);
		this.scenarioName = scenarioName;
		this.scenarioType = scenarioType;
		this.requirementType = requirementType;
		this.priority = priority;
	}

	/**
	 * Getter for the scenario's ID
	 * @return this.id as a BigInteger
	 */
	public BigInteger getId() {
		return this.id;
	}

	/**
	 * Getter for the scenario's name
	 * @return scenarioName as a String
	 */
	public String getScenarioName() {
		return scenarioName;
	}

	/**
	 * Setter for the scenario's name
	 * @param scenarioName the new scenario name to set
	 */
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	/**
	 * Getter for the scenario's type
	 * @return scenarioType as a String
	 */
	public String getScenarioType() {
		return scenarioType;
	}

	/**
	 * Setter for the scenario's type
	 * @param scenarioType the new scenario type to set
	 */
	public void setScenarioType(String scenarioType) {
		this.scenarioType = scenarioType;
	}

	/**
	 * Getter for the scenario's requirement type
	 * @return requirementType as a String
	 */
	public String getRequirementType() {
		return requirementType;
	}

	/**
	 * Setter for the scenario's requirement type
	 * @param requirementType the new requirement type to set
	 */
	public void setRequirementType(String requirementType) {
		this.requirementType = requirementType;
	}

	/**
	 * Getter for the scenario's priority mode
	 * @return priority as a String
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * Setter for the scenario's priority mode
	 * @param priority the new priority mode to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * Getter for the project ID that the scenario is attached
	 * @return projectId as a Project object
	 */
	public Project getProjectId() {
		return projectId;
	}

	/**
	 * Setter for the project ID that the scenario is attached
	 * @param projectId the new project ID to set
	 */
	public void setProjectId(Project projectId) {
		this.projectId = projectId;
	}	

	/**
	 * Redefining the equality between two Scenario objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Scenario)) {
			return false;
		}
		final Scenario scenario = (Scenario) o;
		if (scenarioName == null) {
			return scenario.scenarioName == null;
		}
		else if (!projectId.equals(scenario.getProjectId()))
			return false;

		return scenarioName.equalsIgnoreCase(scenario.getScenarioName());
	}

	/**
	 * Redefining the hash code of a Scenario object.
	 * @return scenarioName.hashCode() + projectId.hashCode(), the hash code of the object
	 */
	@Override
	public int hashCode() {
		if (scenarioName != null) {
			return (scenarioName.hashCode() + projectId.hashCode());
		}
		return 0;
	}
}
