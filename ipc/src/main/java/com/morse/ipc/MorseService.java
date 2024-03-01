package com.morse.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

public class MorseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MorseBinderInterface.Stub() {
            @Override
            public String reuqest(String request) throws RemoteException {
                return null;
            }
        };
    }
}
