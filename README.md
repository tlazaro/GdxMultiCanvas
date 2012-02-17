GdxMultiCanvas
-------------------------------------------------------------------------------

This project is just a simple test for having multiple canvases inside a Gdx application. It is a work in progress and it is not finished yet.

There is a single main class:

* GdxTest

Uses my custom Application implementation, LwjglMultiCanvas. That application is based on a similiar implementation for SWT http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=2588. It's using LwjglGraphics2, a minor modification of LwjglGraphics provided in the gdx-lwjgl-backed and AWTGLCanvas2, a minor modification of AWTGLCanvas provided in Lwjgl.

Details
-------------------------------------------------------------------------------

* AWTGLCanvas2

Instead of working on the AWT I modified and split it's behavior so it can be used in a regular thread and do active rendering.

* LwjglMultiCanvas

The application can be created and new canvases can just be added and removed while running. It currently updates at a given rate using a Timer. A better main loop will be provided. Input still not implemented

Help
-------------------------------------------------------------------------------

Help is welcome!

Usage
-------------------------------------------------------------------------------

A Netbeans project is already setup, you only have to run it. For other environments just make sure the provided jars inside the 'lib' folder are included.

License
-------------------------------------------------------------------------------

I don't really care, do whatever you want with the code. I would like to have this included in Libgdx core API if it works. If it is mandatory to provide a license Apache 2 it is.
