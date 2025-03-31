package ec.gob.ambiente.prevencion.registro.proyecto.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.categoria1.bean.RecibirCertificadoRegistroAmbientalBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.ProcesoAdministrativoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.bean.VerProyectoBean;
import ec.gob.ambiente.prevencion.registro.proyecto.controller.base.RegistroProyectoController;
import ec.gob.ambiente.suia.AutorizacionCatalogo.facade.AutorizacionCatalogoFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.ProyectosCamaronerasFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionFacade;
import ec.gob.ambiente.suia.control.aprobacionrequisitostecnicos.facade.AprobacionRequisitosTecnicosFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreasAutorizadasCatalogo;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InterseccionProyecto;
import ec.gob.ambiente.suia.domain.MineroArtesanal;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.ProyectoCamaronera;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoArea;
import ec.gob.ambiente.suia.domain.TipoSector;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.eia.facade.LicenciaAmbientalFacade;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.notificacionautoridades.controllers.NotificacionAutoridadesController;
import ec.gob.ambiente.suia.notificaciones.facade.NotificacionesFacade;
import ec.gob.ambiente.suia.prevencion.categoria1.facade.Categoria1Facade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.v2.CategoriaIIFacadeV2;
import ec.gob.ambiente.suia.prevencion.requisitosPrevios.RequisitosPreviosLicenciaFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.proyectolicenciamientoambiental.facade.ProyectoLicenciamientoAmbientalFacade;
import ec.gob.ambiente.suia.reportes.DocumentoPDFPlantillaHtml;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.validaPago.PagoConfiguracionesUtil;

@ManagedBean
public class VerProyectoController implements Serializable {

    private static final long serialVersionUID = -1121710921505280710L;
    private static final Logger LOGGER = Logger.getLogger(VerProyectoController.class);
    private static final String MENSAJE_REQUISITOS_PREVIOS_DESECHOS = "mensaje.requisitosprevios.genera.desechos";
    private static final String MENSAJE_REQUISITOS_PREVIOS_INTERSECA = "mensaje.requisitosprevios.interseca";
    private static final String MENSAJE_REQUISITOS_NO_FACTIBLE = "mensaje.requisitosprevios.no.factible";
    private static final String IMAGEN_REQUIERE_PREGUNTAS = "/resources/images/mensajes/ayuda_certificado_viabilidad_suia_iii.png";
    private static final String IMAGEN_GESTIONA_DESECHOS = "/resources/images/mensajes/ayuda_gestion_desechos_suia_iii.png";

    private static final String MENSAJE_CATEGORIAI_REGISTRO = "mensaje.categoriaI.registro";
    private static final String MENSAJE_CATEGORIAI_INTERSECA = "mensaje.categoriaI.interseca";
    private static final String MENSAJE_CATEGORIAI_INTERSECA_QUEBRADAS = "mensaje.categoriaI.interseca_quebradas";
    private static final String MENSAJE_CATEGORIAII = "mensaje.categoriaII";
    private static final String MENSAJE_CATEGORIAIII = "mensaje.categoriaIII";
    private static final String MENSAJE_CATEGORIAIV = "mensaje.categoriaIV";
    private static final String MENSAJE_ENTE = "mensaje.ente";
    private static final String MENSAJE_ENTE_MDQ = "mensaje.entemdq";
    private static final String MENSAJE_CATEGORIAII_EXENTO_PAGO = "mensaje.categoriaII.exento.pago";
    private static final String IMAGEN_LISTA_PROYECTOS = "/resources/images/mensajes/lista_proyecto.png";
    private static final String IMAGEN_BANDEJA_PROYECTOS = "/resources/images/mensajes/bandeja_tarea.png";
    private static final String IMAGEN_VALIDAR_PAGO = "/resources/images/mensajes/validar_Pago_tasas.png";
    private static final String IMAGEN_CATEGORIAIIIIV = "/resources/images/mensajes/CategoriaIIIIV.png";
    private static final String IMAGEN_LICENCIA_AMBIENTAL_NO_HIDROCARBUROS = "/resources/images/mensajes/ayuda_LicenciaAmbiental_suia_iii.png";
    private static final String IMAGEN_BANDEJA_ENCUESTA = "/resources/images/mensajes/bandeja_encuesta.png";
    private static final String IMAGEN_BANDEJA_COBERTURA = "/resources/images/mensajes/bandeja_cobertura.png";

    @EJB
    private RequisitosPreviosLicenciaFacade requisitosPreviosFacade;

    @EJB
    private CategoriaIIFacadeV2 categoriaIIFacade;

