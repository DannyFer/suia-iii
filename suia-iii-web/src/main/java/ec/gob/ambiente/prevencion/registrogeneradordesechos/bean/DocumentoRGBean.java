package ec.gob.ambiente.prevencion.registrogeneradordesechos.bean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.GeneradorDesechosDesechoPeligroso;
import ec.gob.ambiente.suia.domain.GeneradorDesechosPeligrosos;
import ec.gob.ambiente.suia.domain.InformeTecnicoRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.ObservacionesFormularios;
import ec.gob.ambiente.suia.domain.OficioEmisionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.OficioObservacionRegistroGeneradorDesechos;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.PoliticaDesecho;
import ec.gob.ambiente.suia.domain.PuntoRecuperacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.observaciones.facade.ObservacionesFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.InformeOficioRGFacade;
import ec.gob.ambiente.suia.prevencion.registrogeneradordesechos.facade.RegistroGeneradorDesechosFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;

@ManagedBean
@ViewScoped
public class DocumentoRGBean implements Serializable {

	private static final long serialVersionUID = -42729579304117925L;

	private final Logger LOG = Logger.getLogger(DocumentoRGBean.class);

	@EJB
	private ObservacionesFacade observacionesFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;

	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	private InformeOficioRGFacade informeOficioRGFacade;

	@EJB
	private RegistroGeneradorDesechosFacade registroGeneradorDesechosFacade;

	@EJB
	private DocumentosFacade documentosFacade;

	@EJB
	private CrudServiceBean crudServiceBean;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;

	@Setter
	@Getter
	private GeneradorDesechosPeligrosos generador;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteInforme;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficio;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteOficioObservacion;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteBorrador;

	@Getter
	@Setter
	private PlantillaReporte plantillaReporteBorradorNormativa;

	@Getter
	private InformeTecnicoRegistroGeneradorDesechos informe;

	@Getter
	public OficioEmisionRegistroGeneradorDesechos oficio;

	@Getter
	private OficioObservacionRegistroGeneradorDesechos oficioObservaciones;

	private LinkedHashMap<String, List<ObservacionesFormularios>> observaciones;
	
	@Getter
	@Setter
	private Documento documentoManual;

	private String accion;

	private String nombreEmpresa;

	private String nombreEmpresaMostrar;

	private String cargoRepresentanteLegalMostrar;

	private String[] cargoEmpresa;
	
	private String ruc;

	@Getter
	private boolean emision, descargado, subido;

	private Usuario usuarioAutoridad;

	private Usuario tecnicoResponsable;

	private UbicacionesGeografica ubicacion;

	@Getter
	@Setter
	private int contadorBandejaTecnico;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	@Getter
	@Setter
	private boolean mostrarCampos = true;


