package com.dak.cursejarbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.listener.BalancesListener;
import com.dak.cursejarbot.listener.CursesListener;
import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BotService {
	
	private final DiscordAPI api;
	private final CursesListener cursesListener;
	private final BalancesListener balancesListener;
	
	@Autowired
	public BotService(
			@Value("${discord.api.token}") final String token, 
			final CursesListener cursesListener,
			final BalancesListener balancesListener){
		api = Javacord.getApi(token, true);	
		this.cursesListener = cursesListener;
		this.balancesListener = balancesListener;
	}

	public Boolean startBot(){
		log.trace("startBot()");
		
        api.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(DiscordAPI api) {
                // register listeners
                api.registerListener(cursesListener);
                api.registerListener(balancesListener);
            }

            @Override
            public void onFailure(Throwable t) {
            	log.error("Bot error!", t);
            }
        });
		return true;
	}
	
}
