package com.dak.cursejarbot.listener;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dak.cursejarbot.model.CurseWord;
import com.dak.cursejarbot.repository.CurseWordRepository;
import com.dak.cursejarbot.service.CurseService;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.entities.permissions.Role;
import de.btobastian.javacord.listener.message.MessageCreateListener;

@Component
public class MaintenanceListener implements MessageCreateListener {
	
	private final String curseJarRoleName;
	private final CurseWordRepository curseWordRepository;
	private final CurseService curseService;
	
	@Autowired
	public MaintenanceListener(
			@Value("${bot.maintenance.role}") final String curseJarRoleName,
			final CurseWordRepository curseWordRepository,
			final CurseService curseService) {
		this.curseJarRoleName = curseJarRoleName;
		this.curseWordRepository = curseWordRepository;
		this.curseService = curseService;
	}
	
	@Override
	public void onMessageCreate(DiscordAPI api, Message message) {
		final String messageContent = message.getContent().toLowerCase();
		if(!messageContent.startsWith("!maint")){
			return;
		}
		
		final String owner = message.getChannelReceiver().getServer().getOwnerId();	
		final Collection<Role> roles = message.getAuthor().getRoles(message.getChannelReceiver().getServer());

		Boolean isInCurseJarRole = false;
		for(Role r : roles){
			if(curseJarRoleName.equals(r.getName())){
				isInCurseJarRole = true;
				break;
			}
		}
		
		if(!isInCurseJarRole && !owner.equals(message.getAuthor().getId())){
			return;
		}
		
		if("!maint".equals(messageContent)){
			StringBuilder sb = new StringBuilder();
			sb.append(message.getAuthor().getMentionTag())
				.append(" here are maintenance options:\n")
				.append("!maint remove [curse word]\n")
				.append("!maint add [curse word]\n")
				.append("!maint list");
			
			message.reply(sb.toString());
			return;
		}
		
		final String[] options = messageContent.split(" ");
		if(options.length == 1){
			return; // problem?? shouldn't be possible
		}
		
		if("remove".equals(options[1])){
			CurseWord cw = curseWordRepository.findOne(options[2]);
			
			if(cw == null){
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" not found!");
				return;
			}
			
			curseWordRepository.delete(cw);
			curseService.clearPatternCache();
			
			message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" removed!");
			return;
		}
		
		if("add".equals(options[1])){
			if(curseWordRepository.findOne(options[2]) != null){
				message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" already in system!");
				return;
			}
			
			CurseWord cw = CurseWord.builder().curseWord(options[2]).build();
			curseWordRepository.save(cw);
			curseService.clearPatternCache();
			
			message.reply(message.getAuthor().getMentionTag() + " \"" + options[2] + "\" added!");
			return;
		}
		
		if("list".equals(options[1])){
			final StringBuilder sb = new StringBuilder();
			sb.append(message.getAuthor().getMentionTag() + " Curse Word Listing:\n");
			
			final List<CurseWord> curseWords = curseWordRepository.findAllByOrderByCurseWordAsc();
			curseWords.stream().forEach(s -> sb.append(s.getCurseWord() + "\n"));
			
			message.reply(sb.toString());
			return;
		}
		
	}

}
