
package ec.gob.ambiente.suia.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Definición de valores constantes o Enum
 * @author RGALLO
 * @date 2022-09-18
 */
public enum ConstantesEnum {
    
    //Tipo de Solicitud. Para el reporte de Importación Sustancia Químicas
  	SOLICITUD_AUTORIZACIONES_EMITIDAS("EMITIDA", "Solicitudes de Autorizaciones Emitidas", 1L),
  	SOLICITUD_AUTORIZACIONES_ANULADAS("ANULADA", "Solicitudes de Autorizaciones Anuladas", 2L),
	SOLICITUD_AUTORIZACIONES_PENDIENTES("ENVIADA", "Solicitudes de Autorizaciones Pendientes", 3L), 
	SOLICITUD_AUTORIZACIONES_CORREGIDAS("CORREGIDA", "Solicitudes de Autorizaciones Corregidas", 4L), 
	SOLICITUD_TODAS("TODAS", "Todas", 0L);
    
    private String nemonico;
    private String descripcion;
    private Long codigo;

    private static final Map<Long, ConstantesEnum> MAP = new HashMap<>();
    
    private ConstantesEnum(String nemonico, String descripcion, Long codigo) {
        this.nemonico = nemonico;
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    private ConstantesEnum(Long codigo) { 
    	this.codigo = codigo; 
    }
    
    public static ConstantesEnum fromCodigo(Long codigo){
        return MAP.get(codigo);
    }
    
    static{
        for(ConstantesEnum n : values()){
            MAP.put(n.getCodigo(), n);
        }
    }
    
    public String getNemonico() {
        return nemonico;
    }

    public void setNemonico(String nemonico) {
        this.nemonico = nemonico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
}