package ec.gob.ambiente.rcoa.revisionDiagnosticoAmbiental.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.dto.EntityInformeOficioDiagnosticoAmbiental;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.InformePronunciamientoDiagnosticoFacade;
import ec.gob.ambiente.rcoa.facade.ObservacionesPreliminarFacade;
import ec.gob.ambiente.rcoa.facade.OficioPronunciamientoDiagnosticoFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.InformePronunciamientoDiagnostico;
import ec.gob.ambiente.rcoa.model.ObservacionesPreliminar;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoDiagnostico;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.BandejaFacade;
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
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeOficioDiagnosticoAmbientalBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Logger LOG = Logger.getLogger(InformeOficioDiagnosticoAmbientalBean.class);
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private  UsuarioFacade usuarioFacade;
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private InformePronunciamientoDiagnosticoFacade informeFacade;
	
	@EJB
	private DocumentosCoaFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private BandejaFacade bandejaFacade;
	
	@EJB
    private ObservacionesPreliminarFacade observacionesPreliminarFacade;
	
	@EJB
	private OficioPronunciamientoDiagnosticoFacade oficioPronunciamientoFacade;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporte, plantillaOficioMemo;
	
	@Getter
	@Setter
	private InformePronunciamientoDiagnostico informeTecnico;
	
	@Getter
	@Setter
	private OficioPronunciamientoDiagnostico oficioPronunciamiento;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad, tecnicoResponsable;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private UbicacionesGeografica ubicacionPrincipal = new UbicacionesGeografica();
	
	@Getter
	@Setter
	private DocumentosCOA documentoInforme, documentoOficioPronunciamiento;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroObservaciones, idTarea, tipoInforme, numeroRevision, idInformePrincipal;
	
	@Getter
	@Setter
	private String urlReporte, nombreReporte, resumenObservaciones;
	
	@Getter
	@Setter
	private Boolean existeObservacionesDiagnostico, esTecnico, requiereCorrecciones, esRequerido, datosGuardados;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String idProyectoString = (String) variables.get("u_idProyecto");
			idProyecto = Integer.valueOf(idProyectoString);
						
			numeroRevision = 1;
			String revision = (String) variables.get("numeroRevisionDiagnostico");
			if(revision != null) 
				numeroRevision = Integer.parseInt(revision);

			proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);

			String usrTecnicoResponsable = (String) variables.get("tecnicoResponsablePPC");
			tecnicoResponsable = usuarioFacade.buscarUsuario(usrTecnicoResponsable);
			
			areaTramite = proyecto.getAreaResponsable();
			
			ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
			
			idTarea = (int)bandejaTareasBean.getTarea().getTaskId();

			String usrTecnico = (String) variables.get("u_tecnicoResponsable");
			tecnicoResponsable = usuarioFacade.buscarUsuario(usrTecnico);
			
			//Recuperar el usuario responsable de la firma del oficio de archivación de proyecto
			String tipoRol = "role.esia.cz.autoridad";
			
			if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				tipoRol = "role.esia.pc.autoridad";
			else if(areaTramite.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			
			String rolAutoridad = Constantes.getRoleAreaName(tipoRol);
			
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaTramite);
			if (listaUsuario == null || listaUsuario.size() == 0)			
			{
				Log.debug("No se encontró autoridad ambiental en "+ areaTramite.getAreaName());
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return;
			}else{
				usuarioAutoridad = listaUsuario.get(0);
			}
			
			cargarDatos();
			
			validarExisteObservacionesDiagnostico();

			idInformePrincipal = (informeTecnico.getIdInformePrincipal() != null) ? informeTecnico.getIdInformePrincipal() : informeTecnico.getId();
			
			datosGuardados = false;
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe.");
		}
	}
	
	public void cargarDatos() throws Exception {
		String tarea = bandejaFacade.getNombreTarea(JsfUtil.getCurrentTask().getTaskId());
		requiereCorrecciones = false;
		
		esTecnico = false;
		if(tarea.contains("revisarInformacionRegistroPreliminar"))
			esTecnico = true;
		
		informeTecnico = informeFacade.getPorProyecto(proyecto.getId());
		oficioPronunciamiento = oficioPronunciamientoFacade.getPorProyecto(proyecto.getId());
		
		if(esTecnico) {
			
			if(!tecnicoResponsable.getNombre().equals(JsfUtil.getLoggedUser().getNombre())) {
				Map<String, Object> parametros = new HashMap<>();
				parametros.put("u_tecnicoResponsable", JsfUtil.getLoggedUser().getNombre());
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				tecnicoResponsable = usuarioFacade.buscarUsuario(JsfUtil.getLoggedUser().getNombre());
			}
			
			Area areaTecnico = new Area();
			
			if(tecnicoResponsable.getListaAreaUsuario() != null && tecnicoResponsable.getListaAreaUsuario().size() == 1){
				areaTecnico = tecnicoResponsable.getListaAreaUsuario().get(0).getArea();
			}else if(tecnicoResponsable.getListaAreaUsuario() != null){
				areaTecnico = proyecto.getAreaInventarioForestal();
			}			
			
			if(informeTecnico.getNumeroRevision() != numeroRevision) {
				informeTecnico = new InformePronunciamientoDiagnostico();
				informeTecnico.setNombreReporte("informeTecnico.pdf");
				informeTecnico.setProyectoLicenciaCoa(proyecto);
				informeTecnico.setIdTarea(idTarea);
				informeTecnico.setTipoPronunciamiento(tipoInforme);
				informeTecnico.setFechaElaboracion(new Date());
				informeTecnico.setNumeroRevision(numeroRevision);
				informeFacade.guardar(informeTecnico, areaTecnico);
//				informeFacade.guardar(informeTecnico, tecnicoResponsable.getArea());
				
			} else if(informeTecnico.getIdTarea() != null && !informeTecnico.getIdTarea().equals(idTarea)) {
				InformePronunciamientoDiagnostico informeTecnicoOriginal = (InformePronunciamientoDiagnostico) SerializationUtils.clone(informeTecnico);
				informeTecnicoOriginal.setId(null);
				informeTecnicoOriginal.setCodigo(null);
				informeTecnicoOriginal.setIdTarea(idTarea);
				informeTecnicoOriginal.setFechaElaboracion(new Date());
				informeTecnicoOriginal.setIdInformePrincipal(informeTecnico.getIdInformePrincipal() != null ? informeTecnico.getIdInformePrincipal() : informeTecnico.getId());
				informeTecnico = informeFacade.guardar(informeTecnicoOriginal, areaTecnico);
//				informeTecnico = informeFacade.guardar(informeTecnicoOriginal, tecnicoResponsable.getArea());
			}

			if(oficioPronunciamiento.getNumeroRevision() != numeroRevision) {
				oficioPronunciamiento = new OficioPronunciamientoDiagnostico();
				oficioPronunciamiento.setNombreReporte("informeTecnico.pdf");
				oficioPronunciamiento.setProyectoLicenciaCoa(proyecto);
				oficioPronunciamiento.setIdTarea(idTarea);
				oficioPronunciamiento.setFechaElaboracion(new Date());
				oficioPronunciamiento.setNumeroRevision(numeroRevision);
				
				oficioPronunciamientoFacade.guardar(oficioPronunciamiento, areaTramite);	
			}
		} 
	}
	
	public void validarExisteObservacionesDiagnostico() throws ServiceException {
		existeObservacionesDiagnostico = false;
		String nombreClaseObservaciones = "observacionesDiagnosticoAmbiental";
		Integer idClaseObservaciones = proyecto.getId();
		List<ObservacionesPreliminar> observacionesPendientes = observacionesPreliminarFacade.listarPorIdClaseNombreClaseNoCorregidas(
				idClaseObservaciones, nombreClaseObservaciones);
		
		if(observacionesPendientes.size() > 0) {
			existeObservacionesDiagnostico = true;
			resumenObservaciones = "<ul>";
			for (ObservacionesPreliminar observacionesEsIA : observacionesPendientes) {
				resumenObservaciones += "<li>";
				resumenObservaciones += observacionesEsIA.getCampo() + ": " + observacionesEsIA.getDescripcion();
				resumenObservaciones += "</li>";
			}
			resumenObservaciones += "</ul>";
		} else {
			resumenObservaciones = "";
		}
		if(informeTecnico.getFechaModificacion() == null)
			informeTecnico.setPronunciamiento(resumenObservaciones);
		
		if(oficioPronunciamiento.getFechaModificacion() == null)
			oficioPronunciamiento.setPronunciamiento(resumenObservaciones);
		
		tipoInforme = (existeObservacionesDiagnostico) ? 1: 2;
	}
	
	public StreamedContent getStreamInforme() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (informeTecnico != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(informeTecnico.getArchivoReporte()), "application/pdf");
            content.setName(informeTecnico.getNombreReporte());
        }
        return content;
    }

	public StreamedContent getStreamOficio() throws Exception {
        DefaultStreamedContent content = new DefaultStreamedContent();
        if (oficioPronunciamiento != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(
            		oficioPronunciamiento.getArchivoReporte()), "application/pdf");
            content.setName(oficioPronunciamiento.getNombreReporte());
        }
        return content;
    }
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_INFORME_DIAGNOSTICO_AMBIENTAL);
			
			
			informeTecnico.setNombreReporte("InformeTecnico_" + informeTecnico.getCodigo().replace("/", "-") + ".pdf");
			
			informeTecnico.setTipoPronunciamiento(tipoInforme);
			
			EntityInformeOficioDiagnosticoAmbiental entity = cargarDatosInforme();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					informeTecnico.getNombreReporte(), true, entity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeTecnico.setArchivoReporte(Files.readAllBytes(path));
			String reporteHtmlfinal = informeTecnico.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeTecnico.getArchivoReporte());
			file.close();
			informeTecnico.setPathReporte(JsfUtil.devolverContexto("/reportesHtml/" + informeTecnico.getNombreReporte()));
			
			urlReporte = informeTecnico.getPathReporte();
			nombreReporte = informeTecnico.getNombreReporte();
			archivoReporte = informeTecnico.getArchivoReporte();

		} catch (Exception e) {
			LOG.error(
					"Error al cargar el informe tecnico",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public EntityInformeOficioDiagnosticoAmbiental cargarDatosInforme() throws ServiceException {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		Usuario proponente = proyecto.getUsuario();
		Organizacion organizacion = organizacionFacade.buscarPorRuc(proponente.getNombre());
		String nombreProponente = proponente.getPersona().getNombre();
		String tratamiento = (organizacion == null) ? (proponente.getPersona().getTipoTratos() == null ? " " : proponente.getPersona().getTipoTratos().getNombre()) : (organizacion.getPersona().getTipoTratos() == null ? " " : organizacion.getPersona().getTipoTratos().getNombre());

		EntityInformeOficioDiagnosticoAmbiental entity = new EntityInformeOficioDiagnosticoAmbiental();
		
		entity.setNumero(informeTecnico.getCodigo());		
		entity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFechaEmision(formatoFechaEmision.format(new Date()));

		String cargo;
		if(organizacion == null) {
			if (proponente.getPersona().getPosicion() != null) {
				cargo = proponente.getPersona().getPosicion();
				entity.setDisplayCargo("inline");
			} else {
				cargo = "";
				entity.setDisplayCargo("none");
			}
			entity.setDisplayEmpresa("none");
			entity.setNombreOperador(nombreProponente);
			entity.setCargoOperador(cargo);
			entity.setNombreEmpresa(" ");
			entity.setTratamientoOperador(tratamiento);
		} else {
			if (organizacion.getCargoRepresentante() != null) {
				cargo = organizacion.getCargoRepresentante();
				entity.setDisplayCargo("inline");
			} else {
				cargo = "";
				entity.setDisplayCargo("none");
			}
			entity.setNombreOperador(nombreProponente);
			entity.setCargoOperador(cargo);
			entity.setNombreEmpresa(organizacion.getNombre());
			entity.setDisplayEmpresa("inline");
			entity.setTratamientoOperador(tratamiento);
		}

		entity.setAsunto(informeTecnico.getAsunto());
		entity.setPronunciamiento(informeTecnico.getPronunciamiento());

		entity.setNombreResponsable(tecnicoResponsable.getPersona().getNombre());
		
		Area areaTecnico = new Area();
		
		if(tecnicoResponsable.getListaAreaUsuario() != null && tecnicoResponsable.getListaAreaUsuario().size() == 1){
			areaTecnico = tecnicoResponsable.getListaAreaUsuario().get(0).getArea();
		}else if(tecnicoResponsable.getListaAreaUsuario() != null){
			areaTecnico = proyecto.getAreaInventarioForestal();//no area tramite porque esa variable contiene la zonal
		}
		
//		entity.setAreaResponsable(tecnicoResponsable.getArea().getAreaName());
		entity.setAreaResponsable(areaTecnico.getAreaName());
		
		
		if(areaTecnico.getTipoArea().getId().equals(3))
			entity.setSiglasEnte(areaTecnico.getAreaAbbreviation());
		
		return entity;
	}

	public void generarOficio(Boolean marcaAgua) {
		try {

			plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_OFICIO_DIAGNOSTICO_AMBIENTAL);
			
			
			oficioPronunciamiento.setNombreReporte("OficioPronunciamiento_" + oficioPronunciamiento.getCodigo().replace("/", "-") + ".pdf");
			
			EntityInformeOficioDiagnosticoAmbiental entity = cargarDatosOficio();

			File oficioPdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					oficioPronunciamiento.getNombreReporte(), true, entity);
			
			File oficioPdf = JsfUtil.fileMarcaAgua(oficioPdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(oficioPdf.getAbsolutePath());
			oficioPronunciamiento.setArchivoReporte(Files.readAllBytes(path));
			String reporteHtmlfinal = oficioPronunciamiento.getNombreReporte().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(oficioPronunciamiento.getArchivoReporte());
			file.close();
			oficioPronunciamiento.setPathReporte(JsfUtil.devolverContexto("/reportesHtml/" + oficioPronunciamiento.getNombreReporte()));
			
			urlReporte = oficioPronunciamiento.getPathReporte();
			nombreReporte = oficioPronunciamiento.getNombreReporte();
			archivoReporte = oficioPronunciamiento.getArchivoReporte();

		} catch (Exception e) {
			LOG.error(
					"Error al cargar el informe tecnico",
					e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	public EntityInformeOficioDiagnosticoAmbiental cargarDatosOficio() throws ServiceException {
		DateFormat formatoFechaEmision = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		Usuario proponente = proyecto.getUsuario();
		Organizacion organizacion = organizacionFacade.buscarPorRuc(proponente.getNombre());
		String nombreProponente = proponente.getPersona().getNombre();
		String tratamiento = (organizacion == null) ? (proponente.getPersona().getTipoTratos() == null ? " " : proponente.getPersona().getTipoTratos().getNombre()) : (organizacion.getPersona().getTipoTratos() == null ? " " : organizacion.getPersona().getTipoTratos().getNombre());

		EntityInformeOficioDiagnosticoAmbiental entity = new EntityInformeOficioDiagnosticoAmbiental();
		
		entity.setNumero(oficioPronunciamiento.getCodigo());		
		entity.setCiudad(JsfUtil.getLoggedUser().getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entity.setFechaEmision(formatoFechaEmision.format(new Date()));

		String cargo;
		if(organizacion == null) {
			if (proponente.getPersona().getPosicion() != null) {
				cargo = proponente.getPersona().getPosicion();
				entity.setDisplayCargo("inline");
			} else {
				cargo = "";
				entity.setDisplayCargo("none");
			}
			entity.setDisplayEmpresa("none");
			entity.setNombreOperador(nombreProponente);
			entity.setCargoOperador(cargo);
			entity.setNombreEmpresa(" ");
			entity.setTratamientoOperador(tratamiento);
		} else {
			if (organizacion.getCargoRepresentante() != null) {
				cargo = organizacion.getCargoRepresentante();
				entity.setDisplayCargo("inline");
			} else {
				cargo = "";
				entity.setDisplayCargo("none");
			}
			entity.setNombreOperador(nombreProponente);
			entity.setCargoOperador(cargo);
			entity.setNombreEmpresa(organizacion.getNombre());
			entity.setDisplayEmpresa("inline");
			entity.setTratamientoOperador(tratamiento);
		}

		entity.setAsunto(oficioPronunciamiento.getAsunto());
		entity.setPronunciamiento(oficioPronunciamiento.getPronunciamiento());

		entity.setNombreResponsable(usuarioAutoridad.getPersona().getNombre());
		
		Area areaAutoridad = new Area();
		if(usuarioAutoridad.getListaAreaUsuario() != null && usuarioAutoridad.getListaAreaUsuario().size() == 1){
			areaAutoridad = usuarioAutoridad.getListaAreaUsuario().get(0).getArea();
		}else if(usuarioAutoridad.getListaAreaUsuario() != null){
			areaAutoridad = areaTramite;
		}
		
//		entity.setAreaResponsable(usuarioAutoridad.getArea().getAreaName());
		entity.setAreaResponsable(areaAutoridad.getAreaName());
		
		if(proyecto.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
			entity.setAreaResponsable(Constantes.CARGO_AUTORIDAD_ZONAL);
		
		
		if(areaAutoridad.getTipoArea().getId().equals(3))
			entity.setSiglasEnte(areaAutoridad.getAreaAbbreviation());
		
		return entity;
	}
	
	public void guardarInforme() {
		
		informeTecnico.setFechaElaboracion(new Date());
		informeFacade.guardar(informeTecnico);
		informeTecnico.setFechaModificacion(new Date());

		generarInforme(true);
		
		datosGuardados = false;
	}

	public void guardarOficio() {
		oficioPronunciamiento.setTipoPronunciamiento(informeTecnico.getTipoPronunciamiento());
		oficioPronunciamiento.setFechaElaboracion(new Date());
		oficioPronunciamientoFacade.guardar(oficioPronunciamiento);
		oficioPronunciamiento.setFechaModificacion(new Date());
		
		generarOficio(true);
		
		datosGuardados = true;
	}
	
	public DocumentosCOA guardarDocumentoInformeAlfresco() {
		try {
			generarInforme(false);
			
			documentoInforme = new DocumentosCOA();
			documentoInforme.setContenidoDocumento(informeTecnico.getArchivoReporte());
			documentoInforme.setNombreDocumento(informeTecnico.getNombreReporte());
			documentoInforme.setExtencionDocumento(".pdf");		
			documentoInforme.setTipo("application/pdf");
			documentoInforme.setNombreTabla("InformeDiagnosticoAmbiental");
			documentoInforme.setIdTabla(informeTecnico.getId());
			documentoInforme.setProyectoLicenciaCoa(proyecto);

			documentoInforme = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 1L, documentoInforme, TipoDocumentoSistema.RCOA_INFORME_DIAGNOSTICO_AMBIENTAL);

			return documentoInforme;

		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			Log.debug(e.toString());
		}

		return null;
	}
	
	public DocumentosCOA guardarDocumentoOficioAlfresco() {
		try {
			generarInforme(false);
			
			documentoOficioPronunciamiento = new DocumentosCOA();
			documentoOficioPronunciamiento.setContenidoDocumento(oficioPronunciamiento.getArchivoReporte());
			documentoOficioPronunciamiento.setNombreDocumento(oficioPronunciamiento.getNombreReporte());
			documentoOficioPronunciamiento.setExtencionDocumento(".pdf");		
			documentoOficioPronunciamiento.setTipo("application/pdf");
			documentoOficioPronunciamiento.setNombreTabla("OficioDiagnosticoAmbiental");
			documentoOficioPronunciamiento.setIdTabla(oficioPronunciamiento.getId());
			documentoOficioPronunciamiento.setProyectoLicenciaCoa(proyecto);

			documentoOficioPronunciamiento = documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigoUnicoAmbiental(), "DIAGNOSTICO_AMBIENTAL", 1L, documentoOficioPronunciamiento, TipoDocumentoSistema.RCOA_OFICIO_DIAGNOSTICO_AMBIENTAL);

			return documentoOficioPronunciamiento;

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

		return null;
	}

	public void recuperarInformeTecnico() {
		try{
			informeTecnico = informeFacade.getPorProyecto(proyecto.getId());
			List<DocumentosCOA> documentos = documentosFacade.documentoXTablaIdXIdDoc(informeTecnico.getId(), TipoDocumentoSistema.RCOA_INFORME_DIAGNOSTICO_AMBIENTAL, "InformeDiagnosticoAmbiental");
			if(documentos.size() > 0) {
				documentoInforme = documentos.get(0);
				
				File fileDoc = documentosFacade.descargarFile(documentoInforme);
				
				Path path = Paths.get(fileDoc.getAbsolutePath());
				byte[] contenido = Files.readAllBytes(path);
				String reporteHtmlfinal = documentoInforme.getNombreDocumento().replace("/", "-");
				File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
				FileOutputStream file = new FileOutputStream(archivoFinal);
				file.write(contenido);
				file.close();
				
				nombreReporte = documentoInforme.getNombreDocumento();
				archivoReporte = contenido;
				
				informeTecnico.setPathReporte(JsfUtil.devolverContexto("/reportesHtml/" + documentoInforme.getNombreDocumento()));
				informeTecnico.setArchivoReporte(contenido);
				informeTecnico.setNombreReporte(nombreReporte);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
}
