package mihajlovic.todor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main
{
    // Length of defence in miliseconds
    private static volatile boolean ACTIVE = false;
    private static final AtomicInteger GRADE_SUM = new AtomicInteger(0);
    private static final AtomicInteger N_OF_DONE_DEFENCES = new AtomicInteger(0);
    private static final long DEFENCE_LENGTH = 1000;
    private static int numOfStudents = 50;


    public static void main(String[] args)
    {
        ScheduledExecutorService studentsThreadPool = Executors.newScheduledThreadPool(numOfStudents);
        ExecutorService teachersThreadPool = Executors.newFixedThreadPool(2);
        Professor professor = new Professor("Professor", 2);
        Assistant assistant = new Assistant("Assistant", 1);

        // start teachers
        teachersThreadPool.execute(professor);
        teachersThreadPool.execute(assistant);
        Main.ACTIVE = true;

        // start students
        long arrivalDelay;
        Student student;
        for (int i = 0; i < numOfStudents; i++)
        {
            arrivalDelay = (long)(Math.random() * 1000);
            student = new Student(i, arrivalDelay, professor, assistant);
            studentsThreadPool.schedule(student, student.getArrivalDelay(), TimeUnit.MILLISECONDS);
        }

        // wait for defence to finish
        try {
            Thread.sleep(Main.DEFENCE_LENGTH);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //shut down
        Main.ACTIVE = false;
        studentsThreadPool.shutdownNow();
        teachersThreadPool.shutdownNow();



        try {
            Thread.sleep((long)(500));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double average = (Main.GRADE_SUM.get() * 1.0) / Main.N_OF_DONE_DEFENCES.get();
        System.out.println("Kraj : " + average);
        System.out.println("Grade sum : " + Main.GRADE_SUM.get());
        System.out.println("Number of students : " + Main.N_OF_DONE_DEFENCES.get());
    }

    public static void increaseGradeSum(int grade)
    {
        Main.GRADE_SUM.addAndGet(grade);
        Main.N_OF_DONE_DEFENCES.incrementAndGet();
    }

    public static boolean isActive()
    {
        return Main.ACTIVE;
    }
}
