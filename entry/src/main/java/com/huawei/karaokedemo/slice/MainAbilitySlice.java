package com.huawei.karaokedemo.slice;

import com.huawei.karaokedemo.MainAbility;
import com.huawei.karaokedemo.ResourceTable;
import com.huawei.karaokedemo.controller.Const;
import com.huawei.karaokedemo.controller.HandleRemoteProxy;
import com.huawei.karaokedemo.controller.LogUtil;
import com.huawei.karaokedemo.controller.ThreadPoolManager;
import com.huawei.karaokedemo.model.DisconnectEvent;
import com.huawei.karaokedemo.model.GrantPermissionEvent;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Operation;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.IDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.AbilityInfo;
import ohos.bundle.ElementName;
import ohos.bundle.IBundleManager;
import ohos.data.distributed.common.KvManagerConfig;
import ohos.data.distributed.common.KvManagerFactory;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.distributedschedule.interwork.IInitCallback;
import ohos.media.audio.AudioCapturer;
import ohos.media.audio.AudioCapturerInfo;
import ohos.media.audio.AudioStreamInfo;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;
import ohos.security.SystemPermission;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private final String TAG = MainAbilitySlice.class.getName();
    private boolean isRecording = false;
    private Image recordButton;
    private final int REQUEST_LIST_DEVICE = 0;
    private String deviceId;
    private DeviceInfo.DeviceType deviceType;
    private AudioCapturer audioCapturer;
    private HandleRemoteProxy remoteProxy;

    private final Runnable audioCapture = () -> {
        byte[] buffer = new byte[Const.BUFFER_SIZE];
        audioCapturer.start();
        while (isRecording) {
            int count = audioCapturer.read(buffer, 0, Const.BUFFER_SIZE);
            if (count > 0) {
                if (remoteProxy != null) {
                    remoteProxy.playAudio(buffer);
                }
            } else {
                LogUtil.debug(TAG, "Error capture audio " + count);
            }
        }
    };

    private final IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject remote, int resultCode) {
            remoteProxy = new HandleRemoteProxy(remote);
            LogUtil.info(TAG, "ability connect done!");
            String localDeviceId = KvManagerFactory.getInstance()
                    .createKvManager(new KvManagerConfig(MainAbilitySlice.this))
                    .getLocalDeviceInfo()
                    .getId();
            remoteProxy.start(localDeviceId);
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            LogUtil.info(TAG, "ability disconnect done!");
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setupUI();
        initData();
    }

    @Override
    protected void onResult(int requestCode, Intent resultIntent) {
        if (requestCode == REQUEST_LIST_DEVICE) {
            IntentParams params = resultIntent.getParams();
            Object deviceIdObj = params.getParam(Const.DEVICE_ID_KEY);
            if (deviceIdObj instanceof String) {
                deviceId = (String) deviceIdObj;
            }
            Object deviceTypeObj = params.getParam(Const.DEVICE_TYPE_KEY);
            if (deviceTypeObj instanceof DeviceInfo.DeviceType) {
                deviceType = (DeviceInfo.DeviceType) deviceTypeObj;
            }
            connectToRemoteService();
        } else {
            super.onResult(requestCode, resultIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        onBackPressed();
    }

    @Override
    protected void onBackPressed() {
        super.onBackPressed();
        if (remoteProxy != null) {
            remoteProxy.finish();
        }
        EventBus.getDefault().unregister(this);
        disconnectAbility(connection);
        if (isRecording) {
            ThreadPoolManager.getInstance().cancel(audioCapture);
            audioCapturer.stop();
        }
        audioCapturer.release();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGrantPermissionEvent(GrantPermissionEvent event) {
        switch (event.getPermission()) {
            case SystemPermission.MICROPHONE:
                initAudioCapture();
                if (remoteProxy != null) {
                    remoteProxy.startPlay();
                }
                ThreadPoolManager.getInstance().execute(audioCapture);
                break;
            case SystemPermission.DISTRIBUTED_DATASYNC:
                gotoDeviceListScreen();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDisconnectEvent(DisconnectEvent event) {
        CommonDialog dialog = new CommonDialog(this);
        dialog.setTitleText("Notification");
        dialog.setContentText("Remote device has been disconnected");
        dialog.setButton(IDialog.BUTTON3, "OK", (iDialog, code) -> iDialog.destroy());
        dialog.setSize(ComponentContainer.LayoutConfig.MATCH_CONTENT, ComponentContainer.LayoutConfig.MATCH_CONTENT);
        dialog.show();
        disconnectAbility(connection);
    }

    private void setupUI() {
        setUIContent(ResourceTable.Layout_ability_main);
        recordButton = (Image) findComponentById(ResourceTable.Id_record_button);
        recordButton.setClickedListener(component -> {
            if (deviceId == null) {
                showToast("Please connect with target device first");
                return;
            }
            if (requestPermissions(SystemPermission.MICROPHONE)) {
                isRecording = !isRecording;
                if (isRecording) {
                    recordButton.setPixelMap(ResourceTable.Media_stop_button);
                    initAudioCapture();
                    if (remoteProxy != null) {
                        remoteProxy.startPlay();
                    }
                    ThreadPoolManager.getInstance().execute(audioCapture);
                } else {
                    recordButton.setPixelMap(ResourceTable.Media_microphone);
                    ThreadPoolManager.getInstance().cancel(audioCapture);
                    audioCapturer.stop();
                    if (remoteProxy != null) {
                        remoteProxy.stop();
                    }
                }
            }
        });
        Image castButton = (Image) findComponentById(ResourceTable.Id_cast_button);
        castButton.setClickedListener(component -> {
            if (requestPermissions(SystemPermission.DISTRIBUTED_DATASYNC)) {
                gotoDeviceListScreen();
            }
        });
    }

    private void initData() {
        EventBus.getDefault().register(this);
    }

    private boolean requestPermissions(String... permissions) {
        boolean isGranted = true;
        for (String permission : permissions) {
            if (verifyCallingOrSelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
                isGranted = false;
                requestPermissionsFromUser(
                        new String[] {
                                permission
                        },
                        MainAbility.REQUEST_CODE);
            }
        }
        return isGranted;
    }

    private void initAudioCapture() {
        if (audioCapturer != null) {
            return;
        }
        AudioStreamInfo audioStreamInfo =
                new AudioStreamInfo.Builder()
                        .encodingFormat(AudioStreamInfo.EncodingFormat.ENCODING_PCM_16BIT)
                        .channelMask(AudioStreamInfo.ChannelMask.CHANNEL_IN_STEREO)
                        .sampleRate(Const.AUDIO_SAMPLE_RATE)
                        .streamUsage(AudioStreamInfo.StreamUsage.STREAM_USAGE_MEDIA)
                        .build();
        AudioCapturerInfo audioCapturerInfo =
                new AudioCapturerInfo.Builder()
                        .audioStreamInfo(audioStreamInfo)
                        .build();
        audioCapturer = new AudioCapturer(audioCapturerInfo);
    }

    private void gotoDeviceListScreen() {
        presentForResult(
                new DeviceListAbilitySlice(),
                new Intent(),
                REQUEST_LIST_DEVICE
        );
    }

    private void connectToRemoteService() {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(Const.BUNDLE_NAME)
                .withAbilityName(Const.ABILITY_NAME)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        try {
            List<AbilityInfo> abilityInfoList = getBundleManager().queryAbilityByIntent(
                    intent,
                    IBundleManager.GET_BUNDLE_DEFAULT,
                    0);
            if (abilityInfoList != null && !abilityInfoList.isEmpty()) {
                if (deviceType == DeviceInfo.DeviceType.SMART_TV) {
                    connectAbility(intent, connection);
                } else {
                    DeviceManager.initDistributedEnvironment(deviceId, new IInitCallback() {
                        @Override
                        public void onInitSuccess(String s) {
                            connectAbility(intent, connection);
                            LogUtil.info(TAG, "connect service on " + deviceType + " with id " + deviceId);
                        }

                        @Override
                        public void onInitFailure(String s, int i) {
                            showToast("Cannot connect service on " + deviceType + " with error " + s + " and error code is " + i);
                        }
                    });
                }
            } else {
                showToast("Cannot connect service on " + deviceType.name());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showToast(String text) {
        LogUtil.info(TAG, text);
        new ToastDialog(this)
                .setText(text)
                .setAutoClosable(false)
                .show();
    }
}
