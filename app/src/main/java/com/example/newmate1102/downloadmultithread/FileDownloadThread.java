package com.example.newmate1102.downloadmultithread;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by newmate1102 on 2016/1/20.
 */
public class FileDownloadThread extends Thread {

    private boolean isCompledted = false;
    private int downloadLength = 0;
    private URL downloadUrl;
    private File file;
    private  int blocksize;
    private int threadID;

    public FileDownloadThread(URL downloadUrl, File file, int blocksize, int threadID){
        this.downloadUrl = downloadUrl;
        this.file = file;
        this.blocksize = blocksize;
        this.threadID = threadID;
    }

    @Override
    public void run() {
//        super.run();
        BufferedInputStream bis = null;   //学习这两个类的用途和接口
        RandomAccessFile raf = null;
        try {
            URLConnection connection = downloadUrl.openConnection();
            connection.setAllowUserInteraction(true);   //xuhy
            int startpos = blocksize * (threadID - 1);
            int stoppos = blocksize * threadID - 1;
            connection.setRequestProperty("Range", "bytes=" + startpos + "-" + stoppos);

            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(connection.getInputStream());

            raf = new RandomAccessFile(file, "rwd");
            raf.seek(startpos);

            int len;
            while ((len = bis.read(buffer, 0, 1024)) != -1) {
                raf.write(buffer, 0, len);
                downloadLength += len;
            }
            isCompledted = true;


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean isCompledted(){
        return isCompledted;
    }

    public int getDownloadLength(){
        return downloadLength;
    }
}
