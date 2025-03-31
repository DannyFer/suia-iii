/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.rcoa.agrupacionAutorizaciones.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import ec.gob.ambiente.rcoa.digitalizacion.model.ProyectoAsociadoDigitalizacion;
import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Mariela Guano
 * @version Revision: 1.0
 */
public class AutorizacionAdministrativa implements Serializable {

	private static final long serialVersionUID = -2850603895104640584L;

	public static final String SOURCE_TYPE_INTERNAL = "source_type_internal";
	public static final String SOURCE_TYPE_EXTERNAL_HIDROCARBUROS = "source_type_external_hidrocarburos";
	public static final String SOURCE_TYPE_EXTERNAL_SUIA = "source_type_external_suia";

	@Getter
	@Setter
	private Integer id;
	@Getter
	@Setter
	private Integer idRegistro;

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
	private String codigoDocumento;
	
	@Getter
	@Setter
	private String tipoProyecto;
	
	@Getter
	@Setter
	private String areaEmisora;
	
	@Getter
	@Setter
	private String areaControl;
	
	@Getter
	@Setter
	private String resolucion;
	
	@Getter
	@Setter
	private String fechaResolucion;
	
	@Getter
	@Setter
	private String sector;	
	
	@Getter
	@Setter
	private String fuente;	
	
	@Getter
	@Setter
	private String categoria;

	@Getter
	@Setter
	private String estado;

	@Getter
	@Setter
	private Integer nroTareas;
	
	@Getter
	@Setter
	private String fecha; //
	
	@Getter
	@Setter
	private String fechaFin; //rgd art
	
	@Getter
	@Setter
	private String resumen;
	
	@Getter
	@Setter
	private String actividad;
	
	@Getter
	@Setter
	private  Boolean seleccionadoSecundario;
	
	@Getter
	@Setter
	private Boolean digitalizado;
	
	@Getter
	@Setter
	private Integer idDigitalizacion;
	
	@Getter
	@Setter
	private Integer idProceso;
	
	@Getter
	@Setter
	@Transient
	private List<ProyectoAsociadoDigitalizacion> listaProyectosAsociadosVer;
	
	
	public AutorizacionAdministrativa() {
	}

