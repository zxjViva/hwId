package com.huawei.hwid;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.UniversalTimeScale;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;

import java.lang.ref.WeakReference;


public class ForegroundHwId {
    private final HuaweiIdSignInClient mSignInClient;
    private final WeakReference<Context> contextWeakReference;

    public ForegroundHwId(Context context) {
        contextWeakReference = new WeakReference<>(context);
        HuaweiIdSignInOptions options = new HuaweiIdSignInOptions
                .Builder(HuaweiIdSignInOptions.DEFAULT_GAMES_SIGN_IN)
                .requestUid()
                .requestAccessToken()
                .build();
        mSignInClient = HuaweiIdSignIn.getClient(context, options);

    }

    /**
     * 用透明activity 实现中转
     * @param activity
     * @param callback
     */
    public void signIn(Activity activity,HwIdCallback callback) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Account account = null;
                if (intent != null && intent.hasExtra(HwIdConstants.KEY_GET_SIGN_INFO)){
                    account = intent.getParcelableExtra(HwIdConstants.KEY_GET_SIGN_INFO);
                }
                if (callback != null){
                    callback.onResult(account,account == null ? (new Exception()) : null);
                }
                LocalBroadcastManager.getInstance(activity).unregisterReceiver(this);
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HwIdConstants.ACTION_GET_SIGN_INFO);
        LocalBroadcastManager.getInstance(activity)
                .registerReceiver(broadcastReceiver,intentFilter);
        activity.startActivity(new Intent(activity,HwIdBridgeActivity.class));
    }

    public void slienceSignIn(HwIdCallback callback) {
        Task<SignInHuaweiId> task = mSignInClient.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<SignInHuaweiId>() {
            @Override
            public void onSuccess(SignInHuaweiId signInHuaweiId) {
                Account account = Utils.entityTranslate(signInHuaweiId);
                Context context = contextWeakReference.get();
                if (callback != null){
                    Utils.sendAccountBroadcastSlience(context,account);
                }
                if (callback != null){
                    callback.onResult(account,account == null ? (new Exception()) : null);
                }


            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (callback != null){
                    callback.onResult(null,e);
                }
            }
        });
    }

    public void signOut(HwIdSignOutCallback callback) {
        Task<Void> signOutTask = mSignInClient.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (callback != null){
                    callback.onSuccessed(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (callback != null){
                    callback.onSuccessed(false);
                }
            }
        });
    }
}
