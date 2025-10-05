import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.badlogic.gdx.math.GridPoint2;

import ru.mipt.bit.platformer.classes.Positionable;
import ru.mipt.bit.platformer.classes.Tank;
import ru.mipt.bit.platformer.classes.Tree;
import ru.mipt.bit.platformer.classes.Direction;
import ru.mipt.bit.platformer.classes.ObjectDirection;

class TankTest {
    @Test
    void testMoveSelfInCurrentDirection() {
        Tank tank;
        Tree tree, tree1, tree2;

        tank = new Tank(1, 1, 0.4f);
        tank.direction.dir = Direction.RIGHT;
        tree = new Tree(1, 3);
        Positionable[] obstacles = {tree};
        tank.moveSelfInCurrentDirection(obstacles);
        tank.movementProgress = 1f;
        assertEquals(2, tank.destinationCoordinates.x);
        assertEquals(1, tank.destinationCoordinates.y);

        tank = new Tank(1, 1, 0.4f);
        tank.direction.dir = Direction.RIGHT;
        tree = new Tree(2, 1);
        obstacles = new Positionable[]{tree};
        tank.moveSelfInCurrentDirection(obstacles);
        tank.movementProgress = 1f;
        assertEquals(1, tank.destinationCoordinates.x);
        assertEquals(1, tank.destinationCoordinates.y);

        tank = new Tank(1, 1, 0.4f);
        tank.direction.dir = Direction.UP;
        tree1 = new Tree(2, 1);
        tree2 = new Tree(0, 1);
        obstacles = new Positionable[]{tree1, tree2};
        tank.moveSelfInCurrentDirection(obstacles);
        tank.movementProgress = 1f;
        assertEquals(1, tank.destinationCoordinates.x);
        assertEquals(2, tank.destinationCoordinates.y);
    }
}
