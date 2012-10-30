package com.ketanolab.simidic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ketanolab.simidic.adapters.DownloadsListAdapter;
import com.ketanolab.simidic.util.Constants;
import com.ketanolab.simidic.util.Util;

public class DescargaActivity extends SherlockActivity implements OnItemClickListener {

	// URLs
	private ArrayList<String> urls;
	private ArrayList<String> fileNames;

	// List
	private ListView listView;
	private DownloadsListAdapter listAdapter;

	// Progress
	private ProgressDialog mProgressDialog;

	// Loading
	private RelativeLayout layoutCargando;
	private RelativeLayout layoutMensaje;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar_ForceOverflow);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descarga);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show home icon

		// ProgressDialog
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(R.string.downloading));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);

		// Loading
		layoutCargando = (RelativeLayout) findViewById(R.id.layoutCargando);
		layoutMensaje = (RelativeLayout) findViewById(R.id.layoutMensaje);
		layoutMensaje.setVisibility(View.GONE);

		// List
		listView = (ListView) findViewById(R.id.lista);
		listAdapter = new DownloadsListAdapter(this);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

		if (!Util.hayInternet(this)) {
			Util.showAlertNoInternet(this);
		} else {
			new LoadJSON().execute(Constants.URL_JSON);
		}
	}

	public class LoadJSON extends AsyncTask<String, String, Void> {

		@Override
		protected void onPreExecute() {
			listAdapter.eliminarTodo();
			urls = new ArrayList<String>();
			fileNames = new ArrayList<String>();
			layoutMensaje.setVisibility(View.GONE);
			layoutCargando.setVisibility(View.VISIBLE);
			
			//DescargaActivity.this.setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(String... values) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(Constants.URL_JSON);
			httpGet.setHeader("content-type", "application/json");
			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				String resultado = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
				JSONArray json = new JSONArray(resultado);
				int n = json.length();
				for (int i = 0; i < n; i++) {
					JSONObject obj = json.getJSONObject(i);

					String name = obj.getString("name");
					String author = obj.getString("author");
					String descripcion = obj.getString("description");
					String file = obj.getString("file");
					String url = obj.getString("url");
					String size = obj.getString("size");
					publishProgress(name, author, descripcion, file, url, size);
				}
			} catch (Exception ex) {
				Log.i(Constants.DEBUG, "Error: " + ex.toString());
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			if (!isDownloaded(values[3])) {
				fileNames.add(values[3]);
				urls.add(values[4]);
				listAdapter.adicionarItem(values[0], values[1], values[2], values[5]);
				listAdapter.notifyDataSetChanged();
			}
			Log.i(Constants.DEBUG, "Added " + values[0]);
		}

		@Override
		protected void onPostExecute(Void result) {
			listAdapter.notifyDataSetChanged();
			//Toast.makeText(DescargaActivity.this, R.string.load_completed, Toast.LENGTH_SHORT).show();
			// DescargaActivity.this.setSupportProgressBarIndeterminateVisibility(false);

			layoutCargando.setVisibility(View.GONE);
			if (listAdapter.getCount() == 0) {
				layoutMensaje.setVisibility(View.VISIBLE);
			}

		}
	}



	public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {

		Log.i(Constants.DEBUG, "Descargando... " + fileNames.get(posicion) + " " + urls.get(posicion));
		new DownloadFile().execute(urls.get(posicion), fileNames.get(posicion));
	}

	private class DownloadFile extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... args) {
			try {
				File directorio = new File(Constants.PATH_DICTIONARIES);
				if (!directorio.exists()) {
					directorio.mkdirs();
				}

				URL url = new URL(args[0]);
				URLConnection connection = url.openConnection();
				connection.connect();

				int fileLength = connection.getContentLength();
				Log.i("dic", ">> " + fileLength);

				// Download file
				InputStream input = new BufferedInputStream(url.openStream());
				Log.i("dic", ">> ");
				OutputStream output = new FileOutputStream(Constants.PATH_DICTIONARIES + args[1]);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {
				Log.i("dic", "Error al descargar: " + e.toString());
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
			mProgressDialog.setProgress(0);
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			mProgressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();

			// Update List
			if (!Util.hayInternet(DescargaActivity.this)) {
				Util.showAlertNoInternet(this);
			} else {
				listAdapter.eliminarTodo();
				new LoadJSON().execute(Constants.URL_JSON);
			}

		}
	}

	private boolean isDownloaded(String file) {
		File directory = new File(Constants.PATH_DICTIONARIES);
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].getName().equals(file)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// MENU
	// ****************************************************************************************************
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_download, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home || item.getItemId() == 0) {
			this.finish();
			return false;
		}
		switch (item.getItemId()) {
		case R.id.item_update:
			if (!Util.hayInternet(this)) {
				Util.showAlertNoInternet(this);
			} else {
				listAdapter.eliminarTodo();
				new LoadJSON().execute(Constants.URL_JSON);
			}
			break;

		}
		return true;
	}

}
