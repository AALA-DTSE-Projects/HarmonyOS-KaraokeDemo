package com.huawei.karaokedemo;

import com.huawei.karaokedemo.controller.LogUtil;
import com.huawei.karaokedemo.controller.ResultRemote;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ResultServiceAbility extends Ability {
    private static final String TAG = ResultServiceAbility.class.getName();
    private final ResultRemote remote = new ResultRemote(this);

    @Override
    public void onStart(Intent intent) {
        LogUtil.debug(TAG, "ResultServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        LogUtil.debug(TAG, "ResultServiceAbility::onBackground");
        super.onBackground();
    }

    @Override
    public void onStop() {
        LogUtil.debug(TAG, "ResultServiceAbility::onStop");
        super.onStop();
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return remote.asObject();
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}