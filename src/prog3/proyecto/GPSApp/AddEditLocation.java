package prog3.proyecto.GPSApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEditLocation extends Activity 
{
   private long rowID; // id of contact being edited, if any
   
   // EditTexts for contact information
   private EditText nameEditText;
   private EditText latitudeEditText;
   private EditText longitudeEditText;
   private EditText altitudeEditText;

   
   // called when the Activity is first started
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call super's onCreate
      setContentView(R.layout.add_location); // inflate the UI

      nameEditText = (EditText) findViewById(R.id.nameEditText);
      
      latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
      longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
      altitudeEditText = (EditText) findViewById(R.id.altitudeEditText);

      
      Bundle extras = getIntent().getExtras(); // get Bundle of extras

      // if there are extras, use them to populate the EditTexts
      if (extras != null)
      {
         rowID = extras.getLong("row_id");
         nameEditText.setText(extras.getString("name"));  
           
         latitudeEditText.setText(extras.getString("latitude"));  
         longitudeEditText.setText(extras.getString("longitude"));
         altitudeEditText.setText(extras.getString("altitude"));    
      } // end if
      
      // set event listener for the Save Contact Button
      Button saveContactButton = 
         (Button) findViewById(R.id.saveContactButton);
      saveContactButton.setOnClickListener(saveContactButtonClicked);
   } // end method onCreate

// responds to event generated when user clicks the Done Button
   OnClickListener saveContactButtonClicked = new OnClickListener() 
   {
      
      public void onClick(View v) 
      {
         if (nameEditText.getText().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveContactTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveContact(); // save contact to the database
                     return null;
                  } // end method doInBackground
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     finish(); // return to the previous Activity
                  } // end method onPostExecute
               }; // end AsyncTask
               
            // save the contact to the database using a separate thread
            saveContactTask.execute((Object[]) null); 
         } // end if
         else
         {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(AddEditLocation.this);
      
            // set dialog title & message, and provide Button to dismiss
            builder.setTitle(R.string.errorTitle); 
            builder.setMessage(R.string.errorMessage);
            builder.setPositiveButton(R.string.errorButton, null); 
            builder.show(); // display the Dialog
         } // end else
      } // end method onClick
   }; // end OnClickListener saveContactButtonClicked
   

   // saves contact information to the database
   private void saveContact() 
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = new DatabaseConnector(this);

      if (getIntent().getExtras() == null)
      {
         // insert the contact information into the database
         databaseConnector.insertLocation(
            nameEditText.getText().toString(),
            longitudeEditText.getText().toString(), 
            latitudeEditText.getText().toString(), 
            altitudeEditText.getText().toString());
      } // end if
      else
      {
         databaseConnector.updateLocation(rowID,
            nameEditText.getText().toString(),
            longitudeEditText.getText().toString(), 
            latitudeEditText.getText().toString(), 
            altitudeEditText.getText().toString());
      } // end else
   } 
} 
