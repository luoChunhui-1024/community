package com.nowcoder.work.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTIRY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";

    // 某个实体的赞
    // like:entity:entityType:entityId -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTIRY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // like:user:userId --> int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体, 实体可以是帖子也可以用户
    // followee:userId:entityType: -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType){
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个用户的粉丝, 为了和关注列表的key不重复，这里把key的entityType和userId顺序交换一下
    // follower:entityType:userId: --> zset(userId,now)
    public static String getFollowerKey(int entityType, int userId){
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + userId;
    }

}
