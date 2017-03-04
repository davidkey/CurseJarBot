package com.dak.cursejarbot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dak.cursejarbot.model.CurseWord;

public interface CurseWordRepository extends JpaRepository<CurseWord, String> {
	List<CurseWord> findAllByOrderByCurseWordAsc();
}
