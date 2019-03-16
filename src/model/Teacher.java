package model;

import java.util.concurrent.Semaphore;

public abstract class Teacher implements Runnable
{
    protected int numOfStudents = 0;
    protected Semaphore semaphore;
    protected String name;


    public Teacher(String name, int nStudents)
    {
        numOfStudents = nStudents;
        this.name = name;
        this.semaphore = new Semaphore(this.numOfStudents);
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public String getName() {
        return name;
    }

    /*
     *  Returns random grade from 0 to 10
     */
    public int giveStudentGrade()
    {
        double f = Math.random() / Math.nextDown(1.0);
        return (int)(0 * (1.0 - f) + 10 * f);
    }

}
