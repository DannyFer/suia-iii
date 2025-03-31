package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.DetalleMineriaBean;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.ImpresionFichaMineriaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalConcesionesMinerasFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalConcesionesMineras;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoBioticoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoFisicoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoSocialFacade;
import ec.gob.ambiente.suia.certificadointerseccion.service.CertificadoInterseccionService;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.dto.*;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ImpresionFichaGeneralFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.*;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author christian
 */
@ManagedBean
@ViewScoped
public class ImpresionFichaMineriaController implements Serializable {

    private static final long serialVersionUID = -5570162253511765252L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImpresionFichaMineriaController.class);
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private CaracteristicasGeneralesMineriaFacade caracteristicasGeneralesMineriaFacade;
    @EJB
    private DescripcionActividadMineriaFacade descripcionActividadMineriaFacade;
    @EJB
    private CatalogoFisicoFacade catalogoFisicoFacade;
    @EJB
    private CatalogoBioticoFacade catalogoBioticoFacade;
    @EJB
    private CatalogoSocialFacade catalogoSocialFacade;
    @EJB
    private ImpresionFichaGeneralFacade impresionFichaGeneralFacade;
    @EJB
    private ActividadMineriaFacade actividadMineriaFacade;
    @EJB
    private MatrizAmbientalMineriaFacade matrizAmbientalMineriaFacade;
    @EJB
    private OrganizacionFacade organizacionFacade;
    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;

    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;

    @Getter
    @Setter
    private ImpresionFichaMineriaBean impresionFichaMineriaBean;
    @Getter
    @Setter
    private String ss;
    @Getter
    @Setter
    @ManagedProperty(value = "#{descripcionActividadMineraController}")
    private DescripcionActividadMineraController descripcionActividadMineraController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{caracterisiticasAreaInfluenciaMineriaController}")
    private CaracterisiticasAreaInfluenciaMineriaController caracterisiticasAreaInfluenciaMineriaController;
    @Getter
    @Setter
    @ManagedProperty(value = "#{detalleMineriaBean}")
    private DetalleMineriaBean detalleMineriaBean;
    private static final String CANTIDAD = "Cantidad";
    private static final String DESCRIPCION = "Descripcion";
    private static final String DESCRIPCION_TILDE = "Descripción";
    private static final String NOMBRE = "Nombre";
    private static final String TAG = "<span style=\"font-size: small\">";
    private static final String TAG1 = "<br/></span>";
    @EJB
    private CrudServiceBean crudServiceBean;
    

    @PostConstruct
    public void cargarDatos() {
        try {
            setImpresionFichaMineriaBean(new ImpresionFichaMineriaBean());
            getImpresionFichaMineriaBean().iniciarDatos();
            getImpresionFichaMineriaBean().setFichaAmbientalMineria(
                    fichaAmbientalMineriaFacade
                    .obtenerPorId(getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getId()));
            String codigoCatalogo=getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo();
            codigoActualizarScdr=fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental()).getCodeUpdate();
            if(codigoActualizarScdr==null)
            	codigoActualizarScdr="0";
            
            if (codigoCatalogo.equals("21.02.03.05") || codigoCatalogo.equals("21.02.04.03") || codigoCatalogo.equals("21.02.05.03") || codigoCatalogo.equals("21.02.02.03") || codigoActualizarScdr.equals("21.02.02.01") || codigoActualizarScdr.equals("21.02.03.06"))
        	{
            	getImpresionFichaMineriaBean()
            	.setPlantillaHtml(
            			UtilFichaMineria.extraeHtml(JsfUtil
            					.devolverPathReportesHtml("fichaAmbientalMineria020.html")));

        	}
            else
            {
            	getImpresionFichaMineriaBean()
            	.setPlantillaHtml(
            			UtilFichaMineria.extraeHtml(JsfUtil
            					.devolverPathReportesHtml("fichaAmbientalMineria.html")));
            }
            obtenerContacto();
        } catch (Exception e) {
            LOG.error(e, e);
            JsfUtil.addMessageError(e.getMessage());
        }
    }

    public void guardar() {
        configurarDatosExportar();
        String[] parametros = {
            getImpresionFichaMineriaBean().getFichaAmbientalMineria()
            .getProyectoLicenciamientoAmbiental().getCodigo(),
            JsfUtil.devuelveFechaEnLetrasSinHora(new Date())};

        UtilFichaMineria.generarHtmlPdf(getImpresionFichaMineriaBean()
                .getPlantillaHtml(), getImpresionFichaMineriaBean()
                .getEntityFichaMineriaReporte(), "fichaMnieria", true, parametros);
    }

    public File cargarDatosPdfFile() {
        configurarDatosExportar();
        String fecha;
        String codigoMineria=getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo();
        if(codigoMineria.equals("21.02.03.05") ||  codigoMineria.equals("21.02.04.03") || codigoMineria.equals("21.02.05.03") || codigoMineria.equals("21.02.02.03") || codigoActualizarScdr.equals("21.02.02.01") || codigoActualizarScdr.equals("21.02.03.06")){
            fecha="Quito 11 de Junio de 2020";//+ JsfUtil.devuelveFechaEnLetrasSinHora(new Date());
        }else{
//            fecha=JsfUtil.devuelveFechaEnLetrasSinHora(new Date());
        	fecha="11 de Junio de 2020";
        }
        
        String[] parametros = {
                getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                .getProyectoLicenciamientoAmbiental().getCodigo(),
                fecha};
        
        Area area = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                .getProyectoLicenciamientoAmbiental().getAreaResponsable();
        
        Object x;
        if(codigoMineria.equals("21.02.03.05") ||  codigoMineria.equals("21.02.04.03") || codigoMineria.equals("21.02.05.03") || codigoMineria.equals("21.02.02.03") || codigoActualizarScdr.equals("21.02.02.01") || codigoActualizarScdr.equals("21.02.03.06"))        
        	x=	getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte();
        else
        	x=getImpresionFichaMineriaBean().getEntityFichaMineriaReporte();
        
        return UtilFichaMineria.generarFichero(getImpresionFichaMineriaBean()
                .getPlantillaHtml(), x, "fichaMnieria", true, area, parametros);

    }
    
    @Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
    
    @Setter
    @Getter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
    
    public File cargarDatosPdfFileRCOA() {
    	proyectoLicenciaCoa=proyectosBean.getProyectoRcoa();    	
    	cargaDatosMineria020RCOA();
		String fecha;
		fecha = "Quito " + JsfUtil.devuelveFechaEnLetrasSinHora(new Date());
		String[] parametros = { proyectoLicenciaCoa.getCodigoUnicoAmbiental(), fecha };
		Area area = proyectoLicenciaCoa.getAreaResponsable();
		Object x = getImpresionFichaMineriaBean().getEntityFichaMineriaReporte();		
		String cadenaHtml = UtilFichaMineria.extraeHtml(JsfUtil.devolverPathReportesHtml("fichaAmbientalMineria020.html"));
		return UtilFichaMineria.generarFichero(cadenaHtml, x, "fichaMnieria", true, area, parametros);
    }

    private void configurarDatosExportar() {
        try {


            Area areaResponsable = getImpresionFichaMineriaBean()
                    .getFichaAmbientalMineria()
                    .getProyectoLicenciamientoAmbiental().getAreaResponsable();
            if (areaResponsable != null) {
                String nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
                String nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
                URL pie = UtilDocumento.getRecursoImage("ente/" + nombre_pie);
                if (pie == null) {
                    try {
                        System.out.println(nombre_pie);
                        byte[] pie_datos = alfrescoServiceBean.downloadDocumentByNameAndFolder(nombre_pie, Constantes.getRootStaticDocumentsId(), true);
                        File archivo = new File(
                                JsfUtil.devolverPathImagenEnte(nombre_pie));
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(pie_datos);
                        file.close();
                    } catch (Exception e) {
                       // LOG.error("Error al obtener la imagen del pie para el área " + areaResponsable.getAreaAbbreviation(), e);
                    }
                }

                URL logo = UtilDocumento.getRecursoImage("ente/" + nombre_logo);
                if (logo == null) {
                    try {
                        System.out.println(nombre_logo);
                        byte[] logo_datos = alfrescoServiceBean.downloadDocumentByNameAndFolder(nombre_logo, Constantes.getRootStaticDocumentsId(), true);
                        File archivo = new File(
                                JsfUtil.devolverPathImagenEnte(nombre_logo));
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(logo_datos);
                        file.close();
                    } catch (Exception e) {
                       // LOG.error("Error al obtener la imagen del logo para el área " + areaResponsable.getAreaAbbreviation(), e);
                    }
                }
            }
            String codigoMineria=getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getCatalogoCategoria().getCodigo();
            if(codigoMineria.equals("21.02.03.05") ||  codigoMineria.equals("21.02.04.03") || codigoMineria.equals("21.02.05.03") || codigoMineria.equals("21.02.02.03") || codigoActualizarScdr.equals("21.02.02.01") || codigoActualizarScdr.equals("21.02.03.06"))
            {
            	cargaDatosMineria020();
            	return;
            }
            	
            DateFormat patronFecha = new SimpleDateFormat("yyyy-MM-dd");
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setNombreTitular(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental()
                            .getUsuario().getPersona().getNombre());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCodigo(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental()
                            .getUsuario().getPersona().getId()
                            .toString());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setFechaOtorgamientoPermiso(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getFechaEmisionPermiso() == null ? ""
                                    : patronFecha
                                    .format(getImpresionFichaMineriaBean()
                                            .getFichaAmbientalMineria()
                                            .getFechaEmisionPermiso()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setFechaInscripcionContrato(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getFecharegistroContrato() == null ? ""
                                    : patronFecha
                                    .format(getImpresionFichaMineriaBean()
                                            .getFichaAmbientalMineria()
                                            .getFecharegistroContrato()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setFechaPresentacionFicha(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getFechaPresentacionFichaAmbiental() == null ? ""
                                    : patronFecha
                                    .format(getImpresionFichaMineriaBean()
                                            .getFichaAmbientalMineria()
                                            .getFechaPresentacionFichaAmbiental()));

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPlazoDuracionPermiso(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getDuracionPermiso() == null ? ""
                                    : getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getDuracionPermiso().toString());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPlazoDuracionContrato(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getDuracionContrato() == null ? ""
                                    : getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getDuracionContrato().toString());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setObservaciones(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getObservaciones());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setDireccion(getImpresionFichaMineriaBean().getDireccion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTelefono(getImpresionFichaMineriaBean().getTelefono());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setFax(getImpresionFichaMineriaBean().getFax());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCorreoElectronico(
                            getImpresionFichaMineriaBean().getEmail());
            UbicacionesGeografica ubicacionesGeografica = obtenerUbicacionContacto(getImpresionFichaMineriaBean()
                    .getFichaAmbientalMineria()
                    .getProyectoLicenciamientoAmbiental().getUsuario()
                    .getPersona().getIdUbicacionGeografica());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setProvincia(
                            ubicacionesGeografica.getUbicacionesGeografica()
                            .getUbicacionesGeografica().getNombre());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCiudad(
                            ubicacionesGeografica.getUbicacionesGeografica()
                            .getNombre());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setBarrioSector(ubicacionesGeografica.getNombre());
            List<UbicacionesGeografica> listaUbicacionProyecto = ubicacionGeograficaFacade
                    .listarPorProyecto(getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental());
            List<EntityUbicacionReporte> listaUbicacion = new ArrayList<EntityUbicacionReporte>();
            StringBuilder concatenaRegiones = new StringBuilder();
            for (UbicacionesGeografica ubi : listaUbicacionProyecto) {
                EntityUbicacionReporte obj = new EntityUbicacionReporte();
                obj.setProvincia(ubi.getUbicacionesGeografica()
                        .getUbicacionesGeografica().getNombre());
                obj.setCanton(ubi.getUbicacionesGeografica().getNombre());
                obj.setParroquia(ubi.getNombre());
                listaUbicacion.add(obj);
                concatenaRegiones
                        .append(TAG)
                        .append(ubi.getUbicacionesGeografica()
                                .getUbicacionesGeografica().getRegion()
                                .getNombre()).append(TAG1);
            }
            String[] columnas = {"Provincia", "Cantón", "Parroquia"};
            String[] detalle = {"Provincia", "Canton", "Parroquia"};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setDetalleUbicacionGeografica(
                            UtilFichaMineria.devolverDetalle(null, columnas,
                                    listaUbicacion, detalle, null));
            List<EntityCoordenadasMineriaReporte> listaCoordenadas = new ArrayList<EntityCoordenadasMineriaReporte>();
            for (FormaProyecto f : getImpresionFichaMineriaBean()
                    .getFichaAmbientalMineria()
                    .getProyectoLicenciamientoAmbiental().getFormasProyectos()) {
                for (Coordenada c : f.getCoordenadas()) {
                    EntityCoordenadasMineriaReporte obj = new EntityCoordenadasMineriaReporte();
                    obj.setNorte(c.getX().toString());
                    obj.setEste(c.getY().toString());
                    listaCoordenadas.add(obj);
                }
            }
            String[] columnasCoordenadas = {"Norte", "Este"};
            String[] detalleCoordenadas = {"Norte", "Este"};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setDetalleCoordenadas(
                            UtilFichaMineria.devolverDetalle(null,
                                    columnasCoordenadas, listaCoordenadas,
                                    detalleCoordenadas, null));
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setMetalico(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getTipoMaterial().getNombre());
            List<CatalogoTipoMaterial> lista = caracteristicasGeneralesMineriaFacade
                    .listarPorTipoMaterialFichaAmbiental(getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria());
            StringBuilder concatena = new StringBuilder();
            for (CatalogoTipoMaterial c : lista) {
                concatena.append(TAG)
                        .append(c.getNombre()).append(TAG1);
            }
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCaracteristica(concatena.toString());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setPredio(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getOtrosPredios() != null ? getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getOtrosPredios()
                                    : getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getPredio().getDescripcion());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setEtapa(getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getEtapa().getDescripcion());
            DescripcionActividadMineria des = descripcionActividadMineriaFacade
                    .obtenerPorFichaMineria(getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setNumeroPersonas(
                            des.getNumeroPersonasLaboran().toString());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setNombreCodigoConsesion(des.getNumeroConcesion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setMontoInversion(des.getMontoInversion().toString());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setVolumenProduccion(des.getVolumenProduccionDiario());
            List<EntityActividadProceso> listaActividades = new ArrayList<EntityActividadProceso>();
            for (ActividadMinera a : getDescripcionActividadMineraController().getDescripcionActividadMineraBean().getListaActividadesSeleccionadas()) {
                EntityActividadProceso obj = new EntityActividadProceso();
                obj.setDescripcion(a.getDescripcion());
                obj.setDuracion(a.getDiasDuracion().toString());
                obj.setNombre(a.getActividadComercial().getNombreActividad());
                listaActividades.add(obj);
            }
            columnas = new String[]{"Actividad", "Duración (días)", DESCRIPCION_TILDE};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setDetalleActividades(UtilFichaMineria.devolverDetalle(null, columnas, listaActividades, new String[]{NOMBRE, "Duracion", DESCRIPCION}, null));

            List<EntityInstalacion> listaInstalaciones = new ArrayList<EntityInstalacion>();
            for (Instalacion a : getDescripcionActividadMineraController().getDescripcionActividadMineraBean().getListaInstalaciones()) {
                EntityInstalacion obj = new EntityInstalacion();
                obj.setDescripcion(a.getDescripcion());
                obj.setNombre(a.getCatalogoInstalacion().getNombre());
                listaInstalaciones.add(obj);
            }
            columnas = new String[]{NOMBRE, DESCRIPCION_TILDE};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setDetalleInstalaciones(UtilFichaMineria.devolverDetalle(null, columnas, listaInstalaciones, new String[]{NOMBRE, DESCRIPCION}, null));

            List<EntityHerramientaMineria> listaHerramientas = new ArrayList<EntityHerramientaMineria>();
            for (HerramientaMinera a : getDescripcionActividadMineraController().getDescripcionActividadMineraBean().getListaHerramientas()) {
                EntityHerramientaMineria obj = new EntityHerramientaMineria();
                obj.setCantidad(a.getCantidadHerramientas().toString());
                obj.setMateriales(a.getDescripcion()!=null?"Otro: "+a.getDescripcion():a.getCatalogoHerramienta().getDescripcion());
                obj.setProceso(a.getCatalogoHerramienta().getProcesoMinero().getDescripcion());
                obj.setTipoObtencion(a.getCatalogoHerramienta().getProcesoMinero().getCatalogoTipoMaterial().getNombre());
                listaHerramientas.add(obj);
            }
            columnas = new String[]{"Tipo obtención", "Proceso", "Materiales", CANTIDAD};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setDetalleHerramientas(UtilFichaMineria.devolverDetalle(null, columnas, listaHerramientas, new String[]{"TipoObtencion", "Proceso", "Materiales", CANTIDAD}, null));

            List<EntityInsumos> listaInsumos = new ArrayList<EntityInsumos>();
            for (FichaMineriaInsumos a : getDescripcionActividadMineraController().getDescripcionActividadMineraBean().getListaInsumosAgregados()) {
                EntityInsumos obj = new EntityInsumos();
                obj.setCantidad(a.getCantidadHijoInsumo().toString() + " " + a.getUnidadMedida().getSiglas());
                obj.setInsumo(a.getCatalogoInsumo().getDescripcion());
                if ("Otros".equalsIgnoreCase(a.getCatalogoHijoInsumo().getDescripcion())) {
                    obj.setMaterial(a.getHijoInsumoOtro());
                } else {
                    obj.setMaterial(a.getCatalogoHijoInsumo().getDescripcion());
                }
                listaInsumos.add(obj);
            }
            columnas = new String[]{"Insumo", "Material", CANTIDAD};
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setDetalleInsumos(UtilFichaMineria.devolverDetalle(null, columnas, listaInsumos, columnas, null));

            List<EntityMatrizImpactosMineria> listaMatriz = new ArrayList<EntityMatrizImpactosMineria>();
            List<ActividadMinera> listaActividadMinera = actividadMineriaFacade.listarPorFichaAmbiental(getImpresionFichaMineriaBean().getFichaAmbientalMineria());
            List<MatrizAmbientalMineria> listaMatrizAmbientalMineria = matrizAmbientalMineriaFacade.listarPorFicha(getImpresionFichaMineriaBean().getFichaAmbientalMineria());
            if (listaMatrizAmbientalMineria != null && !listaMatrizAmbientalMineria.isEmpty()) {
                StringBuilder matriz = new StringBuilder();
                for (ActividadMinera a : listaActividadMinera) {
                    for (MatrizAmbientalMineria m : listaMatrizAmbientalMineria) {
                        if (a.getId().equals(m.getIdActividadMineria())) {
                            a.setListaMatrizFactorImpacto(matrizAmbientalMineriaFacade.listarPorMatriz(m.getId()));
                            break;
                        }
                    }

                    String cabeza = "Fase: " + a.getActividadComercial().getCategoriaFase().getFase().getNombre() + " - Actividad: " + a.getActividadComercial().getNombreActividad();
                    for (MatrizFactorImpacto m : a.getListaMatrizFactorImpacto()) {
                        EntityMatrizImpactosMineria obj = new EntityMatrizImpactosMineria();
                        obj.setFactor(m.getFactorPma().getNombre());
                        obj.setImpacto(m.getImpactoPma().getNombre());
                        listaMatriz.add(obj);
                    }
                    columnas = new String[]{"Factor", "Impacto"};
                    matriz.append(UtilFichaMineria.devolverDetalle(cabeza, columnas, listaMatriz, columnas, null));
                    listaMatriz = new ArrayList<EntityMatrizImpactosMineria>();
                }
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setMatrizImpactosAmbientales(matriz.toString());
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setPma(obtenerCronogramaValorado(getImpresionFichaMineriaBean().getFichaAmbientalMineria().getId()));

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setInventarioForestal(obtenerInventarioForestal(getImpresionFichaMineriaBean().getFichaAmbientalMineria().getId()));

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setNumeroLicencia(
                            des.getNumeroObservacionLicenciaAmbiental());
            String observaciones="";
            String observacionesTitulo="";
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("fichaAmbientalMineria", getImpresionFichaMineriaBean().getFichaAmbientalMineria());

            List<CronogramaValoradoPma> listaPma = crudServiceBean.findByNamedQueryGeneric(CronogramaValoradoPma.OBTENER_POR_FICHA_MINERIA,parametros);
            if(listaPma.size()>0){
                for (CronogramaValoradoPma c1 : listaPma) {
                    if(c1.getPlan()==null){
                    observaciones=c1.getActividad();
                    if (!c1.getActividad().equals(""))
                    	observacionesTitulo="Observaciones";
                    }
                }
            }
            
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte().setObservacionesPMA("<div style=\"text-align: center;\">"
                    + "<span style=\"font-weight: bold;font-size: small\">"+observacionesTitulo+"<br/></span></div>"
                    + "<div style=\"text-align: justify;\"> <p style=\"font-size: small; text-align: justify;\">"+observaciones+"<br/></p></div>");
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setRegionGeografica(concatenaRegiones.toString());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setSuperficieArea(
                            String.valueOf(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getArea()) + " " + getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental()
                            .getUnidad());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setAltitud(
                            getCaracterisiticasAreaInfluenciaMineriaController().getCaracterisiticasAreaInfluenciaMineriaBean().getCatalogoAltura().getDescripcion());

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setDescripcionGeneralClima(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getClima() != null
                                    ? getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria().getClima().getDescripcion() : getCaracterisiticasAreaInfluenciaMineriaController().getCaracterisiticasAreaInfluenciaMineriaBean().getCatalogoClima().getDescripcion());

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setOcupacionAreaInfluencia(
                            obtenerColumnaCatalogoFisico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getAreaInfluencia()));

            String otraAreaInfluencia = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosAreaInfluencia();

            if (otraAreaInfluencia != null && !otraAreaInfluencia.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setOcupacionAreaInfluencia(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getOcupacionAreaInfluencia().replace("Otra", otraAreaInfluencia));
            }

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setPendienteSuelo(
                            obtenerColumnaCatalogoFisico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getPendienteSuelo()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setTipoSuelo(
                            obtenerColumnaCatalogoFisico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria().getTipoSuelo()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setCalidadSuelo(
                            obtenerColumnaCatalogoFisico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCalidadSuelo()));

            String otroCalidadSuelo = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosCalidadSuelo();
            if (otroCalidadSuelo != null && !otroCalidadSuelo.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setCalidadSuelo(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getCalidadSuelo().replace("Otro", otroCalidadSuelo));
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPermeabilidadSuelo(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPermeabilidadSuelo().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCondicionesDrenaje(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getCondicionesDrenaje().getDescripcion());

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setRecursoHidrico(
                            obtenerColumnaCatalogoFisico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getRecursosHidricos()));
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setNivelFreatico(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getNivelFreatico().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPrecipitaciones(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPrecipitacionesAgua().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCaracteristicasAgua(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getCaracteristicasAgua().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setFuentesContaminacionAgua(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getDescripcionFuentesContaminacionAgua());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setCaracteristicasAire(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getCaracteristicasAire().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setRecirculacionAire(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getRecirculacionAire().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setRuidoAire(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getRuido()
                            .getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setFuentesGeneracionRuido(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getDescripcionFuentesRuido());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setFormacionV(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getFormacionVegetal().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setHabitad(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getHabitat()
                            .getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTipoBosque(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getTipoBosque()
                            .getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setGradoIntervencion(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getGradoIntervencion().getDescripcion());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setAreasIntervenidas(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoAreasIntervenidas()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setDatosEcologicos(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoDatosEcologicos()));
            String otros = getImpresionFichaMineriaBean()
                    .getFichaAmbientalMineria().getOtrosUsosFlora() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosUsosFlora();
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setUsoRecurso(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoUsoRecurso()));

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setUsoRecurso(getImpresionFichaMineriaBean()
                            .getEntityFichaMineriaReporte()
                            .getUsoRecurso().replace("Otro", otros));

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPisoZoogeografico(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPisoZoogeografico().getDescripcion());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setComponenteBioticoFauna(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoComponenteBiotico()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setSensibilidad(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoSensibilidad()));
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setDatosEcologicosFauna(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoDatosEcologicosFauna()));
            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosUsosFauna() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosUsosFauna();
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setUsoRecursoFauna(
                            obtenerColumnaCatalogoBiotico(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoUsoRecursoFauna())
                            + " " + otros);

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setConsolidacionArea(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental().getTipoPoblacion().getNombre());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTamanioPoblacion(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getTamanioPoblacion().getDescripcion());
            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setComposicionEtnica(
                            obtenerColumnaCatalogoSocial(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoComposicionEtnica()));

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosComposicionEtnica();
            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setComposicionEtnica(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getComposicionEtnica().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setAbastecimientoAgua(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getAbastecimientoAgua().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setEvacuacionAguasServidas(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getEvacuacionAguasServidas()
                            .getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setEvacuacionAguasLluvia(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getEvacuacionAguasLluvia()
                            .getDescripcion());

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosDesechoSolido() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosDesechoSolido();

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setDesechosSolidos(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getDesechosSolidos().getDescripcion()
                    );

            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                        .setDesechosSolidos(getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                                .getDesechosSolidos().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setElectrificacion(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getElectrificacion().getDescripcion());
            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosTransportePublico() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosTransportePublico();
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTransportePublico(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getTransportePublico().getDescripcion()
                    );
            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                        .setTransportePublico(getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                                .getTransportePublico().replace("Otro", otros));
            }

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosVialidad() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosVialidad();

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setViabilidadAcessos(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getVialidadYAcceso().getDescripcion()
                    );
            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                        .setViabilidadAcessos(getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                                .getViabilidadAcessos().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTelefonia(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria().getTelefonia()
                            .getDescripcion());
            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosAprovechamientoTierra() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosAprovechamientoTierra();
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setAprovechamientoTierra(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getAprovechamientoTierra()
                            .getDescripcion()
                    );

            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                        .setAprovechamientoTierra(getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                                .getAprovechamientoTierra().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setTenenciaTierra(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getTenenciaTierra().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setOrganizacionSocial(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getOrganizacionSocial().getDescripcion());

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosOrganizacionSocial();

            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                        .setOrganizacionSocial(getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                                .getOrganizacionSocial().replace("Otra", otros));
            }

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setLengua(
                            obtenerColumnaCatalogoSocial(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoLengua()));

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosLengua();

            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setLengua(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getLengua().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setReligion(
                            obtenerColumnaCatalogoSocial(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoReligion()));

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosReligion();
            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setReligion(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getReligion().replaceAll("Otro", otros));
            }

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setTradiciones(
                            obtenerColumnaCatalogoSocial(getImpresionFichaMineriaBean()
                                    .getFichaAmbientalMineria()
                                    .getCatalogoTradiciones())
                    );

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria().getOtrosTradiciones();

            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setTradiciones(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getTradiciones().replace("Otra", otros));
            }

            otros = getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                    .getOtrosPaisaje() == null ? ""
                            : getImpresionFichaMineriaBean().getFichaAmbientalMineria()
                            .getOtrosPaisaje();
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPaisajeTurismo(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPaisajeTurismo().getDescripcion()
                    );
            if (otros != null && !otros.isEmpty()) {
                getImpresionFichaMineriaBean()
                        .getEntityFichaMineriaReporte()
                        .setPaisajeTurismo(getImpresionFichaMineriaBean()
                                .getEntityFichaMineriaReporte()
                                .getPaisajeTurismo().replace("Otro", otros));
            }

            getImpresionFichaMineriaBean()
                    .getEntityFichaMineriaReporte()
                    .setPeligroDeslizamiento(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPeligroDeslizamiento().getDescripcion());
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPeligroInundaciones(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPeligroInundacion().getDescripcion()
                    );
            getImpresionFichaMineriaBean().getEntityFichaMineriaReporte()
                    .setPeligroTerremotos(
                            getImpresionFichaMineriaBean()
                            .getFichaAmbientalMineria()
                            .getPeligroTerremoto().getDescripcion()
                    );

        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    @SuppressWarnings("deprecation")
	public void obtenerContacto() {
        try {
            List<Contacto> contactos = null;

            Organizacion org = organizacionFacade.buscarPorPersona(getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental().getUsuario().getPersona());
            if (org == null) {
                contactos = contactoFacade
                        .buscarPorPersona(getImpresionFichaMineriaBean()
                                .getFichaAmbientalMineria()
                                .getProyectoLicenciamientoAmbiental().getUsuario()
                                .getPersona());

            } else {
                contactos = contactoFacade.buscarPorOrganizacion(org);
            }
            for (Contacto contacto : contactos) {
                if (contacto.getFormasContacto().getId()
                        .equals(FormasContacto.DIRECCION)) {
                    getImpresionFichaMineriaBean().setDireccion(
                            contacto.getValor());
                } else if (contacto.getFormasContacto().getId()
                        .equals(FormasContacto.TELEFONO)) {
                    getImpresionFichaMineriaBean().setTelefono(
                            contacto.getValor());
                } else if (contacto.getFormasContacto().getId()
                        .equals(FormasContacto.FAX)) {
                    getImpresionFichaMineriaBean().setFax(contacto.getValor());
                } else if (contacto.getFormasContacto().getId()
                        .equals(FormasContacto.EMAIL)) {
                    getImpresionFichaMineriaBean()
                            .setEmail(contacto.getValor());
                }
            }
        } catch (ServiceException e) {
            LOG.error("Error al obtener contacto: ", e);
        }
    }

    public UbicacionesGeografica obtenerUbicacionContacto(Integer idUbicacion) {
        try {
            return ubicacionGeograficaFacade.buscarPorId(idUbicacion);
        } catch (ServiceException e) {
            LOG.info("Error al consultar ubicacion geografica del contacto", e);
            return null;
        }
    }

    private String obtenerColumnaCatalogoFisico(String ids) {
        List<CatalogoGeneralFisico> listaFisco = catalogoFisicoFacade
                .obtenerListaFisicoIds(ids);
        StringBuilder concatena = new StringBuilder();
        for (CatalogoGeneralFisico c : listaFisco) {
            concatena.append(TAG)
                    .append(c.getDescripcion()).append(TAG1);
        }
        return concatena.toString();
    }

    private String obtenerColumnaCatalogoBiotico(String ids) {
        List<CatalogoGeneralBiotico> listaFisco = catalogoBioticoFacade
                .obtenerListaBioticoIds(ids);
        StringBuilder concatena = new StringBuilder();
        for (CatalogoGeneralBiotico c : listaFisco) {
            concatena.append(TAG)
                    .append(c.getDescripcion()).append(TAG1);
        }
        return concatena.toString();
    }

    private String obtenerColumnaCatalogoSocial(String ids) {
        List<CatalogoGeneralSocial> listaFisco = catalogoSocialFacade
                .obtenerListaSocialIds(ids);
        StringBuilder concatena = new StringBuilder();
        for (CatalogoGeneralSocial c : listaFisco) {
            concatena.append(TAG)
                    .append(c.getDescripcion()).append(TAG1);
        }
        return concatena.toString();
    }

    private String obtenerCronogramaValorado(Integer idFicha) {
        try {
            List<EntityDetalleCronogramaValorado> listaDetalles = impresionFichaGeneralFacade
                    .obtenerDetalleCronogramaValoradoPOrFichaMineria(idFicha);
            String plan = listaDetalles.get(0).getPlan();
            String planIngresoDatos = listaDetalles.get(0).getIngresaDatos();
            String[] columnas = {"Actividad", "Responsable", "Fecha desde",
                    "Fecha hasta", "Presupuesto", "Justificativo", "Frecuencia"};	// cambio periodicidad
            String[] ordenColumnas = {"Actividad", "Responsable",
                    "FechaInicio", "FechaFin", "Presupuesto","Justificativo", "Frecuencia"};	// cambio periodicidad
            
            String[] columnas3 = {"Actividad", "Responsable", "Fecha desde",
                    "Fecha hasta", "Presupuesto","Justificativo", "Frecuencia"};
            String[] ordenColumnas3 = {"Actividad", "Responsable",
                    "FechaInicio", "FechaFin", "Presupuesto", "Justificativo","Frecuencia"};
            String[] columnas1 = {"Requiere un plan de manejo ambiental la actividad?","Justificativo técnico"};
            String[] ordenColumnas1 = {"IngresaDatos","Observacion"};
            
            String html = "";
            int i = 0;
            Double total = 0.0;
            List<EntityDetalleCronogramaValorado> listaAux = new ArrayList<EntityDetalleCronogramaValorado>();
            for (EntityDetalleCronogramaValorado e : listaDetalles) {
                i++;
                double presupuesto = Double.valueOf(e.getPresupuesto());
                total += presupuesto;
                e.setPresupuesto(DecimalFormat.getCurrencyInstance(Locale.US)
                        .format(presupuesto));
                e.setFechaFin(e.getFechaFin());
                e.setFechaInicio(e.getFechaInicio());
                if (plan.equals(e.getPlan())) {
                    listaAux.add(e);
                } else {
                	if (planIngresoDatos.equals("NO")){
                		columnas=columnas1;
                		ordenColumnas=ordenColumnas1;
                	}else{
                		columnas=columnas3;
                		ordenColumnas=ordenColumnas3;
                	}
                    html += cargarListaPorCabecera(plan, columnas, listaAux,
                            ordenColumnas);
                    plan = e.getPlan();
                    planIngresoDatos=e.getIngresaDatos();
                    listaAux = new ArrayList<EntityDetalleCronogramaValorado>();
                    listaAux.add(e);
                }
                if (i == listaDetalles.size()) {
                	if (e.getIngresaDatos().equals("NO")){
                		columnas=columnas1;
                		ordenColumnas=ordenColumnas1;
                	}else{
                		columnas=columnas3;
                		ordenColumnas=ordenColumnas3;
                	}    
                    html += cargarListaPorCabecera(plan, columnas, listaAux,
                            ordenColumnas);
                }
            }
            html += cargarTotal(String.valueOf(DecimalFormat
                    .getCurrencyInstance(Locale.US).format(total)));
            return html;
        } catch (Exception e) {
            LOG.error(e, e);
            return null;
        }
    }

    private String cargarListaPorCabecera(final String cabecera,
            final String[] columnas, final List<? extends Object> listaFilas,
            final String[] ordenColumnas) {
        return UtilFichaMineria.devolverDetalle(cabecera, columnas, listaFilas,
                ordenColumnas, null);
    }

    private String cargarTotal(final String total) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>&nbsp;</p><table align=\"left\" border=\"0\" cellpadding=\"2\" cellspacing=\"1\" style=\"width:100%\">");
        sb.append("<tbody><tr>");
        sb.append("<td style=\"width: ")
                .append(100)
                .append("%; text-align: right\"> <span style=\"font-size: small;font-weight: bold;background-color: inherit;\">");
        sb.append("Total: ").append(total);
        sb.append("<br/></span></td>");
        sb.append("</tr>");
        sb.append("</tbody></table><p>&nbsp;</p>");
        return sb.toString();
    }

    private String obtenerInventarioForestal(Integer idFicha) {
        try {
            InventarioForestalPma inventarioForestal = inventarioForestalPmaFacade.obtenerInventarioForestalPmaMineriaPorFicha(idFicha);
            if (inventarioForestal == null) {
                return "";
            } else if (inventarioForestal.getRemocionVegetal()==true) {
                //Si tiene remoción de cobertura vegetal nativa
                StringBuilder sb = new StringBuilder();
                sb.append("<div style=\"text-align: left;\">");
                sb.append("<span style=\"font-weight: bold;\">8. INVENTARIO FORESTAL<br/></span></div><br/>");
                sb.append("Cantidad de madera en pie: ");
                sb.append(inventarioForestal.getMaderaEnPie());
                sb.append(" m3 <br/>");
                sb.append("Valor a pagar: ");
                sb.append(Float.parseFloat(Constantes.getPropertyAsString("costo.factor.covertura.vegetal")) * inventarioForestal.getMaderaEnPie());
                sb.append(" USD <br/>");
                return sb.toString();
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
        return "";
    }
    
    /*
     * Walter carga de datos para la ficha de mineria 020
     */
	@EJB
	private FichaAmbientalMineria020Facade fichaAmbientalMineria020Facade;
	@EJB
	private ComponenteFisicoSDFacade componenteFisicoFacade;
	@EJB
	private ComponenteSocialSDFacade componenteSocialFacade;
	@EJB
	private ComponenteBioticoSDFacade componenteBioticoFacade;
    @EJB
    private ActividadesImpactoFacade actividadesImpactoFacade;
	@EJB
	private PlanManejoAmbiental020Facade planManejoAmbiental020Facade;
	@EJB
	private CertificadoInterseccionService certificadoInterseccionService;
	private Map<String, List<CatalogoGeneralBiotico>> listaGeneralBiotico;
	private List<CatalogoGeneralBiotico> coberturaVegetalList, pisosZooGeograficosList, componenteBioticoNativoList, componenteBioticoIntroducidoList,
	aspectosEcologicosList, areasSensiblesList, coberturaVegetalPadreList;
	private List<ComponenteBioticoSD> listaBioticoCoberturaVegetal, listaBioticoPisosZooGeograficos, listaBioticoNativo, listaBioticoIntroducido, 
	listaBioticoAspectoEcologico, listaBioticoAreasSensibles;
	private String codigoActualizarScdr="";
	
    public void cargaDatosMineria020()
    {
    	ProyectoLicenciamientoAmbiental proyecto=getImpresionFichaMineriaBean().getFichaAmbientalMineria().getProyectoLicenciamientoAmbiental();
		PerforacionExplorativa perforacionExplorativa = null;
		try {
			perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		String titulo="";
		if(perforacionExplorativa.getCodeUpdate()==null || perforacionExplorativa.getCodeUpdate().equals(""))
			titulo="REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";
		else
			titulo="ACTUALIZACIÓN DEL REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitulo(titulo);
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setNombre_proyecto(proyecto.getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setNombre_catalogo(proyecto.getCatalogoCategoria().getDescripcion());
		
//		punto 3
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCodigo_suia(proyecto.getCodigo());
		String superficie = "";
		if(proyecto.getConcesionesMineras().size()>0)
		{
//			superficie=superficie+"<p>"+proyecto.getArea()+" "+proyecto.getUnidad()+"</p>";
			for(ConcesionMinera consesiones:proyecto.getConcesionesMineras())
			{
				superficie=superficie+"<p>" +consesiones.getNombre()+" Código:"+consesiones.getCodigo()+" "+consesiones.getArea()+" "+consesiones.getUnidad()+"</p>";
			}
		}
		else
		{
			superficie=superficie+"<p>"+proyecto.getArea()+" "+proyecto.getUnidad()+" Código:"+proyecto.getCodigoMinero()+"</p>";
		}
		
		String fechaExpost=""; 
		//$F{fechaExpost}
		if(proyecto.getTipoEstudio().getId()==2)
		{
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			String dateString = format.format(proyecto.getFechaInicioOperaciones());
			fechaExpost="<tr>"
					+ "<td width=\"360\">"
					+ "<p align=\"left\">"
					+ "<b>Fecha de inicio de actividades:</b></p>"
					+ "</td>"
					+ "<td colspan=\"2\" width=\"476\">"
					+ "<p align=\"left\">"
					+ "<span>"+dateString+"</span></p>"
					+ "</td>"
					+ "</tr>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setFechaExpost(fechaExpost);
		}
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setSuperficie_concesion(superficie);
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setParroquia(proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica().getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCanton(proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setProvincia(proyecto.getProyectoUbicacionesGeograficas().get(0).getUbicacionesGeografica().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTipo_parroquia(proyecto.getTipoPoblacion().getNombre());
		
		String coordenadas="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">";
			for (FormaProyecto formacoordenadas : proyecto.getFormasProyectos()) {
				coordenadas=coordenadas +"<tr>"
						+ "<td colspan=\"2\">Grupo de coordenadas ("+formacoordenadas.getTipoForma().getNombre()+")</td>"
						+ "</tr>"
						+ "<tr>"
						+ " <td>X</td>"
						+ "	<td>Y</td>"
						+ "</tr>";
				for (Coordenada listcordenadas : formacoordenadas.getCoordenadas()) {
					coordenadas=coordenadas+ "<tr>"
							+ "	<td>"+listcordenadas.getX()+"</td>"
							+ "<td>"+listcordenadas.getY()+"</td>"
							+ "</tr>";
				}
						
			}
			coordenadas=coordenadas+"</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCoordenadas(coordenadas);
				
		String coordenadasMineria="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>X</td>"
				+ "	<td>Y</td>"
				+ "</tr>";
		List<PerforacionCoordenadas> listaCoordenadas= new ArrayList<PerforacionCoordenadas>();
		try {
			listaCoordenadas=fichaAmbientalMineria020Facade.cargarPerforacionCoordendas(perforacionExplorativa);
			for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
				coordenadasMineria=coordenadasMineria+ "<tr>"
						+ "	<td>"+perforacionCoordenadas.getX()+"</td>"
						+ "<td>"+perforacionCoordenadas.getY()+"</td>"
						+ "</tr>";
						
			}
			coordenadasMineria=coordenadasMineria+"</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCoordenadas_derecho_minero(coordenadasMineria);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}				
		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(String.valueOf(proyecto.getUsuario().getNombre()));
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEmail(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTelefono(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.CELULAR)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCelular(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.DIRECCION)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setDomicilio(contacto.getValor());
				}
				if(contacto.getOrganizacion()!=null)
				{
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitular_minero(contacto.getOrganizacion().getNombre());
					String org =proyecto.getUsuario().getNombre();              
					Organizacion orgd= organizacionFacade.buscarPorRuc(org);
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRepresentate_legal(orgd.getPersona().getNombre());
				}
				else
				{
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitular_minero(contacto.getPersona().getNombre());
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRepresentate_legal(contacto.getPersona().getNombre());
				}			
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		if(perforacionExplorativa.getJudicialLocker()==null)
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCasillero_judicial("S/N");
		else
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCasillero_judicial(perforacionExplorativa.getJudicialLocker());
		
		Consultor consultor=perforacionExplorativa.getConsultor();
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsultor(consultor.getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRegistro_consultor(consultor.getRegistro());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEmail_consultor(consultor.getEmail());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTelefono_consultor(consultor.getTelefono());
		
		String equipoMultidisciplinario="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 33%\">Nombre</td>"
				+ "<td style=\"width: 33%\">Formación Profecional</td>"
				+ "<td style=\"width: 33%\">Componente</td>"
				+ "</tr>";				
		List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis= new ArrayList<PerforacionEquipoMultidisciplinario>();
		try {
			listaEquipoMultidis=fichaAmbientalMineria020Facade.cargarEquipoMultidisciplinario(perforacionExplorativa);
			for (PerforacionEquipoMultidisciplinario perforacionEquipoMultidisciplinario : listaEquipoMultidis) {
				equipoMultidisciplinario=equipoMultidisciplinario + "<tr>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getName()+"</td>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getVocationalTraining()+"</td>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getComponent()+"</td>"
						+ "</tr>";
			}
			equipoMultidisciplinario=equipoMultidisciplinario+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_equipo_multidiciplinario(equipoMultidisciplinario);
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		
//		punto 4
		
//		permiso_ejecucion
//		certificado_interseccion
//		certificado_viabilidad
//		permisos_ambientales_anteriores
		
		String antecedenteProyecto="";
		if(certificadoInterseccionService.isProyectoIntersecaCapas(proyecto.getCodigo()))
		{
			antecedenteProyecto+="<p><b>Permiso de ejecución del proyecto (título minero)</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getProjectExecutionPermit()+"</p>";
			antecedenteProyecto+="<p><b>Certificado de Intersección</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getIntersectionCertificate()+"</p>";
			antecedenteProyecto+="<p><b>Certificado de Viabilidad emitido por la Dirección Nacional Forestal</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getFeasibilityCertificate()+"</p>";
		}
		else
		{
			antecedenteProyecto+="<p>Permiso de ejecución del proyecto (título minero)</p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getProjectExecutionPermit()+"</p>";
			antecedenteProyecto+="<p>Certificado de Intersección</p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getIntersectionCertificate()+"</p>";			
		}		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAntecedentes_proyecto(antecedenteProyecto);
		
//		punto 5
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setResumen_proyecto(perforacionExplorativa.getProjectSummary());
		
//		punto 6
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAreas_intervenidas(perforacionExplorativa.getIntervenedAreas());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTecnicas_exploracion(perforacionExplorativa.getExplorationTechniques());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEtapas_cierre_proyecto(perforacionExplorativa.getProjectClosureStages());		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setActivdades_complementarias(perforacionExplorativa.getInfrastructureComplementaryActivities());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAcceso_sitios(perforacionExplorativa.getAccessesTrails());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura_temporal(perforacionExplorativa.getTemporaryInfrastructureImplementation());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCampamentos_temporles(perforacionExplorativa.getTemporaryCamps());
		
		String maquinariasEquipos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Denominación de la maquinaria, equipos y materiales</td>"
				+ "<td style=\"width: 25%\">Cantidad (unidades)</td>"
				+ "<td style=\"width: 25%\">Características</td>"
				+ "<td style=\"width: 25%\">Uso / Proceso</td>"
				+ "</tr>";
		List<PerforacionMaquinasEquipos> listaMaquinariaEquipos= new ArrayList<PerforacionMaquinasEquipos>();
		try {
			listaMaquinariaEquipos=fichaAmbientalMineria020Facade.cargarMaquinariaEquipo(perforacionExplorativa);
			for (PerforacionMaquinasEquipos perforacionMaquinasEquipos : listaMaquinariaEquipos) {
				maquinariasEquipos=maquinariasEquipos+ "<tr>"
						+ "<td>"+perforacionMaquinasEquipos.getName()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getUnit()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getCharacteristics()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getProcess()+"</td>"
						+ "</tr>";
			}			
			maquinariasEquipos=maquinariasEquipos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_maquinaria_equipos(maquinariasEquipos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		String materialesInsumo="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Material (combustibles, insumos, productos químicos)</td>"
				+ "<td style=\"width: 25%\">Cantidad (Unidades, kg, gal, etc.) /año</td>"
				+ "<td style=\"width: 25%\">Proceso en el que es empleado</td>"
				+ "<td style=\"width: 25%\">No. CAS /ONU</td>"
				+ "</tr>";
		List<PerforacionMaterialInsumo> listaMaterialInsumo= new ArrayList<PerforacionMaterialInsumo>();
		try {
			listaMaterialInsumo=fichaAmbientalMineria020Facade.cargarMaterialInsumo(perforacionExplorativa);
			for (PerforacionMaterialInsumo perforacionMaterialInsumo : listaMaterialInsumo) {
				materialesInsumo=materialesInsumo+ "<tr>"
						+ "<td>"+perforacionMaterialInsumo.getSustanciaQuimicaPeligrosa().getDescripcion()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getValue()+" "+perforacionMaterialInsumo.getUnit()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getProcess()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getCasonu()+"</td>"
						+ "</tr>";
			}			
			materialesInsumo=materialesInsumo+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_materiales_insumos(materialesInsumo);
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		String desechonopeligrosos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Tipo de residuo/ desecho (Orgánico, Papel, Cartón, Plástico, Vidrio, etc.)</td>"
				+ "<td style=\"width: 25%\">Cantidad Generada aproximada /Mes (kg, ton, etc.)</td>"
				+ "<td style=\"width: 25%\">Reducción, tratamiento</td>"
				+ "<td style=\"width: 25%\">Disposición Final</td>"
				+ "</tr>";
		List<PerforacionDesechosNoPeligrosos> listadesechonopeligrosos= new ArrayList<PerforacionDesechosNoPeligrosos>();
		try {
			listadesechonopeligrosos=fichaAmbientalMineria020Facade.cargarDesechosNoPeligrosos(perforacionExplorativa);
			for (PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos : listadesechonopeligrosos) {
				desechonopeligrosos=desechonopeligrosos+ "<tr>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getWasteType()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getValue()+" "+perforacionDesechosNoPeligrosos.getUnit()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getTreatment()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getFinalArrangement()+"</td>"
						+ "</tr>";
			}
			desechonopeligrosos=desechonopeligrosos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_desechos_no_peligrosos(desechonopeligrosos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		String desechopeligrosos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 15%\">Tipo de Desecho</td>"
				+ "<td style=\"width: 15%\">Código (AM NO. 142 o el que lo reemplace)</td>"
				+ "<td style=\"width: 15%\">CRTIB</td>"
				+ "<td style=\"width: 15%\">Cantidad Generada aproximada /Mes</td>"
				+ "<td style=\"width: 15%\">Proceso o unidad operativa</td>"
				+ "<td style=\"width: 15%\">Tipo de Eliminación o Disposición final</td>"
				+ "</tr>";
		List<PerforacionDesechosPeligrosos> listadesechopeligrosos= new ArrayList<PerforacionDesechosPeligrosos>();
		try {
			listadesechopeligrosos=fichaAmbientalMineria020Facade.cargarDesechosPeligrosos(perforacionExplorativa);
			for (PerforacionDesechosPeligrosos perforacionDesechosPeligrosos : listadesechopeligrosos) {
				desechopeligrosos=desechopeligrosos+ "<tr>"
						+ "<td>"+perforacionDesechosPeligrosos.getWasteType()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getCode()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getCrtib()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getValue()+" "+perforacionDesechosPeligrosos.getUnit()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getProcess()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getFinalArrangement()+"</td>"
						+ "</tr>";
			}
			desechopeligrosos=desechopeligrosos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_desechos_peligrosos(desechopeligrosos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setMano_obra_calificada(perforacionExplorativa.getQualifiedHandWork());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setDiagrama_flujo_base(perforacionExplorativa.getBaseFlowDiagram());
		
//		punto 7
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAgua_consumo_humano(perforacionExplorativa.getWaterHumanConsumption()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAgua_perforacion(perforacionExplorativa.getWaterDrilling()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEnergia_electrica(perforacionExplorativa.getElectricPower()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCombustible(perforacionExplorativa.getFuelConsumption()?"SI":"NO");		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_agua(perforacionExplorativa.getWaterConsumption());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_agua_perforacion(perforacionExplorativa.getWaterConsumptionDrilling());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_enegia_eletrica(perforacionExplorativa.getElectricPowerConsumption());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_combustible(perforacionExplorativa.getFuelTypeConsumption());
		
//		punto 8
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_fisica(perforacionExplorativa.getDirectPhysicalInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_biotica(perforacionExplorativa.getDirectBioticInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_social(perforacionExplorativa.getDirectSocialInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_indirecta_social(perforacionExplorativa.getIndirectSocialInfluence());
		
//		punto 9
		
		ComponenteFisicoSD componenteFisico = new ComponenteFisicoSD();
		ComponenteSocialSD componenteSocial = new ComponenteSocialSD();
		componenteFisico = componenteFisicoFacade.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa.getId());
		componenteSocial = componenteSocialFacade.buscarComponenteSocialPorPerforacionExplorativa(perforacionExplorativa.getId());
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPrecipitacion_anual(componenteFisico.getPrecipitacionAnual());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTemperatura_anual(componenteFisico.getTemperaturaMediaAnual());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAltitud(componenteFisico.getAltitud());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setComponente_hidrico(componenteFisico.getComponenteHidrico());
		
		
		String geomorfologia="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td style=\"width: 60%\">Pendiente</td>"
				+ "	<td style=\"width: 40%\">Seleccionado</td>"
				+ "</tr>";	
		
		List<String> codigosFisicos = new ArrayList<String>();
		Map<String, List<CatalogoGeneralFisico>> listaGeneralFisico;
		listaGeneralFisico = new HashMap<String, List<CatalogoGeneralFisico>>();
		codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE);
		List<CatalogoGeneralFisico> fisicos = catalogoFisicoFacade.obtenerListaFisicoTipo(codigosFisicos);
		for (CatalogoGeneralFisico catalogoGeneralFisico : fisicos) {
			List<CatalogoGeneralFisico> tmp = new ArrayList<CatalogoGeneralFisico>();
			String key = catalogoGeneralFisico.getTipoCatalogo().getCodigo();
			if (listaGeneralFisico.containsKey(key)) {
				tmp = listaGeneralFisico.get(key);
			}
			tmp.add(catalogoGeneralFisico);
			listaGeneralFisico.put(key, tmp);
		}
		List<CatalogoGeneralFisico> pendienteSuelo = null;
		if (listaGeneralFisico.containsKey(TipoCatalogo.CODIGO_PENDIENTE)) {
			pendienteSuelo= listaGeneralFisico.get(TipoCatalogo.CODIGO_PENDIENTE);
		}	
		
		for(ComponenteFisicoPendienteSD pendiente : componenteFisico.getComponenteFisicoPendienteList()){
			for(CatalogoGeneralFisico pendienteCat : pendienteSuelo){
				if(pendiente.getCatalogoFisico().getId().equals(pendienteCat.getId()) && pendiente.getEstado() == true){
					pendienteCat.setSeleccionado(true);
				}
			}				
		}	
		
		for(CatalogoGeneralFisico pendiente: pendienteSuelo)
		{
			String selecc="";
			if(pendiente.isSeleccionado())
				selecc="X";
			
			geomorfologia=geomorfologia+"<tr>"
				+ "<td>"+pendiente.getDescripcion()+"</td>"
				+ "<td>"+selecc+"</td>"
			+ "</tr>";			
		}
		geomorfologia=geomorfologia+ "</table>";
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPendiente(geomorfologia);
		
		inicializarDatosGeneralesBiotico();
		coberturaVegetalList = inicializarBiotico(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
		pisosZooGeograficosList = inicializarBiotico(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
		componenteBioticoNativoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
		componenteBioticoIntroducidoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
		aspectosEcologicosList = inicializarBiotico(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
		areasSensiblesList = inicializarBiotico(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
		
		inicializarCobertura();
		
		listaBioticoCoberturaVegetal = new ArrayList<ComponenteBioticoSD>();
		listaBioticoPisosZooGeograficos = new ArrayList<ComponenteBioticoSD>(); 
		listaBioticoNativo = new ArrayList<ComponenteBioticoSD>();
		listaBioticoIntroducido = new ArrayList<ComponenteBioticoSD>();
		listaBioticoAspectoEcologico = new ArrayList<ComponenteBioticoSD>();
		listaBioticoAreasSensibles = new ArrayList<ComponenteBioticoSD>();
		
		
		List<ComponenteBioticoSD> listaBiotico;
		try {
			listaBiotico = componenteBioticoFacade.buscarComponenteBioticoPorPerforacionExplorativa(fichaAmbientalMineria020Facade.cargarPerforacionExplorativa(proyecto).getId());
			for(ComponenteBioticoSD biotico : listaBiotico){
				for(CatalogoGeneralBiotico cobertura : coberturaVegetalList){
					if(cobertura.getCatalogoGeneralBioticoList() != null){
						for(CatalogoGeneralBiotico coberturaHijo : cobertura.getCatalogoGeneralBioticoList()){
							if(biotico.getCatalogoGeneralBiotico().getId().equals(coberturaHijo.getId())){
								coberturaHijo.setSeleccionado(true);
								listaBioticoCoberturaVegetal.add(biotico);
							}
						}
					}										
				}
				
				for(CatalogoGeneralBiotico pisos : pisosZooGeograficosList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(pisos.getId())){
						pisos.setSeleccionado(true);
						listaBioticoPisosZooGeograficos.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico nativo : componenteBioticoNativoList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(nativo.getId())){
						nativo.setSeleccionado(true);
						listaBioticoNativo.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico introducido : componenteBioticoIntroducidoList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(introducido.getId())){
						introducido.setSeleccionado(true);
						listaBioticoIntroducido.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico ecologico : aspectosEcologicosList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(ecologico.getId())){
						ecologico.setSeleccionado(true);
						listaBioticoAspectoEcologico.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico area : areasSensiblesList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(area.getId())){
						area.setSeleccionado(true);
						listaBioticoAreasSensibles.add(biotico);
					}
				}				
			}
			
			String coberturavegetal="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td style=\"width: 60%\">Cobertura Nivel II</td>"
				+ "	<td style=\"width: 40%\">Seleccionado</td>"
				+ "</tr>";
			
			for(CatalogoGeneralBiotico coberturaVegetalPadre: coberturaVegetalPadreList)
			{
				coberturavegetal=coberturavegetal+"<tr><td style=\"text-align: left;\" colspan=\"2\">"+coberturaVegetalPadre.getDescripcion()+"</td></tr>";
				for(CatalogoGeneralBiotico coberturaVegetalhijos: coberturaVegetalPadre.getCatalogoGeneralBioticoList())
				{
					String selecc="";
					if(coberturaVegetalhijos.isSeleccionado())
						selecc="X";
					coberturavegetal=coberturavegetal+"<tr>"
							+ "<td style=\"text-align: left;\">"+coberturaVegetalhijos.getDescripcion()+"</td>"
							+ "<td>"+selecc+"</td>"
						+ "</tr>";	
				}
			}
			coberturavegetal=coberturavegetal+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_cobertura_v(coberturavegetal);
			
			String zooGeograficos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 40%\">Pisos Zoo Geográficos</td>"
					+ "	<td style=\"width: 40%\">Simbología</td>"
					+ "	<td style=\"width: 20%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico pisosZooGeograficos: pisosZooGeograficosList)
			{
				String selecc="";
				if(pisosZooGeograficos.isSeleccionado())
					selecc="X";
				zooGeograficos=zooGeograficos+"<tr>"
						+ "<td style=\"text-align: left;\">"+pisosZooGeograficos.getDescripcion()+"</td>"
						+ "<td style=\"text-align: left;\">"+pisosZooGeograficos.getAyuda()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			zooGeograficos=zooGeograficos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_pisos_zoogeograficos(zooGeograficos);
			
			
			String bioticoNativo="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td >Componentes bióticos nativos</td>"
					+ "	<td>Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico componenteBioticoNativo: componenteBioticoNativoList)
			{
				String selecc="";
				if(componenteBioticoNativo.isSeleccionado())
					selecc="X";
				bioticoNativo=bioticoNativo+"<tr>"
						+ "<td style=\"text-align: left;\">"+componenteBioticoNativo.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			bioticoNativo=bioticoNativo+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_nativo(bioticoNativo);
			
			String bioticoIntroducido="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Componente biótico introducido</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico componenteBioticoIntroducido: componenteBioticoIntroducidoList)
			{
				String selecc="";
				if(componenteBioticoIntroducido.isSeleccionado())
					selecc="X";
				bioticoIntroducido=bioticoIntroducido+"<tr>"
						+ "<td style=\"text-align: left;\">"+componenteBioticoIntroducido.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			bioticoIntroducido=bioticoIntroducido+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_introducido(bioticoIntroducido);
			
			
			
			String aspectosEcologicos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico aspectosEcologicosL: aspectosEcologicosList)
			{
				String selecc="";
				if(aspectosEcologicosL.isSeleccionado())
					selecc="X";
				aspectosEcologicos=aspectosEcologicos+"<tr>"
						+ "<td style=\"text-align: left;\">"+aspectosEcologicosL.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			aspectosEcologicos=aspectosEcologicos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_aspectos_ecologicos(aspectosEcologicos);
			
			
			String areasSensibles="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico areasSensiblesL: areasSensiblesList)
			{
				String selecc="";
				if(areasSensiblesL.isSeleccionado())
					selecc="X";
				areasSensibles=areasSensibles+"<tr>"
						+ "<td style=\"text-align: left;\">"+areasSensiblesL.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			areasSensibles=areasSensibles+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_areas_sensibles(areasSensibles);
			
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setComunidades(componenteSocial.getComunidades());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPoblaciones(componenteSocial.getPoblaciones());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPredios(componenteSocial.getPredios());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura(componenteSocial.getInfraestructura());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAsentamientos_grupos_etnicos(componenteSocial.getAsentamientosGruposEtnicos());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura_salud_publica(componenteSocial.getInfraestructuraSaludPublica());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setElementos_culturales(componenteSocial.getElementosCulturales());				
		
//		punto 11
		List<EntityActividad> listaActividades;		
		String impactosAmbientales="<table width=\"70%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>Sub Actividad</td>"
				+ "	<td>Equipos / Herramientas</td>"
				+ "	<td>Impactos Identificados</td>"
				+ "	<td>Descripción del impacto</td>"
				+ "	<td>Agua</td>"
				+ "	<td>Aire</td>"
				+ "	<td>Suelo</td>"
				+ "	<td>Biotico</td>"
				+ "	<td>Paisaje</td>"
				+ "	<td>Social</td>"				
				+ "</tr>";
		try {
			listaActividades = actividadesImpactoFacade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
			for (EntityActividad objListaImpacto: listaActividades){				
				impactosAmbientales=impactosAmbientales+"<tr><td style=\"text-align: left;\" colspan=\"10\"><strong>"+objListaImpacto.getActividad()+"</strong></td></tr>";				
				for (ActividadesImpactoProyecto objImpacto: objListaImpacto.getSubactividades()){
					String agua=objImpacto.isAgua()?"X":"";
					String aire=objImpacto.isAire()?"X":"";
					String suelo=objImpacto.isSuelo()?"X":"";
					String biotico=objImpacto.isBiotico()?"X":"";
					String paisaje=objImpacto.isPaisaje()?"X":"";
					String social=objImpacto.isSocial()?"X":"";
					impactosAmbientales=impactosAmbientales+"<tr>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getActividad().getNombre()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getHerramientas()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getImpacto()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getDescripcionImpacto()+"</td>"
							+ "<td>"+agua+"</td>"
							+ "<td>"+aire+"</td>"
							+ "<td>"+suelo+"</td>"
							+ "<td>"+biotico+"</td>"
							+ "<td>"+paisaje+"</td>"
							+ "<td>"+social+"</td>"
							+ "</tr>";
				}
			}
			impactosAmbientales=impactosAmbientales+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_identificacion_impactos_ambientales(impactosAmbientales);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
//		punto 12
		String pma="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>MEDIDAS PROPUESTAS</td>"
				+ "	<td>INDICADOR</td>"
				+ "	<td>MEDIO DE VERIFICACIÓN</td>"
				+ "	<td>FRECUENCIA</td>"
				+ "	<td>PRESUPUESTO</td>"				
				+ "</tr>";
		Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();
		parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
        for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters.entrySet()) {
            String key = entry.getKey();
            pma=pma+"<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"+key+"</strong></td></tr>";
            List<PlanManejoAmbientalProyecto> values = entry.getValue();
            for (PlanManejoAmbientalProyecto planManejoAmbientalProyecto : values) {
            	if(planManejoAmbientalProyecto.getPlanManejoAmbiental020()==null)
            	{
            		pma=pma+"<tr>"
            				+ " <td style=\"width: 20%\">"+planManejoAmbientalProyecto.getMedidasPropuestas()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getIndicador()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getVerificacionMedios()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getFrecuencia()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getCalificacion()+"</td>"				
            				+ "</tr>";
            	}
            	else
            	{
            		if(planManejoAmbientalProyecto.getCalificacion()==null)
            			pma=pma+"<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getDescripcion()+"</strong></td></tr>";
            		else
            		{
            		pma=pma+"<tr>"
            				+ " <td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getMedidasPropuestas()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getIndicador()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getVerificacionMedios()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getFrecuencia()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getCalificacion()+"</td>"				
            				+ "</tr>";
            		}
            	}
			}
        }
        pma=pma+ "</table>";
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_prevencion_impactos(pma);
//		punto 13
		double total;
		String cronogramaPma="<table width=\"700px\" border=\"1\" cellpadding=\"5\" bordercolor=\"#000000\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ "<td rowspan=\"2\">SUB PLAN-ACTIVIDADES</td>"
				+ "<td colspan=\"12\">MESES</td>"
				+ "<td rowspan=\"2\">VALOR</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>1</td>"
				+ "<td>2</td>"
				+ "<td>3</td>"
				+ "<td>4</td>"
				+ "<td>5</td>"
				+ "<td>6</td>"
				+ "<td>7</td>"
				+ "<td>8</td>"
				+ "<td>9</td>"
				+ "<td>10</td>"
				+ "<td>11</td>"
				+ "<td>12</td>"
				+ "</tr>";
		List<PerforacionCronogramaPma> listaCronogramaPma = new ArrayList<PerforacionCronogramaPma>();
		try {
			listaCronogramaPma=fichaAmbientalMineria020Facade.cargarCronogramaPma(perforacionExplorativa);
			total = 0;
			for (PerforacionCronogramaPma perforacionCronogramaPma : listaCronogramaPma) {
				String m1=perforacionCronogramaPma.getMonth1()?"X":"";
				String m2=perforacionCronogramaPma.getMonth2()?"X":"";
				String m3=perforacionCronogramaPma.getMonth3()?"X":"";
				String m4=perforacionCronogramaPma.getMonth4()?"X":"";
				String m5=perforacionCronogramaPma.getMonth5()?"X":"";
				String m6=perforacionCronogramaPma.getMonth6()?"X":"";
				String m7=perforacionCronogramaPma.getMonth7()?"X":"";
				String m8=perforacionCronogramaPma.getMonth8()?"X":"";
				String m9=perforacionCronogramaPma.getMonth9()?"X":"";
				String m10=perforacionCronogramaPma.getMonth10()?"X":"";
				String m11=perforacionCronogramaPma.getMonth11()?"X":"";
				String m12=perforacionCronogramaPma.getMonth12()?"X":"";
				cronogramaPma=cronogramaPma+"<tr>"
						+ "<td style=\"text-align: left;\">"+perforacionCronogramaPma.getTipoPlanManejoAmbiental().getTipo()+"</td>"						
						+ "<td>"+m1+"</td>"
						+ "<td>"+m2+"</td>"
						+ "<td>"+m3+"</td>"
						+ "<td>"+m4+"</td>"
						+ "<td>"+m5+"</td>"
						+ "<td>"+m6+"</td>"
						+ "<td>"+m7+"</td>"
						+ "<td>"+m8+"</td>"
						+ "<td>"+m9+"</td>"
						+ "<td>"+m10+"</td>"
						+ "<td>"+m11+"</td>"
						+ "<td>"+m12+"</td>"
						+ "<td>"+perforacionCronogramaPma.getBudget()+"</td>"
						+ "</tr>";	
				total+=perforacionCronogramaPma.getBudget();
			}
			cronogramaPma=cronogramaPma+"<tr>"
					+ "<td colspan=\"13\" style=\"text-align: right;\">TOTAL</td>"
					+ "<td>"+total+"</td>"
					+ "</tr></table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_cronograma_actividades(cronogramaPma);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
    }
    public void inicializarDatosGeneralesBiotico() {
        List<String> codigosBioticos = new ArrayList<String>();
        listaGeneralBiotico = new HashMap<String, List<CatalogoGeneralBiotico>>();
        codigosBioticos.add(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
        codigosBioticos.add(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
        List<CatalogoGeneralBiotico> bioticos = catalogoBioticoFacade.obtenerListaBioticoTipo(codigosBioticos);
        for (CatalogoGeneralBiotico catalogoGeneralBiotico : bioticos) {
            List<CatalogoGeneralBiotico> tmp = new ArrayList<CatalogoGeneralBiotico>();
            String key = catalogoGeneralBiotico.getTipoCatalogo().getCodigo();
            if (listaGeneralBiotico.containsKey(key)) {
                tmp = listaGeneralBiotico.get(key);
            }
            tmp.add(catalogoGeneralBiotico);
            listaGeneralBiotico.put(key, tmp);
        }
    }
    public List<CatalogoGeneralBiotico> inicializarBiotico(String codigo) {

        if (listaGeneralBiotico.containsKey(codigo)) {
            return listaGeneralBiotico.get(codigo);
        }
        return new ArrayList<CatalogoGeneralBiotico>();
    }
    public void inicializarCobertura() {
		coberturaVegetalPadreList = new ArrayList<>();
		for(CatalogoGeneralBiotico cobertura : coberturaVegetalList){
			if(cobertura.getCatalogoPadre() == null){
				coberturaVegetalPadreList.add(cobertura);
			}
		}
    }
    
    @EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
    @EJB
	private ProyectoLicenciaAmbientalConcesionesMinerasFacade proyectoConcesionesMinerasFacade;
    @EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
    @EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
    @EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
    @EJB
	private  DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
    
    public void cargaDatosMineria020RCOA()
    {
    	try {

		PerforacionExplorativa perforacionExplorativa = fichaAmbientalMineria020Facade.cargarPerforacionExplorativaRcoa(proyectoLicenciaCoa.getId());
		
		ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyectoLicenciaCoa);
		
		String titulo="";
		if(perforacionExplorativa.getCodeUpdate()==null || perforacionExplorativa.getCodeUpdate().equals(""))
			titulo="REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";
		else
			titulo="ACTUALIZACIÓN DEL REGISTRO AMBIENTAL PARA EXPLORACIÓN INICIAL EN EL QUE SE INCLUYE SONDEOS DE PRUEBA O RECONOCIMIENTO";
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitulo(titulo);
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setNombre_proyecto(proyectoLicenciaCoa.getNombreProyecto());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setNombre_catalogo(actividadPrincipal.getCatalogoCIUU().getNombre());
		
//		punto 3
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCodigo_suia(proyectoLicenciaCoa.getCodigoUnicoAmbiental());
		String superficie = "";
		List<ProyectoLicenciaAmbientalConcesionesMineras> concesionesMinerasRcoa = proyectoConcesionesMinerasFacade.cargarConcesiones(proyectoLicenciaCoa);

		 if(concesionesMinerasRcoa.size() > 0) {
		 	for(ProyectoLicenciaAmbientalConcesionesMineras concesiones: concesionesMinerasRcoa) {
		 		superficie=superficie+"<p>" +concesiones.getNombre()+" Código:"+concesiones.getCodigo()+" "+concesiones.getArea()+"</p>";
		 	}
		 }
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setSuperficie_concesion(superficie);
		
		UbicacionesGeografica ubicacionPrincipal=proyectoLicenciaCoaUbicacionFacade.ubicacionPrincipal(proyectoLicenciaCoa).getUbicacionesGeografica();
		String parroquia=ubicacionPrincipal.getNombre();
		String canton=ubicacionPrincipal.getUbicacionesGeografica().getNombre();
		String provincia=ubicacionPrincipal.getUbicacionesGeografica().getUbicacionesGeografica().getNombre();
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setParroquia(parroquia);
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCanton(canton);
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setProvincia(provincia);
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTipo_parroquia(proyectoLicenciaCoa.getTipoPoblacion().getNombre());
		
		String coordenadas="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">";
		
		List<CoordendasPoligonos> coordenadasImplantacion = new ArrayList<CoordendasPoligonos>();
		List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyectoLicenciaCoa, 1, 0); //coordenadas implantacion
			
		if(formasImplantacion != null) {
			for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
				List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
				
				CoordendasPoligonos poligono = new CoordendasPoligonos();
				poligono.setCoordenadas(coordenadasGeograficasImplantacion);
				poligono.setTipoForma(forma.getTipoForma());
				
				coordenadasImplantacion.add(poligono);
			}
		}
		
		for(CoordendasPoligonos formacoordenadas : coordenadasImplantacion){
			coordenadas=coordenadas +"<tr>"
					+ "<td colspan=\"2\">Grupo de coordenadas ("+formacoordenadas.getTipoForma().getNombre()+")</td>"
					+ "</tr>"
					+ "<tr>"
					+ " <td>X</td>"
					+ "	<td>Y</td>"
					+ "</tr>";
			
			for(CoordenadasProyecto listcordenadas: formacoordenadas.getCoordenadas()){
				coordenadas=coordenadas+ "<tr>"
						+ "	<td>"+listcordenadas.getX()+"</td>"
						+ "<td>"+listcordenadas.getY()+"</td>"
						+ "</tr>";
			}
		}
		
		coordenadas=coordenadas+"</table>";
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCoordenadas(coordenadas);
				
		String coordenadasMineria="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>X</td>"
				+ "	<td>Y</td>"
				+ "</tr>";
		List<PerforacionCoordenadas> listaCoordenadas= new ArrayList<PerforacionCoordenadas>();
		try {
			listaCoordenadas=fichaAmbientalMineria020Facade.cargarPerforacionCoordendas(perforacionExplorativa);
			for (PerforacionCoordenadas perforacionCoordenadas : listaCoordenadas) {
				coordenadasMineria=coordenadasMineria+ "<tr>"
						+ "	<td>"+perforacionCoordenadas.getX()+"</td>"
						+ "<td>"+perforacionCoordenadas.getY()+"</td>"
						+ "</tr>";
						
			}
			coordenadasMineria=coordenadasMineria+"</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCoordenadas_derecho_minero(coordenadasMineria);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}				
		
		try {
			List<Contacto> contactos = contactoFacade.buscarUsuarioNativeQuery(String.valueOf(proyectoLicenciaCoa.getUsuario().getNombre()));
			for (Contacto contacto : contactos) {
				if (contacto.getFormasContacto().getId().equals(FormasContacto.EMAIL)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEmail(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.TELEFONO)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTelefono(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.CELULAR)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCelular(contacto.getValor());
				}
				if (contacto.getFormasContacto().getId().equals(FormasContacto.DIRECCION)){
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setDomicilio(contacto.getValor());
				}
				if(contacto.getOrganizacion()!=null)
				{
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitular_minero(contacto.getOrganizacion().getNombre());
					String org =proyectoLicenciaCoa.getUsuario().getNombre();              
					Organizacion orgd= organizacionFacade.buscarPorRuc(org);
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRepresentate_legal(orgd.getPersona().getNombre());
				}
				else
				{
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTitular_minero(contacto.getPersona().getNombre());
					getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRepresentate_legal(contacto.getPersona().getNombre());
				}			
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		if(perforacionExplorativa.getJudicialLocker()==null)
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCasillero_judicial("S/N");
		else
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCasillero_judicial(perforacionExplorativa.getJudicialLocker());
		
		Consultor consultor=perforacionExplorativa.getConsultor();
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsultor(consultor.getNombre());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setRegistro_consultor(consultor.getRegistro());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEmail_consultor(consultor.getEmail());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTelefono_consultor(consultor.getTelefono());
		
		String equipoMultidisciplinario="<table width=\"100%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 33%\">Nombre</td>"
				+ "<td style=\"width: 33%\">Formación Profecional</td>"
				+ "<td style=\"width: 33%\">Componente</td>"
				+ "</tr>";				
		List<PerforacionEquipoMultidisciplinario> listaEquipoMultidis= new ArrayList<PerforacionEquipoMultidisciplinario>();
		try {
			listaEquipoMultidis=fichaAmbientalMineria020Facade.cargarEquipoMultidisciplinario(perforacionExplorativa);
			for (PerforacionEquipoMultidisciplinario perforacionEquipoMultidisciplinario : listaEquipoMultidis) {
				equipoMultidisciplinario=equipoMultidisciplinario + "<tr>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getName()+"</td>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getVocationalTraining()+"</td>"
						+ "<td>"+perforacionEquipoMultidisciplinario.getComponent()+"</td>"
						+ "</tr>";
			}
			equipoMultidisciplinario=equipoMultidisciplinario+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_equipo_multidiciplinario(equipoMultidisciplinario);
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		
//		punto 4
		
//		permiso_ejecucion
//		certificado_interseccion
//		certificado_viabilidad
//		permisos_ambientales_anteriores
		
		String antecedenteProyecto="";
		if(detalleInterseccionProyectoAmbientalFacade.isProyectoIntersecaCapas(proyectoLicenciaCoa.getCodigoUnicoAmbiental()))
		{
			antecedenteProyecto+="<p><b>Permiso de ejecución del proyecto (título minero)</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getProjectExecutionPermit()+"</p>";
			antecedenteProyecto+="<p><b>Certificado de Intersección</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getIntersectionCertificate()+"</p>";
			antecedenteProyecto+="<p><b>Certificado de Viabilidad emitido por la Dirección Nacional Forestal</b></p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getFeasibilityCertificate()+"</p>";
		}
		else
		{
			antecedenteProyecto+="<p>Permiso de ejecución del proyecto (título minero)</p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getProjectExecutionPermit()+"</p>";
			antecedenteProyecto+="<p>Certificado de Intersección</p>";
			antecedenteProyecto+="<p align=\"justify\">"+perforacionExplorativa.getIntersectionCertificate()+"</p>";			
		}		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAntecedentes_proyecto(antecedenteProyecto);
		
//		punto 5
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setResumen_proyecto(perforacionExplorativa.getProjectSummary());
		
//		punto 6
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAreas_intervenidas(perforacionExplorativa.getIntervenedAreas());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTecnicas_exploracion(perforacionExplorativa.getExplorationTechniques());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEtapas_cierre_proyecto(perforacionExplorativa.getProjectClosureStages());		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setActivdades_complementarias(perforacionExplorativa.getInfrastructureComplementaryActivities());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAcceso_sitios(perforacionExplorativa.getAccessesTrails());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura_temporal(perforacionExplorativa.getTemporaryInfrastructureImplementation());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCampamentos_temporles(perforacionExplorativa.getTemporaryCamps());
		
		String maquinariasEquipos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Denominación de la maquinaria, equipos y materiales</td>"
				+ "<td style=\"width: 25%\">Cantidad (unidades)</td>"
				+ "<td style=\"width: 25%\">Características</td>"
				+ "<td style=\"width: 25%\">Uso / Proceso</td>"
				+ "</tr>";
		List<PerforacionMaquinasEquipos> listaMaquinariaEquipos= new ArrayList<PerforacionMaquinasEquipos>();
		try {
			listaMaquinariaEquipos=fichaAmbientalMineria020Facade.cargarMaquinariaEquipo(perforacionExplorativa);
			for (PerforacionMaquinasEquipos perforacionMaquinasEquipos : listaMaquinariaEquipos) {
				maquinariasEquipos=maquinariasEquipos+ "<tr>"
						+ "<td>"+perforacionMaquinasEquipos.getName()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getUnit()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getCharacteristics()+"</td>"
						+ "<td>"+perforacionMaquinasEquipos.getProcess()+"</td>"
						+ "</tr>";
			}			
			maquinariasEquipos=maquinariasEquipos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_maquinaria_equipos(maquinariasEquipos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		String materialesInsumo="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Material (combustibles, insumos, productos químicos)</td>"
				+ "<td style=\"width: 25%\">Cantidad (Unidades, kg, gal, etc.) /año</td>"
				+ "<td style=\"width: 25%\">Proceso en el que es empleado</td>"
				+ "<td style=\"width: 25%\">No. CAS /ONU</td>"
				+ "</tr>";
		List<PerforacionMaterialInsumo> listaMaterialInsumo= new ArrayList<PerforacionMaterialInsumo>();
		try {
			listaMaterialInsumo=fichaAmbientalMineria020Facade.cargarMaterialInsumo(perforacionExplorativa);
			for (PerforacionMaterialInsumo perforacionMaterialInsumo : listaMaterialInsumo) {
				materialesInsumo=materialesInsumo+ "<tr>"
						+ "<td>"+perforacionMaterialInsumo.getSustanciaQuimicaPeligrosa().getDescripcion()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getValue()+" "+perforacionMaterialInsumo.getUnit()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getProcess()+"</td>"
						+ "<td>"+perforacionMaterialInsumo.getCasonu()+"</td>"
						+ "</tr>";
			}			
			materialesInsumo=materialesInsumo+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_materiales_insumos(materialesInsumo);
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		String desechonopeligrosos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 25%\">Tipo de residuo/ desecho (Orgánico, Papel, Cartón, Plástico, Vidrio, etc.)</td>"
				+ "<td style=\"width: 25%\">Cantidad Generada aproximada /Mes (kg, ton, etc.)</td>"
				+ "<td style=\"width: 25%\">Reducción, tratamiento</td>"
				+ "<td style=\"width: 25%\">Disposición Final</td>"
				+ "</tr>";
		List<PerforacionDesechosNoPeligrosos> listadesechonopeligrosos= new ArrayList<PerforacionDesechosNoPeligrosos>();
		try {
			listadesechonopeligrosos=fichaAmbientalMineria020Facade.cargarDesechosNoPeligrosos(perforacionExplorativa);
			for (PerforacionDesechosNoPeligrosos perforacionDesechosNoPeligrosos : listadesechonopeligrosos) {
				desechonopeligrosos=desechonopeligrosos+ "<tr>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getWasteType()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getValue()+" "+perforacionDesechosNoPeligrosos.getUnit()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getTreatment()+"</td>"
						+ "<td>"+perforacionDesechosNoPeligrosos.getFinalArrangement()+"</td>"
						+ "</tr>";
			}
			desechonopeligrosos=desechonopeligrosos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_desechos_no_peligrosos(desechonopeligrosos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		String desechopeligrosos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000;\">"
				+ "<tr>"
				+ "<td style=\"width: 15%\">Tipo de Desecho</td>"
				+ "<td style=\"width: 15%\">Código (AM NO. 142 o el que lo reemplace)</td>"
				+ "<td style=\"width: 15%\">CRTIB</td>"
				+ "<td style=\"width: 15%\">Cantidad Generada aproximada /Mes</td>"
				+ "<td style=\"width: 15%\">Proceso o unidad operativa</td>"
				+ "<td style=\"width: 15%\">Tipo de Eliminación o Disposición final</td>"
				+ "</tr>";
		List<PerforacionDesechosPeligrosos> listadesechopeligrosos= new ArrayList<PerforacionDesechosPeligrosos>();
		try {
			listadesechopeligrosos=fichaAmbientalMineria020Facade.cargarDesechosPeligrosos(perforacionExplorativa);
			for (PerforacionDesechosPeligrosos perforacionDesechosPeligrosos : listadesechopeligrosos) {
				desechopeligrosos=desechopeligrosos+ "<tr>"
						+ "<td>"+perforacionDesechosPeligrosos.getWasteType()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getCode()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getCrtib()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getValue()+" "+perforacionDesechosPeligrosos.getUnit()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getProcess()+"</td>"
						+ "<td>"+perforacionDesechosPeligrosos.getFinalArrangement()+"</td>"
						+ "</tr>";
			}
			desechopeligrosos=desechopeligrosos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_desechos_peligrosos(desechopeligrosos);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setMano_obra_calificada(perforacionExplorativa.getQualifiedHandWork());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setDiagrama_flujo_base(perforacionExplorativa.getBaseFlowDiagram());
		
//		punto 7
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAgua_consumo_humano(perforacionExplorativa.getWaterHumanConsumption()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAgua_perforacion(perforacionExplorativa.getWaterDrilling()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setEnergia_electrica(perforacionExplorativa.getElectricPower()?"SI":"NO");
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setCombustible(perforacionExplorativa.getFuelConsumption()?"SI":"NO");		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_agua(perforacionExplorativa.getWaterConsumption());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_agua_perforacion(perforacionExplorativa.getWaterConsumptionDrilling());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_enegia_eletrica(perforacionExplorativa.getElectricPowerConsumption());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setConsumo_combustible(perforacionExplorativa.getFuelTypeConsumption());
		
//		punto 8
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_fisica(perforacionExplorativa.getDirectPhysicalInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_biotica(perforacionExplorativa.getDirectBioticInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_directa_social(perforacionExplorativa.getDirectSocialInfluence());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfluencia_indirecta_social(perforacionExplorativa.getIndirectSocialInfluence());
		
//		punto 9
		
		ComponenteFisicoSD componenteFisico = new ComponenteFisicoSD();
		ComponenteSocialSD componenteSocial = new ComponenteSocialSD();
		componenteFisico = componenteFisicoFacade.buscarComponenteFisicoPorPerforacionExplorativa(perforacionExplorativa.getId());
		componenteSocial = componenteSocialFacade.buscarComponenteSocialPorPerforacionExplorativa(perforacionExplorativa.getId());
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPrecipitacion_anual(componenteFisico.getPrecipitacionAnual());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTemperatura_anual(componenteFisico.getTemperaturaMediaAnual());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAltitud(componenteFisico.getAltitud());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setComponente_hidrico(componenteFisico.getComponenteHidrico());
		
		
		String geomorfologia="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td style=\"width: 60%\">Pendiente</td>"
				+ "	<td style=\"width: 40%\">Seleccionado</td>"
				+ "</tr>";	
		
		List<String> codigosFisicos = new ArrayList<String>();
		Map<String, List<CatalogoGeneralFisico>> listaGeneralFisico;
		listaGeneralFisico = new HashMap<String, List<CatalogoGeneralFisico>>();
		codigosFisicos.add(TipoCatalogo.CODIGO_PENDIENTE);
		List<CatalogoGeneralFisico> fisicos = catalogoFisicoFacade.obtenerListaFisicoTipo(codigosFisicos);
		for (CatalogoGeneralFisico catalogoGeneralFisico : fisicos) {
			List<CatalogoGeneralFisico> tmp = new ArrayList<CatalogoGeneralFisico>();
			String key = catalogoGeneralFisico.getTipoCatalogo().getCodigo();
			if (listaGeneralFisico.containsKey(key)) {
				tmp = listaGeneralFisico.get(key);
			}
			tmp.add(catalogoGeneralFisico);
			listaGeneralFisico.put(key, tmp);
		}
		List<CatalogoGeneralFisico> pendienteSuelo = null;
		if (listaGeneralFisico.containsKey(TipoCatalogo.CODIGO_PENDIENTE)) {
			pendienteSuelo= listaGeneralFisico.get(TipoCatalogo.CODIGO_PENDIENTE);
		}	
		
		for(ComponenteFisicoPendienteSD pendiente : componenteFisico.getComponenteFisicoPendienteList()){
			for(CatalogoGeneralFisico pendienteCat : pendienteSuelo){
				if(pendiente.getCatalogoFisico().getId().equals(pendienteCat.getId()) && pendiente.getEstado() == true){
					pendienteCat.setSeleccionado(true);
				}
			}				
		}	
		
		for(CatalogoGeneralFisico pendiente: pendienteSuelo)
		{
			String selecc="";
			if(pendiente.isSeleccionado())
				selecc="X";
			
			geomorfologia=geomorfologia+"<tr>"
				+ "<td>"+pendiente.getDescripcion()+"</td>"
				+ "<td>"+selecc+"</td>"
			+ "</tr>";			
		}
		geomorfologia=geomorfologia+ "</table>";
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPendiente(geomorfologia);
		
		inicializarDatosGeneralesBiotico();
		coberturaVegetalList = inicializarBiotico(TipoCatalogo.CODIGO_COBERTURA_VEGETAL_SD);
		pisosZooGeograficosList = inicializarBiotico(TipoCatalogo.CODIGO_PISOS_ZOOGEOGRAFICOS_SD);
		componenteBioticoNativoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_NATIVO_SD);
		componenteBioticoIntroducidoList = inicializarBiotico(TipoCatalogo.CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD);
		aspectosEcologicosList = inicializarBiotico(TipoCatalogo.CODIGO_ASPECTOS_ECOLOGICOS_SD);
		areasSensiblesList = inicializarBiotico(TipoCatalogo.CODIGO_AREAS_SENSIBLES_SD);
		
		inicializarCobertura();
		
		listaBioticoCoberturaVegetal = new ArrayList<ComponenteBioticoSD>();
		listaBioticoPisosZooGeograficos = new ArrayList<ComponenteBioticoSD>(); 
		listaBioticoNativo = new ArrayList<ComponenteBioticoSD>();
		listaBioticoIntroducido = new ArrayList<ComponenteBioticoSD>();
		listaBioticoAspectoEcologico = new ArrayList<ComponenteBioticoSD>();
		listaBioticoAreasSensibles = new ArrayList<ComponenteBioticoSD>();
		
		
		List<ComponenteBioticoSD> listaBiotico;
		
			listaBiotico = componenteBioticoFacade.buscarComponenteBioticoPorPerforacionExplorativa(perforacionExplorativa.getId());
			for(ComponenteBioticoSD biotico : listaBiotico){
				for(CatalogoGeneralBiotico cobertura : coberturaVegetalList){
					if(cobertura.getCatalogoGeneralBioticoList() != null){
						for(CatalogoGeneralBiotico coberturaHijo : cobertura.getCatalogoGeneralBioticoList()){
							if(biotico.getCatalogoGeneralBiotico().getId().equals(coberturaHijo.getId())){
								coberturaHijo.setSeleccionado(true);
								listaBioticoCoberturaVegetal.add(biotico);
							}
						}
					}										
				}
				
				for(CatalogoGeneralBiotico pisos : pisosZooGeograficosList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(pisos.getId())){
						pisos.setSeleccionado(true);
						listaBioticoPisosZooGeograficos.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico nativo : componenteBioticoNativoList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(nativo.getId())){
						nativo.setSeleccionado(true);
						listaBioticoNativo.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico introducido : componenteBioticoIntroducidoList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(introducido.getId())){
						introducido.setSeleccionado(true);
						listaBioticoIntroducido.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico ecologico : aspectosEcologicosList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(ecologico.getId())){
						ecologico.setSeleccionado(true);
						listaBioticoAspectoEcologico.add(biotico);
					}
				}
				
				for(CatalogoGeneralBiotico area : areasSensiblesList){
					if(biotico.getCatalogoGeneralBiotico().getId().equals(area.getId())){
						area.setSeleccionado(true);
						listaBioticoAreasSensibles.add(biotico);
					}
				}				
			}
			
			String coberturavegetal="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td style=\"width: 60%\">Cobertura Nivel II</td>"
				+ "	<td style=\"width: 40%\">Seleccionado</td>"
				+ "</tr>";
			
			for(CatalogoGeneralBiotico coberturaVegetalPadre: coberturaVegetalPadreList)
			{
				coberturavegetal=coberturavegetal+"<tr><td style=\"text-align: left;\" colspan=\"2\">"+coberturaVegetalPadre.getDescripcion()+"</td></tr>";
				for(CatalogoGeneralBiotico coberturaVegetalhijos: coberturaVegetalPadre.getCatalogoGeneralBioticoList())
				{
					String selecc="";
					if(coberturaVegetalhijos.isSeleccionado())
						selecc="X";
					coberturavegetal=coberturavegetal+"<tr>"
							+ "<td style=\"text-align: left;\">"+coberturaVegetalhijos.getDescripcion()+"</td>"
							+ "<td>"+selecc+"</td>"
						+ "</tr>";	
				}
			}
			coberturavegetal=coberturavegetal+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_cobertura_v(coberturavegetal);
			
			String zooGeograficos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 40%\">Pisos Zoo Geográficos</td>"
					+ "	<td style=\"width: 40%\">Simbología</td>"
					+ "	<td style=\"width: 20%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico pisosZooGeograficos: pisosZooGeograficosList)
			{
				String selecc="";
				if(pisosZooGeograficos.isSeleccionado())
					selecc="X";
				zooGeograficos=zooGeograficos+"<tr>"
						+ "<td style=\"text-align: left;\">"+pisosZooGeograficos.getDescripcion()+"</td>"
						+ "<td style=\"text-align: left;\">"+pisosZooGeograficos.getAyuda()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			zooGeograficos=zooGeograficos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_pisos_zoogeograficos(zooGeograficos);
			
			
			String bioticoNativo="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td >Componentes bióticos nativos</td>"
					+ "	<td>Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico componenteBioticoNativo: componenteBioticoNativoList)
			{
				String selecc="";
				if(componenteBioticoNativo.isSeleccionado())
					selecc="X";
				bioticoNativo=bioticoNativo+"<tr>"
						+ "<td style=\"text-align: left;\">"+componenteBioticoNativo.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			bioticoNativo=bioticoNativo+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_nativo(bioticoNativo);
			
			String bioticoIntroducido="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Componente biótico introducido</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico componenteBioticoIntroducido: componenteBioticoIntroducidoList)
			{
				String selecc="";
				if(componenteBioticoIntroducido.isSeleccionado())
					selecc="X";
				bioticoIntroducido=bioticoIntroducido+"<tr>"
						+ "<td style=\"text-align: left;\">"+componenteBioticoIntroducido.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			bioticoIntroducido=bioticoIntroducido+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_introducido(bioticoIntroducido);
			
			
			
			String aspectosEcologicos="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico aspectosEcologicosL: aspectosEcologicosList)
			{
				String selecc="";
				if(aspectosEcologicosL.isSeleccionado())
					selecc="X";
				aspectosEcologicos=aspectosEcologicos+"<tr>"
						+ "<td style=\"text-align: left;\">"+aspectosEcologicosL.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			aspectosEcologicos=aspectosEcologicos+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_aspectos_ecologicos(aspectosEcologicos);
			
			
			String areasSensibles="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
					+ "<tr>"
					+ " <td style=\"width: 60%\">Aspectos ecológicos</td>"
					+ "	<td style=\"width: 40%\">Seleccionado</td>"
					+ "</tr>";
			for(CatalogoGeneralBiotico areasSensiblesL: areasSensiblesList)
			{
				String selecc="";
				if(areasSensiblesL.isSeleccionado())
					selecc="X";
				areasSensibles=areasSensibles+"<tr>"
						+ "<td style=\"text-align: left;\">"+areasSensiblesL.getDescripcion()+"</td>"
						+ "<td>"+selecc+"</td>"
					+ "</tr>";	
			}
			areasSensibles=areasSensibles+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_componente_biotico_areas_sensibles(areasSensibles);
			
		
		
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setComunidades(componenteSocial.getComunidades());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPoblaciones(componenteSocial.getPoblaciones());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setPredios(componenteSocial.getPredios());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura(componenteSocial.getInfraestructura());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setAsentamientos_grupos_etnicos(componenteSocial.getAsentamientosGruposEtnicos());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setInfraestructura_salud_publica(componenteSocial.getInfraestructuraSaludPublica());
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setElementos_culturales(componenteSocial.getElementosCulturales());				
		
//		punto 11
		List<EntityActividad> listaActividades;		
		String impactosAmbientales="<table width=\"70%\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>Sub Actividad</td>"
				+ "	<td>Equipos / Herramientas</td>"
				+ "	<td>Impactos Identificados</td>"
				+ "	<td>Descripción del impacto</td>"
				+ "	<td>Agua</td>"
				+ "	<td>Aire</td>"
				+ "	<td>Suelo</td>"
				+ "	<td>Biotico</td>"
				+ "	<td>Paisaje</td>"
				+ "	<td>Social</td>"				
				+ "</tr>";
		try {
			listaActividades = actividadesImpactoFacade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
			for (EntityActividad objListaImpacto: listaActividades){				
				impactosAmbientales=impactosAmbientales+"<tr><td style=\"text-align: left;\" colspan=\"10\"><strong>"+objListaImpacto.getActividad()+"</strong></td></tr>";				
				for (ActividadesImpactoProyecto objImpacto: objListaImpacto.getSubactividades()){
					String agua=objImpacto.isAgua()?"X":"";
					String aire=objImpacto.isAire()?"X":"";
					String suelo=objImpacto.isSuelo()?"X":"";
					String biotico=objImpacto.isBiotico()?"X":"";
					String paisaje=objImpacto.isPaisaje()?"X":"";
					String social=objImpacto.isSocial()?"X":"";
					impactosAmbientales=impactosAmbientales+"<tr>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getActividad().getNombre()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getHerramientas()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getImpacto()+"</td>"
							+ "<td style=\"text-align: left;\">"+objImpacto.getDescripcionImpacto()+"</td>"
							+ "<td>"+agua+"</td>"
							+ "<td>"+aire+"</td>"
							+ "<td>"+suelo+"</td>"
							+ "<td>"+biotico+"</td>"
							+ "<td>"+paisaje+"</td>"
							+ "<td>"+social+"</td>"
							+ "</tr>";
				}
			}
			impactosAmbientales=impactosAmbientales+ "</table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_identificacion_impactos_ambientales(impactosAmbientales);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
//		punto 12
		String pma="<table width=\"700px\" border=\"1\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ " <td>MEDIDAS PROPUESTAS</td>"
				+ "	<td>INDICADOR</td>"
				+ "	<td>MEDIO DE VERIFICACIÓN</td>"
				+ "	<td>FRECUENCIA</td>"
				+ "	<td>PRESUPUESTO</td>"				
				+ "</tr>";
		Map<String, List<PlanManejoAmbientalProyecto>> parameters = new HashMap<String, List<PlanManejoAmbientalProyecto>>();
		parameters = planManejoAmbiental020Facade.obtenerActividdaesProyecto(perforacionExplorativa.getId());
        for (Map.Entry<String, List<PlanManejoAmbientalProyecto>> entry : parameters.entrySet()) {
            String key = entry.getKey();
            pma=pma+"<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"+key+"</strong></td></tr>";
            List<PlanManejoAmbientalProyecto> values = entry.getValue();
            for (PlanManejoAmbientalProyecto planManejoAmbientalProyecto : values) {
            	if(planManejoAmbientalProyecto.getPlanManejoAmbiental020()==null)
            	{
            		pma=pma+"<tr>"
            				+ " <td style=\"width: 20%\">"+planManejoAmbientalProyecto.getMedidasPropuestas()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getIndicador()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getVerificacionMedios()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getFrecuencia()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getCalificacion()+"</td>"				
            				+ "</tr>";
            	}
            	else
            	{
            		if(planManejoAmbientalProyecto.getCalificacion()==null)
            			pma=pma+"<tr><td style=\"text-align: center;\" colspan=\"5\"><strong>"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getDescripcion()+"</strong></td></tr>";
            		else
            		{
            		pma=pma+"<tr>"
            				+ " <td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getMedidasPropuestas()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getIndicador()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getVerificacionMedios()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getPlanManejoAmbiental020().getFrecuencia()+"</td>"
            				+ "	<td style=\"width: 20%\">"+planManejoAmbientalProyecto.getCalificacion()+"</td>"				
            				+ "</tr>";
            		}
            	}
			}
        }
        pma=pma+ "</table>";
		getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_prevencion_impactos(pma);
//		punto 13
		double total;
		String cronogramaPma="<table width=\"700px\" border=\"1\" cellpadding=\"5\" bordercolor=\"#000000\" style=\"border-collapse:collapse;border-color:#000000; text-align: center;\">"
				+ "<tr>"
				+ "<td rowspan=\"2\">SUB PLAN-ACTIVIDADES</td>"
				+ "<td colspan=\"12\">MESES</td>"
				+ "<td rowspan=\"2\">VALOR</td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td>1</td>"
				+ "<td>2</td>"
				+ "<td>3</td>"
				+ "<td>4</td>"
				+ "<td>5</td>"
				+ "<td>6</td>"
				+ "<td>7</td>"
				+ "<td>8</td>"
				+ "<td>9</td>"
				+ "<td>10</td>"
				+ "<td>11</td>"
				+ "<td>12</td>"
				+ "</tr>";
		List<PerforacionCronogramaPma> listaCronogramaPma = new ArrayList<PerforacionCronogramaPma>();
		try {
			listaCronogramaPma=fichaAmbientalMineria020Facade.cargarCronogramaPma(perforacionExplorativa);
			total = 0;
			for (PerforacionCronogramaPma perforacionCronogramaPma : listaCronogramaPma) {
				String m1=perforacionCronogramaPma.getMonth1()?"X":"";
				String m2=perforacionCronogramaPma.getMonth2()?"X":"";
				String m3=perforacionCronogramaPma.getMonth3()?"X":"";
				String m4=perforacionCronogramaPma.getMonth4()?"X":"";
				String m5=perforacionCronogramaPma.getMonth5()?"X":"";
				String m6=perforacionCronogramaPma.getMonth6()?"X":"";
				String m7=perforacionCronogramaPma.getMonth7()?"X":"";
				String m8=perforacionCronogramaPma.getMonth8()?"X":"";
				String m9=perforacionCronogramaPma.getMonth9()?"X":"";
				String m10=perforacionCronogramaPma.getMonth10()?"X":"";
				String m11=perforacionCronogramaPma.getMonth11()?"X":"";
				String m12=perforacionCronogramaPma.getMonth12()?"X":"";
				cronogramaPma=cronogramaPma+"<tr>"
						+ "<td style=\"text-align: left;\">"+perforacionCronogramaPma.getTipoPlanManejoAmbiental().getTipo()+"</td>"						
						+ "<td>"+m1+"</td>"
						+ "<td>"+m2+"</td>"
						+ "<td>"+m3+"</td>"
						+ "<td>"+m4+"</td>"
						+ "<td>"+m5+"</td>"
						+ "<td>"+m6+"</td>"
						+ "<td>"+m7+"</td>"
						+ "<td>"+m8+"</td>"
						+ "<td>"+m9+"</td>"
						+ "<td>"+m10+"</td>"
						+ "<td>"+m11+"</td>"
						+ "<td>"+m12+"</td>"
						+ "<td>"+perforacionCronogramaPma.getBudget()+"</td>"
						+ "</tr>";	
				total+=perforacionCronogramaPma.getBudget();
			}
			cronogramaPma=cronogramaPma+"<tr>"
					+ "<td colspan=\"13\" style=\"text-align: right;\">TOTAL</td>"
					+ "<td>"+total+"</td>"
					+ "</tr></table>";
			getImpresionFichaMineriaBean().getEntityFichaMineria020Reporte().setTabla_cronograma_actividades(cronogramaPma);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    }
}