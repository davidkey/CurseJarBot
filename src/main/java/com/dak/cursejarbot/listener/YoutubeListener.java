package com.dak.cursejarbot.listener;

import org.springframework.stereotype.Component;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class YoutubeListener implements MessageCreateListener {

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if(message.getAuthor().equals(api.getYourself())){
			return;
		}
		/* check balances */
		if(message.getContent().toLowerCase().contains("youtube") || message.getContent().toLowerCase().contains("youtu.be")){
			message.reply(message.getAuthor().getMentionTag() + " youtube eh? risky choice... NOT! rekt");
		}

	}

}
