/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.composite.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import java.io.Serializable;

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
public class CorregirPronunciamientoCC implements Serializable{
    
    private static final long serialVersionUID = -3526371288569229686L;

	@Setter
	private Pronunciamiento pronunciamiento;

	@Getter
	@Setter
	private String fechaMostrar;

	List<UploadedFile> ficherosSubidos;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;

	private static final Logger LOG = Logger.getLogger(CorregirPronunciamientoCC.class);

	public Pronunciamiento getPronunciamiento() {
		return pronunciamiento == null ? pronunciamiento = new Pronunciamiento() : pronunciamiento;
	}

	public List<UploadedFile> getFicherosSubidos() {
		return ficherosSubidos == null ? ficherosSubidos = new ArrayList<UploadedFile>() : ficherosSubidos;
	}

	public void setFicherosSubidos(List<UploadedFile> ficherosSubidos) {
		this.ficherosSubidos = ficherosSubidos;
	}

	@PostConstruct
	public void initFunction() {
		try {
			long idInstanciaProceso = bandejaTareasBean.getProcessId();

			List<Integer> pronunciamientos = pronunciamientoFacade.obtenerIdsIntegerPronunciamientosSolicitados(
					idInstanciaProceso, loginBean.getUsuario());

			Pronunciamiento pronunciamiento = pronunciamientoFacade.getPronunciamientosPorUsuarioYProceso(
					pronunciamientos, loginBean.getUsuario().getId());

			if (pronunciamiento != null)
				this.pronunciamiento = pronunciamiento;
		} catch (Exception e) {
			LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA, e);
		}

	}
}
