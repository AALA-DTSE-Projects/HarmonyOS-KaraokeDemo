package com.huawei.karaokedemo.controller;

import com.huawei.karaokedemo.ControllerServiceAbility;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.rpc.IRemoteObject;

import java.util.HashMap;

public class ResultRemote extends ResultStub {
    private final Ability ability;
    private final HashMap<String, IAbilityConnection> connectionMap;
    private final HashMap<String, ControllerRemoteProxy> controllerRemoteProxyMap;

    public ResultRemote(Ability ability) {
        super("Result remote");
        this.ability = ability;
        connectionMap = new HashMap<>();
        controllerRemoteProxyMap = new HashMap<>();
    }

    @Override
    public void connect(String deviceId) {
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(deviceId)
                .withBundleName(Const.BUNDLE_NAME)
                .withAbilityName(Const.ABILITY_NAME)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        IAbilityConnection connection = new IAbilityConnection() {
            @Override
            public void onAbilityConnectDone(ElementName elementName, IRemoteObject remoteObject, int resultCode) {
                connectionMap.put(deviceId, this);
                ControllerRemoteProxy controllerRemoteProxy = new ControllerRemoteProxy(remoteObject);
                controllerRemoteProxyMap.put(deviceId, controllerRemoteProxy);
            }

            @Override
            public void onAbilityDisconnectDone(ElementName elementName, int resultCode) {
                connectionMap.remove(deviceId);
                controllerRemoteProxyMap.remove(deviceId);
            }
        };
        ability.connectAbility(intent, connection);
    }

    @Override
    public void disconnect(String deviceId) {
        ControllerRemoteProxy controllerRemoteProxy = controllerRemoteProxyMap.get(deviceId);
        if (controllerRemoteProxy != null) {
            controllerRemoteProxy.disconnect();
        }
        IAbilityConnection connection = connectionMap.getOrDefault(deviceId, null);
        if (connection != null) {
            ability.disconnectAbility(connection);
        }
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withBundleName(ability.getBundleName())
                .withAbilityName(ControllerServiceAbility.class.getName())
                .build();
        intent.setOperation(operation);
        ability.stopAbility(intent);
    }

}
