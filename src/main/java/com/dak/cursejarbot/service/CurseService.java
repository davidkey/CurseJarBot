package com.dak.cursejarbot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.model.CurseWord;
import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.repository.CurseWordRepository;
import com.dak.cursejarbot.repository.CursesRepository;

import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class CurseService {
	private final CursesRepository cursesRepos;
	private final CurseWordRepository curseWordRepos;

	private Map<String, Pattern> cursePatterns;
	private final Map<String, Boolean> silentMode; // TODO: refactor to a table so we can persist this through server restarts...
	private final Map<Message, LocalTime> deleteQueue;
	private List<String> cursesFromFile;

	@Autowired
	public CurseService(final CursesRepository cursesRepos, final CurseWordRepository curseWordRepos){
		this.cursesRepos = cursesRepos;
		this.curseWordRepos = curseWordRepos;
		cursePatterns = null;
		cursesFromFile = null;
		silentMode = new HashMap<String, Boolean>();
		deleteQueue = new HashMap<Message, LocalTime>();
	}

	public Curses incrementCurseCount(final User user, final String serverId, final int count){
		Curses c = cursesRepos.findByUserIdAndServerId(user.getId(), serverId);

		if(c == null){
			c = Curses.builder()
					.curseCount((long)count)
					.userId(user.getId())
					.username(user.getName())
					.serverId(serverId)
					.build();
		} else {
			c.setCurseCount(c.getCurseCount() + count);
		}

		return cursesRepos.save(c);
	}

	public void addToDeleteQueue(final Message message, final Long secondsBeforeDelete){
		synchronized(deleteQueue){
			deleteQueue.put(message, LocalTime.now().plusSeconds(secondsBeforeDelete));
		}
	}

	@Scheduled(fixedDelay=1000) // check delete queue for messages every second
	protected void deleteFromDeleteQueue(){
		synchronized(deleteQueue){
			if(deleteQueue.isEmpty()){
				return;
			}
			
			final Iterator<Message> it = deleteQueue.keySet().iterator();
			while (it.hasNext())
			{
				final Message m = it.next();
				final LocalTime lt = deleteQueue.get(m);

				if (lt.compareTo(LocalTime.now()) < 0){
					log.trace("deleting message {}", m);
					m.delete(); // should I wait here?
					it.remove();	
				}
			}
		}
	}

	public void enableSilentMode(final String serverId){
		silentMode.put(serverId, true);
	}

	public void disableSilentMode(final String serverId){
		silentMode.put(serverId, false);
	}

	public Boolean isSilentModeEnabled(final String serverId){
		return silentMode.containsKey(serverId) && silentMode.get(serverId);
	}

	public Boolean clearBalances(@NonNull final String serverId){
		try{
			cursesRepos.delete(cursesRepos.findByServerIdOrderByCurseCountDesc(serverId));
		} catch (Exception ex){
			log.error("error while deleting records for server id " + serverId, ex);
			return false;
		}

		return true;
	}

	public synchronized void clearPatternCache(@NonNull final String serverId){
		if(cursePatterns != null && cursePatterns.containsKey(serverId)){
			cursePatterns.remove(serverId);
		}

		return;
	}

	public synchronized Pattern getCursePattern(final String serverId){
		log.trace("getCursePattern({})", serverId);

		if(cursePatterns == null){
			cursePatterns = new HashMap<String, Pattern>();
		}

		if(!cursePatterns.containsKey(serverId)){
			final List<String> curses = getCurses(serverId);

			final StringBuilder sb = new StringBuilder();
			curses.stream().forEach(s -> sb.append(s).append("|"));

			final String matcherString = sb.toString().substring(0, sb.length()-1);
			log.trace("Matcher: {}", matcherString);

			cursePatterns.put(serverId, Pattern.compile(matcherString));
		}

		return cursePatterns.get(serverId);
	}

	private synchronized List<String> getCurses(final String serverId){
		List<CurseWord> curseWords = curseWordRepos.findAllByServerIdOrderByCurseWordAsc(serverId);
		if(curseWords.isEmpty()){
			// if there are no curse words already saved for this server, use the default list provied by curses.txt
			final List<String> curseWordsFromFile = getCursesFromFile();

			curseWords = curseWordsFromFile.stream()
					.map(s -> CurseWord.builder().curseWord(s).serverId(serverId).build())
					.collect(Collectors.toList());

			curseWordRepos.save(curseWords);
		}

		return curseWords.stream().map(CurseWord::getCurseWord).collect(Collectors.toList());
	}

	private synchronized List<String> getCursesFromFile(){
		log.trace("getCursesFromFile()");

		if(cursesFromFile == null){
			final List<String> curses = new ArrayList<String>();

			try (Stream<String> stream = new BufferedReader(new InputStreamReader(new ClassPathResource("curses.txt").getInputStream())).lines()) {
				stream.forEach(curses::add);
			} catch (IOException ioe){
				log.error("Cannot open curses.txt!", ioe);
				throw new RuntimeException("Cannot open curses.txt!", ioe);
			}

			cursesFromFile = new ArrayList<String>(curses);
		}

		return cursesFromFile;
	}
}
