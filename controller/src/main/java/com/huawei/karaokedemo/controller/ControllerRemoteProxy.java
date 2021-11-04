package com.huawei.karaokedemo.controller;

import ohos.rpc.*;

public class ControllerRemoteProxy implements IRemoteBroker {
    private final String TAG = ControllerRemoteProxy.class.getName();
    static final int DISCONNECT_COMMAND = RemoteObject.MIN_TRANSACTION_ID;
    private final IRemoteObject remote;

    public ControllerRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public void disconnect() {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        try {
            remote.sendRequest(DISCONNECT_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

}
