package ec.gob.ambiente.rcoa.registroGeneradorDesechos.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.component.wizard.Wizard;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.AdicionarDesechosPeligrososRcoaBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.DatosOperadorRgdBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.bean.PuntosRecuperacionRgdREPBean;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.ActividadDesechoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.CoordenadaRgdCoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DocumentosRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.FormaPuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PoliticaDesechoRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PoliticaRcoaDesechoaPeligrosoFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoGeneracionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.PuntoRecuperacionRgdRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.CoordenadaRgdCoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.FormaPuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PoliticaDesechoRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoGeneracionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.PuntoRecuperacionRgdRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
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

@ManagedBean
@ViewScoped
public class InformacionRegistroGeneradorREPController {

	private static final Logger LOG = Logger.getLogger(InformacionRegistroGeneradorREPController.class);

	@EJB
	private ProcesoFacade procesoFacade;
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private RegistroGeneradorDesechosRcoaFacade registroGeneradorDesechosRcoaFacade;
	@EJB
	private UbicacionGeograficaFacade ubicacionGeograficaFacade;
	@EJB
	private PuntoGeneracionRgdRcoaFacade puntoGeneracionRgdRcoaFacade;
	@EJB
	private ActividadDesechoFacade actividadDesechoFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private PoliticaDesechoRgdRcoaFacade politicaDesechoRgdRcoaFacade;
	@EJB
	private PoliticaRcoaDesechoaPeligrosoFacade politicaRcoaDesechoaPeligrosoFacade;
	@EJB
	private DocumentosRgdRcoaFacade documentosRgdRcoaFacade;
	@EJB
	private CoordenadaRgdCoaFacade coordenadaRgdCoaFacade;
	@EJB
	private FormaPuntoRecuperacionRgdRcoaFacade formaPuntoRecuperacionRgdRcoaFacade;
	@EJB
	private PuntoRecuperacionRgdRcoaFacade puntoRecuperacionRgdRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private AreaFacade areaFacade;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{datosOperadorRgdBean}")
	private DatosOperadorRgdBean datosOperadorRgdBean;

	@ManagedProperty(value = "#{wizardBean}")
	@Getter
	@Setter
	private WizardBean wizardBean;

	@Getter
	@Setter
	private List<String> listaDesechosPorActividad;

	@Getter
	@Setter
	private boolean aceptarCondiciones = false;

	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;

	// variables de flujo
	@Getter
	private String tramite;

	private Map<String, Object> variables;

	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@ManagedProperty(value = "#{adicionarDesechosPeligrosRcoaBean}")
	@Getter
	@Setter
	private AdicionarDesechosPeligrososRcoaBean adicionarDesechosPeligrosRcoaBean;

	@Getter
	@Setter
	private boolean existeObservaciones = false;

	@Getter
	@Setter
	private Integer idUbicacionGeografica;

	@Getter
	@Setter
	private UbicacionesGeografica ubicacionGeografica;

	@Setter
	@Getter
	private List<UbicacionesGeografica> listaUbicaciones;

	@Getter
	@Setter
	private RegistroGeneradorDesechosRcoa registroGeneradorDesechos;

	@Getter
	@Setter
	private boolean desechoModificado = false;

	@Getter
	@Setter
	private String nombreEmpresaSolicitante, representanteLegal, rucEmpresa, actividadPrincipal, segundaActividad,
			terceraActividad, direccion, telefono, correo, celular, cedulaRepresentanteLegal, tipoOperador = "N";

	@Getter
	@Setter
	private List<DesechosRegistroGeneradorRcoa> desechosRegistroLista;

	@Getter
	@Setter
	private Integer idDesecho;

	@Getter
	@Setter
	private List<PuntoGeneracionRgdRcoa> listaOrigenGeneracion;

	@Getter
	private List<PoliticaDesechoRgdRcoa> politicasDesechos;

	@Getter
	private List<DesechoPeligroso> listaDesechosPeligrosos;

	@Getter
	@Setter
	private String codigoCiiu;

	@Getter
	@Setter
	private boolean guardado, habilitarEdicion;

	@Getter
	@Setter
	private boolean enviar, existenCoordenadas;

