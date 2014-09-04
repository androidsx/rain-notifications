package com.androidsx.rainnotifications;

import com.google.android.gms.wearable.NodeApi;

public interface WearNotificationManagerResultListener {
    public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult);
    public void onWearManagerFailure(WearNotificationManagerException exception);
}
