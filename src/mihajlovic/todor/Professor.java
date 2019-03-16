package mihajlovic.todor;

import java.util.concurrent.CyclicBarrier;

public class Professor extends Teacher implements Runnable
{

    private CyclicBarrier startBarrier;
    private CyclicBarrier endBarrier;

    public Professor(String name, int n) {
        super(name, n);
        this.startBarrier = new CyclicBarrier(numOfStudents, () -> {
            System.out.println("Professor: " + numOfStudents + " student(s) started defence");
        });
        this.endBarrier = new CyclicBarrier(numOfStudents, () -> {
            System.out.println("Professor: " + numOfStudents + " student(s) ended defence");
        });
    }

    @Override
    public void run()
    {
        Thread.currentThread().setName("Professor");

        while (Main.isActive())
        {

        }
    }

    public CyclicBarrier getStartBarrier() {
        return startBarrier;
    }

    public CyclicBarrier getEndBarrier() {
        return endBarrier;
    }
}