    @EJB
    private LicenciaAmbientalFacade licenciaAmbientalFacade;

    @EJB
    private ProyectoLicenciamientoAmbientalFacade proyectoLicenciamientoAmbientalFacade;

    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    @Getter
    @Setter
    @ManagedProperty(value = "#{verProyectoBean}")
    private VerProyectoBean verProyectoBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{generarCertificadoInterseccionController}")
    private GenerarCertificadoInterseccionController generarCertificadoInterseccionController;

    @EJB
    private DocumentosFacade documentosFacade;

    @EJB
    private NotificacionesFacade notificacionesFacade;

    @EJB
    private CertificadoInterseccionFacade certificadoInterseccionService;

    @EJB
    private AprobacionRequisitosTecnicosFacade aprobacionRequisitosTecnicosFacade;

    @EJB
    private AreaFacade areaFacade;
    
    @EJB
	private UsuarioFacade usuarioFacade;

    @EJB
   	private CrudServiceBean crudServiceBean;
    
    @EJB
	private ContactoFacade contactoFacade;
    
    @EJB
	private Categoria1Facade categoria1Facade;
    
    /**** pagos 20160303****/
    @Inject
	private PagoConfiguracionesUtil pagoConfiguracionesUtil;
    /**** pagos 20160303****/
    
    @EJB
    private ProcesoFacade procesoFacade;
    
    @EJB
    private AutorizacionCatalogoFacade autorizacionCatalogoFacade;
    

    @Getter
    @Setter
    @ManagedProperty(value = "#{registroProyectoController}")
    private RegistroProyectoController registroProyectoController;

    
    public void notificarProcesoAdministrativo() {
        try {
            int cantidadProcesosAdministrativos = JsfUtil.getBean(ProcesoAdministrativoBean.class)
                    .getCantidadProcesosAdmUsuarioAutenticado();
            if (cantidadProcesosAdministrativos > 0 && getUsuariosNotificarProcesoAdministrativo() != null) {

                String[] parametrosAsunto = {String.valueOf(cantidadProcesosAdministrativos)};
                String asunto = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                        "notificacion_proceso_administrativo_asunto", parametrosAsunto);

                String nombreProponente = JsfUtil.getBean(LoginBean.class).getUsuario().getPersona().getNombre();
                String cedulaORuc = JsfUtil.getBean(LoginBean.class).getUsuario().getPersona().getPin();
                String nombreProyecto = verProyectoBean.getProyecto().getNombre();
                String codigoProyecto = verProyectoBean.getProyecto().getCodigo();
                String actividadEconomica = verProyectoBean.getProyecto().getCatalogoCategoria().getDescripcion();

                String[] parametrosDescripcion = {nombreProponente, cedulaORuc, nombreProyecto, codigoProyecto,
                        actividadEconomica};
                String descripcion = DocumentoPDFPlantillaHtml.getPlantillaConParametros(
                        "notificacion_proceso_administrativo_descripcion", parametrosDescripcion);

                notificacionesFacade.adicionarNotificacion(codigoProyecto, asunto, descripcion,
                        getUsuariosNotificarProcesoAdministrativo(), " ", 0L, null);
            }

        } catch (Exception exception) {
            LOGGER.error("Ocurrio un error en el proceso de notificacion", exception);
        }
    }

    public String[] getUsuariosNotificarProcesoAdministrativo() {
        if (Constantes.getUsuariosNotificarProcesoAdministrativo() != null) {
            return Constantes.getUsuariosNotificarProcesoAdministrativo().split(",");
        }
        return null;
    }

    private boolean isProyectoListoParaFinalizar() {
        return generarCertificadoInterseccionController
                .validarDocumentosCertificadosInterseccionProyectoParaFinalizar()
                && verProyectoBean.getProyecto().getProyectoInterseccionExitosa();
    }

    private void iniciarProcesoRespectivos() throws ServiceException {
        // Para el sector de Hidrocarburos no inicia proceso de licenciamiento
        // en suiia azul
        if (TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()
                && (verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV())) {
            if (verProyectoBean.getProyecto().getAreaResponsable().getTipoArea().getId()
                    .equals(TipoArea.TIPO_AREA_ENTE_ACREDITADO)) {
            	AreasAutorizadasCatalogo vahidro=autorizacionCatalogoFacade.getaAreasAutorizadasCatalogo(verProyectoBean.getProyecto().getCatalogoCategoria(), verProyectoBean.getProyecto().getAreaResponsable());
            	if(vahidro!=null)
            	{
            		verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIV));
            		verProyectoBean.setPathImagen(IMAGEN_CATEGORIAIIIIV);
            		return;
            	}
            	else
            	{
            		verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_ENTE) + " "
            				+ verProyectoBean.getProyecto().getAreaResponsable().getAreaName());
            		verProyectoBean.setPathImagen(null);
            		LOGGER.info("No inicia proceso porque es ente acreditado, solamente se creó el proyecto");
            		return;
            	}
            } else {
                verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIV));
                verProyectoBean.setPathImagen(IMAGEN_CATEGORIAIIIIV);
                return;
            }
        }
        
        if(verProyectoBean.listProyectoCamaroneras!=null && verProyectoBean.listProyectoCamaroneras.size()>0){
        	for (ProyectoCamaronera proyectoCamaronera : verProyectoBean.listProyectoCamaroneras) {
				if(proyectoCamaronera.getCamaroneras()!=null){
					proyectoCamaronera.getCamaroneras().setUsado(true);
					crudServiceBean.saveOrUpdate(proyectoCamaronera.getCamaroneras());
				}
			}
        }
        
