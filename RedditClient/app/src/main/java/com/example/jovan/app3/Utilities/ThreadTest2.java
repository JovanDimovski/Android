package com.example.jovan.app3.Utilities;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jovan on 17-Oct-16.
 */
public class ThreadTest2 implements Runnable {
    ArrayList<Integer> S;
    public ThreadTest2(ArrayList<Integer> S)
    {
        this.S = S;
    }
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            S.set(0, S.get(0) + 1);
            S.set(1, S.get(1) + 1);
            S.set(2, S.get(2) + 1);
        }
    }
}
