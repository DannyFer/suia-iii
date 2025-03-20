package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.dto.EntityDocumentoResponsabilidad;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.DocumentosImpactoEstudioFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade.InformacionProyectoEIACoaFacade;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.DocumentoEstudioImpacto;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoDocumento;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;
import ec.gob.ambiente.suia.webservice.facade.AsignarTareaFacade;

@ManagedBean
@ViewScoped
public class FirmarResponsabilidadEsIAController implements Serializable{

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
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;	
	
	@EJB
	private DocumentosImpactoEstudioFacade documentosFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InformacionProyectoEIACoaFacade informacionProyectoEIACoaFacade;
	
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	
	@EJB
	private AsignarTareaFacade asignarTareaFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;

	@Getter
	@Setter
	private DocumentoEstudioImpacto documentoResponsabilidad,documentoFirmado;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private InformacionProyectoEia esiaProyecto;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocumento;
	
	@Getter
	@Setter
	private Integer idProyecto, numeroRevision;

	@Getter
	@Setter
	private Boolean documentoDescargado = false;
	
	@Getter
	@Setter
	private boolean token, documentoSubido, firmaSoloToken;
	
	private Map<String, Object> variables;
	
	@Getter
	@Setter
	private String tramite ="", docuTableClass, usuarioFirma;
	
