package io.github.squid233.game;

import overrungl.glfw.GLFW;
import overrungl.glfw.GLFWCallbacks;
import overrungl.glfw.GLFWErrorCallback;
import overrungl.opengl.GL;
import overrungl.opengl.GLLoader;
import overrungl.util.CheckUtil;

import java.lang.foreign.MemorySegment;

/**
 * @author squid233
 * @since 1.0
 */
public final class MyGame {
    /** The window handle */
    private MemorySegment window;

    public void run() {
        init();
        load();
        loop();

        // Free the window callbacks and destroy the window
        GLFWCallbacks.free(window);
        GLFW.destroyWindow(window);

        // Terminate GLFW and remove the error callback
        GLFW.terminate();
        GLFW.setErrorCallback(null);
    }

    private void init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint().set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        // If you are using Kotlin, you should use the builtin check function
        CheckUtil.check(GLFW.init(), "Unable to initialize GLFW");

        // Configure GLFW
        // optional, the current window hints are already the default
        GLFW.defaultWindowHints();
        // the window will stay hidden after creation
        GLFW.windowHint(GLFW.VISIBLE, false);
        // the window will be resizable
        GLFW.windowHint(GLFW.RESIZABLE, true);

        // Create the window
        window = GLFW.createWindow(300, 300, "Hello World!", MemorySegment.NULL, MemorySegment.NULL);
        CheckUtil.checkNotNullptr(window, "Failed to create the GLFW window");

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.setKeyCallback(window, (handle, key, scancode, action, mods) -> {
            if (key == GLFW.KEY_ESCAPE && action == GLFW.RELEASE) {
                // We will detect this in the rendering loop
                GLFW.setWindowShouldClose(window, true);
            }
        });

        GLFW.setFramebufferSizeCallback(window, (handle, width, height) ->
            // Resize the viewport
            GL.viewport(0, 0, width, height));

        // Get the resolution of the primary monitor
        var vidMode = GLFW.getVideoMode(GLFW.getPrimaryMonitor());
        if (vidMode != null) {
            // Get the window size passed to GLFW.createWindow
            var size = GLFW.getWindowSize(window);
            // Center the window
            GLFW.setWindowPos(
                window,
                (vidMode.width() - size.x()) / 2,
                (vidMode.height() - size.y()) / 2
            );
        }

        // Make the OpenGL context current
        GLFW.makeContextCurrent(window);
        // Enable v-sync
        GLFW.swapInterval(1);

        // Make the window visible
        GLFW.showWindow(window);
    }

    private void load() {
        // Load OpenGL capabilities with GLFW.
        // The default loading function uses a compatibility profile,
        // which can access deprecated and removed functions.
        CheckUtil.checkNotNull(GLLoader.load(GLFW::getProcAddress), "Failed to load OpenGL");

        // Set the clear color
        GL.clearColor(0.4f, 0.6f, 0.9f, 1.0f);
    }

    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!GLFW.windowShouldClose(window)) {
            // clear the framebuffer
            GL.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT);

            // swap the color buffers
            GLFW.swapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.pollEvents();
        }
    }

    public static void main(String[] args) {
        new MyGame().run();
    }
}
