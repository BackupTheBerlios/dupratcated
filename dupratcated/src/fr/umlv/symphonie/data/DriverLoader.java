package fr.umlv.symphonie.data;


public class DriverLoader {
    
    private static boolean driversLoaded = false;
    
    private DriverLoader(){
    }
    
    public static void loadDrivers(){
        
        if (driversLoaded == false) {
            
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            
            
            driversLoaded = true;
        }
    }

}

