package yuni.oss;

import android.util.Base64;

import com.alibaba.sdk.android.oss.internal.RequestMessage;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class YuniOSS {

    public static final int ENCRYPT_BYTES = 64;

    public interface UploadDelegate {
        long getContentLength(RequestMessage requestMessage);
        InputStream getUploadInputStream(RequestMessage requestMessage) throws IOException;
    }

    public static YuniOSS.UploadDelegate uploadDelegate = new YuniOssUploadDelegate();

    private static Map<String, Boolean> sObjectKeyMap = new HashMap<String, Boolean>();

    /**
     * 添加需要加密的上传文件
     * @param request
     */
    public static synchronized void setNeedEncrypt(PutObjectRequest request) {
        String key = request.getBucketName() + "-" + request.getObjectKey();
        sObjectKeyMap.put(key, true);
    }

    public static synchronized void removeNeedEncrypt(PutObjectRequest request) {
        String key = request.getBucketName() + "-" + request.getObjectKey();
        sObjectKeyMap.remove(key);
    }

    public static synchronized boolean isNeedEncrypt(RequestMessage message) {
        String key = message.getBucketName() + "-" + message.getObjectKey();
        return sObjectKeyMap.containsKey(key);
    }

    public static String getOssContentMd5(File file, boolean encrypt) throws IOException {
        FileInputStream in = new FileInputStream(file);
        if (encrypt) {
            in.skip(ENCRYPT_BYTES);
        }
        try {
            FileChannel channel = in.getChannel();
            long position = 0;
            long total = file.length();
            long page = 1024 * 1024 * 5;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            while (position < total) {
                long size = page <= total - position ? page : total - position;
                MappedByteBuffer byteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, position, size);
                if (position == 0) {

                } else {
                    md5.update(byteBuffer);
                }
                position += size;

            }
            byte[] b = md5.digest();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
