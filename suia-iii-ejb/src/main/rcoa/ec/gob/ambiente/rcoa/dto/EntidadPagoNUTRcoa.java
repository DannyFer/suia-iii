package ec.gob.ambiente.rcoa.dto;


import ec.gob.ambiente.suia.recaudaciones.model.NumeroUnicoTransaccional;
import ec.gob.ambiente.suia.recaudaciones.model.SolicitudUsuario;
import lombok.Getter;
import lombok.Setter;

public class EntidadPagoNUTRcoa {


    @Getter
    @Setter
    private SolicitudUsuario solicitudUsuario;

    @Getter
    @Setter
    private NumeroUnicoTransaccional numeroUnicoTransaccional;
    
    @Getter
    @Setter
    private byte[] archivo;
    
    @Getter
    @Setter
    private boolean correcto;
    
    @Getter
    @Setter
    private String mensaje;
}