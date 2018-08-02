import java.util.TimerTask;

public class MyTimerTask extends TimerTask {

    private Coordinator coordinator;

    MyTimerTask(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void run() {
        coordinator.repeat();
    }
}
