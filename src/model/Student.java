package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Student implements Runnable
{
    // How many miliseconds should this thread wait for more threads to join on professor's defence
    private static final long WAIT_FOR_MORE_STUDENTS = 1000;
    private static final double TEMPO_MIN = 500;
    private static final double TEMPO_MAX = 1000;

    private Professor professor;
    private Assistant assistant;
    private long tempo;
    private long arrivalDelay;
    private int grade;
    private boolean started;
    private int index;
    private Date startedDefenceAt;

    public Student(int index, long arrivalDelay, Professor professor, Assistant assistant)
    {
        this.assistant = assistant;
        this.professor = professor;
        this.tempo = this.intiTempo();
        this.grade = -1;
        this.index = index;
        this.arrivalDelay = arrivalDelay;
    }

    @Override
    public void run()
    {
        Thread.currentThread().setName("Student <"+ this.index +">");
        System.out.println(this.toString());

        while (Main.isActive())
        {
            if(this.professor.getSemaphore().tryAcquire() && Main.isActive()) {
                System.out.println(this.toString() + " has acquired professors semaphore");
                professorDefense();
                this.professor.getSemaphore().release();
                System.out.println(this.toString() + "has released professors semaphore");
                if(this.hasGrade()){
                    System.out.println(this.getConsoleRepresentation(this.professor));
                    break;
                }
            }
            if(this.assistant.getSemaphore().tryAcquire() && Main.isActive()) {
                System.out.println(this.toString() + " has acquired assistant's semaphore");
                assistantDefense();
                this.assistant.getSemaphore().release();
                System.out.println(this.toString() + " has released assistant's semaphore");
                break;
            }
        }
    }

    /*
     *  Returns value in the closed range [TEMPO_MIN, TEMPO_MAX].
     */
    private long intiTempo()
    {
        double f = Math.random() / Math.nextDown(1.0);
        return (long)(TEMPO_MIN * (1.0 - f) + TEMPO_MAX * f);
    }

    private void assistantDefense()
    {
        try {
            this.doDefense();
        } catch (InterruptedException e) {

        } finally {
            this.gradeStudent(this.assistant);
            System.out.println(this.getConsoleRepresentation(this.assistant));
        }
    }

    private void professorDefense()
    {
        try {
            this.professor.getStartBarrier().await(WAIT_FOR_MORE_STUDENTS, TimeUnit.MILLISECONDS);
            this.started = true;
            System.out.println(this.toString() + "has started project defence");

            this.doDefense();
            this.gradeStudent(this.professor);

            this.professor.getEndBarrier().await();
            System.out.println(this.toString() + "has ended project defence");

        } catch (InterruptedException e ) {
            if(this.started && !hasGrade()) this.gradeStudent(this.professor);
        } catch (TimeoutException e) {

        } catch (BrokenBarrierException e) {

        }
    }

    private void doDefense() throws InterruptedException
    {
        this.startedDefenceAt = new Date();
        Thread.sleep(this.tempo);
    }

    private void gradeStudent(Teacher teacher)
    {
        this.grade = teacher.giveStudentGrade();
        Main.increaseGradeSum(this.grade);
    }

    private boolean hasGrade()
    {
        return this.grade > -1;
    }

    public long getArrivalDelay() {
        return this.arrivalDelay;
    }

    public String getConsoleRepresentation(Teacher teacher)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\tThread: ");
        sb.append(Thread.currentThread().getName());
        sb.append("\n\tArrival: ");
        sb.append(this.arrivalDelay);
        sb.append("\n\tProf name: ");
        sb.append(teacher.getName());
        sb.append("\n\tTTC: ");
        sb.append(this.tempo);
        sb.append(" : ");
        sb.append(new SimpleDateFormat("HH:mm:ss.SSS").format(this.startedDefenceAt));
        sb.append("\n\tScore: ");
        sb.append(this.grade);
        sb.append("\n}");

        return sb.toString();
    }

    @Override
    public String toString()
    {
        return Thread.currentThread().getName();
    }
}
