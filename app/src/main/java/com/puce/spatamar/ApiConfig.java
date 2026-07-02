package com.puce.spatamar;

public class ApiConfig {

    public static final String BASE_URL = "https://spatamar-backend.onrender.com/api";

    public static final String URL_LOGIN = BASE_URL + "/usuarios/login";
    public static final String URL_REGISTRO = BASE_URL + "/usuarios";
    public static final String URL_USUARIOS = BASE_URL + "/usuarios";

    public static final String URL_SERVICIOS = BASE_URL + "/servicios";
    public static final String URL_SERVICIOS_ACTIVOS = BASE_URL + "/servicios/activos";

    public static final String URL_CITAS = BASE_URL + "/citas";
    public static final String URL_CITAS_FECHA = BASE_URL + "/citas/fecha/";
    public static final String URL_CITAS_CLIENTE = BASE_URL + "/citas/cliente/";
    public static final String URL_CITAS_USUARIO = BASE_URL + "/citas/usuario/";

    public static final String URL_CUENTAS_COBRAR = BASE_URL + "/cuentas-cobrar";
    public static final String URL_CUENTAS_COBRAR_USUARIO = BASE_URL + "/cuentas-cobrar/usuario/";

    public static final String URL_CUENTAS_PAGAR = BASE_URL + "/cuentas-pagar";

    public static final String URL_FINANZAS_RESUMEN = BASE_URL + "/finanzas/resumen";
    public static final String URL_FINANZAS_MOVIMIENTOS = BASE_URL + "/finanzas/movimientos";

    public static final String URL_SOLICITAR_RECUPERACION = BASE_URL + "/usuarios/solicitar-recuperacion";
    public static final String URL_RESTABLECER_CLAVE = BASE_URL + "/usuarios/restablecer-clave";
}