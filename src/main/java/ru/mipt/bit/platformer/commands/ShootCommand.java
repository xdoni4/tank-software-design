package ru.mipt.bit.platformer.commands;
import ru.mipt.bit.platformer.commands.Command;

import java.util.UUID;
import java.util.HashMap;
import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;


import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.Shooter;


public class ShootCommand implements Command {
    public Shooter entity;
    private HashMap<String, ShootableEntity> projectiles;

    public ShootCommand(
        Shooter entity,
        HashMap<String, ShootableEntity> projectiles
    ) {
        this.entity = entity;
        this.projectiles = projectiles;
    }

    @Override
    public void execute() {
        ShootableEntity projectile = entity.shoot();
        projectiles.put(UUID.randomUUID().toString(), projectile);
    }
}
