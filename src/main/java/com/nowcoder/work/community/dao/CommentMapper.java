package com.nowcoder.work.community.dao;

import com.nowcoder.work.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId")int entityId, @Param("offset")int offset, @Param("limit") int limit);

    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

    int insertComment(Comment comment);

    Comment selectCommentById(int id);

    // 查找某个用户所有的回复和评论
    List<Comment> selectCommentsOfUser(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // 查找某个用户所有回复和评论的数量
    int selectCountOfUser(int userId);
}
