package com.huawei.karaokedemo;

import com.huawei.karaokedemo.controller.HandleRemote;
import com.huawei.karaokedemo.controller.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;

public class ControllerServiceAbility extends Ability {
    private static final String TAG = ControllerServiceAbility.class.getName();
    private final HandleRemote remote = new HandleRemote(this);

    @Override
    public void onStart(Intent intent) {
        LogUtil.debug(TAG, "ControllerServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public void onBackground() {
        LogUtil.debug(TAG, "ControllerServiceAbility::onBackground");
        super.onBackground();
    }

    @Override
    public void onStop() {
        LogUtil.debug(TAG, "ControllerServiceAbility::onStop");
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