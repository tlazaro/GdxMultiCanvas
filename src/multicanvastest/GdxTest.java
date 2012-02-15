package multicanvastest;

import com.badlogic.gdx.backends.lwjgl.LwjglMultiCanvas;
import com.badlogic.gdx.graphics.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

/**
 * <p> Tests Gdx AWTGLCanvas functionality <p>
 *
 * @author tomas
 */
public class GdxTest extends Frame {

    /**
     * AWT GL canvas
     */
    private AWTGLCanvas canvas0, canvas1, canvas2;

    /**
     * C'tor
     */
    public GdxTest() throws LWJGLException {
        LwjglMultiCanvas app = new LwjglMultiCanvas(new BasicApplication(Color.CYAN), false);
        
        setTitle("GDX LWJGL AWT Canvas Test");
        setSize(640, 320);
        setLayout(new GridLayout(1, 3));
        add(canvas0 = app.getCanvases().get(0));
        add(canvas1 = app.addListener(new BasicApplication(Color.MAGENTA)));
        add(canvas2 = app.addListener(new BasicApplication(Color.YELLOW)));
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setResizable(true);
        setVisible(true);
    }

    public static void main(String[] args) throws LWJGLException {
        new GdxTest();
    }
}
