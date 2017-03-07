package com.dak.cursejarbot.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Component;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;;

@Component
public class HelpListener implements MessageCreateListener {
	
	private final String javaVersion;
	private final String appVersion;
	private final String buildTime;
	private GitProperties gitProperties;
	
	@Autowired
	public HelpListener(
			@Value("${info.app.java.source}") final String javaVersion,
			@Value("${info.app.version}") final String appVersion,
			@Value("${info.app.build.time}") final String buildTime,
			final GitProperties gitProperties){
		this.javaVersion = javaVersion;
		this.appVersion = appVersion;
		this.buildTime = buildTime;
		this.gitProperties = gitProperties;
	}

	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		if(message.getAuthor().equals(api.getYourself())){
			return;
		}

		final List<User> mentionedUsers = message.getMentions();
		
		if(mentionedUsers == null){
			return;
		}
		
		if(mentionedUsers.contains(api.getYourself())){
			if(message.getContent().contains(" help")){
				StringBuilder sb = new StringBuilder();
				sb.append(message.getAuthor().getMentionTag())
				.append(" here are maintenance options:\n")
				.append("!maint list\n")
				.append("!maint add [curse word]\n")
				.append("!maint remove [curse word]\n")
				.append("!maint clearbalances\n");
				
				sb.append("\nCurseJarBot version ").append(appVersion).append("\n")
					.append("https://github.com/davidkey/CurseJarBot/commit/").append(gitProperties.getCommitId()).append("\n")
					.append("Built w/ java ").append(javaVersion)
					.append(" @ ").append(buildTime).append("\n");

				message.reply(sb.toString());
			} else {
				message.reply(message.getAuthor().getMentionTag() + " hi there!");
			}
		}
		
		return;
	}

}
