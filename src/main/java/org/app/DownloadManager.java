package main.java.org.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    private int maxThreads;
    private ExecutorService threadPool;
    private List<Download> downloadQueue;

    public DownloadManager(int maxThreads)
    {
        this.maxThreads = maxThreads;
        this.threadPool = Executors.newFixedThreadPool(maxThreads);
        this.downloadQueue = new ArrayList<>();
    }

    public void addDownload(String url, String dest)
    {
        Download download = new Download(url, dest);
        downloadQueue.add(download);
    }

    public void startDownloads()
    {
        int c = 1;
        for(Download download : downloadQueue)
        {
            DownloadThread downloadThread = new DownloadThread(download, "Task " + c);
            c += 1;
            threadPool.execute(downloadThread);
        }
    }
    public void pauseDownload(int index){
        this.downloadQueue.get(0).pauseDownload();
    }
    public void stopDownloads() {
        threadPool.shutdownNow();

    }
    public int getDownloadProgress(int index)
    {

        return downloadQueue.get(index).getProgress();
    }
    public String getDownloadPath()
    {
        return downloadQueue.get(0).getDestination();
    }

    public Status getDownloadStatus(int index)
    {
        return downloadQueue.get(index).getStatus();
    }

    public void printDownloadStatus() {
        for (Download download : downloadQueue) {
            System.out.println(download);
        }
    }


        //downloadManager.addDownload("https://archive.org/download/heroeseverychild0000hami/heroeseverychild0000hami.pdf");
        //downloadManager.addDownload("https://archive.org/download/foundationsofnor0000bori/foundationsofnor0000bori.pdf");












}
