package cl.soshelp.www.rescuecaruser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EntreMaps extends ConexionMysqlHelper {
    Double lat = 0.0, lng = 0.0;
    Double latC = 0.0, lngC = 0.0;
    String ciudad, direccion, id_mob, mob_chofer, tipo_alert;
    TextView dir, tipo, nombreChofer, tServicio;
    Button cancelar, aceptar;
    ImageView internet;
    JSONObject jsonObject;
    JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entre_maps);

        dir = (TextView) findViewById(R.id.tvDireccion);
        cancelar = (Button) findViewById(R.id.btCancelar);
        aceptar = (Button) findViewById(R.id.btAceptar);
        tipo = (TextView) findViewById(R.id.tvTipo);
        tServicio = (TextView) findViewById(R.id.tvTServicio);
        nombreChofer = (TextView) findViewById(R.id.tvNombre);
        presentarDatos();
        agregarAlerta();
        agregaChofer();
        aceptar.setEnabled(false);

        cancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /******eliminacion de alerta ******/
                new CargarDatos().execute("http://www.webinfo.cl/soshelp/del_alerta.php?id_mob="+id_mob);
                Intent m = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(m);
            }
        });

        aceptar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /****** Ingreso segunda pagina ******/
                new CargarDatos().execute("http://www.webinfo.cl/soshelp/del_alerta.php?id_mob=" + id_mob);
                Intent s = new Intent(getApplicationContext(), Maps2Activity.class);
                s.putExtra("latGrua", latC);
                s.putExtra("longGrua", lngC);
                s.putExtra("latAuto", lat);
                s.putExtra("longAuto", lng);
                s.putExtra("mob_chofer", mob_chofer);
                s.putExtra("id_mob", id_mob);
                startActivity(s);
            }
        });
    }

    private void presentarDatos() {

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            lat = (Double) b.get("latitud");
            lng = (Double) b.get("longitud");
            ciudad = (String) b.get("ciudad");
            direccion = (String) b.get("direccion");
            id_mob = (String) b.get("id_mob");
            tipo_alert = (String) b.get("tipo");
            dir.setText(ciudad + ", " + direccion);
            presentarTipo(tipo_alert);

        }

    }

    private void presentarTipo(String tipo_alert) {

        switch(tipo_alert){
            case "m": tipo.setText("Solicitud de mecanico"); break;
            case "n": tipo.setText("Solicitud de cambio de neumatico o repuesto"); break;
            case "p": tipo.setText("Solicitud de Carabineros de Chile"); break;
            case "a": tipo.setText("Solicitud de ambulancia"); break;
            case "b": tipo.setText("Solicitud de Bomberos"); break;
            case "moto": tipo.setText("Solicitud de Grua para Motocicleta"); break;
            case "auto": tipo.setText("Solicitud de Grua para Automovil"); break;
            case "camta": tipo.setText("Solicitud de Grua para Automovil mayor"); break;
            case "camio": tipo.setText("Solicitud de Grua para vehículo mayor tonelada"); break;

        }

    }

    private void agregarAlerta() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /******Envio de datos a mysql ******/
                        new CargarDatos().execute("http://www.webinfo.cl/soshelp/ins_alert.php?id_mob=" + id_mob + "&lat=" + lat + "&lng=" + lng+ "&tipo=" +tipo_alert);

                    }
                });
            }
        }, 0);
    }

    public void agregaChofer() {
        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "http://www.webinfo.cl/soshelp/cons_chofer.php?id_mob=" + id_mob;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING = result;
            presentar();

        }
    }

    public void presentar() {
        if (JSON_STRING != null) {
            json_string = JSON_STRING;
            if (json_string.length() < 23) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    agregaChofer();
                                }
                            });
                        }
                    }, 5000);

            } else {
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        String tip, nombreCh;

                        JSONObject cho = jsonArray.getJSONObject(count);
                        mob_chofer = cho.getString("id_mob_driv");
                        nombreCh = cho.getString("nom_driv");
                        tip = cho.getString("tip_driv");
                        latC = Double.parseDouble(cho.getString("lat_driv"));
                        lngC = Double.parseDouble(cho.getString("lng_driv"));
                        nombreChofer.setText(nombreCh);
                        tServicio.setText(tip);
                        aceptar.setEnabled(true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

    }
}

//Toast.makeText(getApplicationContext(),"""",Toast.LENGTH_SHORT).show();