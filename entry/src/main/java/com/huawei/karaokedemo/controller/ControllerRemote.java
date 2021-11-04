package com.huawei.karaokedemo.controller;

import com.huawei.karaokedemo.model.DisconnectEvent;
import ohos.rpc.*;
import org.greenrobot.eventbus.EventBus;

public class ControllerRemote extends RemoteObject implements IRemoteBroker {
    static final int DISCONNECT_COMMAND = RemoteObject.MIN_TRANSACTION_ID;

    public ControllerRemote(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        if (code == DISCONNECT_COMMAND) {
            EventBus.getDefault().post(new DisconnectEvent());
            return true;
        }
        return false;
    }
}
