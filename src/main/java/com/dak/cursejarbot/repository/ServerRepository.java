package com.dak.cursejarbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dak.cursejarbot.model.Server;

public interface ServerRepository extends JpaRepository<Server, String> {

}
