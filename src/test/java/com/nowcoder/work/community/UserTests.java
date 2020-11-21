package com.nowcoder.work.community;


import com.nowcoder.work.community.dao.UserMapper;
import com.nowcoder.work.community.entity.User;
import com.nowcoder.work.community.service.UserService;
import com.nowcoder.work.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class UserTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testEditPassword(){
        List<User> users = userService.findAllUsers();
        for(User user : users){
            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
            // 修改密码
            userMapper.updatePassword(user.getId(), user.getPassword());
        }

    }
}
