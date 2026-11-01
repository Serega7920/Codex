package com.reefdream.tv;
import android.content.Context;import android.opengl.GLSurfaceView;
public class ReefView extends GLSurfaceView {
 public ReefView(Context c){super(c);setEGLContextClientVersion(2);setPreserveEGLContextOnPause(true);setRenderer(new ReefRenderer());setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);}
}