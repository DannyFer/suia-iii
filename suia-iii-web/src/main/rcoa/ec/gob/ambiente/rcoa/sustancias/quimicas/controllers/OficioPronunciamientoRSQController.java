package ec.gob.ambiente.rcoa.sustancias.quimicas.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.enums.TipoInformeOficioEnum;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ActividadSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.AnalisisTecnicoRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.DocumentosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.InformesOficiosRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.ObservacionesRSQFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.RegistroSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.facade.UbicacionSustanciaQuimicaFacade;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ActividadSustancia;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.AnalisisTecnicoRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.DocumentosSustanciasQuimicasRcoa;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.InformeOficioRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.ObservacionesFormulariosRSQ;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.RegistroSustanciaQuimica;
import ec.gob.ambiente.rcoa.sustancias.quimicas.model.UbicacionSustancia;
import ec.gob.ambiente.rcoa.util.BuscarUsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.DigitalSign;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class OficioPronunciamientoRSQController implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger LOG = Logger.getLogger(OficioPronunciamientoRSQController.class);
	
	//EJBs
	@EJB
	private ActividadSustanciaQuimicaFacade actividadSustanciaQuimicaFacade;
	
	@EJB
	private AnalisisTecnicoRSQFacade analisisTecnicoRSQFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private DocumentosRSQFacade documentosRSQFacade;

	@EJB
	private InformesOficiosRSQFacade informesOficiosRSQFacade;
	   
    @EJB
    private ObservacionesRSQFacade observacionesRSQFacade;
    
    @EJB
    private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	    
	@EJB
	private RegistroSustanciaQuimicaFacade registroSustanciaQuimicaFacade;  
	
	@EJB
    private SecuenciasFacade secuenciasFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
    private UbicacionSustanciaQuimicaFacade ubicacionSustanciaQuimicaFacade;
	    
	//BEANs
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{buscarUsuarioBean}")
    private BuscarUsuarioBean buscarUsuarioBean;
     
    //LISTs    
    @Getter
    private List<ActividadSustancia> actividadSustanciaLista;
    
    @Getter
    private List<AnalisisTecnicoRSQ> analisisTecnicoRSQLista;
           
    @Getter
    private List<CatalogoGeneralCoa> catalogoHallazgosLista;
    
    private List<ObservacionesFormulariosRSQ> observacionesRsqLista;
    
    private List<UbicacionesGeografica> proyectoUbicacionLista;
    
    @Getter
    private List<UbicacionSustancia> ubicacionSustanciaProyectoLista;

	//OBJs
    @Getter
    @Setter
    private DocumentosSustanciasQuimicasRcoa documentoInforme;
    
	@Getter
	@Setter
	private InformeOficioRSQ informe,oficio;
	
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	private RegistroSustanciaQuimica registroSustanciaQuimica;
	
	@Getter
	@Setter
	private Usuario usuarioAutoridad;
	
	//MAPs
	private Map<String, Object> variables;

    //STRINGs
	@Getter
	@Setter
	private String urlInforme,urlOficio,urlRsq;
	
	private String varTramite;
	
	//INTEGERs
	private Integer numeroRevision;
	
	//BOOLEANs
	private boolean firmadoFisico;
	
	@Getter
	boolean pronunAprobado;
	
	@Getter
	@Setter
	boolean token,editarToken;
	
	@Setter
	@Getter
	private boolean verInforme,verOficio,verRsq;
	
	@Getter
	@Setter
	private DocumentosSustanciasQuimicasRcoa documentoEvidencia; 
	
	@Getter
	@Setter
	private boolean noGuardado = true;
	
	@Getter
    private List<ActividadSustancia> actividadSustanciaTodoLista;

	@PostConstruct
	public void init() {
		try {
			cargarDatosTarea();
			cargarDatosProyecto();
			
			verInforme=true;
			verOficio=false;
			verRsq=false;
			
			token= JsfUtil.getTokenUser();
			editarToken=!Constantes.getAmbienteProduccion();
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos OficioPronunciamientoRSQController.");
		}
	}
	
	private void cargarDatosTarea() throws ServiceException{

		try {
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			varTramite=(String)variables.get("tramite");
			numeroRevision=variables.containsKey("numero_revision")?(Integer.valueOf((String)variables.get("numero_revision"))):1;
		} catch (JbpmException e) {
			LOG.error("Error al cargarDatosTarea "+e.getCause()+" "+e.getMessage());
		}
	}
	
	private void cargarDatosProyecto() throws ServiceException{		
		proyectoLicenciaCoa=proyectoLicenciaCoaFacade.buscarProyecto(varTramite);
		registroSustanciaQuimica=registroSustanciaQuimicaFacade.obtenerRegistroPorProyecto(proyectoLicenciaCoa);
		proyectoUbicacionLista=proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyectoLicenciaCoa);
		analisisTecnicoRSQLista=new ArrayList<>();
		actividadSustanciaLista=new ArrayList<>();
		actividadSustanciaTodoLista = new ArrayList<ActividadSustancia>();
		
		if(registroSustanciaQuimica!=null) {
			informe = informesOficiosRSQFacade.obtenerPorRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_TECNICO,numeroRevision);
			oficio=informesOficiosRSQFacade.obtenerPorRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,numeroRevision);
			ubicacionSustanciaProyectoLista=ubicacionSustanciaQuimicaFacade.obtenerUbicacionesPorRSQ(registroSustanciaQuimica);
			observacionesRsqLista=observacionesRSQFacade.listarPorIdClaseNombreClaseNoCorregidas(registroSustanciaQuimica.getId(), RegistroSustanciaQuimica.class.getSimpleName());
			
			pronunAprobado=registroSustanciaQuimica.getTipoPronunciamiento().getNombre().toUpperCase().contains("APROBADO");
						
			List<ActividadSustancia> actividadSustanciaListaTodo=actividadSustanciaQuimicaFacade.obtenerActividadesPorRSQ(registroSustanciaQuimica);
			for (ActividadSustancia actividadSustancia : actividadSustanciaListaTodo) {
				if(actividadSustancia.getCupo()!=null) {
					actividadSustanciaLista.add(actividadSustancia);
				}
				
				actividadSustanciaTodoLista.add(actividadSustancia);
			}	
						
			if (informe == null) {
				informe = new InformeOficioRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.INFORME_TECNICO,numeroRevision,registroSustanciaQuimica.getArea());
			}else {
				analisisTecnicoRSQLista=analisisTecnicoRSQFacade.obtenerPorInformeOficio(informe,CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS);
				documentoInforme=documentosRSQFacade.obtenerDocumentoPorTipo(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_NEGADO,"InformeOficioRSQ",informe.getId());
				
				documentoEvidencia = documentosRSQFacade.obtenerDocumentoPorTipoIdTabla(TipoDocumentoSistema.RCOA_RSQ_EVIDENCIA_FOTOGRAFICA, InformeOficioRSQ.class.getSimpleName(), informe.getId());
			}
			
			if (oficio == null) {
				oficio = new InformeOficioRSQ(registroSustanciaQuimica,TipoInformeOficioEnum.OFICIO_PRONUNCIAMIENTO,numeroRevision,registroSustanciaQuimica.getArea());
			}
			
			if (documentoInforme == null) {
//				documentoInforme = new DocumentosSustanciasQuimicasRcoa(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_NEGADO,"InformeOficioRSQ",informe.getId(),bandejaTareasBean.getTarea().getProcessInstanceId());
				documentoInforme = new DocumentosSustanciasQuimicasRcoa(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_APROBADO:TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_NEGADO,"InformeOficioRSQ",informe.getId(), null);
	        }
			
			if(analisisTecnicoRSQLista.isEmpty()) {
				catalogoHallazgosLista=catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS);
				for (CatalogoGeneralCoa hallazgo : catalogoHallazgosLista) {
					analisisTecnicoRSQLista.add(new AnalisisTecnicoRSQ(informe,CatalogoTipoCoaEnum.RSQ_INFORME_REQUISITOS,hallazgo));											
				}			
			}			
		}
		
		generarDocumentoInforme(true);		
	}
	
	
	public String getTituloDocumento() {
		if(verInforme)
			return "Informe Técnico";
		if(verOficio)
			return "Oficio Pronunciamiento";
		if(verRsq)
			return "Registro Sustancias Quimicas";
		return "";
	}	
	
	public String getUrlDocumento() {
		if(verInforme)
			return urlInforme;
		if(verOficio)
			return urlOficio;
		if(verRsq)
			return urlRsq;
		return "";
	}
		
	public void verListener(boolean siguiente) {
		if(siguiente && verInforme) {
			guardar(false);
			verInforme=false;
			verOficio=true;
			generarDocumentoOficio(true);
			return;
		}
		
		if(siguiente && verOficio) {
			verOficio=false;
			verRsq=true;
			generarDocumentoRsq(true);
			return;
		}
		
		if(!siguiente && verOficio) {
			verOficio=false;
			verInforme=true;
			return;
		}
		
		if(!siguiente && verRsq) {
			verRsq=false;
			verOficio=true;	
			return;
		}
	}
	
	public boolean getVerFirmar(){
		if(pronunAprobado) {
			return verRsq;
		}else{
			return verOficio;
		}
	}
	
	
	private String getLabelProponente() {       
        try {
            Organizacion organizacion = organizacionFacade.buscarPorRuc(informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getNombre());
            if (organizacion != null) {
                return organizacion.getNombre();
            } 
            return informe.getRegistroSustanciaQuimica().getProyectoLicenciaCoa().getUsuario().getPersona().getNombre();
            
        } catch (ServiceException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return null;
    }
	
	public Date getCurrentDate() {
		return new Date();
	}
	
	private Usuario buscarUsuario(Area area,String tipo){
		String roleKey="role.rsq."+(area.getTipoArea().getId().intValue()==1?"pc":"cz")+"."+tipo;
		if(area.getTipoArea().getId().intValue()==1 && tipo.contains("autoridad")) {
			area=areaFacade.getArea(253);//SCA
		}
		try {
			return buscarUsuarioBean.buscarUsuario(roleKey,area);
		} catch (Exception e) {
			LOG.error("No se ha encontrado usuario con en rol "+Constantes.getRoleAreaName(roleKey)+"("+roleKey+") en el area "+area.getAreaName());			
		}
		
		return null;
	}
	
	public void guardar(boolean generarDocumento) {
		try {
			
			boolean elementoSeleccionado = false;
			
			List<String> mensajesDoc = new ArrayList<>();
			
			for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
				if(item.getHabilitado()){
					elementoSeleccionado = true;
				}
				
				if(item.getCatalogoGeneralCoa().getDescripcion().equals("Evidencia Fotográfica.") && item.getHabilitado()){
					if(documentoEvidencia == null || documentoEvidencia.getContenidoDocumento() == null){
						mensajesDoc.add("Debe adjunta el documento de Evidencia fotográfica.");
					}					
				}
			}
			
			if(!elementoSeleccionado){
				JsfUtil.addMessageError("Debe seleccionar al menos un hallazgo");
				return;
			}
			
			if(!mensajesDoc.isEmpty()){
				JsfUtil.addMessageError(mensajesDoc);
				return;
			}
			
			
			boolean existeDoc = false;
			informesOficiosRSQFacade.guardarInforme(informe);								
			for (AnalisisTecnicoRSQ item : analisisTecnicoRSQLista) {
				analisisTecnicoRSQFacade.guardar(item);
				if(item.getCatalogoGeneralCoa().getDescripcion().equals("Evidencia Fotográfica.") && item.getHabilitado()){
					existeDoc = true;
				}
			}			
			
			if(existeDoc && documentoEvidencia != null && documentoEvidencia.getContenidoDocumento() != null){
				Long id = bandejaTareasBean.getTarea().getProcessInstanceId();
				documentoEvidencia.setProcessinstanceid(id.intValue());
				documentosRSQFacade.guardarDocumento(varTramite, documentoEvidencia,informe.getId());
			}
			
			if(generarDocumento) {
				generarDocumentoInforme(true);
				informesOficiosRSQFacade.guardarInforme(oficio);
			}
						
			String tipoUsuario = "";
			String abreviatura = "";
			
			if(informe.getRegistroSustanciaQuimica().getUsuario().getNombre().length() == 10){
				tipoUsuario = "N";			
			}else{
				Organizacion org = organizacionFacade.buscarPorRuc(informe.getRegistroSustanciaQuimica().getUsuario().getNombre());
				
				if(org != null){
					tipoUsuario = "J";
				}else{
					tipoUsuario = "N";
				}			
			}
								
			if(registroSustanciaQuimica.getCodigo() == null){
				registroSustanciaQuimica.setCodigo(registroSustanciaQuimica.getNumeroAplicacion());
				registroSustanciaQuimica.setNumeroAplicacion(null);
			}
			
			if(registroSustanciaQuimica.getCodigo().equals(registroSustanciaQuimica.getNumeroAplicacion())){
				registroSustanciaQuimica.setNumeroAplicacion(null);
			}
			
			if(registroSustanciaQuimica.getNumeroAplicacion() == null){
				Area area = informe.getRegistroSustanciaQuimica().getArea();
				if(area.getTipoArea().getSiglas().equals("OT")){
					String[] otArray=area.getAreaAbbreviation().split("-");
					abreviatura = area.getAreaAbbreviation() + "-" + otArray[0];				
							
				}else{
					abreviatura = area.getArea().getAreaAbbreviation() + "-" + area.getAreaAbbreviation(); 	
				}
				
				registroSustanciaQuimica.setNumeroAplicacion(registroSustanciaQuimicaFacade.generarCodigoRSQ(abreviatura, tipoUsuario));
			}				
			
			registroSustanciaQuimicaFacade.guardar(registroSustanciaQuimica, JsfUtil.getLoggedUser());
			
			JsfUtil.addMessageInfo("Verifique la información antes de enviar.");    
			noGuardado = false;

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar. Comuníquese con mesa de ayuda.");
		}		
	}
	
	
	public void generarDocumentoInforme(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {			
			String nombreReporte= "InformeTecnicoRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_APROBADO.getIdTipoDocumento():TipoDocumentoSistema.RCOA_RSQ_INFORME_TECNICO_NEGADO.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new InformeTecnicoRSQHtml(informe,pronunAprobado,analisisTecnicoRSQLista,actividadSustanciaLista,observacionesRsqLista,proyectoUbicacionLista,getLabelProponente(),JsfUtil.getLoggedUser()),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        
	    	byte[] byteArchivo = Files.readAllBytes(path);
	    	File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlInforme=JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName());
	        
	        if(!marcaAgua){	        			    	
	        	JsfUtil.uploadDocumentoRSQ(informePdf, documentoInforme);
	        } 
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public void generarDocumentoOficio(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {	
			Area areaTramite = registroSustanciaQuimica.getArea();
			if(areaTramite.getTipoArea().getSiglas().equalsIgnoreCase(Constantes.SIGLAS_TIPO_AREA_OT))
				areaTramite = areaTramite.getArea();
			
			usuarioAutoridad=buscarUsuario(areaTramite,"autoridad");
			if(usuarioAutoridad==null) {
				JsfUtil.addMessageError("No se pudo generar el oficio, Comuníquese on mesa de ayuda");
				verInforme=true;
				verOficio=false;
				return;
			}
			
			String nombreReporte= "OficioPronunciamientoRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(pronunAprobado?TipoDocumentoSistema.RCOA_RSQ_OFICIO_APROBADO.getIdTipoDocumento():TipoDocumentoSistema.RCOA_RSQ_OFICIO_NEGADO.getIdTipoDocumento());
			File informePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new OficioPronunciamientoRSQHtml(oficio,informe,actividadSustanciaLista,getLabelProponente(),usuarioAutoridad,JsfUtil.getLoggedUser()),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(informePdf.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(informePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlOficio=JsfUtil.devolverContexto("/reportesHtml/"+ informePdf.getName());             
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public void generarDocumentoRsq(boolean marcaAgua)
	{			
		FileOutputStream file;
		try {
						
			informe.getRegistroSustanciaQuimica().setVigenciaDesde(new Date());
			
			Calendar fechaAprobacion = Calendar.getInstance();
			fechaAprobacion.set(Calendar.MONTH, 11);
			fechaAprobacion.set(Calendar.DATE, 31);
			Date lastDayOfMonth = fechaAprobacion.getTime();
			informe.getRegistroSustanciaQuimica().setVigenciaHasta(lastDayOfMonth);		
			
			
			String nombreReporte= "DocumentoRSQ";
			
			PlantillaReporte plantillaReporte = this.informesOficiosRSQFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_RSQ_REGISTRO_SUSANCIAS_QUIMICAS.getIdTipoDocumento());
			File filePdf = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new RegistroSustanciasQuimicasHtml(informe,null,actividadSustanciaLista,ubicacionSustanciaProyectoLista,getLabelProponente(),usuarioAutoridad,actividadSustanciaTodoLista),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(filePdf.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(filePdf.getName()));
	        file = new FileOutputStream(fileArchivo);
	        file.write(byteArchivo);
	        file.close();
	        urlRsq=JsfUtil.devolverContexto("/reportesHtml/"+ filePdf.getName());
	             
	        
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	public StreamedContent descargarDocumento() {
		try {
			generarDocumentoInforme(false);
            byte[] byteFile = documentoInforme.getContenidoDocumento();
            if (byteFile != null) {
                InputStream is = new ByteArrayInputStream(byteFile);
                return new DefaultStreamedContent(is, "application/pdf", documentoInforme.getNombre());
            } else {
                JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
                return null;
            }
		} catch (Exception e) {
			LOG.error("error al descargar ", e);
			JsfUtil.addMessageError("Ocurrió un error al descargar el archivo. Por favor comunicarse con mesa de ayuda.");
            return null;
		}
	}
	
	public void uploadFileFirmado(FileUploadEvent event) {
		try{		
			documentoInforme.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
	    	documentosRSQFacade.guardarDocumento(varTramite, documentoInforme,informe.getId());
			firmadoFisico=true;
			JsfUtil.addMessageInfo("El informe técnico aprobado fue adjuntado con éxito");
		}catch(Exception e)
		{
			JsfUtil.addMessageError("Error al guardar el documento del Informe.");
			e.printStackTrace();
		}		
	}	
	
	public String firmaElectronica() {
        try {
        	generarDocumentoInforme(false);
        	documentosRSQFacade.guardarDocumento(varTramite, documentoInforme,informe.getId());        	
        	return DigitalSign.sign(documentosRSQFacade.direccionDescarga(documentoInforme.getIdAlfresco()), JsfUtil.getLoggedUser().getNombre());
        } catch (Throwable e) {
            LOG.error(e);
            JsfUtil.addMessageError("Error al realizar la operación.");
            return "";
        }
    }
	
	public boolean validarFirma(){		
		if((token && documentosRSQFacade.verificarFirmaVersion(documentoInforme.getIdAlfresco()))
		||(!token && firmadoFisico)){
			return true;
		}
		return false;
	}
	
	public void enviar()
	{
		try {
			if(!validarFirma()){
				JsfUtil.addMessageError("Debe firmar el Informe Técnico");
				return;
			}
			
			Usuario usuarioCoordinador=buscarUsuario(registroSustanciaQuimica.getArea(),"coordinador");
			if(usuarioCoordinador==null) {
				JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
				return;
			}
			
			Map<String, Object> params=new HashMap<>();			
			params.put("usuario_coordinador",usuarioCoordinador.getNombre());
			params.put("revision_actual","coordinador");
			params.put("pronunciamiento_aprobado",registroSustanciaQuimica.pronunciamientoAprobado());
			procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
			procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);
			JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);
	        JsfUtil.redirectToBandeja();
		} catch (/*Jbpm*/Exception e) {
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION);
			e.printStackTrace();
		}
	}
	
	public void fileUploadListener(FileUploadEvent event) {
		
		if(documentoEvidencia==null) {
			documentoEvidencia=new DocumentosSustanciasQuimicasRcoa(TipoDocumentoSistema.RCOA_RSQ_EVIDENCIA_FOTOGRAFICA, InformeOficioRSQ.class.getSimpleName(), registroSustanciaQuimica.getId(), bandejaTareasBean.getTarea().getProcessInstanceId());
		}
		documentoEvidencia.cargarArchivo(event.getFile().getContents(),event.getFile().getFileName());
		
	}
}