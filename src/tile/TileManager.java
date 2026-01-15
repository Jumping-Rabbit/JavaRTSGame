package tile;

import main.game.Game;
import main.game.Viewport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class TileManager {
    public enum Maps {
        OVERWORLD
    }
    Maps currentMap;
    Game gd;
    Tile[] tile;
    int [][] currentMapNum;
    int mapWidth;
    int mapHeight;
    String[] tilePaths = {

    };

    public TileManager(File file) {
        tile = new Tile[tilePaths.length]; // amount of diffrent tiles
        getTileImage();
        setCurrentMap(Maps.OVERWORLD);
    }

    public void getTileImage() {
        for (int x = 0; x < tilePaths.length; x++) {
            try {
                tile[x] = new Tile();
                tile[x].image = ImageIO.read(new File(tilePaths[x]));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setCurrentMap(Maps maps) {
        currentMap = maps;
        File map = null;
        currentMapNum = new int[mapHeight][mapWidth];
        map = new File("C:\\Users\\ryanl\\IdeaProjects\\javaPokemonGame\\res\\map\\overworld");
        try {
            Scanner mapReader = new Scanner(map);
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
        double scale = Viewport.viewport.getScale();
        double viewportX = Viewport.viewport.getX();
        double viewportY = Viewport.viewport.getY();
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                g2.drawImage(tile[currentMapNum[y][x]].image, (int)(((x*32) - viewportX)*scale), (int)(((y*32) - viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
            }
        }
    }
}
