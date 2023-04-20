package app;

import io.github.humbleui.jwm.MouseButton;
import io.github.humbleui.skija.*;
import lombok.Getter;
import misc.CoordinateSystem2d;
import misc.CoordinateSystem2i;
import misc.Vector2d;
import misc.Vector2i;
import panels.PanelLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static app.Colors.*;

/**
 * Класс задачи
 */
public class Task {
    /**
     * Текст задачи
     */
    public static final String TASK_TEXT = """
            ПОСТАНОВКА ЗАДАЧИ:
            На плоскости задано множество точек. Найти такие две
            окружности, что их центры находятся в точках заданного
            множества, внутри каждой из этих окружностей находятся
            хотя бы половина из всех точек заданного множества,
            и меньший из двух радиусов минимален.""";

    /**
     * Вещественная система координат задачи
     */
    @Getter
    private final CoordinateSystem2d ownCS;
    /**
     * Список точек
     */
    @Getter
    private final ArrayList<Point> points;
    /**
     * Список окружностей
     */
    @Getter
    private final ArrayList<Circle> circles;
    /**
     * Размер точки
     */
    private static final int POINT_SIZE = 3;

    /**
     * коэффициент колёсика мыши
     */
    private static final float WHEEL_SENSITIVE = 0.001f;

    /**
     * последняя СК окна
     */
    protected CoordinateSystem2i lastWindowCS;

    /**
     * Порядок разделителя сетки, т.е. раз в сколько отсечек
     * будет нарисована увеличенная
     */
    private static final int DELIMITER_ORDER = 10;

    /**
     * Флаг, решена ли задача
     */
    private boolean solved;

    /**
     * Задача
     *
     * @param ownCS  СК задачи
     * @param points массив точек
     */
    public Task(
            CoordinateSystem2d ownCS,
            ArrayList<Point> points
    ) {
        this.ownCS = ownCS;
        this.points = points;
        this.circles = new ArrayList<>();
    }

    /**
     * Рисование
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void paint(Canvas canvas, CoordinateSystem2i windowCS) {
        // Сохраняем последнюю СК
        lastWindowCS = windowCS;
        // рисуем координатную сетку
        renderGrid(canvas, lastWindowCS);
        // рисуем задачу
        renderTask(canvas, windowCS);
    }

    /**
     * Клик мыши по пространству задачи
     *
     * @param pos         положение мыши
     * @param mouseButton кнопка мыши
     */
    public void click(Vector2i pos, MouseButton mouseButton) {
        if (lastWindowCS == null) return;
        // получаем положение на экране
        Vector2d taskPos = ownCS.getCoords(pos, lastWindowCS);
        // если левая кнопка мыши, добавляем в первое множество
        addPoint(taskPos);
    }

    /**
     * проверка, решена ли задача
     *
     * @return флаг
     */
    public boolean isSolved() {
        return solved;
    }

    /**
     * Отмена решения задачи
     */
    public void cancel() {
        circles.clear();
        solved = false;
    }

    /**
     * Очистить задачу
     */
    public void clear() {
        points.clear();
        circles.clear();
    }

    /**
     * Добавить точку
     *
     * @param pos      положение
     */
    public void addPoint(Vector2d pos) {
        Point newPoint = new Point(pos);
        points.add(newPoint);
        // Добавляем в лог запись информации
        PanelLog.info("точка " + newPoint + " добавлена");
    }

