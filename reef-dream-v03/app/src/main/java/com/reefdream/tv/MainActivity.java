package com.reefdream.tv;
import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
public class MainActivity extends Activity { public void onCreate(Bundle b){super.onCreate(b);getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_FULLSCREEN);setContentView(new ReefView(this));} }
