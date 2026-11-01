package com.reefdream.tv;
import android.service.dreams.DreamService;
public class ReefDreamService extends DreamService { public void onAttachedToWindow(){super.onAttachedToWindow();setInteractive(false);setFullscreen(true);setContentView(new ReefView(this));} }
