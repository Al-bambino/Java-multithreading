package model;

import java.util.concurrent.Semaphore;

public class Assistant extends Teacher implements Runnable
{

    public Assistant(String name, int n)
    {
        super(name, n);
    }
    @Override
    public void run() {
        Thread.currentThread().setName("Assistant");
        while (Main.isActive())
        {

        }


    }
}
