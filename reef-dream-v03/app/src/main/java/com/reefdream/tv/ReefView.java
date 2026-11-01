package com.reefdream.tv;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;
public class ReefView extends View {
 private final Paint p=new Paint(3); private final Random r=new Random(7);
 private final float[] x=new float[18],y=new float[18],speed=new float[18];
 private long start=System.currentTimeMillis();
 public ReefView(Context c){super(c);setLayerType(View.LAYER_TYPE_SOFTWARE,null);for(int i=0;i<x.length;i++){x[i]=r.nextFloat();y[i]=r.nextFloat();speed[i]=.02f+r.nextFloat()*.04f;}}
 protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();float t=(System.currentTimeMillis()-start)/1000f;
 p.setShader(new LinearGradient(0,0,0,h,0xff006b85,0xff001427,Shader.TileMode.CLAMP));c.drawRect(0,0,w,h,p);p.setShader(null);
 p.setColor(0xffc2a06b);c.drawRect(0,h*.91f,w,h,p);
 for(int i=0;i<22;i++){float bx=(i*137%1000)/1000f*w,by=h*(.95f-(i%5)*.06f);p.setColor(i%2==0?0xff087f73:0xff125f54);c.drawOval(bx-8,by-38,bx+8,by+35,p);}
 for(int i=0;i<x.length;i++){float fx=((x[i]+t*speed[i])%1.3f-.15f)*w,fy=(.16f+y[i]*.65f)*h;float size=Math.max(8,w*.018f+(i%4)*4);
 p.setColor(i%3==0?0xffffc65c:i%3==1?0xff64d5e8:0xffff835c);
 c.drawOval(fx-size,fy-size*.45f,fx+size,fy+size*.45f,p);
 Path tail=new Path();tail.moveTo(fx-size,fy);tail.lineTo(fx-size*1.6f,fy-size*.5f);tail.lineTo(fx-size*1.6f,fy+size*.5f);tail.close();c.drawPath(tail,p);
 p.setColor(0xff102b36);c.drawCircle(fx+size*.55f,fy-size*.12f,Math.max(2,size*.09f),p);
 }postInvalidateDelayed(33);
 }
}