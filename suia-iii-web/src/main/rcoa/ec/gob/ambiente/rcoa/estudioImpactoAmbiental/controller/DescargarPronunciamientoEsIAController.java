package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.CatalogoGeneralEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformeTecnicoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.OficioPronunciamientoEsIAFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.CatalogoGeneralEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.OficioPronunciamientoEsIA;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarPronunciamientoEsIAController implements Serializable{

	 private static final long serialVersionUID = -875087443147320594L;
	  
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private OficioPronunciamientoEsIAFacade oficioPronunciamientoEsIAFacade;
	
	@EJB
	private InformeTecnicoEsIAFacade informeTecnicoEsIAFacade;
	
	@EJB
	private CatalogoGeneralEsIAFacade catalogoGeneralEsIAFacade;

	@Getter
	@Setter
	private DocumentoEstudioImpacto documento;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;
	
	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass;
	
	@Getter
	@Setter
	private Boolean mostrarEncuesta;
	
	@Getter
	@Setter
	private Integer idProyecto, tipoResultado;
	
	@Getter
	@Setter
	private boolean showSurveyD = false;
	
	@Getter
	@Setter
	private String urlLinkSurvey = Constantes.getPropertyAsString("suia.survey.link");
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	private Integer tipoPronunciamiento;
	private String tecnicoResponsable="";
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

		String idProyectoString = (String) variables.get("idProyecto");
		idProyecto = Integer.valueOf(idProyectoString);
		
		String tipoPronunciamientoString = (String) variables.get("tipoPronunciamiento");
		tipoPronunciamiento = Integer.valueOf(tipoPronunciamientoString);

		tecnicoResponsable=(String) variables.get("tecnicoResponsable");
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		tramite = proyecto.getCodigoUnicoAmbiental();
		
		esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
		
		Integer idTableClass = 0;
		tipoResultado = tipoPronunciamiento;
		
		if(tipoPronunciamiento.equals(1)) {
			InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
			OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), informeTecnico.getId());
			
			tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO;
			docuTableClass = OficioPronunciamientoEsIA.class.getSimpleName();
			idTableClass = oficioPronunciamiento.getId();
		} else if(tipoPronunciamiento.equals(3)) {
			
			OficioPronunciamientoEsIA oficioAutomatico = oficioPronunciamientoEsIAFacade.getPorEstudioTipoOficio(esiaProyecto.getId(), OficioPronunciamientoEsIA.oficioArchivoAutomatico);
			if(oficioAutomatico != null) {
				tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_ARCHIVACION_AUTOMATICA;
				docuTableClass = "OficioPronunciamientoArchivacionEsIA";
				idTableClass = esiaProyecto.getId();
				tipoResultado = 2;
			} else {
				InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
				OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), informeTecnico.getId());
				
				tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO;
				docuTableClass = OficioPronunciamientoEsIA.class.getSimpleName();
				idTableClass = oficioPronunciamiento.getId();
			}
		}else if(tipoPronunciamiento.equals(4)){
			InformeTecnicoEsIA informeTecnico = informeTecnicoEsIAFacade.obtenerPorEstudioTipoInforme(esiaProyecto, InformeTecnicoEsIA.consolidado);
			OficioPronunciamientoEsIA oficioPronunciamiento = oficioPronunciamientoEsIAFacade.getPorEstudioInforme(esiaProyecto.getId(), informeTecnico.getId());
			
			tipoDocumento = TipoDocumentoSistema.EIA_OFICIO_APROBACION_CONSOLIDADO;
			docuTableClass = OficioPronunciamientoEsIA.class.getSimpleName();
			idTableClass = oficioPronunciamiento.getId();
		}
		
		verEncuesta();
		
		List<DocumentoEstudioImpacto> listaDocumentosInt = documentosFacade.documentoXTablaIdXIdDocLista(idTableClass, docuTableClass, tipoDocumento);
		if (listaDocumentosInt.size() > 0) 
			documento = listaDocumentosInt.get(0);
	}
	
	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProject(tramite)){			
			mostrarEncuesta = true;
		}else{
			mostrarEncuesta = false;
		}	
		
		return mostrarEncuesta;
	}
	
	public void showSurvey() {		
		
		String url = urlLinkSurvey;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "pronunciamiento-esia-rcoa";
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		urlLinkSurvey = url;
		showSurveyD = true;
	}
	
	private String getProponente() {
		try {
			Usuario user = loginBean.getUsuario();
			if (user.getNombre().length() == 13) {
				Organizacion orga = organizacionFacade.buscarPorRuc(user.getNombre());
				if (orga != null)
					return "juridico";
			}
			return "natural";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public StreamedContent descargar() throws Exception {
		try{
			DefaultStreamedContent content = new DefaultStreamedContent();
			if (documento != null) {
				if (documento.getContenidoDocumento() == null) {
					documento.setContenidoDocumento(documentosFacade.descargar(documento.getAlfrescoId()));
				}
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()), documento.getExtesion());
				content.setName(documento.getNombre());
				
				documentoDescargado = true;
			} else {
				content = null;
				JsfUtil.addMessageError("No se pudo descargar documento.");
			}
			
			return content;
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar documento.");			
			e.printStackTrace();
		}
		return null;
	}
	
	public void finalizar() {
		try {
			if (!documentoDescargado) {
				JsfUtil.addMessageError("Debe descargar el oficio de pronunciamiento antes de finalizar.");
				return;
			} 
			
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);
			
			documento.setIdProceso(bandejaTareasBean.getTarea().getProcessInstanceId());
			documentosFacade.guardar(getDocumento());
			
			CatalogoGeneralEsIA resultado = catalogoGeneralEsIAFacade.buscarPorCodigo("pronunciamiento_estudio_" + tipoResultado);
			
			esiaProyecto.setResultadoFinalEia(resultado.getId());
			esiaProyecto.setFechaFinEstudio(new Date());
			informacionProyectoEIACoaFacade.guardar(esiaProyecto);
			
			if(tipoPronunciamiento.equals(1)) {
				Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
				parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
				parametros.put("tramite",tramite);					
				parametros.put("idProyecto", idProyecto);
				parametros.put("numeroFacilitadores", esiaProyecto.getNumeroFacilitadores());
				parametros.put("facilitadorAdicional", false);
				
				try {
					Boolean existeNormativa = Constantes.getPropertyAsBoolean("rcoa.existe.normativa.ppc");
					if(existeNormativa) {
						parametros.put("tecnicoRealizoEIA", tecnicoResponsable);
						procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_PARTICIPACION_CIUDADANA, tramite, parametros);
					} else {
						parametros.put("tecnicoResponsableEIA", tecnicoResponsable);
						procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_PARTICIPACION_CIUDADANA_BYPASS, tramite, parametros);
					}
					
				} catch (JbpmException e) {
					e.printStackTrace();
				}
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
			JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);

		} catch (Exception e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}