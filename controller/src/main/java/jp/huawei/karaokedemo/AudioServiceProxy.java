package jp.huawei.karaokedemo;

import com.huawei.karaokedemo.controller.LogUtil;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;

public class AudioServiceProxy implements IAudioInterface {

    private static final String TAG = AudioServiceProxy.class.getName();
    private final IRemoteObject remoteObject;

    public AudioServiceProxy(IRemoteObject remoteObject) {
        this.remoteObject = remoteObject;
    }

    @Override
    public void setLyrics(String lyrics) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);

        data.writeInterfaceToken(AudioServiceStub.DESCRIPTOR);
        data.writeString(lyrics);

        try {
            remoteObject.sendRequest(AudioServiceStub.SET_LYRICS_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "set lyrics action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    @Override
    public void startPlay() {
        sendRequest(AudioServiceStub.START_PLAY_COMMAND);
    }

    @Override
    public void playAudio(byte[] buffer) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);

        data.writeInterfaceToken(AudioServiceStub.DESCRIPTOR);
        data.writeByteArray(buffer);

        try {
            remoteObject.sendRequest(AudioServiceStub.PLAY_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "play action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    @Override
    public void stopPlay() {
        sendRequest(AudioServiceStub.STOP_PLAY_COMMAND);
    }

    @Override
    public void finish() {
        sendRequest(AudioServiceStub.FINISH_COMMAND);
    }

    private void sendRequest(int command) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);

        data.writeInterfaceToken(AudioServiceStub.DESCRIPTOR);

        try {
            remoteObject.sendRequest(command, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "play action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    @Override
    public IRemoteObject asObject() {
        return remoteObject;
    }
}
