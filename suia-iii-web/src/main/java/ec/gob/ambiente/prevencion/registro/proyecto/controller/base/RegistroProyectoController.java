package ec.gob.ambiente.prevencion.registro.proyecto.controller.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.CatalogoActividadesBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.DesechoPeligrosoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.RegistroProyectoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.VerProyectoController;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.catalogocategorias.facade.CatalogoCategoriasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.comun.bean.AdicionarUbicacionesBean;
import ec.gob.ambiente.suia.comun.bean.CargarCoordenadasBean;
//import ec.gob.ambiente.suia.comun.bean.DocumentoComunidadPPSBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.Categoria;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormaProyecto;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoFaseDesecho;
import ec.gob.ambiente.suia.domain.ProyectoSustanciaQuimica;
import ec.gob.ambiente.suia.domain.ProyectoUbicacionGeografica;
import ec.gob.ambiente.suia.domain.TipoEstudio;
import ec.gob.ambiente.suia.domain.TipoPoblacion;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.classes.CoordinatesWrapper;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.PagoConfiguracionesUtil;

public abstract class RegistroProyectoController implements Serializable {

	private static final long serialVersionUID = 817890213262978411L;

	public static final String PROYECTO_MODIFICAR = "proyectoModificar";

	private static final Logger LOGGER = Logger.getLogger(RegistroProyectoController.class);

	private static LinkedList<String> CATEGORIAS_INICIO_TRANSPORTE_SUSTANCIAS_QUIMICAS;
	private static LinkedList<String> CATEGORIAS_INICIO_GESTION_DESECHOS_PELIGROSOS;
	private static LinkedList<String> CATEGORIAS_INICIO_GENERA_DESECHOS;
	private static LinkedList<String> CATEGORIAS_INICIO_UTILIZA_SUSTANCIAS_QUIMICAS;

	@Setter
	@ManagedProperty(value = "#{registroProyectoBean}")
	protected RegistroProyectoBean registroProyectoBean;

	@Setter
	@ManagedProperty(value = "#{catalogoActividadesBean}")
	protected CatalogoActividadesBean catalogoActividadesBean;
	
	@EJB
	private AutorizacionCatalogoFacade autorizacionCatalogoFacade;

	@Setter
	@ManagedProperty(value = "#{cargarCoordenadasBean}")
	protected CargarCoordenadasBean cargarCoordenadasBean;

	@Setter
	@ManagedProperty(value = "#{adicionarUbicacionesBean}")
	protected AdicionarUbicacionesBean adicionarUbicacionesBean;

	@Setter
	@ManagedProperty(value = "#{desechoPeligrosoBean}")
	protected DesechoPeligrosoBean desechoPeligrosoBean;	

	@EJB
	protected ProyectoLicenciamientoAmbientalFacade proyectoFacade;

	@EJB
	protected UbicacionGeograficaFacade ubicacionGeograficaFacade;

	@EJB
	protected Categoria1Facade categoria1Facade;

	@EJB
	protected ProcesoFacade procesoFacade;

	@EJB
	private CertificadoInterseccionFacade certificadoInterseccionService;

	@EJB
	protected DocumentosFacade documentosFacade;

	private TipoPoblacion tipoPoblacionSimple;

	@EJB
	protected CatalogoCategoriasFacade catalogoCategoriasFacade;
	/**** pago 20160303 ****/
	@Inject
	PagoConfiguracionesUtil pagoUtil = new PagoConfiguracionesUtil();
	/**** pago 20160303 ****/

	List<Contacto> listContacto = new ArrayList<Contacto>();
	@EJB
	private UsuarioFacade usuarioFacade;

	@EJB
	private ContactoFacade contactoFacade;

