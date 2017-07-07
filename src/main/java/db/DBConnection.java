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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author POS
 */
public class DBConnection {

    private static Connection connection = null;

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

                    //     sessionsStatement = connection.createStatement();
                    //      recordsStatement = connection.createStatement();
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
                Statement statement = connection.createStatement();
                //   sessionsStatement = connection.createStatement();
                //   recordsStatement = connection.createStatement();
                // Убираем журнал (лишние файлы)
                statement.execute("PRAGMA journal_mode = OFF");
                statement.close();
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
    ArrayList<DBSession> sessions;

    public ArrayList<DBSession> getSessions() {
        sessions = new ArrayList<>();
        ResultSet recordsSizeResultSet;
        ResultSet sessionsResultSet;

        try {
            Statement sessionsStatement = connection.createStatement();
            Statement recordsStatement = connection.createStatement();
            sessionsResultSet = sessionsStatement.executeQuery("SELECT * FROM sessions");
           
            while (sessionsResultSet.next()) {

                // Получаем кол-во записей в сессии
                recordsSizeResultSet = recordsStatement.executeQuery("SELECT COUNT(*) FROM records WHERE session_id =" + sessionsResultSet.getInt("id"));
                recordsSizeResultSet.next();
            
                DBSession session = new DBSession(
                        sessionsResultSet.getInt("id"),
                        sessionsResultSet.getString("type"),
                        sessionsResultSet.getString("name"),
                        sessionsResultSet.getLong("time"),
                        sessionsResultSet.getString("date"),
                        recordsSizeResultSet.getInt(1));
                sessions.add(session);
            }

            recordsStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sessions;
    }

    public void uploadRecords(DBSession session) {
        try {
            ResultSet recordsResultSet;
            Statement statement = connection.createStatement();
            recordsResultSet = statement.executeQuery("SELECT * FROM records WHERE session_id = " + session.getId());
            while (recordsResultSet.next()) {

                // Коллекция значений.
                Map<DBRecordType, String[]> values = new HashMap<>();
                DBRecordType[] types = DBRecordType.values();
                // Считываем все значения из БД.
                for (DBRecordType type : types) {
                    // Конвертируем их в массив.
                    values.put(type, stringToArray(recordsResultSet.getString(type.toString())));
                }
                // Создаем запись.
                DBRecord record = new DBRecord(recordsResultSet.getInt("id"), values, recordsResultSet.getLong("time"), recordsResultSet.getString("date"));
                // Добавляем запись к сессии к которой он относится.
                session.addRecord(record);
            }
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    // Перевод строки в массив
    Pattern p = Pattern.compile("[\\[\\]]");
    Matcher m = p.matcher("");

    /**
     * Конвертация записи в массив.
     *
     * @param stringData
     * @return
     */
    public String[] stringToArray(String stringData) {
        return m.reset(stringData).replaceAll("").split(",");
    }

    public void close() {

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
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM sessions WHERE id = '" + index + "'");
            statement.execute("DELETE FROM records WHERE session_id = '" + index + "'");
            statement.execute("VACUUM");
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
