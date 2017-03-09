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
