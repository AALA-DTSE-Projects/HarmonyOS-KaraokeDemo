package com.huawei.karaokedemo;

import com.huawei.karaokedemo.controller.ControllerRemote;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.IRemoteObject;

public class ServiceAbility extends Ability {
    private final ControllerRemote remote = new ControllerRemote("controller");

    @Override
    public IRemoteObject onConnect(Intent intent) {
        return remote.asObject();
    }

}