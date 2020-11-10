package com.nowcoder.work.community.controller;

import com.nowcoder.work.community.annotation.LoginRequired;
import com.nowcoder.work.community.entity.Event;
import com.nowcoder.work.community.entity.Page;
import com.nowcoder.work.community.entity.User;
import com.nowcoder.work.community.event.EventProducer;
import com.nowcoder.work.community.service.FollowService;
import com.nowcoder.work.community.service.UserService;
import com.nowcoder.work.community.util.CommunityConstant;
import com.nowcoder.work.community.util.CommunityUtil;
import com.nowcoder.work.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @LoginRequired
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        // 触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(user.getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注！");
    }

    @LoginRequired
    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注！");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model){
        // 判断用户是否存在
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        // 设置分页
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        // 查询所有关注用户
        List<Map<String, Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                // 判断当前用户对该用户的关注状态
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId")int userId, Page page, Model model){
        // 判断用户是否存在
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        // 设置分页
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        // 查询所有粉丝
        List<Map<String, Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map<String, Object> map : userList){
                User u = (User) map.get("user");
                // 判断当前用户对该用户的关注状态
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return "/site/follower";
    }

    // 查询当前登录用户是否关注了userId用户
    public boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollwed(hostHolder.getUser().getId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }
}
