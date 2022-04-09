package com.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.jwt.handler.ChatWebSoketHandler;

@Configuration
@EnableWebSocket
public class webSocketConfig implements WebSocketConfigurer {

	private final static String CHAT_ENDPOINT = "/chat";
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(getChatWebSoketHandler(),CHAT_ENDPOINT )
		        .setAllowedOrigins("*");
	}
	
@Bean 
public ChatWebSoketHandler getChatWebSoketHandler() {
	return new ChatWebSoketHandler();
}
}
