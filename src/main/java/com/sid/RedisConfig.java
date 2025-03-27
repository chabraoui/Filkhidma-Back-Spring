package com.sid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.sid.shared.dto.AnnonceDto;

@Configuration
public class RedisConfig {
	@Bean
	public RedisTemplate<String, byte[]> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, byte[]> template = new RedisTemplate<>();
		template.setConnectionFactory(factory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(template.getDefaultSerializer());
		return template;
	}

	@Bean
	public RedisTemplate<String, AnnonceDto> redisTemplateAnnonce(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, AnnonceDto> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
