package prog3.proyecto.GPSApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

public class ViewLocation extends Activity 
{
   private long rowID;
   private TextView nameTextView;  
   private TextView latitudeTextView; 
   private TextView longitudeTextView; 
   private TextView altitudeTextView; 
   private Button selectTargetButton;
   private DatabaseConnector databaseConnector;

   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.view_location);

      // get the EditTexts
      nameTextView = (TextView) findViewById(R.id.nameTextView);
      latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
      longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
      altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
      selectTargetButton=(Button) findViewById(R.id.buttonSelectTarget);
      selectTargetButton.setOnClickListener(selectButtonClicked);
      
      // get the selected contact's row ID
      Bundle extras = getIntent().getExtras();
      rowID = extras.getLong(locationsList.ROW_ID); 
   } // end method onCreate

   // called when the activity is first created
   @Override
   protected void onResume()
   {
      super.onResume();
      
      // create new LoadContactTask and execute it 
      new LoadContactTask().execute(rowID);
   } // end method onResume
   
   // performs database query outside GUI thread
   private class LoadContactTask extends AsyncTask<Long, Object, Cursor> 
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(ViewLocation.this);

      @Override
      protected Cursor doInBackground(Long... params)
      {
         databaseConnector.open();
         
         return databaseConnector.getOneLocation(params[0]);
      } 

      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
   
         result.moveToFirst(); 
   
         int nameIndex = result.getColumnIndex("name");
         int latitudeIndex = result.getColumnIndex("latitude");
         int longitudeIndex = result.getColumnIndex("longitude");
         int altitudeIndex = result.getColumnIndex("altitude");

         
         nameTextView.setText(result.getString(nameIndex));
         latitudeTextView.setText(result.getString(latitudeIndex));
         longitudeTextView.setText(result.getString(longitudeIndex));
         altitudeTextView.setText(result.getString(altitudeIndex));
   
         result.close(); 
         databaseConnector.close(); 
      } 
   } 
      
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.view_location_menu, menu);
      return true;
   } 
   

   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId()) 
      {
         case R.id.selectItem:
            
        	 Intent addEditContact =
             new Intent(this, AddEditLocation.class);
          
          addEditContact.putExtra(locationsList.ROW_ID, rowID);
          addEditContact.putExtra("name", nameTextView.getText());
          addEditContact.putExtra("latitude", latitudeTextView.getText());
          addEditContact.putExtra("longitude", longitudeTextView.getText());
          addEditContact.putExtra("altitude", altitudeTextView.getText());
          startActivity(addEditContact);
            return true;
         case R.id.deleteItem:
            deleteContact(); 
            return true;
         default:
            return super.onOptionsItemSelected(item);
      } 
   } 
   
   private void selectTarget(){
	 databaseConnector = new DatabaseConnector(this);
  	 databaseConnector.deleteTarget();
  	 databaseConnector.insertTarget(nameTextView.getText().toString(), latitudeTextView.getText().toString(), 
  			 longitudeTextView.getText().toString(), altitudeTextView.getText().toString()) ;
  	 databaseConnector.close();
  	Toast results = Toast.makeText(ViewLocation.this, 
            getResources().getString(R.string.toast_target_selected), 
            Toast.LENGTH_SHORT);
         
         results.setGravity(Gravity.CENTER, 
            results.getXOffset() / 2, results.getYOffset() / 2);     
         results.show(); 
  	 finish();
   }
   
   OnClickListener selectButtonClicked = new OnClickListener() 
   {
      
      public void onClick(View v) 
      {
    	  selectTarget();
   	   
      } 
   }; 
   
   private void deleteContact()
   {

      AlertDialog.Builder builder = 
         new AlertDialog.Builder(ViewLocation.this);

      builder.setTitle(R.string.confirmTitle); 
      builder.setMessage(R.string.confirmMessage); 


      builder.setPositiveButton(R.string.button_delete,
         new DialogInterface.OnClickListener()
         {
            
            public void onClick(DialogInterface dialog, int button)
            {
               final DatabaseConnector databaseConnector = 
                  new DatabaseConnector(ViewLocation.this);

               AsyncTask<Long, Object, Object> deleteTask =
                  new AsyncTask<Long, Object, Object>()
                  {
                     @Override
                     protected Object doInBackground(Long... params)
                     {
                        databaseConnector.deleteLocation(params[0]); 
                        return null;
                     } 

                     @Override
                     protected void onPostExecute(Object result)
                     {
                        finish(); 
                     } 
                  }; 

               deleteTask.execute(new Long[] { rowID });               
            } 
         } 
      ); 
      
      builder.setNegativeButton(R.string.button_cancel, null);
      builder.show();
   } 
} 