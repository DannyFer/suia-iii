/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author
 */
public class EntityOficioAprobacionEia {

	@Getter
	@Setter
	private String numeroOficio;
	@Getter
	@Setter
	private String ciudadInforme;
	@Getter
	@Setter
	private String fechaInforme;
	@Getter
	@Setter
	private String asunto;
	@Getter
	@Setter
	private String tratamientoReceptor;
	@Getter
	@Setter
	private String nombreReceptor;
	@Getter
	@Setter
	private String cargoReceptor;
	@Getter
	@Setter
	private String empresa;
	@Getter
	@Setter
	private String lugarEntrega;
	@Getter
	@Setter
	private String numeroProyecto;
	@Getter
	@Setter
	private String fechaProyecto;
	@Getter
	@Setter
	private String nombreProyecto;
	@Getter
	@Setter
	private String provinciaProyecto;
	@Getter
	@Setter
	private String cantonProyecto;
	@Getter
	@Setter
	private String parroquiaProyecto;
	@Getter
	@Setter
	private String numeroOficioInterseccion;
	@Getter
	@Setter
	private String fechaOficioInterseccion;
	@Getter
	@Setter
	private String interseccion;
	@Getter
	@Setter
	private String actualizacionCertificadoInterseccion;
	@Getter
	@Setter
	private String numeroOficioObservaciones;
	@Getter
	@Setter
	private String fechaOficioObservaciones;
	@Getter
	@Setter
	private String fechaRespuestas;
	@Getter
	@Setter
	private String tieneObservaciones;
	@Getter
	@Setter
	private String numeroPronunciamiento;
	@Getter
	@Setter
	private String fechaPronunciamiento;
	@Getter
	@Setter
	private String emisorPronunciamiento;
	@Getter
	@Setter
	private String numeroInformeTecnico;
	@Getter
	@Setter
	private String fechaInformeTecnico;
	@Getter
	@Setter
	private String disposicionesLegales;
	@Getter
	@Setter
	private String nombreFavorable;
	@Getter
	@Setter
	private String actividades;
	@Getter
	@Setter
	private String requisitos1;
	@Getter
	@Setter
	private String formula;
	@Getter
	@Setter
	private String pagoInspeccionDiaria;
	@Getter
	@Setter
	private String numeroTecnicos;
	@Getter
	@Setter
	private String numeroDias;
	@Getter
	@Setter
	private String valorTotal;
	@Getter
	@Setter
	private String valorForestal;
	@Getter
	@Setter
	private String requisitos2;
	@Getter
	@Setter
	private String nombreRemitente;
	@Getter
	@Setter
	private String cargoRemitente;

	@Getter
	@Setter
	private String datosRevision;

	@Getter
	@Setter
	private String nombreMinistra;
	@Getter
	@Setter
	private String cargoMinistra;

	@Getter
	@Setter
	private String numeroDNF;

	@Getter
	@Setter
	private String fechaDNF;
	 /**
	    * Nombre:SUIA
	    * Descripción: Para obterner el áres responsable para el logo del documentos. 
	    * ParametrosIngreso:
	    * PArametrosSalida:
	    * Fecha:16/08/2015
	    */
		@Getter
		@Setter
		private String areaResponsable;
		
		@Getter
		@Setter
		private String entidad;
		
		@Getter
		@Setter
		private String pagomae;
				
		/**
		  * FIN Para obterner el áres responsable para el logo del documentos.
		  */
	
}
