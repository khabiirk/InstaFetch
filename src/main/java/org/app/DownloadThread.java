package main.java.org.app;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread{

    private final Download download;
    private final String task;

    public DownloadThread(Download download, String task)
    {
        this.download = download;
        this.task = task;
    }
    @Override
    public void run()
    {

        try {
            URL url = new URL(download.getUrl());
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());

            long fileSize = httpConnection.getContentLength(); // Obtain the size of the file
            try(InputStream inputStream = url.openStream();
                CountingInputStream c = new CountingInputStream(inputStream); // We use CountingInputStream to get the number of bytes downloaded
                FileOutputStream out = new FileOutputStream(download.getDestination() + "/" + FilenameUtils.getName(url.getPath()));

            )
            {


                Thread downloading = new Thread(() -> {
                        try {
                            //Download the file
                            IOUtils.copyLarge(c, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                });
                downloading.start();

                while(c.getByteCount() < fileSize)
                {
                    // Update the progress attribute
                    this.download.progressing((int) (100 * c.getByteCount()/fileSize));

                }
                this.download.changeDestination(download.getDestination() + "/" + FilenameUtils.getName(url.getPath()));

                this.download.changeStatus(Status.COMPLETED);
                System.out.println("File downloaded successfully!");


            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
