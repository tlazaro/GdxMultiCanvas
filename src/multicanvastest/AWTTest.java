package multicanvastest;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;
import static multicanvastest.CustomNativeLoader.*;

/**
 * <p> Tests AWTGLCanvas functionality <p>
 *
 * @version $Revision$
 * @author $Author$ $Id$
 */
public class AWTTest extends Frame {

    /**
     * AWT GL canvas
     */
    private AWTGLCanvas canvas0, canvas1, canvas2;
    private volatile float angle;

    /**
     * C'tor
     */
    public AWTTest() throws LWJGLException {
        setTitle("LWJGL AWT Canvas Test");
        setSize(640, 320);
        setLayout(new GridLayout(1, 2));
        add(canvas0 = new AWTGLCanvas() {

            int current_height;
            int current_width;

            public void paintGL() {
                try {
                    if (getWidth() != current_width || getHeight() != current_height) {
                        current_width = getWidth();
                        current_height = getHeight();
                        glViewport(0, 0, current_width, current_height);
                    }
                    glViewport(0, 0, getWidth(), getHeight());
                    glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
                    glClear(GL_COLOR_BUFFER_BIT);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    gluOrtho2D(0.0f, (float) getWidth(), 0.0f, (float) getHeight());
                    glMatrixMode(GL_MODELVIEW);
                    glPushMatrix();
                    glColor3f(1f, 1f, 0f);
                    glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
                    glRotatef(angle, 0f, 0f, 1.0f);
                    glRectf(-50.0f, -50.0f, 50.0f, 50.0f);
                    glPopMatrix();
                    swapBuffers();
                    repaint();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        add(canvas1 = new AWTGLCanvas() {

            int current_height;
            int current_width;

            public void paintGL() {
                try {
                    angle += 0.1f;
                    if (getWidth() != current_width || getHeight() != current_height) {
                        current_width = getWidth();
                        current_height = getHeight();
                        glViewport(0, 0, current_width, current_height);
                    }
                    glViewport(0, 0, getWidth(), getHeight());
                    glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
                    glClear(GL_COLOR_BUFFER_BIT);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    gluOrtho2D(0.0f, (float) getWidth(), 0.0f, (float) getHeight());
                    glMatrixMode(GL_MODELVIEW);
                    glPushMatrix();
                    glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
                    glRotatef(2 * angle, 0f, 0f, -1.0f);
                    glRectf(-100.0f, -100.0f, 100.0f, 100.0f);
                    glPopMatrix();
                    swapBuffers();
                    repaint();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        add(canvas2 = new AWTGLCanvas() {

            int current_height;
            int current_width;

            public void paintGL() {
                try {
                    if (getWidth() != current_width || getHeight() != current_height) {
                        current_width = getWidth();
                        current_height = getHeight();
                        glViewport(0, 0, current_width, current_height);
                    }
                    glViewport(0, 0, getWidth(), getHeight());
                    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
                    glClear(GL_COLOR_BUFFER_BIT);
                    glMatrixMode(GL_PROJECTION);
                    glLoadIdentity();
                    gluOrtho2D(0.0f, (float) getWidth(), 0.0f, (float) getHeight());
                    glMatrixMode(GL_MODELVIEW);
                    glPushMatrix();
                    glTranslatef(getWidth() / 2.0f, getHeight() / 2.0f, 0.0f);
                    glRotatef(2 * angle, 0f, 0f, -1.0f);
                    glRectf(-75.0f, -75.0f, 75.0f, 75.0f);
                    glPopMatrix();
                    swapBuffers();
                    repaint();
                } catch (LWJGLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        setResizable(true);
        setVisible(true);
    }

    private static void loadNatives() {
        if (isWindows) {
            extractLibrary("OpenAL32.dll", "OpenAL64.dll");
            extractLibrary("lwjgl.dll", "lwjgl64.dll");
        } else if (isMac) {
            extractLibrary("openal.dylib", "openal.dylib");
            extractLibrary("liblwjgl.jnilib", "liblwjgl.jnilib");
        } else if (isLinux) {
            extractLibrary("liblwjgl.so", "liblwjgl64.so");
            extractLibrary("libopenal.so", "libopenal64.so");
        }

        System.setProperty("org.lwjgl.librarypath", nativesDir.getAbsolutePath());
    }

    public static void main(String[] args) throws LWJGLException {
        loadNatives();
        new AWTTest();
    }
}
