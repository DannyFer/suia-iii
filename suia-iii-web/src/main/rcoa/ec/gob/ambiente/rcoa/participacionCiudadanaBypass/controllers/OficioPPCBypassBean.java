package ec.gob.ambiente.rcoa.participacionCiudadanaBypass.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.ProyectoFasesCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.participacionCiudadana.dto.EntityOficioPPCBypass;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.DocumentoPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.FacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ProyectoFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.facade.ReporteFacilitadorPPCFacade;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.DocumentosPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ProyectoFacilitadorPPC;
import ec.gob.ambiente.rcoa.participacionCiudadana.model.ReporteFacilitadorPPC;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class OficioPPCBypassBean {
	
	private static final Logger LOG = Logger.getLogger(OficioPPCBypassBean.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private DocumentoPPCFacade documentosFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private FacilitadorPPCFacade facilitadorFacade;
	@EJB
	private ProyectoFacilitadorPPCFacade proyectoFacilitadorPPCFacade;
	@EJB
	private ReporteFacilitadorPPCFacade reporteFacilitadorPPCFacade;
	@EJB
	private DocumentoPPCFacade documentoPPCFacade;
	@EJB
    private UsuarioFacade usuarioFacade;
	@EJB
	public CatalogoCoaFacade catalogoCoaFacade;
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	@EJB
	private ProyectoFasesCoaFacade proyectoFasesCoaFacade;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{bandejaTareasBean}")
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	@Getter
	@Setter
	private ProyectoFacilitadorPPC proyectoFacilitadorPPC;
	@Getter
	@Setter
	private ReporteFacilitadorPPC oficioPronunciamiento = new ReporteFacilitadorPPC();
	@Getter
	@Setter
	private EntityOficioPPCBypass entityOficio = new EntityOficioPPCBypass();
	@Getter
	@Setter
	private DocumentosPPC documentoOficio = new DocumentosPPC();
	@Getter
	@Setter
	private CatalogoGeneralCoa tipoOficio;
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;
	
	@Inject
	private UtilPPCBypass utilPPC;

	@Getter
    @Setter
    private byte[] archivoOficio;
	@Getter
    @Setter
    private String oficioPath, nombreReporte;
    @Getter
    @Setter
    private File oficio;
    @Getter
    @Setter
    private String tramite;
    @Getter
    @Setter
    private Integer idProyecto;
    @Getter
    @Setter
    private Boolean oficioGuardado = false, descargado = false, esAprobacion;
    @Getter
	@Setter
	private boolean token, subido;
    
    private Area areaAutoridad;
    
	@PostConstruct
	public void inicio() {
		try {
			variables = procesoFacade.recuperarVariablesProceso(loginBean.getUsuario(),bandejaTareasBean.getTarea().getProcessInstanceId());
			tramite = (String) variables.get("tramite");
			
			idProyecto = Integer.valueOf((String)variables.get(Constantes.ID_PROYECTO));
			
			String tipoPronunciamiento = (String) variables.get("esAprobacion");
			esAprobacion = Boolean.parseBoolean(tipoPronunciamiento);
			
			proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
			proyectoFacilitadorPPC = proyectoFacilitadorPPCFacade.getByIdProyecto(idProyecto);

			if(esAprobacion) {
				tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.aprobacion");
				tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_APROBACION;
			} else { 
				tipoOficio = catalogoCoaFacade.obtenerCatalogoPorCodigo("ppc.bypass.oficio.archivo");
				tipoDocumento = TipoDocumentoSistema.RCOA_PPC_BYPASS_OFICIO_ARCHIVO;
			}
			
			oficioPronunciamiento = reporteFacilitadorPPCFacade.getByIdProyectoFacilitadorTipoReporte(proyectoFacilitadorPPC.getId(), tipoOficio.getId());
			
			if(oficioPronunciamiento.getId() == null) {
				String numeroOficio = utilPPC.generarCodigoOficio(proyectoLicenciaCoa.getAreaResponsable());
				oficioPronunciamiento.setCodigoReporte(numeroOficio);
				oficioPronunciamiento.setProyectoFacilitadorPPC(proyectoFacilitadorPPC);
				oficioPronunciamiento.setTipoReporte(tipoOficio);
				
				reporteFacilitadorPPCFacade.guardar(oficioPronunciamiento);
			} else {
				oficioPronunciamiento.setTipoReporte(tipoOficio);
			}
			
			String rolAutoridad = "autoridad";
			if (proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC"))
				rolAutoridad = "subsecretario";
			
			usuarioAutoridad = utilPPC.asignarRol(proyectoLicenciaCoa, rolAutoridad);
			
			areaAutoridad = new Area();
			
			if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
				areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
			}else{
				areaAutoridad = proyectoLicenciaCoa.getAreaResponsable();
			}
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Error visualizar informe / oficio");
		}
	}
	
	public void generarOficio(Boolean marcaAgua) {
		try {
			PlantillaReporte plantillaReporte = this.documentosFacade.obtenerPlantillaReporte(tipoDocumento.getIdTipoDocumento());
			
			asignarDatosEntityOficio();
			
			nombreReporte = "Oficio pronunciamiento " + oficioPronunciamiento.getCodigoReporte().replace("/","-") + ".pdf";
			File oficioPdfAux = UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(),nombreReporte, true, entityOficio);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			String reporteHtmlfinal = oficioPdf.getName();
			archivoOficio = Files.readAllBytes(path);
			oficio = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(oficio);
			file.write(Files.readAllBytes(path));
			file.close();
			setOficioPath(JsfUtil.devolverContexto("/reportesHtml/"+ reporteHtmlfinal));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void asignarDatosEntityOficio() {
		try {
			DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			
			Usuario proponente = proyectoLicenciaCoa.getUsuario();
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proponente.getNombre());
			String nombreProponente = proponente.getPersona().getNombre();
			String tratamiento = (organizacion == null) ? (proponente.getPersona().getTipoTratos() == null ? " " : proponente.getPersona().getTipoTratos().getNombre()) : (organizacion.getPersona().getTipoTratos() == null ? " " : organizacion.getPersona().getTipoTratos().getNombre());

			entityOficio = new EntityOficioPPCBypass();

			entityOficio.setNumero(oficioPronunciamiento.getCodigoReporte());
			entityOficio.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
			if(!proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equalsIgnoreCase("PC")){
		        	// incluir informacion de la sede de la zonal en el documento
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String sedeZonal = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTORCOA", proyectoLicenciaCoa, null, null);
				entityOficio.setCiudad(sedeZonal);
			}
			entityOficio.setFechaEmision(formatoFechaEmision.format(new Date()));

			String cargo;
			if(organizacion == null) {
				if (proponente.getPersona().getPosicion() != null) {
					cargo = proponente.getPersona().getPosicion();
					entityOficio.setDisplayCargo("inline");
				} else {
					cargo = "";
					entityOficio.setDisplayCargo("none");
				}
				entityOficio.setDisplayEmpresa("none");
				entityOficio.setNombreOperador(nombreProponente);
				entityOficio.setCargoOperador(cargo);
				entityOficio.setNombreEmpresa(" ");
				entityOficio.setTratamientoOperador(tratamiento);
			} else {
				if (organizacion.getCargoRepresentante() != null) {
					cargo = organizacion.getCargoRepresentante();
					entityOficio.setDisplayCargo("inline");
				} else {
					cargo = "";
					entityOficio.setDisplayCargo("none");
				}
				entityOficio.setNombreOperador(nombreProponente);
				entityOficio.setCargoOperador(cargo);
				entityOficio.setNombreEmpresa(organizacion.getNombre());
				entityOficio.setDisplayEmpresa("inline");
				entityOficio.setTratamientoOperador(tratamiento);
			}

			entityOficio.setAsunto(oficioPronunciamiento.getAsunto());
			entityOficio.setAntecedentes(oficioPronunciamiento.getAntecedentes());
			entityOficio.setConclusiones(oficioPronunciamiento.getConclusion());

			entityOficio.setNombreResponsable(usuarioAutoridad.getPersona().getNombre());
//			entityOficio.setAreaResponsable(usuarioAutoridad.getArea().getAreaName());
			entityOficio.setAreaResponsable(areaAutoridad.getAreaName());
			
			if(proyectoLicenciaCoa.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				entityOficio.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);
			
			if(JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getTipoArea().getId()==3)
				entityOficio.setSiglasEnte(JsfUtil.getLoggedUser().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation());
			
//			if(JsfUtil.getLoggedUser().getArea().getTipoArea().getId()==3)
//				entityOficio.setSiglasEnte(JsfUtil.getLoggedUser().getArea().getAreaAbbreviation());
			
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos del Oficio.");
		}
	}
	
	public StreamedContent getStreamOficio(String name, byte[] fileContent) throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (fileContent != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
            		fileContent),  "application/pdf");
            content.setName(name);
        }
        return content;
    }
	
	public void guardarOficio(Boolean showMensaje) {
		oficioPronunciamiento = reporteFacilitadorPPCFacade.guardar(oficioPronunciamiento);
		
		proyectoFacilitadorPPCFacade.guardar(proyectoFacilitadorPPC);
		
		oficioGuardado = true;
		
		generarOficio(true);

		if(showMensaje)
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public DocumentosPPC guardarDocumentoOficioAlfresco() {
		try {
			generarOficio(false);
			
			documentoOficio = new DocumentosPPC();
			documentoOficio.setContenidoDocumento(archivoOficio);
			documentoOficio.setNombreDocumento(nombreReporte);
			documentoOficio.setExtencionDocumento(".pdf");		
			documentoOficio.setTipo("application/pdf");
			documentoOficio.setNombreTabla("OficioPronunciamientoPPC");
			documentoOficio.setIdTabla(oficioPronunciamiento.getId());

			documentoOficio = documentosFacade.guardarDocumentoAlfrescoPPC(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),"PARTICIPACIONCIUDADANA",0L,documentoOficio, tipoDocumento);

			return documentoOficio;

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

		return null;
	}
	
}
