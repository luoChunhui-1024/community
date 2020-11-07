package com.nowcoder.work.community.service;

import com.nowcoder.work.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    // 某人给entity点赞
    public void like(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMemeber = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        if(isMemeber){  // 如果是成员，再次点赞则是取消点赞，从set中删除这个成员
            redisTemplate.opsForSet().remove(entityLikeKey, userId);
        }else{
            redisTemplate.opsForSet().add(entityLikeKey, userId);
        }
    }

    // 查询某实体的被点赞的数量
    public long findEntityLikeCount(int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对实体是否点赞-- >已赞/赞
    public int findEntityLikeStatus(int userId, int entityType, int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }
}
