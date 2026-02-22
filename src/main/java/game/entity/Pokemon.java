/*
package main.game.entity;

import main.game.Viewport;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Pokemon extends Entity{
    public EntityDirection entityDirection = EntityDirection.NORTH;
    private int animationState = 0;
    private double timeSinceLastAnimationChange = 0;
    private double animationSpeed = 0.5;
    private ArrayList<BufferedImage> front = new ArrayList<>();
    private ArrayList<BufferedImage> back = new ArrayList<>();
    private ArrayList<BufferedImage> right = new ArrayList<>();
    private ArrayList<BufferedImage> left = new ArrayList<>();
    public String pokemonName;
    public String pokemonType;
    public float hp;
    public float maxHp;
    public float atk;
    public float maxAtk;
    public float spAtk;
    public float maxSpAtk;
    public float def;
    public float maxDef;
    public float spDef;
    public float maxSpDef;
    public float speed;
    public float maxSpeed;
    public float stamina;
    public float maxStamina;
    String[] moves;
    String[] moveSet;
    public int getTypeEffectiveness(String atkType, String defType) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("C:\\Users\\ryanl\\IdeaProjects\\javaPokemonGame\\res\\pokemon\\typeEffectiveness.json"));
        JSONObject jsonObject = (JSONObject) object;
        JSONObject userObject = (JSONObject) jsonObject.get(atkType);
        return (Integer) userObject.get(defType);
    }
    public int getMoveData(String move, String data) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object object = parser.parse(new FileReader("C:\\Users\\ryanl\\IdeaProjects\\javaPokemonGame\\res\\pokemon\\moveStats.json"));
        JSONObject jsonObject = (JSONObject) object;
        JSONObject userObject = (JSONObject) jsonObject.get(move);
        return (Integer) userObject.get(data);
    }
    public float getDamage(String move, Pokemon pokemon) {

        return 0;
    }
    public Pokemon(String name, double x, double y, int exp) {
        this.x = x;
        this.y = y;
        try {
            try {

//                JSONParser parser = new JSONParser();
//                Object object = parser.parse(new FileReader("C:\\Users\\ryanl\\IdeaProjects\\javaPokemonGame\\res\\pokemon\\pokemonStats.json"));
//                JSONObject jsonObject = (JSONObject) object;

                JSONParser parser = new JSONParser();
                Object object = parser.parse(new FileReader("C:\\Users\\ryanl\\IdeaProjects\\javaPokemonGame\\res\\pokemon\\pokemonStats.json"));
                JSONObject jsonObject = (JSONObject) object;
                JSONObject jsonObjectName = (JSONObject) jsonObject.get(name);
                pokemonName = (String)jsonObjectName.get("pokemonName");
                pokemonType = (String)jsonObjectName.get("pokemonType");
                maxHp = Float.parseFloat((String)jsonObjectName.get("maxHp"));
                hp = Float.parseFloat((String)jsonObjectName.get("hp"));
                maxAtk = Float.parseFloat((String)jsonObjectName.get("maxAtk"));
                atk = Float.parseFloat((String)jsonObjectName.get("atk"));
                maxSpAtk = Float.parseFloat((String)jsonObjectName.get("maxSpAtk"));
                spAtk = Float.parseFloat((String)jsonObjectName.get("spAtk"));
                maxDef = Float.parseFloat((String)jsonObjectName.get("maxDef"));
                def = Float.parseFloat((String)jsonObjectName.get("def"));
                maxSpDef = Float.parseFloat((String)jsonObjectName.get("maxSpDef"));
                spDef = Float.parseFloat((String)jsonObjectName.get("spDef"));
                maxSpeed = Float.parseFloat((String)jsonObjectName.get("maxSpeed"));
                speed = Float.parseFloat((String)jsonObjectName.get("speed"));
                maxStamina = Float.parseFloat((String)jsonObjectName.get("maxStamina"));
                stamina = Float.parseFloat((String)jsonObjectName.get("stamina"));
                String location = (String)jsonObjectName.get("path");
                File folder = new File(location);
                File[] listOfFiles = folder.listFiles();
                File frontFolder = new File(location + "\\front");
                File[] filesInFrontFolder = frontFolder.listFiles();
                File backFolder = new File(location + "\\back");
                File[] filesInBackFolder = backFolder.listFiles();
                File rightFolder = new File(location + "\\right");
                File[] filesInRightFolder = rightFolder.listFiles();
                File leftFolder = new File(location + "\\left");
                File[] filesInLeftFolder = leftFolder.listFiles();
                if (filesInFrontFolder != null && filesInBackFolder != null && filesInRightFolder != null && filesInLeftFolder != null) {
                    for (File file : filesInFrontFolder) {
                        front.add(ImageIO.read(new File(file.getAbsolutePath())));
                    }
                    for (File file : filesInBackFolder) {
                        back.add(ImageIO.read(new File(file.getAbsolutePath())));
                    }
                    for (File file : filesInRightFolder) {
                        right.add(ImageIO.read(new File(file.getAbsolutePath())));
                    }
                    for (File file : filesInLeftFolder) {
                        left.add(ImageIO.read(new File(file.getAbsolutePath())));
                    }
                } else {
                    System.out.println("error");
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void updateOnFrame(double timePassed) {
        timeSinceLastAnimationChange += timePassed;
        if (animationSpeed < timeSinceLastAnimationChange) {
            animationState = (animationState + 1) % front.size();
        }
        timeSinceLastAnimationChange -= animationSpeed;
    }
    @Override
    public void draw(Graphics2D g2){
        double scale = Viewport.viewport.getScale();
        double viewportX = Viewport.viewport.getX();
        double viewportY = Viewport.viewport.getY();
        switch (entityDirection) {
            case EntityDirection.NORTH:
                g2.drawImage(back.get(animationState), (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.EAST:
                g2.drawImage(right.get(animationState), (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.SOUTH:
                g2.drawImage(front.get(animationState), (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
            case EntityDirection.WEST:
                g2.drawImage(left.get(animationState), (int)((x-viewportX)*scale), (int)((y-viewportY)*scale), (int)(32*scale), (int)(32*scale), null);
                break;
        }
    }
}

*/

