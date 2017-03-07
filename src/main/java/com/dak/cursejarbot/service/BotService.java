package com.dak.cursejarbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dak.cursejarbot.listener.BalancesListener;
import com.dak.cursejarbot.listener.CursesListener;
import com.dak.cursejarbot.listener.EchoListener;
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
	private final EchoListener echoListener;

	@Autowired
	public BotService(
			final DiscordAPI api, 
			final CursesListener cursesListener,
			final BalancesListener balancesListener,
			final MaintenanceListener maintenanceListener,
			final HelpListener helpListener,
			final EchoListener echoListener){
		this.api = api;	
		this.cursesListener = cursesListener;
		this.balancesListener = balancesListener;
		this.maintenanceListener = maintenanceListener;
		this.helpListener = helpListener;
		this.echoListener = echoListener;
	}

	public Boolean startBot(){
		log.trace("startBot()");

		api.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI api) {
				// register listeners
				api.registerListener(maintenanceListener);
				api.registerListener(cursesListener);
				api.registerListener(balancesListener);
				api.registerListener(helpListener);
				api.registerListener(echoListener);
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
