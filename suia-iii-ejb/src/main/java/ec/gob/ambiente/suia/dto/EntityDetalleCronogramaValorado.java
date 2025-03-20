/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.dto;

/**
 *
 * @author ishmael
 */
public class EntityDetalleCronogramaValorado {
    
    private String plan;
    private String actividad;
    private String responsable;
    private String fechaInicio;
    private String fechaFin;
    private String presupuesto;
    private String justificativo;
    private String frecuencia;
    private String ingresaDatos;
    private String observacion;

    public EntityDetalleCronogramaValorado(String plan, String actividad, String responsable, String fechaInicio, String fechaFin, String presupuesto, String justificativo) {
        this.plan = plan;
        this.actividad = actividad;
        this.responsable = responsable;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.presupuesto = presupuesto;
        this.justificativo=justificativo;
    }
    
       public EntityDetalleCronogramaValorado(String plan, String actividad, String responsable, String fechaInicio, String fechaFin, String presupuesto,String justificativo,String frecuencia, String ingresaDatos, String observacion) {
        this.plan = plan;
        this.actividad = actividad;
        this.responsable = responsable;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.presupuesto = presupuesto;
        this.justificativo = justificativo;
        this.setFrecuencia(frecuencia);
        this.ingresaDatos = ingresaDatos;
        this.observacion = observacion;
    }

    /**
     * @return the plan
     */
    public String getPlan() {
        return plan;
    }

    /**
     * @param plan the plan to set
     */
    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * @return the actividad
     */
    public String getActividad() {
        return actividad;
    }

    /**
     * @param actividad the actividad to set
     */
    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the fechaInicio
     */
    public String getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public String getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    
    /**
     * @param justificativo the justificativo to set
     */
    
    
    public String getJustificativo() {
		return justificativo;
	}

	public void setJustificativo(String justificativo) {
		this.justificativo = justificativo;
	}

	/**
	 * 
	 * 
	 */
    /**
     * @return the presupuesto
     */
    public String getPresupuesto() {
        return presupuesto;
    }

    /**
     * @param presupuesto the presupuesto to set
     */
    public void setPresupuesto(String presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

	public String getIngresaDatos() {
		return ingresaDatos;
	}

	public void setIngresaDatos(String ingresaDatos) {
		this.ingresaDatos = ingresaDatos;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
    
    
}
