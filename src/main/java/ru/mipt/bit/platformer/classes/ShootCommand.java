package ru.mipt.bit.platformer.classes;

import java.util.ArrayList;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Command;
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.ShootableEntity;
import ru.mipt.bit.platformer.classes.ShooterEntity;


public class ShootCommand implements Command {
    private ShooterEntity shooterEntity;
    private ArrayList<ShootableEntity> projectiles;

    public void MoveCommand(
        ShooterEntity shooterEntity,
        ArrayList<ShootableEntity> projectiles
    ) {
        this.shooterEntity = shooterEntity;
        this.projectiles = projectiles;
    }

    @Override
    public void execute() {
        ShootableEntity projectile = shooterEntity.shoot();
        projectiles.add(projectile);
    }
}
