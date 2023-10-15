package maryan.stoykov.gpslocation.Models;

public class WorkTime {
    private String whiteListFrom;
    private String whiteListTo;

    @Override
    public String toString() {
        return "WorkTime{" +
                "whiteListFrom='" + whiteListFrom + '\'' +
                ", whiteListTo='" + whiteListTo + '\'' +
                '}';
    }

    public String getWhiteListFrom() {
        return whiteListFrom;
    }

    public void setWhiteListFrom(String whiteListFrom) {
        this.whiteListFrom = whiteListFrom;
    }

    public String getWhiteListTo() {
        return whiteListTo;
    }

    public void setWhiteListTo(String whiteListTo) {
        this.whiteListTo = whiteListTo;
    }

}
