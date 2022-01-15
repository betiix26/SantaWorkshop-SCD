import java.util.concurrent.locks.ReentrantLock;

public class CyclicBarrier {

    private ReentrantLock counterLock = new ReentrantLock();
    private int counter = 0;
    private int parties;
    public CyclicBarrier(int parties) {

        this.parties = parties;
    }

    public void await() {

        try {
            // modifica numarul de elfi in asteptare

            counterLock.lock();
            counter++;

        }	finally {
            counterLock.unlock();
        }

        while(counter < parties) {
            // asteapta pentru ceilalti elfi
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
