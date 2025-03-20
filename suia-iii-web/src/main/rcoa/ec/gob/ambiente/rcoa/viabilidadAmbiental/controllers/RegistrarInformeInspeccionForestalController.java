package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.CoordenadasForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DelegadosInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.DocumentosViabilidadFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.FotografiasForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformacionViabilidadLegalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InformeInspeccionForestalFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.InterseccionViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.CoordenadaForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoOperadorForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DelegadoTecnicoForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.DocumentoViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.FotografiaInformeForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformacionViabilidadLegal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestal;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.InformeInspeccionForestalEntity;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.model.ViabilidadCoa;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class RegistrarInformeInspeccionForestalController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Logger LOG = Logger.getLogger(RegistrarInformeInspeccionForestalController.class);
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(
			Constantes.getUrlWsRegistroCivilSri());
	
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	
	@EJB
	private DocumentosViabilidadFacade documentosFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private InformeInspeccionForestalFacade informeInspeccionFacade;
	
	@EJB
    private UsuarioFacade usuarioFacade;
	
	@EJB
	private DelegadosInspeccionForestalFacade delegadosInspeccionForestalFacade;
	
	@EJB
	private CoordenadasForestalFacade coordenadasForestalFacade;
	
	@EJB
	private FotografiasForestalFacade fotografiasForestalFacade;
	
	@EJB
    private ProcesoFacade procesoFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private InterseccionViabilidadCoaFacade interseccionViabilidadCoaFacade;
	
	@EJB
	private	InformacionViabilidadLegalFacade revisionTecnicoJuridicoFacade;
	
	@Getter
	@Setter
	private ViabilidadCoa viabilidadProyecto;
	
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	private DocumentoViabilidad documentoInformeAlfresco, informeFirmaManual;
	
	@Getter
	@Setter
	private InformeInspeccionForestal informeInspeccion;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	
	@Getter
	@Setter
	private List<DelegadoOperadorForestal> listaDelegadosOperador, listaDelegadosOperadorEliminar;
	
	@Getter
	@Setter
	private List<DelegadoTecnicoForestal> listaTecnicosDelegados, listaTecnicosDelegadosEliminar;
	
	@Getter
	@Setter
	private List<FotografiaInformeForestal> listaImgsTipoCobertura, listaImgsTipoEcosistema, listaImgsAreaImplantacion, listaImgsEliminar;
	
	@Getter
	@Setter
	private List<CoordenadaForestal> listaCoordenadas, listaCoordenadasEliminar;
	
	@Getter
	@Setter
	private List<File> filesFotos;
	
	@Getter
	@Setter
	private DelegadoOperadorForestal delegadoOperador;
	
	@Getter
	@Setter
	private DelegadoTecnicoForestal tecnicoDelegado;
	
	@Getter
	@Setter
	private FotografiaInformeForestal nuevaFotografia;
	
	@Getter
	@Setter
	private CoordenadaForestal nuevaCoordenada;
	
	@Getter
	@Setter
	private InformacionViabilidadLegal informacionViabilidadLegal;
	
	@Getter
	@Setter
	private Boolean informeGuardado, informeFirmado, mostrarFirma, token, documentoDescargado, subido, cedulaDelegadoValida, soloToken;

	@Getter
	@Setter
	private String tipoPronunciamiento, nombreDocumentoFirmado;

	@Getter
	@Setter
	private String urlInforme, nombreInforme, razonSocial, interseccionesProyecto;

	@Getter
	@Setter
	private byte[] archivoInforme;
	
	@Getter
	@Setter
	Integer idProyecto, idViabilidad, tipoFotografia;
	
	private Map<String, Object> variables;

	@PostConstruct
	private void iniciar() {
		try {
		
		variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

		String idProyectoString=(String)variables.get("idProyecto");
		
		idProyecto = Integer.valueOf(idProyectoString); 
		
		soloToken = Constantes.getPropertyAsBoolean("ambiente.produccion") ;
		
		informeGuardado = false;
		informeFirmado = false;
		mostrarFirma = false;
		subido = false;
		documentoDescargado = false;
		verificaToken();
		
		listaDelegadosOperador = new ArrayList<>();
		listaTecnicosDelegados = new ArrayList<>();
		listaDelegadosOperadorEliminar = new ArrayList<>();
		listaTecnicosDelegadosEliminar = new ArrayList<>();
		listaImgsTipoCobertura = new ArrayList<>();
		listaImgsTipoEcosistema = new ArrayList<>();
		listaImgsAreaImplantacion = new ArrayList<>();
		listaImgsEliminar = new ArrayList<>();
		listaCoordenadas = new ArrayList<>();
		listaCoordenadasEliminar = new ArrayList<>();
		filesFotos = new ArrayList<>();
		
		plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION);
		
		proyectoLicenciaCoa = proyectoLicenciaCoaFacade.buscarProyectoPorId(idProyecto);
		viabilidadProyecto = viabilidadCoaFacade.getViabilidadForestalPorProyecto(idProyecto);
		idViabilidad = viabilidadProyecto.getId();
		
		informacionViabilidadLegal= revisionTecnicoJuridicoFacade.getInformacionViabilidadLegalPorId(viabilidadProyecto.getId());
		
		interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(idProyecto, 2);
		
		informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
		if(informeInspeccion == null) {
			informeInspeccion = new InformeInspeccionForestal();
		} else {
			cargarDatos();
		}
		
		generarInforme(true);
		
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos RegistroSnap.");
		}
	}
	
	public void validarTareaBpm() {
		JsfUtil.validarPaginasUrlTareasBpm(JsfUtil.getCurrentTask(),
				"/pages/rcoa/viabilidadAmbiental/forestal/ingresarInformeInspeccionForestal.jsf");
	}
	
	public void guardarToken() {
		Usuario usuario = JsfUtil.getLoggedUser();
		usuario.setToken(token);
		try {
			usuarioFacade.guardar(usuario);
			verificaToken();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean verificaToken() {
		token = false;
		if (JsfUtil.getLoggedUser().getToken() != null && JsfUtil.getLoggedUser().getToken())
			token = true;
		
		if(soloToken) {
			token = true;
		}
		
		return token;
	}
	
	public void cargarDatos() {
		listaDelegadosOperador = (List<DelegadoOperadorForestal>) delegadosInspeccionForestalFacade.getDelegadosOperador(informeInspeccion.getId());
		if(listaDelegadosOperador == null)
			listaDelegadosOperador = new ArrayList<>();
		
		listaTecnicosDelegados = (List<DelegadoTecnicoForestal>) delegadosInspeccionForestalFacade.getDelegadosTecnicos(informeInspeccion.getId());
		if(listaTecnicosDelegados == null)
			listaTecnicosDelegados = new ArrayList<>();
		
		listaCoordenadas = coordenadasForestalFacade.getListaCoordenadasPorInforme(informeInspeccion.getId());
		if(listaCoordenadas == null)
			listaCoordenadas = new ArrayList<>();
		
		listaImgsTipoCobertura = fotografiasForestalFacade.getListaFotografiasPorInformeTipo(informeInspeccion.getId(), 1);
		listaImgsTipoEcosistema = fotografiasForestalFacade.getListaFotografiasPorInformeTipo(informeInspeccion.getId(), 2);
		listaImgsAreaImplantacion = fotografiasForestalFacade.getListaFotografiasPorInformeTipo(informeInspeccion.getId(), 3);
		
		if(listaImgsTipoCobertura != null) {
			for (FotografiaInformeForestal  foto : listaImgsTipoCobertura) {
				String url = DatatypeConverter.printBase64Binary(foto.getDocImagen().getContenidoDocumento());
				foto.setUrl(url);
			}
		} else 
			listaImgsTipoCobertura = new ArrayList<>();
		
		if(listaImgsTipoEcosistema != null) {
			for (FotografiaInformeForestal  foto : listaImgsTipoEcosistema) {
				String url = DatatypeConverter.printBase64Binary(foto.getDocImagen().getContenidoDocumento());
				foto.setUrl(url);
			}
		} else
			listaImgsTipoEcosistema = new ArrayList<>();
		
		if(listaImgsAreaImplantacion != null) {
			for (FotografiaInformeForestal  foto : listaImgsAreaImplantacion) {
				String url = DatatypeConverter.printBase64Binary(foto.getDocImagen().getContenidoDocumento());
				foto.setUrl(url);
			}
		} else 
			listaImgsAreaImplantacion = new ArrayList<>();
	}

	public StreamedContent getStream() throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (archivoInforme != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(
					archivoInforme), "application/octet-stream");
			content.setName(informeInspeccion.getNombreReporte());
		}
		return content;
	}
	
	public void nuevoDelegado() {
		delegadoOperador = new DelegadoOperadorForestal();
	}
	
	public void eliminarDelegado(DelegadoOperadorForestal delegado) {
		try {
			if (delegado.getId() != null) {
				delegado.setEstado(false);
				listaDelegadosOperadorEliminar.add(delegado);
			}
			listaDelegadosOperador.remove(delegado);
		} catch (Exception e) {

		}
	}
	
	public void validarCedulaListener() {
		String cedulaRuc = delegadoOperador.getCedula();
		cedulaDelegadoValida = false;
		delegadoOperador.setNombre(null);

		if (JsfUtil.validarCedulaORUC(cedulaRuc)) {
			try {
				if (cedulaRuc.length() == 10) {
					Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(
							Constantes.USUARIO_WS_MAE_SRI_RC,
							Constantes.PASSWORD_WS_MAE_SRI_RC, cedulaRuc);
					cedulaDelegadoValida = true;
					delegadoOperador.setNombre(cedula.getNombre());
				} else if (cedulaRuc.length() == 13) {
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
							.obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC,
									Constantes.PASSWORD_WS_MAE_SRI_RC,
									cedulaRuc);
					if (contribuyenteCompleto != null) {
						cedulaDelegadoValida = true;
						delegadoOperador.setNombre(contribuyenteCompleto
								.getRazonSocial());
					} else {
						JsfUtil.addMessageError("Error al validar Ruc, Servicio Web No Disponible");
						return;
					}
				}
			} catch (Exception e) {
				JsfUtil.addMessageError("Error al validar Cédula o Ruc");
				return;
			}
		}

		if (!cedulaDelegadoValida) {
			JsfUtil.addMessageError("Error en Cédula o Ruc no válido");
		}
	}
	
	public void agregarDelegadoOperador() {
		if(!cedulaDelegadoValida){
			JsfUtil.addMessageError("Debe ingresar una cedula válida. ");
			return;
		}
		
		if (!listaDelegadosOperador.contains(delegadoOperador))
			listaDelegadosOperador.add(delegadoOperador);

		JsfUtil.addCallbackParam("addDelegadoOperador");
	}
	
	public void nuevoTecnicoDelegado() {
		tecnicoDelegado = new DelegadoTecnicoForestal();
	}
	
	public void eliminarTecnico(DelegadoTecnicoForestal delegado) {
		try {
			if (delegado.getId() != null) {
				delegado.setEstado(false);
				listaTecnicosDelegadosEliminar.add(delegado);
			}
			listaTecnicosDelegados.remove(delegado);
		} catch (Exception e) {

		}
	}
	
	public void buscarUsuario() {
		Usuario usuario =usuarioFacade.buscarUsuario(tecnicoDelegado.getCedula());
		if(usuario!=null)
		{
			tecnicoDelegado.setUsuario(usuario);
			return;
		}
		
		JsfUtil.addMessageError("Usuario no encontrado. ");
	}
	
	public void agregarTecnicoDelegado() {
		
		if(tecnicoDelegado.getUsuario() == null){
			JsfUtil.addMessageError("Debe ingresar un usuario válido. ");
			return;
		}
		
		if (!listaTecnicosDelegados.contains(tecnicoDelegado))
			listaTecnicosDelegados.add(tecnicoDelegado);

		JsfUtil.addCallbackParam("addTecnioDelegado");
	}
	
	//FOTOS
	
	public void nuevaFotografia(Integer tipoFoto) {
		tipoFotografia = tipoFoto;
		
		nuevaFotografia = new FotografiaInformeForestal();
		nuevaFotografia.setTipoFoto(tipoFoto);
	}
	
	public void uploadFileFoto(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		DocumentoViabilidad documentoImagen = new DocumentoViabilidad(); 
		documentoImagen.setId(null);
		documentoImagen.setContenidoDocumento(contenidoDocumento);
		documentoImagen.setNombre(event.getFile().getFileName());
		documentoImagen.setMime("image/jpg");
		documentoImagen.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FOTOGRAFIA_INFORME_FORESTAL.getIdTipoDocumento());
		
		nuevaFotografia.setDocImagen(documentoImagen);
		nuevaFotografia.setNombre(event.getFile().getFileName());
		
		String url = DatatypeConverter.printBase64Binary(contenidoDocumento);
		nuevaFotografia.setUrl(url);
	}
	
	public void agregarFotografia() {
		if(tipoFotografia.equals(1))
			listaImgsTipoCobertura.add(nuevaFotografia);
		else if(tipoFotografia.equals(2))
			listaImgsTipoEcosistema.add(nuevaFotografia);
		else
			listaImgsAreaImplantacion.add(nuevaFotografia);
		
		JsfUtil.addCallbackParam("addFotografia");
	}
	
	public void validateDatosFoto(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (nuevaFotografia == null || (nuevaFotografia.getUrl() == null))
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Fotografía' es requerido.", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void cancelarFotografia() {
		tipoFotografia = null;
		
		nuevaFotografia = new FotografiaInformeForestal();
	}
	
	public void eliminarFotografia(Integer tipoFoto, FotografiaInformeForestal foto) {
		try {
			if (foto.getId() != null) {
				foto.setEstado(false);
				listaImgsEliminar.add(foto);
			}
			
			if(tipoFoto.equals(1))
				listaImgsTipoCobertura.remove(foto);
			else if(tipoFoto.equals(2))
				listaImgsTipoEcosistema.remove(foto);
			else
				listaImgsAreaImplantacion.remove(foto);
		} catch (Exception e) {

		}
	}
	
	public StreamedContent descargarFotografia(FotografiaInformeForestal fotografia) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
				documentoContent = fotografia.getDocImagen().getContenidoDocumento();
			
			if (fotografia != null && documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(fotografia.getDocImagen().getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	//fin FOTOS
	
	//COORDENADA
	public void nuevaCoordenada() {
		nuevaCoordenada = new CoordenadaForestal();
	}
	
	public void agregarCoordenada() {
		listaCoordenadas.add(nuevaCoordenada);
		
		JsfUtil.addCallbackParam("addCoordenada");
	}
	
	public void eliminarCoordenada(CoordenadaForestal coordenada) {
		try {
			if (coordenada.getId() != null) {
				coordenada.setEstado(false);
				listaCoordenadasEliminar.add(coordenada);
			}
			listaCoordenadas.remove(coordenada);
		} catch (Exception e) {

		}
	}
	
	public void validateCooordedana(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		// validar coordenada
		if (nuevaCoordenada.getCoordenadaX() != null && nuevaCoordenada.getCoordenadaY() != null) {
			String coordenadaX = nuevaCoordenada.getCoordenadaX().toString().replace(",", ".");
			String coordenadaY = nuevaCoordenada.getCoordenadaY().toString().replace(",", ".");

			String mensaje = JsfUtil.validarCoordenadaPunto17S(coordenadaX,
					coordenadaY, proyectoLicenciaCoa.getCodigoUnicoAmbiental());

			if (mensaje != null)
				errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,mensaje, null));
		}

		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	//fin COORDENADA
	
	public void generarInforme(Boolean marcaAgua) {
		try {

			informeInspeccion = informeInspeccionFacade.getInformePorViabilidad(viabilidadProyecto.getId());
			
			if(informeInspeccion == null) {
				informeInspeccion = new InformeInspeccionForestal();
				informeInspeccion.setNombreFichero("InformeInspeccion.pdf");
				informeInspeccion.setFechaElaboracion(new Date());
			} else {
				informeInspeccion.setNombreFichero("InformeInspeccion_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			informeInspeccion.setNombreReporte("InformeInspeccion.pdf");
			
			InformeInspeccionForestalEntity informeEntity = cargarDatosDocumento();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteInforme.getHtmlPlantilla(),
					informeInspeccion.getNombreReporte(), true, informeEntity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeInspeccion.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeInspeccion.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeInspeccion.getArchivoInforme());
			file.close();
			informeInspeccion.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeInspeccion.getNombreFichero()));
			
			urlInforme = informeInspeccion.getInformePath();
			nombreInforme = informeInspeccion.getNombreReporte();
			archivoInforme = informeInspeccion.getArchivoInforme();
			
			if(filesFotos!= null && filesFotos.size() > 0) {
				for (File foto : filesFotos) {
					foto.delete();
				}
			}
			
			filesFotos = new ArrayList<>();

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeInspeccionForestalEntity cargarDatosDocumento() throws Exception {
		InformeInspeccionForestalEntity informeEntity = new InformeInspeccionForestalEntity();
		filesFotos = new ArrayList<>();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		
		razonSocial = usuarioFacade.recuperarNombreOperador(proyectoLicenciaCoa.getUsuario()).get(0);
		
		informeEntity.setNroInforme(informeInspeccion.getNumeroInforme());
		informeEntity.setFechaElaboracion(dateFormat.format(new Date()));
		if(informeInspeccion.getFechaInspeccion() == null)
			informeEntity.setFechaInspeccion("");
		else
			informeEntity.setFechaInspeccion(dateFormat.format(informeInspeccion.getFechaInspeccion()));
		informeEntity.setNombreProyecto(proyectoLicenciaCoa.getNombreProyecto());
		informeEntity.setRazonSocial(razonSocial);
		informeEntity.setUnidadResponsable(viabilidadProyecto.getAreaResponsable().getAreaAbbreviation());
		informeEntity.setNombreUnidadResponsable(viabilidadProyecto.getAreaResponsable().getAreaName());
		informeEntity.setAntecedentes(informeInspeccion.getAntecedentes());
		informeEntity.setMarcoLegal(informeInspeccion.getMarcoLegal());
		informeEntity.setObjetivo(informeInspeccion.getObjetivo());
		informeEntity.setDetalleTipoCobertura(informeInspeccion.getTipoCoberturaVegetal());
		informeEntity.setDetalleTipoEcosistema(informeInspeccion.getTipoEcosistema());
		informeEntity.setDetalleAreaImplantacion(informeInspeccion.getAreaImplantacion());
		informeEntity.setConclusiones(informeInspeccion.getConclusiones());
		informeEntity.setRecomendaciones(informeInspeccion.getRecomendaciones());
		informeEntity.setNombreTecnico(JsfUtil.getLoggedUser().getPersona().getNombre());
		
		//interseca
		String interseccionesProyecto = interseccionViabilidadCoaFacade.getInterseccionesForestal(idProyecto, 2);
		informeEntity.setIntersecaProyecto(interseccionesProyecto);
		
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDelegadosOperador != null && !listaDelegadosOperador.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Cédula</th><th>Nombres y Apellidos</th><th>Cargo</th></tr>");
			for (DelegadoOperadorForestal delegado : listaDelegadosOperador) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCedula());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCargo());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		informeEntity.setDelegadosOperador(stringBuilder.toString());
		
		stringBuilder = new StringBuilder();
		if(listaTecnicosDelegados != null && !listaTecnicosDelegados.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Nombres y Apellidos</th><th>Cargo</th></tr>");
			for (DelegadoTecnicoForestal delegado : listaTecnicosDelegados) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getUsuario().getPersona().getNombre());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(delegado.getCargo());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		informeEntity.setEquipoTecnico(stringBuilder.toString());
		
		stringBuilder = new StringBuilder();
		if(listaCoordenadas != null && !listaCoordenadas.isEmpty()) {
			stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
			stringBuilder.append("<tr><th>Número</th><th>Coordenada X</th><th>Coordenada Y</th><th>Descripción</th></tr>");
			int nro=1;
			for (CoordenadaForestal coordenada : listaCoordenadas) {
				stringBuilder.append("<tr>");
				stringBuilder.append("<td>" + nro + "</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getCoordenadaX());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getCoordenadaY());
				stringBuilder.append("</td>");
				stringBuilder.append("<td>");
				stringBuilder.append(coordenada.getDescripcion());
				stringBuilder.append("</td>");
				stringBuilder.append("</tr>");
			}
			stringBuilder.append("</table>");
		}
		informeEntity.setTablaCoordenadas(stringBuilder.toString());
		
		if(!listaImgsTipoCobertura.isEmpty()) {
			String foto = getTablaFotos(listaImgsTipoCobertura);
			informeEntity.setFotografiasTipoCobertura(foto);
		}
		
		if(!listaImgsTipoEcosistema.isEmpty()) {
			String foto = getTablaFotos(listaImgsTipoEcosistema);
			informeEntity.setFotografiasTipoEcosistema(foto);
		}
		
		if(!listaImgsAreaImplantacion.isEmpty()) {
			String foto = getTablaFotos(listaImgsAreaImplantacion);
			informeEntity.setFotografiasAreaImplantacion(foto);
		}
		
		
		return informeEntity;
	}
	
	public String getTablaFotos(List<FotografiaInformeForestal> listaFotos) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		StringBuilder stringBuilder = new StringBuilder();
		String tamanioTabla = (listaFotos.size() >= 2) ? "width: 100%;" :  "width: 50%;";
		
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\""+tamanioTabla+" border-collapse:collapse;font-size:12px;\">");
		
		for (int i = 0; i < listaFotos.size(); i++) {
			stringBuilder.append("<tr>");
			for (int j = 0; j < 2; j++) {
				FotografiaInformeForestal foto = null;
				if(i < listaFotos.size())
					foto = listaFotos.get(i);
				
				if(foto != null) {
					Timestamp timestamp = new Timestamp(System.currentTimeMillis());
					
					if(foto.getDocImagen().getContenidoDocumento() == null) {
						foto.getDocImagen().setContenidoDocumento(documentosFacade.descargar(foto.getDocImagen().getIdAlfresco(), foto.getDocImagen().getFechaCreacion()));
					}
					
					ByteArrayInputStream inputStreamFirma = new ByteArrayInputStream(foto.getDocImagen().getContenidoDocumento());
					BufferedImage bImageFirma = ImageIO.read(inputStreamFirma);
					File imageFile = new File(timestamp.getTime() +"_" + foto.getDocImagen().getNombre());
					ImageIO.write(bImageFirma, "jpg", imageFile);
					
					stringBuilder.append("<td style=\"text-align: center; width: 50%\">");
					stringBuilder.append("<img src=\'" + imageFile.getPath() + "\' height=\'160\' width=\'160\' ></img>");
					stringBuilder.append("<br />" + foto.getDescripcion());
					stringBuilder.append("<br />" + dateFormat.format(foto.getFechaFotografia()));
					stringBuilder.append("</td>");
					
					filesFotos.add(imageFile);
				} else {
					if(listaFotos.size() >= 2)
						stringBuilder.append("<td></td>");
				}
				
				if(j==0)
					i++;
			}
			stringBuilder.append("</tr>");
		}
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}

	public void validateDatosIngreso(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		if (listaDelegadosOperador == null || listaDelegadosOperador.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Delegados del operador para inspección' es requerido.", null));
		if (listaTecnicosDelegados == null || listaTecnicosDelegados.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Equipo técnico delegado para la inspección' es requerido.", null));
		if (listaCoordenadas == null || listaCoordenadas.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"El campo 'Coordenadas de sitios de referencia' es requerido.", null));
		if (listaImgsTipoCobertura == null || listaImgsTipoCobertura.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe adjuntar las fotografías del campo 'Caracterización del tipo y estado de cobertura vegetal nativa existente en el área del proyecto, obra o actividad'.", null));
		if (listaImgsTipoEcosistema == null || listaImgsTipoEcosistema.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe adjuntar las fotografías del campo 'Caracterización del tipo de Ecosistema presente en el área del proyecto, obra o actividad'.", null));
		if (listaImgsAreaImplantacion == null || listaImgsAreaImplantacion.isEmpty())
			errorMessages.add(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							"Debe adjuntar las fotografías del campo 'Descripción del área de implantación del proyecto, obra o actividad'.", null));
		
		if(informeInspeccion.getConclusiones() == null || informeInspeccion.getConclusiones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Conclusiones' es requerido", null));
		
		if(informeInspeccion.getConclusiones()== null || informeInspeccion.getRecomendaciones().isEmpty())
			errorMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, "El campo 'Recomendaciones' es requerido", null));

		if(context.getMessageList().size() > 0 || errorMessages.size() > 0)
			informeGuardado = false;
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarInforme() {
		try {
			informeInspeccion.setIdViabilidad(viabilidadProyecto.getId());
	
			informeInspeccionFacade.guardar(informeInspeccion, viabilidadProyecto.getAreaResponsable());
			
			guardarDelegados();
			
			guardarFotografias();
			
			if(!listaCoordenadas.isEmpty()){
				for (CoordenadaForestal coordenada : listaCoordenadas) {
					coordenada.setIdInformeInspeccion(informeInspeccion.getId());
					
					coordenadasForestalFacade.guardarCoordenada(coordenada);
				}
			}
			
			if(!listaCoordenadasEliminar.isEmpty()){
				for (CoordenadaForestal coordenada : listaCoordenadasEliminar) {
					coordenada.setEstado(false);
					
					coordenadasForestalFacade.guardarCoordenada(coordenada);
				}
			}
			listaCoordenadasEliminar = new ArrayList<> ();
			
			generarInforme(true);
			
			informeGuardado = true;
			
			JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
		}
	}
	
	public void guardarDelegados() {
		if(!listaDelegadosOperador.isEmpty()){
			for (DelegadoOperadorForestal delegado : listaDelegadosOperador) {
				delegado.setIdInformeInspeccion(informeInspeccion.getId());
				
				delegadosInspeccionForestalFacade.guardarDelegado(delegado);
			}
		}
		
		if(!listaTecnicosDelegados.isEmpty()){
			for (DelegadoTecnicoForestal tecnico : listaTecnicosDelegados) {
				tecnico.setIdInformeInspeccion(informeInspeccion.getId());
				
				delegadosInspeccionForestalFacade.guardarTecnicoDelegado(tecnico);
			}
		}
		
		if(!listaDelegadosOperadorEliminar.isEmpty()){
			for (DelegadoOperadorForestal delegado : listaDelegadosOperadorEliminar) {
				delegado.setEstado(false);
				
				delegadosInspeccionForestalFacade.guardarDelegado(delegado);
			}
		}
		
		if(!listaTecnicosDelegadosEliminar.isEmpty()){
			for (DelegadoTecnicoForestal tecnico : listaTecnicosDelegadosEliminar) {
				tecnico.setEstado(false);
				
				delegadosInspeccionForestalFacade.guardarTecnicoDelegado(tecnico);
			}
		}
		
		listaDelegadosOperadorEliminar = new ArrayList<>();
		listaTecnicosDelegadosEliminar = new ArrayList<>();
		
	}
	
	public void guardarFotografias() {
		try{
			if(!listaImgsTipoCobertura.isEmpty()){
				for (FotografiaInformeForestal fotografia : listaImgsTipoCobertura) {
					if (fotografia.getDocImagen().getIdAlfresco() == null) {
						fotografia.getDocImagen().setIdViabilidad(viabilidadProyecto.getId());
						DocumentoViabilidad imagen = documentosFacade
								.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
										"VIABILIDAD_AMBIENTAL", fotografia.getDocImagen(), 2);
	
						if (imagen == null || imagen.getId() == null) {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
							return;
						}
	
						fotografia.setDocImagen(imagen);
					}
					fotografia.setIdInformeInspeccion(informeInspeccion.getId());
					fotografiasForestalFacade.guardarFotografia(fotografia);
				}
			}
			
			if(!listaImgsTipoEcosistema.isEmpty()){
				for (FotografiaInformeForestal fotografia : listaImgsTipoEcosistema) {
					if (fotografia.getDocImagen().getIdAlfresco() == null) {
						fotografia.getDocImagen().setIdViabilidad(viabilidadProyecto.getId());
						DocumentoViabilidad imagen = documentosFacade
								.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
										"VIABILIDAD_AMBIENTAL", fotografia.getDocImagen(), 2);
	
						if (imagen == null || imagen.getId() == null) {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
							return;
						}
	
						fotografia.setDocImagen(imagen);
					}
					fotografia.setIdInformeInspeccion(informeInspeccion.getId());
					fotografiasForestalFacade.guardarFotografia(fotografia);
				}
			}
			
			if(!listaImgsAreaImplantacion.isEmpty()){
				for (FotografiaInformeForestal fotografia : listaImgsAreaImplantacion) {
					if (fotografia.getDocImagen().getIdAlfresco() == null) {
						fotografia.getDocImagen().setIdViabilidad(viabilidadProyecto.getId());
						DocumentoViabilidad imagen = documentosFacade
								.guardarDocumento(proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
										"VIABILIDAD_AMBIENTAL", fotografia.getDocImagen(), 2);
	
						if (imagen == null || imagen.getId() == null) {
							JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_TAREA);
							return;
						}
	
						fotografia.setDocImagen(imagen);
					}
					fotografia.setIdInformeInspeccion(informeInspeccion.getId());
					fotografiasForestalFacade.guardarFotografia(fotografia);
				}
			}
			
			if (!listaImgsEliminar.isEmpty()) {
				for (FotografiaInformeForestal fotografia : listaImgsEliminar) {
					fotografia.setEstado(false);
					fotografiasForestalFacade.guardarFotografia(fotografia);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void firmarInforme() {
		mostrarFirma = true;
		documentoDescargado = false;
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			subirInforme();
			
			byte[] documentoContent = informeInspeccion.getArchivoInforme();
			
			if (documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(informeInspeccion.getNombreFichero());
			} else
				JsfUtil.addMessageError("Error al generar el archivo");
			
			documentoDescargado = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void subirInforme() throws ServiceException, CmisAlfrescoException {
		generarInforme(false);
		
		DocumentoViabilidad documentoInforme = new DocumentoViabilidad();
		documentoInforme.setNombre("InformeInspeccionForestal_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
		documentoInforme.setContenidoDocumento(informeInspeccion.getArchivoInforme());
		documentoInforme.setMime("application/pdf");
		documentoInforme.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION.getIdTipoDocumento());
		documentoInforme.setIdViabilidad(viabilidadProyecto.getId());

		documentoInforme = documentosFacade.guardarDocumentoProceso(
				proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
				"VIABILIDAD_AMBIENTAL", documentoInforme, 2, JsfUtil.getCurrentProcessInstanceId());
	}

	public String firmarDocumento() {
		try {
			generarInforme(false);
			
			documentoInformeAlfresco = new DocumentoViabilidad();
			documentoInformeAlfresco.setNombre("InformeInspeccionForestal_" + UtilViabilidad.getFileNameEscaped(informeInspeccion.getNumeroInforme().replace("/", "-")) + ".pdf");
			documentoInformeAlfresco.setContenidoDocumento(informeInspeccion.getArchivoInforme());
			documentoInformeAlfresco.setMime("application/pdf");
			documentoInformeAlfresco.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION.getIdTipoDocumento());
			documentoInformeAlfresco.setIdViabilidad(viabilidadProyecto.getId());

			documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
					proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
					"VIABILIDAD_AMBIENTAL", documentoInformeAlfresco, 2, JsfUtil.getCurrentProcessInstanceId());
			
			if(documentoInformeAlfresco.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInformeAlfresco);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if(documentoDescargado) {
	        byte[] contenidoDocumento = event.getFile().getContents();
	        
	        nombreDocumentoFirmado = event.getFile().getFileName();
	        
	        informeFirmaManual = new DocumentoViabilidad();
	        informeFirmaManual.setId(null);
	        informeFirmaManual.setContenidoDocumento(contenidoDocumento);
	        informeFirmaManual.setNombre(event.getFile().getFileName());
	        informeFirmaManual.setMime("application/pdf");
	        informeFirmaManual.setIdViabilidad(viabilidadProyecto.getId());
	        informeFirmaManual.setIdTipoDocumento(TipoDocumentoSistema.RCOA_VIABILIDAD_FORESTAL_INFORME_INSPECCION.getIdTipoDocumento());
	        
	        subido = true;
		} else{
			JsfUtil.addMessageError("No ha realizado la descarga del oficio");
		}
    }

	public void aceptar() {
		try {
			if(subido){
				documentoInformeAlfresco = documentosFacade.guardarDocumentoProceso(
						proyectoLicenciaCoa.getCodigoUnicoAmbiental(),
						"VIABILIDAD_AMBIENTAL", informeFirmaManual, 2, JsfUtil.getCurrentProcessInstanceId());
			}
			
			if (token) {
				String idAlfresco = documentoInformeAlfresco.getIdAlfresco();

				if (!documentosFacade.verificarFirmaVersion(idAlfresco)) {
					JsfUtil.addMessageError("El informe no está firmado electrónicamente.");
					return;
				}
			} else if (!token && !subido) {
				JsfUtil.addMessageError("Debe adjuntar el informe firmado.");
				return;
			}
			
			try {
				Map<String, Object> parametros = new HashMap<>();
				
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), parametros);
				
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);

				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_TAREA_COMPLETADA);
				JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				
			} catch (Exception e) {
				e.printStackTrace();
				JsfUtil.addMessageError("Ocurrió un error al enviar la tarea.");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Error al realizar la operación.");
		}
	}
}
