package com.reefdream.tv;
import android.app.Activity;import android.os.Bundle;
public class PreviewActivity extends Activity {private ReefView view;
 @Override public void onCreate(Bundle b){super.onCreate(b);view=new ReefView(this);setContentView(view);}
 @Override public void onResume(){super.onResume();view.onResume();}
 @Override public void onPause(){view.onPause();super.onPause();}}
