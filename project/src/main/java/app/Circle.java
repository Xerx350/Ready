package app;

import misc.Misc;
import misc.Vector2d;

import java.util.Objects;

/**
 * Класс окружности
 */
public class Circle {
    /**
     * Координаты центра
     */
    public final Vector2d pos;

    /**
     * радиус
     */
    public final double r;

    /**
     * Конструктор окружности
     *
     * @param pos     положение окружности
     * @param r       радиус окружности
     */
    public Circle(Vector2d pos, double r) {
        this.pos = pos;
        this.r = r;
    }


    /**
     * Получить цвет окружности
     *
     * @return цвет окружности
     */
    public int getColor() {
        return Misc.getColor(0xCC, 0x00, 0xFF, 0x0);
    }

    /**
     * получить точки для рисования окружности
     * @return точки
     */
    public float[] paint() {
        int loopCnt = 40;
        // создаём массив координат опорных точек
        float[] points = new float[loopCnt * 4];
        for (int i = 0; i < loopCnt; ++i) {
            // x координата первой точки
            points[i * 4] = (float) (pos.x + r * Math.cos(Math.PI / 20 * i));
            // y координата первой точки
            points[i * 4 + 1] = (float) (pos.y + r * Math.sin(Math.PI / 20 * i));

            // x координата второй точки
            points[i * 4 + 2] = (float) (pos.x + r * Math.cos(Math.PI / 20 * (i + 1)));
            // y координата точки
            points[i * 4 + 3] = (float) (pos.y + r * Math.sin(Math.PI / 20 * (i + 1)));
        }
        return points;
    }

    /**
     * Строковое представление объекта
     *
     * @return строковое представление объекта
     */
    @Override
    public String toString() {
        return "Point{" +
                "center=" + pos +
                " radius=" + r +
                '}';
    }

    /**
     * Получить хэш-код объекта
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(pos, r);
    }
}
