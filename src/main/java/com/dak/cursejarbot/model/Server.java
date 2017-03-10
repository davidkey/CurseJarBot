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
@Table(name = "server")
@Getter
@Setter
@Builder
@ToString
public class Server implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Tolerate
	public Server(){}
	
	@Id
	@Column(nullable = false)
	private String serverId;
	
	@Column(nullable = false)
	private Boolean silentMode;
	
	public Boolean getSilentMode(){
		return silentMode != null && silentMode;
	}
}
