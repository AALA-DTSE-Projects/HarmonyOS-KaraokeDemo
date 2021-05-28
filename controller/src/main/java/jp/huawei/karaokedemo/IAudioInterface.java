package jp.huawei.karaokedemo;

import ohos.rpc.IRemoteBroker;

public interface IAudioInterface extends IRemoteBroker {
    void setLyrics(String lyrics);
    void startPlay();
    void playAudio(byte[] buffer);
    void stopPlay();
    void finish();
}
