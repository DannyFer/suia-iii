package ec.gob.ambiente.rcoa.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

public final class NumeroTextoUtil {
	
	private static final Format FORMATO_ESTANDAR = new SimpleDateFormat(FormatoFechaEnum.DD_MM_YYYY.toString());
    private static final long UNO = 1;

    private NumeroTextoUtil() {
    }

    /**
     * Metodo generico para determinar si un objecto es nulo
     *
     * @param object Objeto a validar
     * @return
     */
    public static boolean esNulo(Object object) {
        return object == null;
    }

    /**
     * Metodo generico para determinar si un String, Lista, etc esta vacio
     *
     * @param object Objeto a validar
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean esVacio(Object object) {
        if (esNulo(object)) {
            return true;
        } else if (object instanceof String) {
            if (((String) object).trim().equals("")) {
                return true;
            }
        } else if (object instanceof List) {
            List<Object> lista = (List<Object>) object;
            if (lista.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida si el numero ingresado es un entero mayor que 0
     *
     * @param object Objeto a validar
     * @return
     */
    public static boolean esEnteroMayorQueCero(Object object) {
        if (esNulo(object)) {
            return false;
        }
        if (object instanceof Number) {
            Number number = (Number) object;
            if (number.longValue() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida si el numero ingresado es un entero mayor que 1
     *
     * @param object Objeto a validar
     * @return
     */
    public static boolean esEnteroMayorQueUno(Object object) {
        if (esNulo(object)) {
            return false;
        }
        if (object instanceof Number) {
            Number number = (Number) object;
            if (number.longValue() > UNO) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida si el numero ingresado es un entero mayor o igual que 0
     *
     * @param object Objeto a validar
     * @return
     */
    public static boolean esEnteroMayorIgualQueCero(Object object) {
        if (esNulo(object)) {
            return false;
        }
        if (object instanceof Number) {
            Number number = (Number) object;
            if (number.intValue() >= 0) {
                return true;
            }
        }
        return false;
    }

    public static Integer anioActual() {
        Calendar calendario = Calendar.getInstance();
        return calendario.get(Calendar.YEAR);
    }

    public static Integer mesActual() {
        Calendar calendario = Calendar.getInstance();
        return calendario.get(Calendar.MONTH) + 1;
    }

    /**
     * Obtiene la fecha en el formato enviado
     *
     * @param formato Formato de fecha
     * @return
     */
    public static String getFechaActual(String formato) {
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat(formato);
        return formateador.format(ahora);
    }

    public static Boolean esEntero(Object object) {
        if (esNulo(object)) {
            return false;
        }
        if (object instanceof Number) {
            Number number = (Number) object;
            if (number.intValue() > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Metodo que sirve para restar dos valores si el resultado es negativo
     * devuelve cero.
     *
     * @param minuendo Minuendo
     * @param sustraendo Sustraendo
     * @return resta
     */
    public static double restarValores(double minuendo, double sustraendo) {
        double diferencia = 0;
        if (minuendo >= sustraendo) {
            diferencia = minuendo - sustraendo;
        } else {
            diferencia = 0;
        }
        return diferencia;
    }

    /**
     * Metodo para mostrar en formato 0.00 los numeros
     *
     * @param numero Número a formatear
     * @return
     */
    public static String formatearNumero(BigDecimal numero) {
        DecimalFormat myFormatter = formateoNumeroDecimal();
        if (numero.compareTo(BigDecimal.ZERO) == 0) {
            return numero.toString();
        } else {
            return myFormatter.format(numero.setScale(2, RoundingMode.HALF_UP));
        }
    }

    public static String formatearNumeroPlantillas(BigDecimal numero) {
        DecimalFormat myFormatter = formateoNumeroDecimalPlantilla();
        return myFormatter.format(numero);
    }

    private static DecimalFormat formateoNumeroDecimalPlantilla() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        return new DecimalFormat("########0.00", simbolos);
    }

    private static DecimalFormat formateoNumeroDecimal() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        simbolos.setGroupingSeparator(',');
        return new DecimalFormat("###,###,##0.00", simbolos);
    }

    /**
     * Cambiar texto a estilo CamelCase ejemplo: Hola mundo ==> holaMundo;
     *
     * @param s Cadena que se desea cambiar
     * @return
     */
    public static String toCamelCase(String s) {
        String[] parts = s.split(" ");
        StringBuffer camelCaseString = new StringBuffer();
        for (String part : parts) {
            camelCaseString = camelCaseString.append(toProperCase(part));
        }
        return camelCaseString.toString().substring(0, 1).toLowerCase() + camelCaseString.toString().substring(1);
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     * * Agrega dias a una fecha determinada.
     *
     * @param fecha Fecha a la que se desea agregar días
     * @param numeroDias Número de días a sumar
     * @return
     */
    public static Date agregarDiasFecha(Date fecha, int numeroDias) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.add(Calendar.DATE, numeroDias);
        return cal.getTime();
    }

    /**
     * Retorna el valor del BooleanoEnum en base al valor booleano
     *
     * @param valor Valor booleano
     * @return
     */
    /*public static SiNoEnum retornarSN(boolean valor) {
        return valor ? SiNoEnum.S : SiNoEnum.N;
    }*/

    /**
     * Convierte un date en texto de acuerdo al formato enviado
     *
     * @param fecha Fecha a convertir
     * @param formato Formato de fecha
     * @return
     */
    public static String convertirDateAString(Date fecha, String formato) {
        SimpleDateFormat sdf = new SimpleDateFormat(formato);
        return sdf.format(fecha);
    }

    /**
     * Convierte un texto a un objeto Date de acuerdo al formato enviado
     *
     * @author Christian Inapanta
     * @since 07/09/2018
     * @param fecha Texto con la fecha a convertir
     * @param formatoOrigen Formato de origen de la fecha
     * @return
     */
    public static Date convertirStringADate(String fecha, String formatoOrigen) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(formatoOrigen);
        return sdf.parse(fecha);
    }

    /**
     * Convierte un date en texto de acuerdo al formato estandar dd/mm/aaaa
     *
     * @param fecha Fecha a convertir
     * @return
     */
    public static String convertirFechaFormatoEstandar(Date fecha) {
        return FORMATO_ESTANDAR.format(fecha);
    }

    public static String convertirFechaFormatoDocumento(Date fecha) {
        if (NumeroTextoUtil.esVacio(fecha)) {
            return "";
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es", "ES"));
        return df.format(fecha);
    }

    public static <T> boolean tieneElementosDuplicados(List<T> lista) {
        if (null == lista) {
            return false;
        }
        return new HashSet<T>(lista).size() != lista.size();
    }

    public static boolean compararFechasIguales(Date fecha, Date fecha2) {
        if (null == fecha || null == fecha2) {
            return false;
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        String questionDateStr = dateFormatter.format(fecha);
        String todayStr = dateFormatter.format(fecha2);
        return questionDateStr.equals(todayStr);
    }

    /**
     * Verifica si una cadena tiene el formato correcto de placa
     *
     * @param cadena cadena de carateres a validar
     * @return true=es placa válida false=placa invalida
     */
    public static boolean esPlaca(String cadena) {
        String regex = "^([a-z A-Z]{1,3})(-{0,1})([0-9]{3,6})([a-z A-Z]{0,1})$";
        if (cadena.matches(regex)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static Long cortarParteDecimal(Double numero) {
        BigDecimal numeroResultado = new BigDecimal(numero.toString());
        numeroResultado = numeroResultado.setScale(0, RoundingMode.DOWN);
        return Long.parseLong(numeroResultado.toString());
    }

    public static String quitarCaracteresEspeciales(String cadena) {
        String nuevaCadena = cadena.replace(" ", "").replace("-", "").replace("_", "").replace("�?", "A")
                .replace("É", "E").replace("�?", "I").replace("Ó", "O").replace("Ú", "U").replace("Ñ", "N");
        return nuevaCadena;
    }

    /**
     * Método que normaliza una cadena de texto eliminando caracteres especiles
     * y espacios entre palabras.
     *
     * @param cadena Cadena de entrada a ser normalizada.
     * @return Texto normalizado.
     */
    public static String normalizarTexto(String cadena) {
        if (esNulo(cadena)) {
            return cadena;
        }
        //Quitar los espacios
        String cadenaSinEspacios = cadena.replaceAll("\\s", "");
        // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
        String limpio = Normalizer.normalize(cadenaSinEspacios, Normalizer.Form.NFD);
        // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
        limpio = limpio.replaceAll("[^\\p{ASCII}(N\u0303)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
        //Quitar los puntos,punto y comas, comas, guiones, comillas simples, slash
        limpio = limpio.replaceAll("\\.", "");
        limpio = limpio.replaceAll("\\;", "");
        limpio = limpio.replaceAll("\\,", "");
        limpio = limpio.replaceAll("\\-", "");
        limpio = limpio.replaceAll("\\'", "");
        limpio = limpio.replaceAll("\\/", "");
        // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
        limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC);
        return limpio;
    }

    /**
     * Permite asignar el valor de null aun objeto por temas de pmd
     *
     * @param <T> tipo null
     * @return null
     */
    public static <T> T encerarObjeto() {
        return null;
    }

    /**
     * Método que reemplaza una cadena caracteres especiales, espacios y cambiar
     * a mayúsculas.
     *
     * @param cadena Texto a reemplazar caracteres especiales, espacios y
     * cambiar a mayúsculas.
     * @return Texto sin caracteres especiales, espacios y en mayúscula.
     */
    public static String reemplazarCaracteresEspecialesQuitarEspaciosCambiarMayusculas(String cadena) {
        String nuevaCadena = cadena.toUpperCase().replace(" ", "").replace("-", "").replace("_", "").replace("�?", "A")
                .replace("É", "E").replace("�?", "I").replace("Ó", "O").replace("Ú", "U").
                replace("*", "").replace("Ñ", "N").replace(".", "").replace(",", "");
        return nuevaCadena;
    }
    
    public static SelectItem crearSelectItem(Object value, String label) {
        return new SelectItem(value, label);
    }

}
