package prog3.proyecto.GPSApp;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class locationsList extends ListActivity 
{
   public static final String ROW_ID = "row_id"; // Intent extra key
   private ListView locationListView; // the ListActivity's ListView
   private CursorAdapter locationAdapter; // adapter for ListView
   
   // called when the activity is first created
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      locationListView = getListView(); 
      locationListView.setOnItemClickListener(viewContactListener);      

     
      String[] from = new String[] { "name" };
      int[] to = new int[] { R.id.locationTextView };
      locationAdapter = new SimpleCursorAdapter(
         locationsList.this, R.layout.location_list_item, null, from, to);
      setListAdapter(locationAdapter); 
   } 

   @Override
   protected void onResume() 
   {
      super.onResume();
      
   
       new GetLocationTask().execute((Object[]) null);
    } 

   @Override
   protected void onStop() 
   {
      Cursor cursor = locationAdapter.getCursor(); // get current Cursor
      
      if (cursor != null) 
         cursor.deactivate(); // deactivate it
      
      locationAdapter.changeCursor(null); // adapted now has no Cursor
      super.onStop();
   } // end method onStop

   // performs database query outside GUI thread
   private class GetLocationTask extends AsyncTask<Object, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(locationsList.this);

      // perform the database access
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();

         // get a cursor containing call contacts
         return databaseConnector.getAllLocations(); 
      } // end method doInBackground

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         locationAdapter.changeCursor(result); // set the adapter's Cursor
         databaseConnector.close();
      } // end method onPostExecute
   } // end class GetLocationTask
      
// create the Activity's menu from a menu resource XML file
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.locationslist_menu, menu);
      return true;
   } // end method onCreateOptionsMenu
   
   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      // create a new Intent to launch the AddEditContact Activity
      Intent addNewLocation = 
         new Intent(locationsList.this, AddEditLocation.class);
      startActivityForResult(addNewLocation,1); // start the AddEditContact Activity
      return super.onOptionsItemSelected(item); // call super's method
   } // end method onOptionsItemSelected

   OnItemClickListener viewContactListener = new OnItemClickListener() 
   {
      
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
         long arg3) 
      {
         // create an Intent to launch the ViewContact Activity
         Intent viewContact = 
            new Intent(locationsList.this, ViewLocation.class);
         
         // pass the selected contact's row ID as an extra with the Intent
         viewContact.putExtra(ROW_ID, arg3);
         startActivity(viewContact); // start the ViewContact Activity
      } // end method onItemClick
   }; // end viewContactListener
} // end class locationsList
