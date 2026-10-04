package com.umg.citasmedicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    // Spring Boot detecta automáticamente este bean y lo usa como
    // configuración por defecto para TODAS las cachés de Redis (no solo
    // "especialidades"). Las claves se guardan como texto plano legible
    // (por eso vimos "especialidades::SimpleKey[]" en redis-cli), y los
    // valores como JSON en vez del formato binario de Java — así,
    // cualquier entidad que cachees de ahora en adelante funciona sin
    // tener que implementar Serializable.
    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}
