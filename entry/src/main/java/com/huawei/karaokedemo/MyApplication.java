package com.huawei.karaokedemo;

import ohos.aafwk.ability.AbilityPackage;
import org.greenrobot.eventbus.EventBus;

public class MyApplication extends AbilityPackage {
    @Override
    public void onInitialize() {
        super.onInitialize();
        EventBus.initThreadForHos(getUITaskDispatcher());
    }
}
