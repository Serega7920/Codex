package com.reefdream.tv;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
public final class MainActivity extends Activity {
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);setContentView(new ReefView(this));}
}
