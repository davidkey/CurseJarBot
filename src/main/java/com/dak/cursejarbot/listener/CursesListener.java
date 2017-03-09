package com.dak.cursejarbot.listener;

import java.text.NumberFormat;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.Curses;
import com.dak.cursejarbot.service.CurseService;
import com.dak.cursejarbot.service.MaintenanceService;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CursesListener implements MessageCreateListener {

	private final CurseService curseService;
	private final MaintenanceService maintService;

	@Autowired
	public CursesListener(final CurseService curseService, final MaintenanceService maintService){
		this.curseService = curseService;
		this.maintService = maintService;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if(maintService.isIgnoredMessageId(message.getId()) || message.getAuthor().equals(api.getYourself())){
			return;
		}

		final String content = message.getContent().toLowerCase();
		final String serverId = message.getChannelReceiver().getServer().getId();
		final Matcher matcher = curseService.getCursePattern(serverId).matcher(content);

		/* check for cursing */
		int curseCount = 1;
		if(matcher.find()){
			while (matcher.find()){
				curseCount++;
			}

			final Curses c = curseService.incrementCurseCount(message.getAuthor(), message.getChannelReceiver().getServer().getId(), curseCount);


			if(!curseService.isSilentModeEnabled(serverId)){
				Future<Message> futureMessage = message.reply(message.getAuthor().getMentionTag() + " - one of us needs to calm down! You've now cursed " + c.getCurseCount() + " times "
						+ "for a curse jar balance of " + NumberFormat.getCurrencyInstance().format(c.getCurseCount() * .1f) + ".");

				try {
					curseService.addToDeleteQueue(futureMessage.get(), 10L);
				} catch (InterruptedException | ExecutionException e) {
					log.warn("failed to retrieve posted message", e);
				}
			}
		}

	}

}
