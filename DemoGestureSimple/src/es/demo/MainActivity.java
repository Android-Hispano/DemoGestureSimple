package es.demo;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.hispano.fotocach.ImageCache;
import android.hispano.fotocach.ImageCache.ImageCacheParams;
import android.hispano.fotocach.ImageFetcher;
import android.hispano.fotocach.utils.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

import es.demo.pojos.Contacto;
import es.demo.tasks.ContactsMapTask;


public class MainActivity extends SherlockFragmentActivity implements OnGesturePerformedListener {
	public static ImageFetcher fotoCach;
	public static final String IMAGE_CACHE_DIR = "photos";
	protected static Context ctx;
	public static MainActivity actividad;
	private ListView listView;
	private String nombre;
	private String email;
	private ArrayAdapterAgenda mAdapter;
	private ArrayList<Contacto> contactos;
	private String id;
	private ProgressDialog pd;
	private GestureLibrary gestureLib;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GestureOverlayView gestureOverlayView = new GestureOverlayView(this);
        View inflate = getLayoutInflater().inflate(R.layout.activity_main, null);
        gestureOverlayView.addView(inflate);
        gestureOverlayView.addOnGesturePerformedListener(this);
        gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if (!gestureLib.load()) {
          finish();
        }
        setContentView(gestureOverlayView);
      
        ActionBar action = getSupportActionBar();
        action.setDisplayShowTitleEnabled(false);
        
        actividad = this;
        ctx = this.getApplicationContext();
        
        listView = (ListView)findViewById(R.id.listViewContactos);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        
        // Progress Dialog para mostrar
        // al cargar las imágenes si hubieren
        // muchos contactos.
		pd = new ProgressDialog(actividad);
		pd.setTitle("");
		pd.setMessage("Cargando...");
		pd.setIndeterminate(true);
		pd.setCancelable(false);
		if(pd != null){
			pd.show();
		 }
        
		//Inicia la caché de memoria y disco para
		//las imágenes de los contactos
        if(fotoCach == null){
	        initFotoCach();
        }
        
        //Rellena la lista con contactos de la agenda
        populateContactList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }
    
    
	// El ArrayAdapter
	public class ArrayAdapterAgenda extends ArrayAdapter<Contacto>  {

		private ArrayList<Contacto> mData;

		public ArrayAdapterAgenda(Context context, ArrayList<Contacto> contactos) {
			super(context, R.layout.fila_contactos);
			this.mData = contactos;
		}
		
		@Override
		public Contacto getItem(int position) {
			return mData.get(position);
		}
		
		@Override
		public int getCount() {
			return mData.size();
		}


		class ViewHolder {
			protected ImageView imgContacto;
			protected TextView tvnombre;
			protected TextView tvemail;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			
			LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            convertView = inflater.inflate(R.layout.fila_contactos, null);
            holder = new ViewHolder();
            holder.imgContacto = (ImageView) convertView.findViewById(R.id.imgContacto);
            holder.tvnombre = (TextView) convertView.findViewById(R.id.nombreContacto);
			holder.tvemail = (TextView) convertView.findViewById(R.id.emailContacto);
			
			String cuenta = mData.get(position).getEmail();
			if(cuenta==null){cuenta="";}
			holder.tvnombre.setText("Nombre " + mData.get(position).getNombre());
			holder.tvemail.setText("Email: " + cuenta);
			fotoCach.loadImage(null, null, cuenta, holder.imgContacto);
			
			return convertView;
		}
    
    }
    
	private void initFotoCach() {
		ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
		
        cacheParams.memCacheSize = 1024 * 1024 * Utils.getMemoryClass(MainActivity.this) / 3;
        // Crea un Map<mail, idPhoto> con los contactos
        new ContactsMapTask().execute();
        fotoCach = new ImageFetcher(MainActivity.this);
        fotoCach.setLoadingImage(R.drawable.icon);
        fotoCach.setImageCache(ImageCache.findOrCreateCache(MainActivity.this, cacheParams));
	}
	
	
    // Rellena la lista de contactos //
    private void populateContactList() {
    			
        Cursor cursor = getContacts();
        if(cursor.getCount() > 0){
        	contactos = new ArrayList<Contacto>();
        	while(cursor.moveToNext()){
		        Contacto contacto = new Contacto();
        		id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        		contacto.setId(id);
		        nombre = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		        contacto.setNombre(nombre);
		        
		        Cursor cmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
	                     new String[] { ContactsContract.CommonDataKinds.Email.DATA,
	                         ContactsContract.CommonDataKinds.Email.TYPE},
	                   ContactsContract.CommonDataKinds.Email.CONTACT_ID + "='" + id + "'", null, null);
		        while(cmail.moveToNext()){
		        	email = cmail.getString(cmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
		        }
		        contacto.setEmail(email);
		        contactos.add(contacto);
		        email = null;
        	}
        }
        cursor.close();
        if(contactos!=null){
	        mAdapter = new ArrayAdapterAgenda(actividad, contactos);
	        listView.setAdapter(mAdapter);
        }
        pd.dismiss();
        pd = null;
	}

    // Recupera los Contactos //
	@SuppressWarnings("deprecation")
	private Cursor getContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };
        String selection = null;
//      String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        return actividad.managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}

	
	// Listener para los Gestos
	private ArrayList<Contacto> listaSeleccionados;
	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		 ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		    for (Prediction prediction : predictions) {
		      if (prediction.score > 1.0) {

		    	  listaSeleccionados = new ArrayList<Contacto>();
		    	  
		    	  int countChoice = listView.getCount();
					SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
						for(int i = 0;i < countChoice; i++){
							if(sparseBooleanArray.get(i) && mAdapter.getItem(i).getEmail() != null){
								Contacto contacto = new Contacto();
								contacto.setNombre(mAdapter.getItem(i).getNombre());
								contacto.setEmail(mAdapter.getItem(i).getEmail());
								contacto.setId(mAdapter.getItem(i).getId());
								listaSeleccionados.add(contacto);
							}
							listView.setItemChecked(i, false);
						}
						if(listaSeleccionados.size()>0){
							Intent i = new Intent(MainActivity.this, DialogActivitySelecteds.class);
							DialogActivitySelecteds.setContactosSeleccionados(listaSeleccionados);
							startActivity(i);
							listaSeleccionados = null;
					}
		      }
		    }
	  }
}
