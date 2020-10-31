package com.nowcoder.work.community.controller;

import com.nowcoder.work.community.entity.DiscussPost;
import com.nowcoder.work.community.entity.User;
import com.nowcoder.work.community.service.DiscussPostService;
import com.nowcoder.work.community.util.CommunityUtil;
import com.nowcoder.work.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){

        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403, "您还未登录，不能发帖");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 根据报错情况，将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");

    }
}
