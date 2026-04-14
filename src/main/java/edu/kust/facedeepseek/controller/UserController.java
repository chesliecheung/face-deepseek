package edu.kust.facedeepseek.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.kust.facedeepseek.entity.Article;
import edu.kust.facedeepseek.entity.Follow;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.mapper.ArticleMapper;
import edu.kust.facedeepseek.mapper.FollowMapper;
import edu.kust.facedeepseek.mapper.UserMapper;
import edu.kust.facedeepseek.service.UserService;
import edu.kust.facedeepseek.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/user") // 类级路径：/user
public class UserController {

    @Autowired
    private HttpSession session;

    // 优化：统一用 @Resource 注入（或保留 @Autowired，选一种即可）
    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;



    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private FollowMapper followMapper;


    // 注意：这里改为 UserService 接口，而非 UserServiceImpl 实现类！

    // 获取所有用户信息，接口路径：/user/all
    @GetMapping("all")
    public List<User> showAllUsers() {
        // 优化：直接返回 List<User>，Spring 自动转 JSON（更规范）
        return userService.list();
    }

    // 用户注册接口，接口路径：/user/register
// 在注册接口中添加密码加密
    @PostMapping("register")
    public String register(User user) {
        // 1. 检查用户名/邮箱是否已存在
        if (userService.checkUsernameExists(user.getUsername())) {
            return "用户名已存在";
        }
        if (userService.checkEmailExists(user.getEmail())) {
            return "邮箱已注册";
        }

        // 3. 保存用户
        boolean saved = userService.save(user);
        return saved ? "success" : "fail";
    }

    // 测试接口，用于接收 name 和 password 参数，接口路径：/user/test
    @PostMapping("test")
    public String registerTest(
            @RequestParam("name") String name,  // 必加 @RequestParam 显式绑定
            @RequestParam("password") String password
    ) {
        System.out.println("name: " + name + ", password: " + password);
        return name + "||" + password;
    }


    // 用户名密码登录
    @PostMapping("loginByUsername")
    public Result<User> loginByUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        System.out.println("前端传参: username=" + username + ", password=" + password);

        User dbUser = userService.loginByUsername(username, password);


        if (dbUser != null) {

            session.setAttribute("loginUser", dbUser);
            return Result.success("登录成功", dbUser);
        }
        return Result.fail("用户名或密码错误");
    }

    // 邮箱验证码登录
// 邮箱验证码登录（修复后）
    @PostMapping("loginByEmail")
    public Result<User> loginByEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        System.out.println("前端传参: email=" + email + ", code=" + code);

        User dbUser = userService.loginByEmail(email, code);
        if (dbUser != null) {
            session.setAttribute("loginUser", dbUser);

            return Result.success("登录成功", dbUser);
        }
        return Result.fail("邮箱或验证码错误");
    }

    @GetMapping("info")
    public Result<User> getUserInfo(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {

            return Result.success("已登录", loginUser);
        }
        return Result.fail("未登录");
    }

    @PostMapping("logout")
    public Result<Void> logout(HttpSession session) {
        session.invalidate(); // 清空 session
        return Result.success("退出成功", null);
    }


    //新增加的功能！！！！！！！！！！！！！！！！！！！

//    /**
//     * 获取用户详细信息
//     */
//    @GetMapping("/detail")
//    public Result<?> getUserDetail(@RequestParam Integer id) {
//        User user = userMapper.selectById(id);
//        if (user == null) {
//            return Result.fail("用户不存在");
//        }
//
//        // 获取用户文章数量
//        Integer articleCount = articleMapper.selectCount(
//                new QueryWrapper<Article>().eq("user_id", id).eq("status", "published")
//        );
//
//        // 获取粉丝数量
//        Integer followerCount = followMapper.selectCount(
//                new QueryWrapper<Follow>().eq("followee_id", id)
//        );
//
//        // 获取关注数量
//        Integer followingCount = followMapper.selectCount(
//                new QueryWrapper<Follow>().eq("follower_id", id)
//        );
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("id", user.getId());
//        data.put("username", user.getUsername());
//        data.put("avatar", user.getAvatarUrl());
//        data.put("bio", user.getBio());
//        data.put("articleCount", articleCount);
//        data.put("followerCount", followerCount);
//        data.put("followingCount", followingCount);
//
//        return Result.success("ok", data);
//    }






    /**
     * 新增：获取用户的粉丝数（前端调用：/user/followerCount）
     * 粉丝数 = follow表中「followee_id = 当前用户ID」的记录数
     */
    @GetMapping("/followerCount")
    public Result<Integer> getFollowerCount(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }
        // 关联follow表，查询followee_id=当前用户ID的数量
        QueryWrapper<Follow> qw = new QueryWrapper<>();
        qw.eq("followee_id", loginUser.getId());
        int followerCount = followMapper.selectCount(qw); // 需要注入FollowMapper
        return Result.success("获取粉丝数成功", followerCount);
    }



// 在UserController类中添加以下接口

    /**
     * 获取粉丝增长数据（按月份统计）
     */
    @GetMapping("/followerGrowth")
    public Result<?> getFollowerGrowth(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return Result.fail("未登录");
        }

        // 查询当前用户的粉丝关注记录，按创建时间排序
        QueryWrapper<Follow> qw = new QueryWrapper<>();
        qw.eq("followee_id", loginUser.getId())
                .orderByAsc("create_time");
        List<Follow> follows = followMapper.selectList(qw);

        // 按月份统计粉丝增长
        Map<String, Long> monthlyGrowth = new LinkedHashMap<>();
        // 初始化12个月
        for (int i = 1; i <= 12; i++) {
            monthlyGrowth.put(i + "月", 0L);
        }

        follows.forEach(f -> {
            LocalDateTime createTime = f.getCreateTime();
            String monthKey = createTime.getMonthValue() + "月";
            monthlyGrowth.put(monthKey, monthlyGrowth.get(monthKey) + 1);
        });

        // 为了前端折线图需要，将Map转换为按月份顺序的列表
        List<Long> growthData = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            growthData.add(monthlyGrowth.get(i + "月"));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("growthData", growthData);

        return Result.success("获取粉丝增长数据成功", data);
    }

    @GetMapping("detail") // 注意不要写成 "/detail"
    public Result<?> getUserDetail(@RequestParam Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        // 获取文章数量
        Integer articleCount = articleMapper.selectCount(
                new QueryWrapper<Article>().eq("user_id", id).eq("status", "published")
        );

        // 获取粉丝数量
        Integer followerCount = followMapper.selectCount(
                new QueryWrapper<Follow>().eq("followee_id", id)
        );

        // 获取关注数量
        Integer followingCount = followMapper.selectCount(
                new QueryWrapper<Follow>().eq("follower_id", id)
        );

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("avatarUrl", user.getAvatarUrl());
        data.put("bio", user.getBio() != null ? user.getBio() : "暂无简介");
        data.put("articleCount", articleCount);
        data.put("followerCount", followerCount);
        data.put("followingCount", followingCount);

        return Result.success("ok", data);
    }




}

