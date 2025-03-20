package ec.gob.ambiente.prevencion.categoriaii.mineria.controller;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.prevencion.categoriaii.mineria.bean.DetalleMineriaBean;
import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.suia.catalogos.service.UnidadPeriodicidadService;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.detallefichaplan.facade.DetalleFichaPlanFacade;
import ec.gob.ambiente.suia.domain.CronogramaValoradoPma;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.FichaAmbientalMineria;
import ec.gob.ambiente.suia.domain.TipoPlanManejoAmbiental;
import ec.gob.ambiente.suia.dto.EntityAdjunto;
import ec.gob.ambiente.suia.dto.EntityPlanCronograma;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.prevencion.categoria2.facade.CronogramaValoradoFacade;
import ec.gob.ambiente.suia.prevencion.categoria2.ficha.mineria.facade.FichaAmbientalMineriaFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.SerializationUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.util.Log;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineModel;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@ManagedBean
@ViewScoped
public class DetalleMineriaController implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4678724648295914870L;

    @Getter
    @Setter
    private DetalleMineriaBean detalleMineriaBean;
    
    private boolean verDiag=true;

    @EJB
    private CronogramaValoradoFacade cronogramaValoradoFacade;
    @EJB
    private FichaAmbientalMineriaFacade fichaAmbientalMineriaFacade;
    @EJB
    private DetalleFichaPlanFacade detalleFichaPlanFacade;
    @ManagedProperty(value = "#{proyectosBean}")
    @Getter
    @Setter
    private ProyectosBean proyectosBean;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(DetalleMineriaController.class);
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private static final String REQUERIDO = "\" es requerido.";
    private static final long ZOOM_SEGUNDOS = 1000L;
    private static final String NO_SE_RECUPERO_PLAN = "No se logró recuperar el plan para este sector.";
    private static final int TAMANIO_ARCHIVO = 1024;

    private TimelineModel model;

    @EJB
    private DocumentosFacade documentosFacade;
    
    /***** cambio periodicidad *****/
	@EJB
	private UnidadPeriodicidadService unidadPeriodicidadService;
	/***** cambio periodicidad *****/

	@Getter
    @Setter
    private CronogramaValoradoPma cronogramaValoradoPma= new CronogramaValoradoPma();;
    
    @EJB
    private CrudServiceBean crudServiceBean;
    
	// MarielaG historial
	@Getter
	@Setter
	private List<CronogramaValoradoPma> listaCronogramaOriginal, listaCronogramasHistoriales, historialCronogramaSeleccionado;

	@Getter
	@Setter
	private String planCronogramaHistorial;

	@Getter
	@Setter
	private Documento documentoPuntosOriginal;

	@Getter
	@Setter
	private List<Documento> historialDocumentosPuntos;

	@Getter
	@Setter
	private Boolean newFileLoad = false;

	@Getter
	@Setter
	private Boolean existeDatosCronograma = false;

	// MarielaG historial
    
    @PostConstruct
    protected void initialize() throws CmisAlfrescoException {
    	historialCronogramaSeleccionado = new ArrayList<>(); //MarielaG historial
    	
        detalleMineriaBean = new DetalleMineriaBean();
        /***** cambio 31-03-2016*****/
        try {	// se agrega bloque try-catch para esta variable
        	FichaAmbientalMineria f = (FichaAmbientalMineria) JsfUtil
                    .devolverObjetoSession(Constantes.SESSION_FICHA_AMBIENTAL_MINERA_OBJECT);
        	detalleMineriaBean.setFicha(fichaAmbientalMineriaFacade.obtenerPorId(f.getId()));
        } catch(Exception e) {
        	e.printStackTrace();
        }
        /***** cambio 31-03-2016*****/
        cargarCatalogoPlan();
        detalleMineriaBean.setZoomMax(ZOOM_SEGUNDOS * 60 * 60 * 24 * 31 * 12);
        detalleMineriaBean.setZoomMin(ZOOM_SEGUNDOS * 60 * 60 * 24 * 31 * 6);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 0, 0, 0, 0);
        detalleMineriaBean.setStart(cal.getTime());
        cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31, 0, 0, 0);
        detalleMineriaBean.setEnd(cal.getTime());
        cargarAdjunto();
        inicializarPlantillaAgregarPuntosMonitoreo();

    }
    
    public boolean visualizar(List<CronogramaValoradoPma> listaDetalleCronograma) {
		boolean resultado = false;
		for (CronogramaValoradoPma a : listaDetalleCronograma) {
			if (a.getIngresaInformacion()) {
				resultado = false;
				break;
			}
			else {
				resultado = true;
				break;
			}
		}
		return resultado;
	}

    private void cargarCatalogoPlan() {
        try {
            detalleMineriaBean.setListaCronogramaEliminar(new ArrayList<CronogramaValoradoPma>());
            detalleMineriaBean.setListaEntityPlanCronograma(new ArrayList<EntityPlanCronograma>());
            List<TipoPlanManejoAmbiental> listaPlanes = detalleFichaPlanFacade.obtenerListaPlanes(TipoPlanManejoAmbiental.REGISTRO_AMBIENTAL);
            int i = 1;
            for (TipoPlanManejoAmbiental plan : listaPlanes) {
                EntityPlanCronograma e = new EntityPlanCronograma();
                e.setPlan(plan);
                e.setListaDetalleCronograma(new ArrayList<CronogramaValoradoPma>());
                if ((i % 2) == 0) {
                    e.setIdTabla("tblUno tblUnoBorder");
                } else {
                    e.setIdTabla("tblDos tblDosBorder");
                }
                detalleMineriaBean.getListaEntityPlanCronograma().add(e);
                i++;
            }
            cargarRecuperar();
            /***** cambio periodicidad *****/
            detalleMineriaBean.setListaUnidadPeriodicidad(unidadPeriodicidadService.listaUnidadesPeriodicidad()); 
			/***** cambio periodicidad *****/
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    private void cargarRecuperar() throws ServiceException {
    	//MarielaG cambios para manejo del historial
        List<CronogramaValoradoPma> listaCronogramas = cronogramaValoradoFacade
                .recuperarTodoPorFichaMineria(detalleMineriaBean.getFicha());
        listaCronogramaOriginal = new ArrayList<CronogramaValoradoPma>();
		listaCronogramasHistoriales = new ArrayList<CronogramaValoradoPma>();
		
        if (listaCronogramas != null && !listaCronogramas.isEmpty()) {
        	existeDatosCronograma = true;
        	
            TipoPlanManejoAmbiental plan = listaCronogramas.get(0).getPlan();
            List<CronogramaValoradoPma> listaAux = new ArrayList<CronogramaValoradoPma>();
            int i = 0;
            for (CronogramaValoradoPma c : listaCronogramas) {
				if (c.getIdRegistroOriginal() == null) {
					i++;
					//MarielaG para historial clonar el cronograma y llenar en nueva lista
					CronogramaValoradoPma cronogramaOriginal = (CronogramaValoradoPma) SerializationUtils.clone(c);
					listaCronogramaOriginal.add(cronogramaOriginal);
					
					if (plan.equals(c.getPlan())) {
						listaAux.add(c);
					} else {
						recuperarCronograma(plan, listaAux);
						plan = c.getPlan();
						listaAux = new ArrayList<CronogramaValoradoPma>();
						listaAux.add(c);
					}
					if (i == listaCronogramas.size()) {
						recuperarCronograma(plan, listaAux);
					}
					if (c.getPlan() == null) {
						cronogramaValoradoPma.setId(c.getId());
						cronogramaValoradoPma.setActividad(c.getActividad());
					}
				} else {
					listaCronogramasHistoriales.add(c);
				}
            }
            cargarTimeLineTotal();
        }
        
        setRegistrosHistorial();
    }

    private void recuperarCronograma(TipoPlanManejoAmbiental plan, List<CronogramaValoradoPma> listaCronogramas) {
        try {
            for (EntityPlanCronograma e : detalleMineriaBean.getListaEntityPlanCronograma()) {
                if (e.getPlan().equals(plan)) {
                    reasignarIndiceTabla(listaCronogramas);
                    e.setListaDetalleCronograma(listaCronogramas);
                }
            }
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void guardar() {
        try {
            List<CronogramaValoradoPma> listaGuardar = new ArrayList<CronogramaValoradoPma>();
            List<String> mensajes = new ArrayList<String>();
            for (EntityPlanCronograma etc : detalleMineriaBean.getListaEntityPlanCronograma()) {
            	if(etc.getPlan().getId()==1 || etc.getPlan().getId()==2 || etc.getPlan().getId()==3 || etc.getPlan().getId()==4 || 
            			etc.getPlan().getId()==5 || etc.getPlan().getId()==6 || etc.getPlan().getId()==7 || etc.getPlan().getId()==8 || etc.getPlan().getId()==9)
            	{
            		if(etc.getListaDetalleCronograma().size()<1)
            		{
            		
            			mensajes.add("Debe ingresar minimo una actividad en el "+etc.getPlan().getTipo() +" o la respectiva justificación técnica para no incluir ninguna actividad");
            		}
            	}              	
            	
                listaGuardar.addAll(etc.getListaDetalleCronograma());
            }           
            if (listaGuardar.isEmpty()) {
                mensajes.add("Debe ingresar registros.");
            }
            if (!validarPuntos()) {
                mensajes.add("Debe adjuntar el archivo de puntos de monitoreo.");
            }
            if (mensajes.isEmpty()) {
            	//MarielaG para guardar el historial del registro
				if(existeDatosCronograma == true){
                	this.guardarHistorial(listaGuardar, detalleMineriaBean.getListaCronogramaEliminar());
                }
				
                cronogramaValoradoFacade.guardarMineriaAdjunto(listaGuardar,
                        detalleMineriaBean.getListaCronogramaEliminar(), detalleMineriaBean.getFicha(),
                        getDetalleMineriaBean().getEntityAdjunto(), documentoPuntosOriginal); 
                
                if(cronogramaValoradoPma.getActividad()!=null){
                    cronogramaValoradoPma.getId();
                    cronogramaValoradoPma.setFichaAmbientalMineria(detalleMineriaBean.getFicha());
                    crudServiceBean.saveOrUpdate(cronogramaValoradoPma);
                }
                cargarCatalogoPlan();
                cargarAdjunto(); //MarielaG para cargar archivo despues de guardar 
                JsfUtil.addMessageInfo(JsfUtil.REGISTRO_GUARDADO);
            } else {
                JsfUtil.addMessageError(mensajes);
            }
        } catch (Exception e) {
            JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + " " + e.getMessage());
            LOG.error(e, e);
        }

    }

    private boolean validarPuntos() {
        return getDetalleMineriaBean().getEntityAdjunto().getArchivo() != null;
    }

    private boolean validarActividad() {
        List<String> msgs = new ArrayList<String>();
        if (detalleMineriaBean.getActividad().getActividad() == null
                || detalleMineriaBean.getActividad().getActividad().isEmpty()) {
            msgs.add("El campo \"'Actividad'" + REQUERIDO);
        } else {
            for (CronogramaValoradoPma cv : detalleMineriaBean.getSeleccionadoEntityPlanCronograma()
                    .getListaDetalleCronograma()) {
                if (!cv.isEditar() && detalleMineriaBean.getActividad().getActividad().equals(cv.getActividad())) {
                    msgs.add("Está ingresando una actividad que ya existe.");
                    break;
                }
            }
        }
        if (detalleMineriaBean.getActividad().getResponsable() == null
                || detalleMineriaBean.getActividad().getResponsable().isEmpty()) {
            msgs.add("El campo \"'Responsable'" + REQUERIDO);
        }
        if (detalleMineriaBean.getActividad().getPresupuesto() == null
                || detalleMineriaBean.getActividad().getPresupuesto() == 0) {        	
        	detalleMineriaBean.getActividad().setPresupuesto(0.0);
        	if (detalleMineriaBean.getActividad().getJustificativo().equals("")){
                msgs.add("El campo \"'Justificativo'" + REQUERIDO);        		
        	}        
        }
        if (detalleMineriaBean.getActividad().getFechaFin() == null) {
            msgs.add("El campo \"'Fecha fin'" + REQUERIDO);
        }
        /****** campo periodicidad ******/
		if(detalleMineriaBean.getActividad().getUnidadPeriodo() == null) 
			msgs.add("El campo \'Periodo" + REQUERIDO);
		/****** campo periodicidad ******/
        if ("Ex-ante".equals(getDetalleMineriaBean().getFicha().getProyectoLicenciamientoAmbiental().getTipoEstudio()
                .getNombre())) {
            long miliSistema, miliFechaInicio;
            miliSistema = JsfUtil.devuelveFechaTruncadaMilisegundos(new Date(), 0);
            miliFechaInicio = JsfUtil.devuelveFechaTruncadaMilisegundos(detalleMineriaBean.getActividad()
                    .getFechaInicio(), 0);
            if (detalleMineriaBean.getActividad().getFechaInicio() != null && miliFechaInicio < miliSistema) {
                msgs.add("La fecha inicio no debe ser anterior a la fecha actual porque el proyecto es Ex-ante.");
            }
        }
        if (detalleMineriaBean.getActividad().getFechaFin() != null
                && detalleMineriaBean.getActividad().getFechaFin()
                .before(detalleMineriaBean.getActividad().getFechaInicio())) {
            msgs.add("La fecha fin no debe ser anterior a la fecha inicio.");
        }

        if (!msgs.isEmpty()) {
            JsfUtil.addMessageError(msgs);
            return false;
        }
        return true;
    }

    public TimelineModel getModel() {
        return model;
    }

    private void reasignarIndiceTabla(List<CronogramaValoradoPma> listaCronograma) {
        int i = 0;
        for (CronogramaValoradoPma a : listaCronograma) {
            a.setIndice(i);
            a.setEditar(false);
            i++;
        }
    }

    private void cargarTimeLineTotal() {
        model = new TimelineModel();
        detalleMineriaBean.setSumaTotal(0.0);
        for (EntityPlanCronograma e : detalleMineriaBean.getListaEntityPlanCronograma()) {
            for (CronogramaValoradoPma c : e.getListaDetalleCronograma()) {
            	if (!(c.getFechaInicio()==null)){
                model.add(new TimelineEvent(c, c.getFechaInicio(), c.getFechaFin()));
                detalleMineriaBean.setSumaTotal(detalleMineriaBean.getSumaTotal() + c.getPresupuesto());
            	}
            }
        }
    }

    public void agregarActividad(EntityPlanCronograma entityPlanCronograma) {
        try {
            detalleMineriaBean.setSeleccionadoEntityPlanCronograma(entityPlanCronograma);
            CronogramaValoradoPma a = new CronogramaValoradoPma();
            a.setFechaInicio(new Date());
            a.setFrecuencia(1);
            a.setFichaAmbientalMineria(detalleMineriaBean.getFicha());
            a.setPlan(detalleMineriaBean.getSeleccionadoEntityPlanCronograma().getPlan());
            a.setIngresaInformacion(true);
            detalleMineriaBean.setActividad(a);
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void guardarActividad() {
        try {
            RequestContext context = RequestContext.getCurrentInstance();
            if (detalleMineriaBean.getActividad().getIngresaInformacion()){            	            
            if (validarActividad()) {
            	detalleMineriaBean.getActividad().setObservacion("");
                if (!detalleMineriaBean.getActividad().isEditar()) {
                    detalleMineriaBean.getSeleccionadoEntityPlanCronograma().getListaDetalleCronograma()
                            .add(detalleMineriaBean.getActividad());
                }
                reasignarIndiceTabla(detalleMineriaBean.getSeleccionadoEntityPlanCronograma()
                        .getListaDetalleCronograma());
                cargarTimeLineTotal();
                context.addCallbackParam("actividadIn", true);
            } else {
                context.addCallbackParam("actividadIn", false);
            }
           }else{
    			Integer tamanioObservacion= detalleMineriaBean.getActividad().getObservacion().length();
    			if (tamanioObservacion<=255){
//    			}
    			List<CronogramaValoradoPma> listaDetalleCronograma =detalleMineriaBean.getSeleccionadoEntityPlanCronograma().getListaDetalleCronograma();
    			boolean resultadoLista=true;
    			if (listaDetalleCronograma.size()>0){
    				 resultadoLista=visualizar(listaDetalleCronograma);
    			}
    			
    			if (resultadoLista){							
    			if (!detalleMineriaBean.getActividad().isEditar()) {
    				detalleMineriaBean.getActividad().setActividad("");
    				detalleMineriaBean.getActividad().setResponsable("");
    				detalleMineriaBean.getActividad().setPresupuesto(0.0);
    				detalleMineriaBean.getActividad().setJustificativo("");
    				detalleMineriaBean.getActividad().setFechaInicio(null);
    				detalleMineriaBean.getActividad().setFechaFin(null);
    				detalleMineriaBean.getActividad().setFrecuencia(null);
    				detalleMineriaBean
    						.getSeleccionadoEntityPlanCronograma()
    						.getListaDetalleCronograma()
    						.add(detalleMineriaBean.getActividad());
    			}else{
    				detalleMineriaBean.getActividad().setActividad("");
    				detalleMineriaBean.getActividad().setResponsable("");
    				detalleMineriaBean.getActividad().setPresupuesto(0.0);
    				detalleMineriaBean.getActividad().setJustificativo("");
    				detalleMineriaBean.getActividad().setFechaInicio(new Date());
    				detalleMineriaBean.getActividad().setFechaFin(null);
    				detalleMineriaBean.getActividad().setFrecuencia(1);		
    				detalleMineriaBean.getActividad().setUnidadPeriodo(null);
    				
    				detalleMineriaBean.setZoomMax(ZOOM_SEGUNDOS * 60 * 60 * 24 * 31 * 12);
    				detalleMineriaBean.setZoomMin(ZOOM_SEGUNDOS * 60 * 60 * 24 * 31 * 6);
    				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    				cal.set(cal.get(Calendar.YEAR), Calendar.JANUARY, 0, 0, 0, 0);
    				detalleMineriaBean.setStart(cal.getTime());
    				cal.set(cal.get(Calendar.YEAR), Calendar.DECEMBER, 31, 0, 0, 0);
    				detalleMineriaBean.setEnd(cal.getTime());
    				
    			}
    			reasignarIndiceTabla(detalleMineriaBean
    					.getSeleccionadoEntityPlanCronograma()
    					.getListaDetalleCronograma());
    			context.addCallbackParam("actividadIn", true);		
    		}else{
    			JsfUtil.addMessageError("No se puede guardar la información ya que existe información ingresada.");
    		}
    		}else{
    			JsfUtil.addMessageError("La justificación ingresada no debe ser mayor a 255 caracteres, si supera el limite no se guardará la información");
    		}
    		}
//    			}else{
//    				JsfUtil.addMessageError("La justificación ingresada no debe ser mayor a 255 caracteres, si supera el limite no se guardará la información");
//    			}
        } catch (Exception e) {
            LOG.error(e, e);
        }
    }

    public void seleccionaEntityPlan(EntityPlanCronograma entityPlanCronograma) {
        detalleMineriaBean.setSeleccionadoEntityPlanCronograma(entityPlanCronograma);
    }

    public void seleccionarActividad(CronogramaValoradoPma actividad,EntityPlanCronograma entityPlanCronograma) {
        actividad.setEditar(true);
        detalleMineriaBean.setActividad(actividad);
        /****** campo periodicidad ******/
        detalleMineriaBean.setSeleccionadoEntityPlanCronograma(entityPlanCronograma); 
                /****** campo periodicidad ******/
    }

    public void seleccionarActividadEliminar(CronogramaValoradoPma actividad, EntityPlanCronograma entityPlanCronograma) {
        detalleMineriaBean.setSeleccionadoEntityPlanCronograma(entityPlanCronograma);
        detalleMineriaBean.setActividad(actividad);
    }

    public void remover() {
        detalleMineriaBean.getSeleccionadoEntityPlanCronograma().getListaDetalleCronograma()
                .remove(detalleMineriaBean.getActividad().getIndice());
        if (detalleMineriaBean.getActividad().getId() != null) {
            detalleMineriaBean.getListaCronogramaEliminar().add(detalleMineriaBean.getActividad());
        }
        reasignarIndiceTabla(detalleMineriaBean.getSeleccionadoEntityPlanCronograma().getListaDetalleCronograma());
        cargarTimeLineTotal();
    }

    public void generarReporteTablas() {

        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

        response.reset();
        response.setContentType("application/ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + "cronograma.xls" + "\"");
        HSSFWorkbook objWB = new HSSFWorkbook();
        HSSFSheet hoja1 = objWB.createSheet("hoja 1");
        HSSFRow fila = hoja1.createRow(0);
        HSSFCell celdaPlan = fila.createCell(0);
        celdaPlan.setCellValue("PLAN DE MANEJO AMBIENTAL (PMA)");
        int orden = 1;
        for (EntityPlanCronograma e : detalleMineriaBean.getListaEntityPlanCronograma()) {
            if (!e.getListaDetalleCronograma().isEmpty()) {
                orden = filaReporte(hoja1, orden, e);
                orden++;
            }
        }
        HSSFRow filaTotal = hoja1.createRow(orden);
        HSSFCell celdal = filaTotal.createCell(3);
        celdal.setCellValue("Total");
        HSSFCell celdaTotal = filaTotal.createCell(4);
        celdaTotal.setCellValue(DecimalFormat.getCurrencyInstance(Locale.US).format(detalleMineriaBean.getSumaTotal()));

        try {
            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            objWB.write(outByteStream);
            byte[] outArray = outByteStream.toByteArray();
            OutputStream output = response.getOutputStream();
            output.write(outArray);
            output.flush();
            output.close();

            fc.responseComplete();
        } catch (IOException ex) {
            LOG.error(ex);
        }
    }

    private int filaReporte(HSSFSheet hoja1, int orden, EntityPlanCronograma e) {
        HSSFRow fila = hoja1.createRow(orden);
        int aux = orden + 1;
        HSSFCell celdaPlan = fila.createCell(0);
        celdaPlan.setCellValue(e.getPlan().getTipo());
        filaCabecera(hoja1, aux);
        for (CronogramaValoradoPma cv : e.getListaDetalleCronograma()) {
            aux++;
            filaDatos(hoja1, aux, cv);
        }
        return aux;
    }

    private void filaCabecera(HSSFSheet hoja1, int orden) {
        HSSFRow fila = hoja1.createRow(orden);
        HSSFCell celda1 = fila.createCell(0);
        celda1.setCellValue("Actividad");
        HSSFCell celda2 = fila.createCell(1);
        celda2.setCellValue("Responsable");
        HSSFCell celda3 = fila.createCell(2);
        celda3.setCellValue("Fecha inicio");
        HSSFCell celda4 = fila.createCell(3);
        celda4.setCellValue("Fecha fin");
        HSSFCell celda5 = fila.createCell(4);
        celda5.setCellValue("Presupuesto");
    }

    private void filaDatos(HSSFSheet hoja1, int orden, CronogramaValoradoPma cv) {
        HSSFRow fila = hoja1.createRow(orden);
        HSSFCell celda1 = fila.createCell(0);
        celda1.setCellValue(cv.getActividad());
        HSSFCell celda2 = fila.createCell(1);
        celda2.setCellValue(cv.getResponsable());
        HSSFCell celda3 = fila.createCell(2);
        celda3.setCellValue(FORMAT.format(cv.getFechaInicio()));
        HSSFCell celda4 = fila.createCell(3);
        celda4.setCellValue(FORMAT.format(cv.getFechaFin()));
        HSSFCell celda5 = fila.createCell(4);
        celda5.setCellValue(DecimalFormat.getCurrencyInstance(Locale.US).format(cv.getPresupuesto()));
    }

    public StreamedContent descargarPlan() {
        InputStream is = null;
        try {
            String nombrDocumento = getDetalleMineriaBean().getFicha().getProyectoLicenciamientoAmbiental()
                    .getCatalogoCategoria().getTipoSubsector().getDescripcion()
                    + ".pdf";
            byte[] plan = detalleFichaPlanFacade.descargarPlanPorSector(nombrDocumento);

            if (plan != null) {
                is = new ByteArrayInputStream(plan);
                return new DefaultStreamedContent(is, "application/pdf", nombrDocumento);
            } else {
                JsfUtil.addMessageError(NO_SE_RECUPERO_PLAN);
                return null;
            }
        } catch (Exception e) {
            LOG.error(NO_SE_RECUPERO_PLAN, e);
            JsfUtil.addMessageError(NO_SE_RECUPERO_PLAN);
            return null;
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        if (event.getFile().getContents().length > TAMANIO_ARCHIVO) {
            getDetalleMineriaBean().getEntityAdjunto().setArchivo(event.getFile().getContents());
            getDetalleMineriaBean().getEntityAdjunto().setExtension(
                    JsfUtil.devuelveExtension(event.getFile().getFileName()));
            getDetalleMineriaBean().getEntityAdjunto().setNombre(event.getFile().getFileName());
            getDetalleMineriaBean().getEntityAdjunto().setMimeType(event.getFile().getContentType());
            newFileLoad = true;
            LOG.info(event.getFile().getFileName() + " is uploaded.");
        } else {
            JsfUtil.addMessageError("Debe adjuntar un archivo superior a 0 bytes.");
        }
    }

    private void cargarAdjunto() throws CmisAlfrescoException {
        try {
            getDetalleMineriaBean().setEntityAdjunto(
                    fichaAmbientalMineriaFacade.obternerPorFichaPuntos(getDetalleMineriaBean().getFicha()));
            if (getDetalleMineriaBean().getEntityAdjunto() == null) {
                getDetalleMineriaBean().setEntityAdjunto(new EntityAdjunto());
			} else {
				// MarielaG búsqueda de documento para historial
				List<Documento> docs = documentosFacade.listarTodoDocumentosXTablaId(detalleMineriaBean.getFicha().getId(),
						detalleMineriaBean.getFicha().getClass().getSimpleName() + "-1");
				historialDocumentosPuntos = new ArrayList<>();
				if (docs != null && !docs.isEmpty())
					documentoPuntosOriginal = docs.get(0);
				for (Documento documento : docs) {
					if (documento.getIdHistorico() != null)
						historialDocumentosPuntos.add(documento);
				}
			}
        } catch (ServiceException e) {
            LOG.error(e, e);
        }
    }

    public void descargar() throws IOException {
        JsfUtil.descargarMimeType(getDetalleMineriaBean().getEntityAdjunto().getArchivo(), "puntoMonitoreo",
                getDetalleMineriaBean().getEntityAdjunto().getExtension(), getDetalleMineriaBean().getEntityAdjunto()
                        .getMimeType());
    }

    public void inicializarPlantillaAgregarPuntosMonitoreo() {
        Documento plantillaAgregarPuntosMonitoreo = detalleMineriaBean.getPlantillaAgregarPuntosMonitoreo();
        try {
            String nombreDocumento = proyectosBean.getProyecto().getCatalogoCategoria().getTipoSubsector()
                    .getDescripcion() + ".xlsx";
            byte[] plantillaAgregarPuntosMonitoreoContenido =null;
            try{
                plantillaAgregarPuntosMonitoreoContenido =  documentosFacade
                        .descargarDocumentoPorNombreYDirectorioBase(nombreDocumento, Constantes.getRootStaticDocumentsId());
            }catch (Exception ex){
                 Log.error("Ocurrió un error al descargar la plantilla para agregar puntos de monitoreo del alfresco",
                    ex);
            }

            if (plantillaAgregarPuntosMonitoreoContenido == null) {
                nombreDocumento = Constantes.PLANTILLA_AGREGAR_PUNTOS_MONITOREO;
                plantillaAgregarPuntosMonitoreoContenido =
                        documentosFacade
                                .descargarDocumentoPorNombreYDirectorioBase(Constantes.PLANTILLA_AGREGAR_PUNTOS_MONITOREO, null);
            }

            if (plantillaAgregarPuntosMonitoreoContenido != null) {
                plantillaAgregarPuntosMonitoreo.setContenidoDocumento(plantillaAgregarPuntosMonitoreoContenido);
                plantillaAgregarPuntosMonitoreo.setNombre(nombreDocumento);
            }
        } catch (Exception exception) {
            Log.error("Ocurrió un error al descargar la plantilla para agregar puntos de monitoreo del alfresco",
                    exception);
        }
    }

    public StreamedContent getStreamContent(Documento documento) throws Exception {
        DefaultStreamedContent content = null;
        try {
            if (documento != null && documento.getNombre() != null && documento.getContenidoDocumento() != null) {
                content = new DefaultStreamedContent(new ByteArrayInputStream(documento.getContenidoDocumento()));
                content.setName(documento.getNombre());
            } else
                JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

        } catch (Exception exception) {
            Log.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
            JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
        }
        return content;

    }
    
    public boolean isVerDiag() {
		if(verDiag){
			verDiag=false;
			return true;
		}
		return verDiag;
	}
    
	// MarielaG metodos para manejo de historial
	public void setRegistrosHistorial() {
		// Establece si el cronograma es un historial
		for (EntityPlanCronograma e : detalleMineriaBean.getListaEntityPlanCronograma()) {
			List<CronogramaValoradoPma> listaActual = e.getListaDetalleCronograma();
			for (CronogramaValoradoPma cronograma : listaActual) {
				if (cronograma.getFechaHistorico() != null) {
					cronograma.setNuevoEnModificacion(true);
				}
				for (CronogramaValoradoPma cronogramaHistorial : listaCronogramasHistoriales) {
					if (cronograma.getId().equals(
							cronogramaHistorial.getIdRegistroOriginal())) {
						cronograma.setHistorialModificaciones(true);
						break;
					}
				}
			}
		}
	}

	public void guardarHistorial(List<CronogramaValoradoPma> listaGuardar, List<CronogramaValoradoPma> listaEliminar) {
		//guardar los registros originales antes del cambio del usuario
		List<CronogramaValoradoPma> listaCronogramaOriginales = new ArrayList<CronogramaValoradoPma>();
		
		for (CronogramaValoradoPma cronograma : listaGuardar) {
			if(cronograma.getId() == null){
				//registro nuevo en correccion
				cronograma.setFechaHistorico(new Date());
			}else{
				//validar si existen cambios para guardar el historial
				for (CronogramaValoradoPma cronogramaOriginal : listaCronogramaOriginal) {
					if(cronogramaOriginal.getId() != null && cronogramaOriginal.getId().equals(cronograma.getId())){
						if(!cronograma.equalsObject(cronogramaOriginal)){
							//guardar historial
							cronogramaOriginal.setId(null);
							cronogramaOriginal.setIdRegistroOriginal(cronograma.getId());
							cronogramaOriginal.setFechaHistorico(new Date());
							listaCronogramaOriginales.add(cronogramaOriginal);
						}
						break;
					}
				}
			}
		}
		
		for (CronogramaValoradoPma cronogramaEliminar : listaEliminar) {
			if(cronogramaEliminar.getFechaHistorico() == null){
				CronogramaValoradoPma cronogramaOriginal = (CronogramaValoradoPma) SerializationUtils.clone(cronogramaEliminar);
				cronogramaOriginal.setId(null);
				cronogramaOriginal.setIdRegistroOriginal(cronogramaEliminar.getId());
				cronogramaOriginal.setFechaHistorico(new Date());
				listaCronogramaOriginales.add(cronogramaOriginal);
			}
		}
		
		cronogramaValoradoFacade.guardarOriginal(listaCronogramaOriginales);
	}
	
	public void fillHistorialCronograma(CronogramaValoradoPma cronograma) {
		historialCronogramaSeleccionado = new ArrayList<>();
		planCronogramaHistorial = cronograma.getPlan().getTipo();
		
		for (CronogramaValoradoPma cronogramaHistorial : listaCronogramasHistoriales) {
			if(cronograma.getId().equals(cronogramaHistorial.getIdRegistroOriginal())){
				historialCronogramaSeleccionado.add(cronogramaHistorial);
			}
		}
	}
	
	public void fillHistorialCronogramaEliminadosPorPlan(EntityPlanCronograma actividad) {
		historialCronogramaSeleccionado = new ArrayList<>();
		
		planCronogramaHistorial = actividad.getPlan().getTipo();
		
		for (CronogramaValoradoPma cronogramaHistorial : listaCronogramasHistoriales) {
			if(cronogramaHistorial.getPlan().equals(actividad.getPlan())){
				Boolean existeItem = false;
				for (CronogramaValoradoPma cronogramaOriginal : listaCronogramaOriginal) {
					if(cronogramaHistorial.getIdRegistroOriginal().equals(cronogramaOriginal.getId())){
						existeItem = true;
						break;
					}
				}
				
				if(!existeItem){
					historialCronogramaSeleccionado.add(0, cronogramaHistorial);
				}
			}
		}
	}
	
	public Boolean visualizarEliminados(EntityPlanCronograma actividad) {
		List<CronogramaValoradoPma> cronogramasEliminados = new ArrayList<>();
		
		for (CronogramaValoradoPma cronogramaHistorial : listaCronogramasHistoriales) {
			if(cronogramaHistorial.getPlan().equals(actividad.getPlan())){
				Boolean existeItem = false;
				for (CronogramaValoradoPma cronogramaOriginal : listaCronogramaOriginal) {
					if(cronogramaHistorial.getIdRegistroOriginal().equals(cronogramaOriginal.getId())){
						existeItem = true;
						break;
					}
				}
				
				if(!existeItem){
					cronogramasEliminados.add(cronogramaHistorial);
				}
			}
		}
		
		if(cronogramasEliminados.size() > 0){
			return true;
		}
		
		return false;
	}
	
	public StreamedContent getStreamContentOriginal(Documento documento)
			throws Exception {
		DefaultStreamedContent content = null;
		try {
			documento = this.descargarAlfresco(documento);
			if (documento != null && documento.getNombre() != null
					&& documento.getContenidoDocumento() != null) {
				content = new DefaultStreamedContent(new ByteArrayInputStream(
						documento.getContenidoDocumento()));
				content.setName(documento.getNombre());
			} else
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);

		} catch (Exception exception) {
			Log.error(JsfUtil.MESSAGE_ERROR_ALFRESCO, exception);
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_ALFRESCO);
		}
		return content;
	}

	/**
	 * Descarga documento desde el Alfresco
	 */
	public Documento descargarAlfresco(Documento documento)
			throws CmisAlfrescoException {
		byte[] documentoContenido = null;
		if (documento != null && documento.getIdAlfresco() != null)
			documentoContenido = documentosFacade.descargar(documento
					.getIdAlfresco());
		if (documentoContenido != null)
			documento.setContenidoDocumento(documentoContenido);
		return documento;
	}
	//Fin metodos historial
}
