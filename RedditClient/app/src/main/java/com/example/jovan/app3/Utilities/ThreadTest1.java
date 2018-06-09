package com.example.jovan.app3.Utilities;

import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jovan on 17-Oct-16.
 */
public class ThreadTest1 implements Runnable {
    ArrayList<Integer> S;
    public ThreadTest1(ArrayList<Integer> S)
    {
        this.S = S;
    }
    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("ThreadTest1",S.get(0)+","+S.get(1)+","+S.get(2));
        }
    }
}
