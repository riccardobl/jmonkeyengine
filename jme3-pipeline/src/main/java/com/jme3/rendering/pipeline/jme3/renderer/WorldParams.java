package com.jme3.rendering.pipeline.jme3.renderer;

import com.jme3.math.Matrix3f;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.shader.BufferObject;
import com.jme3.system.Timer;
import com.jme3.util.StatefulObject;
import com.jme3.util.TempVars;
import com.jme3.util.StatefulObject.State;
import com.jme3.util.struct.Struct;
import com.jme3.util.struct.StructField;
import com.jme3.util.struct.StructuredBufferSTD140Layout;

/**
 * WorldParams
 */
public class WorldParams {
    private  static final java.util.logging.Logger logger =  java.util.logging.Logger.getLogger( WorldParams.class.getName());
    
    
    public static class CameraStruct  implements Struct<Void>{ 
        public final StructField<Vector2f> frustumNearFar=new StructField<Vector2f>(0,new Vector2f());
        public final StructField<Vector2f> resolutionInverse=new StructField<Vector2f>(1,new Vector2f());
        public final StructField<Vector2f> resolution=new StructField<Vector2f>(2,new Vector2f());
        public final StructField<Matrix4f> viewMatrix=new StructField<Matrix4f>(3,new Matrix4f());
        public final StructField<Matrix4f> projectionMatrix=new StructField<Matrix4f>(4,new Matrix4f());
        public final StructField<Matrix4f> viewProjectionMatrix=new StructField<Matrix4f>(5,new Matrix4f());
        public final StructField<Matrix4f> viewMatrixInverse=new StructField<Matrix4f>(6,new Matrix4f());
        public final StructField<Matrix4f> projectionMatrixInverse=new StructField<Matrix4f>(7,new Matrix4f());
        public final StructField<Matrix4f> viewProjectionMatrixInverse=new StructField<Matrix4f>(8,new Matrix4f());
        public final StructField<Vector4f> viewPort=new StructField<Vector4f>(9,new Vector4f());

        public final StructField<Float> aspect=new StructField<Float>(10,0f);

        public final StructField<Vector3f> cameraPosition=new StructField<Vector3f>(11,new Vector3f());
        public final StructField<Vector3f> cameraDirection=new StructField<Vector3f>(12,new Vector3f());
        public final StructField<Vector3f> cameraLeft=new StructField<Vector3f>(13,new Vector3f());
        public final StructField<Vector3f> cameraUp=new StructField<Vector3f>(14,new Vector3f());
        @Override public Void get() {return null;}
    }


    public static class TimerStruct  implements Struct<Void>{
        public final StructField<Float> speed=new StructField<Float> (0,0f);
        public final StructField<Float> time=new StructField<Float> (1,0f);
        public final StructField<Integer> intTime=new StructField<Integer> (2,0);
        public final StructField<Float> tpf=new StructField<Float> (3,0f);
        public final StructField<Float> frameRate=new StructField<Float> (4,0f);
        public final StructField<Vector2f> deltaTime=new StructField<Vector2f> (5,new Vector2f());
        @Override public Void get() {return null;}
    }


    public static class GeometryStruct  implements Struct<Void>{
        public final StructField<Matrix4f> worldMatrix=new StructField<Matrix4f> (0,new Matrix4f());
        public final StructField<Matrix4f> worldViewMatrix=new StructField<Matrix4f>(1,new Matrix4f());
        public final StructField<Matrix3f> normalMatrix=new StructField<Matrix3f>(2,new Matrix3f());
        public final StructField<Matrix3f> worldNormalMatrix=new StructField<Matrix3f>(3,new Matrix3f());
        public final StructField<Matrix4f> worldViewProjMatrix=new StructField<Matrix4f>(4,new Matrix4f());
        public final StructField<Matrix4f> worldMatrixInv=new StructField<Matrix4f>(5,new Matrix4f());
        public final StructField<Matrix3f> worldMatrixInvTrsp=new StructField<Matrix3f>(6,new Matrix3f());
        public final StructField<Matrix4f> worldViewMatrixInv=new StructField<Matrix4f>(7,new Matrix4f());
        public final StructField<Matrix4f> worldViewProjMatrixInv=new StructField<Matrix4f>(8,new Matrix4f());
        public final StructField<Matrix3f> normalMatrixInv=new StructField<Matrix3f>(9,new Matrix3f());
        @Override public Void get() {return null;}
    }


    
    public static class CameraState extends State implements Struct<BufferObject>{        
        public final StructField<CameraStruct> camera=new StructField<CameraStruct>(0,new CameraStruct());
        private BufferObject bo;
        public BufferObject get(){
            if(bo==null) {
                bo=new BufferObject(StructuredBufferSTD140Layout.class);
                bo.setName( "CameraBuffer");
            }
            bo.updateData(this);
            return bo;
       }
    }

