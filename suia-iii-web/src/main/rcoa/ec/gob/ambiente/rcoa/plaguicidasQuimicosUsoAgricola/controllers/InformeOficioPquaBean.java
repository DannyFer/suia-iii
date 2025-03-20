package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import lombok.Getter;
import lombok.Setter;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.DocumentoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.EfectoOrganismosAcuaticosFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.InformeTecnicoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ObservacionesActualizacionPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.OficioPronunciamientoPquaFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasDetalleFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade.ProyectoPlaguicidasFacade;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.DocumentoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.EfectoOrganismosAcuaticos;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.InformeOficioPquaEntity;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.InformeTecnicoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ObservacionesActualizacionPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.OficioPronunciamientoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProductoPqua;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidas;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.ProyectoPlaguicidasDetalle;
import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.RegistroProducto;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers.UtilViabilidad;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoUsuarioFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class InformeOficioPquaBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
	
	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
    private ProyectoPlaguicidasFacade proyectoPlaguicidasFacade;
	@EJB
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
    private ProyectoPlaguicidasDetalleFacade proyectoPlaguicidasDetalleFacade;
	@EJB
    private EfectoOrganismosAcuaticosFacade efectoOrganismosAcuaticosFacade;
	@EJB
    private InformeTecnicoPquaFacade informeTecnicoPquaFacade;
	@EJB
    private ObservacionesActualizacionPquaFacade observacionesActualizacionPquaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private OficioPronunciamientoPquaFacade oficioPronunciamientoPquaFacade;
	@EJB
	private  UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private TipoUsuarioFacade tipoUsuarioFacade;
	@EJB
	private DocumentoPquaFacade documentosFacade;
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@Getter
	@Setter
	private ProyectoPlaguicidas proyectoPlaguicidas;
	
	@Getter
	@Setter
	private InformeTecnicoPqua informeTecnico;
	
	@Getter
	@Setter
	private OficioPronunciamientoPqua oficioRevision;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;
	
	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficio;
	
	@Getter
	@Setter
	private EfectoOrganismosAcuaticos efectoMayorCategoria;
	
	@Getter
	@Setter
	private TipoDocumentoSistema tipoDocInforme, tipoDocOficio;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad, usuarioOperador;
	
	@Getter
	@Setter
	private Area areaTramite;
	
	@Getter
	@Setter
	private DocumentoPqua documentoOficioAlfresco;
	
	@Getter
	@Setter
	private List<ProyectoPlaguicidasDetalle> listaDetalleProyectoPlaguicidas;
	
	@Getter
	@Setter
	private List<EfectoOrganismosAcuaticos> listaComponentesResumen;
	
	@Getter
	@Setter
	private List<String> datosOperador;
	
	@Getter
	@Setter
	private Integer numeroRevision;
	
	@Getter
	@Setter
	private Boolean esPronunciamientoFavorable;
	
	@Getter
	@Setter
	private Map<String, Object> variables;
	
	@PostConstruct
	public void init() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());

			String tramite = (String) variables.get("tramite");

			if (variables.containsKey("numeroRevision")) {
				numeroRevision = Integer.valueOf((String) variables.get("numeroRevision"));
			}
			
			proyectoPlaguicidas = proyectoPlaguicidasFacade.getPorCodigoProyecto(tramite);
			
			if(proyectoPlaguicidas != null && proyectoPlaguicidas.getId() != null) {
				cargarDatosProyecto(proyectoPlaguicidas);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos Informe Revision Forestal.");
		}
	}
	
	public void cargarDatosProyecto(ProyectoPlaguicidas proyectoPlaguicidas) {
		this.proyectoPlaguicidas = proyectoPlaguicidas;
		areaTramite = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_SUSTANCIAS_RESIDUOS_DESECHOS);
		
		usuarioOperador = usuarioFacade.buscarUsuario(proyectoPlaguicidas.getUsuarioCreacion());
		datosOperador = usuarioFacade.recuperarNombreOperador(usuarioOperador);
		
		listaDetalleProyectoPlaguicidas = proyectoPlaguicidasDetalleFacade.getDetallePorProyecto(proyectoPlaguicidas.getId());
		
		efectoMayorCategoria = efectoOrganismosAcuaticosFacade.getItemMayorCategoria(proyectoPlaguicidas.getId());
		
		listaComponentesResumen = new ArrayList<>();
		
		List<EfectoOrganismosAcuaticos> listaEfectosTotal = efectoOrganismosAcuaticosFacade.getPorProyecto(proyectoPlaguicidas.getId());
		
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
		
		List<InformeTecnicoPqua> informesTecnicos = informeTecnicoPquaFacade.getPorProyecto(proyectoPlaguicidas.getId(), numeroRevision);
		if(informesTecnicos != null) {
			informeTecnico = informesTecnicos.get(0);
		}
	}
	
	public void generarInforme(Boolean marcaAgua) {
		try {
			
			if(informeTecnico == null || informeTecnico.getId() == null) {
				informeTecnico = new InformeTecnicoPqua();
				
				informeTecnico.setNombreFichero("Informe tecnico.pdf");
			} else {
				informeTecnico.setNombreFichero("Informe tecnico " + UtilViabilidad.getFileNameEscaped(informeTecnico.getNumeroInforme().replace("/", "-")) + ".pdf");
			}
			
			informeTecnico.setNombreReporte("Informe tecnico.pdf");
			informeTecnico.setFechaInforme(new Date());
			informeTecnico.setEsAprobacion(esPronunciamientoFavorable);
			
			if(esPronunciamientoFavorable) {
				tipoDocInforme = TipoDocumentoSistema.PQUA_INFORME_APROBADO;
				plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.PQUA_INFORME_APROBADO);
			} else {
				tipoDocInforme = TipoDocumentoSistema.PQUA_INFORME_OBSERVADO;
				plantillaReporteInforme = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.PQUA_INFORME_OBSERVADO);
			}
			
			InformeOficioPquaEntity informeEntity = cargarDatosInforme();

			File informePdfAux = UtilGenerarInforme.generarFichero(
					plantillaReporteInforme.getHtmlPlantilla(),
					informeTecnico.getNombreReporte(), true, informeEntity);
			
			File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

			Path path = Paths.get(informePdf.getAbsolutePath());
			informeTecnico.setArchivoInforme(Files.readAllBytes(path));
			String reporteHtmlfinal = informeTecnico.getNombreFichero().replace("/", "-");
			File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
			FileOutputStream file = new FileOutputStream(archivoFinal);
			file.write(informeTecnico.getArchivoInforme());
			file.close();
			informeTecnico.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informeTecnico.getNombreFichero()));

		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}
	
	public InformeOficioPquaEntity cargarDatosInforme() throws ServiceException {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		InformeOficioPquaEntity informeEntity = new InformeOficioPquaEntity();
		
		RegistroProducto registroProducto = proyectoPlaguicidas.getProductoPqua().getRegistroProducto();
		ProductoPqua productoReporte = proyectoPlaguicidas.getProductoPqua();
		
		String nombreOperador = datosOperador.get(1).equals("") ? datosOperador.get(0) : datosOperador.get(4);
		
		informeEntity.setNumeroInforme(informeTecnico.getNumeroInforme());
		informeEntity.setNombreOperador(nombreOperador);
		informeEntity.setFechaInforme(formatoFecha.format(informeTecnico.getFechaInforme()));
		informeEntity.setEvaluador(JsfUtil.getLoggedUser().getPersona().getNombre());
		informeEntity.setNroTramite(proyectoPlaguicidas.getCodigoProyecto());
		informeEntity.setFechaTramite(formatoFecha.format(proyectoPlaguicidas.getFechaCreacion()));
		
		informeEntity.setNroOficioAgrocalidad(registroProducto.getNumeroOficioAgrocalidad());
		informeEntity.setFechaOficioAgrocalidad(formatoFecha.format(registroProducto.getFechaIngresoOficioAgrocalidad()));
		informeEntity.setMesRevision(registroProducto.getMesIngresoSeleccion());
		
		informeEntity.setNombreProducto(productoReporte.getNombreComercialProducto());
		informeEntity.setComposicionProducto(productoReporte.getInfoIngredienteActivo() + ", " + productoReporte.getAbreviaturaFormulacion());
		informeEntity.setNroRegistro(productoReporte.getNumeroRegistro());
		informeEntity.setFechaRegistro(formatoFecha.format(productoReporte.getFechaRegistro()));
		informeEntity.setCategoriaAnterior(productoReporte.getCategoriaToxicologica());
		
		informeEntity.setInformacionAnalizada(getInformacionAnalizada(productoReporte, nombreOperador));
		
		informeEntity.setIngredienteActivo(productoReporte.getInfoIngredienteActivo());
		informeEntity.setFormulacion(productoReporte.getFormulacion());
		informeEntity.setTipoProducto(proyectoPlaguicidas.getProductoPqua().getTipoProducto().getNombre());
		informeEntity.setCategoriaNueva(proyectoPlaguicidas.getProductoPqua().getCategoriaFinal().getNombre());
		informeEntity.setColorNuevo(proyectoPlaguicidas.getProductoPqua().getColorFranjaFinal().getNombre());
		informeEntity.setDosis(getDosis());
		informeEntity.setCultivos(getCultivos());
		informeEntity.setPlagas(getPlagas());
		
		informeEntity.setCondicionEtiqueta(getCondicionEtiqueta(true));
		
		if(esPronunciamientoFavorable) {
			informeEntity.setInformacionOrganismosAcuaticos(getInfoOrganismosAcuaticos(productoReporte, nombreOperador));
		} else {
			informeEntity.setObservaciones(getObservacionesInforme());
		}
		
		
		return informeEntity;
	}
	
	public String getInformacionAnalizada(ProductoPqua productoReporte, String nombreOperador) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
		stringBuilder.append("<tr><th>N° Registro</th><th>Nombre Comercial</th><th>Nombre Genérico</th><th>Solicitante</th><th>Tipo de Producto</th><th>Categoría Toxicológica (Anterior)</th></tr>");
		
		stringBuilder.append("<tr>");
		stringBuilder.append("<td style=\"text-align: center;\">" + productoReporte.getNumeroRegistro() + "</td>");
		stringBuilder.append("<td>");
		stringBuilder.append(productoReporte.getNombreComercialProducto());
		stringBuilder.append("</td>");
		stringBuilder.append("<td>");
		stringBuilder.append(productoReporte.getInfoIngredienteActivo() + ", " + productoReporte.getAbreviaturaFormulacion());
		stringBuilder.append("</td>");
		stringBuilder.append("<td>");
		stringBuilder.append(nombreOperador);
		stringBuilder.append("</td>");
		stringBuilder.append("<td>");
		stringBuilder.append(proyectoPlaguicidas.getProductoPqua().getTipoProducto().getNombre());
		stringBuilder.append("</td>");
		stringBuilder.append("<td>");
		stringBuilder.append(productoReporte.getCategoriaToxicologica());
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getDosis() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDetalleProyectoPlaguicidas != null && !listaDetalleProyectoPlaguicidas.isEmpty()) {
			for (ProyectoPlaguicidasDetalle  item : listaDetalleProyectoPlaguicidas) {
				stringBuilder.append(item.getDetalleDosis());
				stringBuilder.append("<br />");
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String getCultivos() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDetalleProyectoPlaguicidas != null && !listaDetalleProyectoPlaguicidas.isEmpty()) {
			for (ProyectoPlaguicidasDetalle  item : listaDetalleProyectoPlaguicidas) {
				stringBuilder.append(item.getCultivo().getNombreComun());
				stringBuilder.append("<i>(");
				stringBuilder.append(item.getNombreCientificoCultivo());
				stringBuilder.append("</i>)");
				stringBuilder.append("<br />");
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String getPlagas() {
		StringBuilder stringBuilder = new StringBuilder();
		if(listaDetalleProyectoPlaguicidas != null && !listaDetalleProyectoPlaguicidas.isEmpty()) {
			for (ProyectoPlaguicidasDetalle  item : listaDetalleProyectoPlaguicidas) {
				stringBuilder.append(item.getDetallePlagas());
				stringBuilder.append("<br />");
			}
		}
		
		return stringBuilder.toString();
	}
	
	public String getCondicionEtiqueta(Boolean verPictograma) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
		stringBuilder.append("<tr><th colspan=\"2\">CONDICIÓN CON EL SISTEMA GLOBALMENTE ARMONIZADO EN ETIQUETA</th></tr>");
		
		StringBuilder stringBuilderPicto = new StringBuilder();
		
		for (EfectoOrganismosAcuaticos item : listaComponentesResumen) {
			stringBuilder.append("<tr>");
			stringBuilder.append("<th colspan=\"2\">" + item.getIngredienteActivo() + "</th>");
			stringBuilder.append("</tr>");
			stringBuilder.append("<tr>");
			stringBuilder.append("<td width=\"30%\"><strong>CATEGORÍA:</strong></td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getCategoria().getNombre());
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			stringBuilder.append("<tr>");
			stringBuilder.append("<td><strong>PICTOGRAMA:</strong></td>");
			stringBuilder.append("<td>");
			stringBuilder.append((item.getPictograma()) ? "SI" : "NO");
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			stringBuilder.append("<tr>");
			stringBuilder.append("<td><strong>PALABRA DE ADVERTENCIA:</strong></td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getPalabraAdvertencia());
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			stringBuilder.append("<tr>");
			stringBuilder.append("<td><strong>INDICACIONES DE PELIGRO:</strong></td>");
			stringBuilder.append("<td>");
			stringBuilder.append(item.getIndicacionesPeligro());
			stringBuilder.append("</td>");
			stringBuilder.append("</tr>");
			
			if(item.getPictograma()) {
				if(stringBuilderPicto.length() == 0) {
					stringBuilderPicto.append("<br />");
					stringBuilderPicto.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 50%; border-collapse:collapse;font-size:12px;\">");
				}
				stringBuilderPicto.append("<tr>");
				stringBuilderPicto.append("<th>" + item.getIngredienteActivo() + "</th>");
				stringBuilderPicto.append("</tr>");
				stringBuilderPicto.append("<tr>");
				stringBuilderPicto.append("<td style=\"text-align: center; \">");
				stringBuilderPicto.append("<img src=\'" + getRecursoImage("pictograma_plaguicidas.png") + "\' height=\'160\' width=\'160\' ></img>");
				stringBuilderPicto.append("</td>");
				stringBuilderPicto.append("</tr>");
			}
		}
		
		if(stringBuilderPicto.length() > 0) {
			stringBuilderPicto.append("</table>");
		}
		
		stringBuilder.append("</table>");
		
		if(verPictograma) {
			stringBuilder.append(stringBuilderPicto);
		}
		
		return stringBuilder.toString();
	}

	public String getInfoOrganismosAcuaticos(ProductoPqua productoReporte, String nombreOperador) {
		
		Boolean cambioColor = false;
		if(!productoReporte.getColorFranja().getId().equals(proyectoPlaguicidas.getProductoPqua().getColorFranjaFinal().getId())) {
			cambioColor = true;
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table border=\"1\" cellpadding=\"3\" cellspacing=\"0\" style=\"width: 100%; border-collapse:collapse;font-size:12px;\">");
		stringBuilder.append("<tr><th rowspan=\"2\">TIPO DE MODIFICACIONES POR ACTUALIZACIÓN EN ETIQUETA SGA</th><th colspan=\"2\">MODIFICACIONES EVIDENCIADAS</th></tr>");
		stringBuilder.append("<tr><th>SI</th><th>NO</th></tr>");
		
		stringBuilder.append("<tr>");
		stringBuilder.append("<td style=\"text-align: center;\">Inclusión de pictograma</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((efectoMayorCategoria.getPictograma()) ? "X" : "");
		stringBuilder.append("</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((efectoMayorCategoria.getPictograma()) ? "" : "X");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td style=\"text-align: center;\">Inclusión de palabra de advertencia</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((efectoMayorCategoria.getPictograma()) ? "X" : "");
		stringBuilder.append("</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((efectoMayorCategoria.getPictograma()) ? "" : "X");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td style=\"text-align: center;\">Inclusión de indicaciones de Peligro</td>");
		stringBuilder.append("<td style=\"text-align: center;\">X</td>"); //siempre si porque todas las categorias tienen indicaciones de peligro
		stringBuilder.append("<td style=\"text-align: center;\"> </td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td style=\"text-align: center;\">Cambio de color de franja por categoría toxicológica*</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((cambioColor) ? "X" : "");
		stringBuilder.append("</td>");
		stringBuilder.append("<td style=\"text-align: center;\">");
		stringBuilder.append((cambioColor) ? "" : "X");
		stringBuilder.append("</td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td colspan=\"3\">* Este cambio de franjas se dan conforme a la Autoridad Competente MSP </td>");
		stringBuilder.append("</tr>");
		
		stringBuilder.append("</table>");
		
		return stringBuilder.toString();
	}
	
	public String getObservacionesInforme() throws ServiceException {
		StringBuilder stringBuilder = new StringBuilder();
		
		List<ObservacionesActualizacionPqua> observacionesInforme = observacionesActualizacionPquaFacade.listarPorIdClaseNombreClaseNoCorregidas(
				proyectoPlaguicidas.getId(), "ActualizacionEtiquetaPqua");
		if(observacionesInforme.size() > 0) {
			stringBuilder.append("<ul style=\"font-size:12px; margin-top: 0px; margin-bottom: 0px;\">");
			for (ObservacionesActualizacionPqua item : observacionesInforme) {
				stringBuilder.append("<li>");
				stringBuilder.append(item.getTipoObservacion().getNombre());
				if(item.getTipoObservacion().getNombre().equals("Otros")) {
					stringBuilder.append(" - ");
					stringBuilder.append(item.getDescripcion());
				}
				stringBuilder.append("</li>");
			}
			stringBuilder.append("</ul>");
		}
		
		return stringBuilder.toString();
	}
	
	private static URL getRecursoImage(String nombreImagen) {
		ServletContext servletContext = (ServletContext) FacesContext
				.getCurrentInstance().getExternalContext().getContext();
		try {
			return servletContext.getResource("/resources/images/"
					+ nombreImagen);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void guardarInforme() {
		informeTecnico.setIdTarea((int) bandejaTareasBean.getTarea().getTaskId());
		informeTecnico.setIdProyecto(proyectoPlaguicidas.getId());
		informeTecnico.setNumeroRevision(numeroRevision);

		informeTecnicoPquaFacade.guardar(informeTecnico, areaTramite);
	}

	public void generarOficio(Boolean marcaAgua) throws Exception {
		oficioRevision = oficioPronunciamientoPquaFacade.getPorProyectoRevision(proyectoPlaguicidas.getId(), numeroRevision);
		
		if(oficioRevision == null || oficioRevision.getId() == null) {
			oficioRevision = new OficioPronunciamientoPqua();
			
			oficioRevision.setNombreFichero("Oficio pronunciamiento.pdf");
		} else {
			oficioRevision.setNombreFichero("Oficio pronunciamiento " + UtilViabilidad.getFileNameEscaped(oficioRevision.getNumeroOficio().replace("/", "-")) + ".pdf");
		}
		
		oficioRevision.setNombreReporte("Oficio pronunciamiento.pdf");
		oficioRevision.setFechaOficio(new Date());
		oficioRevision.setEsAprobacion(esPronunciamientoFavorable);
		
		if(esPronunciamientoFavorable) {
			tipoDocOficio = TipoDocumentoSistema.PQUA_OFICIO_APROBADO;
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.PQUA_OFICIO_APROBADO);
		} else {
			tipoDocOficio = TipoDocumentoSistema.PQUA_OFICIO_OBSERVADO;
			plantillaReporteOficio = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.PQUA_OFICIO_OBSERVADO);
		}
		
		buscarAutoridadFirma();
		
		InformeOficioPquaEntity oficioEntity = cargarDatos();

		File informePdfAux = UtilGenerarInforme.generarFichero(
				plantillaReporteOficio.getHtmlPlantilla(),
				oficioRevision.getNombreReporte(), true, oficioEntity);
		
		File oficioPdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(oficioPdf.getAbsolutePath());
		oficioRevision.setArchivoOficio(Files.readAllBytes(path));
		String reporteHtmlfinal = oficioRevision.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficioRevision.getArchivoOficio());
		file.close();
		oficioRevision.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficioRevision.getNombreFichero()));
	}
	
	public InformeOficioPquaEntity cargarDatos() throws ServiceException {
		DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
		
		oficioRevision.setFechaOficio(new Date());
		
		RegistroProducto registroProducto = proyectoPlaguicidas.getProductoPqua().getRegistroProducto();
		ProductoPqua productoReporte = proyectoPlaguicidas.getProductoPqua();
		
		String nombreOperador = datosOperador.get(1).equals("") ? datosOperador.get(0) : datosOperador.get(4);

		InformeOficioPquaEntity oficioEntity = new InformeOficioPquaEntity();
		oficioEntity.setNumeroOficio(oficioRevision.getNumeroOficio());
		oficioEntity.setFechaOficio(formatoFecha.format(oficioRevision.getFechaOficio()));
		
		oficioEntity.setDestinatario(datosOperador.get(1).equals("") ? datosOperador.get(0) : datosOperador.get(1));
		if(datosOperador.get(1).equals("")) {
			oficioEntity.setNombreEmpresa("");
			oficioEntity.setDisplayEmpresa("none");
		} else {
			oficioEntity.setNombreEmpresa(datosOperador.get(4));
			oficioEntity.setDisplayEmpresa("inline");
		}
		
		oficioEntity.setNombreProducto(productoReporte.getNombreComercialProducto());
		
		oficioEntity.setNroOficioAgrocalidad(registroProducto.getNumeroOficioAgrocalidad());
		oficioEntity.setFechaOficioAgrocalidad(formatoFecha.format(registroProducto.getFechaIngresoOficioAgrocalidad()));
		oficioEntity.setMesRevision(registroProducto.getMesIngresoSeleccion());
		
		oficioEntity.setTipoProducto(proyectoPlaguicidas.getProductoPqua().getTipoProducto().getNombre());
		oficioEntity.setComposicionProducto(productoReporte.getInfoIngredienteActivo() + ", " + productoReporte.getAbreviaturaFormulacion());
		oficioEntity.setNroTramite(proyectoPlaguicidas.getCodigoProyecto());
		oficioEntity.setFechaTramite(formatoFecha.format(proyectoPlaguicidas.getFechaCreacion()));
		oficioEntity.setNombreOperador(nombreOperador);

		oficioEntity.setFormulacion(productoReporte.getFormulacion());
		oficioEntity.setCategoriaNueva(proyectoPlaguicidas.getProductoPqua().getCategoriaFinal().getNombre());
		oficioEntity.setColorNuevo(proyectoPlaguicidas.getProductoPqua().getColorFranjaFinal().getNombre());
		oficioEntity.setDosis(getDosis());
		oficioEntity.setCultivos(getCultivos());
		oficioEntity.setPlagas(getPlagas());
		
		oficioEntity.setNumeroInforme(informeTecnico.getNumeroInforme());
		oficioEntity.setFechaInforme(formatoFecha.format(informeTecnico.getFechaInforme()));
		
		oficioEntity.setNombreAutoridad(usuarioAutoridad.getPersona().getTitulo() + ". " + usuarioAutoridad.getPersona().getNombre());
		oficioEntity.setCargoAutoridad(cargoAutoridad());
		
		if(esPronunciamientoFavorable) {
			oficioEntity.setCondicionEtiqueta(getCondicionEtiqueta(false));
			
			if(proyectoPlaguicidas.getProductoPqua().getTipoProducto().getCodigo() != null 
					&& proyectoPlaguicidas.getProductoPqua().getTipoProducto().getCodigo().equals("pqua.tipo.producto.insecticida")) {
				oficioEntity.setDisplayConclusion("none");
				oficioEntity.setDisplayConclusionInsecticida("inline");
			} else {
				oficioEntity.setDisplayConclusion("inline");
				oficioEntity.setDisplayConclusionInsecticida("none");
			}
		} else {
			oficioEntity.setNroRegistro(productoReporte.getNumeroRegistro());
			oficioEntity.setCategoriaAnterior(productoReporte.getCategoriaToxicologica());
			
			oficioEntity.setObservaciones(getObservacionesInforme());
		}
		
		return oficioEntity;
	}
	
	public void buscarAutoridadFirma() {
		
		String roldirector = Constantes.getRoleAreaName("role.pqua.pc.director");
		Area areaFirma = areaTramite;
		
		if(esPronunciamientoFavorable) {
			roldirector = Constantes.getRoleAreaName("role.pqua.pc.autoridad");
			areaFirma = areaTramite.getArea();
		}
	
		List<Usuario> listaUsuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(roldirector, areaFirma);
		
		if(listaUsuarioAutoridad==null || listaUsuarioAutoridad.isEmpty()){
			System.out.println("No se encontro usuario " + roldirector + " en " + areaTramite.getAreaName());
			throw new RuntimeException("_autoridad_invalid");
		}
		
		usuarioAutoridad = listaUsuarioAutoridad.get(0);
	}
	
	private String cargoAutoridad(){
		
		String nombreCargo = "";
		try {
			
			List<TipoUsuario> listaTipos = new ArrayList<TipoUsuario>();
			listaTipos = tipoUsuarioFacade.obtenerListaTipoUsuario(usuarioAutoridad);
			
			boolean femenino = false;
			boolean masculino = false;
			if(usuarioAutoridad.getPersona().getGenero().equals("FEMENINO") || usuarioAutoridad.getPersona().getGenero().equals("MUJER")){
				femenino = true;
			}else if(usuarioAutoridad.getPersona().getGenero().equals("MASCULINO") || usuarioAutoridad.getPersona().getGenero().equals("HOMBRE")){
				masculino = true;
			}
			
			String cargoAdicional = "";
			if(listaTipos != null && !listaTipos.isEmpty()){
				if(listaTipos.get(0).getTipo().equals(1)){
					cargoAdicional = ", (SUBROGANTE)";
				}else{
					if(femenino){
						cargoAdicional = ", (ENCARGADA)";
					}else if(masculino){
						cargoAdicional = ", (ENCARGADO)";
					}else{
						cargoAdicional = ", (ENCARGADO/A)";
					}				
				}
			}
			
			if(esPronunciamientoFavorable) {
				if(femenino){
					nombreCargo = "SUBSECRETARIA DE CALIDAD AMBIENTAL" + cargoAdicional;
				}else if(masculino){
					nombreCargo = "SUBSECRETARI0 DE CALIDAD AMBIENTAL" + cargoAdicional;
				}else{
					nombreCargo = "SUBSECRETARI0/A DE CALIDAD AMBIENTAL" + cargoAdicional;
				}			
			}else{
				if(femenino){
					nombreCargo = "DIRECTORA DE SUSTANCIAS QUÍMICAS RESIDUOS Y DESECHOS PELIGROSOS Y NO PELIGROSOS" + cargoAdicional;
				}else if(masculino){
					nombreCargo = "DIRECTOR DE SUSTANCIAS QUÍMICAS RESIDUOS Y DESECHOS PELIGROSOS Y NO PELIGROSOS" + cargoAdicional;
				}else{
					nombreCargo = "DIRECTOR/A DE SUSTANCIAS QUÍMICAS RESIDUOS Y DESECHOS PELIGROSOS Y NO PELIGROSOS" + cargoAdicional;
				}			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return nombreCargo;
	}
	
	public void guardarOficio() throws Exception {
		oficioRevision.setIdTarea((int) bandejaTareasBean.getTarea().getTaskId());
		oficioRevision.setProyectoPlaguicidas(proyectoPlaguicidas);
		oficioRevision.setNumeroRevision(numeroRevision);
		
		oficioPronunciamientoPquaFacade.guardar(oficioRevision, areaTramite);
	}
	
	public void guardarFechaOficio(OficioPronunciamientoPqua oficioRevision) throws Exception {
		oficioRevision.setFechaFirma(new Date());
		
		oficioPronunciamientoPquaFacade.guardar(oficioRevision);
	}
	
	public void guardarProyecto() throws Exception {
		proyectoPlaguicidasFacade.guardar(proyectoPlaguicidas);
	}
	
	public DocumentoPqua guardarDocumentoOficio() throws Exception {
		documentoOficioAlfresco = new DocumentoPqua();
		documentoOficioAlfresco.setNombre(oficioRevision.getNombreFichero());
		documentoOficioAlfresco.setContenidoDocumento(oficioRevision.getArchivoOficio());
		documentoOficioAlfresco.setMime("application/pdf");
		documentoOficioAlfresco.setIdTabla(oficioRevision.getId());
		documentoOficioAlfresco.setNombreTabla(OficioPronunciamientoPqua.class.getSimpleName());
		documentoOficioAlfresco.setIdTipoDocumento(tipoDocOficio.getIdTipoDocumento());
		
		
		documentoOficioAlfresco = documentosFacade.guardarDocumentoAlfresco(proyectoPlaguicidas.getCodigoProyecto(), 
				"PQUA_OFICIO_PRONUNCIAMIENTO", 0L, documentoOficioAlfresco, tipoDocOficio);
		
		return documentoOficioAlfresco;
	}
	
	public void notificarOperador(DocumentoPqua documentoOficioAlfresco) throws Exception {
		
		ProductoPqua productoReporte = proyectoPlaguicidas.getProductoPqua();
		
		String nombreOperador = datosOperador.get(1).equals("") ? datosOperador.get(0) : datosOperador.get(4);
		
		String plantilla = "bodyNotificacionPquaObservacion";
		Object[] parametrosCorreo = new Object[] {nombreOperador, nombreOperador};
		if (esPronunciamientoFavorable) {
			plantilla = "bodyNotificacionPquaAprobacion";
			parametrosCorreo = new Object[] {nombreOperador, productoReporte.getNombreComercialProducto(), productoReporte.getInfoIngredienteActivo(), nombreOperador};
		}
		
		MensajeNotificacion mensajeNotificacion =  mensajeNotificacionFacade.buscarMensajesNotificacion(plantilla);
		String notificacion = String.format(mensajeNotificacion.getValor(), parametrosCorreo);
		
		Object[] parametrosAsunto = new Object[] {productoReporte.getNombreComercialProducto()};
		String asunto = String.format(mensajeNotificacion.getAsunto(), parametrosAsunto);
		
		byte[] oficioContent = documentosFacade.descargar(documentoOficioAlfresco.getIdAlfresco());
		
		String nombreOficio = documentoOficioAlfresco.getNombre();
		File fileArchivo = new File(System.getProperty("java.io.tmpdir") + "/" + nombreOficio);
		FileOutputStream file = new FileOutputStream(fileArchivo);
		file.write(oficioContent);
		file.close();
		
		List<File> filesDocumentos = new ArrayList<>();
		filesDocumentos.add(fileArchivo);
		
		double tamanioOficio = fileArchivo.length() / (1024 * 1024);
		
		if(!datosOperador.get(1).equals("")) {
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuarioOperador.getNombre());
			usuarioOperador.getPersona().setContactos(organizacion.getContactos());
		}
		
		if(tamanioOficio <= 20) {
			List<String> listaArchivos = new ArrayList<>();
			listaArchivos.add(nombreOficio);
			
			Email.sendEmailAdjuntos(usuarioOperador, asunto, notificacion, listaArchivos, proyectoPlaguicidas.getCodigoProyecto(), JsfUtil.getLoggedUser());
		} else {
			Email.sendEmail(usuarioOperador, asunto, notificacion, proyectoPlaguicidas.getCodigoProyecto(), JsfUtil.getLoggedUser());
		}
		
		if(filesDocumentos!= null && filesDocumentos.size() > 0) {
			for (File item : filesDocumentos) {
				item.delete();
			}
		}
	}
	
}
