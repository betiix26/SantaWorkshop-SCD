import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;

public class Atelier {

    public static int nrFabrici;
    private FabricaJucarii fabrici[];
    private SpawnareaElfilor spawners[];
    public static volatile int nrTotalElfi = 1;
    private static ReentrantLock elfiCounterLock = new ReentrantLock();
    private Reni reni[];
    private TransferulCadourilor cadouQueue;
    public static volatile Semaphore retragereElfiSemafoare = new Semaphore(0);
    private RetragereaElfilor retragereElf = new RetragereaElfilor();

    public Atelier(TransferulCadourilor cadouQueue) {
        this.cadouQueue = cadouQueue;
    }

    public static ReentrantLock getElfiCounterLock() {
        return elfiCounterLock;
    }

    public void creeazaFabrici() {

        Random rand = new Random();
        nrFabrici = rand.nextInt(4) + 2;
        int nrReni = rand.nextInt(10) + 8;

        fabrici = new FabricaJucarii[nrFabrici];
        spawners = new SpawnareaElfilor[nrFabrici];
        reni = new Reni[nrReni];

        System.out.println("Acolo au fost create " + Atelier.nrFabrici + " fabrici");
        System.out.println("Acolo au fost creati " + nrReni + " reni");

        for (int i = 0; i < Atelier.nrFabrici; ++i) {
            int N = rand.nextInt(500) + 100;
            fabrici[i] = new FabricaJucarii(N, i + 1);
            spawners[i] = new SpawnareaElfilor(fabrici[i]);
            System.out.println("Fabrica " + (i + 1) + " are N = " + N);
        }

        for (int i = 0; i < nrReni; ++i) {
            reni[i] = new Reni(fabrici, i + 1, cadouQueue);
        }

        for (int i = 0; i < Atelier.nrFabrici; ++i) {
            spawners[i].start();
            fabrici[i].start();
        }

        for (int i = 0; i < nrReni; ++i) {
            reni[i].start();
        }

        retragereElf.start();
    }
}
