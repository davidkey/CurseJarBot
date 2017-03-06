package com.dak.cursejarbot.service;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.model.CurseWord;
import com.dak.cursejarbot.repository.CurseWordRepository;

@Service
public class MaintenanceService {
	
	private final CurseService curseService;
	private final CurseWordRepository curseWordRepos;
	
	private final Set<String> ignoredMessageIds;
	
	public MaintenanceService(final CurseService curseService, final CurseWordRepository curseWordRepos){
		this.curseService = curseService;
		this.curseWordRepos = curseWordRepos;
		
		ignoredMessageIds = new HashSet<String>();
	}

	public boolean isIgnoredMessageId(final String messageId){
		synchronized(ignoredMessageIds){
			return ignoredMessageIds.contains(messageId);
		}
	}
	
	public void addIgnoredMessageId(final String messageId){
		synchronized(ignoredMessageIds){
			ignoredMessageIds.add(messageId);
		}
	}
	
	@Transactional
	public Boolean addCurseWord(final String serverId, final String curseWord){
		if(curseWordRepos.findByServerIdAndCurseWord(serverId, curseWord) != null){
			return false;
		}
		
		final CurseWord cw = CurseWord.builder().curseWord(curseWord).serverId(serverId).build();
		curseWordRepos.save(cw);
		curseService.clearPatternCache(serverId);
		
		return true;
	}
	
	@Transactional
	public Boolean removeCurseWord(final String serverId, final String curseWord){
		final CurseWord cw = curseWordRepos.findByServerIdAndCurseWord(serverId, curseWord);
		if(cw == null){
			return false;
		}
		
		curseWordRepos.delete(cw);
		curseService.clearPatternCache(serverId);
		
		return true;
	}
	
	@Scheduled(fixedDelay=60000) // clear ignored message ids every minute
	protected void clearIgnoredMessageIds(){
		synchronized(ignoredMessageIds){
			ignoredMessageIds.clear();
		}
	}
}