    public static class TimerState extends State implements Struct<BufferObject>{
        public final StructField<TimerStruct> timer=new StructField<TimerStruct>(0,new TimerStruct());
        private BufferObject bo;
        public BufferObject get(){
            if(bo==null) {
                bo=new BufferObject(StructuredBufferSTD140Layout.class);
                bo.setName(  "TimerBuffer");
            }
            bo.updateData(this);
            return bo;
        }
    }


    public static class GeometryState extends State implements Struct<BufferObject>{
        long currentCamera;
        public final StructField<GeometryStruct> geometry=new StructField<GeometryStruct>(0,new GeometryStruct());
        private BufferObject bo;
        public BufferObject get(){
            if(bo==null) {
                bo=new BufferObject(StructuredBufferSTD140Layout.class);
                bo.setName(  "GeometryBuffer");
            }
            bo.updateData(this);
            return bo;
        }
    }

  


    public static TimerState updateAndGet(Timer timer,float speed){
        TimerState timerState=timer.getState(WorldParams.class, TimerState::new);
        TimerStruct timerStruct=timerState.timer.getValueForUpdate();

        if(timerStruct.speed.getValue()!=speed)timerState.setStateUpdateNeeded();
        if(timerState.isStateUpdateNeeded()){

            // Tpf
            float tpf = timer.getTimePerFrame() * speed;
            timerStruct.tpf.setValue(tpf);

            // Time
            float time = timer.getTimeInSeconds() * speed;
            timerStruct.time.setValue(time);

            long ticksXsec = timer.getResolution();
            long tickTime = (long) (timer.getTime() * speed);
    
            // DeltaTime
            long asec = 3600;
            long tickDeltaA = asec * ticksXsec;
            float timeA = (float) (((double) tickTime) / tickDeltaA);
            timeA -= (float) Math.floor(timeA);
            float timeAsec = timeA * asec;
            timerStruct.deltaTime.getValueForUpdate().set(timeA, timeAsec);
    
            //IntTime
            long msTime = tickTime / (ticksXsec / 1000l);
            if (msTime >= Integer.MAX_VALUE) msTime %= Integer.MAX_VALUE; // for hardcore gamers...
            timerStruct.intTime.setValue( (int) msTime);

            // FrameRate
            timerStruct.frameRate.setValue(timer.getFrameRate());

            timerState.clearStateUpdateNeeded();
        }

        return timerState;
    }

    // public static GeometryState updateAndGet(StatefulObject renderer,Camera cam,Geometry geometry){
    //     GeometryState geoState=renderer.getState(WorldParams.class, GeometryState::new);
    //     return updateAndGet(geoState,cam,geometry);
    // }

    public static GeometryState updateAndGet(Camera cam,Geometry geometry){
        GeometryState geoState=geometry.getState(WorldParams.class, GeometryState::new);
        return updateAndGet(geoState,cam,geometry);
    }

    private static GeometryState updateAndGet(GeometryState geoState,Camera cam,Geometry geometry){
        GeometryStruct geoStruct=geoState.geometry.getValueForUpdate();
        CameraState camState=updateAndGet(cam);
        CameraStruct camStruct=camState.camera.getValueForUpdate();

        if(geoState.currentCamera!=camState.getStateId())geoState.setStateUpdateNeeded();
        if(geoState.isStateUpdateNeeded()){
            TempVars vars=TempVars.get();


            geoState.currentCamera=camState.getStateId();
            
            //WorldMatrix
            geoStruct.worldMatrix.getValueForUpdate().set(geometry.getWorldMatrix());
            
            //WorldViewMatrix
            geoStruct.worldViewMatrix.getValueForUpdate().set(camStruct.viewMatrix.getValue());
            geoStruct.worldViewMatrix.getValueForUpdate().multLocal( geoStruct.worldMatrix.getValue());

            //NormalMatrix
            Matrix4f tempMatrix=vars.tempMat4;
            tempMatrix.set(camStruct.viewMatrix.getValue());
            tempMatrix.multLocal(geoStruct.worldMatrix.getValue());
            tempMatrix.toRotationMatrix(geoStruct.normalMatrix.getValueForUpdate());
            geoStruct.normalMatrix.getValueForUpdate().invertLocal();
            geoStruct.normalMatrix.getValueForUpdate().transposeLocal();
            
            //WorldNormalMatrix
            tempMatrix.set(geoStruct.worldMatrix.getValue());
            tempMatrix.toRotationMatrix(geoStruct.worldNormalMatrix.getValueForUpdate());
            geoStruct.worldNormalMatrix.getValueForUpdate().invertLocal();
            geoStruct.worldNormalMatrix.getValueForUpdate().transposeLocal();

            //WorldViewProjectionMatrix
            geoStruct.worldViewProjMatrix.getValueForUpdate().set(camStruct.viewProjectionMatrix.getValue());
            geoStruct.worldViewProjMatrix.getValueForUpdate().multLocal(geoStruct.worldMatrix.getValue());
            
            //WorldMatrixInverse
            geoStruct.worldMatrixInv.getValueForUpdate().set(geoStruct.worldMatrix.getValue());
            geoStruct.worldMatrixInv.getValueForUpdate().invertLocal();

            //WorldMatrixInverseTranspose
            geoStruct.worldMatrix.getValue().toRotationMatrix(geoStruct.worldMatrixInvTrsp.getValueForUpdate());
            geoStruct.worldMatrixInvTrsp.getValueForUpdate().invertLocal().transposeLocal();

            //WorldViewMatrixInverse
            geoStruct.worldViewMatrixInv.getValueForUpdate().set(camStruct.viewMatrix.getValue());
            geoStruct.worldViewMatrixInv.getValueForUpdate().multLocal(geoStruct.worldMatrix.getValue());
            geoStruct.worldViewMatrixInv.getValueForUpdate().invertLocal();
            

            //NormalMatrixInverse:
            tempMatrix.set(camStruct.viewMatrix.getValue());
            tempMatrix.multLocal(geoStruct.worldMatrix.getValue());
            tempMatrix.toRotationMatrix(geoStruct.normalMatrixInv.getValueForUpdate());
            geoStruct.normalMatrixInv.getValueForUpdate().invertLocal();
            geoStruct.normalMatrixInv.getValueForUpdate().transposeLocal();
            geoStruct.normalMatrixInv.getValueForUpdate().invertLocal();

            //WorldViewProjectionMatrixInverse:
            geoStruct.worldViewProjMatrixInv.getValueForUpdate().set(camStruct.viewProjectionMatrix.getValue());
            geoStruct.worldViewProjMatrixInv.getValueForUpdate().multLocal(geoStruct.worldMatrix.getValue());
            geoStruct.worldViewProjMatrixInv.getValueForUpdate().invertLocal();

            geoState.clearStateUpdateNeeded();
        }
        return geoState;
    }

