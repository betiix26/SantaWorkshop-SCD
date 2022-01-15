
public class TransferulCadourilor {

    private volatile int head = 0;
    private volatile int tail = 0;

    private int[] cadouri = new int[10];

    public synchronized int primesteCadou() {

        int cadou = 0;

        //aici asteptam pana cand buffer-ul nu mai e gol
        while (tail == head) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //aici luam cadoul de la buffer
        cadou = cadouri[head % cadouri.length];
        head++;

        //notificam faptul ca buffer-ul nu e full
        notifyAll();
        return cadou;
    }

    public synchronized void daCadou(int cadou) {

        //asteptam pana cand buffer-ul nu mai e plin
        while (tail - head == cadouri.length) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //adaugam cadoul in buffer
        cadouri[tail % cadouri.length] = cadou;
        tail++;

        //notificam faptul ca buffer-ul nu e gol
        notifyAll();
    }
}
