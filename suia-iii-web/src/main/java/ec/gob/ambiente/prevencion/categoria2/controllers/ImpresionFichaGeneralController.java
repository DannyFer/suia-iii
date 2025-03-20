/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.prevencion.categoria2.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

import org.apache.commons.lang.StringEscapeUtils;

import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.prevencion.categoria2.bean.ImpresionFichaGeneralBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.catalogos.facade.CatalogoSocialFacade;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.CatalogoCategoriaFase;
import ec.gob.ambiente.suia.domain.CatalogoGeneral;
import ec.gob.ambiente.suia.domain.CatalogoGeneralSocial;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Coordenada;
import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.FichaAmbientalPma;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.ProyectoDesechoPeligroso;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.TipoCatalogo;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.dto.EntityCoordenadaReporte;
import ec.gob.ambiente.suia.dto.EntityDetalleCronogramaValorado;
import ec.gob.ambiente.suia.dto.EntityPlanManejoAmbiental;
import ec.gob.ambiente.suia.dto.EntityUbicacionReporte;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.facade.InventarioForestalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FaseFichaAmbientalFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.FichaAmbientalPmaFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.ImpresionFichaGeneralFacade;
import ec.gob.ambiente.suia.proyectodesechopeligroso.facade.ProyectoDesechoPeligrosoFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.ubicaciongeografica.facade.UbicacionGeograficaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilFichaMineria;

/**
 * @author Jonathan Guerrero
 */
