package com.huawei.karaokedemo.slice;

import com.huawei.karaokedemo.ResourceTable;
import com.huawei.karaokedemo.controller.Const;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.agp.components.ListContainer;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.distributedschedule.interwork.IDeviceStateCallback;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import provider.DeviceItemProvider;

import java.util.List;

public class DeviceListAbilitySlice extends AbilitySlice {

    private ListContainer deviceList;
    private DeviceItemProvider provider;
    private static final int EVENT_STATE_CHANGE = 10001;

    private final EventHandler handler = new EventHandler(EventRunner.current()) {
        @Override
        protected void processEvent(InnerEvent event) {
            if (event.eventId == EVENT_STATE_CHANGE) {
                updateDeviceList();
            }
        }
    };

    private final IDeviceStateCallback callback = new IDeviceStateCallback() {
        @Override
        public void onDeviceOffline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }

        @Override
        public void onDeviceOnline(String deviceId, int deviceType) {
            handler.sendEvent(EVENT_STATE_CHANGE);
        }
    };

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        setupUI();
        initData();
    }

    @Override
    protected void onActive() {
        super.onActive();
        if (provider.getCount() == 0) {
            updateDeviceList();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DeviceManager.unregisterDeviceStateCallback(callback);
    }

    private void updateDeviceList() {
        List<DeviceInfo> deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        provider.updateItems(deviceInfoList);
        deviceList.setItemProvider(provider);
    }

    private void setupUI() {
        setUIContent(ResourceTable.Layout_ability_device_list);
        deviceList = (ListContainer) findComponentById(ResourceTable.Id_device_list);
        provider = new DeviceItemProvider(this, this::setResult);
    }

    private void initData() {
        DeviceManager.registerDeviceStateCallback(callback);
    }

    private void setResult(DeviceInfo deviceInfo) {
        Intent intent = new Intent();
        IntentParams params = new IntentParams();
        params.setParam(Const.DEVICE_ID_KEY, deviceInfo.getDeviceId());
        params.setParam(Const.DEVICE_TYPE_KEY, deviceInfo.getDeviceType());
        intent.setParams(params);
        setResult(intent);
        onBackPressed();
    }
}