	@PostConstruct
	public void init() {
		try {
			Integer idGenerador = Integer.parseInt(JsfUtil.getCurrentTask()
					.getVariable(GeneradorDesechosPeligrosos.VARIABLE_ID_GENERADOR).toString());

			Object contadorBandejaTecnicoValue = JsfUtil.getCurrentTask().getVariable(
					GeneradorDesechosPeligrosos.VARIABLE_CANTIDAD_OBSERVACIONES);

			if (contadorBandejaTecnicoValue == null || contadorBandejaTecnicoValue.toString().isEmpty()) {
				contadorBandejaTecnico = 0;
			} else {
				try {
					contadorBandejaTecnico = Integer.parseInt(contadorBandejaTecnicoValue.toString());
				} catch (Exception e) {
					LOG.error("Error recuperado cantidad de observaciones en registro de generador", e);
				}
			}

			accion = JsfUtil.getCurrentTask().getVariable("accion").toString();

			boolean responsabilidadExtendida = Boolean.parseBoolean(JsfUtil.getCurrentTask()
					.getVariable("esResponsabilidadExtendida").toString());

			emision = Boolean.parseBoolean(JsfUtil.getCurrentTask().getVariable("tipoTramite").toString());

			generador = registroGeneradorDesechosFacade.cargarGeneradorParaDocumentoPorId(idGenerador);
						
			//si el rgd está asignado a una DP se busca la oficina tecnica correspondiente 
        	if(!generador.getResponsabilidadExtendida() && 
        			generador.getAreaResponsable().getTipoArea().getSiglas().equals("DP") && 
        			!generador.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS)) {
        		List<PuntoRecuperacion> listaPuntos = registroGeneradorDesechosFacade.buscarPuntoRecuperacion(generador);
        		Area areaResponsable = areaFacade.getAreaCoordinacionZonal(listaPuntos
						.get(0).getUbicacionesGeografica().getUbicacionesGeografica());
        		if(areaResponsable != null) {
        			generador.setAreaResponsable(areaResponsable);
        			registroGeneradorDesechosFacade.guardar(generador);
        			generador = registroGeneradorDesechosFacade.cargarGeneradorParaDocumentoPorId(idGenerador);
        		}
        	}

			String cedulaAutoridad = (String)JsfUtil
					.getCurrentTask()
					.getVariable(responsabilidadExtendida && !validarExistenciaObservacionesProponente() ? "subsecretaria": "director");
//			usuarioAutoridad = usuarioFacade.buscarUsuarioWithOutFilters(cedulaAutoridad);
			Map<String, Object> params = new ConcurrentHashMap<String, Object>();
			if (responsabilidadExtendida) {
				if(validarExistenciaObservacionesProponente())
				{
					usuarioAutoridad = areaFacade.getUsuarioPorRolArea("role.gerente", generador.getAreaResponsable());
					params.put("director", usuarioAutoridad.getNombre());
				}else
				{
					usuarioAutoridad = areaFacade.getSubsecretariaCalidadAmbiental();
					params.put("subsecretaria", usuarioAutoridad.getNombre());
				}
			} else {
				Usuario usuario = new Usuario();
				if (generador.getAreaResponsable()==null){
					usuario = usuarioFacade.buscarUsuario(cedulaAutoridad);
					generador.setAreaResponsable(usuario.getListaAreaUsuario().get(0).getArea());
					registroGeneradorDesechosFacade.guardar(generador);
				}
				if(generador.getAreaResponsable().getId().equals(Constantes.ID_PARQUE_NACIONAL_GALAPAGOS))
					usuarioAutoridad = areaFacade.getDirectorProvincial(generador.getAreaResponsable());
				else
					usuarioAutoridad = areaFacade.getDirectorProvincial(generador.getAreaResponsable().getArea());
				
				params.put("director", usuarioAutoridad.getNombre());
			}
			
			try
			{	
				if(cedulaAutoridad==null  || !cedulaAutoridad.equals(usuarioAutoridad.getNombre()))
				{
					procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), JsfUtil.getCurrentTask().getProcessInstanceId(), params);	
					JsfUtil.getCurrentTask().updateVariable(responsabilidadExtendida && !validarExistenciaObservacionesProponente() ? "subsecretaria": "director",usuarioAutoridad.getNombre());
				}

			}
			catch(JbpmException e)
			{}
			
			//para actualizar el codigo del registro con la nueva área del proyecto solo para los proyectos de emisión 
			if(generador != null && generador.getCodigo() != null && generador.getDocumentoBorrador() == null && emision) {
				if(generador.getResponsabilidadExtendida() == true){
					if(!generador.getCodigo().contains(generador.getAreaResponsable().getArea().getAreaAbbreviation())){
						generador.setCodigo(registroGeneradorDesechosFacade.generarCodigoRegistroGeneradorDesechos(generador.getAreaResponsable()));
						registroGeneradorDesechosFacade.guardar(generador);
					}
				} else {
					if(!generador.getCodigo().contains(generador.getAreaResponsable().getAreaAbbreviation())){
						generador.setCodigo(registroGeneradorDesechosFacade.generarCodigoRegistroGeneradorDesechos(generador.getAreaResponsable()));
						registroGeneradorDesechosFacade.guardar(generador);
					}
				}
			}

			cargoEmpresa = getEmpresa();
			if (cargoEmpresa != null) {
				if(cargoEmpresa[0]==null)
					cargoEmpresa[0]="";
				
                nombreEmpresa = cargoEmpresa[0] + " de la empresa " + cargoEmpresa[1];
                nombreEmpresa= nombreEmpresa.replaceAll("&(?!amp;)", "&amp;");
                nombreEmpresaMostrar = cargoEmpresa[1];
                nombreEmpresaMostrar= nombreEmpresaMostrar.replaceAll("&(?!amp;)", "&amp;");
                cargoRepresentanteLegalMostrar = cargoEmpresa[0];
            }
			
			if(generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()){
				mostrarCampos = false;
			}
			
		} catch (Exception e) {
			LOG.error("Error al cargar el informe tecnico del registro de generador", e);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_INICIAR_TAREA);
		}
	}

	public void visualizarInforme(boolean setCurrentDate) throws Exception {
        String url = JsfUtil.getRequest().getRequestURI();
        if (!url.equals("/suia-iii/prevencion/registrogeneradordesechos/tecnicoResponsableOficioEmisionActualizacion.jsf")) {
		if (plantillaReporteInforme == null)
			plantillaReporteInforme = informeOficioRGFacade
					.getPlantillaReporte(TipoDocumentoSistema.TIPO_INFORME_TECNICO_RGD);

		if (informe == null)
			inicializarInformeTecnicoAsociado();

		if (informe == null) {
			informe = new InformeTecnicoRegistroGeneradorDesechos();
			informe.setContadorBandejaTecnico(contadorBandejaTecnico);
			informe.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			informe.setNumero(informeOficioRGFacade.generarCodigoInforme(generador));
			informe.setCiudad(generador.getAreaResponsable().getAreaName());
			informe.setAccion(accion.equals("emision.jsf") ? "Emisión" : "Actualización");
			informe.setFecha(new Date());
			informe.setAntecedentesAdicional("");
			informe.setObjetivosAdicional("");
			informe.setObservacionesDocumentoAdjuntado("");
			informe.setObservacionesAdicional("");
			informe.setAntecedentesAdicional("");
			informe.setConclusionesAdicional("");
			informe.setRecomendaciones("");
			informe.setRecomendacionesEncabezado("");

			informeOficioRGFacade.guardarInforme(informe, generador, JsfUtil.getLoggedUser());

			borrarObservacionesInformeTecnico();
		} else {
			//para actualizar el codigo del informe con la nueva área del proyecto o el nuevo año de generacion del informe
			if(url.equals("/suia-iii/prevencion/registrogeneradordesechos/tecnicoResponsableInformeTecnico.jsf")) {
				Calendar cal = Calendar.getInstance();
				String anioActual = Integer.toString(cal.get(Calendar.YEAR));
				
				if(!informe.getNumero().contains(generador.getAreaResponsable().getAreaAbbreviation())
						|| !informe.getNumero().contains("-" + anioActual + "-")) {
					informe.setNumeroAnterior(informe.getNumero());
					informe.setNumero(informeOficioRGFacade.generarCodigoInforme(generador));
					
					informeOficioRGFacade.guardarInforme(informe);
					
					informe.setModificado(true);
				}
			}
		}

		// elimino html generado del objeto 
		informe.setNormaVigente(elimuinarParrafoHtml(informe.getNormaVigente()));
		informe.setConclusiones(elimuinarParrafoHtml(informe.getConclusiones()));
		
		
		if (tecnicoResponsable == null || !tecnicoResponsable.getId().equals(informe.getEvaluador().getId())) {
			//tecnicoResponsable = usuarioFacade.buscarUsuario(informe.getUsuarioCreacion());
			tecnicoResponsable = usuarioFacade.buscarUsuario(informe.getEvaluador().getNombre());
		}

		if (setCurrentDate)
//			informe.setFecha(new Date());
		informe.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFechaModificacion()!=null?informe.getFechaModificacion():informe.getFecha()));
		informe.setNombreFichero("InformeTecnico_" + getFileNameEscaped(informe.getNumero()).replace("/", "-") + ".pdf");
		informe.setNombreReporte("InformeTecnico.pdf");

		String articulo = "";
		if (generador.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(generador.getUsuario().getPersona().getTipoTratos()
					.getNombre())
					+ " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "Sra./Sr.";

		if (generador.getUsuario().getPersona().getTipoTratos() != null
				&& generador.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !generador.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = generador.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = generador.getUsuario().getPersona().getTipoTratos().getNombre();
		}

		sujetoControl = articulo + sujetoControl;

		if (generador.getUsuario().getPersona().getTitulo() != null
				&& !generador.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + generador.getUsuario().getPersona().getTitulo() + ",";
		}

		if (generador.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + generador.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += " " + generador.getUsuario().getPersona().getNombre();
		}

		if (informe.getRecomendaciones() == null || informe.getRecomendaciones().isEmpty())
			informe.setRecomendacionesEncabezado("");
		else
			informe.setRecomendacionesEncabezado("<strong>6. RECOMENDACIONES</strong>");

		informe.setSujetoControl(sujetoControl);
		informe.setSujetoControlEncabez(sujetoControlEncabez);

		informe.setProponente(getProponente(sujetoControl));
		informe.setEvaluadorMostrar(tecnicoResponsable != null ? tecnicoResponsable.getPersona().getNombre()
				: "TECNICO RESPONSABLE");
		informe.setSolicitud(generador.getSolicitud());
		informe.setProyectoCodigo((generador.getProyecto() == null)?"":(generador.getProyecto().getCodigo()+" asociado a "));
		informe.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(generador.getFechaCreacion()));
		informe.setTipoDocumentoAdjuntado(null);
		informe.setCumpleMostrar(informe.getCumple() == null ? "PENDIENTE" : informe.getCumple() ? "CUMPLE"
				: "NO CUMPLE");
		informe.setObservaciones(getObservacionesGeneradorTabla());

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteInforme.getHtmlPlantilla(),
				informe.getNombreReporte(), true, informe);

		Path path = Paths.get(informePdf.getAbsolutePath());
		informe.setArchivoInforme(Files.readAllBytes(path));
		String reporteHtmlfinal = informe.getNombreFichero().replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(informe.getArchivoInforme());
		file.close();
		informe.setInformePath(JsfUtil.devolverContexto("/reportesHtml/" + informe.getNombreFichero()));
        }
	}

	public void visualizarOficio(boolean setCurrentDate, boolean generarDatosAutoridad, boolean firma) throws Exception {

		if (plantillaReporteOficio == null){
			if(generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()){
				plantillaReporteOficio = informeOficioRGFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_EMISION_RGD_REP);
			}else{
				plantillaReporteOficio = informeOficioRGFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_EMISION_RGD);
			}			
		}

		if (oficio == null)
			oficio = informeOficioRGFacade.getOficioEmisionActualizacion(generador, JsfUtil.getCurrentTask()
					.getProcessInstanceId(), contadorBandejaTecnico);

		if (oficio == null) {
			oficio = new OficioEmisionRegistroGeneradorDesechos();
			oficio.setContadorBandejaTecnico(contadorBandejaTecnico);
			oficio.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			oficio.setNumero(informeOficioRGFacade.generarCodigoOficioEmisionActualizacion(generador));

			oficio.setRecomendaciones("");

			informeOficioRGFacade.guardarOficioEmision(oficio, generador, JsfUtil.getLoggedUser());

			borrarObservacionesOficios();
		} else {
			//para actualizar el codigo del oficio con la nueva área del proyecto
			String url = JsfUtil.getRequest().getRequestURI();
			Calendar cal = Calendar.getInstance();
			String anioActual = Integer.toString(cal.get(Calendar.YEAR));
			if(url.equals("/suia-iii/prevencion/registrogeneradordesechos/tecnicoResponsableOficioEmisionActualizacion.jsf")) {
				if(!oficio.getNumero().contains(generador.getAreaResponsable().getAreaAbbreviation())
						|| !oficio.getNumero().contains("-" + anioActual + "-")) {
					oficio.setNumeroAnterior(oficio.getNumero());
					oficio.setNumero(informeOficioRGFacade.generarCodigoOficioEmisionActualizacion(generador));
					
					informeOficioRGFacade.guardarOficioEmision(oficio);
				}
			}
		}
		
		if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
			switch (generador.getPoliticaDesecho().getId()) {
			case PoliticaDesecho.POLITICA_CELULARES:
				oficio.setDesecho("equipos celulares en desuso");
				oficio.setAcuerdo("191 publicado en el R. O. 881 del 29 de enero de 2013");
				break;
			case PoliticaDesecho.POLITICA_NEUMATICOS:
				oficio.setDesecho("neumáticos usados");
				oficio.setAcuerdo("098 publicado en el R. O. 598 del 30 de septiembre de 2015 ");
				break;
			case PoliticaDesecho.POLITICA_PILAS:
				oficio.setDesecho("pilas usadas");
				oficio.setAcuerdo("022 publicado en el R. O. 943 del 29 de abril de 2013");
				break;
			case PoliticaDesecho.POLITICA_PLASTICOS:
				oficio.setDesecho("desechos plásticos de uso agrícola");
				oficio.setAcuerdo("042 publicado en el R. O. 498 del 30 de mayo de 2019");
				break;
			
			}
		}
		
		// elimino html generado del objeto 
		oficio.setCumplimiento(elimuinarParrafoHtml(oficio.getCumplimiento()));
		oficio.setEstablecido(elimuinarParrafoHtml(oficio.getEstablecido()));
		
		if (oficio.getNumero().isEmpty()){
			oficio.setNumero(informeOficioRGFacade.generarCodigoOficioEmisionActualizacion(generador));
			informeOficioRGFacade.guardarOficioEmision(oficio, generador, JsfUtil.getLoggedUser());
		}
		
		String anioCodigoOficioObservaciones=oficio.getNumero().substring(6, 10);
		String anioActual=secuenciasFacade.getCurrentYear();

		if(!anioCodigoOficioObservaciones.equals(anioActual)){
			oficio.setNumero(oficio.getNumero().replace(anioCodigoOficioObservaciones, anioActual));
			crudServiceBean.saveOrUpdate(oficio);
		}

		if (setCurrentDate)
			oficio.setFecha(new Date());

		if(generarDatosAutoridad){
			// incluir informacion de la sede de la zonal en el documento
			if(generador.getProyecto() != null){
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, generador.getProyecto(), null);
				oficio.setFechaMostrar(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficio.getFecha()));
			}else{
				if(generador.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					oficio.setFechaMostrar(generador.getAreaResponsable().getArea().getAreaSedeZonal()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficio.getFecha()));
				else
					oficio.setFechaMostrar(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficio.getFecha()));
			}
		}else{
			oficio.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(oficio.getFecha()));
		}

		String articulo = "";
		if (generador.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(generador.getUsuario().getPersona().getTipoTratos()
					.getNombre())
					+ " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "<strong>Sra./Sr. &nbsp; &nbsp;&nbsp; </strong>";

		if (generador.getUsuario().getPersona().getTipoTratos() != null
				&& generador.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !generador.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = generador.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = "<strong>" + generador.getUsuario().getPersona().getTipoTratos().getNombre()
					+ " &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; </strong>";
		}

		sujetoControl = articulo + sujetoControl;

		if (generador.getUsuario().getPersona().getTitulo() != null
				&& !generador.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + generador.getUsuario().getPersona().getTitulo() + ",";
		}

		if (generador.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + generador.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += generador.getUsuario().getPersona().getNombre();
			oficio.setOperador(generador.getUsuario().getPersona().getNombre());
			oficio.setRepresentante(generador.getUsuario().getPersona().getNombre());
		}
		
		if (generador.getUsuario().getPersona().getPin() != null) {
			oficio.setRuc(generador.getUsuario().getPersona().getPin());
		}else{
			oficio.setRuc("");
		}

		oficio.setSujetoControl(sujetoControl.trim());
		oficio.setSujetoControlEncabez(sujetoControlEncabez);
		oficio.setEvaluadorMostrar(JsfUtil.getLoggedUser().getPersona().getNombre());
		oficio.setSolicitud(generador.getSolicitud());
		oficio.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(generador.getFechaCreacion()));
		oficio.setNumeroInforme(informe.getNumero());
		oficio.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFechaModificacion()==null?informe.getFecha():informe.getFechaModificacion()));
		oficio.setAutoridad(generarDatosAutoridad ? usuarioAutoridad.getPersona().getNombre() : "");
		oficio.setCargoAutoridad(generarDatosAutoridad ? JsfUtil.obtenerCargoUsuario(usuarioAutoridad) : "");

		if (generador.getProyecto() != null) {
			oficio.setParaProyecto(", para el proyecto " + generador.getProyecto().getNombre().replace("&","&#38;"));
			oficio.setSector(generador.getProyecto().getTipoSector().getNombre());
		} else {
			oficio.setParaProyecto("");
			oficio.setSector(generador.getTipoSector().getNombre());
		}

		oficio.setParaActividad("");
		if (generador.getPoliticaDesechoActividad() != null) {
			String politicaDesechoActividad = generador.getPoliticaDesechoActividad().getDescripcion();
			if (politicaDesechoActividad.endsWith("."))
				politicaDesechoActividad = politicaDesechoActividad.substring(0, politicaDesechoActividad.length() - 1);
			oficio.setParaActividad(", para la actividad " + politicaDesechoActividad);
		}

		oficio.setEmpresaCargo("");
		oficio.setNombreEmpresaMostrar("");
		oficio.setCargoRepresentanteLegalMostrar("");
		oficio.setDeLaEmpresa("");
		if (nombreEmpresa != null) {
            oficio.setEmpresaCargo(nombreEmpresa);
            oficio.setNombreEmpresaMostrar("<strong>Empresa:&nbsp; </strong>" + nombreEmpresaMostrar + "");
            oficio.setCargoRepresentanteLegalMostrar("<strong>Cargo:</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                    + cargoRepresentanteLegalMostrar);
            oficio.setDeLaEmpresa("de la empresa " + nombreEmpresaMostrar);
            oficio.setRuc("<strong>RUC:</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + ruc);
            oficio.setOperador(nombreEmpresaMostrar);
            String representante = oficio.getRepresentante();
            oficio.setRepresentante(representante + " Representante Legal de la empresa " + nombreEmpresaMostrar);
        }
		if (emision) {
			oficio.setAccionOficioAsunto("Emisi&oacute;n");
			oficio.setAccionOficio("obtenci&oacute;n");
			oficio.setAccionOficioResultado("emite");
			oficio.setNumeroRegistroGenerador(generador.getCodigo());
			oficio.setNombreFichero("OficioEmision_" + getFileNameEscaped(oficio.getNumero()).replace("/", "-") + ".pdf");
			oficio.setNombreReporte("OficioEmision.pdf");
			oficio.setEmite("emisi&oacute;n");
		} else {
			oficio.setAccionOficioAsunto("Actualizaci&oacute;n");
			oficio.setAccionOficio("actualizaci&oacute;n");
			oficio.setAccionOficioResultado("actualiza");
			oficio.setNumeroRegistroGenerador(generador.getCodigo());
			oficio.setNombreFichero("OficioActualizacion_" + getFileNameEscaped(oficio.getNumero()).replace("/", "-") + ".pdf");
			oficio.setNombreReporte("OficioActualizacion.pdf");
			oficio.setEmite("actualizaci&oacute;n");
		}
		
		visualizarBorrador(generarDatosAutoridad, firma);

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteOficio.getHtmlPlantilla(),
				oficio.getNombreReporte(), true, oficio);
	
		Path path = Paths.get(informePdf.getAbsolutePath());
		oficio.setArchivoOficio(Files.readAllBytes(path));
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(oficio.getNombreFichero()));
		oficio.setOficioRealPath(archivoFinal.getAbsolutePath());
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficio.getArchivoOficio());
		file.close();
		oficio.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/" + oficio.getNombreFichero()));
	}

	public void visualizarOficioObservaciones(boolean setCurrentDate, boolean generarDatosAutoridad) throws Exception {
		if (plantillaReporteOficioObservacion == null){
			if(generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()){
				plantillaReporteOficioObservacion = informeOficioRGFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_RGD_REP);
			}else{
				plantillaReporteOficioObservacion = informeOficioRGFacade.getPlantillaReporte(TipoDocumentoSistema.TIPO_OFICIO_OBSERVACION_RGD);
			}
		}
			
		if (oficioObservaciones == null)
			oficioObservaciones = informeOficioRGFacade.getOficioObservacion(generador, JsfUtil.getCurrentTask()
					.getProcessInstanceId(), contadorBandejaTecnico);

		if (oficioObservaciones == null) {
			oficioObservaciones = new OficioObservacionRegistroGeneradorDesechos();
			oficioObservaciones.setContadorBandejaTecnico(contadorBandejaTecnico);
			oficioObservaciones.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
			oficioObservaciones.setNumero(informeOficioRGFacade.generarCodigoOficioObservacion(generador));

			oficioObservaciones.setRecomendaciones("");

			informeOficioRGFacade.guardarOficioObservacion(oficioObservaciones, generador, JsfUtil.getLoggedUser());

			borrarObservacionesOficios();
		} else {
			//para actualizar el codigo del oficio con la nueva área del proyecto
			String url = JsfUtil.getRequest().getRequestURI();
			Calendar cal = Calendar.getInstance();
			String anioActual = Integer.toString(cal.get(Calendar.YEAR));
			if(url.equals("/suia-iii/prevencion/registrogeneradordesechos/tecnicoResponsableOficioObservaciones.jsf")) {
				if(!oficioObservaciones.getNumero().contains(generador.getAreaResponsable().getAreaAbbreviation())
						|| !oficioObservaciones.getNumero().contains("-" + anioActual + "-")) {
					oficioObservaciones.setNumeroAnterior(oficioObservaciones.getNumero());
					oficioObservaciones.setNumero(informeOficioRGFacade.generarCodigoOficioObservacion(generador));
					
					informeOficioRGFacade.guardarOficioObservacion(oficioObservaciones);
				}
			}
		}
		
		if (emision) {			
			oficioObservaciones.setEmite("emisi&oacute;n");
		} else {
			
			oficioObservaciones.setEmite("actualizaci&oacute;n");
		}
		
		// elimino html generado del objeto 
		oficioObservaciones.setEstablecido(elimuinarParrafoHtml(oficioObservaciones.getEstablecido()));
		oficioObservaciones.setCumplimiento(elimuinarParrafoHtml(oficioObservaciones.getCumplimiento()));
		
		
		String anioCodigoOficioObservaciones=oficioObservaciones.getNumero().substring(6, 10);
		String anioActual=secuenciasFacade.getCurrentYear();
		if(!anioCodigoOficioObservaciones.equals(anioActual)){
			oficioObservaciones.setNumero(oficioObservaciones.getNumero().replace(anioCodigoOficioObservaciones, anioActual));
			crudServiceBean.saveOrUpdate(oficioObservaciones);
		}

		if (setCurrentDate)
			oficioObservaciones.setFecha(new Date());
		if(generarDatosAutoridad){
			// incluir informacion de la sede de la zonal en el documento
			if(generador.getProyecto() != null){
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, generador.getProyecto(), null);
				oficioObservaciones.setFechaMostrar(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficioObservaciones.getFecha()));
			}else{
				if(generador.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					oficioObservaciones.setFechaMostrar(generador.getAreaResponsable().getArea().getAreaSedeZonal()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficioObservaciones.getFecha()));
				else
					oficioObservaciones.setFechaMostrar(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(oficioObservaciones.getFecha()));
			}
		}else{
			oficioObservaciones.setFechaMostrar(JsfUtil.devuelveFechaEnLetrasSinHora(oficioObservaciones.getFecha()));
		}
		String articulo = "";
		if (generador.getUsuario().getPersona().getTipoTratos() != null) {
			articulo += JsfUtil.obtenerArticuloSegunTratamiento(generador.getUsuario().getPersona().getTipoTratos()
					.getNombre())
					+ " ";
		}

		String sujetoControl = "Sra./Sr.";
		String sujetoControlEncabez = "<strong>Sra./Sr. &nbsp; &nbsp;&nbsp; </strong>";

		if (generador.getUsuario().getPersona().getTipoTratos().getNombre() != null
				&& !generador.getUsuario().getPersona().getTipoTratos().getNombre().isEmpty()) {
			sujetoControl = generador.getUsuario().getPersona().getTipoTratos().getNombre();
			sujetoControlEncabez = "<strong>" + generador.getUsuario().getPersona().getTipoTratos().getNombre()
					+ " &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; </strong>";
		}
		
		sujetoControl = articulo + sujetoControl;
		
		if (generador.getUsuario().getPersona().getTitulo() != null
				&& !generador.getUsuario().getPersona().getTitulo().trim().isEmpty()) {
			sujetoControl += " / " + generador.getUsuario().getPersona().getTitulo() + ",";
		}

		if (generador.getUsuario().getPersona().getNombre() != null) {
			sujetoControl += " " + generador.getUsuario().getPersona().getNombre();
			sujetoControlEncabez += generador.getUsuario().getPersona().getNombre();
			
			oficioObservaciones.setOperador(generador.getUsuario().getPersona().getNombre());
			oficioObservaciones.setRepresentante(generador.getUsuario().getPersona().getNombre());
		}

		oficioObservaciones.setSujetoControl(sujetoControl.trim());
		oficioObservaciones.setSujetoControlEncabez(sujetoControlEncabez);
		oficioObservaciones.setSolicitud(generador.getSolicitud());
		oficioObservaciones.setFechaSolicitud(JsfUtil.devuelveFechaEnLetrasSinHora(generador.getFechaCreacion()));
		oficioObservaciones.setNumeroInforme(informe.getNumero());
		oficioObservaciones.setFechaInforme(JsfUtil.devuelveFechaEnLetrasSinHora(informe.getFechaModificacion()==null?informe.getFecha():informe.getFechaModificacion()));
		oficioObservaciones.setAutoridad(generarDatosAutoridad ? usuarioAutoridad.getPersona().getNombre() : "");
		oficioObservaciones.setCargoAutoridad(generarDatosAutoridad ? JsfUtil.obtenerCargoUsuario(usuarioAutoridad) : "");
		
		oficioObservaciones.setRuc(generador.getUsuario().getPersona().getPin());
		
		if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
			switch (generador.getPoliticaDesecho().getId()) {
			case PoliticaDesecho.POLITICA_CELULARES:
				oficioObservaciones.setDesecho("equipos celulares en desuso");
				oficioObservaciones.setAcuerdo("191 publicado en el R. O. 881 del 29 de enero de 2013");
				break;
			case PoliticaDesecho.POLITICA_NEUMATICOS:
				oficioObservaciones.setDesecho("neumáticos usados");
				oficioObservaciones.setAcuerdo("098 publicado en el R. O. 598 del 30 de septiembre de 2015 ");
				break;
			case PoliticaDesecho.POLITICA_PILAS:
				oficioObservaciones.setDesecho("pilas usadas");
				oficioObservaciones.setAcuerdo("022 publicado en el R. O. 943 del 29 de abril de 2013");
				break;
			case PoliticaDesecho.POLITICA_PLASTICOS:
				oficioObservaciones.setDesecho("desechos plásticos de uso agrícola");
				oficioObservaciones.setAcuerdo("042 publicado en el R. O. 498 del 30 de mayo de 2019");
				break;			
			}
		}

		if (generador.getProyecto() != null) {			
			oficioObservaciones.setParaProyecto(", para el proyecto " + generador.getProyecto().getNombre().replace("&","&#38;"));
		} else {
			oficioObservaciones.setParaProyecto("");
		}
		oficioObservaciones.setEmpresaCargo("");
		oficioObservaciones.setNombreEmpresaMostrar("");
		oficioObservaciones.setCargoRepresentanteLegalMostrar("");
		oficioObservaciones.setDeLaEmpresa("");
		if (nombreEmpresa != null) {
            oficioObservaciones.setEmpresaCargo(nombreEmpresaMostrar);
            oficioObservaciones.setNombreEmpresaMostrar("<strong>Empresa:&nbsp;&nbsp; </strong>"
                    + nombreEmpresaMostrar);
            oficioObservaciones
                    .setCargoRepresentanteLegalMostrar("<strong>Cargo:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong>"
                            + cargoRepresentanteLegalMostrar);
            oficioObservaciones.setDeLaEmpresa("de la empresa " + nombreEmpresaMostrar);
            oficioObservaciones.setRuc("<strong>RUC:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </strong>" + ruc);
            oficioObservaciones.setOperador(nombreEmpresaMostrar);
            
            String representante = oficioObservaciones.getRepresentante();
            oficioObservaciones.setRepresentante(representante + " Representante Legal de la empresa ");
        }

		oficioObservaciones.setNombreFichero("OficioObservacion_" + getFileNameEscaped(oficioObservaciones.getNumero()).replace("/", "-") + ".pdf");
		oficioObservaciones.setNombreReporte("OficioObservacion.pdf");
		oficioObservaciones.setObservaciones(getObservacionesGeneradorTabla());

		File informePdf = UtilGenerarInforme.generarFichero(plantillaReporteOficioObservacion.getHtmlPlantilla(),
				oficioObservaciones.getNombreReporte(), true, oficioObservaciones);

		Path path = Paths.get(informePdf.getAbsolutePath());
		oficioObservaciones.setArchivoOficio(Files.readAllBytes(path));
		oficioObservaciones.setOficioRealPath(informePdf.getAbsolutePath());
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(oficioObservaciones.getNombreFichero()));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(oficioObservaciones.getArchivoOficio());
		file.close();
		oficioObservaciones.setOficioPath(JsfUtil.devolverContexto("/reportesHtml/"
				+ oficioObservaciones.getNombreFichero()));
	}

	public void visualizarBorrador(boolean generarDatosAutoridad, boolean firma) throws Exception {
	
		if (plantillaReporteBorrador == null)
			if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
				plantillaReporteBorrador = informeOficioRGFacade
						.getPlantillaReporte(TipoDocumentoSistema.TIPO_BORRADOR_RGD_RESPONSABILIDAD_EXTENDIDA_SI);
			}else{
				plantillaReporteBorrador = informeOficioRGFacade
						.getPlantillaReporte(TipoDocumentoSistema.TIPO_BORRADOR_RGD);
			}

		if(generarDatosAutoridad){
			// incluir informacion de la sede de la zonal en el documento
			if(generador.getProyecto() != null){
				ProyectoSedeZonalUbicacionController proyectoSedeZonalUbicacionController = JsfUtil.getBean(ProyectoSedeZonalUbicacionController.class);
				String nombreCanton = proyectoSedeZonalUbicacionController.obtenerSedeUbicacionProyecto(usuarioAutoridad, "PROYECTOSUIAIII", null, generador.getProyecto(), null);
				generador.setFecha(nombreCanton+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			}else{
				if(generador.getAreaResponsable().getTipoArea().getSiglas().equals(Constantes.SIGLAS_TIPO_AREA_OT))
					generador.setFecha(generador.getAreaResponsable().getArea().getAreaSedeZonal()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
				else
					generador.setFecha(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre()+", "+JsfUtil.devuelveFechaEnLetrasSinHora1(new Date()));
			}
		}else{
			generador.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
		}
		generador.setNombreFichero("BorradorRegistroGenerador_" + generador.getSolicitud().replace("Ñ", "N") + ".pdf");
		generador.setNombreReporte("BorradorRegistroGenerador.pdf");
		generador.setEmpresa(cargoEmpresa == null ? "N/A" : generador.getUsuario().getPersona().getNombre().replace("&", "&#38;"));
		generador.setCampoCargoResponsable(cargoEmpresa == null ? "Sujeto de control" : "Cargo o puesto en la empresa");
		generador.setCargo(cargoEmpresa == null ? generador.getUsuario().getPersona().getNombre() : cargoEmpresa[0]);
		generador.setDireccion(getUbicacionGeografica());
		generador.setCampoSeLeOtorga(cargoEmpresa == null ? "al operador" : "a la empresa");
		generador.setResponsable(cargoEmpresa == null ? generador.getUsuario().getPersona().getNombre().replace("&", "&#38;")
				: cargoEmpresa[1].replace("&", "&#38;"));
		generador.setNumeroRegistro(generador.getCodigo()!=null ? generador.getCodigo().replace("Ñ", "&Ntilde;"):generador.getCodigo());
		generador.setTablaDesechos(getGeneradorDesechosTabla());
		generador.setTablaInstalaciones(getGeneradorPuntosRecuperacionTabla());
		generador.setNormativaLegal(getNormativaLegal());
		generador.setAutoridad(generarDatosAutoridad ? usuarioAutoridad.getPersona().getNombre() : "");
		generador.setCargoAutoridad(generarDatosAutoridad ? JsfUtil.obtenerCargoUsuario(usuarioAutoridad) : "");

		File reportePdf = UtilGenerarInforme.generarFichero(plantillaReporteBorrador.getHtmlPlantilla(),
				generador.getNombreReporte(), true, generador);
		Path path = Paths.get(reportePdf.getAbsolutePath());
		generador.setArchivoGenerador(Files.readAllBytes(path));
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(generador.getNombreFichero()));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(generador.getArchivoGenerador());
		file.close();
		generador.setRegistroPath(JsfUtil.devolverContexto("/reportesHtml/" + generador.getNombreFichero()));
	}
	
	public String elimuinarParrafoHtml(String texto){
		if (texto == null){
			return null;
		}
		texto = texto.replace("<p>\r\n", "");
		texto = texto.replace("</p>\r\n", ""); 
		return texto;
	}

	public void guardarInforme() {
		if (informe.getRecomendaciones() == null || informe.getRecomendaciones().isEmpty())
			informe.setRecomendacionesEncabezado("");
		else
			informe.setRecomendacionesEncabezado("<strong>6. RECOMENDACIONES</strong>");

		informe.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		informeOficioRGFacade.guardarInforme(informe, generador, JsfUtil.getLoggedUser());
		
		InformeTecnicoRegistroGeneradorDesechos informeActualizado = informeOficioRGFacade.getInforme(generador, JsfUtil.getCurrentTask().getProcessInstanceId(),
				contadorBandejaTecnico);
		informe.setFechaModificacion(informeActualizado.getFechaModificacion());
		informe.setEvaluador(informeActualizado.getEvaluador());
	}

	public void guardarOficio() {
		oficio.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		// Generar codigo generador desecho
		try {
			if (informe == null)
				inicializarInformeTecnicoAsociado();
			if (informe.getCumple() && generador.getCodigo() == null) {
				generador.setCodigo(registroGeneradorDesechosFacade.generarCodigoRegistroGeneradorDesechos(JsfUtil.getBean(DocumentoRGBean.class).getGenerador()
								.getAreaResponsable()));
			}
			generador.setFecha(JsfUtil.devuelveFechaEnLetrasSinHora(new Date()));
			registroGeneradorDesechosFacade.guardar(generador);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		informeOficioRGFacade.guardarOficioEmision(oficio, generador, JsfUtil.getLoggedUser());
	}

	public void guardarOficioObservaciones() {
		oficioObservaciones.setProcessInstanceId(JsfUtil.getCurrentProcessInstanceId());
		informeOficioRGFacade.guardarOficioObservacion(oficioObservaciones, generador, JsfUtil.getLoggedUser());
	}

	public Documento guardarInformeDocumento() throws ServiceException, CmisAlfrescoException {
		Documento documento = new Documento();
		documento.setNombre("InformeTecnico-" + informe.getNumero() + ".pdf");
		documento.setContenidoDocumento(informe.getArchivoInforme());
		documento.setNombreTabla(informe.getClass().getSimpleName());
		documento.setIdTable(informe.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		if (generador.getProyecto() != null) {
			documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					informe.getTipoDocumento().getTipoDocumentoSistema(), null);
		} else {
			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					informe.getTipoDocumento().getTipoDocumentoSistema(), null);
		}
		return documento;
	}

	public Documento guardarOficioDocumento() throws ServiceException, CmisAlfrescoException {

		Documento documento = new Documento();
		String accion = emision ? "Emision" : "Actualizacion";
		documento.setNombre("Oficio" + accion + "-" + oficio.getNumero() + ".pdf");
		documento.setContenidoDocumento(oficio.getArchivoOficio());
		documento.setNombreTabla(oficio.getClass().getSimpleName());
		documento.setIdTable(oficio.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		if (generador.getProyecto() != null) {
			documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					oficio.getTipoDocumento().getTipoDocumentoSistema(), null);
		} else {
			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					oficio.getTipoDocumento().getTipoDocumentoSistema(), null);
		}
		return documento;
	}

	public Documento guardarOficioObservacionesDocumento() throws ServiceException, CmisAlfrescoException {
		Documento documento = new Documento();
		documento.setNombre("OficioObservacion-" + oficioObservaciones.getNumero() + ".pdf");
		documento.setContenidoDocumento(oficioObservaciones.getArchivoOficio());
		documento.setNombreTabla(oficioObservaciones.getClass().getSimpleName());
		documento.setIdTable(oficioObservaciones.getId());
		documento.setMime("application/pdf");
		documento.setExtesion(".pdf");
		if (generador.getProyecto() != null) {

			documento = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					oficioObservaciones.getTipoDocumento().getTipoDocumentoSistema(), null);
		} else {

			documento = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
					Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documento,
					oficioObservaciones.getTipoDocumento().getTipoDocumentoSistema(), null);
		}
		return documento;
	}

	public StreamedContent getStream(String name, byte[] fileContent) throws Exception {
		DefaultStreamedContent content = new DefaultStreamedContent();
		if (fileContent != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(fileContent), "application/octet-stream");
			content.setName(name);
		}
		return content;
	}

	public void inicializarInformeTecnicoAsociado() {
		if(informe==null)

			informe = informeOficioRGFacade.getInforme(generador, JsfUtil.getCurrentTask().getProcessInstanceId(),
				contadorBandejaTecnico);
	}

	public OficioEmisionRegistroGeneradorDesechos inicializarOficioEmisionActualizacionAsociado() {
		if(oficio==null)
		oficio = informeOficioRGFacade.getOficioEmisionActualizacion(generador, JsfUtil.getCurrentTask()
				.getProcessInstanceId(), contadorBandejaTecnico);
		return oficio;
	}

	public OficioObservacionRegistroGeneradorDesechos inicializarOficioObservacionesAsociado() {
		if(oficioObservaciones==null)
		oficioObservaciones = informeOficioRGFacade.getOficioObservacion(generador, JsfUtil.getCurrentTask()
				.getProcessInstanceId(), contadorBandejaTecnico);
		return oficioObservaciones;
	}

	private String getProponente(String label) throws ServiceException {
		if (label == null)
			label = "";
		Organizacion organizacion = organizacionFacade.buscarPorPersona(generador.getUsuario().getPersona(), generador
				.getUsuario().getNombre());
		if (organizacion != null) {
			return organizacion.getNombre();
		}
		return label;
	}

	private String getObservacionesGeneradorTabla() throws ServiceException {
		if (observaciones == null) {
			List<ObservacionesFormularios> observacionesGenerador = observacionesFacade.listarPorIdClaseNombreClase(
					generador.getId(), GeneradorDesechosPeligrosos.class.getSimpleName());
			observaciones = new LinkedHashMap<String, List<ObservacionesFormularios>>();
			if (observacionesGenerador != null && !observacionesGenerador.isEmpty()) {
				for (ObservacionesFormularios observacion : observacionesGenerador) {
					if (!observacion.isObservacionCorregida()) {
						if (!observaciones.containsKey(observacion.getSeccionFormulario()))
							observaciones.put(observacion.getSeccionFormulario(),
									new ArrayList<ObservacionesFormularios>());
						observaciones.get(observacion.getSeccionFormulario()).add(observacion);
					}
				}
			}
		}

		StringBuilder stringBuilder = new StringBuilder();
		if (observaciones.isEmpty())
			return "";
		Object[] secciones = observaciones.keySet().toArray();
		for (Object seccion : secciones) {
			stringBuilder.append("<b>Secci&oacute;n:</b> " + seccion + "");
			List<ObservacionesFormularios> observacionesSeccion = observaciones.get(seccion);
			for (ObservacionesFormularios observacion : observacionesSeccion) {
				stringBuilder.append("<p>&nbsp;&nbsp;&nbsp;&nbsp;" + observacion.getCampo() + ": "
						+ observacion.getDescripcion() + "</p>");
			}
		}
		return stringBuilder.toString();
	}

	private String getGeneradorDesechosTabla() throws ServiceException {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<table cellpadding='5' style='border:0; margin: 0;'>");
		stringBuilder.append("<tr>");
		stringBuilder
				.append("<td style='text-align: center; width:230px;'><strong>C&oacute;digo de desecho</strong></td>");
		stringBuilder.append("<td style='text-align: center;'><strong>Desecho Peligroso y/o Especial</strong></td>");
		stringBuilder.append("</tr>");
		for (GeneradorDesechosDesechoPeligroso desecho : generador.getGeneradorDesechosDesechoPeligrosos()) {
			stringBuilder.append("<tr>");
			stringBuilder.append("<td style='text-align: center;'>" + desecho.getDesechoPeligroso().getClave()
					+ "</td>");
			String descripcion=desecho.getDesechoPeligroso().getDescripcion();
			descripcion=descripcion.replace("<", "&#60;");
			descripcion=descripcion.replace(">", "&#62;");
			stringBuilder.append("<td>" + descripcion + "</td>");
			stringBuilder.append("</tr>");
		}
		stringBuilder.append("</table>");
		return stringBuilder.toString();
	}

	private String getGeneradorPuntosRecuperacionTabla() throws ServiceException {
		StringBuilder stringBuilder = new StringBuilder();
		for (PuntoRecuperacion puntoRecuperacion : generador.getPuntosRecuperacion()) {
			stringBuilder.append(puntoRecuperacion.getNombre() + ", localizado en ");
			stringBuilder.append(puntoRecuperacion.getUbicacionesGeografica().getUbicacionesGeografica()
					.getUbicacionesGeografica()
					+ ", ");
			stringBuilder.append(puntoRecuperacion.getUbicacionesGeografica().getUbicacionesGeografica() + ", ");
			stringBuilder.append(puntoRecuperacion.getUbicacionesGeografica() + ", ");
			stringBuilder.append(puntoRecuperacion.getDireccion() + ".<br/>");
		}
		try {
			return stringBuilder.toString().substring(0, stringBuilder.toString().length() - 5);
		} catch (Exception e) {
			return "";
		}
	}

	private String[] getEmpresa() throws ServiceException {
		Organizacion organizacion = organizacionFacade.buscarPorPersona(generador.getUsuario().getPersona(), generador
				.getUsuario().getNombre());
		if (organizacion != null) {
			String[] cargoEmpresa = new String[2];
			if(organizacion.getCargoRepresentante()==null)
				cargoEmpresa[0]=organizacion.getPersona().getPosicion();
			else				
				cargoEmpresa[0] = organizacion.getCargoRepresentante();
			
			cargoEmpresa[1] = organizacion.getNombre();
			ubicacion = ubicacionGeograficaFacade.buscarPorId(organizacion.getIdUbicacionGeografica());
			ruc = organizacion.getRuc();
			return cargoEmpresa;
		}
		return null;
	}

	private String getUbicacionGeografica() throws ServiceException {
		if (ubicacion == null)
			ubicacion = ubicacionGeograficaFacade.buscarPorId(generador.getUsuario().getPersona()
					.getIdUbicacionGeografica());
		if (ubicacion != null) {
			return ubicacion.getNombre() + ", " + ubicacion.getUbicacionesGeografica().getNombre() + ", "
					+ ubicacion.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		}
		return "";
	}

	private String getNormativaLegal() {
		if (plantillaReporteBorradorNormativa == null) {
			TipoDocumentoSistema tipoDocumento = TipoDocumentoSistema.TIPO_BORRADOR_NORMATIVA_DESCHOS_PROVINCIALES_RGD;
			if (generador.getResponsabilidadExtendida() != null && generador.getResponsabilidadExtendida()) {
				switch (generador.getPoliticaDesecho().getId()) {
				case PoliticaDesecho.POLITICA_CELULARES:
					tipoDocumento = TipoDocumentoSistema.TIPO_BORRADOR_NORMATIVA_CELULARES_DESUSO_RGD;
					break;
				case PoliticaDesecho.POLITICA_NEUMATICOS:
					tipoDocumento = TipoDocumentoSistema.TIPO_BORRADOR_NORMATIVA_NEUMATICOS_USADOS_RGD;
					break;
				case PoliticaDesecho.POLITICA_PILAS:
					tipoDocumento = TipoDocumentoSistema.TIPO_BORRADOR_NORMATIVA_PILAS_USADAS_RGD;
					break;
				case PoliticaDesecho.POLITICA_PLASTICOS:
					tipoDocumento = TipoDocumentoSistema.TIPO_BORRADOR_NORMATIVA_PLASTICOS_AGRICOLAS_RGD;
					break;
				}
			}
			plantillaReporteBorradorNormativa = informeOficioRGFacade.getPlantillaReporte(tipoDocumento);
		}
		return plantillaReporteBorradorNormativa.getHtmlPlantilla();
	}

	public boolean validarExistenciaObservaciones() {
		boolean puedeContinuar = false;
		try {
			List<ObservacionesFormularios> observacionesUsuarioAutenticado = observacionesFacade
					.listarPorIdClaseNombreClaseUsuarioNoCorregidas(generador.getId(),
							"GeneradorDesechosPeligrososRevision", JsfUtil.getLoggedUser().getId());
			if (observacionesUsuarioAutenticado == null)
				puedeContinuar = true;
			else if (observacionesUsuarioAutenticado.isEmpty())
				puedeContinuar = true;
			else
				puedeContinuar = false;

		} catch (ServiceException exception) {
			LOG.error(
					"Ocurrió un error al recuperar las observaciones del usuario logueado en el proceso Registro de Generador",
					exception);
		}
		return puedeContinuar;
	}

	public boolean validarExistenciaObservacionesProponente() {
		boolean existenObservacionesProponente = true;
		try {
			List<ObservacionesFormularios> observacionesGenerador = observacionesFacade
					.listarPorIdClaseNombreClaseNoCorregidas(generador.getId(),
							GeneradorDesechosPeligrosos.class.getSimpleName());
			if (observacionesGenerador == null || observacionesGenerador.isEmpty())
				existenObservacionesProponente = false;

		} catch (ServiceException exception) {
			LOG.error(
					"Ocurrió un error al recuperar las observaciones dirigidas al proponente en el proceso Registro de Generador",
					exception);
		}
		return existenObservacionesProponente;
	}

	public void borrarObservacionesInformeTecnico() throws Exception {
		List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClase(
				generador.getId(), "GeneradorDesechosPeligrososRevision", "Informe técnico");
		if (observaciones != null) {
			crudServiceBean.delete(observaciones);
		}
	}

	public void borrarObservacionesOficios() throws Exception {
		List<ObservacionesFormularios> observaciones = observacionesFacade.listarPorIdClaseNombreClase(
				generador.getId(), "GeneradorDesechosPeligrososRevision", "Oficio de pronunciamiento");
		if (observaciones != null) {
			crudServiceBean.delete(observaciones);
		}
	}

	private String getFileNameEscaped(String file) {
		String result = file.replaceAll("Ñ", "N");
		result = result.replaceAll("Á", "A");
		result = result.replaceAll("É", "E");
		result = result.replaceAll("Í", "I");
		result = result.replaceAll("Ó", "O");
		result = result.replaceAll("Ú", "U");
		return result;
	}
	
	public void guardarOficioEmision() {
		try {
			if (oficio != null)
				informeOficioRGFacade.guardarOficioEmision(oficio);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String firmarInforme() {
		try {
			Documento documentoInforme = informe.getDocumento();
			
			if(documentoInforme != null && documentoInforme.getId() != null) {
				String documentOffice = documentosFacade.direccionDescarga(documentoInforme);
				return DigitalSign.sign(documentOffice, JsfUtil.getLoggedUser().getNombre());
			}
		} catch (Exception exception) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
		return "";
	}
	
	public StreamedContent descargar() throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			Documento documentoInforme = informe.getDocumento();
			
			if (documentoInforme != null && documentoInforme.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documentoInforme.getIdAlfresco(), documentoInforme.getFechaCreacion());
			} 
			
			if (documentoInforme != null && documentoInforme.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documentoInforme.getNombre());
				
				descargado = true;
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void uploadListenerDocumentos(FileUploadEvent event) {
		if (descargado== true) {
			byte[] contenidoDocumento = event.getFile().getContents();

			documentoManual=new Documento();
			documentoManual.setContenidoDocumento(contenidoDocumento);
			documentoManual.setNombre(event.getFile().getFileName());
			documentoManual.setExtesion(".pdf");		
			documentoManual.setMime("application/pdf");
			documentoManual.setNombreTabla(informe.getClass().getSimpleName());
			documentoManual.setIdTable(informe.getId());

			subido = true;
			JsfUtil.addMessageInfo("Documento subido exitosamente");
		} else{
			JsfUtil.addMessageError("No ha descargado el documento para la firma");
		}
	}
	
	public Boolean guardarDocumentoManual() throws ServiceException, CmisAlfrescoException {
		if(subido) {
			if (generador.getProyecto() != null) {
				documentoManual = documentosFacade.guardarDocumentoAlfresco(generador.getProyecto().getCodigo(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documentoManual,
						informe.getTipoDocumento().getTipoDocumentoSistema(), null);
			} else {
				documentoManual = documentosFacade.guardarDocumentoAlfrescoSinProyecto(generador.getSolicitud(),
						Constantes.NOMBRE_PROCESO_GENERADOR_DESECHOS, JsfUtil.getCurrentProcessInstanceId(), documentoManual,
						informe.getTipoDocumento().getTipoDocumentoSistema(), null);
			}
		}
		return subido;
	}
}
