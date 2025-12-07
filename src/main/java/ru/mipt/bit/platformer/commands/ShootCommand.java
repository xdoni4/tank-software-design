package ru.mipt.bit.platformer.commands;

import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;


import ru.mipt.bit.platformer.Direction;
import ru.mipt.bit.platformer.ShootableEntity;
import ru.mipt.bit.platformer.Shooter;
import ru.mipt.bit.platformer.Level;


public class ShootCommand implements Command {
    public Shooter entity;
    private HashMap<String, ShootableEntity> projectiles;
    Level level;

    public ShootCommand(
        Shooter entity,
        Level level
    ) {
        this.entity = entity;
        this.level = level;
    }

    @Override
    public void execute() {
        ShootableEntity projectile = entity.shoot();
        level.addProjectile(projectile);
    }
}
