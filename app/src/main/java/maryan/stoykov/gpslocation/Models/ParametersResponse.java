package maryan.stoykov.gpslocation.Models;

public class ParametersResponse {
    private int updateInterval;
    private int minUpdateInterval;
    private int updateDistance;
    private boolean startOnBoot;

    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public String toString() {
        return "ParametersResponse{" +
                "updateInterval=" + updateInterval +
                ", minUpdateInterval=" + minUpdateInterval +
                ", updateDistance=" + updateDistance +
                ", startAtBoot=" + startOnBoot +
                '}';
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getMinUpdateInterval() {
        return minUpdateInterval;
    }

    public void setMinUpdateInterval(int minUpdateInterval) {
        this.minUpdateInterval = minUpdateInterval;
    }

    public int getUpdateDistance() {
        return updateDistance;
    }

    public void setUpdateDistance(int updateDistance) {
        this.updateDistance = updateDistance;
    }

    public boolean isStartAtBoot() {
        return startOnBoot;
    }

    public void setStartAtBoot(boolean startAtBoot) {
        this.startOnBoot = startAtBoot;
    }
}
