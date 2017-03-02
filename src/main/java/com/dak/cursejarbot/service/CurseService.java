package com.dak.cursejarbot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.repository.CursesRepository;

import de.btobastian.javacord.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CurseService {
	private final CursesRepository cursesRepos;

	private Pattern cursePattern;

	@Autowired
	public CurseService(final CursesRepository cursesRepos){
		this.cursesRepos = cursesRepos;
		cursePattern = null;
	}

	public Curses incrementCurseCount(final User user, final String serverId, final int count){
		Curses c = cursesRepos.findOne(user.getId());

		if(c == null){
			c = Curses.builder()
					.id(user.getId())
					.curseCount((long)count)
					.username(user.getName())
					.serverId(serverId)
					.build();
		} else {
			c.setCurseCount(c.getCurseCount() + count);
		}

		return cursesRepos.save(c);
	}

	public synchronized Pattern getCursePattern(){
		log.trace("getCursePattern()");

		if(cursePattern == null){

			final List<String> curses = getCursesFromFile();

			final StringBuilder sb = new StringBuilder();
			curses.stream().forEach(s -> sb.append(s).append("|"));

			final String matcherString = sb.toString().substring(0, sb.length()-1);
			log.trace("Matcher: {}", matcherString);

			cursePattern = Pattern.compile(matcherString);
		}

		return cursePattern;
	}

	private List<String> getCursesFromFile(){
		log.trace("getCursesFromFile()");

		final List<String> curses = new ArrayList<String>();

		try (Stream<String> stream = new BufferedReader(new InputStreamReader(new ClassPathResource("curses.txt").getInputStream())).lines()) {
			stream.forEach(curses::add);
		} catch (IOException ioe){
			log.error("Cannot open curses.txt!", ioe);
			throw new RuntimeException("Cannot open curses.txt!", ioe);
		}

		return curses;
	}
}
