/*
 * Copyright 2015 MAGMASOFT Innovando Tecnologia
 * Todos los derechos reservados 
 */
package ec.gob.ambiente.suia.comun.bean;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import ec.gob.ambiente.core.interfaces.CompleteOperation;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.comun.base.ComunBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.Pronunciamiento;
import ec.gob.ambiente.suia.domain.PronunciamientoDocumentos;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.EstadoPronunciamiento;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.pronunciamiento.facade.PronunciamientoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

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
@SessionScoped
public class EmitirPronunciamientoComunBean extends ComunBean implements Serializable {

	private static final long serialVersionUID = -5938260028359656351L;

	private Pronunciamiento pronunciamiento;

	@Getter
	@Setter
	private Date fecha;

	private Map<String, Object> paramsTaskToComplete;

	@Getter
	@Setter
	private long idTarea;

	@Getter
	@Setter
	private long idInstanciaProceso;

	@Getter
	@Setter
	private Usuario usuario;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@EJB
	private ProcesoFacade procesoFacade;

	@EJB
	private PronunciamientoFacade pronunciamientoFacade;

	private List<UploadedFile> ficherosSubidos;

	private static final Logger LOG = Logger.getLogger(EmitirPronunciamientoComunBean.class);

	public void uploadListener(FileUploadEvent event) {
		LOG.info("Aqui va el codigo para salvar el fichero");

		getFicherosSubidos().add(event.getFile());
	}

	public StreamedContent getFileToDownload(final UploadedFile file) {
		DefaultStreamedContent content = null;
		if (file != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(file.getContents()));
			content.setName(file.getFileName());
		}
		return content;
	}

	public void eliminarArchivo(final UploadedFile file) {
		ficherosSubidos.remove(file);
	}

	public Pronunciamiento getPronunciamiento() {
		return pronunciamiento == null ? pronunciamiento = new Pronunciamiento() : pronunciamiento;
	}

	public void setPronunciamiento(Pronunciamiento pronunciamiento) {
		this.pronunciamiento = pronunciamiento;
	}

	public List<UploadedFile> getFicherosSubidos() {
		return ficherosSubidos == null ? ficherosSubidos = new ArrayList<UploadedFile>() : ficherosSubidos;
	}

	public void setFicherosSubidos(List<UploadedFile> ficherosSubidos) {
		this.ficherosSubidos = ficherosSubidos;
	}

	public Map<String, Object> getParamsTaskToComplete() {
		return paramsTaskToComplete == null ? paramsTaskToComplete = new ConcurrentHashMap<String, Object>()
				: paramsTaskToComplete;
	}

	public void setParamsTaskToComplete(Map<String, Object> paramsTaskToComplete) {
		this.paramsTaskToComplete = paramsTaskToComplete;
	}

	public void initFunction(long idTarea, long idInstanciaProceso, Usuario usuario,
			Map<String, Object> paramsTaskToComplete, String nextURL, CompleteOperation completeOperation) {
		super.initFunction(nextURL, completeOperation);
		this.idTarea = idTarea;
		this.idInstanciaProceso = idInstanciaProceso;
		this.paramsTaskToComplete = paramsTaskToComplete;
		this.usuario = usuario;
		this.fecha = new Date();

		List<Integer> pronunciamientos = pronunciamientoFacade.obtenerIdsIntegerPronunciamientosSolicitados(
				idInstanciaProceso, loginBean.getUsuario());

		Pronunciamiento pronunciamiento = pronunciamientoFacade.getPronunciamientosPorUsuarioYProceso(pronunciamientos,
				loginBean.getUsuario().getId());
		if (pronunciamiento != null)
			this.pronunciamiento = pronunciamiento;
		getPronunciamiento().setFecha(fecha);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ec.gob.ambiente.suia.comun.base.ComunBean#getFunctionURL()
	 */
	@Override
	public String getFunctionURL() {
		return "/comun/emitirPronunciamiento.jsf";
		// return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ec.gob.ambiente.suia.comun.base.ComunBean#cleanData()
	 */
	@Override
	public void cleanData() {
		pronunciamiento = null;
		ficherosSubidos = null;
		paramsTaskToComplete = null;
		fecha = null;
		idTarea = 0;
		idInstanciaProceso = 0;
		usuario = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ec.gob.ambiente.suia.comun.base.ComunBean#executeBusinessLogic()
	 */
	@Override
	public boolean executeBusinessLogic(Object object) {
		// List<Documento> documentos = new ArrayList<Documento>();

		List<PronunciamientoDocumentos> pronuncDocum = new ArrayList<PronunciamientoDocumentos>();
		if (pronunciamiento != null && pronunciamiento.isPersisted()) {
			try {

				for (int i = 0; i < ficherosSubidos.size(); i++) {
					Documento documento = new Documento();
					documento.setNombre(ficherosSubidos.get(i).getFileName());
					pronunciamientoFacade.saveOrUpdate(documento);
					PronunciamientoDocumentos pronunciamientoDocumentos = new PronunciamientoDocumentos();
					pronunciamientoDocumentos.setDocumento(documento);
					pronunciamientoDocumentos.setPronunciamiento(getPronunciamiento());
					pronunciamientoFacade.saveOrUpdate(pronunciamientoDocumentos);
					pronuncDocum.add(pronunciamientoDocumentos);
					// documentos.add(documento);
				}
				// getPronunciamiento().setDocumentos(documentos);
				pronunciamiento.setPronunciamientoDocumentos(pronuncDocum);
				if (usuario != null)
					getPronunciamiento().setUsuario(usuario);

				pronunciamiento.setEstadoPronunciamiento(EstadoPronunciamiento.RECIBIDO);

				if (pronunciamiento.isPersisted()) {
					pronunciamientoFacade.saveOrUpdate(pronunciamiento);
				} else {
					pronunciamientoFacade.saveOrUpdate(pronunciamiento);
					/*
					 * pronunciamientoFacade.adicionarPronunciamientoVariableProceso
					 * (pronunciamiento, idInstanciaProceso,
					 * loginBean.getNombreUsuario(), loginBean.getPassword());
					 */
				}
				procesoFacade.aprobarTarea(loginBean.getUsuario(), idTarea, idInstanciaProceso, paramsTaskToComplete);
			} catch (JbpmException e) {
				LOG.error(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA, e);
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
				return false;
			}
			return true;
		}
		return false;
	}
}
