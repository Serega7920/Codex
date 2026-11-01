package com.reefdream.tv;
import android.service.dreams.DreamService;
public class ReefDreamService extends DreamService {
 private ReefView view;
 @Override public void onAttachedToWindow(){super.onAttachedToWindow();setInteractive(false);setFullscreen(true);setScreenBright(true);view=new ReefView(this);setContentView(view);}
 @Override public void onDreamingStarted(){super.onDreamingStarted();if(view!=null)view.onResume();}
 @Override public void onDreamingStopped(){if(view!=null)view.onPause();super.onDreamingStopped();}
}