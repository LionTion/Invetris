package net.LionTion.Invetris;

import java.awt.*;

public class Block {
    public Color color;
    public int x;
    public int y;

    public Block(Color color) {
        this.color = color;
    }
    public Block(Color color, int x, int y) {
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "Color: " + color + " X: " + x + " Y: " + y;
    }
}
