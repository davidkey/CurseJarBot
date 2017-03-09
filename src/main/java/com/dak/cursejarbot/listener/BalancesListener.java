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
