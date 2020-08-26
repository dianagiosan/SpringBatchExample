package com.example.demojpa;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "actor")
public class Actor {
	public void setActor_id(Integer actor_id) {
		this.actor_id = actor_id;
	}
	
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public void setLast_updated(LocalDateTime last_updated) {
		this.last_updated = last_updated;
	}
	
	public Integer getActor_id() {
		return actor_id;
	}
	
	public String getFirst_name() {
		return first_name;
	}
	
	public String getLast_name() {
		return last_name;
	}
	
	public LocalDateTime getLast_updated() {
		return last_updated;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer actor_id;
	@Column(name = "first_name")
	public String first_name;
	@Column(name = "last_name")
	public String last_name;
	@Column(name = "last_update")
	public LocalDateTime last_updated;
	
	public String toString() {
		return actor_id + " " + first_name + " " + last_name + "/n";
	}

}
