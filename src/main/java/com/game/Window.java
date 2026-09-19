package com.game;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryUtil;

public class Window {
    int width;
    int height;

    private GLFWVidMode vidMode;
    private long windowHandle;

    private boolean shouldClose;
    public void makeWindow() {
        if (!GLFW.glfwInit()) {
            throw new RuntimeException("Unable to initialize GLFW");
        }

        if (!GLFWVulkan.glfwVulkanSupported()) {
            throw new RuntimeException("Cannot find a compatible Vulkan installable client driver (ICD)");
        }

        vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode == null) {
            throw new RuntimeException("Error getting primary monitor");
        }
        width = vidMode.width();
        height = vidMode.height();

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_FALSE);

        // Create the window
        windowHandle = GLFW.glfwCreateWindow(width, height, "Java RTS Game", MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

//        keyboardInput = new KeyboardInput(handle);

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, w, h) -> {
            width = w;
            height = h;
        });

//        mouseInput = new MouseInput(handle);
    }
    public void setShouldClose(){
        GLFW.glfwSetWindowShouldClose(windowHandle, true);
    }
    public boolean shouldClose(){
        return GLFW.glfwWindowShouldClose(windowHandle);
    }
    public void cleanup() {
        Callbacks.glfwFreeCallbacks(windowHandle);
        GLFW.glfwDestroyWindow(windowHandle);
        GLFW.glfwTerminate();
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }
}
