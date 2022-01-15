import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class SpawnareaElfilor extends Thread {

    private FabricaJucarii fabrici;

    public SpawnareaElfilor(FabricaJucarii fabrici) {
        this.fabrici = fabrici;
    }

    public void run() {

        while (true) {
            Random rand = new Random();
            long milis = rand.nextInt(1000) + 500;
            try {
                //dorm un timp aleatoriu intre 500 si 1000 milisecunde
                Thread.sleep(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //spawnam un elf
            spawneazaUnElf();
        }
    }

    private void spawneazaUnElf() {

        Random rand = new Random();

        //luam zavorul fabricii
        ReentrantLock fabricaLock = fabrici.getFabricaLock();

        //elfii nu se pot misca in timp ce se adauga un nou elf in fabrica
        fabricaLock.lock();

        //dimensiunea fabricii
        int fabricaSize = fabrici.getN();

        if (fabrici.nrElfiExistenti() != fabricaSize / 2) {

            //facem o pozitie aleatoare pentru elf
            int X = rand.nextInt(fabricaSize) + 0;
            int Y = rand.nextInt(fabricaSize) + 0;

            //counter lock
            ReentrantLock elfiCounterLock = Atelier.getElfiCounterLock();

            //niciun thread nu mai poate accesa numarul de elfi din fabrica, pt ca a fost modificat
            elfiCounterLock.lock();

            //cream un elf nou
            Elf elf = new Elf(Atelier.nrTotalElfi, X, Y, fabrici);

            if (fabrici.adaugaElf(elf)) {

                //inseram elful in fabrica
                Atelier.nrTotalElfi++;
                System.out.println("Elful " + elf.getNumar() + " a fost creat in fabrica " + fabrici.getNumar());
            }
            //deblocam counter lock pt elfi
            elfiCounterLock.unlock();
        }
        //deblocam fabrica
        fabricaLock.unlock();
    }
}