//        if (verProyectoBean.getProyecto().getOficioCamaronera()!=null){
//        	verProyectoBean.getProyecto().getOficioCamaronera().setUsado(true);
//        	crudServiceBean.saveOrUpdate(verProyectoBean.getProyecto().getOficioCamaronera());
//        }

        // En caso de ser ente acreditado
    	int ID_TIPO_AREA_ENTE_ACREDITADO=3;
        boolean isAcreditado = verProyectoBean.getProyecto().getAreaResponsable().getTipoArea().getId()
                .equals(TipoArea.TIPO_AREA_ENTE_ACREDITADO);
        Area enteAcreditado = areaFacade.getAreaEnteAcreditado(TipoArea.TIPO_AREA_ENTE_ACREDITADO,
                loginBean.getNombreUsuario());

        /**
         * Nombre:SUIA
         * Descripción: Validación de áridos y petreos
         * ParametrosIngreso:
         * PArametrosSalida:
         * Fecha:16/08/2015
         */
        if (verProyectoBean.getProyecto().getCatalogoCategoria().getCodigo()
                .equals("21.02.08.01")) {
            if (!(((verProyectoBean.getProyecto().getAreaResponsable()
                    .getUbicacionesGeografica().getNombre().equals("PICHINCHA")) || verProyectoBean
                    .getProyecto().getAreaResponsable()
                    .getUbicacionesGeografica().getNombre().equals("GUAYAS")) || ((verProyectoBean
                    .getProyecto().getAreaResponsable()
                    .getUbicacionesGeografica().getNombre().equals("AZUAY")) || (verProyectoBean
                    .getProyecto().getAreaResponsable()
                    .getUbicacionesGeografica().getNombre().equals("EL ORO"))))) {
				UbicacionesGeografica parroquia;
				try {
					parroquia = verProyectoBean.getProyecto()
							.getPrimeraParroquia();
					if (parroquia.getEnteAcreditadomunicipio() != null) {
            } else {
                    enteAcreditado = areaFacade.getAreaEnteAcreditado(
								ID_TIPO_AREA_ENTE_ACREDITADO, verProyectoBean
                                    .getProyecto().getAreaResponsable()
                                    .getIdentificacionEnte());
                }
				} catch (Exception e) {
					e.printStackTrace();
            }
            }
        } else {
			if (verProyectoBean.getProyecto().getAreaResponsable()
					.getAreaAbbreviation().equals("GPT")) {
				enteAcreditado = areaFacade.getAreaEnteAcreditado(
						TipoArea.TIPO_AREA_ENTE_ACREDITADO, verProyectoBean
								.getProyecto().getAreaResponsable()
								.getIdentificacionEnte());
			}
		}
        /**
         * FIN Validación de áridos y petreos
         */


        Boolean isEstrategico = verProyectoBean.getProyecto().getCatalogoCategoria().getEstrategico();
        Boolean interseca = certificadoInterseccionService.isProyectoIntersecaCapas(verProyectoBean.getProyecto()
                .getCodigo());
        int valorcapa = certificadoInterseccionService.getDetalleInterseccionlista(verProyectoBean.getProyecto().getCodigo());
        String nota = "";
        Integer entrocapa=0;
        if (valorcapa == 1 || valorcapa==2) {
        	isAcreditado=true;
        	isEstrategico=false;
        	enteAcreditado=verProyectoBean.getProyecto().getAreaResponsable();
			interseca = true;
			nota = DocumentoPDFPlantillaHtml
					.getValorResourcePlantillaInformes("nota_ente_mdq_registro");
			entrocapa = 1;
        }

        if (verProyectoBean.getProyecto().getAreaResponsable()
				.getAreaAbbreviation() != "GPT") {

			Area res = areaFacade.getAreaEnteAcreditado(
					ID_TIPO_AREA_ENTE_ACREDITADO, verProyectoBean.getProyecto()
							.getUsuario().getNombre());
			if (res != null && entrocapa != 1) {
				if (res.getTipoEnteAcreditado().equals("MUNICIPIO")
						|| res.getTipoEnteAcreditado().equals("GOBIERNO")) {
					enteAcreditado = null;
				}
			}
		}
        if (!interseca
                && isAcreditado
                && !isEstrategico
                && verProyectoBean.getProyecto().getAreaResponsable().getArea() == null
                && enteAcreditado != null) {
            /*
			 * if (!interseca && isAcreditado && !isEstrategico &&
			 * verProyectoBean.getProyecto().getAreaResponsable().getArea() ==
			 * null && enteAcreditado !=null) {
			 */
        	if (valorcapa == 1 || valorcapa ==2) {
        		verProyectoBean.setMensaje(Constantes
                        .getMessageResourceString(MENSAJE_ENTE_MDQ)
                        + " " + nota);
            } else {
                verProyectoBean.setMensaje(Constantes
                        .getMessageResourceString(MENSAJE_ENTE)
                        + " "
                        + verProyectoBean.getProyecto().getAreaResponsable()
                        .getAreaName() + " " + nota);
            }
            verProyectoBean.setPathImagen(null);
            LOGGER.info("No inicia proceso porque es ente acreditado, solamente se creó el proyecto");
            return;
        }
        // Se verifica si el proyecto requiere de requisitos previos a Permiso
        // Ambiental
        if (requisitosPreviosFacade.requiereRequisitosPrevios(verProyectoBean.getProyecto())) {
            // Se verifica si el proyecto es minero e interseca con Áreas
            // Protegidas
            if (requisitosPreviosFacade.proyectoNoFactible(verProyectoBean.getProyecto())) {
                verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_NO_FACTIBLE));
                verProyectoBean.setPathImagen(null);
            }
            // Si interseca, se muestra el mensaje de interseccion
            else if (requisitosPreviosFacade.intersecaAreasProtegidas(verProyectoBean.getProyecto())) {
                verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_PREVIOS_INTERSECA));
                verProyectoBean.setPathImagen(IMAGEN_REQUIERE_PREGUNTAS);
            }
            // Si no interseca y requiere requisitos técnicos, se muestra el
            // mensaje de gestion de desechos
            else if (requisitosPreviosFacade.requiereRequisitosTecnicos(verProyectoBean.getProyecto())) {
                verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_REQUISITOS_PREVIOS_DESECHOS));
                //verProyectoBean.setPathImagen(IMAGEN_GESTIONA_DESECHOS);
            }
            // Inicio de requisitos previos
            requisitosPreviosFacade.iniciarProcesoRequisitosPrevios(loginBean.getUsuario(),
                    verProyectoBean.getProyecto(), verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV());
        }
        // Si no se requieren requisitos previos, se inician los procesos de
        // registro o licencia
        else {
            if (verProyectoBean.isCategoriaIII() || verProyectoBean.isCategoriaIV()) {
                // Para el sector de Hidrocarburos no inicia proceso de
                // licenciamiento
                if (TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()) {
                    verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIV));
                    verProyectoBean.setPathImagen(IMAGEN_CATEGORIAIIIIV);
                    return;
                }
                // Inicio de Licenciamiento Ambiental
                if (valorcapa == 1 || valorcapa ==2) {
            		verProyectoBean.setMensaje(Constantes
                            .getMessageResourceString(MENSAJE_ENTE_MDQ)
                            + " " + nota +" "+Constantes.getMessageResourceString(MENSAJE_CATEGORIAIII));
            		verProyectoBean.setPathImagen(IMAGEN_LICENCIA_AMBIENTAL_NO_HIDROCARBUROS);
            		 iniciarProcesoLicenciaAmbiental();
                }else{
                	 verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAIII));
                     verProyectoBean.setPathImagen(IMAGEN_LICENCIA_AMBIENTAL_NO_HIDROCARBUROS);
                     iniciarProcesoLicenciaAmbiental();
                }
               
            } else if (verProyectoBean.isCategoriaII()) {
            	
            	if (valorcapa == 1 || valorcapa ==2) {
            		verProyectoBean.setMensaje(Constantes
                            .getMessageResourceString(MENSAJE_ENTE_MDQ)
                            + " " + nota +" "+Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_EXENTO_PAGO));
                } else {
//                    verProyectoBean.setMensaje(Constantes
//                            .getMessageResourceString(MENSAJE_ENTE)
//                            + " "
//                            + verProyectoBean.getProyecto().getAreaResponsable()
//                            .getAreaName() + " " + nota);
//               }
            	
                verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAII_EXENTO_PAGO));
                verProyectoBean.setPathImagen(IMAGEN_BANDEJA_PROYECTOS);
                }
                iniciarProcesoCategoriaII();
            }
        }
    }

    private boolean finalizarRegistroProyectoSuperioACategoriaI() {
        try {
            if (isProyectoListoParaFinalizar() && !verProyectoBean.getProyecto().isFinalizado()) {
                iniciarProcesoRespectivos();
            } else {
                if (verProyectoBean.getProyecto().getProyectoInterseccionExitosa() != null
                        && !verProyectoBean.getProyecto().getProyectoInterseccionExitosa()) {
                    JsfUtil.addMessageError("La ejecución del proceso de intersección no ha sido ejecutada exitosamente, por lo tanto no puede finalizar el registro del proyecto.");
                } else {
                    JsfUtil.addMessageError("El mapa u oficio del certificado de intersección aún no han sido generado, por lo tanto no puede finalizar el registro del proyecto.");
                }
                return false;
            }
            
            return true;

        } catch (Exception e) {
            LOGGER.error(e, e);
			try {
				// envionotificacion de no existir el usuario con rol requerido
				if (e.getMessage().contains("No se encontró un usuario")){
					GenerarNotificacionesAplicativoController enviarNotificaciones = JsfUtil.getBean(GenerarNotificacionesAplicativoController.class);
					enviarNotificaciones.enviarNotificacionError(verProyectoBean.getProyecto(), e.getMessage());						
				}
			} catch (ServiceException | InterruptedException e1) {
				LOGGER.error("Error al enviar la notificacion", e);
			}
            JsfUtil.addMessageError("Ocurrió un error con el registro del proyecto, por favor contacte a Mesa de Ayuda.");
            return false;
        }

    }

    public String getUrlContinuar() {
        return JsfUtil.getStartPage();
    }

    public void finalizarRegistroProyecto() {
        try {
            boolean finalizar = false;
            int valorcapa = certificadoInterseccionService.getDetalleInterseccionlista(verProyectoBean.getProyecto().getCodigo());
            UbicacionesGeografica parroquia;
            parroquia = verProyectoBean.getProyecto().getPrimeraParroquia();
            if(valorcapa>0 && TipoSector.TIPO_SECTOR_MINERIA==verProyectoBean.getProyecto().getTipoSector().getId())
            {	
            	List<String> nombresCapasOmitir=new ArrayList<String>(Arrays.asList("BOSQUES PROTECTORES","PATRIMONIO FORESTAL DEL ESTADO","ZONA DE PROTECCIÓN BOSQUES PROTECTORES"));
            	List<String> nombresCapasProyecto=new ArrayList<String>();
            	List<InterseccionProyecto> listInterseccion= certificadoInterseccionService.getListaInterseccionProyectoIntersecaCapas(verProyectoBean.getProyecto().getCodigo());
            	for (InterseccionProyecto interseccion : listInterseccion) {
            		String nombreCapa=interseccion.getDescripcion().toUpperCase();
            		if(!nombresCapasProyecto.contains(nombreCapa) && !nombresCapasOmitir.contains(nombreCapa))
            			nombresCapasProyecto.add(nombreCapa);            		
				}
            	
            	if(!nombresCapasProyecto.isEmpty()){
            		RequestContext.getCurrentInstance().execute("PF('dialogBloqueoSnapMineria').show();");
                	return;
            	}
            	
            }
            
            if (valorcapa == 2 && !parroquia.getUbicacionesGeografica().getNombre().equals("DISTRITO METROPOLITANO DE QUITO")) {            	
            	finalizar = false;
                LOGGER.info("error");
                JsfUtil.addMessageInfo("Ocurrió un error al guardar el proyecto las coordenadas no corresponden a la ubicacion.");
            	
            }
            else {
                if (verProyectoBean.isCategoriaI()) {
                    verProyectoBean.setMensaje(Constantes.getMessageResourceString(MENSAJE_CATEGORIAI_REGISTRO));
                    verProyectoBean.setPathImagen(verProyectoBean.getProyecto().getRemocionCoberturaVegetal()?IMAGEN_BANDEJA_COBERTURA:IMAGEN_BANDEJA_ENCUESTA);
                    finalizar = iniciarProcesoCategoriaI();
                    if(!finalizar)
                    {
                    	JsfUtil.addMessageError("No se pudo iniciar el proceso de Certificado Ambiental. Comuníquese con mesa de ayuda");
                    	return;
                    }
                	
                    if(certificadoInterseccionService.isProyectoIntersecaCapas(verProyectoBean.getProyecto().getCodigo())){
                    	verProyectoBean.setMensaje(Constantes.getMessageResourceString(valorcapa==2?MENSAJE_CATEGORIAI_INTERSECA_QUEBRADAS:MENSAJE_CATEGORIAI_INTERSECA));
                    	verProyectoBean.setPathImagen(valorcapa==2?IMAGEN_BANDEJA_ENCUESTA:IMAGEN_LISTA_PROYECTOS);
                    }
                    
                } else {
                    finalizar = finalizarRegistroProyectoSuperioACategoriaI();
                }
            }

            if (finalizar) {
           	 if (!verProyectoBean.isCategoriaI()) {
           		 finalizarProyectoSuperiorI();
           		 generarCertificadoInterseccionController.generarOficioCertificadoInterseccion();
           		 
                 if (!verProyectoBean.isCategoriaII() && TipoSector.TIPO_SECTOR_HIDROCARBUROS == verProyectoBean.getProyecto().getTipoSector().getId()) {
                     migrarProyecto();
                 }
           		 
           	 }else{
           		 finalizarProyecto();
           		 generarCertificadoInterseccionController.generarOficioCertificadoInterseccion();
           	 }
                notificarProcesoAdministrativo();
           	List<Contacto> listContacto = new ArrayList<Contacto>();
           	listContacto = contactoFacade
						.buscarUsuarioNativeQuery(loginBean.getUsuario().getNombre());
           	String valorcorreo=null;
			String nombreProponente=null;
			
           	for (Contacto contacto : listContacto) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					valorcorreo=contacto.getValor();
					nombreProponente=contacto.getOrganizacion()!=null?contacto.getOrganizacion().getNombre():contacto.getPersona().getNombre();					
					if(nombreProponente!=null)
					break;
				}
			}
           	NotificacionAutoridadesController mail_a = new NotificacionAutoridadesController();
           	mail_a.sendEmailProponentesTutoriales(verProyectoBean.getLabelProponente(),valorcorreo, "Registro del proyecto", "Este correo fue enviado usando JavaMail", verProyectoBean.getProyecto().getNombre(), verProyectoBean.getProyecto().getCodigo(), loginBean.getUsuario(), loginBean.getUsuario());            											
				Thread.sleep(2000);               
               RequestContext.getCurrentInstance().execute("PF('continuarDialog').show();");
           }
        } catch (Exception e) {
        	JsfUtil.addMessageError(e.getMessage());
            LOGGER.error(e);
        }

    }
    
    public String eliminar() {
        verProyectoBean.getProyecto().setEstado(false);
        verProyectoBean.getProyecto().setMotivoEliminar("Eliminación por parte del proponente ya que no finalizo el registro del proyecto y no inicio las tareas correspondientes para el proyecto");
        proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(verProyectoBean.getProyecto());
        proyectoLicenciamientoAmbientalFacade.eliminarProyecto4Cat(verProyectoBean.getProyecto().getCodigo(), "Eliminación por parte del proponente ya que no finalizo el registro del proyecto y no inicio las tareas correspondientes para el proyecto");
        JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
        return JsfUtil.actionNavigateTo("/proyectos/listadoProyectos.jsf");
    }

    private void finalizarProyecto() {
    	verProyectoBean.getProyecto().setEstadoMapa(true);
        verProyectoBean.getProyecto().setFinalizado(true);
        proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(verProyectoBean.getProyecto());
    }
    
    private void finalizarProyectoSuperiorI() {
        verProyectoBean.getProyecto().setFinalizado(true);
        verProyectoBean.getProyecto().setEstadoMapa(true);
        proyectoLicenciamientoAmbientalFacade.actualizarSimpleProyecto(verProyectoBean.getProyecto());
    }

    public void migrarProyecto() throws Exception {
        System.out.println("=========================================");
        System.out.println(" EJECUTAR MIGRACION PROYECTO: " + verProyectoBean.getProyecto().getCodigo());
        System.out.println("=========================================");
        proyectoLicenciamientoAmbientalFacade.ejecutarMigracionProyecto(verProyectoBean.getProyecto(),
                JsfUtil.getLoggedUser());

    }
    
    private boolean iniciarProcesoCategoriaI() throws ServiceException {
        return categoria1Facade.iniciarProcesoCertificadoAmbiental(loginBean.getUsuario(), verProyectoBean.getProyecto());
    }

    private void iniciarProcesoCategoriaII() throws ServiceException {
        categoriaIIFacade.inicarProcesoCategoriaII(loginBean.getUsuario(), verProyectoBean.getProyecto());
    }

    private void iniciarProcesoLicenciaAmbiental() throws ServiceException {
        licenciaAmbientalFacade.iniciarProcesoLicenciaAmbiental(loginBean.getUsuario(), verProyectoBean.getProyecto());
    }

    public void verProyecto(Integer id) throws CmisAlfrescoException {
        verProyecto(id, false);
    }


