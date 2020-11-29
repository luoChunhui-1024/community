package com.nowcoder.work.community.controller;

import com.nowcoder.work.community.annotation.LoginRequired;
import com.nowcoder.work.community.entity.Comment;
import com.nowcoder.work.community.entity.DiscussPost;
import com.nowcoder.work.community.entity.Page;
import com.nowcoder.work.community.entity.User;
import com.nowcoder.work.community.service.*;
import com.nowcoder.work.community.util.CommunityConstant;
import com.nowcoder.work.community.util.CommunityUtil;
import com.nowcoder.work.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadpath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @LoginRequired
    @RequestMapping(path = "/setting", method= RequestMethod.GET)
    public String getSettingPage(Model model){

        // 上传文件名称
        String fileName = CommunityUtil.generateUUID();
        // 设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // 生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>fileName : " + fileName);
        return "/site/setting";
    }

    // 更新用户头像
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName){
        if(StringUtils.isBlank(fileName)){
            return CommunityUtil.getJSONString(1, "文件名不能为空！");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }

    // 废弃
    @LoginRequired
    @RequestMapping(path="/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage == null){
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error", "文件格式不正确！");
            return "/site/setting";
        }

        // 生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        // 确定文件的存放路径
        File dest = new File(uploadpath + "/" + filename);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("文件上传失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常！");
        }

        // 更新用户头像的路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headUrl);

        return "redirect:/index";
    }

    // 废弃
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response){
        // 服务器的存放路径
        filename = uploadpath + "/" + filename;
        // 文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(filename);
                OutputStream os = response.getOutputStream();
            ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
           logger.error("读取头像失败：", e.getMessage());
        }
    }

    /**
     * 修改用户的密码
     */
    @LoginRequired
    @RequestMapping(path = "/updatepwd", method = RequestMethod.POST)
    public String updatePwssword(String oldPassword, String newPassword, Model model){

        Map<String, Object> map = userService.updatePassword(oldPassword, newPassword);
        if(map != null){
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }else{
            model.addAttribute("msg", "您的密码已经修改成功，请重新登录！");
            String target = domain + contextPath + "/login";
            model.addAttribute("target", target);
            return "/site/operate-result";
        }
    }

    // 个人主页
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在！");
        }
        // 用户
        model.addAttribute("user", user);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        // 是否已关注
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollwed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);


        // 获赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        return "/site/profile";
    }

    // 查看我的帖子
    @RequestMapping(path = "/my-post", method= RequestMethod.GET)
    public String getMyPost(Model model, Page page){
        // 方法调用前，SpringMVC会自动实例化MOdel和page, 并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        User user = hostHolder.getUser();
        page.setRows(discussPostService.findDiscussPostsRows(user.getId()));
        page.setPath("/user/my-post");

        List<DiscussPost> list = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        for(DiscussPost post : list){
            Map<String, Object> map = new HashMap<>();
            map.put("post", post);

            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
            map.put("likeCount", likeCount);

            discussPosts.add(map);
        }
        model.addAttribute("discussPosts", discussPosts);
        return "site/my-post";
    }

    // 查看我的回复
    @RequestMapping(path = "/my-reply", method= RequestMethod.GET)
    public String getMyReply(Model model, Page page){
        // 方法调用前，SpringMVC会自动实例化MOdel和page, 并将Page注入Model
        // 所以，在thymeleaf中可以直接访问Page对象中的数据
        User user = hostHolder.getUser();
        page.setRows(discussPostService.findDiscussPostsRows(user.getId()));
        page.setPath("/user/my-reply");

        List<Comment> list = commentService.findCommentsOfUser(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> comments = new ArrayList<>();
        for(Comment comment : list){
            Map<String, Object> map = new HashMap<>();
            //System.out.println(comment);
            // 判断这个评论回复的是帖子还是评论
            // 如果是评论，需要再次查找，找到对应的帖子，方便跳转
            if(comment.getEntityType() == ENTITY_TYPE_POST){
                // 查找帖子
                int postId = comment.getEntityId();
                DiscussPost post = discussPostService.findDiscussPostById(postId);
                if(post.getStatus() == 2){  // 如果帖子被删除了，直接跳过
                    continue;
                }
                map.put("postId", postId);
                map.put("post01", post);
                map.put("comment01", null);
                //System.out.println("post01>>>>>>>>>>>>>>>>>>>>>>");
            }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
                // 查找回复的comment
                Comment comment1 = commentService.findCommentById(comment.getEntityId());
                int postId = comment1.getEntityId();
                DiscussPost post = discussPostService.findDiscussPostById(postId);
                if(post.getStatus() == 2){  // 如果帖子被删除了，直接跳过
                    continue;
                }
                map.put("postId", postId);
                map.put("comment01", comment1);
                map.put("post01", null);
                //System.out.println("comment01>>>>>>>>>>>>>>>>>>>>>>>>>");
            }

            map.put("comment", comment);

            comments.add(map);
        }
        model.addAttribute("comments", comments);
        return "site/my-reply";
    }
}