    public static CameraState updateAndGet(Camera cam){
        CameraState camState=cam.getState(WorldParams.class, CameraState::new);
        CameraStruct camStruct=camState.camera.getValueForUpdate();
        if(camState.isStateUpdateNeeded()){
            int width = cam.getWidth();
            int height = cam.getHeight();
            
            // Resolution inverse
            camStruct.resolutionInverse.getValueForUpdate().set(1f / width, 1f / height);

            // Resolution
            camStruct.resolution.getValueForUpdate().set(width, height);

            // ViewMatrix
            camStruct.viewMatrix.getValueForUpdate().set(cam.getViewMatrix());

            // ProjectionMatrix
            camStruct.projectionMatrix.getValueForUpdate().set(cam.getProjectionMatrix());

             // ViewProjectionMatrix
             camStruct.viewProjectionMatrix.getValueForUpdate().set(cam.getViewProjectionMatrix());

            // ViewProjectionMatrixInverse
            camStruct.viewProjectionMatrixInverse.getValueForUpdate().set(  camStruct.viewProjectionMatrix.getValue());
            camStruct.viewProjectionMatrixInverse.getValueForUpdate().invertLocal();

            // ViewMatrixInverse
            camStruct.viewMatrixInverse.getValueForUpdate().set(   camStruct.viewMatrix.getValue());
            camStruct.viewMatrixInverse.getValueForUpdate().invertLocal();
            
            // ProjectionMatrixInverse
            camStruct.projectionMatrixInverse.getValueForUpdate().set(   camStruct.projectionMatrix.getValue());
            camStruct.projectionMatrixInverse.getValueForUpdate().invertLocal();


            // ViewPort
            int viewX      = (int) (cam.getViewPortLeft() * cam.getWidth());
            int viewY      = (int) (cam.getViewPortBottom() * cam.getHeight());
            int viewX2 = (int) (cam.getViewPortRight() * cam.getWidth());
            int viewY2 = (int) (cam.getViewPortTop() * cam.getHeight());
            int viewWidth  = viewX2 - viewX;
            int viewHeight = viewY2 - viewY;
            camStruct.viewPort.getValueForUpdate().set(  viewX, viewY, viewWidth, viewHeight);
     
     
            // Aspect
            camStruct.aspect.setValue( ((float) viewWidth) / viewHeight);
        
            //CameraPosition
            camStruct.cameraPosition.getValueForUpdate().set(cam.getLocation());

            //CameraDirection
            camStruct.cameraDirection.getValueForUpdate().set(cam.getDirection());

            //CameraLeft
            camStruct.cameraLeft.getValueForUpdate().set(cam.getLeft());

            //CameraUp
            camStruct.cameraUp.getValueForUpdate().set(cam.getUp());
            

            // FrustumNearFar
            camStruct.frustumNearFar.getValueForUpdate().set(cam.getFrustumNear(),cam.getFrustumFar());

            camState.clearStateUpdateNeeded();
        }
        return camState;

    }
    
}