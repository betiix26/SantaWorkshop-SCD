
public class Plan {

    public static void main(String[] args) {

        //cream coada tranferului de cadouri
        TransferulCadourilor cadouQueue = new TransferulCadourilor();

        //il cream pe Mos Craciun
        MosCraciun Santa = new MosCraciun(cadouQueue);

        //cream atelierul lui Mos Craciun
        Atelier atelier  = new Atelier(cadouQueue);

        //incepem crearea fabricilor
        atelier.creeazaFabrici();

        //Mos Craciun incepe sa primeasca cadourile
        Santa.start();

    }
}
