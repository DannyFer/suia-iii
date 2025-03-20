package ec.gob.ambiente.suia.domain.enums;

import lombok.Getter;
import lombok.Setter;


public enum CodigoTasa {
    USO_SISTEMA("130108.03"),  //validar CODIGO para tasa 
    REGISTRO_GENERADOR_DESECHOS("130111.02"),
    REGISTRO_AMBIENTAL("130111.01"),
    REGISTRO_SUSTANCIAS_QUIMICAS("130111.16"), //TODO validar CODIGO para tasa
    FACILIATDOR("130108.07"),
    FACILIATDOR_GALAPAGOS("130108.08"),
    REVISION_ESTUDIO_IMPACTO_AMBIENTAL("130112.01"), //validar CODIGO para tasa
    INVENTARIO_FORESTAL_MADERA_PIE("130112.06"),  // VALOR DE LA MADERA PIE - INVENTARIO FORESTAL
    EMISION_LICENCIA_PAGO_MINIMO_BAJO_IMPACTO("130112.26"),
    EMISION_LICENCIA_PAGO_MINIMO_ALTO_IMPACTO("130112.27"),
	EMISION_LICENCIA_AMBIENTAL("130112.01"),
    PAGO_EN_LINEA_COSTO_PROYECTO("130119.04");
    @Getter
    @Setter
    private String descripcion;
    
    
    private CodigoTasa(String descripcion){
        this.descripcion = descripcion;
    }
    
}
