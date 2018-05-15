package skinsenor.jcgf.com.test;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownFileLoadTask extends Thread {

	private DownFileInfo downinfo;
	private long currdownsize;// ��ǰ���ؽ���
	private int BUFF_SIZE = 1024*1024;

	private HttpURLConnection httpconnet;
	
	private boolean isfinish=false;
	int length = 0;

	public DownFileLoadTask(DownFileInfo downinfo) {
		super();
		this.downinfo = downinfo;
	}

	@Override
	public void run() {

		try {
			URL url = new URL(downinfo.getUrlstr());
			httpconnet = (HttpURLConnection) url.openConnection();

			httpconnet.setRequestProperty("Accept-Encoding", "identity");
			httpconnet.setRequestProperty("User-Agent",
					" Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, likeGecko) Chrome/37.0.2062.120 Safari/537.36");
			httpconnet.setRequestProperty("Range", "bytes=" + downinfo.getStartpos() + "-" + downinfo.getEndpos());
			httpconnet.setConnectTimeout(5000);
			httpconnet.setReadTimeout(5000);

			System.out.println("���ش�С--->" + (downinfo.getEndpos() - downinfo.getStartpos())/1024);

			if (httpconnet.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
				BufferedInputStream bis = null;
				RandomAccessFile fos = null;
				byte[] buf = new byte[BUFF_SIZE];

				fos = new RandomAccessFile(new File(downinfo.getFilepath()), "rw");
				fos.seek(downinfo.getStartpos());

				bis = new BufferedInputStream(httpconnet.getInputStream(), BUFF_SIZE);
				
				while ((length = bis.read(buf)) != -1) {
					fos.write(buf, 0, length);
					currdownsize = currdownsize + length;
					DownUtils.currtlength=DownUtils.currtlength+length;
//					System.out.println(this.getName()+"��ǰ����--->" +length);
//					Log.e("下载速度--->",""+length);
				}
				isfinish=true; 
				bis.close();
				fos.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int getLength() {
		return length;
	}

	public long getCurrdownsize() {
		return currdownsize;
	}

	public boolean isIsfinish() {
		return isfinish;
	}

}
