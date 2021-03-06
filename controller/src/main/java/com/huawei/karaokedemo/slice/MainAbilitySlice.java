package com.huawei.karaokedemo.slice;

import com.huawei.karaokedemo.MainAbility;
import com.huawei.karaokedemo.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;

public class MainAbilitySlice extends AbilitySlice {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        if (requestPermissions(SystemPermission.DISTRIBUTED_DATASYNC)) {
            terminateAbility();
        };
    }

    private boolean requestPermissions(String... permissions) {
        boolean isGranted = true;
        for (String permission : permissions) {
            if (verifyCallingOrSelfPermission(permission) != IBundleManager.PERMISSION_GRANTED) {
                requestPermissionsFromUser(
                        new String[] {
                                permission
                        },
                        MainAbility.REQUEST_CODE);
                isGranted = false;
            }
        }
        return isGranted;
    }
}
