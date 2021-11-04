package com.huawei.karaokedemo.controller;

import jp.huawei.karaokedemo.AudioServiceStub;
import jp.huawei.karaokedemo.IAudioInterface;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.rpc.*;

public class HandleRemote extends RemoteObject implements IRemoteBroker {
    private final String TAG = HandleRemote.class.getName();
    private static final int START_COMMAND = IRemoteObject.MIN_TRANSACTION_ID;
    private static final int START_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 1;
    private static final int PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 2;
    private static final int STOP_PLAY_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 3;
    private static final int FINISH_COMMAND = IRemoteObject.MIN_TRANSACTION_ID + 4;
    private final Ability ability;
    private boolean isConnected;
    private String deviceId;
    private IAudioInterface remoteService;

    private final IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
            remoteService = AudioServiceStub.asInterface(remote);
            remoteService.connect(deviceId);
            LogUtil.info(TAG, "Android service connect done!");
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            LogUtil.info(TAG, "Android service disconnect done!");
            isConnected = false;
        }
    };

    public HandleRemote(Ability ability) {
        super("Karaoke handle remote");
        this.ability = ability;
    }

    @Override
    public IRemoteObject asObject() {
        return this;
    }

    @Override
    public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) {
        switch (code) {
            case START_COMMAND:
                if (!isConnected) {
                    startAndroidApp();
                    deviceId = data.readString();
                    connectToAndroidService();
                }
                return true;
            case START_PLAY_COMMAND:
                if (remoteService != null) {
                    remoteService.startPlay();
                }
            case PLAY_COMMAND:
                if (remoteService != null) {
                    remoteService.playAudio(data.getBytes());
                }
                return true;
            case STOP_PLAY_COMMAND:
                if (remoteService != null) {
                    remoteService.stopPlay();
                }
                return true;
            case FINISH_COMMAND:
                if (remoteService != null) {
                    remoteService.finish();
                }
                ability.disconnectAbility(connection);
                return true;
            default:
                return false;
        }
    }

    private void startAndroidApp() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(Const.ANDROID_PACKAGE_NAME)
                .withAbilityName(Const.ANDROID_ACTIVITY_NAME)
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .build();
        intent.setOperation(operation);
        ability.startAbility(intent);
    }

    private void connectToAndroidService() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(Const.ANDROID_PACKAGE_NAME)
                .withAbilityName(Const.ANDROID_SERVICE_NAME)
                .withFlags(Intent.FLAG_NOT_OHOS_COMPONENT)
                .build();
        intent.setOperation(operation);
        isConnected = ability.connectAbility(intent, connection);
    }
}
