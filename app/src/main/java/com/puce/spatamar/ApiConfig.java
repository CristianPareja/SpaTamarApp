package com.puce.spatamar;

public class ApiConfig {

    public static final String BASE_URL = "http://10.0.2.2:3000/api";

    public static final String URL_LOGIN = BASE_URL + "/usuarios/login";
    public static final String URL_REGISTRO = BASE_URL + "/usuarios";

    public static final String URL_SERVICIOS = BASE_URL + "/servicios";
    public static final String URL_SERVICIOS_ACTIVOS = BASE_URL + "/servicios/activos";

    public static final String URL_CITAS = BASE_URL + "/citas";
    public static final String URL_CITAS_FECHA = BASE_URL + "/citas/fecha/";
    public static final String URL_CITAS_CLIENTE = BASE_URL + "/citas/cliente/";
    public static final String URL_CITAS_USUARIO = BASE_URL + "/citas/usuario/";
}