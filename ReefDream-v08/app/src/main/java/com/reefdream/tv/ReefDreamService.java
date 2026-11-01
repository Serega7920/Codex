package com.reefdream.tv;
import android.service.dreams.DreamService;
public final class ReefDreamService extends DreamService {
 @Override public void onAttachedToWindow(){super.onAttachedToWindow();setFullscreen(true);setInteractive(false);setContentView(new ReefView(this));}
}
