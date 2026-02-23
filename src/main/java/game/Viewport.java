package game;


public class Viewport {
    private static double viewportX = 0;
    private static double viewportY = 0;
    private static final double viewportWidth = 1920;
    private static final double viewportHeight = 1080;
    private static double scale;
    private static double xOffset;
    private static double yOffset;
//    public static final Viewport viewport = new Viewport();


    public static void calculateViewport(double windowWidth, double windowHeight){
        double windowWidthScale = windowWidth / 1920d;
        double windowHeightScale = windowHeight / 1080d;
        scale = StrictMath.min(windowWidthScale, windowHeightScale);
        xOffset = ((windowWidth - (viewportWidth * scale)) / 2d);
        yOffset = ((windowHeight - (viewportHeight * scale)) / 2d);
//        System.out.print(windowWidth + " ");
//        System.out.print(windowHeight + " ");
//        System.out.print(yOffset + " ");
//        System.out.print(scale + "\n");
    }

    public static double getX() {
        return viewportX;
    }

    public static void setX(double viewportX) {
        Viewport.viewportX = viewportX;
    }

    public static double getY() {
        return viewportY;
    }

    public static void setY(double viewportY) {
        Viewport.viewportY = viewportY;
    }

    public static double getWidth() {
        return viewportWidth;
    }

    public static double getHeight() {
        return viewportHeight;
    }

    public static double getScale() {
        return scale;
    }

    public static double getXOffset() {
        return xOffset;
    }

    public static double getYOffset() {
        return yOffset;
    }

//    @Override
//    public void componentHidden(ComponentEvent e) {}
//
//    @Override
//    public void componentMoved(ComponentEvent e) {}
//
//    @Override
//    public void componentResized(ComponentEvent e) {
//        calculateViewport(e.getComponent().getBounds().getWidth(), e.getComponent().getBounds().getHeight());
//    }
//
//    @Override
//    public void componentShown(ComponentEvent e) {
//        calculateViewport(e.getComponent().getBounds().getWidth(), e.getComponent().getBounds().getHeight());
//    }
}
