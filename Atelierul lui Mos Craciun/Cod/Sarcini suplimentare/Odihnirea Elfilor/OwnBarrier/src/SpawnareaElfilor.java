import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class SpawnareaElfilor extends Thread {

    private FabricaJucarii fabrici;
    private CyclicBarrier elfBarrier;

    public SpawnareaElfilor(FabricaJucarii fabrici) {
        this.fabrici = fabrici;
        elfBarrier = new CyclicBarrier(fabrici.getN());
    }

    public void run() {

        while (true) {
            Random rand = new Random();
            long milis = rand.nextInt(1000) + 500;
            try {
                Thread.sleep(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spawneazaUnElf();
        }
    }

    private void spawneazaUnElf() {

        Random rand = new Random();
        ReentrantLock fabricaLock = fabrici.getFabricaLock();
        fabricaLock.lock();

        int fabricaSize = fabrici.getN();

        if (fabrici.nrElfiExistenti() != fabricaSize) {

            // modificam fabrica astfel incat sa contina maxim N elfi
            // sau eliminam elfii din lista elfilor cand acestia dorm
            // si ii reinseram dupa ce bariera este PASSED

            int X = rand.nextInt(fabricaSize) + 0;
            int Y = rand.nextInt(fabricaSize) + 0;

            ReentrantLock elfiCounterLock = Atelier.getElfiCounterLock();
            elfiCounterLock.lock();

            Elf elf = new Elf(Atelier.nrTotalElfi, X, Y, fabrici, elfBarrier);

            if (fabrici.adaugaElf(elf)) {

                Atelier.nrTotalElfi++;
                System.out.println("Elful " + elf.getNumar() + " a fost creat in fabrica " + fabrici.getNumar());
            }
            elfiCounterLock.unlock();
        }
        fabricaLock.unlock();
    }
}
