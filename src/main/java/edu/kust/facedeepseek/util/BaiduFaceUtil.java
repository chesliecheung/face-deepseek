package edu.kust.facedeepseek.util;

import com.baidu.aip.face.AipFace;
import org.json.JSONObject;

public class BaiduFaceUtil {
    public static final String APP_ID = "119870045";
    public static final String API_KEY = "5cWdZsKDVdGhPMIVi9sNb8pQ";
    public static final String SECRET_KEY = "B7G1YwlrMv3QRxlkVSdA9DdLQc8u6xPK";

    private static final AipFace aipFace;

    static {
        aipFace = new AipFace(APP_ID, API_KEY, SECRET_KEY);
        aipFace.setConnectionTimeoutInMillis(2000);
        aipFace.setSocketTimeoutInMillis(60000);
    }

    /** 把可能带 dataURL 头的 base64 规范化为纯base64 */
    public static String normalizeDataUrlBase64(String maybeDataUrl) {
        if (maybeDataUrl == null) return null;
        int comma = maybeDataUrl.indexOf(',');
        // 如果包含 data:image/xxx;base64, 前缀，去掉前缀
        return comma >= 0 ? maybeDataUrl.substring(comma + 1) : maybeDataUrl;
    }

    /** 人脸注册（入库） */
    public static JSONObject faceRegister(String base64OrDataUrl, String groupId, String userId) {
        String pureBase64 = normalizeDataUrlBase64(base64OrDataUrl);
        return aipFace.addUser(pureBase64, "BASE64", groupId, userId, null);
    }

    /** 人脸登录（搜索） */
    public static JSONObject faceLogin(String base64OrDataUrl, String groupId) {
        String pureBase64 = normalizeDataUrlBase64(base64OrDataUrl);
        return aipFace.search(pureBase64, "BASE64", groupId, null);
    }
}