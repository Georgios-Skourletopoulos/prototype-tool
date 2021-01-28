/**
 * This class was automatically generated by using the new JPA Entities from Tables.
 */
package com.example.model;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;

/**
 * SharedCocomo is the persistent class for the sharedcocomo database table.
 * @author Georgios Skourletopoulos
 * @version 4 August 2013
 */
@Entity
@NamedQuery(name="Sharedcocomo.findAll", query="SELECT s FROM SharedCocomo s")
public class SharedCocomo implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private String id;

	@ManyToOne
	@JoinColumn(name="cocomoId")
	private Cocomo cocomoId;

	private Timestamp dateShared;

	@ManyToOne
	@JoinColumn(name="fromUserId")    //uni-directional many-to-one association to Userlogin
	private UserLogin fromUserId;

	@ManyToOne
	@JoinColumn(name="toUserId")    //uni-directional many-to-one association to Userlogin
	private UserLogin toUserId;

	public SharedCocomo() {}    //implicit constructor

	/**
	 * The explicit contructor that is used to create the shared COCOMO estimate object.
	 * @param fromUserId the sender's ID
	 * @param toUserId the receiver's ID
	 * @param cocomoId the estimation ID
	 * @param dateShared the sharing date
	 */
	public SharedCocomo(UserLogin fromUserId, UserLogin toUserId, Cocomo cocomoId, Timestamp dateShared) {
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.cocomoId = cocomoId;
		this.dateShared = dateShared;
	}

	/**
	 * Getter for the sharing ID (transaction - insert ID)
	 * @return this.id as a String
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Setter for the sharing ID (transaction - insert ID)
	 * @param id the new ID to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Getter for the estimation's ID
	 * @return this.cocomoId as a Cocomo object
	 */
	public Cocomo getCocomoId() {
		return this.cocomoId;
	}

	/**
	 * Setter for the estimation's ID
	 * @param cocomoId the new COCOMO ID to set
	 */
	public void setCocomoId(Cocomo cocomoId) {
		this.cocomoId = cocomoId;
	}

	/**
	 * Getter for the estimation's sharing date
	 * @return this.dateShared as a Timestamp object
	 */
	public Timestamp getDateShared() {
		return this.dateShared;
	}

	/**
	 * Setter for the estimation's sharing date
	 * @param dateShared the new sharing date to set
	 */
	public void setDateShared(Timestamp dateShared) {
		this.dateShared = dateShared;
	}

	/**
	 * Getter for the sender's ID
	 * @return this.fromUserId as a UserLogin object
	 */
	public UserLogin getFromUserId() {
		return this.fromUserId;
	}

	/**
	 * Setter for the sender's ID
	 * @param fromUserId the new sender's ID to set
	 */
	public void setFromUserId(UserLogin fromUserId) {
		this.fromUserId = fromUserId;
	}

	/**
	 * Getter for the receiver's ID
	 * @return this.toUserId as a UserLogin object
	 */
	public UserLogin getToUserId() {
		return this.toUserId;
	}

	/**
	 * Setter for the receiver's ID
	 * @param toUserId the new receiver's ID to set
	 */
	public void setToUserId(UserLogin toUserId) {
		this.toUserId = toUserId;
	}

	/**
	 * Redefining the equality between two SharedCocomo objects. 
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SharedCocomo)) {
			return false;
		}

		final SharedCocomo sc = (SharedCocomo) o;

		if (!fromUserId.equals(sc.getFromUserId())) {
			return false;
		}
		else if (!toUserId.equals(sc.getFromUserId()))
			return false;
		else if (!cocomoId.equals(sc.getCocomoId()))
			return false;
		return true;
	}

	/**
	 * Redefining the hash code of a SharedCocomo object.
	 * @return cocomoId.hashCode() + fromUserId.hashCode() + toUserId.hashCode(), the hash code of the object
	 */
	@Override
	public int hashCode() {
		if (cocomoId != null) {
			return (cocomoId.hashCode() + fromUserId.hashCode() + toUserId.hashCode());
		}
		return 0;
	}
}
