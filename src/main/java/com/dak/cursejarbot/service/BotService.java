package com.dak.cursejarbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.listener.BalancesListener;
import com.dak.cursejarbot.listener.CursesListener;
import com.dak.cursejarbot.listener.HelpListener;
import com.dak.cursejarbot.listener.MaintenanceListener;
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
	private final MaintenanceListener maintenanceListener;
	private final HelpListener helpListener;

	@Autowired
	public BotService(
			@Value("${discord.api.token}") final String token, 
			final CursesListener cursesListener,
			final BalancesListener balancesListener,
			final MaintenanceListener maintenanceListener,
			final HelpListener helpListener){
		api = Javacord.getApi(token, true);	
		this.cursesListener = cursesListener;
		this.balancesListener = balancesListener;
		this.maintenanceListener = maintenanceListener;
		this.helpListener = helpListener;
	}

	public Boolean startBot(){
		log.trace("startBot()");

		api.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI api) {
				// register listeners
				// note: maint goes first so the bot doesn't call them out when removing a curse word
				api.registerListener(maintenanceListener);
				api.registerListener(cursesListener);
				api.registerListener(balancesListener);
				api.registerListener(helpListener);
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
