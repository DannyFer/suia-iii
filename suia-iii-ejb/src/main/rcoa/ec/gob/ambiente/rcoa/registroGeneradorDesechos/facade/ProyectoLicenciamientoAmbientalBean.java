package ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

public class ProyectoLicenciamientoAmbientalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String nombreOperador;

	@Getter
	@Setter
	private String identificacionOperador;

	@Getter
	@Setter
	private String tipoEmisionActualizacion; // Emision de rgd con AAA , Emision de un único rgd con varias actividades,
												// Emision rgd con licencia en tŕamite

	@Getter
	@Setter
	private String actividad;

	@Getter
	@Setter
	private String codigoProyecto;

	@Getter
	@Setter
	private String nombreProyecto;

	@Getter
	@Setter
	private String numeroResolucion; // numero de autorizacion administrativa ambiental

	@Getter
	@Setter
	private String tipoPermisoAmbiental; // Certificado, registro, licencia

	@Getter
	@Setter
	private String resumenProyectoObraActividad;

	@Getter
	@Setter
	private String sector;

	@Getter
	@Setter
	private String emitidoPor; // 1: suia 2:nuevo ambiente, 3: fisico, // 4 :iniciado en suia terminado en
								// fisico

	@Getter
	@Setter
	private Date fechaAutorizacion; // fecha otorgamiento de autorización administrativa ambiental

	@Getter
	@Setter
	private String actividadEconomicaPrincipal;

	@Getter
	@Setter
	private String segundaActividad;

	@Getter
	@Setter
	private String terceraActividad;

	@Getter
	@Setter
	private String direccion;

	@Getter
	@Setter
	private Integer idActividadPrincipal;

	@Getter
	@Setter
	private Integer idSegundaActividad;

	@Getter
	@Setter
	private Integer idTerceraActividad;

	public ProyectoLicenciamientoAmbientalBean(Object[] array) {

		this.id = (Integer) array[0];
		this.nombreOperador = (String) array[1];
		this.identificacionOperador = (String) array[2];
		this.tipoEmisionActualizacion = (String) array[3];
		this.actividad = (String) array[4];
		this.codigoProyecto = (String) array[5];
		this.nombreProyecto = (String) array[6];
		this.numeroResolucion = (String) array[7];
		this.tipoPermisoAmbiental = (String) array[8];
		this.resumenProyectoObraActividad = (String) array[9];
		this.sector = (String) array[10];
		this.emitidoPor = (String) array[11];
		this.fechaAutorizacion = (Timestamp) array[12];
		this.actividadEconomicaPrincipal = (String) array[13];
		this.segundaActividad = (String) array[14];
		this.terceraActividad = (String) array[15];
		this.direccion = (String) array[16];
		this.idActividadPrincipal = (Integer) array[17];
		this.idSegundaActividad = (Integer) array[18];
		this.idTerceraActividad = (Integer) array[19];
	}

}
