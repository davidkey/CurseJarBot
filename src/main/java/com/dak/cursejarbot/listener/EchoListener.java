package com.dak.cursejarbot.listener;

import org.springframework.stereotype.Component;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class EchoListener implements MessageCreateListener {

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if(message.getAuthor().equals(api.getYourself())){
			return;
		}
		/* check balances */
		if(message.getContent().toLowerCase().startsWith("!echo")){
			message.reply(message.getAuthor().getMentionTag() + " message content: '" + message.getContent() + "'");
		}

	}

}
