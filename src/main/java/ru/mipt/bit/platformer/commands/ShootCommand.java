package ru.mipt.bit.platformer.commands;
import ru.mipt.bit.platformer.commands.Command;

import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;


import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.Shooter;
import ru.mipt.bit.platformer.classes.Level;


public class ShootCommand implements Command {
    public Shooter entity;
    private HashMap<String, ShootableEntity> projectiles;
    Level level;

    public ShootCommand(
        Shooter entity,
        // HashMap<String, ShootableEntity> projectiles
        Level level
    ) {
        this.entity = entity;
        // this.projectiles = projectiles;
        this.level = level;
    }

    @Override
    public void execute() {
        ShootableEntity projectile = entity.shoot();
        level.addProjectile(projectile);
    }
}
