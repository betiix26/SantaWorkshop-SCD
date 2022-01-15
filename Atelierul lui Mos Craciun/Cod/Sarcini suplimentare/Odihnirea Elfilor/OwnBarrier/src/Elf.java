import java.util.Random;

public class Elf extends Thread {

    private int numar;
    private int X;
    private int Y;
    private int cadou = 0;
    private FabricaJucarii fabrica;
    private CyclicBarrier elfBarrier;

    public Elf(int numar, int X, int Y, FabricaJucarii fabrica, CyclicBarrier elfBarrier) {

        this.numar = numar;
        this.X = X;
        this.Y = Y;
        this.fabrica = fabrica;
        this.elfBarrier = elfBarrier;
    }


    public void run() {

        while (true) {

            if (Math.abs(X - Y) <= 1) {

                System.out.println("Elful " + numar + " doarme pe pozitia (" + X + "," + Y + ")");
                
elfBarrier.await();
            }

            cadou = cadou + numar;

            fabrica.mutaElf(this);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (Atelier.retragereElfiSemafoare.tryAcquire()) {
                fabrica.retragereElf(this);
                break;
            }
        }
    }


    public void schimbaPozitia(int newX, int newY) {

        X = newX;
        Y = newY;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public int getNumar() {
        return numar;
    }

    public int getCadou() {
        return cadou;
    }

    public void stopWork() {

        Random rand = new Random();
        long milis = rand.nextInt(50) + 10;
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void raporteazaPozitia() {

        System.out.println("Elful " + numar + " este la (" + X + "," + Y + ") in fabrica " + fabrica.getNumar());
    }
}