	public AutorizacionAdministrativa(Object[] array, String fuente) {
		//1 4Categorias 0 null,  1 p.id, 2 p.nombre, 3 p.usuarioldap, 4 r.nombresapellidos, 5 o.nombre, 6 cc.ca_categoria, 7 cc.estrategico, 8 tareas, 9 tareas4Cat, 11 fecha_fin, 10 resumen, 11 actividadad ,13 cc.cata_sector,  14 digitalizado
		//2 SUIA  0 p.pren_id, 1 p.pren_code, 2 p.pren_name, 3 p.pren_creator_user, 4 l.peop_name, 5 o.orga_name_organization,  6 c.cate_code, 7 y.estadoproceso, 8 sector, 9 fecha,  10 digitalizado
		//3 ART 0 p.apte_id, 1 p.apte_request, 2 p.apte_name_proyect, 3 p.apte_creator_user, 4 l.peop_name, 5 o.orga_name_organization
		//4 RGD
		//5 SECTOR-SUBSECTOR 0 null,  1 p.id, 2 p.nombre, 3 p.usuarioldap, 4 r.nombresapellidos, 5 o.nombre, 6 cc.ca_categoria, 7 cc.estrategico, 8 tareas
		//6 DIGITALIZACION 0 id 1 codigo 2 identificacion 3 nombre usuario 4 nombrea 5 idusuario  6 nombre 7sector 8 fecha 9 categoria
		// 7 RCOA
		String proponente = "";
		if(array[5] == null){
			proponente = (array[4] == null) ? "" : (String) array[4];
		}else{
			proponente = (String) array[5];
		}

		this.codigo = (String) array[1];
		String estado = "", sector = "";
		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.categoria = (array[6] == null) ? null : (String) array[6];
		switch (fuente) {
		case "1":
			nroTareas = (array[8] == null) ? null : (Integer) array[8];
			Integer nroTareas4Cat = (array[9] == null) ? 0 : (Integer) array[9];
			if(nroTareas4Cat > 0)
				estado = (nroTareas.equals(0)) ? "Completado" : "Por completar";
			else
				estado = (array[11] != null) ? "Completado" : "Por completar";
			estado = "Por completar";
			if(array[14] != null){
				String sectorProy = (String) array[14];
				sector = sectorProy.substring(0, 1).toUpperCase() + sectorProy.substring(1);
			}
			this.resumen = (array[12] == null) ? null : ((String) array[12]);
			this.actividad = (array[13] == null) ? null : ((String) array[13]);
			try{
				if(array[10] != null){
					Date fechaInicio = (Date)array[10];
					this.fecha = formato.format(fechaInicio);
				}
				if(array[11] != null){
					Date fechaFinalizacion = (Date)array[11];
					this.fechaFin = formato.format(fechaFinalizacion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "2":
			sector = (array[14] == null) ? "" : (String) array[14];
			if(array[10] != null){
				Date fechaIngreso = (Date)array[10];
				this.fecha = fechaIngreso.toString();
			}else{
				this.fecha = "N/A";
			}
			if(array[11] != null){
				Date fechaIngreso = (Date)array[11];
				this.fechaFin = fechaIngreso.toString();
			}
			
			String categoria = (array[6] == null) ? null : (String) array[6];
			if(categoria != null && categoria.equals("I")){
				this.categoria = "Certificado Ambiental";
			}else if(categoria != null && categoria.equals("II")){
				this.categoria = "Registro Ambiental";
			}else if(categoria != null && categoria.equals("III")){
				this.categoria = "Licencia Ambiental";
			}else if(categoria != null && categoria.equals("IV")){
				this.categoria = "Licencia Ambiental";
			}else{
				this.categoria = "N/A";
			}
		break;
		case"3":
			try {
				if(array[9] != null){
					Date fechaInicio = formato.parse((String)array[9]);
					this.fecha = formato.format(fechaInicio);
				}
				if(array[10] != null){
					Date fechaFinalizacion = formato.parse((String)array[10]);
					this.fechaFin = formato.format(fechaFinalizacion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case"4":
			try {
				if(array[9] != null){
					Date fechaInicio = formato.parse((String)array[9]);
					this.fecha = formato.format(fechaInicio);
				}
				if(array[10] != null){
					Date fechaFinalizacion = formato.parse((String)array[10]);
					this.fechaFin = formato.format(fechaFinalizacion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case "5":
			nroTareas = (array[8] == null) ? null : (Integer) array[8];
			estado = (nroTareas.equals(0)) ? "Por completar" : "Por completar";
			if(array[10] != null){
				this.fecha = array[10].toString();
			}else{
				this.fecha = "N/A";
			}
			this.resumen = (array[12] == null) ? null : ((String) array[12]);
			this.actividad = (array[13] == null) ? null : ((String) array[13]);
			sector = (array[14] == null) ? null : ((String) array[14]);
			break;
		case "6":
			Integer valor =(array[22] != null) ? ((Integer) array[22]): 1;
			fuente = valor.toString();
			if(array[10] != null){
				Date fechaIngreso = (Date)array[10];
				this.fecha = fechaIngreso.toString();
			}else{
				this.fecha = "N/A";
			}
			if(array[14] != null){
				sector = (String)array[14];
			}
			break;
		case "7":
			nroTareas = (array[8] == null) ? null : (Integer) array[8];
			estado = "Por completar";
			if(array[14] != null){
				String sectorProy = (String) array[14];
				sector = sectorProy.substring(0, 1).toUpperCase() + sectorProy.substring(1);
			}
			this.resumen = (array[12] == null) ? null : ((String) array[12]);
			this.actividad = (array[13] == null) ? null : ((String) array[13]);
			try{
				if(array[10] != null){
					Date fechaInicio = (Date)array[10];
					this.fecha = formato.format(fechaInicio);
				}
				if(array[11] != null){
					Date fechaFinalizacion = (Date)array[11];
					this.fechaFin = formato.format(fechaFinalizacion);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		this.id = (array[0] == null) ? null : ((Integer) array[0]);
		this.nombre = (array[2] == null) ? ((fuente.equals("3")) ? "N/A"  : "") : (String) array[2];
		this.cedulaProponente= (array[3] == null) ? "" : (String) array[3];
		this.nombreProponente= proponente;
		this.estado = (!fuente.equals("1") && !fuente.equals("5")) ? (array[7] == null) ? null : ((boolean)array[7])?"Completado":"Por completar" : estado;
		this.sector= sector ;
		this.fuente= fuente;
		if(array.length > 15){
			this.areaEmisora = (array[15] == null) ? "" : (String) array[15];
		}
		if(array.length > 16){
			this.resolucion = (array[16] == null) ? "" : (String) array[16];
			if(array[17] != null){
				Date fechaFinalizacion;
				try {
					fechaFinalizacion = formato.parse((String)array[17]);
					this.fechaResolucion = formato.format(fechaFinalizacion);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		if(array.length > 18){
			if(array[18] != null){
				this.digitalizado = Boolean.valueOf((boolean) array[18]);
				this.estado= (this.digitalizado)?"Completado":"Por completar";
			}else
				this.digitalizado = false;
			this.idDigitalizacion = (array[19] != null) ? ((Integer) array[19]):null;
			this.idProceso = (array[20] != null) ? ((Integer) array[20]): null;
		}else{
			this.digitalizado = false;
			this.idDigitalizacion = null;
			this.idProceso = null;
		}
		if(idDigitalizacion == null)
			this.estado= "Por completar";
		else{
			this.estado= (this.digitalizado)?"Completado":"Por completar";
		}
		/*if(this.fecha != null && !this.fecha.isEmpty() && this.fecha.length() > 9)
			this.fecha = this.fecha.substring(0, 10);*/
	}
}
