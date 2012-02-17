/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badlogic.gdx.backends.lwjgl;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Orientation;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputProcessor;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.AWTGLCanvas2;

/**
 *
 * @author tomas
 */
public class LwjglMultiCanvasInput implements Input {

    private AWTInput currentInput;
    private final List<AWTInput> contexts = new ArrayList<AWTInput>();

    void initContext(AWTGLCanvas2 canvas) {
        AWTInput nContext = new AWTInput(canvas);
        synchronized (contexts) {
            contexts.add(nContext);
        }
    }

    void setCurrentCanvas(AWTGLCanvas2 canvas) {
        synchronized (contexts) {
            currentInput = getContext(canvas);
        }
    }

    void resetValues() {
        if (currentInput != null) {
            currentInput.resetValues();
        }
    }

    void removeContext(AWTGLCanvas2 canvas) {
        synchronized (contexts) {
            AWTInput context = getContext(canvas);
            if (context != null) {
                contexts.remove(context);
                context.deregisterCanvas();
            }
        }
    }

    private AWTInput getContext(AWTGLCanvas2 canvas) {
        for (AWTInput c : contexts) {
            if (c.canvas == canvas) {
                return c;
            }
        }

        return null;
    }

    @Override
    public void vibrate(long[] pattern, int repeat) {
        currentInput.vibrate(pattern, repeat);
    }

    @Override
    public void vibrate(int milliseconds) {
        currentInput.vibrate(milliseconds);
    }

    @Override
    public void setOnscreenKeyboardVisible(boolean bln) {
        currentInput.setOnscreenKeyboardVisible(bln);
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        currentInput.setInputProcessor(processor);
    }

    @Override
    public void setCursorPosition(int x, int y) {
        currentInput.setCursorPosition(x, y);
    }

    @Override
    public void setCursorCatched(boolean bln) {
        currentInput.setCursorCatched(bln);
    }

    @Override
    public void setCatchMenuKey(boolean bln) {
        currentInput.setCatchMenuKey(bln);
    }

    @Override
    public void setCatchBackKey(boolean bln) {
        currentInput.setCatchBackKey(bln);
    }

    @Override
    public boolean justTouched() {
        return currentInput.justTouched();
    }

    @Override
    public boolean isTouched(int pointer) {
        return currentInput.isTouched(pointer);
    }

    @Override
    public boolean isTouched() {
        return currentInput.isTouched();
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        return currentInput.isPeripheralAvailable(peripheral);
    }

    @Override
    public boolean isKeyPressed(int code) {
        return currentInput.isKeyPressed(code);
    }

    @Override
    public boolean isCursorCatched() {
        return currentInput.isCursorCatched();
    }

    @Override
    public boolean isButtonPressed(int button) {
        return currentInput.isButtonPressed(button);
    }

    @Override
    public int getY(int pointer) {
        return currentInput.getY(pointer);
    }

    @Override
    public int getY() {
        return currentInput.getY();
    }

    @Override
    public int getX(int pointer) {
        return currentInput.getX(pointer);
    }

    @Override
    public int getX() {
        return currentInput.getX();
    }

    @Override
    public void getTextInput(TextInputListener tl, String string, String string1) {
        currentInput.getTextInput(tl, string, string1);
    }

    @Override
    public void getRotationMatrix(float[] floats) {
        currentInput.getRotationMatrix(floats);
    }

    @Override
    public int getRotation() {
        return currentInput.getRotation();
    }

    @Override
    public float getRoll() {
        return currentInput.getRoll();
    }

    @Override
    public float getPitch() {
        return currentInput.getPitch();
    }

    @Override
    public Orientation getNativeOrientation() {
        return currentInput.getNativeOrientation();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return currentInput.getInputProcessor();
    }

    @Override
    public int getDeltaY(int pointer) {
        return currentInput.getDeltaY(pointer);
    }

    @Override
    public int getDeltaY() {
        return currentInput.getDeltaY();
    }

    @Override
    public int getDeltaX(int pointer) {
        return currentInput.getDeltaX(pointer);
    }

    @Override
    public int getDeltaX() {
        return currentInput.getDeltaX();
    }

    @Override
    public long getCurrentEventTime() {
        return currentInput.getCurrentEventTime();
    }

    @Override
    public float getAzimuth() {
        return currentInput.getAzimuth();
    }

    @Override
    public float getAccelerometerZ() {
        return currentInput.getAccelerometerZ();
    }

    @Override
    public float getAccelerometerY() {
        return currentInput.getAccelerometerY();
    }

    @Override
    public float getAccelerometerX() {
        return currentInput.getAccelerometerX();
    }

    @Override
    public void cancelVibrate() {
        currentInput.cancelVibrate();
    }

    void processEvents() {
        currentInput.processEvents();
    }
}
