/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.Constantes;

/**
 * <b> AGREGAR DESCRIPCION. </b>
 * 
 * @author mayriliscs
 * @version Revision: 1.0
 *          <p>
 *          [Autor: mayriliscs, Fecha: 12/01/2015]
 *          </p>
 */
@ManagedBean
@ViewScoped
public class AceptarRechazarPronunciamientosComunBean implements Serializable {

	private static final long serialVersionUID = 8021576244114917644L;
	
	private Pronunciamiento pronunciamiento;
	private List<Pronunciamiento> pronunciamientos;

	@Getter
	@Setter
	private long idTarea;

	@Getter
	@Setter
	private Map<String, Object> paramsTaskToComplete;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private static final Logger LOG = Logger.getLogger(AceptarRechazarPronunciamientosComunBean.class);

	public Pronunciamiento getPronunciamiento() {
		return pronunciamiento == null ? pronunciamiento = new Pronunciamiento() : pronunciamiento;
	}

	public void setPronunciamiento(Pronunciamiento pronunciamiento) {
		this.pronunciamiento = pronunciamiento;
	}

	public List<Pronunciamiento> getPronunciamientos() {
		return pronunciamientos == null ? pronunciamientos = new ArrayList<Pronunciamiento>() : pronunciamientos;
	}

	public void setPronunciamientos(List<Pronunciamiento> pronunciamientos) {
		this.pronunciamientos = pronunciamientos;
	}

	@PostConstruct
	public void iniciar() {
		List<Integer> identificadores = obtenerIdsPronunciamientosSolicitados();
		if (identificadores != null)
			pronunciamientos = pronunciamientoFacade.getPronunciamientos(identificadores);
	}

	public List<Integer> obtenerIdsPronunciamientosSolicitados() {
		List<Integer> identificadoresPronunciamientos = new ArrayList<Integer>();
		long idInstanciaProceso = bandejaTareasBean.getTarea().getProcessInstanceId();
		try {
			if (idInstanciaProceso != 0) {
				Map<String, Object> variablesProceso = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(), idInstanciaProceso);
				String idPronunciamientos = (String) variablesProceso.get("identificadoresPronunciamientos");

				if (!("".equals(idPronunciamientos) || idPronunciamientos == null)) {

					String[] arreglo = idPronunciamientos.split(Constantes.SEPARADOR);

					for (int i = 0; i < arreglo.length; i++) {
						identificadoresPronunciamientos.add(Integer.parseInt(arreglo[i]));
					}
				}
			}
		} catch (JbpmException e) {
			LOG.error("Error al iniciar inicializar el bean", e);
		}
		return identificadoresPronunciamientos;
	}

}
