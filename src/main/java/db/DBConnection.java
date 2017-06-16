/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author POS
 */
public class DBConnection {

    private static Connection connection = null;
    private static Statement statmt = null;

    //private ResultSet result = null;
    private String path = null;

    public DBConnection(String path) {
        this.path = path;

    }

    public boolean connect() {

        if (connection != null) {
            try {
                if (!connection.isClosed()) {

                    return true;

                } else {
                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                    statmt = connection.createStatement();

                    return true;
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        } else {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
                statmt = connection.createStatement();
                // Убираем журнал (лишние файлы)
                statmt.execute("PRAGMA journal_mode = OFF");
                return true;
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

        }

    }

    public boolean isClosed() {

        try {
            return connection.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public ArrayList<DBSession> getSessions() {

        ArrayList<DBSession> sessions = new ArrayList<>();
        ResultSet result;
        try {
            result = statmt.executeQuery("SELECT * FROM sessions");
            while (result.next()) {
                DBSession session = new DBSession(result.getInt("id"), result.getString("type"), result.getString("name"), result.getLong("time"), result.getString("date"));
                sessions.add(session);
            }
            result = statmt.executeQuery("SELECT * FROM records");

            long t = System.nanoTime();

            while (result.next()) {

                // Коллекция значений.
                Map<DBRecordType, ArrayList<String>> values = new HashMap<>();
                DBRecordType[] types = DBRecordType.values();
                // Считываем все значения из БД.

                for (DBRecordType type : types) {
                    // Коневертируем их в массив.
                    values.put(type, stringToArray(result.getString(type.toString())));
                }
                // Создаем запись.
                DBRecord record = new DBRecord(result.getInt("id"), values, result.getLong("time"), result.getString("date"));
                // System.out.println(record.getId());
                // Добавляем запись к сессии к которой он относится.

                for (DBSession session : sessions) {
                    if (session.getId() == result.getInt("session_id")) {

                        session.addRecord(record);
                    }

                }
            }
            System.out.println("All time: " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - t, TimeUnit.NANOSECONDS));

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);

        }

        // close();
        return sessions;
    }
    StringTokenizer tokens;

    // Перевод строки в массив
    public ArrayList<String> stringToArray(String valuesString) {

        tokens = new StringTokenizer(valuesString.replaceAll("[\\[\\]]", ""), ",");

        ArrayList<String> valuesArray = new ArrayList<>();
        while (tokens.hasMoreElements()) {
            valuesArray.add(tokens.nextElement().toString());
        }

        return valuesArray;

    }

    public void close() {
        try {
            statmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Функция удаления сессии в БД.
     *
     * @param index номер сессии.
     */
    public void deleteSession(int index) {
        try {

            statmt.execute("DELETE FROM sessions WHERE id = '" + index + "'");
            statmt.execute("DELETE FROM records WHERE session_id = '" + index + "'");
            statmt.execute("VACUUM");
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
