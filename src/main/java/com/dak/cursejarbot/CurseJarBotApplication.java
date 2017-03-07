package com.dak.cursejarbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.dak.cursejarbot.service.BotService;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;

@SpringBootApplication
public class CurseJarBotApplication implements CommandLineRunner {
	
	@Autowired
	private BotService botService;
	
	public static void main(String[] args) {
		SpringApplication.run(CurseJarBotApplication.class, args);
	}
	
	@Override
	public void run(String... args) {
		botService.startBot();
	}
	
	@Bean
	public DiscordAPI getDiscordApi(@Value("${discord.api.token}") final String token){
		return Javacord.getApi(token, true);
	}
}
