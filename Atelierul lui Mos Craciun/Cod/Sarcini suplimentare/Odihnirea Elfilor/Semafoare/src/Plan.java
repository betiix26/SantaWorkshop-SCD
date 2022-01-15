
public class Plan {

    public static void main(String[] args) {

        TransferulCadourilor cadouQueue = new TransferulCadourilor();

        MosCraciun Santa = new MosCraciun(cadouQueue);
        Atelier atelier  = new Atelier(cadouQueue);

        atelier.creeazaFabrici();
        Santa.start();

    }
}
