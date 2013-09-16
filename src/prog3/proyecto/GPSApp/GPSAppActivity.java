package prog3.proyecto.GPSApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.TextView;
import android.widget.Button;


public class GPSAppActivity extends Activity {
	
	private LocationManager locationManager; 
    private Location targetLocation; 
    private PowerManager.WakeLock wakeLock; 
    private boolean gpsFix;  
	private TextView gpsFixTextView;
	private TextView latitudeTextView;
	private TextView longitudeTextView;
	private TextView altitudeTextView;
	private TextView targetTextView;
	private TextView distanceTextView;
	private Button saveLocationButton;
	private Button selectLocationButton;
	private double latitude=0.0;
	private double longitude=0.0;
	private double altitude=0.0;
	private String locationName;
	private DatabaseConnector databaseConnector;
	private boolean targetSelected=false;
	
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    public void onStart() 
    {
       super.onStart(); 
       
       databaseConnector = new DatabaseConnector(this);
       databaseConnector.close();
       gpsFixTextView=(TextView) findViewById(R.id.textViewFix);
       latitudeTextView=(TextView) findViewById(R.id.textViewLatitude);
       longitudeTextView=(TextView) findViewById(R.id.textViewLongitude);
       altitudeTextView=(TextView) findViewById(R.id.textViewAltitude);
       targetTextView=(TextView) findViewById(R.id.textViewTarget);
       distanceTextView=(TextView) findViewById(R.id.textViewDistance);
       saveLocationButton=(Button) findViewById(R.id.buttonSave);
       saveLocationButton.setOnClickListener(saveLocationButtonClicked);
       selectLocationButton=(Button) findViewById(R.id.buttonSelect);
       selectLocationButton.setOnClickListener(selectLocationButtonClicked);
       
       Criteria criteria = new Criteria(); 
       criteria.setAccuracy(Criteria.ACCURACY_FINE); 
       criteria.setBearingRequired(true); 
       criteria.setCostAllowed(true); 
       criteria.setPowerRequirement(Criteria.POWER_LOW); 
       criteria.setAltitudeRequired(true);
       
       try{

       locationManager = 
          (LocationManager) getSystemService(LOCATION_SERVICE);
       
       locationManager.addGpsStatusListener(gpsStatusListener);
       
       String provider = locationManager.getBestProvider(criteria, true);

       locationManager.requestLocationUpdates(provider, 0, 0,
           locationListener);
      
       PowerManager powerManager = 
          (PowerManager) getSystemService(Context.POWER_SERVICE);
       
       wakeLock = powerManager.newWakeLock(
          PowerManager.PARTIAL_WAKE_LOCK, "No sleep");
       wakeLock.acquire(); 
       }catch( java.lang.IllegalArgumentException e ){
    	   
    	   new AlertDialog.Builder(GPSAppActivity.this)
    	       .setTitle("GPS No Disponible")
    	       .setMessage("Debe habilitar el posicionamiento GPS en su dispositivo antes de usar la aplicacion")
    	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int whichButton) {
    	            	gpsFixTextView.setText("No Disponible");
    	            }
    	       })
    	       .show();
    	       
    	       
       }

    }
    
    @Override
    protected void onResume() 
    {
       super.onResume(); 
       new LoadContactTask().execute();
        
     }
    
    private class LoadContactTask extends AsyncTask<Long, Object, Cursor> 
    {
       DatabaseConnector databaseConnectorTarget = 
          new DatabaseConnector(GPSAppActivity.this);

       @Override
       protected Cursor doInBackground(Long... params)
       {
          databaseConnectorTarget.open();
          
          return databaseConnectorTarget.getTarget();
       } 

       @Override
       protected void onPostExecute(Cursor result)
       {
          super.onPostExecute(result);
          if (result.getCount()>0) {

          result.moveToFirst(); 
    
          int nameIndex = result.getColumnIndex("name");
          int latitudeIndex = result.getColumnIndex("latitude");
          int longitudeIndex = result.getColumnIndex("longitude");
          int altitudeIndex = result.getColumnIndex("altitude");
          
          Criteria criteria = new Criteria(); 
          criteria.setAccuracy(Criteria.ACCURACY_FINE); 
          criteria.setBearingRequired(true); 
          criteria.setCostAllowed(true); 
          criteria.setPowerRequirement(Criteria.POWER_LOW); 
          criteria.setAltitudeRequired(true);

          locationManager = 
             (LocationManager) getSystemService(LOCATION_SERVICE);
          
          
          locationManager.addGpsStatusListener(gpsStatusListener);
          
          
          String provider = locationManager.getBestProvider(criteria, true);

          targetLocation=new Location(provider);
          targetLocation.setLatitude(Double.parseDouble(result.getString(latitudeIndex)));
          targetLocation.setLongitude(Double.parseDouble(result.getString(longitudeIndex)));
          targetLocation.setAltitude(Double.parseDouble(result.getString(altitudeIndex)));
          locationName=result.getString(nameIndex);
          
          targetSelected=true;
          targetTextView.setText(locationName);
          distanceTextView.setText("0 m");

          result.close(); // 
          databaseConnectorTarget.close(); 
          }
       } 
    } 
    
    private final LocationListener locationListener = 
    	      new LocationListener() 
    	   {
    	      
    	      public void onLocationChanged(Location location) 
    	      {
    	         gpsFix = true; 
    	         
    	         latitude=location.getLatitude();
    	         longitude=location.getLongitude();
    	         altitude=location.getAltitude();
    	         
    	         latitudeTextView.setText(String.format("%.04f", latitude)+"°");
    	         longitudeTextView.setText(String.format("%.04f", longitude)+"°");
    	         altitudeTextView.setText(String.format("%.02f", altitude)+" metros");
    	         
    	         if(targetSelected){
    	        	 float distance = (float)location.distanceTo(targetLocation);
    	        	 String units = "metros"; 
    	        	 if(distance>1000){
    	        		 distance /= 1000;
    	        		 units = "km";
    	        		 distanceTextView.setText(String.format("%.03f", distance)+" "+units);
    	        	 }
    	        	 else{
    	        		 distanceTextView.setText(Integer.toString((int)location.distanceTo(targetLocation))+" "+units);
    	        	 }
    	         }
    	         
    	         
    	      } 

    	      public void onProviderDisabled(String provider) 
    	      {
    	      } 

    	      public void onProviderEnabled(String provider) 
    	      {
    	      } 

    	      public void onStatusChanged(String provider, 
    	         int status, Bundle extras) 
    	      {
    	      }
    	   }; 
    
    
    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() 
    {
       public void onGpsStatusChanged(int event) 
       {
          if (event == GpsStatus.GPS_EVENT_FIRST_FIX) 
          {
             gpsFix = true;
             Toast results = Toast.makeText(GPSAppActivity.this, 
                getResources().getString(R.string.toast_signal_acquired), 
                Toast.LENGTH_SHORT);
             gpsFixTextView.setText("GPS Conectado!");
             
             
             results.setGravity(Gravity.CENTER, 
                results.getXOffset() / 2, results.getYOffset() / 2);     
             results.show(); 
          } 
       } 
    }; 
    
    OnClickListener saveLocationButtonClicked = new OnClickListener() 
    {
       
       public void onClick(View v) 
       {
    	   final EditText input = new EditText(GPSAppActivity.this);
    	   
    	   new AlertDialog.Builder(GPSAppActivity.this)
    	       .setTitle("Guardar Ubicación")
    	       .setMessage("Ingresa Un Nombre Para la Ubicacion Actual")
    	       .setView(input)
    	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int whichButton) {
    	                locationName = input.getText().toString(); 
    	                databaseConnector.open();
    	                databaseConnector.insertLocation(locationName, Double.toString(latitude), Double.toString(longitude), Double.toString(altitude)) ;
    	                databaseConnector.close();
    	                
    	            }
    	       })
    	       .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int whichButton) {
    	                  
    	            }
    	       }).show();
    	   
       } 
    }; 
    
    OnClickListener selectLocationButtonClicked = new OnClickListener() 
    {
       
       public void onClick(View v) 
       {
    	   Intent viewLocationList = new Intent(GPSAppActivity.this, locationsList.class);
    	         
    	         //viewLocationList.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    	         startActivity(viewLocationList);
    	   
       } 
    }; 
    
    @Override
    protected void onDestroy(){
    	databaseConnector.deleteTarget();
    	databaseConnector.close();
    	databaseConnector = null;
    	locationManager.removeUpdates(this.locationListener);
    	locationManager = null;
    	
    	super.onDestroy();
    }
}