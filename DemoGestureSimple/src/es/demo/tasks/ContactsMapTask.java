package es.demo.tasks;

import java.util.HashMap;
import java.util.Map;

import es.demo.MainActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.hispano.fotocach.ImageWorker;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;

public class ContactsMapTask  extends AsyncTask<Void, Void, Map<String, String>>{

	@Override
	protected Map<String, String> doInBackground(Void... params) {
		String idContact;
        Uri uri = Contacts.CONTENT_URI;
        String[] projection = new String[] {
                Contacts._ID,
        };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        @SuppressWarnings("deprecation")
		Cursor cursor = ((Activity) MainActivity.actividad).managedQuery(uri, projection, selection, selectionArgs, sortOrder);

        ContentResolver cResolver = MainActivity.actividad.getContentResolver();
    	Map<String, String> mapa = new HashMap<String, String>();
        if(cursor.getCount() > 0){
        	while(cursor.moveToNext()){
        		idContact = cursor.getString(cursor.getColumnIndex(Contacts._ID));
        		Cursor cmail = cResolver.query(CommonDataKinds.Email.CONTENT_URI,
                        new String[] {CommonDataKinds.Email.DATA,
                            CommonDataKinds.Email.TYPE},
                            CommonDataKinds.Email.CONTACT_ID + "='" + idContact + "'", null, null);
		           while(cmail.moveToNext()){
		             String emailRecuperado = cmail.getString(cmail.getColumnIndex(CommonDataKinds.Email.DATA));
		             mapa.put(emailRecuperado, idContact);
             	}
		         cmail.close();
   	          }
        }
        cursor.close();
		return mapa;
	}

	@Override
	protected void onPostExecute(Map<String, String> result) {
		ImageWorker.setContactsMap(result);
		super.onPostExecute(result);
	}

	
	
}
