package utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import javafx.scene.paint.Color;

public class Colors {
    private static Color[][][][] colors = new Color[52][52][52][11];
    public static void init(){
        for (int r = 0; r < 52; r++) {
            Color[][][] green = new Color[52][52][11];
            for (int g = 0; g < 52; g++) {
                Color[][] blue = new Color[52][11];
                for (int b = 0; b < 52; b++) {
                    Color[] alpha = new Color[11];
                    for (int a = 0; a <= 10; a++){
                        alpha[a] = Color.rgb(r*5, g*5, b*5, a/10d);
                    }
                    blue[b] = alpha;
                }
                green[g] = blue;
            }
            colors[r] = green;
        }
//        for (Color color : colors[0][0][0]){
//            System.out.println(color.getOpacity());
//        }
    }
    public static Color getColor(int r, int g, int b){
        r = (r/5);
        g = (g/5);
        b = (b/5);
        return colors[r][g][b][10];
    }
    public static Color getColor(int r, int g, int b, double a){
        r = (r/5);
        g = (g/5);
        b = (b/5);
        return colors[r][g][b][(int)(a*10)];
    }
    public static Color fromHex(int hex) {
        int r = (hex >> 24) & 0xFF;
        int g = (hex >> 16) & 0xFF;
        int b = (hex >> 8) & 0xFF;
        double a = (hex & 0xFF) / 255.0d;
        return getColor(r, g, b, a);
    }
}
