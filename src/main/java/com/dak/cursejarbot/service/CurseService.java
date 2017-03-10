/* 
Copyright 2017 David Key

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.dak.cursejarbot.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.model.CurseWord;
import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.model.Server;
import com.dak.cursejarbot.repository.CurseWordRepository;
import com.dak.cursejarbot.repository.CursesRepository;
import com.dak.cursejarbot.repository.ServerRepository;

import de.btobastian.javacord.DiscordAPI;
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
	private final ServerRepository serverRepos;
	private final DiscordAPI api;

	private Map<String, Pattern> cursePatterns;
	private final Map<String, Boolean> silentMode; // TODO: refactor to a table so we can persist this through server restarts...
	private List<String> cursesFromFile;

	@Autowired
	public CurseService(final CursesRepository cursesRepos, 
			final CurseWordRepository curseWordRepos, 
			final DiscordAPI api,
			final ServerRepository serverRepos){
		this.cursesRepos = cursesRepos;
		this.curseWordRepos = curseWordRepos;
		this.api = api;
		this.serverRepos = serverRepos;
		cursePatterns = null;
		cursesFromFile = null;
		silentMode = new HashMap<String, Boolean>();
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

	public void deleteMessageBySchedule(final Message message, final Long secondsBeforeDelete){
		final String messageId = message.getId();
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		                api.getMessageById(messageId).delete();
		            }
		        }, 
		        secondsBeforeDelete * 1000L
		);
	}

	public void enableSilentMode(final String serverId){
		Server server = serverRepos.findOne(serverId);
		
		if(server == null){
			server = Server.builder().serverId(serverId).silentMode(true).build();
		} else {
			server.setSilentMode(true);
		}
		
		serverRepos.save(server);
		
		silentMode.put(serverId, true);
	}

	public void disableSilentMode(final String serverId){
		Server server = serverRepos.findOne(serverId);
		
		if(server == null){
			server = Server.builder().serverId(serverId).silentMode(false).build();
		} else {
			server.setSilentMode(false);
		}
		
		serverRepos.save(server);
		
		silentMode.put(serverId, false);
	}

	public Boolean isSilentModeEnabled(final String serverId){
		if(silentMode.containsKey(serverId)){
			return silentMode.get(serverId);
		}
		
		Server server = serverRepos.findOne(serverId);
		
		if(server == null){
			// if we don't already have a a record for this server, 
			// add one and use default silent setting
			server = Server.builder().serverId(serverId).silentMode(false).build();
			serverRepos.save(server);
		}
		
		return server.getSilentMode();
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
