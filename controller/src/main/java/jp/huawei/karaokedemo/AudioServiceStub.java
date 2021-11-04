package jp.huawei.karaokedemo;

import ohos.rpc.*;

public abstract class AudioServiceStub extends RemoteObject implements IAudioInterface {

    static final String DESCRIPTOR = "jp.huawei.karaokedemo.IAudioInterface";
    static final int CONNECT_COMMAND = IRemoteObject.MIN_TRANSACTION_ID;
    static final int START_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 1;
    static final int PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 2;
    static final int STOP_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 3;
    static final int FINISH_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 4;

    public AudioServiceStub(String descriptor) {
        super(descriptor);
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    public static IAudioInterface asInterface(IRemoteObject remoteObject) {
        if (remoteObject == null) {
            return null;
        }
        IRemoteBroker broker = remoteObject.queryLocalInterface(DESCRIPTOR);
        if (broker instanceof IAudioInterface) {
            return (IAudioInterface) broker;
        } else {
            return new AudioServiceProxy(remoteObject);
        }
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
        String token = data.readInterfaceToken();
        if (!DESCRIPTOR.equals(token)) {
            return false;
        }
        switch (code) {
            case CONNECT_COMMAND:
                connect(data.readString());
                return true;
            case START_PLAY_COMMAND:
                startPlay();
                return true;
            case PLAY_COMMAND:
                playAudio(data.readByteArray());
                return true;
            case STOP_PLAY_COMMAND:
                stopPlay();
                return true;
            case FINISH_COMMAND:
                finish();
                return true;
            default:
                return super.onRemoteRequest(code, data, reply, option);
        }
    }
}
