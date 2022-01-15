
public class MosCraciun extends Thread {

    private TransferulCadourilor cadouQueue;

    public MosCraciun(TransferulCadourilor cadouQueue) {

        this.cadouQueue = cadouQueue;
    }

    public void run() {

        while (true) {
            int cadou = cadouQueue.primesteCadou();

            System.out.println("Mos Craciun a pus cadoul " + cadou + " in sac");
        }
    }
}
