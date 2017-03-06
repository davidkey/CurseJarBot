package com.dak.cursejarbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dak.cursejarbot.model.CurseWord;

public interface CurseWordRepository extends JpaRepository<CurseWord, Long> {
	List<CurseWord> findAllByServerIdOrderByCurseWordAsc(final String serverId);
	CurseWord findByServerIdAndCurseWord(final String serverId, final String curseWord);
}
