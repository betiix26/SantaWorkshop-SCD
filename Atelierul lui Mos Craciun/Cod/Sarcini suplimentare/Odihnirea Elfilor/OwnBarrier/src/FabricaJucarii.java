import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class FabricaJucarii extends Thread {

    private int numar;
    private int N;
    private boolean fabrica[][];
    private ArrayList<Elf> elfi = new ArrayList<Elf>();
    private ArrayList<Integer> cadouri = new ArrayList<Integer>();
    private ReentrantLock fabricaLock = new ReentrantLock();
    private ReentrantLock elfiListLock = new ReentrantLock();
    private Semaphore reniSemafoare = new Semaphore(10);
    private ReentrantLock cadouriLock = new ReentrantLock();


    public ReentrantLock getFabricaLock() {
        return fabricaLock;
    }

    public FabricaJucarii(int N, int numar) {
        this.fabrica = new boolean[N][N];
        this.numar = numar;
        this.N = N;
    }

    public int nrElfiExistenti() {
        return elfi.size();
    }

    public int getN() {
        return N;
    }

    public int getNumar() {
        return numar;
    }

    public void run() {
        while (true) {
            try {
                intreabaElfiiPtPozitie();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void mutaElf(Elf elf) {

        int X = elf.getX();
        int Y = elf.getY();
        int cadou = elf.getCadou();
        int elfNr = elf.getNumar();

        try {
            fabricaLock.lock();
            if (mutaDreapta(X, Y)) {

                fabrica[X][Y] = false;
                fabrica[X][Y + 1] = true;

                creeazaCadou(cadou, elfNr);
                elf.schimbaPozitia(X, Y + 1);
                intreabaElfiiPtPozitie();

            } else if (mutaSus(X, Y)) {

                fabrica[X][Y] = false;
                fabrica[X - 1][Y] = true;

                creeazaCadou(cadou, elfNr);
                elf.schimbaPozitia(X - 1, Y);
                intreabaElfiiPtPozitie();

            } else if (mutaJos(X, Y)) {

                fabrica[X][Y] = false;
                fabrica[X + 1][Y] = true;

                creeazaCadou(cadou, elfNr);
                elf.schimbaPozitia(X + 1, Y);
                intreabaElfiiPtPozitie();

            } else if (mutaStanga(X, Y)) {

                fabrica[X][Y] = false;
                fabrica[X][Y - 1] = true;

                creeazaCadou(cadou, elfNr);
                elf.schimbaPozitia(X, Y - 1);
                intreabaElfiiPtPozitie();

            } else {
                elf.stopWork();
            }

        } finally {

            fabricaLock.unlock();

        }
    }

    private boolean mutaStanga(int X, int Y) {

        if (Y - 1 > 0) {
            if (!fabrica[X][Y - 1]) {
                return true;
            }
        }

        return false;
    }

    private boolean mutaSus(int X, int Y) {

        if (X - 1 > 0) {
            if (!fabrica[X - 1][Y]) {
                return true;
            }
        }
        return false;
    }

    private boolean mutaJos(int X, int Y) {

        if (X + 1 < N) {
            if (!fabrica[X + 1][Y]) {
                return true;
            }
        }
        return false;
    }

    private boolean mutaDreapta(int X, int Y) {

        if (Y + 1 < N) {
            if (!fabrica[X][Y + 1]) {
                return true;
            }
        }
        return false;
    }

    public boolean adaugaElf(Elf elf) {

        elfiListLock.lock();

        int X = elf.getX();
        int Y = elf.getY();

        //raportarea nu poate fi facuta in timp ce un elf este spawnat

        if (fabrica[X][Y]) {

            elfiListLock.unlock();
            return false;

        } else {

            fabrica[X][Y] = true;

            elfi.add(elf);

            elf.start();

            elf.raporteazaPozitia();

            elfiListLock.unlock();

            return true;
        }
    }

    public void intreabaElfiiPtPozitie() {

        try {

            fabricaLock.lock();
            elfiListLock.lock();
            cadouriLock.lock();

            // nu pot fi adaugati elfi noi cand se intreaba de elfii curenti

            // elfii nu se pot muta cand se raporteaza pozitia fiecaruia

            for (Elf elf : elfi) {
                elf.raporteazaPozitia();
            }

        } finally {

            elfiListLock.unlock();
            fabricaLock.unlock();
            cadouriLock.unlock();

        }
    }

    public int getCadou() {

        int cadou = 0;

        try {

            try {
                reniSemafoare.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cadouriLock.lock();

            try {
                cadou = cadouri.get(cadouri.size() - 1);
                cadouri.remove(cadouri.size() - 1);
            } catch (Exception exception) {                // nu sunt cadouri
                cadou = 0;
            }

        } finally {
            cadouriLock.unlock();
            reniSemafoare.release();
        }

        return cadou;
    }

    public void retragereElf(Elf elf) {

        try {
            elfiListLock.lock();
            fabricaLock.lock();
            elfi.remove(elf);
            int X = elf.getX();
            int Y = elf.getY();
            fabrica[X][Y] = false;
            System.out.println("Elful " + elf.getNumar() + " s-a retras de la fabrica " + numar);

        } finally {
            elfiListLock.unlock();
            fabricaLock.unlock();
        }
    }

    private void creeazaCadou(int cadou, int elfNr) {

        try {

            cadouriLock.lock();
            cadouri.add(cadou);
            System.out.println("Elful " + elfNr + " a creat cadoul " + cadou);

        } finally {
            cadouriLock.unlock();
        }
    }
}



