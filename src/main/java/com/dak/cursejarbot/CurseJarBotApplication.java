package com.dak.cursejarbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.dak.cursejarbot.service.BotService;

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
}
