package com.huawei.hwid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.huawei.dispatcher.AbsEvent;
import com.huawei.dispatcher.Message;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public class HwIdEvent extends AbsEvent<Account> {

    private WeakReference<Context> contextWeakReference;
    private AccountReceiver  accountReceiver = new AccountReceiver(this);
    private Account currentAccount;

    @Override
    public Account start(Context context) {
        contextWeakReference = new WeakReference<>(context);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HwIdConstants.ACTION_GET_SIGN_INFO_SLIENCE);
        LocalBroadcastManager.getInstance(context).registerReceiver(accountReceiver,intentFilter);
        final Account[] result = {null};
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ForegroundHwId foregroundHwId = new ForegroundHwId(context);
        foregroundHwId.slienceSignIn(new HwIdCallback() {
            @Override
            public void onResult(Account account, Throwable throwable) {
                currentAccount = account;
                result[0] = account;
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    @Override
    public boolean paramterCheck() {
        return false;
    }

    @Override
    public String tag() {
        return "hwid";
    }

    @Override
    public void destory() {
        super.destory();
        Context context = contextWeakReference.get();
        if (context != null){
            LocalBroadcastManager.getInstance(context).unregisterReceiver(accountReceiver);
        }
    }

    private static class AccountReceiver extends BroadcastReceiver {
        WeakReference<HwIdEvent> hwIdEventWeakReference;

        public AccountReceiver(HwIdEvent hwIdEvent) {
            this.hwIdEventWeakReference = new WeakReference<>(hwIdEvent);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra(HwIdConstants.KEY_GET_SIGN_INFO)) {
                Account account = intent.getParcelableExtra(HwIdConstants.KEY_GET_SIGN_INFO);
                HwIdEvent hwIdEvent = hwIdEventWeakReference.get();
                if (account != null && hwIdEvent != null){
                    //只有当两次数据不相同的时候才认定需要更新
                    Account currentAccount = hwIdEvent.currentAccount;
                    if (currentAccount == null ||
                            !currentAccount.toString().equals(account.toString())){
                        ListenableFuture<Account> future = MoreExecutors
                                .listeningDecorator(hwIdEvent.executorService)
                                .submit(new Callable<Account>() {
                                    @Override
                                    public Account call() throws Exception {
                                        return account;
                                    }
                                });
                        hwIdEvent.updata(future);
                        //只有当UI发生变化，也就是登出或者切换账号的时候才需要通知
                        if ((currentAccount.getUid() == null && account.getUid() != null) ||
                                !currentAccount.getUid().equals(account.getUid()))
                        hwIdEvent.sendMessage(new Message<>(account,null));
                    }
                }
            }
        }
    }
}
