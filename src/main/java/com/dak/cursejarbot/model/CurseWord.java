package com.dak.cursejarbot.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Entity
@Table(
		name = "curse_words",
		uniqueConstraints = {@UniqueConstraint(columnNames = {"curseWord", "serverId"})}
		)
@Getter
@Setter
@Builder
@ToString
public class CurseWord implements Serializable{
	private static final long serialVersionUID = 1L;

	@Tolerate
	public CurseWord(){}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String curseWord;

	@Column(nullable = false)
	private String serverId;
}
