package com.huawei.hwid;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.huawei.hms.support.api.hwid.SignInHuaweiId;

public class Utils {
    public static Account entityTranslate(SignInHuaweiId signInHuaweiId){
        Account account = null;
        if (signInHuaweiId != null){
            account = new Account();
            account.setAt(signInHuaweiId.getAccessToken());
            account.setCountryCode(signInHuaweiId.getCountryCode());
            account.setEmail(signInHuaweiId.getEmail());
            account.setFamilyName(signInHuaweiId.getFamilyName());
            account.setGender(signInHuaweiId.getGender());
            account.setGivenName(signInHuaweiId.getGivenName());
            account.setNick(signInHuaweiId.getDisplayName());
            account.setPicUrl(signInHuaweiId.getPhotoUriString());
            account.setUid(signInHuaweiId.getUid());
        }
        return account;
    }
    public static void sendAccountBroadcast(Context context,Account account){
        Intent intent = new Intent();
        intent.setAction(HwIdConstants.ACTION_GET_SIGN_INFO);
        intent.putExtra(HwIdConstants.KEY_GET_SIGN_INFO,account);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static void sendAccountBroadcastSlience(Context context,Account account){
        Intent intent = new Intent();
        intent.setAction(HwIdConstants.ACTION_GET_SIGN_INFO_SLIENCE);
        intent.putExtra(HwIdConstants.KEY_GET_SIGN_INFO,account);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
