/*
 * Copyright 2015 UNIVERSIDAD CENTRAL DEL ECUADOR
 * Todos los derechos reservados
 */
package ec.gob.ambiente.suia.dto;

/**
 * <b> Entity para el resumen de Fichas. </b>
 * 
 * @author Javier Lucero
 * @version $Revision: 1.0 $
 *          <p>
 *          [$Author: Javier Lucero $, $Date: 21/04/2015 $]
 *          </p>
 */
public class EntityFichaCompleta {

	private String id;
	private String responsable;
	private String codigo;
	private String resumen;
	private String nombre;
	private String descripcion;
	private String sector;
	private String fecha;
	private String proponente;
	private String cedulaProponente;
	private String categoria;
	private String numeroResolucion;
	private String fechaEmision;

	public EntityFichaCompleta(String id, String responsable, String codigo, String resumen, String nombre, String descripcion, String sector,String fecha) {
		super();
		this.id = id;
		this.responsable = responsable;
		this.codigo = codigo;
		this.resumen = resumen;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.sector = sector;
		this.fecha = fecha;
	}
	
	public EntityFichaCompleta(String id, String codigo , String nombreProyecto, String cedulaProponente, String nombrProponente, String nombreOrganizacion, String categoria) {
		super();
		this.id = id;
		this.codigo=codigo;
		this.nombre = nombreProyecto;
		this.cedulaProponente = cedulaProponente;
		this.categoria=categoria;
		//nombreOrganizacion != null && !nombreOrganizacion.isEmpty()
		if (nombreOrganizacion !=null && !nombreOrganizacion.isEmpty()){
			this.proponente = nombreOrganizacion;
		}else{
			this.proponente=nombrProponente;
		}		
	}
	
	public EntityFichaCompleta(String numeroResolucion,String fechaEmision  ){
		super();
		this.numeroResolucion=numeroResolucion;
		this.fechaEmision=fechaEmision;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getProponente() {
		return proponente;
	}

	public void setProponente(String proponente) {
		this.proponente = proponente;
	}

	public String getCedulaProponente() {
		return cedulaProponente;
	}

	public void setCedulaProponente(String cedulaProponente) {
		this.cedulaProponente = cedulaProponente;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public String getNumeroResolucion() {
		return numeroResolucion;
	}

	public void setNumeroResolucion(String numeroResolucion) {
		this.numeroResolucion = numeroResolucion;
	}

	public String getFechaEmision() {
		return fechaEmision;
	}

	public void setFechaEmision(String fechaEmision) {
		this.fechaEmision = fechaEmision;
	}	
	

}
