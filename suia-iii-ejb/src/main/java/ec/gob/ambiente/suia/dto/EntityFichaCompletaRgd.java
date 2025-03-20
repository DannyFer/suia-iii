package ec.gob.ambiente.suia.dto;

import java.io.Serializable;

public class EntityFichaCompletaRgd implements Serializable{


	private String id;
	private String responsable;
	private String codigo;
	private String nombre;
	private String fechaRegistro;
	private String sector;
	private String autorizacion;
	private String sistema; //1 suia-rcoa, 2 suia, 3 4 categorias 5 sector subsector  6 = digitalizacion
	private String tipo; //licencia, registro, certificado
	private String numeroResolucion;
	private String estadoProyecto;
	
	public EntityFichaCompletaRgd(String id, String responsable, String codigo,
			String nombre, String fechaRegistro, String sector, 
			String autorizacion) {
		super();
		this.id = id;
		this.responsable = responsable;
		this.codigo = codigo;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
		this.sector = sector;
		this.autorizacion = autorizacion;
	}

	public EntityFichaCompletaRgd(String id, String responsable, String codigo,
			String nombre, String fechaRegistro, String sector) {
		super();
		this.id = id;
		this.responsable = responsable;
		this.codigo = codigo;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
		this.sector = sector;
	}
	
	public EntityFichaCompletaRgd(String id, String responsable, String codigo,
			String nombre, String fechaRegistro, String sector, String sistema, String tipo) {
		super();
		this.id = id;
		this.responsable = responsable;
		this.codigo = codigo;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
		this.sector = sector;
		this.sistema = sistema;
		this.tipo = tipo;
	}
	
	public EntityFichaCompletaRgd(String id, String responsable, String codigo,
			String nombre, String fechaRegistro, String sector, String sistema, String tipo, String resolucion, String estado) {
		super();
		this.id = id;
		this.responsable = responsable;
		this.codigo = codigo;
		this.nombre = nombre;
		this.fechaRegistro = fechaRegistro;
		this.sector = sector;
		this.sistema = sistema;
		this.tipo = tipo;
		this.numeroResolucion = resolucion;
		this.estadoProyecto = estado;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getFechaRegistro() {
		return fechaRegistro;
	}

	public void setFechaRegistro(String fechaRegistro) {
		this.fechaRegistro = fechaRegistro;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getAutorizacion() {
		return autorizacion;
	}

	public void setAutorizacion(String autorizacion) {
		this.autorizacion = autorizacion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNumeroResolucion() {
		return numeroResolucion;
	}

	public void setNumeroResolucion(String numeroResolucion) {
		this.numeroResolucion = numeroResolucion;
	}

	public String getEstadoProyecto() {
		return estadoProyecto;
	}

	public void setEstadoProyecto(String estadoProyecto) {
		this.estadoProyecto = estadoProyecto;
	}
}