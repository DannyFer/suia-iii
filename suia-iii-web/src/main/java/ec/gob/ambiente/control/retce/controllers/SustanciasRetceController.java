package ec.gob.ambiente.control.retce.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.SerializationUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.retce.model.CatalogoMetodoEstimacion;
import ec.gob.ambiente.retce.model.CatalogoSustanciasRetce;
import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.GeneradorDesechosPeligrososRetce;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.retce.model.ReporteSustancias;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TecnicoResponsable;
import ec.gob.ambiente.retce.services.CatalogoMetodoEstimacionFacade;
import ec.gob.ambiente.retce.services.CatalogoSustanciasRetceFacade;
import ec.gob.ambiente.retce.services.DatosLaboratorioFacade;
import ec.gob.ambiente.retce.services.DetalleCatalogoGeneralFacade;
import ec.gob.ambiente.retce.services.InformacionProyectoFacade;
import ec.gob.ambiente.retce.services.ReporteSustanciasFacade;
import ec.gob.ambiente.retce.services.SubstanciasRetceFacade;
import ec.gob.ambiente.retce.services.TecnicoResponsableFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.comun.bean.WizardBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.MensajeNotificacion;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.Email;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

@ManagedBean
@ViewScoped
public class SustanciasRetceController implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
	
	@ManagedProperty(value = "#{wizardBean}")
    @Getter
    @Setter
    private WizardBean wizardBean;
	
	@EJB
	private DetalleCatalogoGeneralFacade detalleCatalogoGeneralFacade;
	
	@EJB
    private TecnicoResponsableFacade tecnicoResponsableFacade;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	@EJB
	private InformacionProyectoFacade informacionProyectoFacade;
	
	@EJB
	private ReporteSustanciasFacade reporteSustanciasFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private CatalogoSustanciasRetceFacade catalogoSustanciasRetceFacade;
	
	@EJB
	private CatalogoMetodoEstimacionFacade catalogoMetodoEstimacionFacade;
	
	@EJB 
	private DatosLaboratorioFacade datosLaboratorioFacade;
	
	@EJB
	private SubstanciasRetceFacade substanciasRetceFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@Getter
	@Setter
	private List<Documento> listaDocumentosMediosVerificacion;
	
	@Getter
	@Setter
	private List<CatalogoSustanciasRetce> listaSustanciasRetce;
	
	@Getter
	@Setter
	private List<SubstanciasRetce> listaSustanciasEnergia, listaSustanciasEnergiaEliminar;
	
	@Getter
	@Setter
	private List<CatalogoMetodoEstimacion> listaMetodoEstimacion;
	
	@Getter
	@Setter
	private List<InformacionProyecto> listaInformacionBasica;
	
	@Getter
	@Setter
	private List<DetalleCatalogoGeneral> tiposLaboratorio, tiposComponente;
	
	@Getter
	@Setter
	private List<Usuario> usuariosTecnicos;
	
	@Getter
	@Setter
	private InformacionProyecto informacionProyecto;
	
	@Getter
	@Setter
	private ReporteSustancias reporteSustancias;
	
	@Getter
	@Setter
	private TecnicoResponsable tecnicoResponsable;
	
	@Getter
	@Setter
	private Documento informeMonitoreo, documentoCalculo;
	
	@Getter
	@Setter
	private SubstanciasRetce sustanciaRetce, sustanciaRetceOriginal;
	
	@Getter
	@Setter
	private DetalleCatalogoGeneral fuenteSeleccionada;
	
	@Getter
	@Setter
	private Usuario usuarioCoordinador;
	
	@Getter
	@Setter
	private Boolean aceptarCondiciones;
	
	@Getter
	@Setter
	private String nombreProponente, cedulaProponente, mensajeResponsabilidad;
	
	@Getter
	@Setter
	private Boolean editarSustancia;
	
	@Getter
	@Setter
	private Boolean showLaboratorio, showCalculo, habilitarSiguiente, verFormulario;
	
	@Getter
	@Setter
	private Integer totalColsSustancias, tipoComponente;
	
	@Getter
	@Setter
	private Integer ordenMedicionDirecta = 1;

	@PostConstruct
	private void iniciar() {
		JsfUtil.getLoggedUser().getNombre();
		try {
			Integer idReporte =(Integer)(JsfUtil.devolverObjetoSession(ReporteSustancias.class.getSimpleName()));

			if (idReporte != null) {
				reporteSustancias =  reporteSustanciasFacade.getById(idReporte);
				if(reporteSustancias != null)
					informacionProyecto = reporteSustancias.getInformacionProyecto();
				
				JsfUtil.cargarObjetoSession(ReporteSustancias.class.getSimpleName(), null);
			} else {
				return;
			}
			
			cargarInicio();
			
			if(reporteSustancias != null && reporteSustancias.getId() != null) {
				listaSustanciasEnergia = reporteSustancias.getListaSustanciasRetce();
				
				if(listaSustanciasEnergia != null && !listaSustanciasEnergia.isEmpty()) {
					habilitarSiguiente = true;
					
					for (SubstanciasRetce sustancia : listaSustanciasEnergia) {						
						if (sustancia.getDatosLaboratorio() != null) {
							List<Documento> documentos = documentosFacade.documentoXTablaIdXIdDoc(
									sustancia.getDatosLaboratorio().getId(),
									ReporteSustancias.class.getSimpleName(),
									TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
							
							if (documentos.size() > 0)
								sustancia.getDatosLaboratorio().setDocumentoLaboratorio(documentos.get(0));
						} 
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cargarInicio() {
		tecnicoResponsable = (reporteSustancias.getTecnicoResponsable() == null) ? new TecnicoResponsable() : reporteSustancias.getTecnicoResponsable();
		reporteSustancias = (reporteSustancias == null)  ? new ReporteSustancias() : reporteSustancias;
		informeMonitoreo = new Documento();
		
		aceptarCondiciones = false;
		editarSustancia= false;
		habilitarSiguiente = false;
		
		listaSustanciasEnergia = new ArrayList<>();
		listaSustanciasEnergiaEliminar = new ArrayList<>();
		
		nombreProponente = loginBean.getUsuario().getPersona().getNombre();
		cedulaProponente = loginBean.getUsuario().getPersona().getPin();
		MensajeResponsabilidadRetceController mensajeResponsabilidadRetceController = JsfUtil.getBean(MensajeResponsabilidadRetceController.class);
		mensajeResponsabilidad = mensajeResponsabilidadRetceController.mensajeResponsabilidadRetce(informacionProyecto.getUsuarioCreacion());
		sustanciaRetce = new SubstanciasRetce();
		
		listaMetodoEstimacion = catalogoMetodoEstimacionFacade.findAll();
		tiposLaboratorio = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("laboratorio.tipo_laboratorio");
		tiposComponente = detalleCatalogoGeneralFacade.findByCatalogoGeneralString("sustancia.tipo_componente");
	}
	
	public String btnSiguiente() {
		try {
			
		} catch (Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
		}

        return null;
    }
	
	public String btnAtras() {
        return null;
    }
	
	
	public void guardar() {
		try {
            Boolean guardadoExitoso = true;
            
            guardarSustancias();
            
            if(guardadoExitoso){
            	habilitarSiguiente = true;
            	
	            JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
            }

        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
	}
	
	public StreamedContent descargar(Documento documento) throws IOException {
		DefaultStreamedContent content = null;
		try {
			byte[] documentoContent = null;
			
			if (documento != null && documento.getIdAlfresco() != null) {
				documentoContent = documentosFacade.descargar(documento.getIdAlfresco());
			} else if (documento.getContenidoDocumento() != null) {
				documentoContent = documento.getContenidoDocumento();
			}
			
			if (documento != null && documento.getNombre() != null
					&& documentoContent != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(documentoContent));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception e) {
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
		}
		return content;
	}
	
	public void cambiarEstimacion() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)) {
			sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		} else 
			sustanciaRetce.setDatosLaboratorio(null);
	}
	
	public void cambiarTipoLaboratorio() {
		DetalleCatalogoGeneral tipo = sustanciaRetce.getDatosLaboratorio().getTipoLaboratorio();
		
		if(sustanciaRetce.getDatosLaboratorio() != null && sustanciaRetce.getDatosLaboratorio().getId() != null) {
			sustanciaRetce.getDatosLaboratorio().setRuc(null);
			sustanciaRetce.getDatosLaboratorio().setNombre(null);
			sustanciaRetce.getDatosLaboratorio().setNumeroRegistroSAE(null);
			sustanciaRetce.getDatosLaboratorio().setFechaVigenciaRegistro(null);
		} else {
			sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		}
		
		sustanciaRetce.getDatosLaboratorio().setTipoLaboratorio(tipo);
	}

	
	public void nuevaSustancia() {
		sustanciaRetce = new SubstanciasRetce();
		sustanciaRetce.setDatosLaboratorio(new DatosLaboratorio());
		informeMonitoreo = new Documento();
		documentoCalculo = new Documento();
		tipoComponente = null;
	}
	
	public void buscarSustancias() {
		listaSustanciasRetce = new ArrayList<>();
		listaSustanciasRetce = catalogoSustanciasRetceFacade.findByTipoComponente(Integer.parseInt(sustanciaRetce.getTipoComponente().getParametro()), 3);
	}
	
	public List<CatalogoSustanciasRetce> getSustanciasPendientes() {
		List<CatalogoSustanciasRetce> result = new ArrayList<>();

		result.addAll(listaSustanciasRetce);

		if(listaSustanciasEnergia != null) {
			for (SubstanciasRetce desechoSustancia : listaSustanciasEnergia) {
				if (editarSustancia && desechoSustancia.getCatologSustanciasRetce().equals(sustanciaRetce.getCatologSustanciasRetce()))
					continue;

				if (desechoSustancia.getCatologSustanciasRetce() != null)
					result.remove(desechoSustancia.getCatologSustanciasRetce());
			}
		}

		return result;
	}
	
	public void agregarSustancia() {
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			sustanciaRetce.getDatosLaboratorio().setDocumentoLaboratorio(informeMonitoreo);
			sustanciaRetce.setDocumento(null);
		} else {
			sustanciaRetce.setDatosLaboratorio(null);
			sustanciaRetce.setDocumento(documentoCalculo);
		}
		
		if (!listaSustanciasEnergia.contains(sustanciaRetce))
			listaSustanciasEnergia.add(sustanciaRetce);

		sustanciaRetce = new SubstanciasRetce();
		sustanciaRetceOriginal = null;
		editarSustancia = false;
		
		
		JsfUtil.addCallbackParam("addSustancia");
	}
	
	public void editarSustancia(SubstanciasRetce sustancia){
		sustanciaRetceOriginal = (SubstanciasRetce) SerializationUtils.clone(sustancia);
		sustanciaRetce = sustancia;
		
		buscarSustancias();
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			informeMonitoreo = sustancia.getDatosLaboratorio().getDocumentoLaboratorio();
			
			if(informeMonitoreo == null && sustanciaRetce.getId() != null){			
				List<Documento> documentosManifiesto = documentosFacade.documentoXTablaIdXIdDoc(
						sustancia.getDatosLaboratorio().getId(),
						GeneradorDesechosPeligrososRetce.class.getSimpleName(),
						TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO);
				if (documentosManifiesto.size() > 0)
					informeMonitoreo = documentosManifiesto.get(0);
			}
		} else {
			documentoCalculo = (sustancia.getDocumento() != null) ? sustancia.getDocumento() : new Documento();
		}
		
		editarSustancia = true;
		listaSustanciasEnergia.remove(sustancia);
		
		try {
			JsfUtil.addCallbackParam("addSustancia");
		} catch (Exception e) {
			JsfUtil.addMessageError("Error al cargar la información");
		}
	}
	
	public void eliminarSustancia(SubstanciasRetce sustancia){
		try {
			if (sustancia.getId() != null) {
				sustancia.setEstado(false);
				listaSustanciasEnergiaEliminar.add(sustancia);
			}
			listaSustanciasEnergia.remove(sustancia);
		} catch (Exception e) {

		}
	}
	
	public void cancelarSustancia(){
		if (editarSustancia)
			listaSustanciasEnergia.add(sustanciaRetceOriginal);
		
		sustanciaRetce = new SubstanciasRetce();
		editarSustancia = false;
	}
	
	public void validateSustancias(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(listaSustanciasEnergia == null || listaSustanciasEnergia.size() == 0 )
			errorMessages.add(new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					"Debe ingresar información de las Sustancias RETCE", null));
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void guardarSustancias() throws ServiceException, CmisAlfrescoException {
		
		if(listaSustanciasEnergia.size() > 0){
			for (SubstanciasRetce substanciasRetce : listaSustanciasEnergia) {
				substanciasRetce.setReporteSustancias(reporteSustancias);

				Boolean saveLaboratorio = false;
				DatosLaboratorio lab = new DatosLaboratorio();
				if (substanciasRetce.getDatosLaboratorio() != null) {
					DatosLaboratorio laboratorio = substanciasRetce.getDatosLaboratorio();
					Documento documentoInforme = laboratorio.getDocumentoLaboratorio();
					lab = datosLaboratorioFacade.saveLaboratorioEmisiones(laboratorio, loginBean.getUsuario());
					substanciasRetce.setDatosLaboratorio(lab);
					saveLaboratorio = true;

					if (documentoInforme != null && documentoInforme.getContenidoDocumento() != null) {
						documentoInforme.setIdTable(laboratorio.getId());
						documentoInforme.setNombreTabla(ReporteSustancias.class.getSimpleName());
						documentoInforme.setDescripcion("Informe de monitoreo emitido por el laboratorio");
						documentoInforme.setEstado(true);
						Documento documentoInformeSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
										informacionProyecto.getCodigoRetce(),
										"REPORTE_SUSTANCIAS",
										laboratorio.getId().longValue(),
										documentoInforme,
										TipoDocumentoSistema.TIPO_DOCUMENTO_INFORME_LABORATORIO,
										null);

						lab.setDocumentoLaboratorio(documentoInformeSave);
					}
				}

				Documento respaldo = substanciasRetce.getDocumento();
				substanciasRetce.setDocumento(null);
				substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());

				if (respaldo != null
						&& respaldo.getContenidoDocumento() != null) {
					respaldo.setIdTable(substanciasRetce.getId());
					respaldo.setNombreTabla(ReporteSustancias.class.getSimpleName());
					respaldo.setDescripcion("Respaldo sustancia RETCE");
					respaldo.setEstado(true);
					Documento respaldoSave = documentosFacade.guardarDocumentoAlfrescoSinProyecto(
									informacionProyecto.getCodigoRetce(),
									"REPORTE_SUSTANCIAS",
									substanciasRetce.getId().longValue(),
									respaldo,
									TipoDocumentoSistema.TIPO_DOCUMENTO_RESPALDO_LABORATORIO,
									null);
					
					substanciasRetce.setDocumento(respaldoSave);
					substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());
				} else if (respaldo != null && respaldo.getId() != null) {
					substanciasRetce.setDocumento(respaldo);
					substanciasRetceFacade.save(substanciasRetce, loginBean.getUsuario());
				}

				if (saveLaboratorio)
					substanciasRetce.setDatosLaboratorio(lab);
			}
		}
		
		reporteSustanciasFacade.eliminarSustancia(listaSustanciasEnergiaEliminar);
		
		listaSustanciasEnergiaEliminar = new ArrayList<>();
	}
	
	public void validarCedula() {
        try {           
            if (sustanciaRetce.getDatosLaboratorio().getRuc() != null
                    && !sustanciaRetce.getDatosLaboratorio().getRuc().isEmpty()) {

                if (sustanciaRetce.getDatosLaboratorio().getRuc().length() == 13) {
                    ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula
                            .obtenerPorRucSRI(Constantes.USUARIO_WS_MAE_SRI_RC,
                                    Constantes.PASSWORD_WS_MAE_SRI_RC,
                                    sustanciaRetce.getDatosLaboratorio().getRuc());
                    if (contribuyenteCompleto == null) {
                        JsfUtil.addMessageError("RUC inválido");
                        return;
                    }
                    if (!contribuyenteCompleto.getCodEstado().equals("PAS")
                            || Constantes.getPermitirRUCPasivo()) {
                        cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
                    } else {
                        JsfUtil.addMessageError("El estado de su RUC es PASIVO. Si desea registrarse con el mismo debe activarlo en el SRI.");
                    }
                } else {
                    JsfUtil.addMessageError("El RUC debe tener 13 dígitos.");
                }

            } else {
                JsfUtil.addMessageError("El campo 'Cédula / RUC / Pasaporte' es requerido.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void cargarDatosWsRucPersonaNatural(
            ContribuyenteCompleto contribuyenteCompleto) {
        if (contribuyenteCompleto != null) {
            if (contribuyenteCompleto.getRazonSocial() != null) {               
            	sustanciaRetce.getDatosLaboratorio().setNombre(contribuyenteCompleto.getRazonSocial());

            } else {
                JsfUtil.addMessageError("RUC no encontrado.");
            }
        } else {
            JsfUtil.addMessageError("Sin Servicio");
        }
    }
	
	public void uploadFileDocumentoCalculo(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		documentoCalculo = new Documento();
		documentoCalculo.setId(null);
		documentoCalculo.setContenidoDocumento(contenidoDocumento);
		documentoCalculo.setNombre(event.getFile().getFileName());
		documentoCalculo.setMime("application/pdf");
		documentoCalculo.setExtesion(".pdf");
	}

	public void uploadInformeMonitoreo(FileUploadEvent event) {
		byte[] contenidoDocumento = event.getFile().getContents();
		informeMonitoreo = new Documento();
		informeMonitoreo.setId(null);
		informeMonitoreo.setContenidoDocumento(contenidoDocumento);
		informeMonitoreo.setNombre(event.getFile().getFileName());
		informeMonitoreo.setMime("application/pdf");
		informeMonitoreo.setExtesion(".pdf");
				
		JsfUtil.addMessageInfo("Documento subido exitosamente");
	}
	
	public void validateLaboratorio(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion() != null && 
				sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			if (informeMonitoreo.getId() == null && informeMonitoreo.getContenidoDocumento() == null)
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el informe de laboratorio en formato .PDF tamaño permitido 20Mb.", null));
			
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	public void validateDocumentoCalculo(FacesContext context, UIComponent validate, Object value) {
		List<FacesMessage> errorMessages = new ArrayList<>();
		
		if(sustanciaRetce.getCatalogoMetodoEstimacion() != null && !sustanciaRetce.getCatalogoMetodoEstimacion().getOrden().equals(ordenMedicionDirecta)){
			if (documentoCalculo == null || (documentoCalculo.getId() == null && documentoCalculo.getContenidoDocumento() == null))
				errorMessages.add(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								"Debe adjuntar el documento de respaldo del cálculo en formato .PDF tamaño permitido 20Mb.", null));
			
		}
		
		if (!errorMessages.isEmpty())
			throw new ValidatorException(errorMessages);
	}
	
	// INFORMACIÓN DEL TÉCNICO
	public void validarCedulaListener() {
		
		JsfUtil.getBean(InformacionBasicaController.class).setTecnicoResponsable(new TecnicoResponsable());
		JsfUtil.getBean(InformacionBasicaController.class).getTecnicoResponsable().setIdentificador(tecnicoResponsable.getIdentificador());
		
		JsfUtil.getBean(InformacionBasicaController.class).validarCedulaListener();
		
		tecnicoResponsable = new TecnicoResponsable();
		tecnicoResponsable = JsfUtil.getBean(InformacionBasicaController.class).getTecnicoResponsable();
		
		if (tecnicoResponsable.getIdentificador() == null) {
			return;
		}
	}	
	
	public String aceptar() {
		try{
			if(!validarUsuariosNotificacion())
				return null;
			
			tecnicoResponsableFacade.save(tecnicoResponsable, loginBean.getUsuario());			
			
			reporteSustancias.setTecnicoResponsable(tecnicoResponsable);
			reporteSustancias.setRegistroFinalizado(true);
			reporteSustancias.setFechaTramite(new Date());
			reporteSustanciasFacade.guardarReporte(reporteSustancias);
			
			//enviar notificacion a responsable contactos			
			Organizacion organizacion = organizacionFacade.buscarPorRuc(JsfUtil.getLoggedUser().getNombre());
			String nombreOperador = "";
			Usuario usuarioOperador = JsfUtil.getLoggedUser();
			if (usuarioOperador.getPersona().getOrganizaciones().size() == 0) {
				nombreOperador = usuarioOperador.getPersona().getNombre();
			} else {
				nombreOperador = organizacion.getNombre();
			}
			
			DateFormat formatoFecha = DateFormat.getDateInstance(DateFormat.LONG, new Locale("es"));
			MensajeNotificacion mensajeNotificacion = mensajeNotificacionFacade.buscarMensajesNotificacion("bodyNotificacionReporteSustanciasRetce");
			
			if (usuarioCoordinador != null) {
				Object[] parametros = new Object[] {
						usuarioCoordinador.getPersona().getNombre(),
						nombreOperador, reporteSustancias.getCodigoTramite(), formatoFecha.format(reporteSustancias.getFechaTramite()) };
				String notificacion = String.format(mensajeNotificacion.getValor(), parametros);
				Email.sendEmail(usuarioCoordinador, "Reporte de Sustancias RETCE", notificacion, informacionProyecto.getCodigo(), loginBean.getUsuario());
			}
			
			if (usuariosTecnicos != null) {
				for (Usuario usuario : usuariosTecnicos) {
					Object[] param = new Object[] {
							usuario.getPersona().getNombre(), nombreOperador,
							reporteSustancias.getCodigoTramite(), formatoFecha.format(reporteSustancias.getFechaTramite()) };
					String notificacionTecnico = String.format(mensajeNotificacion.getValor(), param);
					Email.sendEmail(usuario, "Reporte de Sustancias RETCE", notificacionTecnico, informacionProyecto.getCodigo(), loginBean.getUsuario());
					Thread.sleep(2000);
				}
			}

			
			JsfUtil.addMessageInfo("La información ingresada ha sido enviada con éxito");
			
			JsfUtil.cargarObjetoSession(InformacionProyecto.class.getSimpleName(), informacionProyecto.getId());
			return JsfUtil.actionNavigateTo("/control/retce/sustanciasRetce.jsf");
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
		
		return null;
	}
	
	public Boolean validarUsuariosNotificacion() {
		try {
			String rolCoordinador = "role.retce.pc.coordinador.sustancias";
			String rolTecnico = "role.retce.pc.tecnico.sustancias";
			
			List<Usuario> usuariosCoordinador = areaFacade.getUsuariosPlantaCentralPorRol(rolCoordinador);
			usuariosTecnicos = areaFacade.getUsuariosPlantaCentralPorRol(rolTecnico);
			
			if((usuariosCoordinador == null || usuariosCoordinador.size() == 0) && 
					(usuariosTecnicos == null || usuariosTecnicos.size() == 0)) {
				JsfUtil.addMessageError("No existe usuario para envío de notificación");
				return false;
			}
			
			usuarioCoordinador = usuariosCoordinador.get(0);
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
            return false;
        }
		
		return true;
	}
	
	public Boolean validarUsuariosNotificacion_soloCoordinador() {
		try {
			String tipoArea;
			switch (informacionProyecto.getAreaSeguimiento().getTipoArea().getId()) {
			case 1:// Planta Central;PC
				tipoArea = "pc";
				break;
			case 2:// Dirección Provincial;DP;TÉCNICO ANALISTA
				tipoArea = "dp";
				break;
			case 3:// Ente Acreditado;EA;TÉCNICO PROVINCIAL
				tipoArea = "ea";
				break;
	
			default:
				return null;
			}
			
			String roleKey = "role.retce." + tipoArea + ".coordinador.desechos";
			List<Usuario> usuariosNotificacion = usuarioFacade.buscarUsuariosPorRolYArea(
					Constantes.getRoleAreaName(roleKey),
					informacionProyecto.getAreaSeguimiento());
			
			if(usuariosNotificacion == null || usuariosNotificacion.isEmpty()) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
				return false;
			}
			
			usuarioCoordinador = usuariosNotificacion.get(0);
			
			return true;
		} catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
        }
		
		return false;
	}
	
	public void validarData() {
		if (informacionProyecto == null) {
			JsfUtil.redirectTo("/control/retce/informacionBasica.jsf");
		}
	}
}
