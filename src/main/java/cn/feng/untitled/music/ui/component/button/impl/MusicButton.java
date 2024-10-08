package cn.feng.untitled.music.ui.component.button.impl;

import cn.feng.untitled.music.api.base.Music;
import cn.feng.untitled.music.api.base.PlayList;
import cn.feng.untitled.music.thread.ChangeMusicThread;
import cn.feng.untitled.music.ui.ThemeColor;
import cn.feng.untitled.music.ui.component.button.Button;
import cn.feng.untitled.ui.font.nano.NanoFontLoader;
import cn.feng.untitled.ui.font.nano.NanoFontRenderer;
import cn.feng.untitled.util.render.nano.NanoUtil;
import cn.feng.untitled.util.misc.TimerUtil;
import cn.feng.untitled.util.render.ColorUtil;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class MusicButton extends Button {
    private final Music music;
    private final PlayList playList;
    private ChangeMusicThread thread;

    public MusicButton(Music music, PlayList playList) {
        this.music = music;
        this.playList = playList;
        height = 30f;
    }

    @Override
    public void render() {
        if (hovering) {
            NanoUtil.drawRect(posX, posY, width - 1f, height, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.1f));
        }

        float textX = posX + 7f;
        float textY = posY + 7f;

        NanoFontLoader.greyCliff.bold().drawString((playList.getMusicList().indexOf(music) + 1) + "", textX, textY + 4f, 13f, ThemeColor.greyColor);

        if (music.getCoverTexture() == 0) {
            music.setCoverTexture(NanoUtil.genImageId(music.getCoverFile()));
        }

        NanoUtil.drawImageRect(music.getCoverTexture(), textX + 10f, textY, 16f, 16f);

        // 歌曲信息
        NanoFontRenderer font = NanoFontLoader.misans;
        String name = font.trimStringToWidth(music.getName(), 120f, 15f, false, true);
        font.drawString(name, textX + 30f, textY, 15f, music.isFree() ? Color.WHITE : ThemeColor.redColor);
        String artist = font.trimStringToWidth(music.getArtist(), 100f, 15f, false, true);
        font.drawString(artist, textX + 30f, textY + 9f, 12f, ThemeColor.greyColor);
        String album = font.trimStringToWidth(music.getAlbum(), 80f, 15f, false, true);
        font.drawString(album, textX + 160f, textY + 5f, 15f, ThemeColor.greyColor);
        long totalSeconds = music.getDuration() / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String duration = String.format("%d:%02d", minutes, seconds);
        font.drawString(duration, textX + 280f, textY + 5f, 15f, ThemeColor.greyColor);

        // 两个分割线
        NanoUtil.drawRect(textX + 150f, textY, 1f, 20f, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.2f));
        NanoUtil.drawRect(textX + 270f, textY, 1f, 20f, ColorUtil.applyOpacity(ThemeColor.greyColor, 0.2f));
    }

    private final TimerUtil clickTimer = new TimerUtil();

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        // 双击
        if (hovering && button == 0) {
            // 防止连击触发多次请求
            if (!clickTimer.hasTimeElapsed(1500) && (thread == null || !thread.isAlive())) {
                thread = new ChangeMusicThread(music, playList);
                thread.start();
            }
            clickTimer.reset();
        }
    }
}
