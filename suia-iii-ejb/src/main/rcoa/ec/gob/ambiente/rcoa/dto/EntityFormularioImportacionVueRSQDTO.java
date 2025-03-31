package ec.gob.ambiente.rcoa.dto;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import lombok.Getter;
import lombok.Setter;

@Stateless
public class EntityFormularioImportacionVueRSQDTO {
	
	@Getter
	@Setter
	private String numeroSolicitud, 
	ciudadSolicitud,
	clasificacionSolicitante,
	TipoSolicitante,
	numeroIdentificacionSolicitante,
	nombreRazonSocialSolicitante,
	nombreSolicitante,
	provinciaSolicitante,
	cantonSolicitante,
	parroquiaSolicitante,
	direccionSolicitante,
	telefonoSolicitante,
	celularSolicitante,
	correoElectronicoSoliciante,
	clasificacionImportador,
	TipoImportador,
	numeroIdentificacionImportador,
	nombreRazonSocialImportador,
	codigoRSQ,
	nombreImportador,
	provinciaImportador,
	cantonImportador,
	parroquiaImportador,
	direccionImportador,
	telefonoImportador,
	correoElectronicoImportador,
	paisEmbarque,
	medioTransporte,
	puertoEmbarque,
	lugarDesembarque;
	
	@Getter
	@Setter
	private String tablaSustancias;
	
	@Getter
	@Setter
	private Date fechaSolicitud,
	fechaAprobacion,
	fechaInicioVigenciaLicencia,
	fechaFinVigenciaLicencia;
	
	@Getter
	@Setter
	private List<EntityTablaFormularioImportacionVueRSQDTO> tablaProductos;
	
	@Getter
	@Setter
	private String nombreReporte;
	
	
	
}
