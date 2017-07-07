/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author POS
 */
public class DBService {

    ArrayList<DBSession> sessions;
    DBConnection database;
    String path;

    public DBService(String path) {
        this.path = path;
        this.sessions = new ArrayList<>();
    }

    public boolean init() {
        database = new DBConnection(path);
        if (database.connect()) {
            sessions.clear();
            sessions = database.getSessions();
            return true;
        } else {
            return false;
        }

    }
    
    public void close(){
        
        database.close();
        
    }
    

    public ArrayList<DBSession> getSessions() {

        return sessions;
    }

    public long getDBSize() {
        return new File(path).length();
    }

    public void deleteSession(int index) {
      
        database.deleteSession(index);
    }
    public void uploadRecords(DBSession session){
        
        database.uploadRecords(session);
    }
    

    public int getRecordsNumber() {
        int records = 0;
        for (DBSession session : sessions) {
            records += session.getRecords().size();
        }

        return records;
    }

    public int getSessionsNumber() {
        return sessions.size();
    }

}
