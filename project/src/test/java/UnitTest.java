//import app.Point;
import app.Circle;
import app.Point;
import app.Task;
import misc.CoordinateSystem2d;
import misc.Vector2d;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс тестирования
 */
public class UnitTest {

    /**
     * Проверяет, находится ли точка в окружности
     * @param point точка
     * @param circle окружность
     * @return результат проверки
     */
    private static boolean isInside(Point point, Circle circle) {
        return Vector2d.subtract(point.pos, circle.pos).length() <= circle.r;
    }
    /**
     * Тело проверки
     * @param points исходные данные
     */
    private static void test(ArrayList<Point> points) {
        Task task = new Task(new CoordinateSystem2d(-10, -10, 10, 10), points);
        task.solve();
        ArrayList<Circle> circles = task.getCircles();

        int count0 = 0;
        int count1 = 0;
        for (Point p: points) {
            if (isInside(p, circles.get(0))) ++count0;
            if (isInside(p, circles.get(1))) ++count1;
        }

        assert count0 >= points.size() / 2 && count1 >= points.size() / 2;
    }

    /**
     * Тест 1
     */

    @Test
    public void test1() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(0, 0)));
        points.add(new Point(new Vector2d(3, 0)));
        points.add(new Point(new Vector2d(2, 2)));
        points.add(new Point(new Vector2d(-8, -7)));
        points.add(new Point(new Vector2d(-3, 2)));

        test(points);
    }
    /**
     * Тест 2
     */
    @Test
    public void test2() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(-10, -10)));
        points.add(new Point(new Vector2d(10, 10)));
        points.add(new Point(new Vector2d(8, 8)));
        points.add(new Point(new Vector2d(-3, -3)));
        points.add(new Point(new Vector2d(0, 0)));

        test(points);
    }
    /**
     * Тест 3
     */
    @Test
    public void test3() {
        ArrayList<Point> points = new ArrayList<>();
        points.add(new Point(new Vector2d(1, 0)));
        points.add(new Point(new Vector2d(9, 0)));
        points.add(new Point(new Vector2d(-3, 2)));
        points.add(new Point(new Vector2d(-10, -7)));
        points.add(new Point(new Vector2d(10, 2)));

        test(points);
    }
}
