package game.screen;

import game.Fonts;
import utils.DrawUtil;
import utils.StringAlignment;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingScreen {
    private AtomicInteger loading = new AtomicInteger(0);
    private String text = "";
    private int total;

    public LoadingScreen(int total) {
        this.total = total;
    }

    public void increment() {
        loading.incrementAndGet();
    }

    public synchronized void addText(String text) {
        this.text = text + "\n" + this.text;
    }

    public synchronized String getText() {
        return text;
    }

    public void draw() {
        DrawUtil.clearCanvas();
        DrawUtil.fillRect(0, 0, 1920, 1080, 0x000000FF);
        DrawUtil.strokeRect(200, 500, 1520, 80, 0xFFFFFFFF, 2);
        DrawUtil.fillRect(200, 500, ((double) loading.get() / total) * 1520, 80, 0xFFFFFFFF);
        DrawUtil.fillText(text, 960, 600, Fonts.DEFAULT, 20, StringAlignment.TOP_MIDDLE, 0xFFFFFFFF);
    }
}
