package com.huawei.karaokedemo;

import com.huawei.karaokedemo.model.GrantPermissionEvent;
import com.huawei.karaokedemo.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import org.greenrobot.eventbus.EventBus;

public class MainAbility extends Ability {
    public static final int REQUEST_CODE = 1;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                EventBus.getDefault().post(new GrantPermissionEvent(
                        permissions[i],
                        grantResults[i] == IBundleManager.PERMISSION_GRANTED
                ));
            }
        }
    }
}
