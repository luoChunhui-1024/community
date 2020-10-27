package com.nowcoder.work.community.service;

import com.nowcoder.work.community.dao.LoginTicketMapper;
import com.nowcoder.work.community.dao.UserMapper;
import com.nowcoder.work.community.entity.LoginTicket;
import com.nowcoder.work.community.entity.User;
import com.nowcoder.work.community.util.CommunityConstant;
import com.nowcoder.work.community.util.CommunityUtil;
import com.nowcoder.work.community.util.HostHolder;
import com.nowcoder.work.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    public static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private HostHolder hostHolder;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if(user == null){
            try {
                throw new IllegalAccessException("参数不能为空！");
            } catch (IllegalAccessException e) {
                logger.info("参数不能为空！");
            }
        }
        // 空值处理
        if(StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空!");
            return map;
        }

        if(StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        if(StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg", "邮箱不能为空!");
            return map;
        }

        // 验证账号是否已存在
        User u = userMapper.selectByName(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "用户名已存在");
            return map;
        }

        // 验证邮箱是否已注册
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "邮箱已被注册!");
            return map;
        }

        // 开始注册功能
        // 填充其他必要默认数据后存入数据库
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUer(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 激活用户的账号
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1); // 状态设置为1
            return ACTIVATION_SUCCESS;  // 激活成功
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String, Object> login(String username, String password, int expired){
        HashMap<String, Object> map = new HashMap<>();

        // 空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if(user == null){
            map.put("usernameMsg", "账号不存在！");
            return map;
        }
        // 判断是否激活
        if(user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        // 验证密码
        System.out.println(password);
        password = CommunityUtil.md5(password + user.getSalt());
        System.out.println(password);
        System.out.println(user.getPassword());
        if(!password.equals(user.getPassword())){
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 生成登录凭证

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        System.out.println("user = " + user);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        System.out.println("expired = " + expired);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + (long)expired * 1000));
        System.out.println("expired * 1000 = " + (long)expired * 1000);
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     * @param ticket
     */
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    // 更新用户的头像
    public void updateHeader(int id, String headerUrl){
        userMapper.updateHeader(id, headerUrl);
    }

    public Map<String, Object> updatePassword(String oldPassword, String newPassword){

        Map<String, Object> map = new HashMap<>();
        // 判断两个密码是否为空
        if(StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg", "原密码不能为空！");
            return map;
        }

        if(StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空！");
            return map;
        }

        if(oldPassword.length() < 3){
            map.put("oldPasswordMsg", "密码长度不能小于3位！");
            return map;
        }

        if(newPassword.length() < 3){
            map.put("newPasswordMsg", "密码长度不能小于3位！");
            return map;
        }

        // 从hostHolder中取得user判断原密码是否正确
        User user = hostHolder.getUser();
        if(!user.getPassword().equals(CommunityUtil.md5(oldPassword + user.getSalt()))){
            map.put("oldPasswordMsg", "密码错误！");
            return map;
        }

        newPassword = CommunityUtil.md5(newPassword + user.getSalt());

        // 修改密码
        userMapper.updatePassword(user.getId(), newPassword);
        return null;
    }
}


