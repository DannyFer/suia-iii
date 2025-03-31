package ec.gob.ambiente.prevencion.certificado.ambiental.controllers;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.Lob;
import javax.persistence.Query;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ec.gob.ambiente.rcoa.facade.CoordenadasProyectoCoaFacade;
import ec.gob.ambiente.rcoa.facade.CriterioMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCertificadoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.GestionarProductosQuimicosProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaAmbientalCoaShapeFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.facade.ValorMagnitudFacade;
import ec.gob.ambiente.rcoa.facade.VariableCriterioFacade;
import ec.gob.ambiente.rcoa.model.CatalogoCIUU;
import ec.gob.ambiente.rcoa.model.CoordenadasProyecto;
import ec.gob.ambiente.rcoa.model.CriterioMagnitud;
import ec.gob.ambiente.rcoa.model.DocumentoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.GestionarProductosQuimicosProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoCertificadoAmbiental;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaAmbientalCoaShape;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.ValorMagnitud;
import ec.gob.ambiente.rcoa.model.VariableCriterio;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.DesechosRegistroGeneradorRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.facade.RegistroGeneradorDesechosProyectosRcoaFacade;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.DesechosRegistroGeneradorRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosProyectosRcoa;
import ec.gob.ambiente.rcoa.registroGeneradorDesechos.model.RegistroGeneradorDesechosRcoa;
import ec.gob.ambiente.rcoa.util.CoordendasPoligonos;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.SustanciaQuimicaPeligrosa;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.survey.SurveyResponseFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class DescargarGuiasController {
	
	private static final Logger LOG = Logger.getLogger(DescargarGuiasController.class);
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoLicenciaCoaFacade;
	@EJB
	private ProyectoLicenciaAmbientalCoaShapeFacade proyectoLicenciaAmbientalShapeFacade;
	@EJB
	private CoordenadasProyectoCoaFacade coordenadasProyectoCoaFacade;
	@EJB
	private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaUbicacionFacade;
	@EJB
	private DocumentosCertificadoAmbientalFacade documentosCertificadoAmbientalFacade;
	@EJB
	private GestionarProductosQuimicosProyectoAmbientalFacade gestionarProductosQuimicosProyectoAmbientalFacade;	
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;
	@EJB
	private CriterioMagnitudFacade criterioMagnitudFacade;
	@EJB
	private VariableCriterioFacade variableCriterioFacade;
	@EJB
	private ValorMagnitudFacade valorMagnitudFacade;
	@EJB
	private SurveyResponseFacade surveyResponseFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private DesechosRegistroGeneradorRcoaFacade desechosRegistroGeneradorRcoaFacade;
	@EJB
	private RegistroGeneradorDesechosProyectosRcoaFacade registroGeneradorDesechosProyectosRcoaFacade;
	@EJB
	private CrudServiceBean crudServiceBean;
		
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyecto;
	
	@Getter
	@Setter
	private List<ProyectoLicenciaAmbientalCoaShape> formas;
	@Getter
	@Setter
	private List<UbicacionesGeografica> ubicacionesSeleccionadas;
	@Getter
	@Setter
	private List<CoordenadasProyecto> coordenadasGeograficas;
	@Getter
	@Setter
    private List<CoordendasPoligonos> coordinatesWrappers;
	@Getter
	@Setter
	private List<SustanciaQuimicaPeligrosa> sustanciaQuimicaSeleccionada = new ArrayList<SustanciaQuimicaPeligrosa>();
	
	@Getter
    @Setter
    @ManagedProperty(value = "#{certificadoAmbientalController}")
    private CertificadoAmbientalController certificadoAmbientalController;

	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	byte[] byteGuias, byteGuiasRgd, byteGuiasAlmacenamiento;
	
	private String tramite;
	
	private Area area;
	
	private Map<String, Object> variables;
	
	@ManagedProperty(value = "#{bandejaTareasBean}")
	@Getter
	@Setter
	private BandejaTareasBean bandejaTareasBean;

	@Getter
	@Setter
    private boolean descargaGuias = false;
    
	@Getter
	@Setter
    private boolean descargaRgd = false;
	
	@Getter
	@Setter
    private boolean descargaAlmacenamiento = false;
	
	@Getter
	@Setter
	private boolean deshabilitar = true;
	
	@Getter
	@Setter
	private CatalogoCIUU ciiuPrincipal= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria1= new CatalogoCIUU();
	@Getter
	@Setter
	private CatalogoCIUU ciiuComplementaria2= new CatalogoCIUU();
	
	@Getter
	@Setter
	private List<ProyectoLicenciaCuaCiuu> listaActividadesCiuu;
	
	@Getter
	@Setter
	private List<CriterioMagnitud> listaCriterioMagnitud;
	
	@Getter
	@Setter
	private CriterioMagnitud criterioMagnitud;
	
	@Getter
	@Setter
	private VariableCriterio variableCriterio;
	
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud1 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud2 = new ValorMagnitud();
	@Getter
	@Setter
	private ValorMagnitud valorMagnitud3 = new ValorMagnitud();	
	@Getter
	@Setter
	private ValorMagnitud valorMagnitudCalculo = new ValorMagnitud();
	
	//para la encuesta
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
	@Getter
    @Setter
    public static String surveyLink = Constantes.getPropertyAsString("suia.survey.link");
	
	@Getter
	@Setter
	private String urlLinkSurvey;
	
	@Getter
	@Setter
	private boolean mostrarEncuesta = true;
	
	@Getter
	@Setter
	private boolean showSurveyD = false;
	
	@Getter
	@Setter
	private String sector = "";
	
	@Getter
	@Setter
	private List<DocumentoCertificadoAmbiental> listaDocumentos;
	
	@Getter
	@Setter
	private boolean finalizar = false;
	
	public String dblinkBpmsSuiaiii = Constantes.getDblinkBpmsSuiaiii();
	
	@PostConstruct
	public void init(){
		try {
			
			variables=procesoFacade.recuperarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId());
			tramite=(String)variables.get(Constantes.VARIABLE_PROCESO_TRAMITE);		
			
			proyecto = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
			
			area = proyecto.getAreaResponsable();
			ubicacionesSeleccionadas = new ArrayList<UbicacionesGeografica>();
			coordenadasGeograficas = new ArrayList<CoordenadasProyecto>();
			coordinatesWrappers = new ArrayList<CoordendasPoligonos>();
			
			if(proyecto != null && proyecto.getId() != null){
				formas = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 2, 0); //coordenadas geograficas
				
				if(formas == null){
					formas = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formas){
						coordenadasGeograficas = coordenadasProyectoCoaFacade.buscarPorForma(forma);
					}
				}
				
				List<ProyectoLicenciaAmbientalCoaShape> formasImplantacion = proyectoLicenciaAmbientalShapeFacade.buscarFormaGeograficaPorProyecto(proyecto, 1, 0); //coordenadas implantacion
				
				if(formasImplantacion == null){
					formasImplantacion = new ArrayList<ProyectoLicenciaAmbientalCoaShape>();				
				}else{
					for(ProyectoLicenciaAmbientalCoaShape forma : formasImplantacion){
						List<CoordenadasProyecto> coordenadasGeograficasImplantacion = coordenadasProyectoCoaFacade.buscarPorForma(forma);
						
						CoordendasPoligonos poligono = new CoordendasPoligonos();
						poligono.setCoordenadas(coordenadasGeograficasImplantacion);
						poligono.setTipoForma(forma.getTipoForma());
						
						coordinatesWrappers.add(poligono);
					}
				}
				
				listaActividadesCiuu = proyectoLicenciaCuaCiuuFacade.actividadesPorProyecto(proyecto);
				
				
				ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
	    		sector = actividadPrincipal.getCatalogoCIUU().getTipoSector().getNombre();
	    		
				
				for(ProyectoLicenciaCuaCiuu actividad : listaActividadesCiuu){
					if(actividad.getOrderJerarquia() == 1){
						ciiuPrincipal = actividad.getCatalogoCIUU();
					}else if(actividad.getOrderJerarquia() == 2){
						ciiuComplementaria1 = actividad.getCatalogoCIUU();
					}else if(actividad.getOrderJerarquia() == 3){
						ciiuComplementaria2 = actividad.getCatalogoCIUU();
					}
				}
				
				ciiuPrincipal = actividadPrincipal.getCatalogoCIUU();
				
				listaCriterioMagnitud = criterioMagnitudFacade.buscarCriterioMagnitudPorProyecto(proyecto);
				
				if(listaCriterioMagnitud != null && !listaCriterioMagnitud.isEmpty()){
					for(CriterioMagnitud criterioMag : listaCriterioMagnitud){
						variableCriterio = new VariableCriterio();
						variableCriterio = variableCriterioFacade.buscarVariableCriterioPorId(criterioMag.getVariableCriterio());
					
											
						if(variableCriterio.getNivelMagnitud().getId() == 1){							
							valorMagnitud1 = criterioMag.getValorMagnitud();							
						}else if(variableCriterio.getNivelMagnitud().getId() == 2){
							valorMagnitud2 = criterioMag.getValorMagnitud();
						}else{
							valorMagnitud3 = criterioMag.getValorMagnitud();
						}
					}					
				}				
			}
			
			byteGuias = documentosCertificadoAmbientalFacade.descargarDocumentoPorNombre("GUIAS_DE_BUENAS_PRACTICAS_AMBIENTALES_CA_RCOA.pdf");
			if(proyecto.getGeneraDesechos()){
				
				byteGuiasAlmacenamiento = documentosCertificadoAmbientalFacade
						.descargarDocumentoPorNombre("GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf");
				listaDocumentos = new ArrayList<DocumentoCertificadoAmbiental>();
				
				List<RegistroGeneradorDesechosProyectosRcoa> registroGeneradorDesechosProyectosRcoas = registroGeneradorDesechosProyectosRcoaFacade
						.asociados(proyecto.getId());

				RegistroGeneradorDesechosRcoa registro = new RegistroGeneradorDesechosRcoa();
				if (registroGeneradorDesechosProyectosRcoas != null
						&& !registroGeneradorDesechosProyectosRcoas.isEmpty()) {
					registro = registroGeneradorDesechosProyectosRcoas.get(0)
							.getRegistroGeneradorDesechosRcoa();
					
				}	
				
				List<DesechosRegistroGeneradorRcoa> listaDesechos = desechosRegistroGeneradorRcoaFacade.buscarPorRegistroGeneradorGenerados(registro);
				
				
				for(DesechosRegistroGeneradorRcoa desecho : listaDesechos){
					boolean existeDocumento = false;
					try{
						String clave = desecho.getDesechoPeligroso().getClave();
						String claveNombre = clave.replace("-", "."); 
						
						String nombreDocumento = claveNombre + ".pdf";
						
						byteGuiasRgd = documentosCertificadoAmbientalFacade.descargarDocumentoPorNombre(nombreDocumento);
						existeDocumento = true;
					}catch(Exception ex){
						existeDocumento = false;
					}
					if(existeDocumento){
						DocumentoCertificadoAmbiental documento = new DocumentoCertificadoAmbiental();
						documento.setContenidoDocumento(byteGuiasRgd);
						documento.setNombre(desecho.getDesechoPeligroso().getClave());
						documento.setDescripcion(desecho.getDesechoPeligroso().getClave() + "-" + desecho.getDesechoPeligroso().getDescripcion());
						listaDocumentos.add(documento);
					}
				}	
			}
			
			for(GestionarProductosQuimicosProyectoAmbiental lista:gestionarProductosQuimicosProyectoAmbientalFacade.listaSustanciasQuimicas(proyecto))
			{
				sustanciaQuimicaSeleccionada.add(lista.getSustanciaquimica());
			}
			
			ubicacionesSeleccionadas = proyectoLicenciaUbicacionFacade.buscarPorProyecto(proyecto);
			
						
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public boolean verEncuesta(){		
		if(!surveyResponseFacade.findByProject(tramite)){			
			mostrarEncuesta = true;
		}else{
			mostrarEncuesta = false;
		}	
		
		return mostrarEncuesta;
	}
	
	public void showSurvey() {		
		
		String url = surveyLink;
		String usuarioUrl = loginBean.getNombreUsuario();
		String proyectoUrl = tramite;
		String appUlr = "certificado-ambiental-rcoa";
		String tipoPerUrl = getProponente();
		String tipoUsr = "externo";
		url = url + "/faces/index.xhtml?" 
				+ "usrid=" + usuarioUrl + "&app=" + appUlr + "&project=" + proyectoUrl 
				+ "&tipoper=" + tipoPerUrl + "&tipouser=" + tipoUsr;
		System.out.println("enlace>>>" + url);
		urlLinkSurvey = url;
		showSurveyD = true;
	}

	private String getProponente()
	{		
		try {
			Usuario user=loginBean.getUsuario();
			if(user.getNombre().length()==13)
			{
				Organizacion orga=organizacionFacade.buscarPorRuc(user.getNombre());
				if(orga!=null)
					return "juridico";
			}		
			return "natural";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}		
	}
	
	public List<CoordenadasProyecto> buscarPorForma(ProyectoLicenciaAmbientalCoaShape forma){
		
		List<CoordenadasProyecto> lista = new ArrayList<CoordenadasProyecto>();
		try {
			
			lista = coordenadasProyectoCoaFacade.buscarPorForma(forma);
			return lista;			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}
	
	public StreamedContent getGuias(){
		try {
			if(byteGuias != null){
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(byteGuias), "application/pdf", "Guias Buenas Practicas Ambientales.pdf");		
				descargaGuias = true;
				return streamedContent;
			}			
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar las guías de buenas prácticas ambientales.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar las guías de buenas prácticas ambientales.");
		return null;
	}
	
	public StreamedContent getGuiasRgd(){
		try {
			if(byteGuiasRgd != null){
				StreamedContent streamedContent = new DefaultStreamedContent(new ByteArrayInputStream(byteGuias), "application/pdf", "Guias Registro Generador Desechos.pdf");		
				descargaRgd = true;
				return streamedContent;
			}			
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar las guías del Registro Generador de Desechos.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar las guías del Registro Generador de Desechos.");
		return null;
	}
	
	public void finalizar(){
		try {
			if(!verificarDescargaGuias()){
				JsfUtil.addMessageError("Debe descargarse el/los documentos de Guías.");
				return;
			}
			
			if(!finalizar){
				
				
				String roleKey="role.certificado.autoridad";
				Usuario usuarioAutoridad = new Usuario();
				String areaNombre = "";
				try {
					
					if(area.getTipoArea().getSiglas().equals("OT")){
						usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area.getArea()).get(0);
						areaNombre = area.getArea().getAreaName();
					}else if(area.getTipoArea().getSiglas().equals("PC")){
						List<Usuario> listaUsuarios = usuarioFacade.buscarUsuarioPorRolActivo(Constantes.getRoleAreaName("role.area.subsecretario.calidad.ambiental"));
						roleKey = "role.area.subsecretario.calidad.ambiental";
						if(listaUsuarios != null && !listaUsuarios.isEmpty()){
							usuarioAutoridad = listaUsuarios.get(0);
						}else
							usuarioAutoridad = null;
						
						areaNombre = area.getAreaName();
					}else{
						usuarioAutoridad = usuarioFacade.buscarUsuariosPorRolYArea(Constantes.getRoleAreaName(roleKey), area).get(0);
						areaNombre = area.getAreaName();
					}				
					
				} catch (Exception e) {
					JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
					System.out.println("No existe usuario con rol " + Constantes.getRoleAreaName(roleKey) +" asignado al area " + areaNombre);
					return;
				}			
				
				if(usuarioAutoridad == null){
					JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda");
					System.out.println("No existe usuario con rol " + Constantes.getRoleAreaName(roleKey) +" asignado al area " + areaNombre);
					return;
				}
				generarCertificadoAmbientalQA();
				finalizar = true;

				Map<String, Object> params=new HashMap<>();
				params.put("autoridadAmbiental",usuarioAutoridad.getNombre());
						
				procesoFacade.modificarVariablesProceso(JsfUtil.getLoggedUser(), bandejaTareasBean.getProcessId(), params);
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), bandejaTareasBean.getTarea().getTaskId(), bandejaTareasBean.getProcessId(), null);			
				
				finalizarProcesoCertificado(usuarioAutoridad);				
				
				JsfUtil.addMessageInfo(JsfUtil.MESSAGE_INFO_OPERACION_SATISFACTORIA);	
		        JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public boolean verificarDescargaGuias(){	
		
		if(proyecto.getGeneraDesechos() == true){
			if(descargaGuias && descargaDesechos() && descargaAlmacenamiento)
				return true;				
			else
				return false;
		}else{
			if(descargaGuias)
				return true;
			else
				return false;
		}		
	}
	
	public StreamedContent getEtiquetado(DocumentoCertificadoAmbiental documento) {
		try {
			
			String clave = documento.getNombre();
			String claveNombre = clave.replace("-", "."); 
			
			String documentoBuscar = claveNombre+".pdf";			
			byte[] guia = documentosCertificadoAmbientalFacade.descargarDocumentoPorNombre(documentoBuscar);
			
			if (guia != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(guia),
						"application/pdf",
						documentoBuscar);	
				
				documento.setNombreTabla("DESCARGADO");
				return streamedContent;
			}
			descargaRgd = true;
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar el documento de etiquetas");
			e.printStackTrace();
		}
		return null;
	}
	

	public void descargarRgd(){
		System.out.println("entro en metodo");
		descargaRgd = true;
	}
	
	public boolean descargaDesechos(){
		
		boolean descargado = true;
		if(listaDocumentos != null && !listaDocumentos.isEmpty()){
			for(DocumentoCertificadoAmbiental doc : listaDocumentos){
				if(doc.getNombreTabla() == null || doc.getNombreTabla().isEmpty()){
					descargado = false;
					break;
				}
			}	
		}			
		
		return descargado;
	}
	
	public StreamedContent getGuiasAlmacenamiento() {
		try {
			if (byteGuiasAlmacenamiento != null) {
				StreamedContent streamedContent = new DefaultStreamedContent(
						new ByteArrayInputStream(byteGuiasAlmacenamiento),
						"application/pdf",
						"GUÍA REFERENCIAL DE ALMACENAMIENTO DE RESIDUOS O DESECHOS PELIGROSOS.pdf");
				descargaAlmacenamiento = true;
				return streamedContent;
			}
		} catch (Exception e) {
			JsfUtil.addMessageError("No se pudo descargar la guía referencial de almacenamiento.");
			e.printStackTrace();
		}
		JsfUtil.addMessageError("No se pudo descargar la guía referencial de almacenamiento.");
		return null;
	}
	
	private void generarCertificadoAmbientalQA(){
		creaDocumento();
	}
		
	private void creaDocumento(){
		try {			
			certificadoAmbientalController.enviarFicha(proyecto.getId(), bandejaTareasBean.getProcessId());			
					
		} catch (Exception e) {
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void finalizarProcesoCertificado(Usuario usuarioAutoridad){
		try {
			String sql="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%firmarElectronicamenteCertificado%'' and v.variableid = ''tramite'' and v.value = ''" + proyecto.getCodigoUnicoAmbiental() +"'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.CertificadoAmbiental'' ') "
					+ "as (id varchar, tramite varchar) "
					+ "order by 1";	
			
			Query query = crudServiceBean.getEntityManager().createNativeQuery(sql);
			List<Object>  resultList = new ArrayList<Object>();
			resultList=query.getResultList();
			List<String[]>listaCodigosProyectos= new ArrayList<String[]>();		
			if (resultList.size() > 0) {
				for (Object a : resultList) {
					Object[] row = (Object[]) a;
					listaCodigosProyectos.add(new String[] { (String) row[0],(String) row[1]});
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectos) {
				
				String idTask = codigoProyecto[0];
				procesoFacade.aprobarTarea(usuarioAutoridad, Long.valueOf(idTask), bandejaTareasBean.getProcessId(), null);	
				break;
			}
			
			String sqlDescarga="select * from dblink('"+dblinkBpmsSuiaiii+"','select t.id, v.value  "
					+ "from task t inner join variableinstancelog v on t.processinstanceid = v.processinstanceid "
					+ "where t.formname like ''%descargarCertificadoAmbiental%'' and v.variableid = ''tramite'' and v.value = ''" + proyecto.getCodigoUnicoAmbiental() +"'' "
					+ "and t.status not in (''Completed'',''Exited'',''Ready'') "
					+ "and t.formname is not null and t.processid = ''rcoa.CertificadoAmbiental'' ') "
					+ "as (id varchar, tramite varchar) "
					+ "order by 1";	
			
			Query queryFirma = crudServiceBean.getEntityManager().createNativeQuery(sqlDescarga);
			List<Object>  resultListDescarga = new ArrayList<Object>();
			resultListDescarga=queryFirma.getResultList();
			List<String[]>listaCodigosProyectosDescarga= new ArrayList<String[]>();		
			if (resultListDescarga.size() > 0) {
				for (Object a : resultListDescarga) {
					Object[] row = (Object[]) a;
					listaCodigosProyectosDescarga.add(new String[] { (String) row[0],(String) row[1]});
				}
			}
			
			for (String[] codigoProyecto : listaCodigosProyectosDescarga) {
				
				String idTaskDescarga = codigoProyecto[0];
				procesoFacade.aprobarTarea(JsfUtil.getLoggedUser(), Long.valueOf(idTaskDescarga), bandejaTareasBean.getProcessId(), null);	
				break;
			}
			
			
		} catch (Exception e) {
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		}
	}
	
	public void iniciar(){
		iniciarProceso(tramite);
	}


private boolean iniciarProceso(String tramite){
	try {
		tramite = "MAAE-RA-2021-360339";
		ProyectoLicenciaCoa proyectoInicial = proyectoLicenciaCoaFacade.buscarProyecto(tramite);
		
		CertificadoAmbientalController certificadoAmbientalController = (CertificadoAmbientalController) BeanLocator.getInstance(CertificadoAmbientalController.class);
		certificadoAmbientalController.guardarGuias(proyectoInicial, Long.valueOf("108763"));
		

//		
//		CertificadoAmbientalController certificadoAmbientalController = (CertificadoAmbientalController) BeanLocator.getInstance(CertificadoAmbientalController.class);
//		certificadoAmbientalController.obtenerIdProcesoCertificado(tramite);
		
		certificadoAmbientalController.enviarMailOperador(proyectoInicial);
		
//		
//		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
//		parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
//		parametros.put("tramite", tramite);	
//		parametros.put("existeInventarioForestal", false);
//		parametros.put("idProyecto", proyectoInicial.getId());
//		parametros.put("urlGeneracionAutomatica", Constantes.getUrlGeneracionAutomatica()+ proyectoInicial.getId());
//		
//		procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_CERTIFICADO_AMBIENTAL,tramite, parametros);	
		
		
		
		return true;
	} catch (Exception e) {
		JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
		LOG.error(e.getMessage()+" "+e.getCause().getMessage());
		return false;
	}
	
	
}

}
