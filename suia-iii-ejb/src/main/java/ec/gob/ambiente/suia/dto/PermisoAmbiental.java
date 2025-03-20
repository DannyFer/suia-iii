/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import java.util.Date;

import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Mariela Guano
 * @version Revision: 1.0
 */
public class PermisoAmbiental implements Serializable {

	private static final long serialVersionUID = -2850603895104640584L;

	public static final String SOURCE_TYPE_INTERNAL = "source_type_internal";
	public static final String SOURCE_TYPE_EXTERNAL_HIDROCARBUROS = "source_type_external_hidrocarburos";
	public static final String SOURCE_TYPE_EXTERNAL_SUIA = "source_type_external_suia";

	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	private String codigo;
	
	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String cedulaProponente;
	
	@Getter
	@Setter
	private String nombreProponente;
	
	@Getter
	@Setter
	private String fuente;
	
	@Getter
	@Setter
	private String motivoEliminar;
	
	@Getter
	@Setter
	private Boolean estado;
	
	@Getter
	@Setter
	private String categoria;
	
	@Getter
	@Setter
	private Integer idProyectoAsociado;
	
	@Getter
	@Setter
	private String codigoProyectoAsociado;
	
	@Getter
	@Setter
	private Date fechaArchivacion;
	
	@Getter
	@Setter
	private Date fechaRegistro;
	
	@Getter
	@Setter
	private String enteResponsable;
	
	@Getter
	@Setter
	private String autoridadResponsable;
	
	@Getter
	@Setter
	private ProyectoLicenciamientoAmbiental proyectoLicenciamientoAmbiental;

	@Getter
	@Setter
	private String provincia;
	
	@Getter
	@Setter
	private Boolean estrategico;
	
	@Getter
	@Setter
	private String abreviacionArea;
	
	public PermisoAmbiental() {
	}

	public PermisoAmbiental(Object[] array, String fuente) {
		//1 4Categorias
		//2 SUIA
		//3 ART
		//5 SECTOR-SUBSECTOR
		String proponente = "";
		if(array[5] == null){
			proponente = (array[4] == null) ? "" : (String) array[4];
		}else{
			proponente = (String) array[5];
		}
		this.id = (array[0] == null) ? null : ((Integer) array[0]);
		this.codigo = (String) array[1];
		this.nombre = (array[2] == null) ? ((fuente.equals("3")) ? "N/A"  : "") : (String) array[2];
		this.cedulaProponente= (array[3] == null) ? "" : (String) array[3];
		this.nombreProponente= proponente;
		this.motivoEliminar = (array[6] == null) ? null : (String) array[6];
		this.estado = (Boolean) array[7];
		if(!fuente.equals("3"))
			this.categoria = (array[8] == null) ? null : (String) array[8];
		else
			this.codigoProyectoAsociado = (array[8] == null) ? null : (String) array[8]; //ART
		
		this.fechaRegistro = (array[9] == null) ? null : (Date) array[9];
		this.enteResponsable = (array[10] == null) ? null : ((String) array[10]);
		
		if(fuente.equals("1") || fuente.equals("5")) {
			this.fechaArchivacion = (array[11] == null) ? null : (Date) array[11];
			this.provincia = (array[12] == null) ? null : (String) array[12];
			this.estrategico = (array[13] == null) ? null : (Boolean) array[13];
		}
			
		this.fuente= fuente;
	}

	public PermisoAmbiental(Object[] array) {
		//4 RGD
		String proponente = "";
		if(array[4] == null){
			proponente = (array[3] == null) ? "" : (String) array[3];
		}else{
			proponente = (String) array[4];
		}
		this.id = (array[0] == null) ? null : ((Integer) array[0]);
		this.codigo = (String) array[1];
		this.nombre = "N/A";
		this.cedulaProponente= (array[2] == null) ? "" : (String) array[2];
		this.nombreProponente= proponente;
		this.motivoEliminar = (array[5] == null) ? null : (String) array[5];
		this.estado = (Boolean) array[6];
		this.idProyectoAsociado = (array[7] == null) ? null : ((Integer) array[7]);
		this.fechaRegistro = (array[8] == null) ? null : (Date) array[8];
		this.enteResponsable = (array[9] == null) ? null : ((String) array[9]);
		
		this.fuente= "4"; 
	}
}
