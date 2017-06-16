/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import utills.Time;

/**
 *
 * @author POS
 */
public class DBSession {

    private int id;
    private String name;
    private String type;
    private long time;
    private String date;
    private ArrayList<DBRecord> records;

    public DBSession(int id, String name, String type, long time, String date) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.time = time;
        this.date = date;
        records = new ArrayList<>();
    }

    public void addRecord(DBRecord record) {

        getRecords().add(record);

    }

    public int getId() {

        return id;

    }

    public String getType() {

        return type;

    }

    /*  public String getDate(){
        
     return date;
     }*/
    public Date getDate() {
        Date result = null;
        try {

            DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            result = df.parse(date);
        } catch (ParseException ex) {
            Logger.getLogger(DBSession.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int getRecordsSize() {

        return getRecords().size();

    }

    @Override
    public String toString() {

        return "Session: Id =" + id + " Name =" + getName() + " Work time =" + getTime() + " Data =" + getDate() + " Number of records: " + getRecords().size();

    }

    public String getSaveName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate());
        return getId() + ". " + new SimpleDateFormat("EEE, d MMM yyyy HH-mm-ss", new Locale("ru")).format(calendar.getTime()) + " '" + getName().replace(':', '-') + "'  (" + Time.formatMillisecondsToTime(getTime()).replace(':', '-') + ")";
    }

    public String getFullName() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate());
        return getId() + ". " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", new Locale("ru")).format(calendar.getTime()) + " '" + getName() + "'  (" + Time.formatMillisecondsToTime(getTime()) + ")";
    }

    public ArrayList<DBRecord> getRecords() {
        return records;
    }

    public void printRecords() {
        toString();
        for (DBRecord record : getRecords()) {
            System.out.println(record.toString());
        }

    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }
}
