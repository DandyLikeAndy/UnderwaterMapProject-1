package db;

public class TestDb {
    public static void main(String[] args) {
        DBService service = new DBService("C:\\Users\\User\\Desktop\\database\\auv_database.db");
        if (service.init()){
            System.out.println(service.getSessions().size());
        }
    }
}
