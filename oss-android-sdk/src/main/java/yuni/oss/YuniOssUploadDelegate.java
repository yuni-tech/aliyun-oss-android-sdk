package yuni.oss;

import com.alibaba.sdk.android.oss.internal.RequestMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static yuni.oss.YuniOSS.ENCRYPT_BYTES;

public class YuniOssUploadDelegate implements YuniOSS.UploadDelegate {

    @Override
    public long getContentLength(RequestMessage message) {
        if (!YuniOSS.isNeedEncrypt(message)) {
            return 0;
        }

        if (message.getUploadFilePath() == null) {
            return 0;
        }

        File file = new File(message.getUploadFilePath());
        return file.length() - ENCRYPT_BYTES;
    }

    @Override
    public InputStream getUploadInputStream(RequestMessage message) throws IOException {
        if (!YuniOSS.isNeedEncrypt(message)) {
            return null;
        }
        if (message.getUploadFilePath() == null) {
            return null;
        }
        File file = new File(message.getUploadFilePath());
        FileInputStream inputStream = new FileInputStream(file);
        long length = file.length();
        if (length <= 0) {
            throw new FileNotFoundException("the length of file is 0!");
        }
        inputStream.skip(ENCRYPT_BYTES);
        return inputStream;
    }

//    private class Fill0000InputStream extends InputStream {
//
//        private FileInputStream mFis;
//        private long mFileLength;
//
//        public Fill0000InputStream(File file, FileInputStream fis) {
//            mFis = fis;
//            mFileLength = file.length();
//        }
//
//        @Override
//        public int read() throws IOException {
//            // 前64B填充0,后面的保持原数据
//            if (mFileLength - mFis.available() > ENCRYPT_BYTES) {
//                return mFis.read();
//            }
//            return 0;
//        }
//    }
}
