package com.dak.cursejarbot.listener;

import java.text.NumberFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.repository.CursesRepository;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class BalancesListener implements MessageCreateListener {
	
	private final CursesRepository cursesRepos;
	
	@Autowired
	public BalancesListener(final CursesRepository cursesRepos){
		this.cursesRepos = cursesRepos;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		/* check balances */
    	if("!balances".equals(message.getContent().toLowerCase())){
    		final List<Curses> curses = cursesRepos.findAllByServerIdOrderByCurseCountDesc(message.getChannelReceiver().getServer().getId());
    		
    		if(curses.isEmpty()){
    			message.reply("No balances at this time.");
    			return;
    		} 
    		
    		int count = 0;
    		StringBuilder sb = new StringBuilder();
    		sb.append("Current Curse Jar Balances: \n\n");
    		
    		for(Curses c : curses){
    			if(count >= 10){
    				break;
    			}
    			sb.append(c.getUsername() + " - " + NumberFormat.getCurrencyInstance().format(c.getCurseCount() * .1f));
    			sb.append("\n");
    			
    			count++;
    		}
    		
    		message.reply(sb.toString());
    	}

	}

}
