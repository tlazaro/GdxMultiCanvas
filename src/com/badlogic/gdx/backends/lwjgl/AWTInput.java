package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Pool;
import java.awt.AWTException;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import org.lwjgl.opengl.AWTGLCanvas2;
import static java.awt.event.KeyEvent.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tomas
 */
public class AWTInput implements Input {

    private InputProcessor processor;
    final AWTGLCanvas2 canvas;
    private final java.awt.event.KeyListener keyListener;
    private final MouseListener mouseListener;
    private final MouseMotionListener mouseMotionListener;
    private int x;
    private int y;
    private int deltaX;
    private int deltaY;
    private boolean justTouched;
    // mouse codes
    public static final int MOUSE_MOVE_LEFT = 0;
    public static final int MOUSE_MOVE_RIGHT = 1;
    public static final int MOUSE_MOVE_UP = 2;
    public static final int MOUSE_MOVE_DOWN = 3;
    public static final int MOUSE_WHEEL_UP = 4;
    public static final int MOUSE_WHEEL_DOWN = 5;
    public static final int MOUSE_BUTTON_1 = 6;
    public static final int MOUSE_BUTTON_2 = 7;
    public static final int MOUSE_BUTTON_3 = 8;
    private static final int NUM_MOUSE_CODES = 9;
    // key codes are defined in java.awt.KeyEvent.
    // most of the codes (except for some rare ones like
    // "alt graph") are less than 600.
    private static final int NUM_VK_CODES = 600;
    private int[] keyActions = new int[NUM_VK_CODES];
    private int[] mouseActions = new int[NUM_MOUSE_CODES];
    long currentEventTimeStamp;

    private void pressMouse(int code) {
        mouseActions[code] += 1;
    }

    private void releaseMouse(int code) {
        if (mouseActions[code] <= 1) {
            mouseActions[code] = 0;
        } else {
            mouseActions[code] -= 1;
        }
    }

    private void resetMouse(int code) {
        mouseActions[code] = 0;
    }

    private boolean isMousePressed(int code) {
        return mouseActions[code] > 0;
    }

    private void press(int code) {
        keyActions[code] += 1;
    }

    private void release(int code) {
        if (keyActions[code] <= 1) {
            keyActions[code] = 0;
        } else {
            keyActions[code] -= 1;
        }
    }

    private void reset(int code) {
        keyActions[code] = 0;
    }

    private boolean isPressed(int code) {
        return keyActions[code] > 0;
    }

