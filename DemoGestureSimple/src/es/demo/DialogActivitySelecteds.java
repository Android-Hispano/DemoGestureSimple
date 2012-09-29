package es.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import es.demo.pojos.Contacto;

public class DialogActivitySelecteds extends Activity{

	private static ArrayList<Contacto> lista;
	private static DialogArrayAdapter adapter;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_activity_selecteds);
		
		listView = (ListView)findViewById(R.id.listViewDialog);
		adapter = new DialogArrayAdapter(getApplicationContext(), lista);
		listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		listView.setAdapter(adapter);
		
		
		super.onCreate(savedInstanceState);
	}

	public static void setContactosSeleccionados(
			ArrayList<Contacto> listaSeleccionados) {
		lista = listaSeleccionados;		
	}
	
	
	
	// El Adapter
	public class DialogArrayAdapter extends ArrayAdapter<Contacto>  {

		private ArrayList<Contacto> mData;

		public DialogArrayAdapter(Context context, ArrayList<Contacto> contactos) {
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
			
			LayoutInflater inflater = LayoutInflater.from(DialogActivitySelecteds.this);
            convertView = inflater.inflate(R.layout.fila_contactos, null);
            holder = new ViewHolder();
            holder.imgContacto = (ImageView) convertView.findViewById(R.id.imgContacto);
            holder.tvnombre = (TextView) convertView.findViewById(R.id.nombreContacto);
			holder.tvemail = (TextView) convertView.findViewById(R.id.emailContacto);
			
			String cuenta = mData.get(position).getEmail();
			if(cuenta==null){cuenta="";}
			holder.tvnombre.setText("Nombre " + mData.get(position).getNombre());
			holder.tvemail.setText("Email: " + cuenta);
			MainActivity.fotoCach.loadImage(null, null, cuenta, holder.imgContacto);
			
			return convertView;
		}
    
    }

}
