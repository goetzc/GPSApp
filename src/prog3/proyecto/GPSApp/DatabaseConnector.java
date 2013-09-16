package prog3.proyecto.GPSApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "savedLocations";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new contact in the database
   public void insertLocation(String name, String latitude, String longitude, 
      String altitude) 
   {
      ContentValues newLocation = new ContentValues();
      newLocation.put("name", name);
      newLocation.put("latitude", latitude);
      newLocation.put("longitude", longitude);
      newLocation.put("altitude", altitude);
      

      open(); // open the database
      database.insert("locations", null, newLocation);
      close(); // close the database
   } // end method insertLocation
   
   public void insertTarget(String name, String latitude, String longitude, 
		      String altitude) 
		   {
		      ContentValues newLocation = new ContentValues();
		      newLocation.put("name", name);
		      newLocation.put("latitude", latitude);
		      newLocation.put("longitude", longitude);
		      newLocation.put("altitude", altitude);
		      

		      open(); // open the database
		      database.insert("target", null, newLocation);
		      close(); // close the database
		   } // end method insertLocation

   // inserts a new contact in the database
   public void updateLocation(long id, String name, String latitude, 
      String longitude, String altitude) 
   {
      ContentValues editLocation = new ContentValues();
      editLocation.put("name", name);
      editLocation.put("latitude", latitude);
      editLocation.put("longitude", longitude);
      editLocation.put("altitude", altitude);
      

      open(); // open the database
      database.update("locations", editLocation, "_id=" + id, null);
      close(); // close the database
   } // end method updateLocation

   // return a Cursor with all contact information in the database
   public Cursor getAllLocations() 
   {
      return database.query("locations", new String[] {"_id", "name"}, 
         null, null, null, null, "name");
   } // end method getAlllocations

   // get a Cursor containing all information about the contact specified

   public Cursor getOneLocation(long id) 
   {
      return database.query(
         "locations", null, "_id=" + id, null, null, null, null);
   } 
   
   //metodo para obtener la posicion que se eligio de la lista
   public Cursor getTarget() 
   {
      return database.query(
         "target", new String[] {"_id", "name","latitude","longitude","altitude"}, null, null, null, null, null);
   } 

   public void deleteTarget(){
	   open();
	   database.delete("target", null, null);
	   close();
   }
   
   public void deleteLocation(long id) 
   {
      open();
      database.delete("locations", "_id=" + id, null);
      close(); 
   } 
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String name,
         CursorFactory factory, int version) 
      {
         super(context, name, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the locations table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table named locations
         String createQuery = "CREATE TABLE locations" +
            "(_id integer primary key autoincrement," +
            "name TEXT, latitude TEXT, longitude TEXT," +
            "altitude TEXT);";
         
         String createQueryTable = "CREATE TABLE target" +
                 "(_id integer primary key autoincrement," +
                 "name TEXT, latitude TEXT, longitude TEXT," +
                 "altitude TEXT);";
         db.execSQL(createQuery);
         db.execSQL(createQueryTable);
      } 

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      } // end method onUpgrade
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector


/**************************************************************************
 * Modificado por Mario Celi para usar base de datos con GPSApp			  *
 * 																		  *
 * 																		  *
 * (C) Copyright 1992-2012 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 **************************************************************************/
