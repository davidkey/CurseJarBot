package com.dak.cursejarbot.controller;

import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dak.cursejarbot.service.BotService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class BotController {

	private final Boolean autostart;
	private final BotService botService;

	@Autowired
	public BotController(final BotService botService, @Value("${bot.autostart}") final Boolean autostart){
		this.botService = botService;
		this.autostart = autostart;
	}

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public Map<String, String> startBot(final HttpServletRequest request){
		log.debug("startBot({})", request == null ? "SYSTEM" : request.getRemoteAddr());

		try{
			if(!botService.startBot()){
				return Collections.singletonMap("response", "bot failed to stop");
			}
		} catch (Exception e){
			return Collections.singletonMap("response", "bot failed to start");
		}


		return Collections.singletonMap("response", "bot started");
	}
	
	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	public Map<String, String> stopBot(final HttpServletRequest request){
		log.debug("stopBot({})", request == null ? "SYSTEM" : request.getRemoteAddr());

		try{
			if(!botService.stopBot()){
				return Collections.singletonMap("response", "bot failed to stop");
			}
		} catch (Exception e){
			return Collections.singletonMap("response", "bot failed to stop");
		}


		return Collections.singletonMap("response", "bot stopped");
	}

	@PostConstruct
	private void startServerAuto(){
		if(autostart){
			botService.startBot();
		}
	}
}
