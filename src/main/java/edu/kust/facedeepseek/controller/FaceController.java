package edu.kust.facedeepseek.controller;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import edu.kust.facedeepseek.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.util.BaiduFaceUtil;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/face")
public class FaceController {

    private static final Logger log = LoggerFactory.getLogger(FaceController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

//    /** Canvas base64 注册：username/password/email/base64(dataURL可直接传) */
//    @PostMapping("/registerUserByCanvas")
//    public String registerUserByCanvas(@RequestBody JSONObject body) {
//        try {
//            String username = body.getString("username");
//            String password = body.getString("password");
//            String email    = body.getString("email");
//            String base64   = body.getString("base64"); // 可为 dataURL 或纯base64
//
//            org.json.JSONObject res = BaiduFaceUtil.faceRegister(base64, "408", username);
//
//            if (res.optInt("error_code", -1) == 0) {
//                String faceToken = res.getJSONObject("result").optString("face_token");
//
//                // TODO: 持久化到DB（示例）
//                User user = new User();
//                user.setUsername(username);
//                user.setPassword(password);
//                user.setEmail(email);
//                user.setImagetoken(faceToken);
//                boolean isSaved = userService.save(user);
//                if (!isSaved) {
//                    return "{ \"msg\": \"注册成功\", \"faceToken\": \"" + faceToken + "\" }";
//                }
//
//
//            }
//            return res.toString(2);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return "{ \"msg\": \"注册失败(JSON 解析错误)\" }";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "{ \"msg\": \"注册失败\" }";
//        }
//    }


    @PostMapping("/registerUserByCanvas")
    public String registerUserByCanvas(@RequestBody JSONObject body) {
        try {
            String username = body.getString("username");
            String password = body.getString("password");
            String email    = body.getString("email");
            String base64   = body.getString("base64");

            org.json.JSONObject res = BaiduFaceUtil.faceRegister(base64, "408", username);

            if (res.optInt("error_code", -1) == 0) {
                // 不再存储 face_token，而是存储 user_id（即用户名）
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setImagetoken(username); // 存储用户名而不是 face_token
                boolean isSaved = userService.save(user);
                if (!isSaved) {
                    return "{ \"msg\": \"注册成功\", \"faceToken\": \"" + username + "\" }";
                }
            }
            return res.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
            return "{ \"msg\": \"注册失败(JSON 解析错误)\" }";
        } catch (Exception e) {
            e.printStackTrace();
            return "{ \"msg\": \"注册失败\" }";
        }
    }

    /** Canvas base64 登录：body 里只要 base64(dataURL可直接传) */
    @PostMapping("/loginByCanvas")
    public JSONObject loginByCanvas(@RequestBody JSONObject body) {
        // 统一返回格式
        JSONObject result = new JSONObject();
        try {
            String base64 = body.getString("base64");
            if (base64 == null || base64.trim().isEmpty()) {
                result.put("success", false);
                result.put("msg", "请上传人脸图片");
                return result;
            }

            // 调用百度人脸登录接口
            org.json.JSONObject baiduRes = BaiduFaceUtil.faceLogin(base64, "408");
            log.info("百度人脸接口返回: {}", baiduRes.toString(2)); // 打印完整返回，便于排查

            int errorCode = baiduRes.optInt("error_code", -1);
            if (errorCode != 0) {
                // 百度接口调用失败（如图片无效、人脸不清晰等）
                result.put("success", false);
                result.put("msg", "人脸验证失败：" + baiduRes.optString("error_msg", "未知错误"));
                return result;
            }

            // 解析人脸标识（关键：确保与注册时存储的字段一致）
            org.json.JSONObject userObj = baiduRes.getJSONObject("result")
                    .optJSONArray("user_list") // 注意：若未找到用户，user_list可能为空
                    .optJSONObject(0);

            if (userObj == null) {
                result.put("success", false);
                result.put("msg", "未找到匹配的人脸信息");
                return result;
            }

            String faceToken = userObj.getString("user_id"); // 核心：人脸唯一标识
            log.info("提取到的faceToken: {}", faceToken); // 打印token，用于与数据库对比

            // 查询数据库用户
            User user = userService.loginByFaceToken(faceToken);
            if (user != null) {
                session.setAttribute("loginUser", user);
                result.put("success", true);
                result.put("msg", "登录成功");
                result.put("data", user); // 可选：返回用户基本信息
            } else {
                result.put("success", false);
                result.put("msg", "未找到用户（人脸标识未注册）");
                log.warn("faceToken: {} 在数据库中无匹配用户", faceToken); // 关键日志
            }

        } catch (Exception e) {
            log.error("人脸登录异常", e); // 打印完整异常堆栈
            result.put("success", false);
            result.put("msg", "登录失败：" + e.getMessage());
        }
        return result;
    }
}