package edu.kust.facedeepseek.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 验证码工具类，支持生成数字验证码和图形验证码，以及验证码的验证和过期管理
 */
public class VerificationCode {

    // 存储验证码的缓存，key为标识(如手机号/邮箱)，value为验证码
    private static final ConcurrentHashMap<String, CodeInfo> CODE_CACHE = new ConcurrentHashMap<>();

    // 验证码默认过期时间(分钟)
    private static final int DEFAULT_EXPIRE_MINUTES = 5;

    // 验证码字符集
    private static final String CODE_CHARACTERS = "0123456789";

    // 图形验证码宽度
    private static final int IMAGE_WIDTH = 120;

    // 图形验证码高度
    private static final int IMAGE_HEIGHT = 40;

    // 图形验证码字符数
    private static final int IMAGE_CODE_LENGTH = 4;

    /**
     * 验证码信息内部类，包含验证码和过期时间
     */
    private static class CodeInfo {
        private String code;
        private long expireTime;

        public CodeInfo(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度
     * @return 数字验证码
     */
    public static String generateNumberCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须大于0");
        }

        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CODE_CHARACTERS.length());
            codeBuilder.append(CODE_CHARACTERS.charAt(index));
        }

        return codeBuilder.toString();
    }

    /**
     * 生成默认长度(6位)的数字验证码
     * @return 6位数字验证码
     */
    public static String generateNumberCode() {
        return generateNumberCode(6);
    }

    /**
     * 生成图形验证码(返回Base64编码字符串)
     * @return 包含图形验证码Base64和验证码值的数组，[0]为Base64字符串，[1]为验证码值
     */
    public static String[] generateImageCode() {
        // 创建图片缓冲区
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 28));

        // 生成随机干扰线
        Random random = new Random();
        for (int i = 0; i < 15; i++) {
            g.setColor(getRandomColor(100, 200));
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 生成验证码
        String code = generateNumberCode(IMAGE_CODE_LENGTH);
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomColor(50, 150));
            // 随机旋转角度，增加识别难度
            int rotate = random.nextInt(20) - 10;
            ((Graphics2D) g).rotate(rotate * Math.PI / 180, 25 + i * 25, 25);
            g.drawString(String.valueOf(code.charAt(i)), 20 + i * 25, 30);
            ((Graphics2D) g).rotate(-rotate * Math.PI / 180, 25 + i * 25, 25);
        }

        g.dispose();

        // 转换为Base64
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            String base64Image = "data:image/png;base64," +
                    Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return new String[]{base64Image, code};
        } catch (IOException e) {
            throw new RuntimeException("生成图形验证码失败", e);
        }
    }

    /**
     * 保存验证码到缓存，带过期时间
     * @param key 验证码标识(如手机号/邮箱/sessionId)
     * @param code 验证码
     * @param expireMinutes 过期时间(分钟)
     */
    public static void saveCode(String key, String code, int expireMinutes) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("验证码标识不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("验证码不能为空");
        }

        long expireTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expireMinutes);
        CODE_CACHE.put(key, new CodeInfo(code, expireTime));
    }

    /**
     * 保存验证码到缓存，使用默认过期时间(5分钟)
     * @param key 验证码标识
     * @param code 验证码
     */
    public static void saveCode(String key, String code) {
        // 修复：第二个参数从 key 改为 code
        saveCode(key, code, DEFAULT_EXPIRE_MINUTES);
    }

    /**
     * 验证验证码是否正确
     * @param key 验证码标识
     * @param inputCode 用户输入的验证码
     * @param removeAfterVerify 验证后是否移除验证码(防止重复使用)
     * @return 验证结果 true-验证通过 false-验证失败
     */
    public static boolean verifyCode(String key, String inputCode, boolean removeAfterVerify) {
        if (key == null || inputCode == null) {
            return false;
        }

        CodeInfo codeInfo = CODE_CACHE.get(key);
        if (codeInfo == null) {
            return false; // 验证码不存在
        }

        if (codeInfo.isExpired()) {
            CODE_CACHE.remove(key); // 移除过期验证码
            return false; // 验证码已过期
        }

        boolean result = codeInfo.getCode().equals(inputCode);

        if (removeAfterVerify) {
            CODE_CACHE.remove(key); // 验证后移除
        }

        return result;
    }

    /**
     * 验证验证码是否正确，验证后自动移除验证码
     * @param key 验证码标识
     * @param inputCode 用户输入的验证码
     * @return 验证结果 true-验证通过 false-验证失败
     */
    public static boolean verifyCode(String key, String inputCode) {
        return verifyCode(key, inputCode, true);
    }

    /**
     * 清除指定的验证码
     * @param key 验证码标识
     */
    public static void removeCode(String key) {
        CODE_CACHE.remove(key);
    }

    /**
     * 清除所有过期的验证码
     */
    public static void clearExpiredCodes() {
        CODE_CACHE.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 生成随机颜色
     * @param min 最小色值
     * @param max 最大色值
     * @return 随机颜色
     */
    private static Color getRandomColor(int min, int max) {
        Random random = new Random();
        int r = min + random.nextInt(max - min);
        int g = min + random.nextInt(max - min);
        int b = min + random.nextInt(max - min);
        return new Color(r, g, b);
    }
}
