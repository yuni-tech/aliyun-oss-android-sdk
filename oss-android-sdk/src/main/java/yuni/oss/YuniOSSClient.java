package yuni.oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

public class YuniOSSClient extends OSSClient {

    public YuniOSSClient(Context context, String endpoint, OSSCredentialProvider credentialProvider) {
        super(context, endpoint, credentialProvider);
    }

    public YuniOSSClient(Context context, String endpoint, OSSCredentialProvider credentialProvider, ClientConfiguration conf) {
        super(context, endpoint, credentialProvider, conf);
    }

    public YuniOSSClient(Context context, OSSCredentialProvider credentialProvider, ClientConfiguration conf) {
        super(context, credentialProvider, conf);
    }

    /**
     * 以加密的方式
     * @param request
     * @param completedCallback
     * @return
     */
    public OSSAsyncTask<PutObjectResult> asyncPutEncryptObject(PutObjectRequest request, final OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        YuniOSS.setNeedEncrypt(request);
        return super.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                YuniOSS.removeNeedEncrypt(request);
                completedCallback.onSuccess(request, result);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                YuniOSS.removeNeedEncrypt(request);
                completedCallback.onFailure(request, clientException, serviceException);
            }
        });
    }
}