@ManagedBean
@ViewScoped
public class ImpresionFichaGeneralController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6071700976638414978L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ImpresionFichaGeneralController.class);
    private static final Integer CODIGO_DESECHOS_PELIGROSOS_ESPECIALES = 49;
    private static final String NO_APLICA = "<span style=\"font-size: small;font-weight: bold;background-color: inherit;\"> (No aplica para este tipo de sector) </span>";
    private static final String NO_APLICA_VIA_ACCESO = "No aplica";
    private static final String VER_ANEXO = "<span style=\"font-size: small;font-weight: bold;background-color: inherit;\"> (Ver Anexo 1) </span>";
    private static final String SI = "Sí";
    private static final String NO = "No";
    private static final String ACTIVO = "Activo";
    private static final String INACTIVO = "Inactivo";
    private static final String SIN_OBSERVACIONES = "No tiene observaciones";
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalFacade;
    @EJB
    private InventarioForestalPmaFacade inventarioForestalPmaFacade;
    @EJB
    private ContactoFacade contactoFacade;
    @EJB
    private UbicacionGeograficaFacade ubicacionGeograficaFacade;
    @EJB
    private ImpresionFichaGeneralFacade impresionFichaGeneralFacade;
    @EJB
    private CatalogoSocialFacade catalogoSocialFacade;
    @EJB
    private ProyectoDesechoPeligrosoFacade proyectoDesechoPeligrosoFacade;
    @EJB
    private FaseFichaAmbientalFacade faseFichaAmbientalFacade;
    @EJB
    private FichaAmbientalPmaFacade fichaAmbientalPmaFacade;
    @EJB
    private DocumentosFacade documentosFacade;

    @EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    AlfrescoServiceBean alfrescoServiceBean;
    @Getter
    @Setter
    private ImpresionFichaGeneralBean impresionFichaGeneralBean;
    @Getter
    @Setter
    private FichaAmbientalPma ficha;
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;
    @Getter
    @Setter
    @ManagedProperty("#{proyectosBean}")
    private ProyectosBean proyectosBean;
    @Getter
    @Setter
    @ManagedProperty("#{impactoAmbientalPmaController}")
    private ImpactoAmbientalPmaController impactoAmbientalPmaController;
    @Getter
    @Setter
    @ManagedProperty("#{descripcionProcesoPmaImprimirController}")
    private DescripcionProcesoPmaImprimirController descripcionProcesoPmaImprimirController;
    @Getter
    @Setter
    private String direccion;
    @Getter
    @Setter
    private String correo;
    @Getter
    @Setter
    private String telefono;
    @Getter
    @Setter
    @ManagedProperty("#{descripcionAreaImplantacionPmaImprimirController}")
    private DescripcionAreaImplantacionPmaImprimirController descripcionAreaImplantacionPmaImprimirController;
    
    @EJB
    private CrudServiceBean crudServiceBean;

    @PostConstruct
    private void cargarDatos() {
        long t1 = System.currentTimeMillis();
        proyecto = proyectosBean.getProyecto();
        ficha = fichaAmbientalPmaFacade.getFichaAmbientalPorIdProyecto(proyecto
                .getId());
        long t2 = System.currentTimeMillis();
        System.out.println("ImpresionFichaGeneralController 1 " + (t2 - t1));
        cargarDatosContacto();

        long t3 = System.currentTimeMillis();
        System.out.println("ImpresionFichaGeneralController 2 " + (t3 - t2));
        setImpresionFichaGeneralBean(new ImpresionFichaGeneralBean());
        getImpresionFichaGeneralBean().iniciarDatos();
        getImpresionFichaGeneralBean().setFichaAmbiental(ficha);
        getImpresionFichaGeneralBean()
                .setPlantillaHtml(
                        UtilFichaMineria.extraeHtml(JsfUtil
                                .devolverPathReportesHtml("fichaAmbientalGeneral.html")));

        long t4 = System.currentTimeMillis();
        System.out.println("ImpresionFichaGeneralController 3 " + (t4 - t3));
    }

    public File imprimirFichaPdf() {
        try {
            imprimirFichaConfigurar();
//            String parametros[] = {proyectosBean.getProyecto().getCodigo(),
//                    JsfUtil.devuelveFechaEnLetrasSinHora(new Date())};
            String parametros[] = {proyectosBean.getProyecto().getCodigo(),"11 de Junio de 2020"};
            UtilFichaMineria.setAreaResponsable(proyectosBean.getProyecto()
                    .getAreaResponsable());
            return UtilFichaMineria.generarFichero(
                    getImpresionFichaGeneralBean().getPlantillaHtml(),
                    getImpresionFichaGeneralBean()
                            .getEntityFichaGeneralReporte(), "fichaGeneral",
                    true, proyectosBean.getProyecto().getAreaResponsable(),
                    parametros);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void imprimirFichaConfigurar() {
        try {
            Area areaResponsable = getImpresionFichaGeneralBean()
                    .getFichaAmbiental().getProyectoLicenciamientoAmbiental()
                    .getAreaResponsable();
            if (areaResponsable != null) {
           	 /**
   		     * Nombre:SUIA
   		     * Descripción: obtener el logo de acuerdo al area
   		     * ParametrosIngreso:
   		     * PArametrosSalida:
   		     * Fecha:17/09/2015
   		     * */      	            	
				String nombre_pie = null;
				String nombre_logo = null;
				URL pie = null;
				URL logo = null;
                byte[] pie_datos=null;
				UtilDocumento utilDocumento=new UtilDocumento();
                nombre_pie = "pie__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
                nombre_logo = "logo__" + areaResponsable.getAreaAbbreviation().replace("/", "_") + ".png";
                pie = utilDocumento.getRecursoImage("ente/" + nombre_pie);
                if (pie == null) {
                    try {
                        pie_datos = documentosFacade.descargarDocumentoPorNombre(nombre_pie);
                        //File fi = new File(JsfUtil.devolverPathImagenMae());
                        //byte[] fileContent = Files.readAllBytes(fi.toPath());
                        File archivo = new File(JsfUtil.devolverPathImagenEnte(nombre_pie));
                        //File archivo = new File(JsfUtil.devolverPathImagenMae());
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(pie_datos);
                        //file.write(fileContent);
                        file.close();
                    } catch (Exception e) {
                        LOG.error("Error al obtener la imagen del pie para el área "
                                        + areaResponsable.getAreaAbbreviation()
                                + " en /Documentos Fijos/DatosEnte/" + nombre_pie);
                        File fi = new File(JsfUtil.devolverPathImagenMae());
                        byte[] fileContent = Files.readAllBytes(fi.toPath());
                        File archivo = new File(JsfUtil.devolverPathImagenMae());
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(fileContent);
                        file.close();
                    }
                }

                logo = UtilDocumento.getRecursoImage("ente/" + nombre_logo);
                if (logo == null) {
                    try {
                        System.out.println(nombre_logo);
                        byte[] logo_datos = documentosFacade.descargarDocumentoPorNombre(nombre_logo);
                        //File fi = new File(JsfUtil.devolverPathImagenMae());
                        //byte[] fileContent = Files.readAllBytes(fi.toPath());
                        File archivo = new File(JsfUtil.devolverPathImagenEnte(nombre_logo));
                        //File archivo = new File(JsfUtil.devolverPathImagenMae());
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(logo_datos);
                        //file.write(fileContent);
                        file.close();
                    } catch (Exception e) {
                        LOG.error("Error al obtener la imagen del logo para el área "
                                        + areaResponsable.getAreaAbbreviation()
                                + " en /Documentos Fijos/DatosEnte/" + nombre_logo);
                        File fi = new File(JsfUtil.devolverPathImagenMae());
                        byte[] fileContent = Files.readAllBytes(fi.toPath());
                        File archivo = new File(JsfUtil.devolverPathImagenMae());
                        FileOutputStream file = new FileOutputStream(archivo);
                        file.write(fileContent);
                        file.close();
                    }
                }
            
                /**
         		  * FIN obtener el logo de acuerdo al area
         		  */

//                URL logo = UtilDocumento.getRecursoImage("ente/" + nombre_logo);
                /**
    		     * Nombre:SUIA
    		     * Descripción: obtener el logo de acuerdo al area
    		     * ParametrosIngreso:
    		     * PArametrosSalida:
    		     * Fecha:17/09/2015
    		     * */
//                if (logo == null) {
//                    try {
//                        System.out.println(nombre_logo);
//                        byte[] logo_datos = documentosFacade.descargarDocumentoPorNombre(nombre_logo);
////                        File fi = new File(JsfUtil.devolverPathImagenMae());
////                        byte[] fileContent = Files.readAllBytes(fi.toPath());
//                        File archivo = new File(JsfUtil.devolverPathImagenEnte(nombre_logo));
////                        File archivo = new File(JsfUtil.devolverPathImagenMae());
//                        FileOutputStream file = new FileOutputStream(archivo);
//                        file.write(logo_datos);
////                        file.write(fileContent);
//                        file.close();
//                    } catch (Exception e) {
//                        LOG.error("Error al obtener la imagen del logo para el área "
//                                        + areaResponsable.getAreaAbbreviation()
//                                + " en /Documentos Fijos/DatosEnte/" + nombre_logo);
//                    }
//                }
                /**
         		  * FIN obtener el logo de acuerdo al area
         		  */
            }

			getImpresionFichaGeneralBean()
					.getEntityFichaGeneralReporte()
					.setNombreProyectoObraActividad(
							StringEscapeUtils
									.escapeHtml(getImpresionFichaGeneralBean()
											.getFichaAmbiental()
											.getProyectoLicenciamientoAmbiental()
											.getNombre()));
			getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
					.setNombreActividadEconomica(
							getImpresionFichaGeneralBean().getFichaAmbiental()
									.getProyectoLicenciamientoAmbiental()
									.getCatalogoCategoria().getDescripcion());
			List<Coordenada> coordenadas = fichaAmbientalFacade
					.getCoordenadasProyectoPma(getImpresionFichaGeneralBean()
							.getFichaAmbiental()
							.getProyectoLicenciamientoAmbiental().getId());
			List<EntityCoordenadaReporte> coordenadasAux = new ArrayList<EntityCoordenadaReporte>();
			for (Coordenada c : coordenadas) {
				EntityCoordenadaReporte ecr = new EntityCoordenadaReporte();
				ecr.setCoordenadaX(String.valueOf(c.getX()));
				ecr.setCoordenadaY(String.valueOf(c.getY()));
				ecr.setAltitud(String.valueOf(getImpresionFichaGeneralBean()
						.getFichaAmbiental()
						.getProyectoLicenciamientoAmbiental().getAltitud()));
				coordenadasAux.add(ecr);
			}
			String[] columnasCoordenadas = { "Este (X)", "Norte (Y)", "Altitud" };
			String[] detalleCoordenadas = { "CoordenadaX", "CoordenadaY",
					"Altitud" };
			getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
					.setDetalleCoordenadas(
							UtilFichaMineria.devolverDetalle(null,
									columnasCoordenadas, coordenadasAux,
									detalleCoordenadas, null));
			List<CatalogoGeneral> tipoPredios = fichaAmbientalFacade
					.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
							getImpresionFichaGeneralBean().getFichaAmbiental()
									.getId(), TipoCatalogo.SITUACION_PREDIO,
							TipoCatalogo.CODIGO_PREDIO_PRIMARIO);
			StringBuilder alquilerRecuperado = new StringBuilder();
			Boolean otrosPredios = false;
			for (CatalogoGeneral cg : tipoPredios) {
				String descripcion = cg.getDescripcion();
				if (descripcion.equals("Otros")) {
					otrosPredios = true;
					descripcion += ": ";
				}
				alquilerRecuperado.append("<span style=\"font-size: small\">")
						.append(descripcion).append("<br/></span>");
			}
			if (otrosPredios) {
				List<CatalogoGeneral> prediosOtros = fichaAmbientalPmaFacade
						.getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
								ficha.getId(), TipoCatalogo.SITUACION_PREDIO,
								TipoCatalogo.CODIGO_PREDIO_SECUDARIO);
				for (CatalogoGeneral cg : prediosOtros) {
					alquilerRecuperado
							.append("<span style=\"font-size: small; padding-left:5px;\">")
							.append(cg.getDescripcion()).append("<br/></span>");
				}
			}
			getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
					.setSituacionPredio(alquilerRecuperado.toString());
			getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
					.setDireccionProyecto(
							getImpresionFichaGeneralBean().getFichaAmbiental()
									.getProyectoLicenciamientoAmbiental()
									.getDireccionProyecto());
			// getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			// .setEstadoProyecto(
			// getImpresionFichaGeneralBean().getFichaAmbiental()
			// .getProyectoLicenciamientoAmbiental()
			// .getEstado() ? ACTIVO : INACTIVO);

            List<CatalogoCategoriaFase> fases = faseFichaAmbientalFacade
                    .obtenerCatalogoCategoriaFasesPorFicha(ficha.getId());
            String fases_suma = " ";
            for (CatalogoCategoriaFase f : fases) {
                fases_suma += "- " + f.toString() + "<br/>";
            }

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setEstadoProyecto(fases_suma);

			/*
             * catalogoGeneralPmaBean
			 * .setActividadesSeleccionadas(faseFichaAmbientalFacade
			 * .obtenerCatalogoCategoriaFasesPorFicha(ficha .getId()));
			 */
            /*
			 * List<CatalogoCategoriaFase> catalogo = faseFichaAmbientalFacade
			 * .obtenerCatalogoCategoriaFasesPorFicha
			 * (getImpresionFichaGeneralBean().getFichaAmbiental() .getId());
			 */
			/*
			 * String a = ""; for (CatalogoCategoriaFase cat: catalogo){ a +=
			 * cat.toString() }
			 */
			/*
			 * faseFichaAmbientalFacade
			 * .obtenerCatalogoCategoriaFasesPorFicha(ficha .getId()));
			 */
			/*
			 * getImpresionFichaGeneralBean() .getEntityFichaGeneralReporte()
			 * .setObservacionesSituacionPredio(
			 * getImpresionFichaGeneralBean().getFichaAmbiental()
			 * .getObservacionesPredio().isEmpty() ? SIN_OBSERVACIONES :
			 * getImpresionFichaGeneralBean() .getFichaAmbiental()
			 * .getObservacionesPredio());
			 *
			 *
			 *
			 * <table align="left" border="0" cellpadding="1" cellspacing="1"
			 * style="width: 100%"> <tbody> <tr> <td style="width: 100%"><span
			 * style="font-size: small">Observaciones:
			 * $F{observacionesSituacionPredio}<br/> </span></td> </tr> </tbody>
			 * </table>
			 */
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAreaProyecto(new Double(proyecto.getArea()).toString());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAreaTotalProyecto(
                            new Double(proyecto.getArea()).toString());

            List<UbicacionesGeografica> listaUbicacionProyecto = (ubicacionGeograficaFacade
                    .listarPorProyecto(getImpresionFichaGeneralBean()
                            .getFichaAmbiental()
                            .getProyectoLicenciamientoAmbiental()));
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
                        .append("<span style=\"font-size: small\">")
                        .append(ubi.getUbicacionesGeografica()
                                .getUbicacionesGeografica().getRegion()
                                .getNombre()).append("<br/></span>");
            }
            String[] columnas = {"Provincia", "Cantón", "Parroquia"};
            String[] detalle = {"Provincia", "Canton", "Parroquia"};
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setDetalleUbicacionGeografica(
                            UtilFichaMineria.devolverDetalle(null, columnas,
                                    listaUbicacion, detalle, null));
			/*
			 * List<CatalogoGeneral> tiposZonas = fichaAmbientalFacade
			 * .getCatalogoGeneralFichaPorIdFichaPorTipoPorCodigo(
			 * getImpresionFichaGeneralBean().getFichaAmbiental() .getId(),
			 * TipoCatalogo.TIPO_POBLACION, TipoCatalogo.CODIGO_TIPO_ZONA);
			 * /*StringBuilder tipoZona = new StringBuilder(); for
			 * (CatalogoGeneral cg : tiposZonas) {
			 * tipoZona.append("<span style=\"font-size: small\">")
			 * .append(cg.getDescripcion()).append("<br/></span>"); }
			 * getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			 * .setTipoZona(tipoZona.toString());
			 */
			/*
			 * getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			 * .setTipoZona
			 * (getImpresionFichaGeneralBean().getFichaAmbiental().getTipoPoblacion
			 * ().toString());
			 */
            if (getImpresionFichaGeneralBean().getFichaAmbiental()
                    .getProyectoLicenciamientoAmbiental().getTipoPoblacion() != null) {
                getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                        .setTipoZona(
                                getImpresionFichaGeneralBean()
                                        .getFichaAmbiental()
                                        .getProyectoLicenciamientoAmbiental()
                                        .getTipoPoblacion().getNombre());
            } else {
                getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                        .setTipoZona("-");
            }
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setDatosPromotor(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getUsuario().getPersona().getNombre());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setCorreoElectronicoPromotor(correo);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setDomicilioPromotor(direccion);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setTelefonoPromotor(telefono);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAreaProyecto(
                            String.valueOf(getImpresionFichaGeneralBean()
                                    .getFichaAmbiental()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getArea()));
            List<CatalogoGeneral> tiposInfraestructuras = fichaAmbientalFacade
                    .getCatalogoGeneralFichaPorIdFichaPorTipo(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getId(), TipoCatalogo.TIPO_INFRAESTRUCTURA);
            StringBuilder tipoInfraestructura = new StringBuilder();
            for (CatalogoGeneral cg : tiposInfraestructuras) {
                tipoInfraestructura.append("<span style=\"font-size: small\">")
                        .append(cg.getDescripcion()).append("<br/></span>");
            }
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setInfraestructura(tipoInfraestructura.toString());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAreaTotalProyecto(
                            String.valueOf(getImpresionFichaGeneralBean()
                                    .getFichaAmbiental()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getArea()));

			/*
			 * getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			 * .setDescripcionZona(getImpresionFichaGeneralBean()
			 * .getFichaAmbiental().getDescripcionZona());
			 *
			 * <td style="width: 66%" colspan="2"><span
			 * style="font-size: small">Descripción de la
			 * zona:$F{descripcionZona}<br/> </span></td>
			 */

            getImpresionFichaGeneralBean()
                    .getEntityFichaGeneralReporte()
                    .setAreaImplantacion(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getAreaImplantacion().toString()
                                    + " "
                                    + getImpresionFichaGeneralBean()
                                    .getFichaAmbiental()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getUnidad());
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setUnidadProyecto(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getProyectoLicenciamientoAmbiental()
                                    .getUnidad());

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAguaPotable(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getAguaPotable() ? SI : NO);

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAlcantarillado(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getAlcantarillado() ? SI : NO);

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setConsumoAgua(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getAguaPotable() ? String
                                    .valueOf(getImpresionFichaGeneralBean()
                                            .getFichaAmbiental()
                                            .getConsumoAgua()) : "0");
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setEnergiaElectrica(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getEnergiaElectrica() ? SI : NO);
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setConsumoEnergia(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getEnergiaElectrica() ? String
                                    .valueOf(getImpresionFichaGeneralBean()
                                            .getFichaAmbiental()
                                            .getConsumoElectrico()) : "0");

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setAccesoVehicular(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getAccesoVehicular() ? SI : NO);

            List<CatalogoGeneralSocial> tiposViasAccesos = catalogoSocialFacade
                    .obtenerListaSocialPorFichaPorTipoCatalogo(
                            getImpresionFichaGeneralBean().getFichaAmbiental()
                                    .getId(), TipoCatalogo.TIPO_VIA);
            StringBuilder tipoViasAcceso = new StringBuilder();
            for (CatalogoGeneralSocial cg : tiposViasAccesos) {
                tipoViasAcceso.append("<span style=\"font-size: small\">")
                        .append(cg.getDescripcion()).append("<br/></span>");
            }
            if (getImpresionFichaGeneralBean().getFichaAmbiental().getTipoVia() != null) {
                getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                        .setViaAcceso(
                                getImpresionFichaGeneralBean()
                                        .getFichaAmbiental().getTipoVia()
                                        .toString());

            } else {
                getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                        .setViaAcceso(NO_APLICA_VIA_ACCESO);

            }
            // /getImpresionFichaGeneralBean().getFichaAmbiental().getTipoPoblacion().toString()
			/*
			 * getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			 * .setViaAcceso( getImpresionFichaGeneralBean().getFichaAmbiental()
			 * .getAccesoVehicular() ? tipoViasAcceso .toString() :
			 * NO_APLICA_VIA_ACCESO);
			 */
			/*
			 * getImpresionFichaGeneralBean() .getEntityFichaGeneralReporte()
			 * .setObservaciones(
			 * getImpresionFichaGeneralBean().getFichaAmbiental()
			 * .getObservacionesEspacioFisico().isEmpty() ? SIN_OBSERVACIONES :
			 * getImpresionFichaGeneralBean() .getFichaAmbiental()
			 * .getObservacionesEspacioFisico());
			 * getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
			 * .setDescripcionProyecto(proyectosBean
			 * .getProyecto().getResumen());
			 *
			 * <table align="left" border="0" cellpadding="1" cellspacing="1"
			 * style="width: 100%"> <tbody> <tr> <td style="width: 100%"><span
			 * style="font-size: small">Observaciones: $F{observaciones}<br/>
			 * </span></td> </tr> </tbody> </table>
			 */

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setDesechosProyecto(obtenerDesechosProyecto());

			getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
					.setDescripcionProyecto(
							StringEscapeUtils.escapeHtml(proyectosBean
									.getProyecto().getResumen()));

            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setDescripcionProceso(
                            descripcionProcesoPmaImprimirController
                                    .processAction(proyectosBean.getProyecto(),
                                            getImpresionFichaGeneralBean()
                                                    .getFichaAmbiental()));
            getImpresionFichaGeneralBean()
                    .getEntityFichaGeneralReporte()
                    .setDescripcionAreaImplantacion(
                            descripcionAreaImplantacionPmaImprimirController
                                    .processAction(getImpresionFichaGeneralBean()
                                            .getFichaAmbiental()));
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setPrincipalesImpactosAmbientales(
                            impactoAmbientalPmaController.generarImpreso());
            getImpresionFichaGeneralBean()
                    .getEntityFichaGeneralReporte()
                    .setPlanManejoAmbiental(
                            obtenerPlanManejoAmbiental(getImpresionFichaGeneralBean()
                                    .getFichaAmbiental().getId()));
            getImpresionFichaGeneralBean()
                    .getEntityFichaGeneralReporte()
                    .setCronogramaValoradoPlanManejoAmbiental(
                            obtenerCronogramaValorado(getImpresionFichaGeneralBean()
                                    .getFichaAmbiental().getId()));
            getImpresionFichaGeneralBean()
                    .getEntityFichaGeneralReporte()
                    .setInventarioForestal(
                            obtenerInventarioForestal(getImpresionFichaGeneralBean()
                                    .getFichaAmbiental().getId()));
            
            String observaciones="";
            String observacionesTitulo="";
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("fichaAmbientalPma", getImpresionFichaGeneralBean().getFichaAmbiental());

            List<CronogramaValoradoPma> listaPma = crudServiceBean.findByNamedQueryGeneric(CronogramaValoradoPma.OBTENER_POR_FICHA,parametros);
            if(listaPma.size()>0){
                for (CronogramaValoradoPma c1 : listaPma) {
                    if(c1.getPlan()==null){
                    observaciones=c1.getActividad();
                    if (!c1.getActividad().equals(""))
                    	observacionesTitulo="Observaciones";
                    }
                }
            }
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte().setObservacionesPMA("<div style=\"text-align: center;\">"
                    + "<span style=\"font-weight: bold;font-size: small\">"+observacionesTitulo+"<br/></span></div>"
                    + "<div style=\"text-align: justify;\"> <p style=\"font-size: small; text-align: justify;\">"+observaciones+"<br/></p></div>");
            
            getImpresionFichaGeneralBean().getEntityFichaGeneralReporte()
                    .setMarcoLegalReferencial(VER_ANEXO);

        } catch (Exception e) {
            LOG.error(e, e);
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

    private String obtenerPlanManejoAmbiental(Integer idFicha) {
        try {
            List<EntityPlanManejoAmbiental> listadetalle = impresionFichaGeneralFacade
                    .obtenerPLanManejoAmbientalPorFicha(idFicha);
            String[] columnas = {"Descripción", "Fecha desde", "Fecha hasta"};
            String[] ordenColumnas = {"Plan", "FechaDesde", "FechaHasta"};
            return UtilFichaMineria.devolverDetalle(null, columnas,
                    listadetalle, ordenColumnas, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String obtenerCronogramaValorado(Integer idFicha) {
        try {
            List<EntityDetalleCronogramaValorado> listaDetalles = impresionFichaGeneralFacade
                    .obtenerDetalleCronogramaValoradoPOrFicha(idFicha);
            String plan = listaDetalles.get(0).getPlan();
            String planIngresoDatos = listaDetalles.get(0).getIngresaDatos();
            String[] columnas = {"Actividad", "Responsable", "Fecha desde",
                    "Fecha hasta", "Presupuesto","Justificativo", "Frecuencia"};
            String[] ordenColumnas = {"Actividad", "Responsable",
                    "FechaInicio", "FechaFin", "Presupuesto", "Justificativo","Frecuencia"};
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
            e.printStackTrace();
            return null;
        }
    }

    private String obtenerInventarioForestal(Integer idFicha) {
        try {
            InventarioForestalPma inventarioForestal = inventarioForestalPmaFacade.obtenerInventarioForestalPmaPorFicha(idFicha);
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

    private String obtenerDescripcionProyecto(final String descripcion) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p>&nbsp;</p><table align=\"left\" border=\"0\" cellpadding=\"2\" cellspacing=\"1\" style=\"width:100%\">");
        sb.append("<tbody><tr>");
        sb.append("<td style=\"width: ")
                .append(100)
                .append("%; text-align: justify\"> <span style=\"font-size: small;background-color: inherit;\">");
        sb.append(descripcion);
        sb.append("<br/></span></td>");
        sb.append("</tr>");
        sb.append("</tbody></table><p>&nbsp;</p>");
        return sb.toString();
    }

    private String obtenerDesechosProyecto() {

        List<ProyectoDesechoPeligroso> proyectoDesechoPeligrosos = proyectoDesechoPeligrosoFacade
                .buscarProyectoDesechoPeligrosoPorProyecto(proyectosBean
                        .getProyecto().getId());
        if (proyectoDesechoPeligrosos != null
                && !proyectoDesechoPeligrosos.isEmpty()) {

            StringBuilder sb = new StringBuilder();

            sb.append("<div style=\"text-align: left;\">"
                    + "<span style=\"font-weight: bold;font-size: small;\">"
                    + "Tipos de desechos especiales<br/></span></div>");

            sb.append("<p>&nbsp;</p><table align=\"left\" border=\"0\" cellpadding=\"2\" cellspacing=\"1\" style=\"width:100%\">");
            sb.append("<tbody>");

            List<DesechoPeligroso> desechoPeligrososEspeciales = proyectoDesechoPeligrosoFacade
                    .buscarDesechoPeligrososPorCodigoTipoDeDesecho(CODIGO_DESECHOS_PELIGROSOS_ESPECIALES);
            sb.append("<tr>");
            sb.append("<td style=\"width: ")
                    .append(33)
                    .append("%; text-align: justify\"> <span style=\"font-size: small;font-weight:bold;background-color: inherit;\">");
            sb.append("Clave del desecho");
            sb.append("<br/></span></td>");

            sb.append("<td style=\"width: ")
                    .append(33)
                    .append("%; text-align: justify\"> <span style=\"font-size: small;font-weight:bold;background-color: inherit;\">");
            sb.append("Descripción del desecho");
            sb.append("<br/></span></td>");

            sb.append("<td style=\"width: ")
                    .append(33)
                    .append("%; text-align: justify\"> <span style=\"font-size: small;font-weight:bold;background-color: inherit;\">");
            sb.append("Capacidad de gestión (Unidad: ton/año)");
            sb.append("<br/></span></td>");

            sb.append("</tr>");

            for (ProyectoDesechoPeligroso pdp : proyectoDesechoPeligrosos) {
                sb.append("<tr>");

                for (DesechoPeligroso dpe : desechoPeligrososEspeciales) {
                    if (pdp.getDesechoPeligroso().getDescripcion()
                            .equals(dpe.getDescripcion())) {
                        sb.append("<td style=\"width: ")
                                .append(33)
                                .append("%; text-align: justify\"> <span style=\"font-size: small;background-color: inherit;\">");
                        sb.append(dpe.getClave());
                        sb.append("<br/></span></td>");

                        sb.append("<td style=\"width: ")
                                .append(33)
                                .append("%; text-align: justify\"> <span style=\"font-size: small;background-color: inherit;\">");
                        sb.append(dpe.getDescripcion());
                        sb.append("<br/></span></td>");

                        sb.append("<td style=\"width: ")
                                .append(33)
                                .append("%; text-align: justify\"> <span style=\"font-size: small;background-color: inherit;\">");
                        sb.append(Double.toString(pdp.getCapacidadGestion()
                                .doubleValue()));
                        sb.append("<br/></span></td>");

                        break;
                    }
                }

                sb.append("</tr>");
            }

            sb.append("</tbody></table><p>&nbsp;</p>");

            return sb.toString();
        }

        return "";
    }

    private void cargarDatosContacto() {
        List<Contacto> listaContactos = null;
        try {
            listaContactos = contactoFacade.buscarUsuarioNativeQuery(proyecto
                    .getUsuario().getNombre());

            correo = "";
            direccion = "";
            telefono = "";
            for (Contacto c : listaContactos) {
                if (correo.isEmpty()
                        && c.getFormasContacto().getId().intValue() == FormasContacto.EMAIL) {
                    correo = c.getValor();
                } else if (direccion.isEmpty()
                        && c.getFormasContacto().getId().intValue() == FormasContacto.DIRECCION) {
                    direccion = c.getValor();
                } else if (telefono.isEmpty()
                        && c.getFormasContacto().getId().intValue() == FormasContacto.TELEFONO) {
                    telefono = c.getValor();
                }
            }
        } catch (ServiceException e) {
            LOG.error(e, e);
        }

    }

}
