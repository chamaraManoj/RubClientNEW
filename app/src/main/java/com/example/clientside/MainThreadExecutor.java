package com.example.clientside;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;


/*This class contains a handler of the main thread to execute the threads in the main thread*/
public class MainThreadExecutor implements Executor {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
