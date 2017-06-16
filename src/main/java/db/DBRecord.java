/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author POS
 */
public class DBRecord {

    /**
     * Индификатор.
     */
    private int id;
    /**
     * Коллекция столбцов с записями.
     */

    private Map<DBRecordType, ArrayList<String>> columns;
    /**
     * Время занесения в БД (мс).
     */
    private long time;
    /**
     * Дата занесения в БД (dd.MM.yyyy HH:mm:ss).
     */
    private String date;

    public DBRecord(int id, Map<DBRecordType, ArrayList<String>> columns, long time, String date) {
        this.id = id;
        this.columns = columns;
        this.time = time;
        this.date = date;

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Record:");
        str.append(" Id =").append(getId()).append(" ");
        str.append("Name =").append(getId()).append(" ");
        str.append("Time =").append(getTime()).append(" ");
        str.append("Date =").append(getDate()).append(" ");

        return str.toString();

    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return null;
    }


    /**
     * Коллекция значений в определенном столбце.
     *
     * @param type
     * @return
     */
    public ArrayList<String> getValues(DBRecordType type) {
        return columns.get(type);
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }
}
