package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.CultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DosisCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.EfectoOrganismosAcuaticosFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaCultivoFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.PlagaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProductoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasDetalleFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.EfectoOrganismosAcuaticos;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.TipoOrganismoAcuatico;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ViewScoped
@ManagedBean
public class VerInformacionPlaguicidasController {
	
	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProductoPquaFacade productoPquaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private CultivoFacade cultivoFacade;
	@EJB
	private PlagaFacade plagaFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
    private OrganizacionFacade organizacionFacade;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
    private ProyectoPlaguicidasDetalleFacade proyectoPlaguicidasDetalleFacade;
	@EJB
    private PlagaCultivoFacade plagaCultivoFacade;
	@EJB
    private DosisCultivoFacade dosisCultivoFacade;
	@EJB
    private EfectoOrganismosAcuaticosFacade efectoOrganismosAcuaticosFacade;
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
	private BandejaFacade bandejaFacade;
	
	@Getter
	@Setter
	private List<ProyectoPlaguicidasDetalle> listaDetalleProyectoPlaguicidas;
	
	@Getter
	@Setter
	private List<EfectoOrganismosAcuaticos> listaEfectosPeces, listaEfectosCrustaceos, listaEfectosAlgas, listaComponentesResumen;
	
	@Getter
	@Setter
	private ProductoPqua productoReporte;

	@Getter
	@Setter
	private DocumentoPqua documentoOficioProrroga, documentoRespaldo, documentoEtiqueta;
	
	@Getter
	@Setter
	private ProyectoPlaguicidas proyectoPlaguicidas;
	
	@Getter
	@Setter
	private Boolean aceptarCondiciones, mostrarObservaciones, editarObservaciones;
	
	@Getter
	@Setter
	private String nombreOperador, cedula;
	
	@Getter
	@Setter
	private Integer idProyecto;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init(){
		
		try {
			
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			String codigoProyecto = (String) variables.get("tramite");
			
			proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(codigoProyecto);
			productoReporte = proyectoPlaguicidas.getProductoPqua();
			idProyecto = proyectoPlaguicidas.getId();
			
			cargarDatos();
			
			mostrarObservaciones = false;
			editarObservaciones = false;
			
			String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
			if (tarea.equals("elaborarInformeTecnicoOficio")) {
				mostrarObservaciones = true;
				editarObservaciones = true;
			} else if (tarea.equals("subsanarObservacionesPqua")) {
				mostrarObservaciones = true;
				editarObservaciones = false;
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.ERROR_INICIALIZAR_DATOS);
		}
	}
	
	public void cargarDatos() throws Exception {
		aceptarCondiciones = true;
		
		Usuario usuario = usuarioFacade.buscarUsuario(proyectoPlaguicidas.getUsuarioCreacion());
		Persona titular = usuario.getPersona();
		Organizacion org = organizacionFacade.buscarPorPersona(titular, usuario.getNombre());	            
		if (org == null) {
			nombreOperador = titular.getNombre();
			cedula = titular.getPin();
		} else {	            	
			nombreOperador = org.getPersona().getNombre();	                
			cedula=org.getPersona().getPin();
		}
		
		listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
		
		listaEfectosPeces = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_PECES);
		listaEfectosCrustaceos = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_CRUSTACEOS);
		listaEfectosAlgas = efectoOrganismosAcuaticosFacade.getPorProyectoTipo(proyectoPlaguicidas.getId(), TipoOrganismoAcuatico.ID_ALGAS);
		
		List<DocumentoPqua> documentosProrroga = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_OFICIO_PRORROGA, "RespaldoOficioProrroga");
		if(documentosProrroga != null && documentosProrroga.size() > 0) {
			documentoOficioProrroga = documentosProrroga.get(0);
		}
		
		List<DocumentoPqua> documentosRespaldo = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_RESPALDO, "RespaldoProyectoPlaguicidas");
		if(documentosRespaldo != null && documentosRespaldo.size() > 0) {
			documentoRespaldo = documentosRespaldo.get(0);
		}
		
		List<DocumentoPqua> documentosEtiqueta = documentosFacade.documentoPorTablaIdPorIdDoc(proyectoPlaguicidas.getId(), TipoDocumentoSistema.PQUA_DOCUMENTO_ETIQUETA, "RespaldoEtiquetaProyectoPlaguicidas");
		if(documentosEtiqueta != null && documentosEtiqueta.size() > 0) {
			documentoEtiqueta = documentosEtiqueta.get(0);
		}
		
		listaComponentesResumen = new ArrayList<>();
		
		List<EfectoOrganismosAcuaticos> listaEfectosTotal = new ArrayList<>();
		listaEfectosTotal.addAll(listaEfectosPeces);
		listaEfectosTotal.addAll(listaEfectosCrustaceos);
		listaEfectosTotal.addAll(listaEfectosAlgas);
		
		listaComponentesResumen = new ArrayList<>();
		
		for (EfectoOrganismosAcuaticos item : listaEfectosTotal) {
			List<EfectoOrganismosAcuaticos> listaComponentesResumenAux = new ArrayList<>();
			listaComponentesResumenAux.addAll(listaComponentesResumen);
			
			if(listaComponentesResumen.size() > 0) {
				Boolean existeComponente = false;
				for (EfectoOrganismosAcuaticos componente : listaComponentesResumenAux) {
					if(componente.getIngredienteActivo().equals(item.getIngredienteActivo())) {
						existeComponente = true;
						if(item.getCategoria() != null && item.getCategoria().getId() != null) {
							Integer ultimaCategoria = 0;
							if(componente.getCategoria() != null && componente.getCategoria().getId() != null) {
								ultimaCategoria = Integer.valueOf(componente.getCategoria().getValor().toString());
							}
							Integer nuevaCategoria = Integer.valueOf(item.getCategoria().getValor().toString()); 
							
							if(ultimaCategoria == 0 || nuevaCategoria < ultimaCategoria) {
								listaComponentesResumen.remove(componente);
								listaComponentesResumen.add(item);
							}
						}
					}
				}
				
				if(!existeComponente) {
					listaComponentesResumen.add(item);
				}
			} else {
				listaComponentesResumen.add(item);
			}
		}
	}
	
	public StreamedContent descargar(DocumentoPqua documentoDescarga) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documentoDescarga != null && documentoDescarga.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoDescarga.getIdAlfresco());
			} else if (documentoDescarga.getContenidoDocumento() != null) {
				documentoContent = documentoDescarga.getContenidoDocumento();
			}
			
			if (documentoDescarga != null && documentoDescarga.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoDescarga.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void cerrar(){
		JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
	}
	
	public void regresar() {
		try {
			String url = JsfUtil.getCurrentTask().getTaskSummary().getDescription();
			 JsfUtil.redirectTo(url);
		} catch (Exception e) {
		}
	}
	
}
