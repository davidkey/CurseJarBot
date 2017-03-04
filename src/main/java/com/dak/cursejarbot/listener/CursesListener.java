package com.dak.cursejarbot.listener;

import java.text.NumberFormat;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.service.CurseService;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class CursesListener implements MessageCreateListener {

	private final CurseService curseService;

	@Autowired
	public CursesListener(final CurseService curseService){
		this.curseService = curseService;
	}
	
	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if(message.getAuthor().equals(api.getYourself())){
			return;
		}
		
		final String content = message.getContent().toLowerCase();
    	final Matcher matcher = curseService.getCursePattern().matcher(content);
    	
    	/* check for cursing */
    	int curseCount = 1;
        if(matcher.find()){
        	while (matcher.find()){
        		curseCount++;
        	}
        	
        	final Curses c = curseService.incrementCurseCount(message.getAuthor(), message.getChannelReceiver().getServer().getId(), curseCount);
        	message.reply(message.getAuthor().getMentionTag() + " - one of us needs to calm down! You've now cursed " + c.getCurseCount() + " times "
        			+ "for a curse jar balance of " + NumberFormat.getCurrencyInstance().format(c.getCurseCount() * .1f) + ".");
        }

	}

}
