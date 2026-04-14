package edu.kust.facedeepseek.controller;

import edu.kust.facedeepseek.entity.User;
import edu.kust.facedeepseek.util.Result;
import org.springframework.beans.factory.annotation.Value; // ✅ 改成这个
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${app.upload.dir:/data/uploads}")
    private String uploadDir;

    @Value("${app.server.base:http://localhost:9999}")
    private String serverBase;

    @PostMapping("/cover")
    public Result<?> uploadCover(@RequestParam("file") MultipartFile file, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return Result.fail("未登录");

        if (file == null || file.isEmpty()) return Result.fail("文件为空");

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = UUID.randomUUID().toString() + ext;

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, filename);
            file.transferTo(dest);

            String url = serverBase + "/uploads/" + filename;
            Map<String, String> body = new HashMap<>();
            body.put("url", url);
            return Result.success("上传成功", body);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("上传失败");
        }
    }
}
