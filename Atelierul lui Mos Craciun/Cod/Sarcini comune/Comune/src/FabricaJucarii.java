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
                //le cerem elfilor pozitia in care se afla
                cerePozitiaElfilor();
                //si dorm pt 3000 milisecunde
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void mutaElf(Elf elf) {

        //aflam detaliile pozitiilor elfilor
        int X = elf.getX();
        int Y = elf.getY();
        int cadou = elf.getCadou();
        int elfNr = elf.getNumar();

        try {
            //doar un elf se poate muta la un moment dat in fabrica (nu mai multi deodata, pe rand deci)
            fabricaLock.lock();
            if (mutaDreapta(X, Y)) {

                //schimba pozitia in matrice
                fabrica[X][Y] = false;
                fabrica[X][Y + 1] = true;

                creeazaCadou(cadou, elfNr);
                //schimba pozitia curenta a elfului
                elf.schimbaPozitia(X, Y + 1);
                //cere tuturor elfilor pozitia lor
                cerePozitiaElfilor();

            } else if (mutaSus(X, Y)) {

                //schimba pozitia in matrice
                fabrica[X][Y] = false;
                fabrica[X - 1][Y] = true;

                creeazaCadou(cadou, elfNr);
                //schimba pozitia curenta a elfului
                elf.schimbaPozitia(X - 1, Y);
                //cere tuturor elfilor pozitia lor
                cerePozitiaElfilor();

            } else if (mutaJos(X, Y)) {

                //schimba pozitia in matrice
                fabrica[X][Y] = false;
                fabrica[X + 1][Y] = true;

                creeazaCadou(cadou, elfNr);
                //schimba pozitia curenta a elfului
                elf.schimbaPozitia(X + 1, Y);
                //cere tuturor elfilor pozitia lor
                cerePozitiaElfilor();

            } else if (mutaStanga(X, Y)) {

                //schimba pozitia in matrice
                fabrica[X][Y] = false;
                fabrica[X][Y - 1] = true;

                creeazaCadou(cadou, elfNr);
                //schimba pozitia curenta a elfului
                elf.schimbaPozitia(X, Y - 1);
                //cere tuturor elfilor pozitia lor
                cerePozitiaElfilor();

            } else {
                //elfii nu se pot misca si nu mai functioneaza
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

        //alte thread-uri nu pot accesa lista elfilor in timp ce un elf se adauga pe lista

        //de asemenea nu se poate face raportarea elfilor in timp ce un elf este spawnat
        elfiListLock.lock();

        int X = elf.getX();
        int Y = elf.getY();

        //raportarea pozitiei nu poate fi facuta in timp ce un elf este spawnat

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

    private void cerePozitiaElfilor() {

        try {

            fabricaLock.lock();
            //nu pot fi adaugati elfi noi in timp ce se cere pozitia celorlalti

            elfiListLock.lock();
            //elfii nu se pot misca in timp ce isi raporteaza pozitia

            cadouriLock.lock();
            //renii nu pot primi cadourile in timp ce fabrica cere pozitia elfilor

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

            //facem rost de un permis pt reni
            try {
                reniSemafoare.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //2 reni nu pot sa citeasca lista de cadouri in acelasi timp
            cadouriLock.lock();

            try {
                cadou = cadouri.get(cadouri.size() - 1);
                cadouri.remove(cadouri.size() - 1);
            } catch (Exception exception) {                // lista cu cadouri e goala
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
            //modificam lista elfilor si matricea fabricii
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

            //renii nu pot lua cadourile in timp ce elfii creeaza cadouri
            cadouriLock.lock();
            cadouri.add(cadou);
            System.out.println("Elful " + elfNr + " a creat cadoul " + cadou);

        } finally {

            cadouriLock.unlock();

        }
    }
}



