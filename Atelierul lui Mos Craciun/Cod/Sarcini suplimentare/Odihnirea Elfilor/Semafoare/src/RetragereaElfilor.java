
public class RetragereaElfilor extends Thread {
    public void run() {

        while (true) {
            Atelier.retragereElfiSemafoare.release();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
