package skinsenor.jcgf.com.test;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DownLaoder extends Thread {

    private String url = "http://down.lmtxz1.com/20180420/LaoMaoTao_STA_gw.exe";
    //	private String url = "https://dldir1.qq.com/qqfile/qq/TIM2.2.0/23734/TIM2.2.0.exe";
    private String filepath = Environment.getExternalStorageDirectory() + File.separator + "LaoMaoTao_STA_gw.exe";

    private HttpURLConnection connet;
    private int threadnum = 10;
    private long blocksize;

    private DownFileLoadTask[] task;

    private long starttime = 0;
    private long currtime = 0;
    private int ustime = 0;
    private long countsize;

    private Handler handler;


    private ArrayList<DownFileLoadTask> tasks;

    public DownLaoder(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {

        try {
            URL urlc = new URL(url);
            connet = (HttpURLConnection) urlc.openConnection();

            connet.setRequestProperty("Accept-Encoding", "identity");
            connet.setRequestProperty("User-Agent",
                    " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, likeGecko) Chrome/37.0.2062.120 Safari/537.36");
            connet.setConnectTimeout(5000);
            connet.setReadTimeout(5000);

            if (connet.getResponseCode() == HttpURLConnection.HTTP_OK) {
                long filesize = connet.getContentLength();
                System.out.println(filesize + "�ļ���С--->" + (filesize / (1024)));
                File file = new File(filepath);
                if (!file.exists()) {
                    file.createNewFile();
                    // file.mkdirs();
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.setLength(filesize);
                raf.close();

                // ÿ���߳����صĴ�С
                blocksize = (filesize % threadnum) == 0 ? ((filesize / threadnum)) : (filesize / threadnum + 1);
                task = new DownFileLoadTask[threadnum];
                tasks = new ArrayList<>();
                for (int i = 0; i < threadnum; i++) {
                    long start = blocksize * i;
                    long end = blocksize * (i + 1);
                    DownFileInfo info = new DownFileInfo();
                    info.setFilepath(filepath);
                    info.setUrlstr(url);
                    info.setStartpos(start);
                    info.setEndpos(end);
                    task[i] = new DownFileLoadTask(info);
                    task[i].start();
                    tasks.add(task[i]);
                }
                starttime = System.currentTimeMillis();
                while (!tasks.isEmpty()) {
                    countsize = 0;
                    for (int i = 0; i < tasks.size(); i++) {
                        countsize = countsize + tasks.get(i).getCurrdownsize();
                        if (tasks.get(i).isIsfinish()) {
                            tasks.remove(tasks.get(i));
                        }
                    }
                    currtime = System.currentTimeMillis();
                    ustime = (int) ((currtime - starttime) / 1000);
                    if (ustime == 0) {
                        ustime = 1;
                    }
                    long downloadSpeed = (countsize / ustime) / (1024);
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("speed", downloadSpeed + "k/s");
//                    bundle.putString("progress", ((float) ((countsize * 100) / filesize)) + "%");
                    message.setData(bundle);
                    handler.sendMessage(message);
//                    System.out.println(countsize + "下载速度---->" + downloadSpeed + "k/s");
                    Log.e("下载速度---" + countsize, "下载速度---->" + downloadSpeed + " k/s");
                    sleep(1000);
                }
                long endtime = System.currentTimeMillis();
//                System.out.println("用时--->" + ((endtime - starttime) / 1000) + "秒");
                Log.e("用时--->", ((endtime - starttime) / 1000) + "秒");

                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("progress", ((endtime - starttime) / 1000) + "秒");
                message.setData(bundle);
                handler.sendMessage(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
