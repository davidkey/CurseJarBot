package com.dak.cursejarbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dak.cursejarbot.model.Curses;

public interface CursesRepository extends JpaRepository<Curses, String> {
	List<Curses> findAllByServerIdOrderByCurseCountDesc(final String serverId);
}
