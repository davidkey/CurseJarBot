package com.dak.cursejarbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dak.cursejarbot.model.Curses;

public interface CursesRepository extends JpaRepository<Curses, Long> {
	List<Curses> findByServerIdOrderByCurseCountDesc(final String serverId);
	Curses findByUserIdAndServerId(final String userId, final String serverId);
}