    /**
     * Рисование сетки
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    public void renderGrid(Canvas canvas, CoordinateSystem2i windowCS) {
        // сохраняем область рисования
        canvas.save();
        // получаем ширину штриха(т.е. по факту толщину линии)
        float strokeWidth = 0.03f / (float) ownCS.getSimilarity(windowCS).y + 0.5f;
        // создаём перо соответствующей толщины
        try (var paint = new Paint().setMode(PaintMode.STROKE).setStrokeWidth(strokeWidth).setColor(TASK_GRID_COLOR)) {
            // перебираем все целочисленные отсчёты нашей СК по оси X
            for (int i = (int) (ownCS.getMin().x); i <= (int) (ownCS.getMax().x); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(i, 0, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % DELIMITER_ORDER == 0 ? 5 : 2;
                // рисуем вертикальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y + strokeHeight, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x, windowPos.y - strokeHeight, paint);
            }
            // перебираем все целочисленные отсчёты нашей СК по оси Y
            for (int i = (int) (ownCS.getMin().y); i <= (int) (ownCS.getMax().y); i++) {
                // находим положение этих штрихов на экране
                Vector2i windowPos = windowCS.getCoords(0, i, ownCS);
                // каждый 10 штрих увеличенного размера
                float strokeHeight = i % 10 == 0 ? 5 : 2;
                // рисуем горизонтальный штрих
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x + strokeHeight, windowPos.y, paint);
                canvas.drawLine(windowPos.x, windowPos.y, windowPos.x - strokeHeight, windowPos.y, paint);
            }
        }
        // восстанавливаем область рисования
        canvas.restore();
    }

    /**
     * Рисование задачи
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     */
    private void renderTask(Canvas canvas, CoordinateSystem2i windowCS) {
        canvas.save();
        // создаём перо
        try (var paint = new Paint()) {
            for (Point p : points) {
                paint.setColor(p.getColor());
                // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
                // а в классическом представлении - вверх
                Vector2i windowPos = windowCS.getCoords(p.pos.x, p.pos.y, ownCS);
                // рисуем точку
                canvas.drawRect(Rect.makeXYWH(windowPos.x - POINT_SIZE, windowPos.y - POINT_SIZE, POINT_SIZE * 2, POINT_SIZE * 2), paint);
            }
            for (Circle c : circles) {
                paint.setColor(c.getColor());
                float[] drawing = c.paint();
                float[] windowPosPoints = new float[drawing.length];
                for (int i = 0; i < drawing.length; i += 4) {
                    // y-координату разворачиваем, потому что у СК окна ось y направлена вниз,
                    // а в классическом представлении - вверх
                    Vector2i windowPos = windowCS.getCoords(drawing[i], drawing[i + 1], ownCS);
                    windowPosPoints[i] = (float) windowPos.x;
                    windowPosPoints[i + 1] = (float) windowPos.y;
                    windowPos = windowCS.getCoords(drawing[i + 2], drawing[i + 3], ownCS);
                    windowPosPoints[i + 2] = (float) windowPos.x;
                    windowPosPoints[i + 3] = (float) windowPos.y;
                }
                // рисуем линии
                canvas.drawLines(windowPosPoints, paint);
            }
        }
        canvas.restore();
    }

    /**
     * Решение задачи
     */
    public void solve() {
        circles.clear();

        int n = points.size();
        double[][] distance = new double[n][n];
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                distance[i][j] = Vector2d.subtract(points.get(i).pos, points.get(j).pos).length();
                distance[j][i] = distance[i][j];
            }
        }
        for (int i = 0; i < n; ++i) {
            Arrays.sort(distance[i]);
        }
        int c1 = 0, c2 = 1;
        int centerElem = (n / 2) - 1 + n % 2;
        if (distance[c1][centerElem] > distance[c2][centerElem]) {
            int t = c1;
            c1 = c2;
            c2 = t;
        }
        for (int i = 2; i < n; ++i) {
            if (distance[c1][centerElem] > distance[i][centerElem]) {
                c2 = c1;
                c1 = i;
            } else {
                if (distance[c2][centerElem] > distance[i][centerElem]) {
                    c2 = i;
                }
            }
        }
        circles.add(new Circle(points.get(c1).pos, distance[c1][centerElem]));
        circles.add(new Circle(points.get(c2).pos, distance[c2][centerElem]));

        solved = true;
    }

    /**
     * Масштабирование области просмотра задачи
     *
     * @param delta  прокрутка колеса
     * @param center центр масштабирования
     */
    public void scale(float delta, Vector2i center) {
        if (lastWindowCS == null) return;
        // получаем координаты центра масштабирования в СК задачи
        Vector2d realCenter = ownCS.getCoords(center, lastWindowCS);
        // выполняем масштабирование
        ownCS.scale(1 + delta * WHEEL_SENSITIVE, realCenter);
    }

    /**
     * Получить ответ решения
     */
    public String getAnswer() {
        return  "окружность №1:" + circles.get(0).toString() +
                " окружность №2:" + circles.get(1).toString();
    }

    /**
     * Получить положение курсора мыши в СК задачи
     *
     * @param x        координата X курсора
     * @param y        координата Y курсора
     * @param windowCS СК окна
     * @return вещественный вектор положения в СК задачи
     */
    public Vector2d getRealPos(int x, int y, CoordinateSystem2i windowCS) {
        return ownCS.getCoords(x, y, windowCS);
    }

    /**
     * Рисование курсора мыши
     *
     * @param canvas   область рисования
     * @param windowCS СК окна
     * @param font     шрифт
     * @param pos      положение курсора мыши
     */
    public void paintMouse(Canvas canvas, CoordinateSystem2i windowCS, Font font, Vector2i pos) {
        // создаём перо
        try (var paint = new Paint().setColor(TASK_GRID_COLOR)) {
            // сохраняем область рисования
            canvas.save();
            // рисуем перекрестие
            canvas.drawRect(Rect.makeXYWH(0, pos.y - 1, windowCS.getSize().x, 2), paint);
            canvas.drawRect(Rect.makeXYWH(pos.x - 1, 0, 2, windowCS.getSize().y), paint);
            // смещаемся немного для красивого вывода текста
            canvas.translate(pos.x + 3, pos.y - 5);
            // положение курсора в пространстве задачи
            Vector2d realPos = getRealPos(pos.x, pos.y, lastWindowCS);
            // выводим координаты
            canvas.drawString(realPos.toString(), 0, 0, font, paint);
            // восстанавливаем область рисования
            canvas.restore();
        }
    }
}