	@PostConstruct
	private void init() {

		this.CATEGORIAS_INICIO_TRANSPORTE_SUSTANCIAS_QUIMICAS = (LinkedList<String>) this.catalogoCategoriasFacade
				.obtenerIdActividadesPorManejoDesechoRequerido("TRANSPORTE_SUSTANCIAS_QUIMICAS");

		this.CATEGORIAS_INICIO_GESTION_DESECHOS_PELIGROSOS = (LinkedList<String>) this.catalogoCategoriasFacade
				.obtenerIdActividadesPorManejoDesechoRequerido("GESTION_DESECHOS_PELIGROSOS");

		this.CATEGORIAS_INICIO_GENERA_DESECHOS = (LinkedList<String>) this.catalogoCategoriasFacade
				.obtenerIdActividadesPorManejoDesechoRequerido("GENERA_DESECHOS");

		this.CATEGORIAS_INICIO_UTILIZA_SUSTANCIAS_QUIMICAS = (LinkedList<String>) this.catalogoCategoriasFacade
				.obtenerIdActividadesPorManejoDesechoRequerido("UTILIZA_SUSTANCIAS_QUIMICAS");

		if (!isProyectoModificar())
			return;

		registroProyectoBean.setEdicion(true);
		registroProyectoBean.setProyecto(JsfUtil.getBean(VerProyectoBean.class).getProyecto());

		catalogoActividadesBean.markAsSelected(registroProyectoBean.getProyecto().getCatalogoCategoria()
				.getCatalogoCategoriaPublico(), registroProyectoBean.getProyecto().getCatalogoCategoria());

		List<UbicacionesGeografica> ubicaciones = new ArrayList<UbicacionesGeografica>();
		Iterator<ProyectoUbicacionGeografica> iteratorUG = registroProyectoBean.getProyecto()
				.getProyectoUbicacionesGeograficas().iterator();
		while (iteratorUG.hasNext()) {
			ProyectoUbicacionGeografica proyectoUbicacionGeografica = iteratorUG.next();
			ubicaciones.add(proyectoUbicacionGeografica.getUbicacionesGeografica());
		}
		adicionarUbicacionesBean.setUbicacionesSeleccionadas(ubicaciones);

		Iterator<FormaProyecto> iteratorFP = registroProyectoBean.getProyecto().getFormasProyectos().iterator();
		cargarCoordenadasBean.setCoordinatesWrappers(new ArrayList<CoordinatesWrapper>());
		while (iteratorFP.hasNext()) {
			FormaProyecto formaProyecto = iteratorFP.next();
			CoordinatesWrapper coordinatesWrapper = new CoordinatesWrapper();
			coordinatesWrapper.setTipoForma(formaProyecto.getTipoForma());
			coordinatesWrapper.setCoordenadas(formaProyecto.getCoordenadas());
			cargarCoordenadasBean.getCoordinatesWrappers().add(coordinatesWrapper);
		}

		if (registroProyectoBean.getProyecto().getGestionaDesechosPeligrosos() != null
				&& registroProyectoBean.getProyecto().getGestionaDesechosPeligrosos()) {
			Iterator<ProyectoFaseDesecho> iteratorFD = registroProyectoBean.getProyecto().getFasesDesechos().iterator();
			while (iteratorFD.hasNext()) {
				registroProyectoBean.getFasesDesechosSeleccionadas().add(iteratorFD.next().getFaseDesecho());
			}
		}

		if (registroProyectoBean.getProyecto().getGeneraDesechos() != null
				&& registroProyectoBean.getProyecto().getGeneraDesechos()) {
			Iterator<ProyectoDesechoPeligroso> iteratorDP = registroProyectoBean.getProyecto()
					.getProyectoDesechoPeligrosos().iterator();
			while (iteratorDP.hasNext()) {
				desechoPeligrosoBean.getDesechosSeleccionados().add(iteratorDP.next().getDesechoPeligroso());
			}
		}

		if (registroProyectoBean.getProyecto().getTransporteSustanciasQuimicasPeligrosos() != null
				&& registroProyectoBean.getProyecto().getTransporteSustanciasQuimicasPeligrosos()) {
			Iterator<ProyectoSustanciaQuimica> iteratorSQ = registroProyectoBean.getProyecto()
					.getProyectoSustanciasQuimicas().iterator();
			while (iteratorSQ.hasNext()) {
				registroProyectoBean.getSustanciasQuimicasSeleccionadas().add(iteratorSQ.next().getSustanciaQuimica());
			}
		}
	}

	public abstract void guardarProyecto() throws CmisAlfrescoException;

	public abstract void adicionarValidaciones(List<String> errors);

	public abstract List<String> validar();

	public abstract List<String> validarNegocio();

