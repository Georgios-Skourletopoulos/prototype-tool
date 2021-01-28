package com.example.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * EMF is a class that deals with creating a singleton for the EntityManagerFactory.
 * @author Georgios Skourletopoulos, by adapting code from Google (2012), "Getting Started with JPA
 * facets and Cloud SQL", https://developers.google.com/appengine/articles/using_jpa_tool [accessed 9 Aug 2013]
 * @version 1 August 2013
 */
public class EMF {

	/**
	 * For running the application.
	 */
	private static final EntityManagerFactory emfInstance = Persistence
			.createEntityManagerFactory("transactions-optional");    //Singleton EMF field

	/**
	 * For running JUnit tests.
	 */
	// private static final EntityManagerFactory emfInstance = Persistence
	//		    .createEntityManagerFactory("testing"); 

	private EMF() {}    //the constructor

	/**
	 * Getter for singleton.
	 * @return emfInstance is the result
	 */
	public static EntityManagerFactory get() {
		return emfInstance;
	}
}
