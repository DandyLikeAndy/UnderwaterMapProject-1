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

    ORIENTATION("Orientation", "pitch", "roll", "course"),
    /**
     * Бортовой GPS. [широта, долгота, достоверность (boolean)].
     */
    GPS_BOARD("GPS board", "lat", "lon", "veracity"),
    /**
     * RedWave GPS. [широта, долгота].
     */
    GPS_REDWAVE("GPS redwave", "lat", "lon"),
    /**
     * Температура. [температурный датчик РАДАР с номером #02, температурный
     * датчик РАДАР с номером #03, датчик температуры установленный на модеме
     * RedWave.].
     */
    TEMP("Temperature values", "temp2", "temp3", "tempRedWave"),
    /**
     * Корпус. [дифферент, крен, курс].
     */
    HULL("Hull values", "pitch", "roll", "course"),
    /**
     * Глубина. [датчик глубины РАДАР,датчик глубины ПД-100, датчик глубины
     * RedWave].
     */
    DEPTH("Depth values", "depthRADAR", "depthPD100", "depthRedWave"),
    /**
     * Батареи. [напряжение, ток, заряд].
     */
    BATTERIES("Batteries", "voltage", "amperage", "charge"),
    /**
     * Управляющая программа. [статус, название режима, активные повeдения].
     */
    HELM("Helm", "status", "mode", "behaviors"),
    /**
     * Значения серво моторов возвращаемых контроллером. серво 1, серво 2, серво
     * 3, серво 4, серво 5].
     */
    SERVO_V("Servo vehicle values", "servo1", "servo2", "servo3", "servo4", "servo5"),
    /**
     * Значения серво моторов управляющей программы. [серво 1, серво 2, серво 3,
     * серво 4, серво 5].
     */
    SERVO_P("Servo program values", "servo1", "servo2", "servo3", "servo4", "servo5"),
    /**
     * ИСТ. [скорость, глубина, курс, температура].
     */
    IST("Velocity measure device", "speed", "depth","course", "temp"),
    /**
     * Трэк миссии. [x, y, скорость, широта, долгота].
     */
    TRACK("Track", "x","y", "speed", "lat", "lon");

    private final String name;
    private String[] parameters;

    private DBRecordType(String name, String ...parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return this.name;
    }

    public String[] getParameters() {
        return parameters;
    }
}
