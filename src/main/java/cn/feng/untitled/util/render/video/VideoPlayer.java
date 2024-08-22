package cn.feng.untitled.util.render.video;

import cn.feng.untitled.util.MinecraftInstance;
import net.minecraft.util.ResourceLocation;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Video-player in Minecraft.
 *
 * @version 1.0.0
 *
 * @author LingYuWeiGuang
 * @author HyperTap
 * @author ChengFeng
 */
public class VideoPlayer extends MinecraftInstance {
    private FFmpegFrameGrabber frameGrabber;
    private TextureBinder textureBinder;

    private int frameLength;
    private int count; // frames counter

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture;

    public final AtomicBoolean paused = new AtomicBoolean(false);
    private final AtomicBoolean stopped = new AtomicBoolean(false);

    private static final Logger logger = Logger.getLogger("VideoPlayer");

    /**
     * Start video-player object.
     *
     * @param resource As you can see.
     */
    public void init(ResourceLocation resource) throws FFmpegFrameGrabber.Exception {
        File videoTemp;

        try {
            videoTemp = File.createTempFile("video_temp", ".mp4");
            InputStream inputStream = mc.getResourceManager().getResource(resource).getInputStream();
            Files.copy(inputStream, videoTemp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            videoTemp.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        frameGrabber = FFmpegFrameGrabber.createDefault(videoTemp);
        frameGrabber.setPixelFormat(avutil.AV_PIX_FMT_RGB24);
        avutil.av_log_set_level(avutil.AV_LOG_QUIET); // Log level -> quiet

        textureBinder = new TextureBinder();

        count = 0;

        stopped.set(false);
        frameGrabber.start();
        frameLength = frameGrabber.getLengthInFrames();

        double frameRate = frameGrabber.getFrameRate();

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduledFuture = scheduler.scheduleAtFixedRate(this::doGetBuffer, 0, (long) (1000 / frameRate), TimeUnit.MILLISECONDS);
    }

    private void doGetBuffer() {
        if (paused.get() || stopped.get()) return;

        try {
            if (count < frameLength - 1) {
                Frame frame = frameGrabber.grabImage();
                if (frame != null) {
                    if (frame.image != null) {
                        textureBinder.setBuffer((ByteBuffer) frame.image[0], frame.imageWidth, frame.imageHeight);

                        count++;
                    }
                }
            } else {
                count = 0;
                frameGrabber.setFrameNumber(0);
            }
        } catch (FFmpegFrameGrabber.Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Binding texture and play video frame.
     *
     * @param left rect left
     * @param top rect top
     * @param right rect right
     * @param bottom rect bottom
     */
    public void render(int left, int top, int right, int bottom) throws FrameGrabber.Exception {
        if (stopped.get() || paused.get()) return;

        textureBinder.bindTexture();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        // draw Quad
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(left, bottom, 0);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(right, bottom, 0);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(right, top, 0);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(left, top, 0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Stop play video frame.
     */
    public void stop() throws FFmpegFrameGrabber.Exception {
        if (stopped.get()) return;

        stopped.set(true);
        paused.set(false);

        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }

        textureBinder = null;

        count = 0;

        if (frameGrabber != null) {
            frameGrabber.stop();
            frameGrabber.release();
            frameGrabber = null;
        }
    }
}
