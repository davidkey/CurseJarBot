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

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.CurseWord;
import com.dak.cursejarbot.repository.CurseWordRepository;
import com.dak.cursejarbot.service.CurseService;
import com.dak.cursejarbot.service.MaintenanceService;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class MaintenanceListener implements MessageCreateListener {

	private final String curseJarRoleName;
	private final CurseWordRepository curseWordRepository;
	private final CurseService curseService;
	private final MaintenanceService maintService;

	@Autowired
	public MaintenanceListener(
			@Value("${bot.maintenance.role}") final String curseJarRoleName,
			final CurseWordRepository curseWordRepository,
			final CurseService curseService,
			final MaintenanceService maintService) {
		this.curseJarRoleName = curseJarRoleName;
		this.curseWordRepository = curseWordRepository;
		this.curseService = curseService;
		this.maintService = maintService;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		final String messageContent = message.getContent().toLowerCase();
		if(!messageContent.startsWith("!maint")){
			return;
		}

		final String owner = message.getChannelReceiver().getServer().getOwnerId();	
		final Collection<Role> roles = message.getAuthor().getRoles(message.getChannelReceiver().getServer());
		final String serverId = message.getChannelReceiver().getServer().getId();

		Boolean isInCurseJarRole = false;
		for(Role r : roles){
			if(curseJarRoleName.equals(r.getName())){
				isInCurseJarRole = true;
				break;
			}
		}

		if(!isInCurseJarRole && !owner.equals(message.getAuthor().getId())){
			return;
		}
		
		maintService.addIgnoredMessageId(message.getId());

		if("!maint".equals(messageContent)){
			StringBuilder sb = new StringBuilder();
			sb.append(message.getAuthor().getMentionTag())
			.append(" here are maintenance options:\n")
			.append("!maint list\n")
			.append("!maint add [curse word]\n")
			.append("!maint remove [curse word]\n")
			.append("!maint clearbalances\n")
			.append("!maint silent [on/off]\n");;

			message.reply(sb.toString());
			return;
		}

		final String[] options = messageContent.split(" ");
		if(options.length == 1){
			return; // problem?? shouldn't be possible
		}

		if("remove".equals(options[1])){
			if(maintService.removeCurseWord(serverId, options[2])){
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" removed!");
			} else {
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" not found!");
			}
			
			return;
		}

		if("add".equals(options[1])){
			if(maintService.addCurseWord(serverId, options[2])){
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" added!");
			} else {
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" already in system!");
			}

			return;
		}

		if("list".equals(options[1])){
			final StringBuilder sb = new StringBuilder();
			sb.append(message.getAuthor().getMentionTag() + " Curse Word Listing:\n");

			final List<CurseWord> curseWords = curseWordRepository.findAllByServerIdOrderByCurseWordAsc(serverId);
			curseWords.stream().forEach(s -> sb.append(s.getCurseWord() + "\n"));

			message.reply(sb.toString());
			return;
		}

		if("clearbalances".equals(options[1])){
			if(curseService.clearBalances(serverId)){
				message.reply(message.getAuthor().getMentionTag() + " curse balances cleared!");
			} else {
				message.reply(message.getAuthor().getMentionTag() + " error while clearing balances. Please check bot logs.");
			}

			return;
		}
		
		if("silent".equals(options[1])){
			if(options.length > 2){
				if("on".equals(options[2])){
					message.reply(message.getAuthor().getMentionTag() + " silent mode enabled");
					curseService.enableSilentMode(serverId);
				} else if("off".equals(options[2])){
					message.reply(message.getAuthor().getMentionTag() + " silent mode disabled");
					curseService.disableSilentMode(serverId);
				} else {
					message.reply(message.getAuthor().getMentionTag() + " " + options[2] + " is not a valid option for !maint silent [on/off]");
				}
			} else {
				message.reply(message.getAuthor().getMentionTag() + " invalid syntax for !maint silent [on/off]");
			}
			
			return;
		}

	}

}
