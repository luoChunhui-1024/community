package com.nowcoder.work.community.controller;

import com.nowcoder.work.community.annotation.LoginRequired;
import com.nowcoder.work.community.entity.Comment;
import com.nowcoder.work.community.entity.DiscussPost;
import com.nowcoder.work.community.entity.Event;
import com.nowcoder.work.community.event.EventProducer;
import com.nowcoder.work.community.service.CommentService;
import com.nowcoder.work.community.service.DiscussPostService;
import com.nowcoder.work.community.util.CommunityConstant;
import com.nowcoder.work.community.util.HostHolder;
import com.nowcoder.work.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Service
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @LoginRequired
    @RequestMapping(path="/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment){
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType()) // 评论的类型和评论的id
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){    // 评论的帖子的作者
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){ // 回复的评论的作者
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        eventProducer.fireEvent(event);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 把帖子放到redis中
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
