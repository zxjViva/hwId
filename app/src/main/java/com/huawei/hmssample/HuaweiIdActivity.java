package com.huawei.hmssample;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.huawei.dispatcher.JDispatcher;
import com.huawei.dispatcher.JObserver;
import com.huawei.dispatcher.Observer;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.huawei.hms.support.api.hwid.SignInHuaweiId;
import com.huawei.hmssample.common.ICallBack;
import com.huawei.hwid.Account;
import com.huawei.hwid.BackgroundHwId;
import com.huawei.hwid.ForegroundHwId;
import com.huawei.hwid.HwIdCallback;
import com.huawei.hwid.HwIdEvent;
import com.huawei.hwid.HwIdSignOutCallback;
import com.huawei.logger.Log;
import com.huawei.logger.LoggerActivity;

/**
 *  Codelab
 *  Demonstration of HuaweiId
 */
public class HuaweiIdActivity extends LoggerActivity implements OnClickListener {

    //Log tag
    public static final String TAG = "HuaweiIdActivity";
    private HuaweiIdSignInClient mSignInClient;
    HuaweiIdSignInOptions mSignInOptions;
    private JObserver<Account> accountJObserver1;
    private JObserver<Account> accountJObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huaweiid);

        findViewById(R.id.hwid_signin).setOnClickListener(this);
        findViewById(R.id.hwid_signout).setOnClickListener(this);
        findViewById(R.id.hwid_signInCode).setOnClickListener(this);

        //sample log Please ignore
        addLogFragment();
//        BackgroundHwId at = new BackgroundHwId();
//        at.getAccount(this, new HwIdCallback() {
//            @Override
//            public void onResult(Account account, Throwable throwable) {
//                if (account != null){
//                    String at1 = account.getAt();
//                }
//            }
//        });
        JDispatcher<Account> accountJDispatcher = new JDispatcher<>();
        accountJDispatcher.cancelEvent(HwIdEvent.class);
        accountJObserver = accountJDispatcher.start(HwIdEvent.class, this, new Observer<Account>() {
            @Override
            public void onResult(Account account, Throwable error) {
                android.util.Log.e("zxj", "start: " + account.toString());

            }
        });
        accountJObserver1 = accountJDispatcher.startSticky(HwIdEvent.class, this, new Observer<Account>() {
            @Override
            public void onResult(Account account, Throwable error) {
                android.util.Log.e("zxj", "startSticky: " + account.toString());

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accountJObserver.unRegister();
        accountJObserver1.unRegister();
    }

    /**
     * Codelab Code
     * Pull up the authorization interface by getSignInIntent
     */
    private void signIn() {
        //Initialize the HuaweiIdSignInClient object by calling the getClient method of HuaweiIdSignIn
        mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN)
                .requestAccessToken()
                .requestIdToken("").build();
        mSignInClient = HuaweiIdSignIn.getClient(HuaweiIdActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
    }

    private void signInCode() {
        mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestServerAuthCode().build();
        mSignInClient = HuaweiIdSignIn.getClient(HuaweiIdActivity.this, mSignInOptions);
        startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN_CODE);
    }

    /**
     * Codelab Code
     * sign Out by signOut
     */
    private void signOut() {
        Task<Void> signOutTask = mSignInClient.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i(TAG, "signOut Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i(TAG, "signOut fail");
            }
        });
    }

    private void validateIdToken(String idToken) {
        if (TextUtils.isEmpty(idToken)) {
            Log.i(TAG, "ID Token is empty");
        } else {
            IDTokenParser idTokenParser = new IDTokenParser();
            try {
                idTokenParser.verify(idToken, new ICallBack() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onSuccess(String idTokenJsonStr) {
                        if (!TextUtils.isEmpty(idTokenJsonStr)) {
                            Log.i(TAG, "id Token Validate Success, verify signature: " + idTokenJsonStr);
                        } else {
                            Log.i(TAG, "Id token validate failed.");
                        }
                    }

                    @Override
                    public void onFailed() {
                        Log.i(TAG, "Id token validate failed.");
                    }
                });
            } catch (Exception e) {
                Log.i(TAG, "id Token validate failed." + e.getMessage());
            }
        }
    }


    /**
     * Codelab Code
     * Silent SignIn by silentSignIn
     */
    private void silentSignIn() {
        Task<SignInHuaweiId> task = mSignInClient.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<SignInHuaweiId>() {
            @Override
            public void onSuccess(SignInHuaweiId signInHuaweiId) {
                Log.i(TAG, "silentSignIn success");
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //if Failed use getSignInIntent
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    signIn();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hwid_signin:
                ForegroundHwId foregroundHwId = new ForegroundHwId(this);
                foregroundHwId.signIn(this, new HwIdCallback() {
                    @Override
                    public void onResult(Account account, Throwable throwable) {
                        if (account != null){
                            String at = account.getAt();
                        }
                    }
                });
                break;
            case R.id.hwid_signout:
                ForegroundHwId foregroundHwId2 = new ForegroundHwId(this);
                foregroundHwId2.signOut(new HwIdSignOutCallback() {
                    @Override
                    public void onSuccessed(boolean successed) {
                    }
                });
                break;
            case R.id.hwid_signInCode:
                ForegroundHwId foregroundHwId3 = new ForegroundHwId(this);
                foregroundHwId3.slienceSignIn(new HwIdCallback() {
                    @Override
                    public void onResult(Account account, Throwable throwable) {
                        if (account != null){
                            String at = account.getAt();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN) {
            //login success
            //get user message by getSignedInAccountFromIntent
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                Log.i(TAG, huaweiAccount.getDisplayName() + " signIn success ");
                Log.i(TAG,"AccessToken: " + huaweiAccount.getAccessToken());
                Log.i(TAG,"Display Photo url: " + huaweiAccount.getPhotoUriString());
                validateIdToken(huaweiAccount.getIdToken());
            } else {
                Log.i(TAG, "signIn failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
        if (requestCode == Constant.REQUEST_SIGN_IN_LOGIN_CODE) {
            //login success
            Task<SignInHuaweiId> signInHuaweiIdTask = HuaweiIdSignIn.getSignedInAccountFromIntent(data);
            if (signInHuaweiIdTask.isSuccessful()) {
                SignInHuaweiId huaweiAccount = signInHuaweiIdTask.getResult();
                Log.i(TAG, "signIn get code success.");
                Log.i(TAG,"ServerAuthCode: " + huaweiAccount.getServerAuthCode());
                Log.i(TAG,"Display Photo url: " + huaweiAccount.getPhotoUriString());

                /**** english doc:For security reasons, the operation of changing the code to an AT must be performed on your server. The code is only an example and cannot be run. ****/
//                getAccessTokenByCode(huaweiAccount.getServerAuthCode());
                /**********************************************************************************************/
            } else {
                Log.i(TAG, "signIn get code failed: " + ((ApiException) signInHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    /**
     * sample log Please ignore
     */
    private void addLogFragment() {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        final LogFragment fragment = new LogFragment();
        transaction.replace(R.id.framelog, fragment);
        transaction.commit();
    }

}
