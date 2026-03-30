package game.screen;

import game.Fonts;
import utils.DrawUtil;
import utils.StringAlignment;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadingScreen{
    private DrawUtil drawUtil;
    private AtomicInteger loading = new AtomicInteger(0);
    private String text = "";
    private int total;
    public LoadingScreen (DrawUtil drawUtil, int total){
        this.drawUtil = drawUtil;
        this.total = total;
    }
    public void increment(){
        loading.incrementAndGet();
    }
    public synchronized void addText(String text){
        this.text = text + "\n" + this.text;
    }
    public synchronized String getText(){
        return text;
    }
    public void draw(){
        drawUtil.clearCanvas();
        drawUtil.setColor(0, 0, 0);
        drawUtil.fillRect(0, 0, 1920, 1080);
        drawUtil.setColor(255, 255, 255);
        drawUtil.strokeRect(200, 500, 1520, 80);
        drawUtil.fillRect(200, 500, ((double) loading.get() / total)*1520, 80);
        drawUtil.drawString(960, 600, text, 20, Fonts.DEFAULT, StringAlignment.TOP_MIDDLE);
    }
}