//	@EJB
//	private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    
    public void verProyecto(Integer id, boolean mostrarAcciones) throws CmisAlfrescoException {
        try {
            verProyectoBean.setProyecto(proyectoLicenciamientoAmbientalFacade.cargarProyectoFullPorId(id));
           
            /**** pagos 20160303****/
            ProyectoLicenciamientoAmbiental proj = new ProyectoLicenciamientoAmbiental(); 
			proj = pagoConfiguracionesUtil.validaPago(verProyectoBean.getProyecto());
			verProyectoBean.getProyecto().getCatalogoCategoria().getTipoLicenciamiento().setCosto(
					proj.getCatalogoCategoria().getTipoLicenciamiento().getCosto());
			/**** pagos 20160303****/
    		
        } catch (Exception e) {
            LOGGER.error("Error al cargar el proyecto.", e);
            JsfUtil.addMessageError("Ha ocurrido un error recuperando los datos del proyecto.");
            return;
        }
        verProyectoBean.setMostrarAcciones(mostrarAcciones);
        generarCertificadoInterseccionController.validarProcesoGeneracionCertificadoInterseccion(mostrarAcciones);
        if (verProyectoBean.isCategoriaI()) {
            verProyectoBean.setShowModalCertificadoIntercepcion(mostrarAcciones);
            verProyectoBean.setShowModalAceptarResponsabilidad(true);
            JsfUtil.getBean(RecibirCertificadoRegistroAmbientalBean.class).iniciar(id);
            generarCertificadoInterseccionController.iniciarVariablesGeneracionCI();
        } else {
            generarCertificadoInterseccionController.iniciarVariablesGeneracionCI();
        }
        if (verProyectoBean.getProyecto().isMinerosArtesanales()) {
            for (int i = 0; i < verProyectoBean.getProyecto().getProyectoMinerosArtesanales().size(); i++) {
                descargarAlfresco(verProyectoBean.getProyecto().getProyectoMinerosArtesanales().get(i)
                        .getMineroArtesanal().getContratoOperacion());
            }
        }
        if (verProyectoBean.isMineriaArtesanalOLibreAprovechamiento()
                && verProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
            MineroArtesanal mineroArtesanal = verProyectoBean.getProyecto().getMineroArtesanal();
            if (mineroArtesanal != null) {
                descargarAlfresco(mineroArtesanal.getContratoOperacion());
                descargarAlfresco(mineroArtesanal.getRegistroMineroArtesanal());
            }
        }
        if (verProyectoBean.isMineriaArtesanalOLibreAprovechamiento()
                && !verProyectoBean.getProyecto().isMineriaAreasConcesionadas()) {
            MineroArtesanal mineroArtesanal = verProyectoBean.getProyecto().getMineroArtesanal();
            if (mineroArtesanal != null)
                descargarAlfresco(mineroArtesanal.getRegistroMineroArtesanal());
        }
        
        List<ProyectoCamaronera>listProyectoCamaroneras= new ArrayList<ProyectoCamaronera>();
        listProyectoCamaroneras= proyectosCamaronerasFacade.buscarProyectoPorId(verProyectoBean.getProyecto().getId());
    	if(listProyectoCamaroneras!=null){
    		verProyectoBean.setRegistroCamaroneras(true);
    		verProyectoBean.setListProyectoCamaroneras(listProyectoCamaroneras);
    	}
    	else
    		verProyectoBean.setRegistroCamaroneras(false);
        
        try {
            if (loginBean.getUsuario() != null && loginBean.getUsuario().getPersona() != null) {
                verProyectoBean.setNotaResponsabilidadRegistro(getNotaResponsabilidadInformacionRegistroProyecto(loginBean
                        .getUsuario().getPersona(), loginBean.getUsuario().getId()));
            }else{
                verProyectoBean.setNotaResponsabilidadRegistro("");
            }
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al recuperar los datos de proponente.");
            LOGGER.error("Error al recuperar los datos de proponente.", e);
        }

		try {
			//Para actualizacion de certificado de interseccion
			verProyectoBean.setCertificadosInterseccionActualizados(new ArrayList<Documento>());
			List<Documento> documentosActualizados= new ArrayList<Documento>();
	    	List<Integer> listaTipos = new ArrayList<Integer>();
			listaTipos.add(TipoDocumentoSistema.TIPO_COORDENADAS_ACTUALIZACION.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_CI_MAPA_ACTUALIZACION.getIdTipoDocumento());
			listaTipos.add(TipoDocumentoSistema.TIPO_CI_OFICIO_ACTUALIZACION.getIdTipoDocumento());
			
			documentosActualizados = documentosFacade.recuperarDocumentosPorTipo(
					verProyectoBean.getProyecto().getId(), ProyectoLicenciamientoAmbiental.class.getSimpleName(), listaTipos);
	    	verProyectoBean.setCertificadosInterseccionActualizados(documentosActualizados);
	    	
	    	
        } catch (Exception e) {
            JsfUtil.addMessageError("Error al recuperar los datos de proponente.");
            LOGGER.error("Error al recuperar los datos de proponente.", e);
        }
    }
    
    @EJB
    private ProyectosCamaronerasFacade proyectosCamaronerasFacade;


    public String modificar() {
        RegistroProyectoController.setProyectoModificar();
        String type = "Hidrocarburos";
        generarCertificadoInterseccionController.iniciarVariablesGeneracionCI();
        if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_MINERIA)
            type = "Mineria";
        else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_OTROS)
            type = "OtrosSectores";
        else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_ELECTRICO)
            type = "Electrico";
        else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_SANEAMIENTO)
            type = "Saneamiento";
        else if (verProyectoBean.getProyecto().getTipoSector().getId().intValue() == TipoSector.TIPO_SECTOR_TELECOMUNICACIONES)
            type = "Telecomunicaciones";
        try {
            generarCertificadoInterseccionController.eliminarPDFMapaCertificadoInterseccion();
            if(verProyectoBean.listProyectoCamaroneras!=null && verProyectoBean.listProyectoCamaroneras.size()>0){
            	proyectoLicenciamientoAmbientalFacade.actualizarProyectoCamaroneras(verProyectoBean.listProyectoCamaroneras);
            	verProyectoBean.getProyecto().setArea(0.00);
            }
        } catch (Exception e) {
            LOGGER.error("Error al eliminar el PDF del certificado de interseccion", e);
        }
        return JsfUtil.actionNavigateTo("/prevencion/registroProyecto/registro" + type);
    }

    private String getNotaResponsabilidadInformacionRegistroProyecto(Persona persona, Integer idUsuario)
            throws ServiceException {
        String[] parametros = {persona.getNombre(), proyectoLicenciamientoAmbientalFacade.getIdentificacion(idUsuario)};
        return DocumentoPDFPlantillaHtml.getPlantillaConParametros("nota_responsabilidad_certificado_interseccion",
                parametros);
    }

    public Documento descargarAlfresco(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        return documento;
    }
    
    public Documento descargarAlfrescoDocumentoCamaronera(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        
        try {
        	JsfUtil.descargarPdf(documentoContenido, documento.getNombre().replace(".pdf", ""));
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        return documento;
    }

	/****** módulo encuesta (2016-02-05) ******/
	@Setter
	@Getter
	private boolean showSurveyD = false;

	public void showDialogSurvey() {
		showSurveyD = true;
	}
	/****** módulo encuesta (2016-02-05) ******/
	
	
	public Documento descargarDocumentoActualizado(Documento documento) throws CmisAlfrescoException {
        byte[] documentoContenido = null;
        if (documento != null && documento.getIdAlfresco() != null)
            documentoContenido = documentosFacade.descargar(documento.getIdAlfresco());
        if (documentoContenido != null)
            documento.setContenidoDocumento(documentoContenido);
        
        try {
        	if(documento.getNombre().contains("pdf"))
        		JsfUtil.descargarPdf(documentoContenido, documento.getNombre().replace(".pdf", ""));
        	else 
        		UtilDocumento.descargarExcel(documentoContenido,
    					"Coordenadas del proyecto");
        } catch (IOException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
        return documento;
    }
}
