/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.data.geo.Circle
 *  org.springframework.data.geo.GeoResults
 *  org.springframework.data.redis.connection.RedisGeoCommands$GeoLocation
 *  org.springframework.data.redis.connection.RedisGeoCommands$GeoRadiusCommandArgs
 *  org.springframework.data.redis.core.HashOperations
 *  org.springframework.data.redis.core.ListOperations
 *  org.springframework.data.redis.core.RedisTemplate
 *  org.springframework.data.redis.core.ZSetOperations$TypedTuple
 *  org.springframework.stereotype.Component
 */
package com.etas.vaas.common.utils;

import com.etas.vaas.common.utils.JsonUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {
    @SuppressWarnings("rawtypes")
    private final RedisTemplate redisTemplate;

    public String getValue(String key) {
        return (String)this.redisTemplate.opsForValue().get(key);
    }

    public void setValue(String key, String value) {
        this.redisTemplate.opsForValue().set((Object)key, (Object)value);
    }

    public void delete(String key) {
        this.redisTemplate.delete((Object)key);
    }

    public void setHashValue(String key, String field, String content) {
        this.redisTemplate.opsForHash().put((Object)key, (Object)field, (Object)content);
    }

    public <T> T getHashValue(String key, String hashKey) {
        HashOperations hashOps = this.redisTemplate.opsForHash();
        return (T)hashOps.get((Object)key, (Object)hashKey);
    }

    public Map<String, String> getEntireHash(String key) {
        HashOperations hashOps = this.redisTemplate.opsForHash();
        return hashOps.entries((Object)key);
    }

    public void delHashValue(String key, String field) {
        this.redisTemplate.opsForHash().delete((Object)key, new Object[]{field});
    }

    public void hashIncrementByInt(String key, String field, long delta) {
        this.redisTemplate.opsForHash().increment((Object)key, (Object)field, delta);
    }

    public void hashIncrementByDouble(String key, String field, double delta) {
        this.redisTemplate.opsForHash().increment((Object)key, (Object)field, delta);
    }

    public Long listSize(String listKey) {
        return this.redisTemplate.opsForList().size((Object)listKey);
    }

    public void leftPush(String listKey, String content) {
        this.redisTemplate.opsForList().leftPush((Object)listKey, (Object)content);
    }

    public void rightPush(String listKey, Object item) {
        this.redisTemplate.opsForList().rightPush((Object)listKey, (Object)JsonUtils.toStr(item));
    }

    public String rightPop(String key) {
        return (String)this.redisTemplate.opsForList().rightPop((Object)key);
    }

    public List<String> leftPopWithCount(String listKey, long count) {
        ListOperations listOps = this.redisTemplate.opsForList();
        return listOps.leftPop((Object)listKey, count);
    }

    public void lTrim(String listKey, long startIndex, long endIndex) {
        this.redisTemplate.opsForList().trim((Object)listKey, startIndex, endIndex);
    }

    public List<String> lRange(String key, long start, long end) {
        return this.redisTemplate.opsForList().range((Object)key, start, end);
    }

    public String getLastItemInList(String key) {
        return (String)this.redisTemplate.opsForList().getLast((Object)key);
    }

    public GeoResults<RedisGeoCommands.GeoLocation<String>> findByRadius(String key, Circle point) {
        return this.redisTemplate.opsForGeo().radius((Object)key, point, RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates());
    }

    public Set<ZSetOperations.TypedTuple<String>> getFromZSet(String key, double minScore, double maxScore) {
        return this.redisTemplate.opsForZSet().rangeByScoreWithScores((Object)key, minScore, maxScore);
    }

    public void addToZSet(String key, String value, double score) {
        this.redisTemplate.opsForZSet().add((Object)key, (Object)value, score);
    }

    public void removeFromZSetByScore(String key, double minScore, double maxScore) {
        this.redisTemplate.opsForZSet().removeRangeByScore((Object)key, minScore, maxScore);
    }

    public Long removeFromZSetByMember(String key, String member) {
        return this.redisTemplate.opsForZSet().remove((Object)key, new Object[]{member});
    }

    public String getMaxScoreMember(String key) {
        Set result = this.redisTemplate.opsForZSet().reverseRangeWithScores((Object)key, 0L, 0L);
        if (result != null && !result.isEmpty()) {
            ZSetOperations.TypedTuple tuple = (ZSetOperations.TypedTuple)result.iterator().next();
            return (String)tuple.getValue();
        }
        return null;
    }

    public Set<String> getFromZSetWithScores(String key, double minScore, double maxScore) {
        return this.redisTemplate.opsForZSet().rangeByScore((Object)key, minScore, maxScore);
    }

    public Set<String> getAllFromZSet(String key) {
        return this.redisTemplate.opsForZSet().range((Object)key, 0L, -1L);
    }

    public void publishMessage(String channel, String message) {
        this.redisTemplate.convertAndSend(channel, (Object)message);
    }

    public void deleteKey(String key) {
        this.redisTemplate.delete((Object)key);
    }

    public RedisUtils(@org.springframework.beans.factory.annotation.Qualifier("redisTemplate") RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}

