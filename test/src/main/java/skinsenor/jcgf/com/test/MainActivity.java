package skinsenor.jcgf.com.test;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text= (TextView) findViewById(R.id.text);
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            String speed= (String) bundle.get("speed");
            String progress= (String) bundle.get("progress");
            text.setText("下载速度："+speed+"  用时："+progress);
        }
    };

    public void clickBtn(View view){
        DownLaoder downLaoder=new DownLaoder(handler);
        downLaoder.start();
    }
}
