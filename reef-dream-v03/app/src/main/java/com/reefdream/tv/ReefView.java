package com.reefdream.tv;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;

/** Low-overhead layered reef renderer for Android 9 TV. No bitmap decoding or network. */
public final class ReefView extends View {
    private static final int FISH_COUNT=16, BUBBLE_COUNT=32, CORAL_COUNT=26;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path=new Path();
    private final Random random=new Random(4242);
    private final float[] fishX=new float[FISH_COUNT], fishY=new float[FISH_COUNT];
    private final float[] fishSpeed=new float[FISH_COUNT], fishSize=new float[FISH_COUNT];
    private final float[] bubbleX=new float[BUBBLE_COUNT], bubbleY=new float[BUBBLE_COUNT];
    private final float[] coralX=new float[CORAL_COUNT], coralH=new float[CORAL_COUNT];
    private final int[] fishColor={0xffffba55,0xff56d6de,0xffff795e,0xfff4e1a4,0xff87b9f0};
    private final int[] coralColor={0xffe07778,0xff9a65a7,0xffdf8c58,0xff508e77};
    private long lastFrame=System.nanoTime();
    private float elapsed=0;
    private LinearGradient background;
    private float w,h;
    public ReefView(Context context) {
        super(context);
        for(int i=0;i<FISH_COUNT;i++){fishX[i]=random.nextFloat();fishY[i]=.13f+random.nextFloat()*.64f;fishSpeed[i]=.013f+random.nextFloat()*.022f;fishSize[i]=.65f+random.nextFloat()*.8f;}
        for(int i=0;i<BUBBLE_COUNT;i++){bubbleX[i]=random.nextFloat();bubbleY[i]=random.nextFloat();}
        for(int i=0;i<CORAL_COUNT;i++){coralX[i]=random.nextFloat();coralH[i]=.035f+random.nextFloat()*.14f;}
    }
    @Override protected void onSizeChanged(int width,int height,int oldW,int oldH){
        super.onSizeChanged(width,height,oldW,oldH);
        w=width;h=height;
        background=new LinearGradient(0,0,0,h,new int[]{0xff00637b,0xff003a56,0xff001725},null,Shader.TileMode.CLAMP);
    }
    private void oval(float l,float t,float r,float b,int color){
        paint.setColor(color);paint.setStyle(Paint.Style.FILL);cCanvas.drawOval(l,t,r,b,paint);
    }
    private Canvas cCanvas;
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(w<=0||h<=0)return;
        cCanvas=canvas;
        long now=System.nanoTime();elapsed+=Math.min(.06f,(now-lastFrame)/1000000000f);lastFrame=now;
        paint.setStyle(Paint.Style.FILL);paint.setShader(background);canvas.drawRect(0,0,w,h,paint);paint.setShader(null);
        // Sun shafts fade into the deep water.
        for(int i=0;i<5;i++){
            float x=w*(.12f+i*.2f);
            path.reset();path.moveTo(x,0);path.lineTo(x+w*.065f,0);path.lineTo(x+w*.22f,h*.84f);path.close();
            paint.setColor(0x0bdbfaff);canvas.drawPath(path,paint);
        }
        // Distant reef silhouettes give depth without a 3D GPU load.
        for(int i=0;i<15;i++){
            float x=w*(i/14f), top=h*(.81f-(i%4)*.028f);
            oval(x-w*.07f,top,x+w*.07f,h*.96f,0xff164c58);
        }
        paint.setColor(0xffb29b72);canvas.drawRect(0,h*.93f,w,h,paint);
        for(int i=0;i<CORAL_COUNT;i++){
            float x=w*coralX[i],base=h*.95f,top=base-h*coralH[i],s=Math.max(2,w*.004f);
            int color=coralColor[i%coralColor.length];
            paint.setColor(color);paint.setStrokeWidth(s);paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawLine(x,base,x,top,paint);
            canvas.drawLine(x,base-h*coralH[i]*.45f,x-s*3,top+s*3,paint);
            canvas.drawLine(x,base-h*coralH[i]*.65f,x+s*3,top+s*2,paint);
            paint.setStyle(Paint.Style.FILL);
            oval(x-s*1.5f,top-s*2,x+s*1.5f,top+s,color);
        }
        for(int i=0;i<BUBBLE_COUNT;i++){
            float y=(bubbleY[i]+elapsed*(.008f+(i%5)*.003f))%1.15f;
            float x=w*(bubbleX[i]+(float)Math.sin(elapsed*.6f+i)*.008f);
            float radius=Math.max(1.5f,w*(.0012f+i%4*.00045f));
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(Math.max(1,radius*.35f));
            paint.setColor(0x6686e4f3);canvas.drawCircle(x,h*(1.05f-y),radius,paint);
        }
        paint.setStyle(Paint.Style.FILL);
        for(int i=0;i<FISH_COUNT;i++){
            float fx=w*((fishX[i]+elapsed*fishSpeed[i])%1.25f-.12f);
            float fy=h*(fishY[i]+(float)Math.sin(elapsed*.7f+i*1.7f)*.018f);
            float s=Math.max(10,w*.016f*fishSize[i]);
            int color=fishColor[i%fishColor.length];
            // Tail, oval body, eye and subtle fin.
            path.reset();path.moveTo(fx-s*.75f,fy);path.lineTo(fx-s*1.65f,fy-s*.57f);path.lineTo(fx-s*1.65f,fy+s*.57f);path.close();
            paint.setColor(color);canvas.drawPath(path,paint);
            oval(fx-s,fy-s*.48f,fx+s,fy+s*.48f,color);
            path.reset();path.moveTo(fx-s*.3f,fy-s*.35f);path.lineTo(fx,fy-s*.8f);path.lineTo(fx+s*.25f,fy-s*.35f);path.close();
            paint.setColor(0x889fffff);canvas.drawPath(path,paint);
            paint.setColor(0xff102b38);canvas.drawCircle(fx+s*.6f,fy-s*.12f,Math.max(1.5f,s*.095f),paint);
        }
        // 30 fps ceiling avoids excess heat and CPU use on Mi TV 4A.
        postInvalidateDelayed(33);
    }
    @Override protected void onDetachedFromWindow(){super.onDetachedFromWindow();cCanvas=null;}
}