package com.puce.spatamar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraficoPastelServiciosView extends View {

    private Paint paintPastel;
    private Paint paintTexto;
    private ArrayList<ItemPastel> datos;

    private int[] colores = {
            0xFF185ABC,
            0xFF2E7D32,
            0xFF6A4BC3,
            0xFFF57C00,
            0xFFC62828,
            0xFF0097A7
    };

    public GraficoPastelServiciosView(Context context) {
        super(context);
        inicializar();
    }

    public GraficoPastelServiciosView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    public GraficoPastelServiciosView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inicializar();
    }

    private void inicializar() {
        paintPastel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPastel.setStyle(Paint.Style.FILL);

        paintTexto = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTexto.setColor(0xFF102A43);
        paintTexto.setTextSize(dp(12));
        paintTexto.setStyle(Paint.Style.FILL);

        datos = new ArrayList<>();
    }

    public void setDatos(ArrayList<ItemPastel> nuevosDatos) {
        datos.clear();

        if (nuevosDatos != null) {
            datos.addAll(nuevosDatos);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (datos.isEmpty()) {
            dibujarSinDatos(canvas);
            return;
        }

        float total = 0;

        for (ItemPastel item : datos) {
            total += item.getValor();
        }

        if (total <= 0) {
            dibujarSinDatos(canvas);
            return;
        }

        int ancho = getWidth();

        int tamanoGrafico = dp(145);
        int izquierda = (ancho - tamanoGrafico) / 2;
        int arriba = dp(8);

        RectF rectF = new RectF(
                izquierda,
                arriba,
                izquierda + tamanoGrafico,
                arriba + tamanoGrafico
        );

        float anguloInicio = -90;

        for (int i = 0; i < datos.size(); i++) {
            ItemPastel item = datos.get(i);
            float angulo = (item.getValor() / total) * 360;

            paintPastel.setColor(colores[i % colores.length]);
            canvas.drawArc(rectF, anguloInicio, angulo, true, paintPastel);

            anguloInicio += angulo;
        }

        dibujarLeyenda(canvas, total, arriba + tamanoGrafico + dp(25));
    }

    private void dibujarLeyenda(Canvas canvas, float total, int inicioY) {
        int xColor = dp(18);
        int xTexto = dp(42);
        int y = inicioY;

        paintTexto.setTextSize(dp(11));
        paintTexto.setColor(0xFF102A43);

        for (int i = 0; i < datos.size(); i++) {
            ItemPastel item = datos.get(i);

            paintPastel.setColor(colores[i % colores.length]);
            canvas.drawCircle(xColor, y - dp(5), dp(6), paintPastel);

            float porcentaje = (item.getValor() / total) * 100;

            String texto = item.getNombre()
                    + " - $"
                    + String.format("%.2f", item.getValor())
                    + " ("
                    + String.format("%.0f", porcentaje)
                    + "%)";

            canvas.drawText(texto, xTexto, y, paintTexto);

            y += dp(22);
        }
    }

    private void dibujarSinDatos(Canvas canvas) {
        paintTexto.setColor(0xFF6B7280);
        paintTexto.setTextSize(dp(14));
        canvas.drawText("Sin datos para graficar", dp(24), getHeight() / 2, paintTexto);
        paintTexto.setColor(0xFF102A43);
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    public static class ItemPastel {

        private String nombre;
        private float valor;

        public ItemPastel(String nombre, float valor) {
            this.nombre = nombre;
            this.valor = valor;
        }

        public String getNombre() {
            return nombre;
        }

        public float getValor() {
            return valor;
        }
    }
}