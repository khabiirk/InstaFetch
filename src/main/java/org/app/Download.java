package main.java.org.app;

public class Download {
    private final String url;
    private int progress;
    private String destination;



    private Status status;

    public Download(String url, String destination)
    {
        this.url = url;
        this.progress = 0;
        this.status = Status.NOT_STARTED;
        this.destination = destination;
    }

    public void changeDestination(String path)
    {
        this.destination = path;
    }
    public String getDestination()
    {
        return this.destination;
    }
    public int getProgress() {
        return progress;
    }

    public String getUrl() {
        return url;
    }
    public Status getStatus() {
        return status;
    }

    public void progressing(int val)
    {
        this.progress = val;
    }
    public void changeStatus(Status s)
    {
        this.status = s;
    }

    public boolean StartDownload()
    {
        /* Download logic */
        this.progress += 1;
        this.status = Status.IN_PROGRESS;
        return true;
    }
    public void pauseDownload()
    {
        /*Pause logic*/
        this.status = Status.PAUSED;

    }
    public boolean resumeDownload()
    {
        /* Resume logic */
        this.status = Status.IN_PROGRESS;
        return true;
    }
}
