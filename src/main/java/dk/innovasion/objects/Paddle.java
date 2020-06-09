package dk.innovasion.objects;

import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Paddle implements IObject {

    private final Vector3f position;
    private final float width = 0.1f;
    private final float height = 0.05f;
    private final FloatBuffer buffer;
    private final float velocity;
    private final int vbo;

    public Paddle(Vector3f position){
        this.position = position;
        velocity = 0.01f;
        buffer = memAllocFloat(6 * 2);
        buffer.put(-width).put(-height);
        buffer.put(+width).put(-height);
        buffer.put(+width).put(+height);
        buffer.put(+width).put(+height);
        buffer.put(-width).put(+height);
        buffer.put(-width).put(-height);
        buffer.flip();
        vbo = glGenBuffers();
    }

    public void left(){
        position.x -= velocity;
    }

    public void right(){
        position.x += velocity;
    }

    public void destroy(){
        memFree(buffer);
    }

    @Override
    public void move() {

    }

    @Override
    public void draw() {
        glPushMatrix();
        glLoadIdentity();
        glTranslatef(position.x, position.y, position.z);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 0, 0L);

        glColor3f(1.0f,1.0f,1.0f);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        glPopMatrix();
    }

    @Override
    public boolean collision(IObject o) {
        if(position.x < o.position().x + o.width() &&
                position.x + width > o.position().x &&
                position.y < o.position().y + o.height() &&
                position.y + height > o.position().y){
            System.out.println("COLLISION!");
            return true;
        }
        return false;
    }

    @Override
    public Vector3f position() {
        return position;
    }

    @Override
    public float width() {
        return width;
    }

    @Override
    public float height() {
        return height;
    }

    @Override
    public void reverse() {

    }
}
