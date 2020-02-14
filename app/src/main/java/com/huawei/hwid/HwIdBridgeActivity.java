package com.huawei.hwid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hmssample.Constant;
import com.huawei.logger.Log;

public class HwIdBridgeActivity extends Activity {
    public static final int REQUEST_SIGN_IN_LOGIN = 1002;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HuaweiIdSignInOptions options = new HuaweiIdSignInOptions
                .Builder(HuaweiIdSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestUid()
                .requestAccessToken()
                .build();
        HuaweiIdSignInClient mSignInClient = HuaweiIdSignIn.getClient(this, options);
        startActivityForResult(mSignInClient.getSignInIntent(),
                REQUEST_SIGN_IN_LOGIN);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGN_IN_LOGIN) {
            //login success
            //get user message by getSignedInAccountFromIntent
            Account account = null;
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                account = Utils.entityTranslate(huaweiAccount);
            }
            Utils.sendAccountBroadcast(this,account);
            finish();
        }
    }
}
