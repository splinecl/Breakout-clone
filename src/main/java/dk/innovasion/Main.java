package dk.innovasion;

import dk.innovasion.input.KeyboardInput;
import dk.innovasion.objects.Ball;
import dk.innovasion.objects.IObject;
import dk.innovasion.objects.Paddle;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {

    private long window;
    private final int width = 1920;
    private final int height = 1080;
    private List<IObject> gameObjects;
    private Paddle paddle;
    private KeyboardInput keyboardInput;

    public void run() {
        System.out.println("Breakout " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void initGame() {
        gameObjects = new ArrayList<>();
        gameObjects.add(new Ball(new Vector3f(0.0f,-0.0f,0.0f)));
        paddle = new Paddle(new Vector3f(0.0f,-0.5f,0.0f));

        glViewport(0,0,1920,1080);
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(width, height, "Breakout", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, keyboardInput = new KeyboardInput());

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);

        glfwSwapInterval(1);

        glfwShowWindow(window);
    }

    private void update(){
        glfwPollEvents();
        if(KeyboardInput.isKeyDown(GLFW_KEY_LEFT)){
            paddle.left();
        } else if(KeyboardInput.isKeyDown(GLFW_KEY_RIGHT)){
           paddle.right();
        }
    }

    private void render(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glTranslatef(0.0f, 0.0f, 0.0f);

        glMatrixMode(GL_MODELVIEW);
        paddle.draw();

        if(gameObjects != null){
            for (IObject gameObject : gameObjects) {
                gameObject.move();
                gameObject.draw();
                if(paddle.collision(gameObject)){
                    gameObject.reverse();
                }
            }
        }

        glfwSwapBuffers(window); // swap the color buffers
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        initGame();

        long lastTime = System.nanoTime();
        double delta = 0.0;
        double ns = 1_000_000_000.0 / 60.0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        while ( !glfwWindowShouldClose(window) ) {
            long now = System.nanoTime();
            delta += (now-lastTime)/ns;
            lastTime = now;

            if(delta >= 1){
                update();
                updates++;
                delta--;
            }

            render();
            frames++;

            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                System.out.println(updates + " UPS " + frames + " FPS");
                updates = 0;
                frames = 0;
            }
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

}