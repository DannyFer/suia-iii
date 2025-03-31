package ec.gob.ambiente.rcoa.dto;

import java.util.Date;

import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.SolicitudImportacionRSQExtVue;
import lombok.Getter;
import lombok.Setter;

public class SolicitudImportacionRSQVueDTO {

	
	public SolicitudImportacionRSQVueDTO() {
		super();
		//solicitudImportacionRSQVue = new SolicitudImportacionRSQVue();
		solicitudImportacionRSQExtVue = new SolicitudImportacionRSQExtVue();
	}

	@Getter
    @Setter
    private String estadoTramite;
	
	@Getter
    @Setter
    private String tipoSolicitud;
	
	@Getter
    @Setter
    private Date fechaInicio;

    @Getter
    @Setter
    private Date fechaFin;

    @Getter
    @Setter
    private String tipoFormato;
    /*
    @Getter
    @Setter
    private SolicitudImportacionRSQVue solicitudImportacionRSQVue;
    */
    @Getter
    @Setter
    private SolicitudImportacionRSQ solicitudImportacionRSQ;
    
    @Getter
    @Setter
    private SolicitudImportacionRSQExtVue solicitudImportacionRSQExtVue;
    
    @Getter
    @Setter
    private Integer indice;
}
