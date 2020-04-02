package com.jme3.rendering.pipeline;

import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;
import com.jme3.system.Timer;
import com.jme3.texture.Texture;
import com.jme3.util.Function.NoArgFunction;

/**
 * MaterialUtils
 */
public class WorldParamsUtil {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(WorldParamsUtil.class.getName());

    public static enum WorldParam {
        Resolution, ResolutionInverse, Time, IntTime, DeltaTime, Tpf, FrustumNearFar

    }

    private static <T> T getOrCreate(PipelinePass pass, String key, Class<T> type, NoArgFunction<?> constructor) {
        Object v = pass.getDefaultInput(key);
        if (v != null && type.isAssignableFrom(v.getClass())) {
            return (T) v;
        } else {
            v = constructor.eval();
            pass.useDefaultInput(key, v);
            return (T) v;
        }
    }

    private static void set(PipelinePass pass, String key, Object v) {
        pass.useDefaultInput(key, v);
    }

    public static void updateFrustumNearFar(PipelinePass pass, Camera cam, String FrustumNearFar) {
        Vector2f frustumNearFar = getOrCreate(pass, FrustumNearFar, Vector2f.class, Vector2f::new);
        frustumNearFar.set(cam.getFrustumNear(), cam.getFrustumFar());
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update frustum for pass {0} to {1}", new Object[] { pass.getName(), frustumNearFar });
        }
    }

    public static void unsetFrustumNearFar(PipelinePass pass, String FrustumNearFar) {
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Unset frustum for pass {0} ", new Object[] { pass.getName() });
        }
        set(pass, FrustumNearFar, null);
    }

    public static void updateResolution(PipelinePass pass, Texture tx, String Resolution, String ResolutionInverse) {
        int width = tx.getImage().getWidth();
        int height = tx.getImage().getHeight();
        updateResolution(pass, width, height, Resolution, ResolutionInverse);
    }

    public static void updateResolution(PipelinePass pass, Camera cam, String Resolution, String ResolutionInverse) {
        int width = cam.getWidth();
        int height = cam.getHeight();
        updateResolution(pass, width, height, Resolution, ResolutionInverse);
    }

    public static void updateResolution(PipelinePass pass, int width, int height, String Resolution, String ResolutionInverse) {
        Vector2f resolution = getOrCreate(pass, Resolution, Vector2f.class, Vector2f::new);
        Vector2f resolutionInverse = getOrCreate(pass, ResolutionInverse, Vector2f.class, Vector2f::new);
        resolutionInverse.set(1f / width, 1f / height);
        resolution.set(width, height);
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update resolution for pass {0} to {1}x{2}", new Object[] { pass.getName(), width, height });
        }
    }

    public static void unsetResolution(PipelinePass pass, String Resolution, String ResolutionInverse) {
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Unset resolution for pass {0} ", new Object[] { pass.getName() });
        }
        set(pass, Resolution, null);
        set(pass, ResolutionInverse, null);

    }

    public static void unsetTime(PipelinePass pass, String Time, String DeltaTime, String IntTime, String Tpf) {
        set(pass, Time, null);
        set(pass, DeltaTime, null);
        set(pass, IntTime, null);
        set(pass, Tpf, null);
    }

    public static void updateTime(PipelinePass pass, Timer timer, float speed, String Time, String DeltaTime, String IntTime, String Tpf) {
        float tpf = timer.getTimePerFrame() * speed;
        set(pass, Tpf, tpf);
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update tpf for pass {0} to {1}", new Object[] { pass.getName(), tpf });
        }
        float time = timer.getTimeInSeconds() * speed;
        set(pass, Time, time);
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update time for pass {0} to {1}", new Object[] { pass.getName(), time });
        }
        long ticksXsec = timer.getResolution();
        long tickTime = (long) (timer.getTime() * speed);

        long asec = 3600;
        long tickDeltaA = asec * ticksXsec;
        float timeA = (float) (((double) tickTime) / tickDeltaA);
        timeA -= (float) Math.floor(timeA);
        float timeAsec = timeA * asec;
        Vector2f deltaTime = getOrCreate(pass, DeltaTime, Vector2f.class, Vector2f::new);
        deltaTime.set(timeA, timeAsec);
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update delta Time for pass {0} to {1}", new Object[] { pass.getName(), deltaTime });
        }
        long msTime = tickTime / (ticksXsec / 1000l);
        if (msTime >= Integer.MAX_VALUE) msTime %= Integer.MAX_VALUE; // for hardcore gamers...
        set(pass, IntTime, (int) msTime);
        if (logger.isLoggable(java.util.logging.Level.FINER)) {
            logger.log(java.util.logging.Level.FINER, "Update int time for pass {0} to {1}", new Object[] { pass.getName(), (int) msTime });
        }
    }
}