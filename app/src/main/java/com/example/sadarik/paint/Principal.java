package com.example.sadarik.paint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Principal extends Activity {

   private Vista v;

    /*************************************************/
    /*                   METODOS ON                */
    /**********************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = new Vista(this);
        setContentView(v);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pintar:
                v.setSeleccion(0);
                return true;
            case R.id.recta:
                v.setSeleccion(1);
                return true;
            case R.id.rectangulo:
                v.setSeleccion(2);
                return true;
            case R.id.circulo:
                v.setSeleccion(3);
                return true;
            case R.id.borrar:
                v.setSeleccion(4);
                return true;
            case R.id.color:
                v.color();
                return true;
            case R.id.nuevo:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.action_nuevo);
                alert.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                v.nuevo();
                            }
                        });
                alert.setNegativeButton(android.R.string.no, null);
                alert.show();
                return true;
            case R.id.action_guardar:
                guardar();
                return true;
            case R.id.action_selector:
                grosor("");
                return true;
            case R.id.action_relleno:
                v.relleno();
                return true;
            case R.id.action_cargar:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK &&
                requestCode == 1) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String s = cursor.getString(columnIndex);
            cursor.close();
            v.cargar(s);
        }
    }

    /*************************************************/
    /*            METODOS AUXILIARES                */
    /**********************************************/

    private void guardar() {
        File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath());
        File archivo = new File(carpeta + generaNombre());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(archivo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        v.getBitmap().compress(Bitmap.CompressFormat.PNG, 90, fos);
    }


    private void grosor(String dialogo) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        alert.setTitle(getString(R.string.grosor));
        final View vista = inflater.inflate(R.layout.dialogo, null);
        alert.setView(vista);
        final SeekBar barra = (SeekBar) vista.findViewById(R.id.seekBar);
        final Paint pincel = v.getPincel();
        final TextView grosor = (TextView) vista.findViewById(R.id.textView);
        barra.setMax(100);
        barra.setProgress((int) pincel.getStrokeWidth());
        barra.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                grosor.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                pincel.setStrokeWidth(barra.getProgress());
                v.setPincel(pincel);
            }
        });

        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }

    public String generaNombre(){
        String s = "imagen_";
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        String fecha = df.format(date);
        s= s+ fecha+".png";
        return s;
    }

}



