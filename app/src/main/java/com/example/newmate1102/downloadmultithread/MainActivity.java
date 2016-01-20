package com.example.newmate1102.downloadmultithread;

import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button download;
    private ProgressBar process;
    private TextView messageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        download = (Button)findViewById(R.id.download);
        process = (ProgressBar)findViewById(R.id.process);
        messageview = (TextView)findViewById(R.id.messageview);
        download.setOnClickListener(this);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            process.setProgress(msg.getData().getInt("size"));

            float temp = (float) process.getProgress()
                    / (float) process.getMax();

            int progress = (int) (temp * 100);
            if (progress == 100) {
                Toast.makeText(MainActivity.this, "下载完成！", Toast.LENGTH_LONG).show();
            }
            messageview.setText("下载进度:" + progress + " %");
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.download){
            doDownLoad();
        }
    }

    private void doDownLoad(){
        String SDCard = Environment.getExternalStorageDirectory()+"/"+"xuhymovie";
        Log.d("SDCard",SDCard);
        File file = new File(SDCard);
        if(!file.exists()){
            file.mkdir();
        }
        process.setProgress(0);
        String urlstr = "http://d.hiphotos.bdimg.com/imgad/pic/item/472309f79052982219a8c0c0d0ca7bcb0a46d41e.jpg";
        String filename = "Wildlife.jpg";
        int thread_num = 5;
        String filepath = SDCard+"/"+filename;
        Log.d("filepath",filepath);
        downloadTask task = new downloadTask(urlstr,thread_num,filepath);
        task.start();

    }

    class downloadTask extends Thread{
        private String downloadurl;
        private int thread_num;
        private String filepath;
        private int blocksize;

        public downloadTask(String downloadurl,int thread_num,String filepath){
            this.downloadurl = downloadurl;
            this.thread_num = thread_num;
            this.filepath = filepath;
            Log.d("downloadTask para= ",downloadurl+"**"+thread_num+""+"**"+filepath);
        }
        @Override
        public void run() {
//            super.run();
            FileDownloadThread[] threads = new FileDownloadThread[thread_num];
            try{
                URL url = new URL(downloadurl);
                HttpURLConnection connect = (HttpURLConnection)url.openConnection();
                int filesize = connect.getContentLength();
                if(filesize < 0){
                    Log.d("file size = ",""+filesize);
                    return;
                }
                process.setMax(filesize);
                blocksize = (filesize % thread_num) == 0 ?(filesize / thread_num) :(filesize / thread_num)+1;
                Log.d("blocksize = ",blocksize+"");
                File file = new File(filepath);
                for (int i = 0;i < threads.length;i++){
                    threads[i] = new FileDownloadThread(url,file,blocksize,(i+1));
                    threads[i].setName("Thread:"+i);
                    threads[i].start();
                }
                boolean isFinished = false;
                int downLoadAllSize = 0;
                while (!isFinished){
                    isFinished = true;
                    downLoadAllSize = 0;
                    for(int i = 0x00;i<threads.length;i++){
                        downLoadAllSize  += threads[i].getDownloadLength();
                        if(!threads[i].isCompledted()){
                            isFinished = false;
                        }
                    }
                    Message msg = new Message();
                    msg.getData().putInt("size",downLoadAllSize);
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                }

            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }










}
