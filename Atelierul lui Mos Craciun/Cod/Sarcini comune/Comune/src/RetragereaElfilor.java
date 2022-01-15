
public class RetragereaElfilor extends Thread {
    public void run() {

        while (true) {

            //obtinem un permis pentru ca elfii sa se poata retrage
            Atelier.retragereElfiSemafoare.release();

            try {
                //dorm 50 milisecunde
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