    /**
     * Gets the mouse code for the button specified in this MouseEvent.
     */
    public static int getMouseButtonCode(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                return MOUSE_BUTTON_1;
            case MouseEvent.BUTTON2:
                return MOUSE_BUTTON_2;
            case MouseEvent.BUTTON3:
                return MOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

    private TouchEvent mouseEvent(MouseEvent me) {
        TouchEvent event = usedTouchEvents.obtain();
        event.x = me.getX();
        event.y = Gdx.graphics.getHeight() - me.getY() - 1;
        event.button = toGdxButton(me.getButton());
        event.pointer = 0;
        event.timeStamp = TimeUnit.MILLISECONDS.toNanos(me.getWhen());

        return event;
    }

    public AWTInput(AWTGLCanvas2 canvas) {
        this.canvas = canvas;

        keyListener = new java.awt.event.KeyListener() {

            @Override
            public void keyTyped(KeyEvent ke) {
                AWTKeyEvent event = usedKeyEvents.obtain();
                event.keyCode = getGdxKeyCode(ke.getKeyCode());
                event.keyChar = ke.getKeyChar();
                event.type = AWTKeyEvent.KEY_TYPED;
                event.timeStamp = TimeUnit.MILLISECONDS.toNanos(ke.getWhen());
                synchronized (keyEvents) {
                    keyEvents.add(event);
                }
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                AWTKeyEvent event = usedKeyEvents.obtain();
                event.keyCode = getGdxKeyCode(ke.getKeyCode());
                event.keyChar = ke.getKeyChar();
                event.type = AWTKeyEvent.KEY_DOWN;
                event.timeStamp = TimeUnit.MILLISECONDS.toNanos(ke.getWhen());

                synchronized (keyEvents) {
                    keyEvents.add(event);
                }

                press(ke.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                AWTKeyEvent event = usedKeyEvents.obtain();
                event.keyCode = getGdxKeyCode(ke.getKeyCode());
                event.keyChar = ke.getKeyChar();
                event.type = AWTKeyEvent.KEY_UP;
                event.timeStamp = TimeUnit.MILLISECONDS.toNanos(ke.getWhen());

                synchronized (keyEvents) {
                    keyEvents.add(event);
                }

                release(ke.getKeyCode());
            }
        };

        mouseListener = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {
            }

            @Override
            public void mousePressed(MouseEvent me) {
                TouchEvent event = mouseEvent(me);

                event.type = TouchEvent.TOUCH_DOWN;
                justTouched = true;
                pressMouse(getMouseButtonCode(me));

                synchronized (touchEvents) {
                    touchEvents.add(event);
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                TouchEvent event = mouseEvent(me);

                event.type = TouchEvent.TOUCH_UP;
                releaseMouse(getMouseButtonCode(me));

                synchronized (touchEvents) {
                    touchEvents.add(event);
                }
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        };

        mouseMotionListener = new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent me) {
                TouchEvent event = mouseEvent(me);

                event.type = TouchEvent.TOUCH_DRAGGED;

                synchronized (touchEvents) {
                    touchEvents.add(event);
                }

                x = me.getX();
                y = me.getY();
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                TouchEvent event = mouseEvent(me);

                event.type = TouchEvent.TOUCH_MOVED;

                synchronized (touchEvents) {
                    touchEvents.add(event);
                }

                x = me.getX();
                y = me.getY();
            }
        };

        canvas.addKeyListener(keyListener);
        canvas.addMouseListener(mouseListener);
        canvas.addMouseMotionListener(mouseMotionListener);
    }

    @Override
    public float getAccelerometerX() {
        return 0;
    }

    @Override
    public float getAccelerometerY() {
        return 0;
    }

    @Override
    public float getAccelerometerZ() {
        return 0;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getX(int pointer) {
        if (pointer > 0) {
            return 0;
        } else {
            return getX();
        }
    }

    @Override
    public int getDeltaX() {
        return deltaX;
    }

    @Override
    public int getDeltaX(int pointer) {
        if (pointer == 0) {
            return 0;
        } else {
            return deltaX;
        }
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getY(int pointer) {
        if (pointer > 0) {
            return 0;
        } else {
            return getY();
        }
    }

    @Override
    public int getDeltaY() {
        return deltaY;
    }

    @Override
    public int getDeltaY(int pointer) {
        if (pointer == 0) {
            return 0;
        } else {
            return deltaY;
        }
    }

    @Override
    public boolean justTouched() {
        return justTouched;
    }

    @Override
    public boolean isTouched() {
        return isMousePressed(MOUSE_BUTTON_1) || isMousePressed(MOUSE_BUTTON_2) || isMousePressed(MOUSE_BUTTON_3);
    }

    @Override
    public boolean isTouched(int pointer) {
        if (pointer > 0) {
            return false;
        } else {
            return isTouched();
        }
    }

    @Override
    public boolean isButtonPressed(int button) {
        return isButtonPressed(toAWTButton(button));
    }

    @Override
    public boolean isKeyPressed(int code) {
        return isPressed(getAWTKeyCode(code));
    }

    @Override
    public void getTextInput(TextInputListener tl, String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean bln) {
    }

    @Override
    public void vibrate(int milliseconds) {
    }

    @Override
    public void vibrate(long[] pattern, int repeat) {
    }

    @Override
    public void cancelVibrate() {
    }

    @Override
    public float getAzimuth() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public void getRotationMatrix(float[] floats) {
    }

    @Override
    public long getCurrentEventTime() {
        return currentEventTimeStamp;
    }

    @Override
    public void setCatchBackKey(boolean bln) {
    }

    @Override
    public void setCatchMenuKey(boolean bln) {
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        this.processor = processor;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return processor;
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        if (peripheral == Peripheral.HardwareKeyboard) {
            return true;
        }
        return false;
    }

    @Override
    public int getRotation() {
        return 0;
    }

    @Override
    public Orientation getNativeOrientation() {
        return Orientation.Landscape;
    }

    @Override
    public void setCursorCatched(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCursorCatched() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCursorPosition(int x, int y) {
        try {
            Point pos = new Point(x, y);
            SwingUtilities.convertPointToScreen(pos, canvas);
            new java.awt.Robot().mouseMove(pos.x, pos.y);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

    private int toGdxButton(int awtButton) {
        switch (awtButton) {
            case MOUSE_BUTTON_1:
                return Buttons.LEFT;
            case MOUSE_BUTTON_2:
                return Buttons.RIGHT;
            case MOUSE_BUTTON_3:
                return Buttons.MIDDLE;
            default:
                return -1;
        }
    }

    private static int toAWTButton(int button) {
        switch (button) {
            case Buttons.LEFT:
                return MOUSE_BUTTON_1;
            case Buttons.RIGHT:
                return MOUSE_BUTTON_2;
            case Buttons.MIDDLE:
                return MOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

    public static int getAWTKeyCode(int gdxKeyCode) {
        switch (gdxKeyCode) {
            case Input.Keys.NUM_0:
                return VK_NUMPAD0;
            case Input.Keys.NUM_1:
                return VK_NUMPAD1;
            case Input.Keys.NUM_2:
                return VK_NUMPAD2;
            case Input.Keys.NUM_3:
                return VK_NUMPAD3;
            case Input.Keys.NUM_4:
                return VK_NUMPAD4;
            case Input.Keys.NUM_5:
                return VK_NUMPAD5;
            case Input.Keys.NUM_6:
                return VK_NUMPAD6;
            case Input.Keys.NUM_7:
                return VK_NUMPAD7;
            case Input.Keys.NUM_8:
                return VK_NUMPAD8;
            case Input.Keys.NUM_9:
                return VK_NUMPAD9;
            case Input.Keys.A:
                return VK_A;
            case Input.Keys.B:
                return VK_B;
            case Input.Keys.C:
                return VK_C;
            case Input.Keys.D:
                return VK_D;
            case Input.Keys.E:
                return VK_E;
            case Input.Keys.F:
                return VK_F;
            case Input.Keys.G:
                return VK_G;
            case Input.Keys.H:
                return VK_H;
            case Input.Keys.I:
                return VK_I;
            case Input.Keys.J:
                return VK_J;
            case Input.Keys.K:
                return VK_K;
            case Input.Keys.L:
                return VK_L;
            case Input.Keys.M:
                return VK_M;
            case Input.Keys.N:
                return VK_N;
            case Input.Keys.O:
                return VK_O;
            case Input.Keys.P:
                return VK_P;
            case Input.Keys.Q:
                return VK_Q;
            case Input.Keys.R:
                return VK_R;
            case Input.Keys.S:
                return VK_S;
            case Input.Keys.T:
                return VK_T;
            case Input.Keys.U:
                return VK_U;
            case Input.Keys.V:
                return VK_V;
            case Input.Keys.W:
                return VK_W;
            case Input.Keys.X:
                return VK_X;
            case Input.Keys.Y:
                return VK_Y;
            case Input.Keys.Z:
                return VK_Z;
            case Input.Keys.ALT_LEFT:
                return VK_ALT;
            case Input.Keys.ALT_RIGHT:
                return VK_ALT_GRAPH;
            case Input.Keys.BACKSLASH:
                return VK_BACK_SLASH;
            case Input.Keys.COMMA:
                return VK_COMMA;
            case Input.Keys.FORWARD_DEL:
                return VK_DELETE;
            case Input.Keys.DPAD_LEFT:
                return VK_LEFT;
            case Input.Keys.DPAD_RIGHT:
                return VK_RIGHT;
            case Input.Keys.DPAD_UP:
                return VK_UP;
            case Input.Keys.DPAD_DOWN:
                return VK_DOWN;
            case Input.Keys.ENTER:
                return VK_ENTER;
            case Input.Keys.HOME:
                return VK_HOME;
            case Input.Keys.MINUS:
                return VK_MINUS;
            case Input.Keys.PERIOD:
                return VK_PERIOD;
            case Input.Keys.PLUS:
                return VK_ADD;
            case Input.Keys.SEMICOLON:
                return VK_SEMICOLON;
            case Input.Keys.SHIFT_LEFT:
                return VK_SHIFT;
            case Input.Keys.SHIFT_RIGHT:
                return VK_SHIFT;
            case Input.Keys.SLASH:
                return VK_SLASH;
            case Input.Keys.SPACE:
                return VK_SPACE;
            case Input.Keys.TAB:
                return VK_TAB;
            case Input.Keys.DEL:
                return VK_DELETE;
            case Input.Keys.CONTROL_LEFT:
                return VK_CONTROL;
            case Input.Keys.CONTROL_RIGHT:
                return VK_CONTROL;
            case Input.Keys.ESCAPE:
                return VK_ESCAPE;
            case Input.Keys.F1:
                return VK_F1;
            case Input.Keys.F2:
                return VK_F2;
            case Input.Keys.F3:
                return VK_F3;
            case Input.Keys.F4:
                return VK_F4;
            case Input.Keys.F5:
                return VK_F5;
            case Input.Keys.F6:
                return VK_F6;
            case Input.Keys.F7:
                return VK_F7;
            case Input.Keys.F8:
                return VK_F8;
            case Input.Keys.F9:
                return VK_F9;
            case Input.Keys.F10:
                return VK_F10;
            case Input.Keys.F11:
                return VK_F11;
            case Input.Keys.F12:
                return VK_F12;
            case Input.Keys.COLON:
                return VK_COLON;
            default:
                return 0;
        }
    }

    public static int getGdxKeyCode(int awtKeyCode) {
        switch (awtKeyCode) {
            case VK_0:
                return Input.Keys.NUM_0;
            case VK_1:
                return Input.Keys.NUM_1;
            case VK_2:
                return Input.Keys.NUM_2;
            case VK_3:
                return Input.Keys.NUM_3;
            case VK_4:
                return Input.Keys.NUM_4;
            case VK_5:
                return Input.Keys.NUM_5;
            case VK_6:
                return Input.Keys.NUM_6;
            case VK_7:
                return Input.Keys.NUM_7;
            case VK_8:
                return Input.Keys.NUM_8;
            case VK_9:
                return Input.Keys.NUM_9;
            case VK_NUMPAD0:
                return Input.Keys.NUM_0;
            case VK_NUMPAD1:
                return Input.Keys.NUM_1;
            case VK_NUMPAD2:
                return Input.Keys.NUM_2;
            case VK_NUMPAD3:
                return Input.Keys.NUM_3;
            case VK_NUMPAD4:
                return Input.Keys.NUM_4;
            case VK_NUMPAD5:
                return Input.Keys.NUM_5;
            case VK_NUMPAD6:
                return Input.Keys.NUM_6;
            case VK_NUMPAD7:
                return Input.Keys.NUM_7;
            case VK_NUMPAD8:
                return Input.Keys.NUM_8;
            case VK_NUMPAD9:
                return Input.Keys.NUM_9;
            case VK_A:
                return Input.Keys.A;
            case VK_B:
                return Input.Keys.B;
            case VK_C:
                return Input.Keys.C;
            case VK_D:
                return Input.Keys.D;
            case VK_E:
                return Input.Keys.E;
            case VK_F:
                return Input.Keys.F;
            case VK_G:
                return Input.Keys.G;
            case VK_H:
                return Input.Keys.H;
            case VK_I:
                return Input.Keys.I;
            case VK_J:
                return Input.Keys.J;
            case VK_K:
                return Input.Keys.K;
            case VK_L:
                return Input.Keys.L;
            case VK_M:
                return Input.Keys.M;
            case VK_N:
                return Input.Keys.N;
            case VK_O:
                return Input.Keys.O;
            case VK_P:
                return Input.Keys.P;
            case VK_Q:
                return Input.Keys.Q;
            case VK_R:
                return Input.Keys.R;
            case VK_S:
                return Input.Keys.S;
            case VK_T:
                return Input.Keys.T;
            case VK_U:
                return Input.Keys.U;
            case VK_V:
                return Input.Keys.V;
            case VK_W:
                return Input.Keys.W;
            case VK_X:
                return Input.Keys.X;
            case VK_Y:
                return Input.Keys.Y;
            case VK_Z:
                return Input.Keys.Z;
            case VK_ALT:
                return Input.Keys.ALT_LEFT;
            case VK_ALT_GRAPH:
                return Input.Keys.ALT_RIGHT;
            case VK_BACK_SLASH:
                return Input.Keys.BACKSLASH;
            case VK_COMMA:
                return Input.Keys.COMMA;
            case VK_DELETE:
                return Input.Keys.FORWARD_DEL;
            case VK_LEFT:
                return Input.Keys.DPAD_LEFT;
            case VK_RIGHT:
                return Input.Keys.DPAD_RIGHT;
            case VK_UP:
                return Input.Keys.DPAD_UP;
            case VK_DOWN:
                return Input.Keys.DPAD_DOWN;
            case VK_ENTER:
                return Input.Keys.ENTER;
            case VK_HOME:
                return Input.Keys.HOME;
            case VK_MINUS:
                return Input.Keys.MINUS;
            case VK_PERIOD:
                return Input.Keys.PERIOD;
            case VK_ADD:
                return Input.Keys.PLUS;
            case VK_SEMICOLON:
                return Input.Keys.SEMICOLON;
            case VK_SHIFT:
                return Input.Keys.SHIFT_LEFT;
            case VK_SLASH:
                return Input.Keys.SLASH;
            case VK_SPACE:
                return Input.Keys.SPACE;
            case VK_TAB:
                return Input.Keys.TAB;
            case VK_CONTROL:
                return Input.Keys.CONTROL_LEFT;
            case VK_ESCAPE:
                return Input.Keys.ESCAPE;
            case VK_F1:
                return Input.Keys.F1;
            case VK_F2:
                return Input.Keys.F2;
            case VK_F3:
                return Input.Keys.F3;
            case VK_F4:
                return Input.Keys.F4;
            case VK_F5:
                return Input.Keys.F5;
            case VK_F6:
                return Input.Keys.F6;
            case VK_F7:
                return Input.Keys.F7;
            case VK_F8:
                return Input.Keys.F8;
            case VK_F9:
                return Input.Keys.F9;
            case VK_F10:
                return Input.Keys.F10;
            case VK_F11:
                return Input.Keys.F11;
            case VK_F12:
                return Input.Keys.F12;
            case VK_COLON:
                return Input.Keys.COLON;
            default:
                return 0;
        }
    }

    void resetValues() {
        deltaX = 0;
        deltaY = 0;
        justTouched = false;
    }

    void deregisterCanvas() {
        canvas.removeKeyListener(keyListener);
        canvas.removeMouseListener(mouseListener);
        canvas.removeMouseMotionListener(mouseMotionListener);
    }

    final static class AWTKeyEvent {

        static final int KEY_DOWN = 0;
        static final int KEY_UP = 1;
        static final int KEY_TYPED = 2;
        long timeStamp;
        int type;
        int keyCode;
        char keyChar;
    }

    final static class TouchEvent {

        static final int TOUCH_DOWN = 0;
        static final int TOUCH_UP = 1;
        static final int TOUCH_DRAGGED = 2;
        static final int TOUCH_SCROLLED = 3;
        static final int TOUCH_MOVED = 4;
        long timeStamp;
        int type;
        int x;
        int y;
        int scrollAmount;
        int button;
        int pointer;
    }
    private final static Pool<AWTKeyEvent> usedKeyEvents = new Pool<AWTKeyEvent>(16, 1000) {

        protected AWTKeyEvent newObject() {
            return new AWTKeyEvent();
        }
    };
    private final static Pool<TouchEvent> usedTouchEvents = new Pool<TouchEvent>(16, 1000) {

        protected TouchEvent newObject() {
            return new TouchEvent();
        }
    };
    final List<AWTKeyEvent> keyEvents = new ArrayList<AWTKeyEvent>();
    final List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();

    void processEvents() {
        synchronized (keyEvents) {
            synchronized (touchEvents) {

                if (processor != null) {
                    int len = keyEvents.size();
                    for (int i = 0; i < len; i++) {
                        AWTKeyEvent e = keyEvents.get(i);
                        currentEventTimeStamp = e.timeStamp;
                        switch (e.type) {
                            case AWTKeyEvent.KEY_DOWN:
                                processor.keyDown(e.keyCode);
                                break;
                            case AWTKeyEvent.KEY_UP:
                                processor.keyUp(e.keyCode);
                                break;
                            case AWTKeyEvent.KEY_TYPED:
                                processor.keyTyped(e.keyChar);
                        }
                        usedKeyEvents.free(e);
                    }

                    len = touchEvents.size();
                    for (int i = 0; i < len; i++) {
                        TouchEvent e = touchEvents.get(i);
                        currentEventTimeStamp = e.timeStamp;
                        switch (e.type) {
                            case TouchEvent.TOUCH_DOWN:
                                processor.touchDown(e.x, e.y, e.pointer, e.button);
                                break;
                            case TouchEvent.TOUCH_UP:
                                processor.touchUp(e.x, e.y, e.pointer, e.button);
                                break;
                            case TouchEvent.TOUCH_DRAGGED:
                                processor.touchDragged(e.x, e.y, e.pointer);
                                break;
                            case TouchEvent.TOUCH_MOVED:
                                processor.touchMoved(e.x, e.y);
                                break;
                            case TouchEvent.TOUCH_SCROLLED:
                                processor.scrolled(e.scrollAmount);
                        }
                        usedTouchEvents.free(e);
                    }
                } else {
                    int len = touchEvents.size();
                    for (int i = 0; i < len; i++) {
                        usedTouchEvents.free(touchEvents.get(i));
                    }

                    len = keyEvents.size();
                    for (int i = 0; i < len; i++) {
                        usedKeyEvents.free(keyEvents.get(i));
                    }
                }

                keyEvents.clear();
                touchEvents.clear();
            }
        }
    }
}
