GdxMultiCanvas
-------------------------------------------------------------------------------

This project is just a simple test for having multiple canvases inside a Gdx application. It is a work in progress and doesn't yet work as expected.

Provided are two main classes

* AWTTest
A test that is part of the Lwjgl API. It show how to use AWTGLCanvas, the only way to have multi canvas with Lwjgl.

* GdxTest
Uses my custom Application implementation, LwjglMultiCanvas. That application is based on LwjglCanvas included in Gdx lwjgl-backend API. Try resizing the window to understand how it (wrongly) behaves.

It currently doesn't work quite well, the first canvas gets screwed up. In other tests in more complex environments the 'y' offest of canvases gets affected by the others.

Details
-------------------------------------------------------------------------------

* LwjglMultiCanvas

The method start() is not being invoked. If I did it would cause a deadlock and nothing would work. Further work need to be done there. Input is not working because of that.

Help
-------------------------------------------------------------------------------

Pleeeeeeeeeeeeeeeeeease help

Usage
-------------------------------------------------------------------------------

A Netbeans project is already setup, you only have to run it. For other environments just make sure the provided jars inside the 'lib' folder are included.

License
-------------------------------------------------------------------------------

I don't really care, do whatever you want with the code. I would like to have this included in Libgdx core API if it works. If it is mandatory to provide a license Apache 2 it is.
