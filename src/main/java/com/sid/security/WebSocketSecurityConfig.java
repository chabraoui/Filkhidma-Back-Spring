//package com.sid.security;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
//
//@Configuration
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//
//	@Override
//	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//		messages.simpDestMatchers("/ws/**", "/topic/**").permitAll().anyMessage().permitAll();
//	}
//
//
//	@Override
//	protected boolean sameOriginDisabled() {
//		return true;
//	}
//}