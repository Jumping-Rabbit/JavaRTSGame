package tile;

import utils.DrawUtil;
import game.screen.Game;
import game.Viewport;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TileManager {
    Game game;
    DrawUtil drawUtil;
    Tile[] tile;
    int [][] currentMapNum;
    int mapWidth;
    int mapHeight;
    BufferedImage[] tiles;


    public TileManager(DrawUtil drawUtil, File file) {
//        this.drawUtil = drawUtil;
//        //TODO: declare and populate tiles here from file
//        //TODO: fix not permission when new scanner of file
//
//
//        main.tile = new Tile[tiles.length]; // amount of diffrent tiles
//        getTileImage();
//        setCurrentMap(file);
    }

    public void getTileImage() {
        for (int x = 0; x < tiles.length; x++) {
            tile[x] = new Tile();
            tile[x].image = tiles[x];
        }
    }

    public void setCurrentMap(File file) {
        currentMapNum = new int[mapHeight][mapWidth];
        try {
            Scanner mapReader = new Scanner(file);
            mapWidth = Integer.parseInt(mapReader.nextLine());
            mapHeight = Integer.parseInt(mapReader.nextLine());
            currentMapNum = new int[mapHeight][mapWidth];
            int linecounter = 0;
            while (mapReader.hasNextLine()) {
                String data = mapReader.nextLine();
                String[] numbers = data.split(" ");
                int[] line = new int[numbers.length];
                for (int x = 0; x < numbers.length; x++) {
                    line[x] = Integer.parseInt(numbers[x]);
                }
                currentMapNum[linecounter] = line;
                linecounter++;
            }
            mapReader.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        double scale = Viewport.getScale();
        double viewportX = Viewport.getX();
        double viewportY = Viewport.getY();
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                g2.drawImage(tile[currentMapNum[y][x]].image, (int)(((x*32) - viewportX)*scale), (int)(((y*32) - viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
            }
        }
    }
}
