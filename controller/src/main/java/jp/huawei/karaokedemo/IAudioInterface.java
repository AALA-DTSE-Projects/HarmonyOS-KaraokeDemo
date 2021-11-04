package jp.huawei.karaokedemo;

import ohos.rpc.IRemoteBroker;

public interface IAudioInterface extends IRemoteBroker {
    void connect(String deviceId);
    void startPlay();
    void playAudio(byte[] buffer);
    void stopPlay();
    void finish();
}
