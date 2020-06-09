package dk.innovasion.objects;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

public class Ball implements IObject {

    private Vector3f position;
    private final Vector3f velocity;
    private final float width = 0.05f;
    private final float height = 0.05f;
    private final FloatBuffer buffer;
    private final int vbo;

    public Ball(Vector3f position){
        this.position = position;
        velocity = new Vector3f(0.01f, 0.02f,0.0f);
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

    private boolean checkBounds(){
        if (position.y <=  -1.0f) {
            position.y = -0.99f;
            velocity.y *= -1.0f;
            return true;
        }
        if (position.y + height >=  1.0f) {
            position.y = 0.99f - height;
            velocity.y *= -1.0f;
            return true;
        }
        if (position.x + width >= 1.0f) {
            position.x = 0.99f - width;
            velocity.x *= -1.0f;
            return true;
        }
        if (position.x <=  -1.0f) {
            position.x = -0.99f;
            velocity.x *= -1.0f;
            return true;
        }

        return false;
    }

    @Override
    public void move() {
        if(!checkBounds()){
            position = new Matrix4f().translate(velocity.x, velocity.y, velocity.z).transformPosition(position);
        }
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

        glColor3f(1.0f,1.0f,0.0f);

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
        velocity.negate();
    }
}
