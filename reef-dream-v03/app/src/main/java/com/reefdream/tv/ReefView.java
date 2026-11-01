package com.reefdream.tv;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/** Real OpenGL ES 2.0 underwater scene. GPU renders 3D fish, rocks and coral. */
public final class ReefView extends GLSurfaceView {
    private final ReefRenderer renderer;
    public ReefView(Context c) {
        super(c);
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8,8,8,0,16,0);
        renderer=new ReefRenderer();
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setPreserveEGLContextOnPause(true);
    }
    private static final class ReefRenderer implements GLSurfaceView.Renderer {
        private static final String VERT =
          "attribute vec3 aPos; attribute vec3 aNormal; uniform mat4 uMvp; uniform mat4 uModel; varying vec3 vNormal; varying vec3 vWorld; void main(){ vec4 w=uModel*vec4(aPos,1.);vWorld=w.xyz;vNormal=mat3(uModel)*aNormal;gl_Position=uMvp*vec4(aPos,1.);}";
        private static final String FRAG =
          "precision mediump float; varying vec3 vNormal; varying vec3 vWorld; uniform vec3 uColor; uniform float uTime; uniform float uFog; void main(){vec3 n=normalize(vNormal);vec3 light=normalize(vec3(-.4,.85,1.));float diff=max(dot(n,light),0.);float rim=pow(1.-max(dot(n,vec3(0.,0.,1.)),0.),2.);float caustic=.08*sin(vWorld.x*5.+uTime*.8)*sin(vWorld.z*4.-uTime*.6);vec3 c=uColor*(.30+.68*diff+caustic)+vec3(.13,.34,.39)*rim;float fog=clamp(uFog*.035*length(vWorld),0.,.48);c=mix(c,vec3(.02,.21,.30),fog);gl_FragColor=vec4(c,1.);}";
        private final float[] projection=new float[16],view=new float[16],model=new float[16],mv=new float[16],mvp=new float[16];
        private int program,aPos,aNormal,uMvp,uModel,uColor,uTime,uFog;
        private Mesh sphere,cone,plane;
        private long start=System.nanoTime();
        private int width,height;
        private final float[][] colors={{1f,.63f,.19f},{.12f,.78f,.88f},{.94f,.31f,.22f},{.93f,.84f,.43f},{.25f,.45f,.92f}};
        @Override public void onSurfaceCreated(javax.microedition.khronos.opengles.GL10 unused,javax.microedition.khronos.egl.EGLConfig config){
            GLES20.glClearColor(.015f,.19f,.29f,1);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            program=link(VERT,FRAG);
            aPos=GLES20.glGetAttribLocation(program,"aPos");aNormal=GLES20.glGetAttribLocation(program,"aNormal");
            uMvp=GLES20.glGetUniformLocation(program,"uMvp");uModel=GLES20.glGetUniformLocation(program,"uModel");
            uColor=GLES20.glGetUniformLocation(program,"uColor");uTime=GLES20.glGetUniformLocation(program,"uTime");uFog=GLES20.glGetUniformLocation(program,"uFog");
            sphere=makeSphere(10,14);cone=makeCone(10);plane=makeFloor();
        }
        @Override public void onSurfaceChanged(javax.microedition.khronos.opengles.GL10 unused,int w,int h){
            width=w;height=h;GLES20.glViewport(0,0,w,h);
            Matrix.perspectiveM(projection,0,48f,(float)w/Math.max(h,1),.1f,100f);
            Matrix.setLookAtM(view,0,0,2.2f,9.8f,0,1.0f,0,0,1,0);
        }
        @Override public void onDrawFrame(javax.microedition.khronos.opengles.GL10 unused){
            float t=(System.nanoTime()-start)/1e9f;
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
            GLES20.glUseProgram(program);GLES20.glUniform1f(uTime,t);GLES20.glUniform1f(uFog,1f);
            // Sandy seabed, made from a horizontal mesh.
            draw(plane,0,-.65f,0,11,1,8,0,0,new float[]{.55f,.47f,.32f},t);
            // Rounded rocks, staggered across the foreground and background.
            for(int i=0;i<18;i++){
                float x=((i*37)%19-9)*.51f,z=-2.8f+(i%5)*1.25f;
                float scale=.28f+(i%4)*.13f;
                draw(sphere,x,-.54f,z,scale*1.3f,scale*.58f,scale,0,0,new float[]{.17f,.31f,.30f},t);
            }
            // Branching coral: cones with 3D depth and uneven heights.
            for(int i=0;i<25;i++){
                float x=((i*19)%31-15)*.31f,z=-2.8f+(i%6)*.76f;
                float hh=.35f+(i%5)*.15f;
                float[] color=i%3==0?new float[]{.83f,.35f,.36f}:i%3==1?new float[]{.59f,.35f,.63f}:new float[]{.76f,.52f,.29f};
                draw(cone,x,-.54f+hh*.5f,z,.10f,hh,.10f,0,0,color,t);
                draw(cone,x-.14f,-.54f+hh*.67f,z,.065f,hh*.62f,.065f,0,28,color,t);
                draw(cone,x+.15f,-.54f+hh*.58f,z,.07f,hh*.66f,.07f,0,-25,color,t);
            }
            // Independently animated 3D tropical fish with tails and fins.
            for(int i=0;i<16;i++){
                float speed=.18f+(i%5)*.06f;
                float phase=(i*.071f+t*speed*.10f)%1.25f;
                boolean left=i%3==0;
                float x=(left?1f-phase:phase)*11f-5.5f;
                float y=.15f+(i%6)*.31f+(float)Math.sin(t*.75+i)*.10f;
                float z=-2.7f+(i%5)*1.1f;
                float size=.24f+(i%4)*.06f;
                float dir=left?180:0;
                float[] color=colors[i%colors.length];
                draw(sphere,x,y,z,size*1.6f,size*.68f,size*.48f,dir,0,color,t);
                float tailX=x+(left?size*1.7f:-size*1.7f);
                draw(cone,tailX,y,z,size*.52f,size*.95f,size*.18f,dir+(float)Math.sin(t*5+i)*18,90,color,t);
                draw(cone,x,y+size*.53f,z,size*.24f,size*.5f,size*.20f,dir,0,color,t);
                draw(sphere,x+(left?-size:size),y+size*.12f,z+size*.43f,size*.10f,size*.10f,size*.08f,0,0,new float[]{.015f,.03f,.04f},t);
            }
            // Rising pearl-like bubbles.
            for(int i=0;i<24;i++){
                float x=((i*13)%29-14)*.35f+(float)Math.sin(t*.8f+i)*.09f;
                float y=-.5f+((i*.21f+t*(.09f+i%4*.025f))%3.9f);
                float z=-2f+(i%7)*.5f;
                float s=.025f+(i%3)*.012f;
                draw(sphere,x,y,z,s,s,s,0,0,new float[]{.52f,.88f,.96f},t);
            }
            try{Thread.sleep(34);}catch(InterruptedException ignored){Thread.currentThread().interrupt();}
        }
        private void draw(Mesh mesh,float x,float y,float z,float sx,float sy,float sz,float yaw,float roll,float[] color,float t){
            Matrix.setIdentityM(model,0);Matrix.translateM(model,0,x,y,z);
            Matrix.rotateM(model,0,yaw,0,1,0);Matrix.rotateM(model,0,roll,0,0,1);Matrix.scaleM(model,0,sx,sy,sz);
            Matrix.multiplyMM(mv,0,view,0,model,0);Matrix.multiplyMM(mvp,0,projection,0,mv,0);
            GLES20.glUniformMatrix4fv(uMvp,1,false,mvp,0);GLES20.glUniformMatrix4fv(uModel,1,false,model,0);
            GLES20.glUniform3fv(uColor,1,color,0);
            mesh.data.position(0);GLES20.glVertexAttribPointer(aPos,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glEnableVertexAttribArray(aPos);
            mesh.data.position(3);GLES20.glVertexAttribPointer(aNormal,3,GLES20.GL_FLOAT,false,24,mesh.data);GLES20.glEnableVertexAttribArray(aNormal);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,mesh.count);
        }
        private static class Mesh {
            FloatBuffer data;int count;
            Mesh(float[] a){data=ByteBuffer.allocateDirect(a.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();data.put(a).position(0);count=a.length/6;}
        }
        private static void vert(java.util.ArrayList<Float> out,float x,float y,float z,float nx,float ny,float nz){
            out.add(x);out.add(y);out.add(z);out.add(nx);out.add(ny);out.add(nz);
        }
        private static Mesh makeSphere(int lat,int lon){
            java.util.ArrayList<Float> a=new java.util.ArrayList<>();
            for(int i=0;i<lat;i++)for(int j=0;j<lon;j++){
                float t0=(float)Math.PI*i/lat,t1=(float)Math.PI*(i+1)/lat,p0=2f*(float)Math.PI*j/lon,p1=2f*(float)Math.PI*(j+1)/lon;
                float[] v0=point(t0,p0),v1=point(t1,p0),v2=point(t1,p1),v3=point(t0,p1);
                tri(a,v0,v1,v2);tri(a,v0,v2,v3);
            }
            return pack(a);
        }
        private static float[] point(float theta,float phi){return new float[]{(float)(Math.sin(theta)*Math.cos(phi)),(float)Math.cos(theta),(float)(Math.sin(theta)*Math.sin(phi))};}
        private static void tri(java.util.ArrayList<Float>a,float[] p,float[] q,float[] r){vert(a,p[0],p[1],p[2],p[0],p[1],p[2]);vert(a,q[0],q[1],q[2],q[0],q[1],q[2]);vert(a,r[0],r[1],r[2],r[0],r[1],r[2]);}
        private static Mesh makeCone(int n){
            java.util.ArrayList<Float>a=new java.util.ArrayList<>();
            for(int i=0;i<n;i++){
                double p=i*2*Math.PI/n,q=(i+1)*2*Math.PI/n;
                float[] b={(float)Math.cos(p),-.5f,(float)Math.sin(p)};
                float[] c={(float)Math.cos(q),-.5f,(float)Math.sin(q)};
                float[] tip={0,.5f,0};tri(a,b,c,tip);
            }
            return pack(a);
        }
        private static Mesh makeFloor(){
            java.util.ArrayList<Float>a=new java.util.ArrayList<>();
            float[] p={-1,0,-1},q={1,0,-1},r={1,0,1},s={-1,0,1};
            for(float[] v:new float[][]{p,q,r,p,r,s})vert(a,v[0],v[1],v[2],0,1,0);
            return pack(a);
        }
        private static Mesh pack(java.util.ArrayList<Float>a){float[] f=new float[a.size()];for(int i=0;i<f.length;i++)f[i]=a.get(i);return new Mesh(f);}
        private static int shader(int type,String src){
            int s=GLES20.glCreateShader(type);GLES20.glShaderSource(s,src);GLES20.glCompileShader(s);
            int[] ok=new int[1];GLES20.glGetShaderiv(s,GLES20.GL_COMPILE_STATUS,ok,0);
            if(ok[0]==0)throw new RuntimeException(GLES20.glGetShaderInfoLog(s));return s;
        }
        private static int link(String vs,String fs){
            int p=GLES20.glCreateProgram();GLES20.glAttachShader(p,shader(GLES20.GL_VERTEX_SHADER,vs));GLES20.glAttachShader(p,shader(GLES20.GL_FRAGMENT_SHADER,fs));GLES20.glLinkProgram(p);
            int[] ok=new int[1];GLES20.glGetProgramiv(p,GLES20.GL_LINK_STATUS,ok,0);
            if(ok[0]==0)throw new RuntimeException(GLES20.glGetProgramInfoLog(p));return p;
        }
    }
}