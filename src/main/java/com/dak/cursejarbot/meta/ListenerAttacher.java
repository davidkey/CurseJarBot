package com.dak.cursejarbot.meta;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ListenerAttacher {

	private final ApplicationContext applicationContext;
	private final DiscordAPI discordAPI;

	@Autowired
	public ListenerAttacher(final ApplicationContext applicationContext, final DiscordAPI discordAPI){
		this.applicationContext = applicationContext;
		this.discordAPI = discordAPI;

		addListenersToDiscordApi();
	}

	private void addListenersToDiscordApi(){
		final Map<String, MessageCreateListener> listeners = applicationContext.getBeansOfType(MessageCreateListener.class);

		for(Entry<String, MessageCreateListener> entry : listeners.entrySet()){
			log.debug("addListenersToDiscordApi() Entry={}; value={}", entry.getKey(), entry.getValue());
			discordAPI.registerListener(entry.getValue());
			
		}
		return;
	}
}
