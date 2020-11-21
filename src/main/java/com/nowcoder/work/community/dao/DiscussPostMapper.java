package com.nowcoder.work.community.dao;

import com.nowcoder.work.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit, @Param("orderMode") int orderMode);

    // @Param注解用于给参数取别名，在参数多余一个或者需要动态sql
    // 参数时必须加
    int selectDiscussPostRows(@Param("userId")int userId);

    // 插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新评论数量
     * @param id
     * @param commentCount
     * @return
     */
    int updateCommentCount(@Param("id")int id, @Param("commentCount") int commentCount);

    /**
     * 修改帖子类型
     */
    int updateType(@Param("id") int id, @Param("type") int type);

    /**
     * 修改帖子状态
     */
    int updateStatus(@Param("id") int id, @Param("status") int status);

    int updateScore(@Param("id") int id, @Param("score") double score);
}
