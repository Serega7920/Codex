package com.reefdream.tv;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Random;

/** Photo-based reef with independently moving fish sprites, bubbles and parallax. */
public final class ReefView extends View {
 private Bitmap reef;
 private final Bitmap[] fish=new Bitmap[7];
 private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
 private final float[] phase=new float[18],height=new float[18],rate=new float[18],size=new float[18];
 private final float[] bx=new float[34],by=new float[34];
 private long start=System.nanoTime();
 private static final String[] NAMES={"clown1","clown2","blue_tang","yellow_tang","angel","banner","butterfly"};
 public ReefView(Context c){super(c);
   try {
     reef=BitmapFactory.decodeStream(c.getAssets().open("reef.jpg"));
     for(int i=0;i<fish.length;i++)fish[i]=BitmapFactory.decodeStream(c.getAssets().open(NAMES[i]+".png"));
   }catch(Exception e){throw new IllegalStateException("Missing reef assets",e);}
   Random r=new Random(73);
   for(int i=0;i<phase.length;i++){phase[i]=r.nextFloat();height[i]=.18f+r.nextFloat()*.58f;rate[i]=.012f+r.nextFloat()*.022f;size[i]=.48f+r.nextFloat()*.6f;}
   for(int i=0;i<bx.length;i++){bx[i]=r.nextFloat();by[i]=r.nextFloat();}
 }
 @Override protected void onDraw(Canvas c){
   super.onDraw(c);
   float w=getWidth(),h=getHeight();if(w<1||h<1)return;
   float t=(System.nanoTime()-start)/1e9f;
   p.setStyle(Paint.Style.FILL);p.setColor(Color.WHITE);p.setAlpha(255);
   float sr=(float)reef.getWidth()/reef.getHeight(),dr=w/h;
   Rect src;
   if(sr>dr){int sw=(int)(reef.getHeight()*dr);int x=(reef.getWidth()-sw)/2;src=new Rect(x,0,x+sw,reef.getHeight());}
   else {int sh=(int)(reef.getWidth()/dr);int y=(reef.getHeight()-sh)/2;src=new Rect(0,y,reef.getWidth(),y+sh);}
   c.drawBitmap(reef,src,new RectF(0,0,w,h),p);
   // Fish have independent speeds and swim in both directions, at different apparent depths.
   for(int i=0;i<phase.length;i++){
     Bitmap b=fish[i%fish.length];
     float fw=w*(.065f+.06f*size[i]);
     float fh=fw*b.getHeight()/b.getWidth();
     boolean reverse=(i%2==1);
     float progress=(phase[i]+t*rate[i])%1.25f;
     float x=(reverse?1.12f-progress:progress-.12f)*w;
     float y=h*(height[i]+.013f*(float)Math.sin(t*(.7f+i*.09f)+i));
     c.save();
     c.translate(x,y);
     if(reverse)c.scale(-1,1);
     c.rotate(2.2f*(float)Math.sin(t*1.4f+i));
     p.setAlpha(i%4==0?195:235);
     c.drawBitmap(b,null,new RectF(-fw/2,-fh/2,fw/2,fh/2),p);
     c.restore();
   }
   p.setAlpha(255);
   for(int i=0;i<bx.length;i++){
     float x=w*(bx[i]+.007f*(float)Math.sin(t*.7f+i));
     float y=h*(1.08f-(by[i]+t*(.009f+i%4*.003f))%1.2f);
     float radius=Math.max(1.2f,w*(.0008f+i%4*.00035f));
     p.setColor(0x88d8f8ff);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(Math.max(1f,radius*.23f));
     c.drawCircle(x,y,radius,p);
   }
   p.setStyle(Paint.Style.FILL);p.setColor(Color.WHITE);
   postInvalidateDelayed(40);
 }
}
