package com.dak.cursejarbot.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false)
	private String userId;
	
	@Column(nullable = false) // FIXME: should I really have this here? probably not...
	private String username;

	@Column(nullable = false)
	private String serverId;

	@Column(nullable = false)
	private Long curseCount;
}
