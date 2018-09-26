package App;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;
import java.io.*;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

//import java.awt.*;

public class App extends Application implements NativeKeyListener, NativeMouseInputListener {
    private static final ConcurrentHashMap<String, Image> CACHE = new ConcurrentHashMap<>();
    private static double RESOLUTION_WIDTH;
    private static double RESOLUTION_HEIGHT;
    private static ImageView IMAGE_VIEW;
    private static String LEFT_KEY;
    private static String RIGHT_KEY;
    private static Scene scene;

    public static void main(String[] args) throws IOException, InterruptedException {
        //Disable jNativeHook's log
        LogManager.getLogManager().reset();
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.WARNING);
        //Register key listener
        GlobalScreen.setEventDispatcher(new JavaFxDispatchService());
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        App app = new App();
        GlobalScreen.addNativeKeyListener(app);
        GlobalScreen.addNativeMouseListener(app);
        GlobalScreen.addNativeMouseMotionListener(app);
        //compatibility of multi-display
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        App.RESOLUTION_WIDTH = gd.getDisplayMode().getWidth();
        App.RESOLUTION_HEIGHT = gd.getDisplayMode().getHeight();
        launch(args);
    }

    private static void initCache() throws IOException, InterruptedException {
        String catImagePathPerfix;
        String jarFilePath = System.getProperty("user.dir");
        String currentUser = System.getProperty("user.name");
        //Find Registry Key of osu!
        Process process = Runtime.getRuntime().exec("reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Classes\\osu!\\DefaultIcon");
        String outPut = IOUtils.toString(process.getInputStream());
        if (outPut.contains("HKEY_LOCAL_MACHINE")) {
            String osuPath = outPut.substring(outPut.indexOf("\"") + 1, outPut.indexOf("osu!.exe"));
            File osuConfigFile = new File(osuPath + "osu!." + currentUser + ".cfg");
            if (osuConfigFile.exists()) {
                Properties osuConfig = new Properties();
                try (InputStream in =
                             new FileInputStream(osuConfigFile)) {
                    osuConfig.load(in);
                } catch (IOException e) {
                    System.err.println(Instant.now() + " Exception When Reading osu! Config: " + e.getMessage() + "，Program will exit");
                    TimeUnit.SECONDS.sleep(10);
                    return;
                }
                if (osuConfig.getProperty("keyOsuLeft") != null && osuConfig.getProperty("keyOsuRight") != null) {
                    System.out.println(Instant.now()
                            + " Reading osu! config success , Left Key is: "
                            + osuConfig.getProperty("keyOsuLeft")
                            + ", Right Key is: "
                            + osuConfig.getProperty("keyOsuRight"));
                    LEFT_KEY = osuConfig.getProperty("keyOsuLeft");
                    RIGHT_KEY = osuConfig.getProperty("keyOsuRight");
                } else {
                    System.out.println(Instant.now() + " Error: Failed reading osu! configs. " +
                            "\nPlease report a issue with your osu! config find by me: "
                            + osuPath + "osu!" + currentUser + ".cfg");
                    TimeUnit.SECONDS.sleep(10);
                    return;
                }
            } else {
                System.err.println(Instant.now() + " Can not find the osu! path, please use [Repair folder permissions] in the osu! options");
            }
        } else {
            System.err.println(Instant.now() + " Cannot find the osu! path, please use [Repair folder permissions] in the osu! options");
        }

        Image KeyTapHand1 = new Image(new File(jarFilePath + "\\cat\\KeyTapHand1.png").toURI().toURL().toString(), false);
        Image KeyTapHand2 = new Image(new File(jarFilePath + "\\cat\\KeyTapHand2.png").toURI().toURL().toString(), false);

        CACHE.put("KeyTapHand2", KeyTapHand2);
        CACHE.put("KeyTapHand1", KeyTapHand1);

        process = Runtime.getRuntime().exec("reg query HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\vmulti");
        outPut = IOUtils.toString(process.getInputStream());
        if (outPut.contains("HKEY_LOCAL_MACHINE")) {
            catImagePathPerfix = "\\cat\\tablet\\";
            System.out.println(Instant.now() + "Third party tablet driver detected! loading tablet background image...");
        } else {
            process = Runtime.getRuntime().exec("reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\Wacom");
            outPut = IOUtils.toString(process.getInputStream());
            if (outPut.contains("HKEY_LOCAL_MACHINE")) {
                catImagePathPerfix = "\\cat\\tablet\\";
                System.out.println(Instant.now() + " Official Wacom tablet driver detected! loading tablet background image...");
            } else {
                catImagePathPerfix = "\\cat\\mouse\\";
            }
        }
        Image hand = new Image(new File(jarFilePath + catImagePathPerfix + "Hand.png").toURI().toURL().toString(), false);
        Image handA = new Image(new File(jarFilePath + catImagePathPerfix + "Hand A.png").toURI().toURL().toString(), false);
        Image handB = new Image(new File(jarFilePath + catImagePathPerfix + "Hand B.png").toURI().toURL().toString(), false);
        Image handC = new Image(new File(jarFilePath + catImagePathPerfix + "Hand C.png").toURI().toURL().toString(), false);
        Image handD = new Image(new File(jarFilePath + catImagePathPerfix + "Hand D.png").toURI().toURL().toString(), false);
        Image handE = new Image(new File(jarFilePath + catImagePathPerfix + "Hand E.png").toURI().toURL().toString(), false);
        Image handF = new Image(new File(jarFilePath + catImagePathPerfix + "Hand F.png").toURI().toURL().toString(), false);
        Image handG = new Image(new File(jarFilePath + catImagePathPerfix + "Hand G.png").toURI().toURL().toString(), false);
        Image handH = new Image(new File(jarFilePath + catImagePathPerfix + "Hand H.png").toURI().toURL().toString(), false);
        Image handI = new Image(new File(jarFilePath + catImagePathPerfix + "Hand I.png").toURI().toURL().toString(), false);
        CACHE.put("hand", hand);
        CACHE.put("handA", handA);
        CACHE.put("handB", handB);
        CACHE.put("handC", handC);
        CACHE.put("handD", handD);
        CACHE.put("handE", handE);
        CACHE.put("handF", handF);
        CACHE.put("handG", handG);
        CACHE.put("handH", handH);
        CACHE.put("handI", handI);
        //It seems javafx doesn't support .ico ...
        Image icon = new Image(new File(jarFilePath + "\\cat\\cat.ico").toURI().toURL().toString(), false);
        CACHE.put("icon", handI);
    }

    @Override
    @SuppressWarnings("Deprecated")
    public void start(Stage primaryStage) throws IOException, InterruptedException {
        //Create console textarea
        TextArea ta = TextAreaBuilder.create().prefWidth(800).prefHeight(400).wrapText(true).editable(false).build();
        ta.setFont(new Font(15));
        //This Console class is A class extend the OutputStream, so it can receive the System.out.println() request,IT IS NOT java.io.Console!!!
        PrintStream ps = new PrintStream(new Console(ta));
        //Redirect system console IOStream
        System.setOut(ps);
        System.setErr(ps);

        //Create window to place console textarea
        Stage newWindow = new Stage();
        newWindow.setTitle("Console");
        VBox vbox = new VBox();
        vbox.getChildren().addAll(ta);
        Scene scene2 = new Scene(vbox);
        newWindow.setScene(scene2);
        // Set position of console window, related to primary window.
        newWindow.setX(primaryStage.getX() + 200);
        newWindow.setY(primaryStage.getY() + 100);
        newWindow.show();
        // Set On Close Event
        newWindow.setOnCloseRequest(event -> System.exit(0));
        newWindow.setResizable(false);


        //Set style of main window
        primaryStage.setTitle("osu! Cat J");
        Group root = new Group();
        Scene scene = new Scene(root, 640, 640, Color.WHITE);
        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(5));
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        final ImageView imv = new ImageView();
        final HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(imv);
        gridpane.add(pictureRegion, 1, 1);
        root.getChildren().add(gridpane);
        primaryStage.setScene(scene);
        primaryStage.show();
        //set On Close event
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.setResizable(false);

        //set the ImageView and Stage to static member to control it later
        App.IMAGE_VIEW = imv;
        App.scene = scene;
        System.out.println("README: " +
                "\n - Will NOT work when any application running with admin rights window is in the foreground" +
                "\n - So do not run osu! in Admin mode;" +
                "\n - Only tested in Windows 10 and JRE 1.8;");

        System.out.println(Instant.now() + " Starting...");

        initCache();
        //set icon :3
        newWindow.getIcons().add(CACHE.get("icon"));
        primaryStage.getIcons().add(CACHE.get("icon"));
    }

    public void nativeKeyPressed(NativeKeyEvent e) {
        if (LEFT_KEY.equals(NativeKeyEvent.getKeyText(e.getKeyCode()))) {
            IMAGE_VIEW.setImage(CACHE.get("KeyTapHand1"));
        }
        if (RIGHT_KEY.equals(NativeKeyEvent.getKeyText(e.getKeyCode()))) {
            IMAGE_VIEW.setImage(CACHE.get("KeyTapHand2"));
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {
        if ("Z".equals(NativeKeyEvent.getKeyText(e.getKeyCode()))) {
            IMAGE_VIEW.setImage(null);
        }
        if ("X".equals(NativeKeyEvent.getKeyText(e.getKeyCode()))) {
            IMAGE_VIEW.setImage(null);
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
        handleMouseMoveOrDrag(nativeMouseEvent);
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
        handleMouseMoveOrDrag(nativeMouseEvent);
    }

    private void handleMouseMoveOrDrag(NativeMouseEvent nativeMouseEvent) {
        int x = nativeMouseEvent.getX();
        int y = nativeMouseEvent.getY();
        int[] coordinate = convert(x, y);

        ImagePattern pattern;
        if (x > 0 && y > 0) {
            if ((x / RESOLUTION_WIDTH) * 3D > 2D) {
                if ((y / RESOLUTION_HEIGHT) * 3D > 2D) {
                    pattern = new ImagePattern(CACHE.get("handI"));
                } else if ((y / RESOLUTION_HEIGHT) * 3D > 1D) {
                    pattern = new ImagePattern(CACHE.get("handF"));
                } else {
                    pattern = new ImagePattern(CACHE.get("handC"));
                }
            } else if ((x / RESOLUTION_WIDTH) * 3D > 1D) {
                if ((y / RESOLUTION_HEIGHT) * 3D > 2D) {
                    pattern = new ImagePattern(CACHE.get("handH"));
                } else if ((y / RESOLUTION_HEIGHT) * 3D > 1D) {
                    pattern = new ImagePattern(CACHE.get("handE"));
                } else {
                    pattern = new ImagePattern(CACHE.get("handB"));
                }
            } else {

                if ((y / RESOLUTION_HEIGHT) * 3D > 2D) {
                    pattern = new ImagePattern(CACHE.get("handG"));
                } else if ((y / RESOLUTION_HEIGHT) * 3D > 1D) {
                    pattern = new ImagePattern(CACHE.get("handD"));
                } else {
                    pattern = new ImagePattern(CACHE.get("handA"));
                }
            }
            scene.setFill(pattern);
        }
    }

    private int[] convert(int x, int y) {
        double percentWidth = (x / RESOLUTION_WIDTH);
        double percentHeight = (y / RESOLUTION_HEIGHT);
        //求出手掌的坐标
        int x1 = 195 - (int) (125d * percentWidth) + (int) (60d * percentHeight) + 20;
        int y1 = 420 - (int) (35d * percentWidth) - (int) (20 * percentHeight) - 20;
        //394 162 - 374 182
//        QuadCurve2D curve1 = new QuadCurver2D.Double(20,10,90,65,55,115);
        return new int[2];
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

}