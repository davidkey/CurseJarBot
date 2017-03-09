package com.dak.cursejarbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BotService {

	private final DiscordAPI api;

	@Autowired
	public BotService(final DiscordAPI api){
		this.api = api;	
	}

	public Boolean startBot(){
		log.trace("startBot()");

		api.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI api) {
				// we used to register listeners here, but that's now done w/ ListenerAttacher automatically...
			}

			@Override
			public void onFailure(Throwable t) {
				log.error("Bot error!", t);
			}
		});

		api.setAutoReconnect(true);
		return true;
	}

	public Boolean stopBot(){
		log.trace("stopBot()");

		api.disconnect();
		return true;
	}
}
