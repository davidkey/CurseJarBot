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

package com.dak.cursejarbot.meta;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.listener.message.MessageCreateListener;
import lombok.extern.slf4j.Slf4j;

/**
 * This class searches for all spring-registered beans of type {@linkplain de.btobastian.javacord.listener.message.MessageCreateListener MessageCreateListener}
 * and registers them via {@linkplain de.btobastian.javacord.DiscordAPI DiscordAPI}. This keeps us from having to manually call
 * {@linkplain de.btobastian.javacord.DiscordAPI#registerListener(de.btobastian.javacord.listener.Listener) registerListener} for every listener we have.
 * @author David Key
 */
@Component
@Slf4j
public class ListenerAttacher {

	private final ApplicationContext applicationContext;
	private final DiscordAPI discordAPI;

	@Autowired
	public ListenerAttacher(final ApplicationContext applicationContext, final DiscordAPI discordAPI){
		this.applicationContext = applicationContext;
		this.discordAPI = discordAPI;

		addListenersToDiscordApi();
	}

	private void addListenersToDiscordApi(){
		final Map<String, MessageCreateListener> listeners = applicationContext.getBeansOfType(MessageCreateListener.class);

		for(Entry<String, MessageCreateListener> entry : listeners.entrySet()){
			log.debug("addListenersToDiscordApi() Entry={}; value={}", entry.getKey(), entry.getValue());
			discordAPI.registerListener(entry.getValue());
		}
		return;
	}
}
