package com.reefdream.tv;
import android.opengl.*;import java.nio.*;import javax.microedition.khronos.egl.EGLConfig;import javax.microedition.khronos.opengles.GL10;
public class ReefRenderer implements GLSurfaceView.Renderer {
 private final float[] p=new float[16],v=new float[16],m=new float[16],mv=new float[16],out=new float[16];
 private int prog,loc,mat,col; private FloatBuffer fish,sand,coral;
 private final long start=System.nanoTime();
 private FloatBuffer buf(float[] a){FloatBuffer b=ByteBuffer.allocateDirect(a.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();b.put(a);b.position(0);return b;}
 private int shader(int type,String s){int id=GLES20.glCreateShader(type);GLES20.glShaderSource(id,s);GLES20.glCompileShader(id);return id;}
 public void onSurfaceCreated(GL10 g,EGLConfig c){
  GLES20.glClearColor(.01f,.12f,.23f,1);GLES20.glEnable(GLES20.GL_DEPTH_TEST);
  prog=GLES20.glCreateProgram();GLES20.glAttachShader(prog,shader(35633,"attribute vec3 a;uniform mat4 u;void main(){gl_Position=u*vec4(a,1.);}"));GLES20.glAttachShader(prog,shader(35632,"precision mediump float;uniform vec4 c;void main(){gl_FragColor=c;}"));GLES20.glLinkProgram(prog);
  loc=GLES20.glGetAttribLocation(prog,"a");mat=GLES20.glGetUniformLocation(prog,"u");col=GLES20.glGetUniformLocation(prog,"c");
  fish=buf(new float[]{-.6f,0,0,.25f,.22f,0,.6f,0,0,-.6f,0,0,.6f,0,0,.25f,-.22f,0,-.6f,0,0,-.9f,.3f,0,-.9f,-.3f,0});
  sand=buf(new float[]{-8,-2.7f,-2,8,-2.7f,-2,8,-2.7f,2,-8,-2.7f,-2,8,-2.7f,2,-8,-2.7f,2});
  coral=buf(new float[]{-.12f,0,0,.12f,0,0,.08f,1,0,-.12f,0,0,.08f,1,0,-.08f,1,0});
 }
 public void onSurfaceChanged(GL10 g,int w,int h){GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(p,0,45,(float)w/Math.max(h,1),.1f,100);Matrix.setLookAtM(v,0,0,0,9,0,0,0,0,1,0);}
 private void draw(FloatBuffer b,int n,float x,float y,float z,float sx,float sy,float r,float gr,float bl){
  Matrix.setIdentityM(m,0);Matrix.translateM(m,0,x,y,z);Matrix.scaleM(m,0,sx,sy,1);Matrix.multiplyMM(mv,0,v,0,m,0);Matrix.multiplyMM(out,0,p,0,mv,0);
  GLES20.glUseProgram(prog);GLES20.glUniformMatrix4fv(mat,1,false,out,0);GLES20.glUniform4f(col,r,gr,bl,1);GLES20.glEnableVertexAttribArray(loc);GLES20.glVertexAttribPointer(loc,3,GLES20.GL_FLOAT,false,0,b);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,n);GLES20.glDisableVertexAttribArray(loc);
 }
 public void onDrawFrame(GL10 g){GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);float t=(System.nanoTime()-start)/1e9f;
  draw(sand,6,0,0,0,1,1,.7f,.56f,.35f);
  for(int i=0;i<22;i++)draw(coral,6,-5+i%11, -2.65f,-1.2f,1,.45f+i%5*.22f,.35f+i%3*.2f,.55f,.48f);
  for(int i=0;i<14;i++){float x=(t*(.22f+i%5*.06f)+i*.93f)%12-6;float y=(float)Math.sin(t*.5f+i)*.25f-1+(i%5)*.55f;draw(fish,9,x,y,-.5f-(i%4)*.35f,.45f+i%3*.13f,.65f,.95f,.5f+i%3*.1f,.15f);}
 }
}