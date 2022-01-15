
public class TransferulCadourilor {

    private volatile int head = 0;
    private volatile int tail = 0;

    private int[] cadouri = new int[10];

    public synchronized int primesteCadou() {

        int cadou = 0;

        while (tail == head) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cadou = cadouri[head % cadouri.length];
        head++;

        notifyAll();
        return cadou;
    }

    public synchronized void daCadou(int cadou) {

        while (tail - head == cadouri.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cadouri[tail % cadouri.length] = cadou;
        tail++;

        notifyAll();
    }
}
