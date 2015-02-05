package com.example.sadarik.paint;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;


import java.io.File;


/**
 * Created by Sadarik on 03/02/2015.
 */

public class Vista extends View implements ColorPicker.OnColorChangedListener{

    private Paint pincel;
    private int alto, ancho;
    private Bitmap bitmap;
    private Canvas lienzoFondo;
    private double radio = 0;
    private int color = Color.BLACK;
    private float x0 = 0, y0 = 0, xi = 0, yi = 0;
    private Path rectaPoligonal = new Path();
    private int grosor = 7;
    int contador =0;
    private int seleccion;

    public Vista(Context context) {
        super(context);
        pincel = new Paint();
        pincel.setStrokeWidth(grosor);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setAntiAlias(true);
    }

    /*************************************************/
    /*              METODOS DIBUJAR                 */
    /**********************************************/

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        switch (seleccion) {
            case 0://Lapiz
                pincel.setColor(color);
            //    pincel.setStrokeWidth(grosor);
                canvas.drawPath(rectaPoligonal, pincel);
                break;
            case 1://Linea recta
                pincel.setColor(color);
            //    pincel.setStrokeWidth(grosor);
                canvas.drawLine(x0, y0, xi, yi, pincel);
                break;
            case 2:
                //Rectangulo
                pincel.setColor(color);
              //  pincel.setStrokeWidth(grosor);
                float xorigen = Math.min(x0, xi);
                float xdestino = Math.max(x0, xi);
                float yorigen = Math.min(y0, yi);
                float ydestino = Math.max(y0, yi);
                canvas.drawRect(xorigen, yorigen, xdestino, ydestino, pincel);
                break;

            case 3:
                //Circulo
                pincel.setColor(color);
            //    pincel.setStrokeWidth(grosor);
                canvas.drawCircle(x0, y0, (float) radio, pincel);
                break;
            case 4://Borrar
                pincel.setColor(Color.WHITE);
                canvas.drawPath(rectaPoligonal, pincel);
                break;

        }
    }


    /*************************************************/
    /*            METODOS PRESIONAR                 */
    /**********************************************/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (seleccion) {
                    case 4:
                    case 0://Lapiz
                        x0 = xi = event.getX();
                        y0 = yi = event.getY();
                        rectaPoligonal.reset();
                        rectaPoligonal.moveTo(x0, y0);
                        break;
                    case 1://Recta
                        x0 = x;
                        y0 = y;
                        break;
                    case 2://Circulo
                        x0 = x;
                        y0 = y;
                        break;
                    case 3://Rectangulo
                        x0 = x;
                        y0 = y;
                        break;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                switch (seleccion) {
                    case 4:
                    case 0://Lapiz
                        rectaPoligonal.quadTo(xi, yi, (x + xi) / 2, (y + yi) / 2);
                        xi = x;
                        yi = y;
                        x0 = xi;
                        y0 = yi;
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        invalidate();
                        break;
                    case 1://Recta
                        xi = x;
                        yi = y;
                        invalidate();
                        break;
                    case 2:
                        //Rectangulo
                        xi = x;
                        yi = y;
                        invalidate();
                        break;
                    case 3:
                        //Circulo
                        xi = x;
                        yi = y;
                        radio = Math.sqrt(Math.pow((xi - x0), 2) + Math.pow((yi - y0), 2));
                        invalidate();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (seleccion) {
                    case 4:
                    case 0://Lapiz
                        xi = x;
                        yi = y;
                        lienzoFondo.drawPath(rectaPoligonal, pincel);
                        x0 = y0 = xi = yi = -1;
                        break;
                    case 1://Recta
                        lienzoFondo.drawLine(x0, y0, xi, yi, pincel);
                        break;
                    case 2:
                        //Rectangulo
                        lienzoFondo.drawRect(x0, y0, xi, yi, pincel);
                        break;
                    case 3://Circulo
                        xi = x;
                        yi = y;
                        radio = Math.sqrt(Math.pow((x0 - xi), 2) + Math.pow((y0 - yi), 2));
                        lienzoFondo.drawCircle(x0, y0, (float) radio, pincel);
                        break;

                }
                invalidate();
                break;
        }
        return true;
    }


    /*************************************************/
    /*          METODOS AUXILIARES                 */
    /**********************************************/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(bitmap);
        alto = h;
        ancho = w;
        bitmap.eraseColor(Color.WHITE);
    }

    public void color() {
        new ColorPicker(this.getContext(), Vista.this, Color.BLACK).show();
    }


    @Override
    public void colorChanged(int color) {
        this.color = color;
    }

    public void setSeleccion(int seleccion) {
        this.seleccion = seleccion;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public Paint getPincel() {
        return pincel;
    }

    public void setPincel(Paint pincel) {
        this.pincel = pincel;
    }


    public void cargar(String s) {
        File archivo = new File(s);
        bitmap = Bitmap.createBitmap(ancho, alto,
                Bitmap.Config.ARGB_8888);
        if (archivo.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bitmap = BitmapFactory.decodeFile(
                    archivo.getAbsolutePath(), options);
        }
        lienzoFondo = new Canvas(bitmap);
        invalidate();
    }

    public void nuevo() {
        bitmap = Bitmap.createBitmap(ancho, alto,
                Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(bitmap);
        lienzoFondo.drawRGB(255, 255, 255);
        invalidate();
    }

    public void relleno(){
        contador++;
        if(contador %2!=0) {
            pincel.setStyle(Paint.Style.FILL);
        }else{
            pincel.setStyle(Paint.Style.STROKE);
        }
    }

}

