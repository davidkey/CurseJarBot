/* 
Copyright 2017 David Key

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, 
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

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
