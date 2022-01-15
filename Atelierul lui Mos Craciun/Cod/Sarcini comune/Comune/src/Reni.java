import java.util.Random;

public class Reni extends Thread {

    private int numar;
    private FabricaJucarii fabrici[];
    private TransferulCadourilor cadouQueue;

    public Reni(FabricaJucarii fabrici[], int numar, TransferulCadourilor cadouQueue) {
        this.fabrici = fabrici;
        this.numar = numar;
        this.cadouQueue = cadouQueue;
    }

    public void run() {
        while (true) {

            //luam cadoul din fabrica
            int cadou = getCadouDinFabrica();

            if (cadou != 0) {
                System.out.println("Renul " + numar + " a primit cadoul " + cadou);
                //ii dam cadoul lui Mos Craciun
                daCadouLuiSanta(cadou);
            }

            //renii dorm intre 10 si 30 de milisecunde
            Random rand = new Random();
            long milis = rand.nextInt(30) + 10;

            try {
                Thread.sleep(milis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void daCadouLuiSanta(int cadou) {
        cadouQueue.daCadou(cadou);
        System.out.println("Renul " + numar + " a dat cadoul " + cadou + " lui Mos Craciun");
    }

    private int getCadouDinFabrica() {

        //alegem o fabrica aleatorie din cele existente
        Random rand = new Random();
        int fabrica = rand.nextInt(Atelier.nrFabrici) + 0;

        //cerem cadoul din fabrica aleasa
        return fabrici[fabrica].getCadou();
    }
}
