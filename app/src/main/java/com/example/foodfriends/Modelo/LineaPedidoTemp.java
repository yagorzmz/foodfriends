package com.example.foodfriends.Modelo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class LineaPedidoTemp implements Parcelable {
    private String idProducto;
    private String nombreProducto;
    private Double precioProducto;
    private Integer unidades;
    // Constructor
    public LineaPedidoTemp(String idProducto, String nombreProducto, double precioProducto, int unidades) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.unidades = unidades;
    }
    public LineaPedidoTemp(String nombreProducto, double precioProducto, int unidades) {
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.unidades = unidades;
    }
    // Métodos Getter
    public String getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getPrecioProducto() {
        return precioProducto;
    }

    public int getUnidades() {
        return unidades;
    }
    protected LineaPedidoTemp(Parcel in) {
        idProducto = in.readString();
        nombreProducto = in.readString();
        precioProducto = in.readDouble();
        unidades = in.readInt();
    }

    public static final Creator<LineaPedidoTemp> CREATOR = new Creator<LineaPedidoTemp>() {
        @Override
        public LineaPedidoTemp createFromParcel(Parcel in) {
            return new LineaPedidoTemp(in);
        }

        @Override
        public LineaPedidoTemp[] newArray(int size) {
            return new LineaPedidoTemp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(idProducto);
        dest.writeString(nombreProducto);
        dest.writeDouble(precioProducto);
        dest.writeInt(unidades);
    }
    public double calcularPrecioTotal() {
        return getPrecioProducto() * getUnidades();
    }
}
