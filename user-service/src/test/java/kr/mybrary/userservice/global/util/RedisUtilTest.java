package kr.mybrary.userservice.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisUtilTest {

    @Mock
    RedisTemplate redisTemplate;
    @InjectMocks
    RedisUtil redisUtil;

    @Test
    @DisplayName("Redis에 데이터를 저장한다")
    void set() {
        // given
        doNothing().when(redisTemplate).setValueSerializer(any());
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        redisUtil.set("key", "value", null);

        // then
        verify(redisTemplate, times(1)).setValueSerializer(any());
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(any(), any(), any());
    }

    @Test
    @DisplayName("Redis에서 데이터를 가져온다")
    void get() {
        // given
        ValueOperations<String, Object> valueOperations = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(any())).willReturn("value");

        // when
        Object value = redisUtil.get("key");

        // then
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(any());
        assertEquals("value", value);
    }

    @Test
    @DisplayName("Redis에서 데이터를 삭제한다")
    void delete() {
        // given
        given(redisTemplate.delete("key")).willReturn(true);

        // when
        redisUtil.delete("key");

        // then
        verify(redisTemplate, times(1)).delete("key");
    }
}