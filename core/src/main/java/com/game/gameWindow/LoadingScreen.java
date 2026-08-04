package com.game.gameWindow;

import com.badlogic.gdx.Screen;
import com.game.Fonts;
import com.game.utils.DrawUtil;
import com.game.utils.StringAlignment;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingScreen implements Screen {
    private final AtomicInteger loading = new AtomicInteger(0);
    private String text = "";
    private final int total;

    public LoadingScreen(int total) {
        this.total = total;
    }

    public void increment() {
        loading.incrementAndGet();
    }

    public synchronized void addText(String text) {
        this.text = text + "\n" + this.text;
//        System.out.println(text);
    }

    public synchronized String getText() {
        return text;
    }

    public void draw() {
        DrawUtil.clearCanvas();
        DrawUtil.fillRect(0, 0, 1920, 1080, 0x000000FF);
        DrawUtil.strokeRect(200, 880, 1520, 80, 0xFFFFFFFF, 4);
        DrawUtil.fillRect(200, 880, ((float) loading.get() / total) * 1520, 80, 0xFFFFFFFF);
        DrawUtil.fillText(text, 960, 780, Fonts.DEFAULT, 20, StringAlignment.TOP_MIDDLE, 0xFFFFFFFF);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