	public void validateRegistroProyecto(FacesContext context, UIComponent validate, Object value) {
		List<String> validateResults = validar();

		if (validateResults == null)
			validateResults = new ArrayList<String>();

		adicionarValidaciones(validateResults);

		if (registroProyectoBean.isMostrarUbicacionGeografica()
				&& adicionarUbicacionesBean.getUbicacionesSeleccionadas().isEmpty())
			validateResults.add("No se han definido ubicaciones geográficas para este proyecto.");
		if (cargarCoordenadasBean.getCoordinatesWrappers().isEmpty() && !catalogoActividadesBean.isCategoriaI())
			validateResults.add("No se han definido las coordenadas para este proyecto.");
		if (cargarCoordenadasBean.getUploadedFile()==null && !catalogoActividadesBean.isCategoriaI() && registroProyectoBean.isEdicion())
			validateResults.add("Estimado proponente se encuentra modificando la información, se necesita que vuelva adjuntar el documento de coordenadas.");
		if (!validateResults.isEmpty()) {
			List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();
			for (String string : validateResults) {
				facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null));
			}

			throw new ValidatorException(facesMessages);
		}
	}

	public void validateSeleccionActividad(FacesContext context, UIComponent validate, Object value) {
		List<String> validateResults = new ArrayList<String>();

		if (catalogoActividadesBean.getActividadSeleccionada() == null)
			validateResults.add("Actividad es requerido. Especifique la actividad a desarrollar en el proyecto.");

		if (!validateResults.isEmpty()) {
			List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();
			for (String string : validateResults) {
				facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null));
			}
			throw new ValidatorException(facesMessages);
		}
	}

	public void validateDesechosSeleccionados(FacesContext context, UIComponent validate, Object value) {
		/*
		 * List<String> validateResults = new ArrayList<String>();
		 * 
		 * if (registroProyectoBean.getProyecto().getGeneraDesechos() != null &&
		 * registroProyectoBean.getProyecto().getGeneraDesechos() &&
		 * desechoPeligrosoBean.getDesechosSeleccionados().isEmpty())
		 * validateResults.add(
		 * "No se han definido los desechos especiales o peligrosos para este proyecto."
		 * );
		 * 
		 * if (!validateResults.isEmpty()) { List<FacesMessage> facesMessages =
		 * new ArrayList<FacesMessage>(); for (String string : validateResults)
		 * { facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR,
		 * string, null)); } throw new ValidatorException(facesMessages); }
		 */
	}

	public void validateNumeroResolucion(FacesContext context, UIComponent validate, Object value) {
		List<String> validateResults = new ArrayList<String>();
		if (isEmisionInclusionAmbiental()) {
			if (!proyectoFacade.validarNumeroResolucion((String) value)) {
				validateResults
						.add("El Número de resolución '"
								+ (String) value
								+ "' no se encuentra registrado, por favor verifique y si el problema persiste, comuníquese con Mesa de Ayuda.");
			}
		}

		if (!validateResults.isEmpty()) {
			List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();
			for (String string : validateResults) {
				facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null));
			}
			throw new ValidatorException(facesMessages);
		}
	}

	public String guardar() throws CmisAlfrescoException {
		if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada()!=null){
			if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.03")||JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.05")){
				if(registroProyectoBean.getProyecto().getArea()<100.01){
					JsfUtil.addMessageError("La sumatoria del hectareaje de su concesión es < a 100ha, lo que corresponde a un Registro Ambiental, por favor seleccionar la opción correcta.");
					return null;
				}
			}
			if(JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.04")||JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.06") ||JsfUtil.getBean(CatalogoActividadesBean.class).getActividadSistemaSeleccionada().getCodigo().equals("11.03.01")){
				if(registroProyectoBean.getProyecto().getArea()>100.01){
					JsfUtil.addMessageError("La sumatoria del hectareaje de su concesión es > a 100ha, lo que corresponde a una Licencia Ambiental, por favor seleccionar la opción correcta.");
					return null;
				}
			}
		}
		
		List<String> erroresNegocio = validarNegocio();
		if (erroresNegocio != null && !erroresNegocio.isEmpty()) {
			JsfUtil.addMessageError(erroresNegocio);
			return null;
		}

		if (catalogoActividadesBean.isCategoriaI()) {
			registroProyectoBean.getProyecto().setTipoEstudio(null);
			registroProyectoBean.getProyecto().setTipoEmisionInclusionAmbiental(null);
			registroProyectoBean.getProyecto().setNumeroDeResolucion(null);

			registroProyectoBean.getProyecto().setGestionaDesechosPeligrosos(false);
			registroProyectoBean.getProyecto().setTransporteSustanciasQuimicasPeligrosos(false);
			registroProyectoBean.getProyecto().setUtilizaSustaciasQuimicas(null);
			registroProyectoBean.getProyecto().setTipoUsoSustancia(null);
//			registroProyectoBean.getProyecto().setProyectoInterseccionExitosa(true);
			registroProyectoBean.getFasesDesechosSeleccionadas().clear();
			registroProyectoBean.getSustanciasQuimicasSeleccionadas().clear();

//			cargarCoordenadasBean.getCoordinatesWrappers().clear();
			desechoPeligrosoBean.getDesechosSeleccionados().clear();
		}

		/*
		 * if (registroProyectoBean.getProyecto().getResiduosSolidos() != null
		 * && !registroProyectoBean.getProyecto().getResiduosSolidos())
		 * registroProyectoBean.getProyecto().setOficioViabilidad(null);
		 */

		registroProyectoBean.getProyecto().setUsuario(JsfUtil.getLoggedUser());
		if (registroProyectoBean.getProyecto().getCuentaCoordenadas()==null){
			registroProyectoBean.getProyecto().setCuentaCoordenadas(1);
		}else{
			registroProyectoBean.getProyecto().setCuentaCoordenadas(registroProyectoBean.getProyecto().getCuentaCoordenadas()+1);
		}
		if (!(registroProyectoBean.getProyecto().getAreaResponsable()==null)){
		if (registroProyectoBean.getProyecto().getAreaResponsable().getTipoArea().getId()!=3){
			
			if (registroProyectoBean.getProyecto().getCuentaCoordenadas().equals(5)){
				NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
				List<Usuario> usuarios_contactoautamb= new ArrayList<Usuario>();
				List<Usuario> usuarios_prevencion= new ArrayList<Usuario>();
				List<Usuario> usuarios_General= new ArrayList<Usuario>();
				List<Usuario> usuarios_control= new ArrayList<Usuario>();
				
				if (!registroProyectoBean.getProyecto().getAreaResponsable().getAreaAbbreviation().equals("DNPCA")){	
					usuarios_contactoautamb=usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL", registroProyectoBean.getProyecto().getAreaResponsable());
				}
				usuarios_prevencion=usuarioFacade.buscarUsuarioPorRol("AUTORIDAD AMBIENTAL MAE");			
				usuarios_General= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES GENERAL");
				usuarios_control= usuarioFacade.buscarUsuarioPorRol("NOTIFICACIONES CONTROL");
				
				try {
					try {
						for (int i = 0; i < usuarios_contactoautamb.size(); i++) {
							listContacto = contactoFacade
									.buscarPorPersona(usuarios_contactoautamb
											.get(i).getPersona());
							for (int j = 0; j < listContacto.size(); j++) {
								if (listContacto.get(j).getFormasContacto().getId()
										.equals(5)) {								
									mail_a.sendEmailCuentaCoordenadas(listContacto.get(j).getValor(), 
											"Registro del proyecto","Este correo fue enviado usando JavaMail",
											registroProyectoBean.getProyecto()
											.getNombre(),
									registroProyectoBean.getProyecto()
											.getCodigo(),JsfUtil.getLoggedUser().getPersona().getNombre(),registroProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion(), usuarios_contactoautamb.get(i), new Usuario());										
									Thread.sleep(2000);
								}
							}
						}
						listContacto = new ArrayList<Contacto>();
						for (int i = 0; i < usuarios_General.size(); i++) {
							listContacto = contactoFacade
									.buscarPorPersona(usuarios_General
											.get(i).getPersona());
							for (int j = 0; j < listContacto.size(); j++) {
								if (listContacto.get(j).getFormasContacto().getId()
										.equals(5)) {
									mail_a.sendEmailCuentaCoordenadas(listContacto.get(j).getValor(), 
											"Registro del proyecto","Este correo fue enviado usando JavaMail",
											registroProyectoBean.getProyecto()
											.getNombre(),
									registroProyectoBean.getProyecto()
											.getCodigo(),JsfUtil.getLoggedUser().getPersona().getNombre(),registroProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion(), usuarios_General.get(i), new Usuario());
									Thread.sleep(2000);
								}
							}
						}					
						listContacto = new ArrayList<Contacto>();
						for (int i = 0; i < usuarios_prevencion.size(); i++) {
							listContacto = contactoFacade
									.buscarPorPersona(usuarios_prevencion
											.get(i).getPersona());
							for (int j = 0; j < listContacto.size(); j++) {
								if (listContacto.get(j).getFormasContacto().getId()
										.equals(5)) {
									mail_a.sendEmailCuentaCoordenadas(listContacto.get(j).getValor(), 
											"Registro del proyecto","Este correo fue enviado usando JavaMail",
											registroProyectoBean.getProyecto()
											.getNombre(),
									registroProyectoBean.getProyecto()
											.getCodigo(),JsfUtil.getLoggedUser().getPersona().getNombre(),registroProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion(), usuarios_prevencion.get(i), new Usuario());
									Thread.sleep(2000);
								}
							}
						}
						listContacto = new ArrayList<Contacto>();
						for (int i = 0; i < usuarios_control.size(); i++) {
							listContacto = contactoFacade
									.buscarPorPersona(usuarios_control
											.get(i).getPersona());
							for (int j = 0; j < listContacto.size(); j++) {
								if (listContacto.get(j).getFormasContacto().getId()
										.equals(5)) {
									mail_a.sendEmailCuentaCoordenadas(listContacto.get(j).getValor(), 
											"Registro del proyecto","Este correo fue enviado usando JavaMail",
											registroProyectoBean.getProyecto()
											.getNombre(),
									registroProyectoBean.getProyecto()
											.getCodigo(),JsfUtil.getLoggedUser().getPersona().getNombre(),registroProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion(), usuarios_control.get(i), new Usuario());
									Thread.sleep(2000);
								}
							}
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// }
					// }
					// }
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		}

		if (!registroProyectoBean.isMostrarUbicacionGeografica()) {
			registroProyectoBean.getProyecto().setTipoUbicacion(null);
		}

		if (registroProyectoBean.getProyecto().getTipoEstudio() != null
				&& registroProyectoBean.getProyecto().getTipoEstudio().getId() != TipoEstudio.ESTUDIO_EX_POST)
			registroProyectoBean.getProyecto().setFechaInicioOperaciones(null);

		if (registroProyectoBean.getProyecto().getTipoEstudio() != null
				&& registroProyectoBean.getProyecto().getTipoEstudio().getId() != TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL) {
			registroProyectoBean.getProyecto().setTipoEmisionInclusionAmbiental(null);
			registroProyectoBean.getProyecto().setNumeroDeResolucion(null);
		}

		
		//		getSector = 2 Mineria 3 Otros Sectores
		// si es minero debe aparecer la caja de texto y validad el ingreso menos en el 869 y 870
		if(catalogoActividadesBean.getTipoSector().getId()==2 && (catalogoActividadesBean.getActividadSistemaSeleccionada().getCategoria().getId()==3 || catalogoActividadesBean.getActividadSistemaSeleccionada().getCategoria().getId()==4))
		{
			if (!(catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(869) 
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(870)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(1820)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(1822)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(1850)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(1852)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(867)
					|| catalogoActividadesBean.getActividadSistemaSeleccionada().getId().equals(865))){
				if(registroProyectoBean.getProyecto().isConcesionesMinerasMultiples())
				{
					if(!validar().isEmpty())
					{						
						JsfUtil.addMessageError(validar());						
						return null;
					}
						
				}else{					
					if (registroProyectoBean.getProyecto().getCodigoMinero()== null || registroProyectoBean.getProyecto().getCodigoMinero().equals("") ){			
						JsfUtil.addMessageError("En el campo 'Código minero' es obligatorio");
						return null;
					}
					Integer valor=registroProyectoBean.getProyecto().getCodigoMinero().length();
					if (valor<=3){
						JsfUtil.addMessageError("En el campo 'Código minero' debe ingresar un mínimo de 4 caracteres");
						return null;
					}
				}				
			}
		}
//		if (!catalogoActividadesBean.isCategoriaI()) {
			registroProyectoBean.getProyecto().setEstadoMapa(false);
//		}		
		guardarProyecto();

		String messagesInfo = JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA + ";;";
		/*if(registroProyectoBean.getProyecto().getId()!=null && !validarActividad()){
		try {
			if (registroProyectoBean.getProyecto().getCatalogoCategoria().getCategoria().getId().intValue() == Categoria.CATEGORIA_I) {
				
				 List<Usuario> userSubsecretario = usuarioFacade
							.buscarUsuarioPorRol("SUBSECRETARIO DE CALIDAD AMBIENTAL");
                if (userSubsecretario.size()==1){
                	validarInterseccionProyectoCapas();
                	if (!viabilidarCertificadoAmbiental()){
	                	String resultadocert= categoria1Facade.crearCertificadoRegistroAmbiental(registroProyectoBean.getProyecto(),JsfUtil.getLoggedUser().getNombre(),1);
	    				messagesInfo += "Se ha generado el <b>Certificado Ambiental</b>, por favor descárguelo haciendo clic en el botón inferior <b>Certificado Ambiental</b>.";
	    				if (resultadocert.equals("")){
	    					JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado Ambiental. Contacte con Mesa de Ayuda.");
	    					if (registroProyectoBean.getProyecto().getCatalogoCategoria().getEstrategico()==true){
	    						LOGGER.error("No existe SUBSECRETARIO DE CALIDAD AMBIENTAL");
	    					}else{
	    						LOGGER.error("No existe AUTORIDAD AMBIENTAL en el área "+ registroProyectoBean.getProyecto().getAreaResponsable().getAreaName());
	    					}
	    				}
                	}
                }else{
                	JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado Ambiental. Contacte con Mesa de Ayuda.");
                	if (registroProyectoBean.getProyecto().getCatalogoCategoria().getEstrategico()==true){
						LOGGER.error("No existe SUBSECRETARIO DE CALIDAD AMBIENTAL");
					}else{
						LOGGER.error("No existe AUTORIDAD AMBIENTAL en el área "+ registroProyectoBean.getProyecto().getAreaResponsable().getAreaName());
					}						        			
                }
                
				
			} else {
				validarInterseccionProyectoCapas();
			}
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado de Intersección. Contacte con Mesa de Ayuda.");
			LOGGER.error("Ocurre algún problema con servicio web del Certificado de Intersección.", e);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado Ambiental. Contacte con Mesa de Ayuda.");
			LOGGER.error(e);
		}

		JsfUtil.addMessageInfo(JsfUtil.getStringAsHtmlUL(messagesInfo, false));
		JsfUtil.getBean(VerProyectoController.class).verProyecto(registroProyectoBean.getProyecto().getId(), true);
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");
		}else{
			if (catalogoActividadesBean.getActividadSistemaSeleccionada().getCategoria().getId().intValue() == Categoria.CATEGORIA_I) {
				return "";
			}
			else
			{
				adicionarUbicacionesBean.setUbicacionesSeleccionadas(new ArrayList<UbicacionesGeografica>());
				adicionarUbicacionesBean.setParroquia(null);
				return "";
			}
		}*/
		try {
			validarInterseccionProyectoCapas();
		} catch (ServiceException e) {
			JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado de Intersección. Contacte con Mesa de Ayuda.");
			LOGGER.error("Ocurre algún problema con servicio web del Certificado de Intersección.", e);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error generando el Certificado Ambiental. Contacte con Mesa de Ayuda.");
			LOGGER.error(e);
		}
		JsfUtil.addMessageInfo(JsfUtil.getStringAsHtmlUL(messagesInfo, false));
		JsfUtil.getBean(VerProyectoController.class).verProyecto(registroProyectoBean.getProyecto().getId(), true);
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");
	}

	@EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;
	
	public boolean viabilidarCertificadoAmbiental() {
		 return requisitosPreviosFacade.requiereRequisitosPrevios(registroProyectoBean.getProyecto()); 
	}
	
	/*public File CertificadoAmbielaI(Integer marcaAgua) {
		
		File archivoAnexoTemporal = null;
		String archivoCertificadoAmbientalTemporal = null;
		File archivoTemporal = null;
		String reporteFinal = null;
		if (registroProyectoBean.getProyecto().getAreaResponsable().getTipoArea().getId()
				.equals(3)) {						
			if (registroProyectoBean.getProyecto().getAreaResponsable().getTipoEnteAcreditado()
					.equals("MUNICIPIO")) {
				reporteFinal = "anexo_coordenadas_certificado_ambiental_categoria_I_Completo_entes_municipio";
			} else {
				reporteFinal = "anexo_coordenadas_certificado_ambiental_categoria_I_Completo_entes";
			}
		} else {
			reporteFinal = "anexo_coordenadas_certificado_ambiental_categoria_I_Completo";
		}		
		
		try {
			archivoAnexoTemporal = ReporteCertificadoAmbientalCategoriaI
					.crearAnexoCoordenadasCertificadoAmbientalCategoriaI(
							"Anexo coordenadas",categoria1Facade
							.cargarDatosAnexosCompletoCertificadoAmbiental(registroProyectoBean.getProyecto()),
							reporteFinal, "Anexo coordenadas", registroProyectoBean.getProyecto()
									.getAreaResponsable(), 1);
			archivoCertificadoAmbientalTemporal= categoria1Facade.crearCertificadoRegistroAmbiental(registroProyectoBean.getProyecto(),JsfUtil.getLoggedUser().getNombre(),1);
			File certificadoAmbiental= new File(archivoCertificadoAmbientalTemporal);
			List<File> listaFiles = new ArrayList<File>();
			listaFiles.add(certificadoAmbiental);
			listaFiles.add(archivoAnexoTemporal);
			archivoTemporal = UtilFichaMineria.unirPdf(listaFiles,
					"Certificado_Ambiental");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return archivoTemporal;		
	}*/
	
	public boolean validarActividad(){
		boolean x=false;
		AreasAutorizadasCatalogo actividad=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(catalogoActividadesBean.getActividadSistemaSeleccionada(), adicionarUbicacionesBean.getUbicacionesSeleccionadas().get(0).getEnteAcreditado());
		if(actividad!=null)
		{
			if(actividad.getActividadBloqueada())
			{
				x=true;
			}
		}
		return x;
	}
	
	private void validarInterseccionProyectoCapas() throws Exception {
		certificadoInterseccionService.generarInterseccionProyectoCapas(registroProyectoBean.getProyecto(), null);
	}

	public String cancelar() {
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/registro.jsf");
	}

	public String test() throws CmisAlfrescoException {
		JsfUtil.getBean(VerProyectoController.class).verProyecto(7101, true);
		return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/verProyecto.jsf");
	}

	public boolean isEstudioExAnte() {
		return registroProyectoBean.getProyecto().getTipoEstudio() != null
				&& registroProyectoBean.getProyecto().getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EX_ANTE;
	}

	public boolean isEstudioExPost() {
		return registroProyectoBean.getProyecto().getTipoEstudio() != null
				&& registroProyectoBean.getProyecto().getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EX_POST;
	}

	public boolean isEmisionInclusionAmbiental() {
		return registroProyectoBean.getProyecto().getTipoEstudio() != null
				&& registroProyectoBean.getProyecto().getTipoEstudio().getId() == TipoEstudio.ESTUDIO_EMISION_INCLUSION_AMBIENTAL;
	}

	protected boolean isCatalogoCategoriaIdPresente(int[] values) {
		try {
			int id = catalogoActividadesBean.getActividadSistemaSeleccionada().getFase().getId();
			return Arrays.binarySearch(values, id) >= 0;
		} catch (Exception e) {
			return false;
		}
	}

	
	protected boolean isCatalogoCategoriaCodigoInicia(String[] values) {
		/**** pago 20160303 ****/
		if(catalogoActividadesBean.getActividadSistemaSeleccionada() != null) {
			if(catalogoActividadesBean.getActividadSistemaSeleccionada().getCategoria().getId()==2) {
				Float costoT = pagoUtil.validaPago(catalogoActividadesBean.getLoginBean().getUsuario(), 
						catalogoActividadesBean.getActividadSistemaSeleccionada());
				catalogoActividadesBean.getActividadSistemaSeleccionada().getTipoLicenciamiento().setCosto(costoT);
			}
		}
		/**** pago 20160303 ****/		
		try {
			String code = catalogoActividadesBean.getActividadSistemaSeleccionada().getCodigo();
			for (String string : values) {
				if (code.compareTo(string)==0)
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	protected boolean isCatalogoCategoriaCodigoIniciaProyecto(String[] values) {
				
		try {
			String code = catalogoActividadesBean.getActividadSistemaSeleccionada().getCodigo();
			for (String string : values) {
				if (code.compareTo(string)==0)
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected boolean isCatalogoCategoriaCodigoInicia(LinkedList<String> values) {
		try {
			String code = catalogoActividadesBean.getActividadSistemaSeleccionada().getCodigo();
			for (String string : values) {
				if (code.compareTo(string)==0)
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	protected String obtenerSubsectorCatalogoCategoriaSistema() {
		if (catalogoActividadesBean.getActividadSistemaSeleccionada() != null
				&& catalogoActividadesBean.getActividadSistemaSeleccionada().getTipoSubsector() != null
				&& catalogoActividadesBean.getActividadSistemaSeleccionada().getTipoSubsector().getCodigo() != null)
			return catalogoActividadesBean.getActividadSistemaSeleccionada().getTipoSubsector().getCodigo();
		return "";
	}

	public static void setProyectoModificar() {
		JsfUtil.cargarObjetoSession(PROYECTO_MODIFICAR, true);
	}

	public static boolean isProyectoModificar() {
		Object object = JsfUtil.devolverEliminarObjetoSession(PROYECTO_MODIFICAR);
		if (object != null)
			return true;
		return false;
	}

	public StreamedContent getStream(Documento documento) throws Exception {
		DefaultStreamedContent content = null;
		if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
			content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
			content.setName(documento.getNombre());

		}
		return content;
	}

	public TipoPoblacion getTipoPoblacionSimple(Integer id) {
		if (tipoPoblacionSimple != null && !tipoPoblacionSimple.getId().equals(id))
			tipoPoblacionSimple = null;
		return tipoPoblacionSimple == null ? tipoPoblacionSimple = new TipoPoblacion(id) : tipoPoblacionSimple;
	}

	/**
	 * Walter tipo de zona
	 * hidrocarburos = getTipoSector().getId()==1 -> continental - no contonetal - mixto
	 * @return
	 */
	public List<TipoPoblacion> getTiposPoblaciones() {
		if(catalogoActividadesBean.getActividadSeleccionada().getTipoSector().getId()==1){
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_URBANA)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_URBANA));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_RURAL)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_RURAL));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MARITIMA)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MARITIMA));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA));
		}else{
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_CONTINENTAL)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_CONTINENTAL));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_NO_CONTINENTAL)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_NO_CONTINENTAL));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MIXTO)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_MIXTO));
			if (registroProyectoBean.getTiposPoblaciones().contains(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA)))
				registroProyectoBean.getTiposPoblaciones().remove(getTipoPoblacionSimple(TipoPoblacion.TIPO_POBLACION_PERIFERICA));
		}
		return registroProyectoBean.getTiposPoblaciones();
	}

	public boolean isMarcarTransporteSustanciasQuimicas() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_INICIO_TRANSPORTE_SUSTANCIAS_QUIMICAS);
		if (result)
			registroProyectoBean.getProyecto().setTransporteSustanciasQuimicasPeligrosos(result);
		return result;
	}

	public boolean isMarcarGestionDesechosPeligrosos() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_INICIO_GESTION_DESECHOS_PELIGROSOS);
		if (result)
			registroProyectoBean.getProyecto().setGestionaDesechosPeligrosos(result);
		return result;
	}

	public boolean isMarcarGeneraDesechos() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_INICIO_GENERA_DESECHOS);
		if (result)
			registroProyectoBean.getProyecto().setGeneraDesechos(result);
		return result;
	}

	public boolean isMarcarUtilizaSustaciasQuimicas() {
		boolean result = isCatalogoCategoriaCodigoInicia(CATEGORIAS_INICIO_UTILIZA_SUSTANCIAS_QUIMICAS);
		if (result)
			registroProyectoBean.getProyecto().setUtilizaSustaciasQuimicas(result);
		return result;
	}
}
