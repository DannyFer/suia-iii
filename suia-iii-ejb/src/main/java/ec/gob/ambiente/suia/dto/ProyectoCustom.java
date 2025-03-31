/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author Carlos Pupo
 * @version Revision: 1.0
 *          <p>
 *          [Autor: Carlos Pupo, Fecha: 12/03/2015]
 *          </p>
 */
public class ProyectoCustom implements Serializable {

	private static final long serialVersionUID = -2850603895104640584L;

	public static final String SOURCE_TYPE_INTERNAL = "source_type_internal";
	public static final String SOURCE_TYPE_EXTERNAL_HIDROCARBUROS = "source_type_external_hidrocarburos";
	public static final String SOURCE_TYPE_EXTERNAL_SUIA = "source_type_external_suia";
	public static final String SOURCE_TYPE_RCOA = "source_type_rcoa";
	public static final String SOURCE_TYPE_DIGITALIZACION = "source_type_digitalizacion";

	@Getter
	@Setter
	private String id;

	@Getter
	@Setter
	private String sourceType;

	@Getter
	@Setter
	private String codigo;

	@Getter
	@Setter
	private String nombre;

	@Getter
	@Setter
	private String sector;

	@Getter
	@Setter
	private String categoria;

	@Getter
	@Setter
	private String registro;
	
	@Getter
	@Setter
	private String responsableSiglas;
	
	@Getter
	@Setter
	private String responsable;

	@Getter
	@Setter
	private String categoriaNombrePublico;

	@Getter
	@Setter
	private String motivoEliminar;
	
	@Getter
	@Setter
	private String cedulaProponente;
	
	@Getter
	@Setter
	private String nombreProponente;
	
	@Getter
	@Setter
	private String fechaArchivo;
	
	@Getter
	@Setter
	private String finalizado;
	
	@Getter
	@Setter
	private String estado;
	
	@Getter
	@Setter
	private String ubicacion;
	
	public ProyectoCustom() {
	}

	public ProyectoCustom(Object[] array) {
		// p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, st.sety_name, ca.cate_public_name, p.pren_delete_reason, a.area_abbreviation, a.area_name
		this.id = ((Integer) array[0]).toString();
		this.codigo = (String) array[1];
		this.nombre = (String) array[2];
		Timestamp registro = (Timestamp) array[3];
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			this.registro = dateFormat.format(registro);
		}
		this.sector = (String) array[4];
		this.categoriaNombrePublico = (String) array[5];
		this.motivoEliminar = (String) array[6];
		this.responsableSiglas = (String) array[7];
		this.responsable = (String) array[8];
		if (array.length>10){
			this.cedulaProponente=(String) array[9];
			this.nombreProponente=(String) array[11];
			if(this.nombreProponente==null){
				this.nombreProponente=(String) array[10];
			}
			}
		this.sourceType = SOURCE_TYPE_INTERNAL;
	}
	
	public ProyectoCustom(Integer id, String codigo, String nombre, Date registro,
			String sector, String categoriaNombrePublico, String motivoEliminar, 
			String responsableSiglas, String responsable) {
		this.id = id.toString();
		this.codigo = codigo;
		this.nombre = nombre;
		if (null != registro) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			this.registro = dateFormat.format(registro);
		}
		this.sector = sector;
		this.categoriaNombrePublico = categoriaNombrePublico;
		this.motivoEliminar = motivoEliminar;
		this.responsableSiglas = responsableSiglas;
		this.responsable = responsable;
		this.sourceType = SOURCE_TYPE_RCOA;
		
	}
	
	public ProyectoCustom(Integer id, String codigo, String nombre, Date registro,
			String sector, String categoriaNombrePublico, String motivoEliminar, 
			String responsableSiglas, String responsable, Date archivo) {
		this.id = id.toString();
		this.codigo = codigo;
		this.nombre = nombre;
		if (null != registro) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			this.registro = dateFormat.format(registro);
		}
		if(archivo != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			this.fechaArchivo = dateFormat.format(archivo);
		}
		this.sector = sector;
		this.categoriaNombrePublico = categoriaNombrePublico;
		this.motivoEliminar = motivoEliminar;
		this.responsableSiglas = responsableSiglas;
		this.responsable = responsable;
		this.sourceType = SOURCE_TYPE_RCOA;
		
	}

    public ProyectoCustom(TaskSummaryCustom task) {
        this.setCodigo(task.getProjectId());
        this.setSourceType(task.getSourceType());
    }

	@Override
	public boolean equals(Object obj) {
		ProyectoCustom pr = (ProyectoCustom) obj;
		return pr.getId().equals(getId());
	}

	public boolean isInternal() {
		return getSourceType().equals(SOURCE_TYPE_INTERNAL);
	}

	public boolean isRequestDeletion() {
		return getMotivoEliminar() != null && !getMotivoEliminar().trim().isEmpty();
	}
	
	public ProyectoCustom(Object[] array, Integer tipo) {
		// p.pren_id, p.pren_code, p.pren_name, p.pren_register_date, p.pren_delete_reason
		//p.prco_id, p.prco_cua, p.prco_name,prco_deactivation_date, p.prco_delete_reason
		this.id = ((Integer) array[0]).toString();
		this.codigo = (String) array[1];
		this.nombre = (String) array[2];
		Timestamp registro = (Timestamp) array[3];
		if (registro != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			this.registro = dateFormat.format(registro);
		}
		
		this.motivoEliminar = (String) array[4];
		
		if(array[5] != null){			
			this.fechaArchivo = array[5].toString();
		}
		this.categoria = ((Integer) array[6]).toString();
		this.finalizado = ((Boolean) array[7]).toString();
		this.estado = ((Boolean) array[8]).toString();
		this.sourceType = SOURCE_TYPE_INTERNAL;
	}
	
	public ProyectoCustom(Object[] array, Boolean tipo){
		this.id = ((Integer) array[0]).toString();
		this.codigo = (String) array[1];
		this.nombre = (String) array[2];
		Timestamp registro = (Timestamp) array[3];
		this.registro = (registro != null) ? new SimpleDateFormat("dd/MM/yyyy").format(registro) : null;
		this.motivoEliminar = (String) array[4];
		this.fechaArchivo = (array[5] != null) ? array[5].toString() : null;
		this.categoria = ((Integer) array[6]).toString();
		this.finalizado = ((Boolean) array[7]).toString();
		this.estado = ((Boolean) array[8]).toString();
		this.cedulaProponente = (String) array[9];
		this.ubicacion = (array[10] != null) ? ((Integer) array[10]).toString() : null;
		this.sourceType = SOURCE_TYPE_RCOA;
	}
}
