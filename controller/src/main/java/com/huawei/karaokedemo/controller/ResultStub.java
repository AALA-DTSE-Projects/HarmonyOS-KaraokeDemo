package com.huawei.karaokedemo.controller;

import ohos.rpc.*;

public abstract class ResultStub extends RemoteObject implements IResultInterface {
    static final String DESCRIPTOR = "com.huawei.karaokedemo.controller.IResultInterface";
    static final int CONNECT_COMMAND = RemoteObject.MIN_TRANSACTION_ID;
    static final int DISCONNECT_COMMAND = RemoteObject.MIN_TRANSACTION_ID + 1;

    public ResultStub(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        String token = data.readInterfaceToken();
        if (!token.equals(DESCRIPTOR)) {
            return false;
        }
        switch (code) {
            case CONNECT_COMMAND:
                connect(data.readString());
                return true;
            case DISCONNECT_COMMAND:
                disconnect(data.readString());
                return true;
            default:
                break;
        }
        return false;
    }
}