	byte[] bytePlantilla;

	@Getter
	@Setter
	private String style;

	@Getter
	@Setter
	private Boolean esLamparas;
	
	@Getter
	@Setter
	private boolean medicamento = false;


	@PostConstruct
	public void init() {
		try {
			medicamento=true;
			
			Long idProceso = (Long) (JsfUtil.devolverObjetoSession("idProceso"));
			if (idProceso != null && idProceso > 0) {
				bandejaTareasBean.setProcessId(idProceso);
			}
			// se establece como parametro tipo para conocer si se desea visualizar el
			// proceso
			// String tipo = null;
			Map<String, String> paramSesion = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			if (paramSesion.containsKey("tipo")) {
				tramite = paramSesion.get("tipo");
			}
			if (tramite == null || tramite.isEmpty()) {
				variables = procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(),
						bandejaTareasBean.getProcessId());
				tramite = (String) variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);
			}
			habilitarEdicion = true;
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			StringBuffer requestURL = request.getRequestURL();
			if (requestURL.toString().contains("VerREP"))
				habilitarEdicion = false;
			registroGeneradorDesechos = registroGeneradorDesechosRcoaFacade.buscarRGDPorCodigo(tramite);
			if (registroGeneradorDesechos == null) {
				registroGeneradorDesechos = new RegistroGeneradorDesechosRcoa();
				registroGeneradorDesechos.setCodigo(tramite);
				registroGeneradorDesechos.setEsResponsabilidadExtendida(true);
				registroGeneradorDesechos.setEstado(true);
				registroGeneradorDesechos.setUsuario(loginBean.getUsuario());
				registroGeneradorDesechos.setFinalizado(false);
				registroGeneradorDesechosRcoaFacade.saveREP(registroGeneradorDesechos, loginBean.getUsuario());
			} else {
				puntosDeRecuperacion();
			}
			datosOperadorRgdBean.setTitulo("Responsable o representante de la empresa");
			if (registroGeneradorDesechos != null && registroGeneradorDesechos.getUsuario() != null) {
				datosOperadorRgdBean.buscarDatosOperador(registroGeneradorDesechos.getUsuario());
			}
			JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).inicializarRegREP(registroGeneradorDesechos);
			// datos del operador
			if (registroGeneradorDesechos.getUsuario().getNombre().length() == 10) {
				nombreEmpresaSolicitante = registroGeneradorDesechos.getUsuario().getPersona().getNombre();
				representanteLegal = registroGeneradorDesechos.getUsuario().getPersona().getNombre();
				cedulaRepresentanteLegal = registroGeneradorDesechos.getUsuario().getNombre();
				rucEmpresa = registroGeneradorDesechos.getUsuario().getNombre();
				idUbicacionGeografica = registroGeneradorDesechos.getUsuario().getPersona().getIdUbicacionGeografica();
				tipoOperador = "N";
				for (Contacto contacto : registroGeneradorDesechos.getUsuario().getPersona().getContactos()) {
					switch (contacto.getFormasContacto().getId()) {
					case FormasContacto.DIRECCION:
						direccion = contacto.getValor();
						break;
					case FormasContacto.TELEFONO:
						if (contacto.getValor() != null && !contacto.getValor().isEmpty())
							telefono = contacto.getValor();
						break;
					case FormasContacto.CELULAR:
						if (telefono != null && !telefono.isEmpty())
							telefono = contacto.getValor();
						celular = contacto.getValor();
						break;
					case FormasContacto.EMAIL:
						correo = contacto.getValor();
						break;
					default:
						break;
					}
				}
			} else {
				tipoOperador = "J";
				Organizacion organizacion = new Organizacion();
				organizacion = organizacionFacade.buscarPorRuc(registroGeneradorDesechos.getUsuario().getNombre());

				if (organizacion != null && organizacion.getId() != null) {
					nombreEmpresaSolicitante = organizacion.getNombre();
					representanteLegal = organizacion.getPersona().getNombre();
					cedulaRepresentanteLegal = organizacion.getPersona().getPin();
					rucEmpresa = organizacion.getRuc();
					idUbicacionGeografica = organizacion.getIdUbicacionGeografica();

					List<Contacto> contactos = contactoFacade.buscarPorOrganizacion(organizacion);

					for (Contacto contacto : contactos) {
						switch (contacto.getFormasContacto().getId()) {
						case FormasContacto.DIRECCION:
							direccion = contacto.getValor();
							break;
						case FormasContacto.TELEFONO:
							if (contacto.getValor() != null && !contacto.getValor().isEmpty())
								telefono = contacto.getValor();
							break;
						case FormasContacto.CELULAR:
							if (telefono != null && !telefono.isEmpty())
								telefono = contacto.getValor();
							celular = contacto.getValor();
							break;
						case FormasContacto.EMAIL:
							correo = contacto.getValor();
							break;
						default:
							break;
						}
					}
				} else {
					nombreEmpresaSolicitante = registroGeneradorDesechos.getUsuario().getPersona().getNombre();
					representanteLegal = registroGeneradorDesechos.getUsuario().getPersona().getNombre();
					cedulaRepresentanteLegal = registroGeneradorDesechos.getUsuario().getNombre();
					rucEmpresa = registroGeneradorDesechos.getUsuario().getNombre();
					idUbicacionGeografica = registroGeneradorDesechos.getUsuario().getPersona()
							.getIdUbicacionGeografica();

					for (Contacto contacto : registroGeneradorDesechos.getUsuario().getPersona().getContactos()) {
						switch (contacto.getFormasContacto().getId()) {
						case FormasContacto.DIRECCION:
							direccion = contacto.getValor();
							break;
						case FormasContacto.TELEFONO:
							if (contacto.getValor() != null && !contacto.getValor().isEmpty())
								telefono = contacto.getValor();
							break;
						case FormasContacto.CELULAR:
							if (telefono != null && !telefono.isEmpty())
								telefono = contacto.getValor();
							celular = contacto.getValor();
							break;
						case FormasContacto.EMAIL:
							correo = contacto.getValor();
							break;
						default:
							break;
						}
					}
				}
			}
			// ubicacion del operado
			ubicacionGeografica = new UbicacionesGeografica();
			ubicacionGeografica = ubicacionGeograficaFacade.buscarPorId(idUbicacionGeografica);

			listaUbicaciones = new ArrayList<UbicacionesGeografica>();
			if (ubicacionGeografica != null) {
				listaUbicaciones.add(ubicacionGeografica);
			}

			politicasDesechos = politicaDesechoRgdRcoaFacade.findAll();
			enviar = true;
			guardado = false;
			if (registroGeneradorDesechos.getPoliticaDesechoRgdRcoa() != null
					&& registroGeneradorDesechos.getPoliticaDesechoRgdRcoa().getId() != null)
				guardado = true;
			style = "wizard-custom-btn-next";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void puntosDeRecuperacion() {
		List<PuntoRecuperacionRgdRcoa> puntosRecuperacion = puntoRecuperacionRgdRcoaFacade
				.buscarPorRgd(registroGeneradorDesechos.getId());
		for (PuntoRecuperacionRgdRcoa punto : puntosRecuperacion) {
			String coordenadaString = "";
			if (punto.getFormasPuntoRecuperacionRgdRcoa() != null
					&& !punto.getFormasPuntoRecuperacionRgdRcoa().isEmpty()) {
				for (CoordenadaRgdCoa coordenada : punto.getFormasPuntoRecuperacionRgdRcoa().get(0).getCoordenadas()) {
					coordenadaString += (coordenadaString == "")
							? coordenada.getX().toString() + " " + coordenada.getY().toString()
							: "," + coordenada.getX().toString() + " " + coordenada.getY().toString();
				}
				punto.setCoordenadasIngresadas(coordenadaString);
			}
		}
		JsfUtil.getBean(PuntosRecuperacionRgdREPBean.class).setPuntosRecuperacion(puntosRecuperacion);
	}

	public void listarDesechosPorPolitica() {
		if (registroGeneradorDesechos.getPoliticaDesechoRgdRcoa() != null
				&& registroGeneradorDesechos.getPoliticaDesechoRgdRcoa().getId() != null) {
			listaDesechosPeligrosos = politicaRcoaDesechoaPeligrosoFacade
					.buscarPorIdPolitica(registroGeneradorDesechos.getPoliticaDesechoRgdRcoa().getId());
		}

		if (registroGeneradorDesechos.getPoliticaDesechoRgdRcoa().getNombre()
				.equals("Lámparas de descarga y/o LED en desuso")
				|| registroGeneradorDesechos.getPoliticaDesechoRgdRcoa().getNombre()
						.equals("Desechos plásticos de uso agrícola")) {
			esLamparas = true;
			for (DesechoPeligroso desecho : listaDesechosPeligrosos) {
				desecho.setEsLampara(true);
				
			}
		} else {
			esLamparas = false;
			for (DesechoPeligroso desecho : listaDesechosPeligrosos) {
			//	System.out.println("VALOR MEDICAMENTO======> "+desecho.getEsMedicamento());
				if (desecho.getClave().equals("C.21.03")) {
					desecho.setOrigen("Percha");
					desecho.setEsMedicamento(false);
					medicamento=false;
					
				} else if (desecho.getClave().equals("Q.86.08")) {
					desecho.setOrigen("Domicilio");
					desecho.setEsMedicamento(false);
					medicamento=false;
				
				}
				
			}
			
		}
	}

	public void ocultarFormulario() {
		wizardBean.setCurrentStep("paso1");
		Wizard wizard = (Wizard) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:wizardGenerador");
		wizard.setStep("paso1");
		RequestContext.getCurrentInstance().update("form:wizardGenerador");
	}

	public String btnAtras() {
		String currentStep = wizardBean.getCurrentStep();
		if (currentStep != null && currentStep.equals("paso2")) {
			guardado = true;
		}
		style = "wizard-custom-btn-back";

		return null;
	}

	public String btnSiguiente() throws CmisAlfrescoException {
		String currentStep = wizardBean.getCurrentStep();
		if ((currentStep == null || currentStep.equals("paso1")) && habilitarEdicion) {
			guardarPaso1();
		}
		if (currentStep != null && currentStep.equals("paso3")) {
			style = "wizard-custom-btn-next";
			RequestContext.getCurrentInstance().update("btnGuardar");
		}
		guardado = false;
		return null;
	}

	public void guardar() {
		try {
			String currentStep = wizardBean.getCurrentStep();
			if (currentStep == null || currentStep.equals("paso1")) {
				guardarPaso1();
				guardado = true;
			} else if (currentStep.equals("paso2")) {
				guardarPaso2_();
			} else if (currentStep.equals("paso3")) {
				guardarPaso3();
			}
		} catch (Exception ae) {
			ae.printStackTrace();
			guardado = false;
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}
	}

	private void guardarPaso1() {

		boolean cambioPolitica = false;
		if (registroGeneradorDesechos == null || registroGeneradorDesechos.getId() == null) {
			registroGeneradorDesechos = new RegistroGeneradorDesechosRcoa();
			registroGeneradorDesechos.setEstado(true);
			registroGeneradorDesechos.setUsuario(loginBean.getUsuario());
			registroGeneradorDesechos.setFinalizado(false);
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
		} else {
			RegistroGeneradorDesechosRcoa registroAnterior = registroGeneradorDesechosRcoaFacade
					.findById(registroGeneradorDesechos.getId());

			if (registroAnterior.getPoliticaDesechoRgdRcoa() != null && !registroAnterior.getPoliticaDesechoRgdRcoa()
					.equals(registroGeneradorDesechos.getPoliticaDesechoRgdRcoa())) {
				cambioPolitica = true;
			}
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());
		}
		listarDesechosPorPolitica();
		if (JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().size() > 0) {
//			DesechoPeligroso desechosOriginal = JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().get(0).getDesechoPeligroso();
			List<DesechosRegistroGeneradorRcoa> listaDesechosOriginales = new ArrayList<>();
			listaDesechosOriginales
					.addAll(JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados());

			if (listaDesechosPeligrosos != null && listaDesechosPeligrosos.size() > 0) {
				if (listaDesechosOriginales != null && !listaDesechosOriginales.isEmpty()) {
					if (cambioPolitica) {
						for (DesechosRegistroGeneradorRcoa desechoRGD : listaDesechosOriginales) {
							desechoRGD.setEstado(false);
							if (desechoRGD.getId() != null) {
								desechosRegistroGeneradorRcoaFacade.save(desechoRGD, loginBean.getUsuario());
							}
							JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()
									.remove(desechoRGD);
						}

						for (DesechoPeligroso desecho : listaDesechosPeligrosos) {
//							if(desecho.getId().intValue() != desechosOriginal.getId().intValue()){
//								// elimino el desecho seleccionado
//								JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().get(0).setEstado(false);
//								// actualizo si ya fue guardado sino no
//								if(JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().get(0).getId() != null)
//									desechosRegistroGeneradorRcoaFacade.save(JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().get(0), loginBean.getUsuario());
//								JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().remove(0);
							// guardo el nuevo desecho
							DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
							desechoRcoa.setDesechoPeligroso(desecho);
							desechoRcoa.setAgregadoPorOperador(false);
							JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()
									.add(desechoRcoa);
//							}
						}
					} else {
						if (esLamparas) {
							List<DesechoPeligroso> listaDesechosAux = new ArrayList<>();
							listaDesechosAux.addAll(listaDesechosPeligrosos);

							for (DesechoPeligroso desecho : listaDesechosPeligrosos) {
								for (DesechosRegistroGeneradorRcoa des : listaDesechosOriginales) {
									if (desecho.equals(des.getDesechoPeligroso())) {
										listaDesechosAux.remove(desecho);
									}
								}
							}

							if (!listaDesechosAux.isEmpty()) {
								for (DesechoPeligroso desecho : listaDesechosAux) {
									DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
									desechoRcoa.setDesechoPeligroso(desecho);
									desechoRcoa.setAgregadoPorOperador(false);
									JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class)
											.getDesechosRcoaSeleccionados().add(desechoRcoa);
								}
							}

							for (DesechosRegistroGeneradorRcoa des : JsfUtil
									.getBean(AdicionarDesechosPeligrososRcoaBean.class)
									.getDesechosRcoaSeleccionados()) {
								if (des.getId() != null) {
									des.setSeleccionarDesecho(true);
									des.getDesechoPeligroso().setEsLampara(true);
								}
							}
						}
					}
				}
			}
		} else {
			if (listaDesechosPeligrosos != null && listaDesechosPeligrosos.size() > 0) {
				for (DesechoPeligroso desecho : listaDesechosPeligrosos) {
					DesechosRegistroGeneradorRcoa desechoRcoa = new DesechosRegistroGeneradorRcoa();
					desechoRcoa.setDesechoPeligroso(desecho);
					desechoRcoa.setAgregadoPorOperador(false);
					JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados()
							.add(desechoRcoa);
				}
			}
		}
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public void guardarPaso2_() {
		try {
			List<PuntoRecuperacionRgdRcoa> listaEliminar = JsfUtil.getBean(PuntosRecuperacionRgdREPBean.class)
					.getPuntosRecuperacionEliminar();

			if (listaEliminar != null && !listaEliminar.isEmpty()) {
				puntoRecuperacionRgdRcoaFacade.eliminarPuntoRecuperacion(listaEliminar, loginBean.getUsuario());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (JsfUtil.getBean(PuntosRecuperacionRgdREPBean.class).getPuntosRecuperacion().isEmpty()) {
			JsfUtil.addMessageError(
					"Debe ingresar la ubicación de los puntos de generación dentro de la instalación regulada");
			return;
		}
		guardado = true;
		JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	}

	public boolean guardarPaso2(PuntoRecuperacionRgdRcoa puntoRecuperacion) {
		boolean insercion = false;
		System.out.println("entro en paso 2");
		try {
			double x = 0, y = 0;
			for (FormaPuntoRecuperacionRgdRcoa formaSubEnvia : puntoRecuperacion.getFormasPuntoRecuperacionRgdRcoa()) {
				List<CoordenadaRgdCoa> listaCoordenadas = formaSubEnvia.getCoordenadas();
				for (CoordenadaRgdCoa coordenada : listaCoordenadas) {
					x = coordenada.getX();
					y = coordenada.getY();
				}
			}
			List<PuntoRecuperacionRgdRcoa> lista = new ArrayList<PuntoRecuperacionRgdRcoa>();
			lista = puntoRecuperacionRgdRcoaFacade.buscarPorRgd(registroGeneradorDesechos.getId());
			for (PuntoRecuperacionRgdRcoa forma : lista) {
				List<FormaPuntoRecuperacionRgdRcoa> listaFormas = forma.getFormasPuntoRecuperacionRgdRcoa();
				for (FormaPuntoRecuperacionRgdRcoa formaSub : listaFormas) {
					List<CoordenadaRgdCoa> listaCoordenadas = formaSub.getCoordenadas();
					for (CoordenadaRgdCoa coordenada : listaCoordenadas) {
						if (x == coordenada.getX() && y == coordenada.getY()) {
							existenCoordenadas = true;
							break;
						}
					}
				}
			}
			if (existenCoordenadas) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_COORDENADAS_EXISTENTES);
			} else {
				puntoRecuperacion.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
				puntoRecuperacionRgdRcoaFacade.savePuntoRecuperacion(puntoRecuperacion, loginBean.getUsuario());
				insercion = true;
			}
			puntosDeRecuperacion();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return insercion;
	}

	private void guardarPaso3() throws ServiceException, CmisAlfrescoException {
		System.out.println("ingreso en guardar");
		if (JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaSeleccionados().size() > 0) {
			for (DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class)
					.getDesechosRcoaSeleccionados()) {
				if (desechoG.getDesechoPeligroso().isEsLampara()) {
					if (desechoG.isSeleccionarDesecho()) {
						if ((desechoG.getGeneraDesecho() == null || !desechoG.getGeneraDesecho())
								&& desechoG.getCantidadKilos() == null) {
							JsfUtil.addMessageError("Generación anual es Requerido");
							guardado = false;
							return;
						}
					}
				} else {
					if ((desechoG.getGeneraDesecho() == null || !desechoG.getGeneraDesecho())
							&& desechoG.getCantidadKilos() == null) {
						JsfUtil.addMessageError("Generación anual es Requerido");
						guardado = false;
						return;
					}
				}
			}

			Boolean errorGuardadoDesecho = false;
			for (DesechosRegistroGeneradorRcoa desechoG : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class)
					.getDesechosRcoaSeleccionados()) {
				try {
					if (desechoG.getDesechoPeligroso().isEsLampara()) {
						if (desechoG.isSeleccionarDesecho()) {
							desechoG.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);

							if (desechoG.getGeneraDesecho() == null) {
								desechoG.setGeneraDesecho(false);
							}
							if (desechoG.getSistemaGestionIndividual()) {
								desechoG.setSistemaGestionNombre(null);
								desechoG.setSistemaGestionFecha(null);
							}
							desechosRegistroGeneradorRcoaFacade.save(desechoG, loginBean.getUsuario());

							if (desechoG.getDocumentoGenera() != null
									&& desechoG.getDocumentoGenera().getContenidoDocumento() != null) {
								desechoG.getDocumentoGenera()
										.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
								desechoG.getDocumentoGenera().setIdTable(desechoG.getId());
								desechoG.getDocumentoGenera().setIdProceso((int) bandejaTareasBean.getProcessId());
								desechoG.getDocumentoGenera()
										.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
								documentosRgdRcoaFacade.guardarDocumento(desechoG.getDocumentoGenera(),
										"REGISTRO GENERADOR DE DESECHOS", TipoDocumentoSistema.RGD_NO_GENERA_DESECHOS);
							}
						}
					} else {
						desechoG.setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);

						if (desechoG.getGeneraDesecho() == null) {
							desechoG.setGeneraDesecho(false);
						}
						if (desechoG.getSistemaGestionIndividual()) {
							desechoG.setSistemaGestionNombre(null);
							desechoG.setSistemaGestionFecha(null);
						}
						desechosRegistroGeneradorRcoaFacade.save(desechoG, loginBean.getUsuario());

						if (desechoG.getDocumentoGenera() != null
								&& desechoG.getDocumentoGenera().getContenidoDocumento() != null) {
							desechoG.getDocumentoGenera().setRegistroGeneradorDesechosRcoa(registroGeneradorDesechos);
							desechoG.getDocumentoGenera().setIdTable(desechoG.getId());
							desechoG.getDocumentoGenera().setIdProceso((int) bandejaTareasBean.getProcessId());
							desechoG.getDocumentoGenera()
									.setNombreTabla(RegistroGeneradorDesechosRcoa.class.getSimpleName());
							documentosRgdRcoaFacade.guardarDocumento(desechoG.getDocumentoGenera(),
									"REGISTRO GENERADOR DE DESECHOS", TipoDocumentoSistema.RGD_NO_GENERA_DESECHOS);
						}
					}
				} catch (Exception ae) {
					errorGuardadoDesecho = true;
				}
			}

			if (errorGuardadoDesecho) {
				guardado = false;
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}

			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			guardado = true;
		}

		if (JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class).getDesechosRcoaEliminados() != null) {
			for (DesechosRegistroGeneradorRcoa desechoE : JsfUtil.getBean(AdicionarDesechosPeligrososRcoaBean.class)
					.getDesechosRcoaEliminados()) {
				desechoE.setEstado(false);
				desechosRegistroGeneradorRcoaFacade.saveDesecho(desechoE, loginBean.getUsuario());
			}
		}
	}

	public String mostrarNumeroSolicitud() {
		String numeroRgd = "";

		if (registroGeneradorDesechos != null && registroGeneradorDesechos.getCodigo() != null) {
			numeroRgd = registroGeneradorDesechos.getCodigo();
		}
		return numeroRgd;
	}

	public void enviar() {
		try {
			String nombreTarea = "Ingresar datos del registro";
			// valido que sea la tarea de completar informacion para pasar de tarea
			if (!nombreTarea.equals(bandejaTareasBean.getTarea().getTaskName())) {
				JsfUtil.addMessageError("Error al procesar la tarea.");
				return;
			}
			guardarPaso3();
			boolean certificado = false;
			boolean licenciaEjecucion = false;
			registroGeneradorDesechos.setFechaEnvioInformacion(new Date());
			registroGeneradorDesechos.setFinalizado(true);
			registroGeneradorDesechosRcoaFacade.save(registroGeneradorDesechos, loginBean.getUsuario());

			String autoridad = "";
			Usuario usuarioAutoridad = areaFacade.getDirectorPlantaCentral("role.area.subsecretario.calidad.ambiental");

			if (usuarioAutoridad != null && usuarioAutoridad.getId() != null) {
				autoridad = usuarioAutoridad.getNombre();
			} else {
				JsfUtil.addMessageError("Ocurrio un error. Comuniquese con mesa de ayuda");
				System.out.println(
						"No existe usuario " + Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
				return;
			}
			Map<String, Object> params = new HashMap<>();
			params.put("certificadoNELicenciaN", certificado);
			params.put("licenciaProyectoEjecucion", licenciaEjecucion);
			params.put("actualizacionRGDREP", false);
			params.put("actualizacionTitularVariosProyectos", false);
			params.put("director", autoridad);

			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(),
					bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
			JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public StreamedContent getPlantilla() {
		try {
			if (bytePlantilla != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(bytePlantilla),
						"application/pdf", "Plantilla_justificacion_de_no_generacion.pdf");
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar la pantilla de justificación de no generación.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar la pantilla de justificación de no generación.");
		return null;
	}

	public void iniciarProceso() {
		iniciar(tramite);
	}

	private boolean iniciar(String tramite) {
		try {
			tramite = "MAE-RA-2020-415573";
			ProyectoLicenciaCoa proyectoInicial = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("tramite", tramite);
			parametros.put("idProyecto", proyectoInicial.getId());
			parametros.put("emisionVariosProyectos", false);
			// parametros.put("actualizacionRGDREP", true);

			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_REGISTRO_GENERADOR_DESECHOS, tramite,
					parametros);

			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage() + " " + e.getCause().getMessage());
			return false;
		}
	}
}
