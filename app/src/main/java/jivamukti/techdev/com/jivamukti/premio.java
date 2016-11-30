package jivamukti.techdev.com.jivamukti;

import android.graphics.Bitmap;

/**
 * Created by reyes on 12/7/15.
 */
public class premio {
    private String titulo;
    private String contenido;
    private String vencimiento;
    private Bitmap idImagen;
    private String url;

    public premio(String titulo, String contenido, String vencimiento,Bitmap idImagen,String url) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.vencimiento = vencimiento;
        this.idImagen = idImagen;
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Bitmap getIdImagen() {
        return idImagen;
    }

    public void setIdImagen(Bitmap idImagen) {
        this.idImagen = idImagen;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String vencimiento) {
        this.url = url;
    }
}
