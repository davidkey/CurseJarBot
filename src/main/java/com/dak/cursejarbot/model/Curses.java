package com.dak.cursejarbot.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Entity
@Table(name = "curses")
@Getter
@Setter
@Builder
@ToString
public class Curses implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Tolerate
	public Curses(){}
	
	@Id
	@Column(nullable = false)
	private String id;
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String serverId;
	
	@Column(nullable = false)
	private Long curseCount;
}
