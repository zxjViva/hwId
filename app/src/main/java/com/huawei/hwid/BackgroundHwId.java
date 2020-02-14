package com.huawei.hwid;

import android.app.Activity;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.entity.auth.Scope;
import com.huawei.hms.support.api.hwid.HuaweiId;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hms.support.api.hwid.SignInResult;
import com.huawei.hwid.exception.HwIdConnectedFailedException;
import com.huawei.hwid.exception.HwIdSuspendException;

/**
 * 静默获取华为账号信息，不触发登录相关操作，本来目的是为了获取AT的
 */
public class BackgroundHwId implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener, ResultCallback<SignInResult> {

    private HuaweiApiClient client;
    private HwIdCallback callback;
    private Activity context;

    public void getAccount(Activity context, HwIdCallback callback){
        this.context = context;
        this.callback = callback;
        HuaweiIdSignInOptions options = new HuaweiIdSignInOptions
                .Builder(HuaweiIdSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestUid()
                .requestAccessToken()
                .build();

        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiId.SIGN_IN_API, options)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addScope(new Scope(""))
                .build();

        if (!client.isConnected() && !client.isConnecting()){
            client.connect(context);
        }

    }

    @Override
    public void onConnected() {
        PendingResult<SignInResult> signInResultPendingResult = HuaweiId.HuaweiIdApi.signInBackend(client);
        signInResultPendingResult.setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        callback.onResult(null,new HwIdSuspendException());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        callback.onResult(null,new HwIdConnectedFailedException());
    }

    @Override
    public void onResult(SignInResult signInResult) {
        Account account = null;
        if (signInResult != null && signInResult.isSuccess()){
            SignInHuaweiId signInHuaweiId = signInResult.getSignInHuaweiId();
            account = Utils.entityTranslate(signInHuaweiId);
        }
        if (callback != null){
            Utils.sendAccountBroadcastSlience(context,account);
            callback.onResult(account,null);
        }
    }
}
