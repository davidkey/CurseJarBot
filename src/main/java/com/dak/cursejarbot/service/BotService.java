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

package com.dak.cursejarbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.FutureCallback;

import de.btobastian.javacord.DiscordAPI;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BotService {

	private final DiscordAPI api;

	@Autowired
	public BotService(final DiscordAPI api){
		this.api = api;	
	}

	public Boolean startBot(){
		log.trace("startBot()");

		api.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI api) {
				// we used to register listeners here, but that's now done w/ ListenerAttacher automatically...
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
