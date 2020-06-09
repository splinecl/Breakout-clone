package dk.innovasion.objects;

import org.joml.Vector3f;

public interface IObject {

    void move();
    void draw();
    boolean collision(IObject o);
    Vector3f position();
    float width();
    float height();
    void reverse();
}