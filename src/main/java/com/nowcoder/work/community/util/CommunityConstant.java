package com.nowcoder.work.community.util;

public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS  = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认状态的登录转态的超时时间
     */
    int DEFAULT_EXPIRE_SECONDS = 3600 * 2;

    /**
     * 记住登录状态的登录凭证超时时间为30天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 30;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */

    int ENTITY_TYPE_USER = 3;

    /**
     * 主题：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 点赞主题
     */
    String TOPIC_LIKE = "like";

    /**
     * 关注主题
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：分享长图
     */
    String TOPIC_SHARE = "share";

    /**
     * 系统用户的ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限：普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限：管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限：版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
