package com.androidsx.rainnotifications;

import com.google.android.gms.wearable.NodeApi;

public interface WearManagerResultListener {
    public void onWearManagerSuccess(NodeApi.GetConnectedNodesResult getConnectedNodesResult, WearManager wearManager);
    public void onWearManagerFailure(WearManagerException exception);
}
