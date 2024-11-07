package entity;

import engine.DrawManager;

public class SkinEntity extends Entity {
    public SkinEntity(DrawManager.SpriteType spriteType) {
        super();
        setSpriteType(spriteType);
    }
}