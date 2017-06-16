/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package db;

/**
 *
 * @author POS
 */
public enum DBRecordType {
    /**
     * Положение в пространстве. [дифферент, крен, курс].
     */

    ORIENTATION("Orientation"),
    /**
     * Бортовой GPS. [широта, долгота, достоверность (boolean)].
     */
    GPS_BOARD("GPS board"),
    /**
     * RedWave GPS. [широта, долгота].
     */
    GPS_REDWAVE("GPS redwave"),
    /**
     * Температура. [температурный датчик РАДАР с номером #02, температурный
     * датчик РАДАР с номером #03, датчик температуры установленный на модеме
     * RedWave.].
     */
    TEMP("Temperature values"),
    /**
     * Корпус. [дифферент, крен, курс].
     */
    HULL("Hull values"),
    /**
     * Глубина. [датчик глубины РАДАР,датчик глубины ПД-100, датчик глубины
     * RedWave].
     */
    DEPTH("Depth values"),
    /**
     * Батареи. [напряжение, ток, заряд].
     */
    BATTERIES("Batteries"),
    /**
     * Управляющая программа. [статус, название режима, активные повeдения].
     */
    HELM("Helm"),
    /**
     * Значения серво моторов возвращаемых контроллером. серво 1, серво 2, серво
     * 3, серво 4, серво 5].
     */
    SERVO_V("Servo vehicle values"),
    /**
     * Значения серво моторов управляющей программы. [серво 1, серво 2, серво 3,
     * серво 4, серво 5].
     */
    SERVO_P("Servo program values"),
    /**
     * ИСТ. [скорость, глубина, курс, температура].
     */
    IST("Velocity measure device"),
    /**
     * Трэк миссии. [x, y, скорость, широта, долгота].
     */
    TRACK("Track");

    private final String name;

    private DBRecordType(String name) {
        this.name = name;

    }

    public String getName() {
        return this.name;
    }
}
