package ec.gob.ambiente.rcoa.certificado.interseccion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.proyectos.bean.ProyectosBean;
import ec.gob.ambiente.rcoa.certificado.interseccion.facade.CertificadoInterseccionCoaFacade;
import ec.gob.ambiente.rcoa.certificado.interseccion.model.CertificadoInterseccionOficioCoa;
import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.facade.CapasCoaFacade;
import ec.gob.ambiente.rcoa.facade.CatalogoCoaFacade;
import ec.gob.ambiente.rcoa.facade.DetalleInterseccionProyectoAmbientalFacade;
import ec.gob.ambiente.rcoa.facade.DocumentosCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCoaUbicacionFacade;
import ec.gob.ambiente.rcoa.facade.ProyectoLicenciaCuaCiuuFacade;
import ec.gob.ambiente.rcoa.model.CapasCoa;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.DetalleInterseccionProyectoAmbiental;
import ec.gob.ambiente.rcoa.model.DocumentosCOA;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarInforme;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class CertificadoInterseccionRcoaController implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(CertificadoInterseccionRcoaController.class);

	@EJB
	private CertificadoInterseccionCoaFacade certificadoInterseccionCoaFacade;
	
    @EJB
    private OrganizacionFacade organizacionFacade;

	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private ProyectoLicenciaCoaFacade proyectoFacade;
	
	@EJB
    private DocumentosCoaFacade documentoFacade;
	
	@EJB 
    private ProyectoLicenciaCoaUbicacionFacade proyectoLicenciaCoaUbicacionFacade;
	
	@EJB
	private CapasCoaFacade capasCoaFacade;
	@EJB
	private CatalogoCoaFacade catalogoCoaFacade;
	
	@EJB
	private DetalleInterseccionProyectoAmbientalFacade detalleInterseccionProyectoAmbientalFacade;
	@EJB
	private ProyectoLicenciaCuaCiuuFacade proyectoLicenciaCuaCiuuFacade;	
		    
    @ManagedProperty(value = "#{bandejaTareasBean}")
    @Getter
    @Setter
    private BandejaTareasBean bandejaTareasBean;
    
    @Getter
    @Setter
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

	@Getter
	@Setter
	@ManagedProperty(value = "#{proyectosBean}")
	private ProyectosBean proyectosBean;
	

	private CertificadoInterseccionOficioCoa oficioCI;
	private Usuario usuarioAutoridad;
	private List<UbicacionesGeografica> ubicacionProyectoLista;
    private List<DetalleInterseccionProyectoAmbiental> interseccionLista;			
    private List<CapasCoa> capasCoaLista;
	
    private String nombreOperador,cedulaOperador,razonSocial,codigoCiiu, codigoCiiuSec1, codigoCiiuSec2;
    
	@PostConstruct
	public void init() {
		try {
			
			
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrió un error al recuperar los datos CertificadoInterseccionRcoaController.");
		}
	}
	
	String codigoProyecto="MAE-RA-2020-415466";
	
	public void guardar() {
		try {	
			ProyectoLicenciaCoa proyecto=proyectoFacade.buscarProyecto(codigoProyecto);
			
			oficioCI = certificadoInterseccionCoaFacade.obtenerPorCodigoProyecto(proyectosBean.getProyecto().getCodigo());
			if (oficioCI == null) {
				oficioCI = new CertificadoInterseccionOficioCoa();
				oficioCI.setProyectoLicenciaCoa(proyecto);			
									
			}			
			certificadoInterseccionCoaFacade.guardar(oficioCI);
			
			Usuario uOperador=usuarioFacade.buscarUsuario(proyecto.getUsuarioCreacion());
			Organizacion orga=organizacionFacade.buscarPorRuc(proyecto.getUsuarioCreacion());
			
			nombreOperador=uOperador.getPersona().getNombre();
			cedulaOperador=uOperador.getPersona().getPin();
			razonSocial=orga==null?" ":orga.getNombre();
			//busco la actividad  ciiu principal
			ProyectoLicenciaCuaCiuu actividadPrincipal = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			codigoCiiu = actividadPrincipal.getCatalogoCIUU() != null ? actividadPrincipal.getCatalogoCIUU().getCodigo(): "";
			//busco la actividad  ciiu complementaria 1
			ProyectoLicenciaCuaCiuu actividadComplementaria1 = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			codigoCiiuSec1 = actividadComplementaria1.getCatalogoCIUU() != null ? actividadPrincipal.getCatalogoCIUU().getCodigo(): "";
			//busco la actividad  ciiu complementaria 2
			ProyectoLicenciaCuaCiuu actividadComplementaria2 = proyectoLicenciaCuaCiuuFacade.actividadPrincipal(proyecto);
			codigoCiiuSec2 = actividadComplementaria2.getCatalogoCIUU() != null ? actividadPrincipal.getCatalogoCIUU().getCodigo(): "";
			
			Area area=new Area();
			area.setId(Area.DIRECCION_NACIONAL_PREVENCION_CONTAMINACION_AMBIENTAL);
			//DIRECTOR DE PREVENCIÓN DE LA CONTAMINACIÓN AMBIENTAL
			//AUTORIDAD AMBIENTAL MAE
			List<Usuario>uList=usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL MAE",area /*proyecto.getAreaResponsable()*/);
			if(uList==null || uList.size()==0)			
			{
				JsfUtil.addMessageError("No se encontró el usuario en "+proyecto.getAreaResponsable().getAreaName());
				return;
			}else{
				usuarioAutoridad=uList.get(0);
			}
			
			ubicacionProyectoLista=proyectoLicenciaCoaUbicacionFacade.buscarPorProyecto(proyecto);			
			interseccionLista= detalleInterseccionProyectoAmbientalFacade.buscarPorProyecto(proyecto);			
			capasCoaLista=capasCoaFacade.listaCapasCertificadoInterseccion();
			
			generarDocumento(false);			
			                       

		} catch (Exception e) {
			LOG.error("error al guardar ", e);
			JsfUtil.addMessageError("Ocurrió un error al guardar CertificadoInterseccionRcoaController");
		}		
	}	
	
	public void generarDocumento(boolean marcaAgua)
	{			
		FileOutputStream fileOutputStream;
		try {			
			String nombreReporte= "CertificadoInterseccionRcoa";
			String capasConali = capasCONALI();
			
			PlantillaReporte plantillaReporte = certificadoInterseccionCoaFacade.obtenerPlantillaReporte(TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO.getIdTipoDocumento());
			File file = JsfUtil.fileMarcaAgua(UtilGenerarInforme.generarFichero(plantillaReporte.getHtmlPlantilla(), nombreReporte, true,new CertificadoInterseccionRcoaOficioHtml(oficioCI,nombreOperador,cedulaOperador,razonSocial,codigoCiiu,capasConali,usuarioAutoridad,ubicacionProyectoLista,interseccionLista,capasCoaLista),null),marcaAgua?" - - BORRADOR - - ":" ",BaseColor.GRAY);
	        Path path = Paths.get(file.getAbsolutePath());
	        byte[] byteArchivo = Files.readAllBytes(path);
	        File fileArchivo = new File(JsfUtil.devolverPathReportesHtml(file.getName()));
	        fileOutputStream = new FileOutputStream(fileArchivo);
	        fileOutputStream.write(byteArchivo);
	        fileOutputStream.close();
	        
	        if(!marcaAgua){
	        	DocumentosCOA documento = new DocumentosCOA();	        	
	        	documento.setContenidoDocumento(Files.readAllBytes(path));
	        	documento.setTipo("application/pdf");
	        	documento.setIdTabla(oficioCI.getId());	       		
	        	documento.setNombreTabla(CertificadoInterseccionOficioCoa.class.getSimpleName());
	        	documento.setNombreDocumento(nombreReporte+"_"+oficioCI.getCodigo()+".pdf");
	        	documento.setExtencionDocumento(".pdf");
	        	documento.setIdProceso(bandejaTareasBean.getProcessId());
	        	
	        	documentoFacade.guardarDocumentoAlfresco(codigoProyecto, "Certificado_Interseccion", 1L, documento, TipoDocumentoSistema.RCOA_CERTIFICADO_INTERSECCION_OFICIO);
	        }        
	       
	       } catch (Exception e) {
	       	e.printStackTrace();
	       }
	}
	
	private String capasCONALI(){
		// obtengo la informacion geografica externa CONALI
		List<CatalogoGeneralCoa> listaCapas =catalogoCoaFacade.obtenerCatalogo(CatalogoTipoCoaEnum.COA_CAPAS_EXTERNAS_CONALI);
		String strTableCapaCONALI = "";
		for (CatalogoGeneralCoa catalogoCapas : listaCapas) {		
			strTableCapaCONALI += catalogoCapas.getDescripcion() + "<br/>";
		}
		return strTableCapaCONALI;
	}
	
}