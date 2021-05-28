package com.huawei.karaokedemo.controller;

import ohos.rpc.*;

public class HandleRemoteProxy implements IRemoteBroker {
    private static final int START_COMMAND = IRemoteObject.MIN_TRANSACTION_ID;
    private static final int SET_LYRICS_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 1;
    private static final int START_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 2;
    private static final int PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 3;
    private static final int STOP_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 4;
    private static final int FINISH_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 5;
    private final String TAG = HandleRemoteProxy.class.getSimpleName();
    private final IRemoteObject remote;

    public HandleRemoteProxy(IRemoteObject remote) {
        this.remote = remote;
    }

    @Override
    public IRemoteObject asObject() {
        return remote;
    }

    public void start() {
        sendRequest(START_COMMAND);
    }

    public void setLyrics(String lyrics) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        data.writeString(lyrics);
        try {
            remote.sendRequest(SET_LYRICS_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "Set lyrics remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    public void startPlay() {
        sendRequest(START_PLAY_COMMAND);
    }

    public void playAudio(byte[] bytes) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        data.writeBytes(bytes);
        try {
            remote.sendRequest(PLAY_COMMAND, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "Play remote action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }

    public void stop() {
        sendRequest(STOP_PLAY_COMMAND);
    }

    public void finish() {
        sendRequest(FINISH_COMMAND);
    }

    private void sendRequest(int command) {
        MessageParcel data = MessageParcel.obtain();
        MessageParcel reply = MessageParcel.obtain();
        MessageOption option = new MessageOption(MessageOption.TF_SYNC);
        try {
            remote.sendRequest(command, data, reply, option);
        } catch (RemoteException e) {
            LogUtil.error(TAG, "play action error " + e.getMessage());
        } finally {
            data.reclaim();
            reply.reclaim();
        }
    }
}