	@PostConstruct
	public void iniciar() throws JbpmException, NumberFormatException, CmisAlfrescoException
	{
		firmaSoloToken = Constantes.getPropertyAsBoolean("ambiente.produccion");
		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),bandejaTareasBean.getProcessId());
		String idProyectoString = (String) variables.get("idProyecto");
		idProyecto = Integer.valueOf(idProyectoString);

		numeroRevision = variables.containsKey("numeroRevision") ? (Integer.valueOf((String) variables.get("numeroRevision"))) : 0;
		
		verificaToken();
		
		proyecto = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		
		esiaProyecto = informacionProyectoEIACoaFacade.obtenerInformacionProyectoEIAPorProyecto(proyecto);
		
		documentoResponsabilidad = documentosFacade
				.documentoXTablaIdXIdDoc(
						esiaProyecto.getId(),
						InformacionProyectoEia.class.getSimpleName(),
						TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);
		
		if(documentoResponsabilidad == null) {
			generarResponsabilidad();
		} else {
			if(!JsfUtil.getSimpleDateFormat(documentoResponsabilidad.getFechaCreacion()).equals(JsfUtil.getSimpleDateFormat(new Date()))) {
				documentoResponsabilidad.setEstado(false);
				documentosFacade.guardar(documentoResponsabilidad);
				documentoResponsabilidad = null;
				
				generarResponsabilidad();
			}
		}
		
		try {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(proyecto.getUsuario().getNombre());

			if (organizacion != null) {

				// Cuando el representante legal es otra organizacion
				Organizacion organizacionRep = organizacion;
				while (organizacionRep.getPersona().getPin().length() == 13) {
					Organizacion orgaAux = organizacionFacade.buscarPorRuc(organizacionRep.getPersona().getPin());
					if (orgaAux != null) {
						organizacionRep = orgaAux;
					} else {
						break;
					}
				}
				usuarioFirma = organizacionRep.getPersona().getPin();
			} else {
				usuarioFirma = JsfUtil.getLoggedUser().getNombre();
			}

			if (usuarioFirma.length() == 13 && usuarioFirma.endsWith("001")) {
				usuarioFirma = usuarioFirma.substring(0, 10);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public boolean verificaToken() {
		if(firmaSoloToken) {
			token = true;
			return token;
		}
		
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null
				&& JsfUtil.getLoggedUser().getToken())
			token = true;
		return token;
	}

	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void generarResponsabilidad() {
		try {

			PlantillaReporte plantillaReporte = plantillaReporteFacade
					.getPlantillaReporte(TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);

			EntityDocumentoResponsabilidad entity = new EntityDocumentoResponsabilidad();

			List<String> datosOperador = usuarioFacade.recuperarNombreOperador(proyecto.getUsuario());
			String nombreOperador = datosOperador.get(0);
			String nombreRepresentante = datosOperador.get(1);

			String actividadPrincipal = "";
			String codigoCiiu = "";

			ProyectoLicenciaCuaCiuu actividad = proyectoLicenciaCuaCiuuFacade
					.actividadPrincipal(proyecto);
			if (actividad != null && actividad.getId() != null) {
				actividadPrincipal = actividad.getCatalogoCIUU().getNombre();
				codigoCiiu = actividad.getCatalogoCIUU().getCodigo();
			}

			SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-YYYY");
			
			entity.setOperador(nombreOperador);
			entity.setFecha(formatoFecha.format(proyecto.getFechaCreacion()));
			entity.setNombreActividad(actividadPrincipal);
			entity.setCodigoCiiu(codigoCiiu);
			entity.setCodigo(proyecto.getCodigoUnicoAmbiental());
			entity.setNombreProyecto(proyecto.getNombreProyecto());
			entity.setArea(proyecto.getAreaResponsable());
			entity.setNombreRepresentante(nombreRepresentante);
			
			String displayRep = (nombreRepresentante.equals("")) ? "none" : "inline";
			String displayOperador = (nombreRepresentante.equals("")) ? "inline" : "none";
			
			entity.setDisplayRepresentante(displayRep);
			entity.setDisplayOperador(displayOperador);

			File informePdf = UtilGenerarPdf.generarFichero(
					plantillaReporte.getHtmlPlantilla(),
					"Documento de Responsabilidad", true, entity);

			String nombreReporte = "Documento de Responsabilidad.pdf";

			Path path = Paths.get(informePdf.getAbsolutePath());
			String reporteHtmlfinal = nombreReporte;
			byte[] archivoInforme = Files.readAllBytes(path);
			File archivoFinal = new File(
					JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(Files.readAllBytes(path));
			file.close();

			TipoDocumento tipoDoc = new TipoDocumento();
			tipoDoc.setId(TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA
					.getIdTipoDocumento());

			DocumentoEstudioImpacto documento = new DocumentoEstudioImpacto();
			documento.setNombre(nombreReporte);
			documento.setExtesion(".pdf");
			documento.setMime("application/pdf");
			documento.setContenidoDocumento(archivoInforme);
			documento.setNombreTabla(InformacionProyectoEia.class
					.getSimpleName());
			documento.setIdTable(esiaProyecto.getId());
			documento.setTipoDocumento(tipoDoc);
			documento.setDescripcion("Documento responsabilidad-" + bandejaTareasBean.getTarea().getTaskId());

			documentoResponsabilidad = documentosFacade
					.guardarDocumentoAlfrescoCA(
							proyecto.getCodigoUnicoAmbiental(),
							Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,
							documento,
							TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String firmarDocumento() {
		try {
			String documentOffice = documentosFacade.direccionDescarga(documentoResponsabilidad);
			return DigitalSign.sign(documentOffice, usuarioFirma);  
		} catch (Throwable e) {
			JsfUtil.addMessageError("Error al realizar la operación.");
			return "";
		}
	}
	
	public StreamedContent descargar() {
		DefaultStreamedContent content = null;
		try {

			byte[] documentoContent = null;
			
			if(documentoResponsabilidad != null  && documentoResponsabilidad.getContenidoDocumento() != null)
				documentoContent = documentoResponsabilidad.getContenidoDocumento();
			else if (documentoResponsabilidad != null && documentoResponsabilidad.getAlfrescoId() != null)
				documentoContent = documentosFacade.descargar(documentoResponsabilidad.getAlfrescoId());

			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documentoContent));
				content.setName(documentoResponsabilidad.getNombre());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");

			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado){
			byte[] contenidoDocumento = event.getFile().getContents();
			documentoFirmado = new DocumentoEstudioImpacto();
			documentoFirmado.setIdTable(esiaProyecto.getId());
			documentoFirmado.setNombreTabla(InformacionProyectoEia.class.getSimpleName());
			documentoFirmado.setDescripcion("Documento responsabilidad-" + bandejaTareasBean.getTarea().getTaskId());
			documentoFirmado.setExtesion(".pdf");
			documentoFirmado.setMime("application/pdf");
			documentoFirmado.setContenidoDocumento(contenidoDocumento);
			documentoFirmado.setNombre(event.getFile().getFileName());	
			documentoFirmado.setIdProceso(bandejaTareasBean.getProcessId());
			documentoSubido = true;
		}else{
			JsfUtil.addMessageError("No ha descargado el documento");
		}
	}
	
	public void completarTarea() {
		try {
			
			if (token) {
				String idAlfrescoInforme = documentoResponsabilidad.getAlfrescoId();

				if (!documentosFacade.verificarFirmaVersion(idAlfrescoInforme)) {
					JsfUtil.addMessageError("El documento no está firmado electrónicamente.");
					return;
				}
			} else {
				if(documentoSubido) {
					documentoResponsabilidad = documentosFacade
							.guardarDocumentoAlfrescoCA(
									proyecto.getCodigoUnicoAmbiental(),
									Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL,
									documentoFirmado,
									TipoDocumentoSistema.EIA_RESPONSABLIDAD_INFORMACION_CONTENIDA);
				} else {
					JsfUtil.addMessageError("Debe adjuntar el documento firmado.");
					return;
				}				
			}

			try {
				Map<String, Object> parametros = new HashMap<>();
				String usrTecnico = (String) variables.get("tecnicoResponsable");
				parametros.putAll(recuperarParametros(proyecto, usrTecnico));

				if(numeroRevision == 0) {
					parametros.put("numeroRevision", 1); 
					parametros.put("esPrimeraRevision", true);
				}
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(),bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getTarea().getProcessInstanceId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo(JsfUtil.NAVIGATION_TO_BANDEJA);
			} catch (JbpmException e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Error al realizar la operación.");
			}


		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
	
	public Map<String, Object> recuperarParametros(ProyectoLicenciaCoa proyecto, String usrTecnico) {
		try {
			
			Map<String, Object> parametros = new HashMap<>();

			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol = "role.esia.cz.tecnico.responsable";

			if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) {
				ProyectoLicenciaCuaCiuu proyectoCiuuPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
				CatalogoCIUU actividadPrincipal = proyectoCiuuPrincipal.getCatalogoCIUU();

				Integer idSector = actividadPrincipal.getTipoSector().getId();

				tipoRol = "role.esia.pc.tecnico.responsable.tipoSector." + idSector;
			} else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)) {
				tipoRol = "role.esia.gad.tecnico.responsable";
			}

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			// buscar usuarios por rol y area
			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError("Ocurrió un error. Comuníquese con Mesa de Ayuda.");
				System.out.println("No se encontró técnico responsable en " + areaResponsable.getAreaName());
				return null;
			}

			// recuperar tecnico de bpm y validar si el usuario existe en el
			// listado anterior
			Usuario tecnicoResponsable = null;
			Usuario usuarioTecnico = usuarioFacade.buscarUsuario(usrTecnico);
			if (usuarioTecnico != null
					&& usuarioTecnico.getEstado().equals(true)) {
				if (listaUsuario != null && listaUsuario.size() >= 0
						&& listaUsuario.contains(usuarioTecnico)) {
					tecnicoResponsable = usuarioTecnico;
				}
			}

			// si no se encontró el usuario se realiza la busqueda de uno nuevo
			// y se actualiza en el bpm
			if (tecnicoResponsable == null) {
				
				String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'', ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
				
				List<Usuario> listaTecnicosResponsables = asignarTareaFacade.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
				tecnicoResponsable = listaTecnicosResponsables.get(0);

				parametros.put("tecnicoResponsable", tecnicoResponsable.getNombre());
			}
			
			// si el proyecto interseca con SNAP o Forestal buscar los usuarios
			// a los que se debe asignar para revision
			Map<String, Object> infoInterseccion = recuperarInfoInterseccion(proyecto);
			if (infoInterseccion == null)
				return null;

			parametros.putAll(infoInterseccion);
			
			
			boolean esPlantaCentral = false;
			boolean esGad = false;
			if (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) 
				esPlantaCentral = true;
			else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)
					&& !areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) 
				esGad = true;
			
			parametros.put("esPlantaCentral", esPlantaCentral);
			parametros.put("esGad", esGad);
			
			return parametros;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Map<String, Object> recuperarInfoInterseccion(ProyectoLicenciaCoa proyecto) {
		Map<String, Object> parametros = new HashMap<String, Object>();

		Boolean intersecaSnap = proyecto.getInterecaSnap();
		Boolean intersecaForestal = false;
		if (proyecto.getInterecaBosqueProtector())
			intersecaForestal = true;
		else if (proyecto.getInterecaPatrimonioForestal())
			intersecaForestal = true;

		Boolean interseca = false;
		if (intersecaForestal || intersecaSnap || proyecto.getRenocionCobertura())
			interseca = true;

		parametros.put("requierePronunciamientoPatrimonio", interseca);
		
		parametros.put("requierePronunciamientoSnap", intersecaSnap);
		parametros.put("requierePronunciamientoForestal", intersecaForestal);
		parametros.put("requierePronunciamientoInventario", proyecto.getRenocionCobertura());

		if (intersecaForestal || proyecto.getRenocionCobertura()) {
			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol = (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) ? "role.esia.pc.tecnico.bosques" : "role.esia.cz.tecnico.bosques";
			
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_BOSQUES);
			else if (!areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT)
					&& !areaResponsable.getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
				UbicacionesGeografica ubicacionPrincipal = proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyecto).getUbicacionesGeografica();
				areaResponsable = areaFacade.getAreaCoordinacionZonal(ubicacionPrincipal.getUbicacionesGeografica());
			}

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaUsuario = usuarioFacade
					.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				System.out.println("No se encontró usuario con el rol " + rolTecnico + " en " + areaResponsable.getAreaName());
				return null;
			}

			Usuario tecnicoBosques = null;
			
			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
			tecnicoBosques = listaTecnicosResponsables.get(0);

			parametros.put("tecnicoBosques", tecnicoBosques.getNombre());
		}

		if (intersecaSnap) {
			Area areaResponsable = proyecto.getAreaResponsable();
			String tipoRol =  (areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC)) ? "role.esia.pc.tecnico.conservacion" : "role.esia.cz.tecnico.conservacion";
			
			if(areaResponsable.getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_PC))
				areaResponsable = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);

			String rolTecnico = Constantes.getRoleAreaName(tipoRol);

			List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolTecnico, areaResponsable);
			if (listaUsuario == null || listaUsuario.size() == 0) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				System.out.println("No se encontró usuario con el rol " + rolTecnico + " en " + areaResponsable.getAreaName());
				return null;
			}

			Usuario tecnicoConservacion = null;
			String proceso = Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL + "'' , ''" + Constantes.RCOA_ESTUDIO_IMPACTO_AMBIENTAL_v2;
			List<Usuario> listaTecnicosResponsables = asignarTareaFacade
					.getCargaLaboralPorUsuariosProceso(listaUsuario,proceso);
			tecnicoConservacion = listaTecnicosResponsables.get(0);

			parametros.put("tecnicoConservacion", tecnicoConservacion.getNombre());
		}

		return parametros;
	}
	
}