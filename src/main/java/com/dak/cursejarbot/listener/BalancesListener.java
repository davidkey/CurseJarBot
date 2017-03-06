package com.dak.cursejarbot.listener;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.repository.CursesRepository;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.User;
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
			final List<Curses> curses = cursesRepos.findByServerIdOrderByCurseCountDesc(message.getChannelReceiver().getServer().getId());

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
				
				User u = api.getCachedUserById(c.getUserId());
				if(u == null){
					
					Future<User> fu = api.getUserById(c.getUserId());
					try {
						u = fu.get();
					} catch (InterruptedException | ExecutionException e) {
						
					}
				}
				
				final String username = u != null ? u.getName() : c.getUsername(); // if we can't pull username from discord, use our cached version (can change?)
				
				sb.append(username + " - " + NumberFormat.getCurrencyInstance().format(c.getCurseCount() * .1f));
				sb.append("\n");

				count++;
			}

			message.reply(sb.toString());
			
		}

	}

}
