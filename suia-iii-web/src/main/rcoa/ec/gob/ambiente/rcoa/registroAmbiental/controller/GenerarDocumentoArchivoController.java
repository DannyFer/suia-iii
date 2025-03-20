package ec.gob.ambiente.rcoa.registroAmbiental.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import com.itextpdf.text.BaseColor;

import ec.gob.ambiente.rcoa.dto.EntityOficioArchivoRAPPC;
import ec.gob.ambiente.rcoa.facade.DocumentosRegistroAmbientalFacade;
import ec.gob.ambiente.rcoa.model.DocumentoRegistroAmbiental;
import ec.gob.ambiente.rcoa.model.OficioPronunciamientoPPC;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCuaCiuu;
import ec.gob.ambiente.rcoa.model.RegistroAmbientalRcoa;
import ec.gob.ambiente.rcoa.proyecto.controller.ProyectoSedeZonalUbicacionController;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.PlantillaReporteFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoUsuarioFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.PlantillaReporte;
import ec.gob.ambiente.suia.domain.TipoUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.dto.EntityResolucionAmbientalRCOA;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.ambiente.suia.utils.UtilGenerarPdf;

@ManagedBean
@ViewScoped
public class GenerarDocumentoArchivoController {
	
	@EJB	
	private PlantillaReporteFacade plantillaReporteFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private DocumentosRegistroAmbientalFacade documentosRegistroFacade;
	@EJB
	private TipoUsuarioFacade tipoUsuarioFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@Getter
	@Setter
	private String nombreReporte;
	
	@Getter
	@Setter
	private byte[] archivoReporte;
	
	@Getter
	@Setter
	private String urlReporte;
	
	public OficioPronunciamientoPPC generarResolucionRegistroAmbiental(RegistroAmbientalRcoa registro, OficioPronunciamientoPPC oficio, boolean marcaAgua) throws Exception{
		
		
		String tipoEnteResponsable = null;
		Area areaResponsable = registro.getProyectoCoa().getAreaResponsable();
		String siglas = null;
		String areaAutoridad = null;
		Usuario usuarioAutoridad = new Usuario();
		String nombreAutoridad;
		String rol="";
		
				
		switch (registro.getProyectoCoa().getAreaResponsable().getTipoArea().getSiglas().toUpperCase()) {
		case "DP":
			areaResponsable = registro.getProyectoCoa().getAreaResponsable();
			areaAutoridad = areaResponsable.getAreaName();
			rol = "role.ppc.galapagos.autoridad";
			break;
		case "ZONALES":
			areaResponsable = registro.getProyectoCoa().getAreaResponsable();
			areaAutoridad = areaResponsable.getAreaName();
			rol = "role.ppc.cz.autoridad";
			break;
		case "OT":
			areaResponsable = registro.getProyectoCoa().getAreaResponsable().getArea();			 
			areaAutoridad = areaResponsable.getAreaName();
			rol = "role.ppc.cz.autoridad";
			break;
		case "PC":
			areaResponsable = registro.getProyectoCoa().getAreaResponsable();
			areaAutoridad = areaResponsable.getAreaName();
			rol="role.ppc.pc.autoridad";
			break;
		case "EA":
			areaResponsable = registro.getProyectoCoa().getAreaResponsable();
			areaAutoridad = areaResponsable.getAreaName();
			rol="role.ppc.gad.autoridad";
			break;
		default:
			break;
		}
		
		List<Usuario> listaUsuario = null;		
		
		String rolAutoridad = Constantes.getRoleAreaName(rol);
		
		listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rolAutoridad, areaResponsable);
		
		if(listaUsuario != null && listaUsuario.size() > 0){
			nombreAutoridad = listaUsuario.get(0).getPersona().getNombre();
			usuarioAutoridad = listaUsuario.get(0);
		}else{
			JsfUtil.addMessageError(JsfUtil.MESSAGE_ERROR_COMPLETAR_OPERACION_MESA_AYUDA);
			return null;
		}		
		
		PlantillaReporte plantillaReporte = plantillaReporteFacade.getPlantillaReporte(TipoDocumentoSistema.RA_PPC_OFICIO_ARCHIVO);
		
		EntityOficioArchivoRAPPC entityOficio = new EntityOficioArchivoRAPPC();
		SimpleDateFormat fecha= new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es"));

		
		entityOficio.setUbicacion(usuarioAutoridad.getPersona().getUbicacionesGeografica().getUbicacionesGeografica().getNombre());
		entityOficio.setFechaEmision(fecha.format(new Date()));
		
		// representante legal
		String usuariocreacion = registro.getProyectoCoa().getUsuario().getNombre();
		String razonSocial = "", representanteLegal = "", cedulaRepresentanteLegal="";
		if(usuariocreacion.length() == 13){
			Organizacion organizacion = organizacionFacade.buscarPorRuc(usuariocreacion);				
			if(organizacion != null && organizacion.getNombre() != null){
				razonSocial = organizacion.getNombre();
				representanteLegal = organizacion.getPersona().getNombre();
				cedulaRepresentanteLegal = organizacion.getPersona().getPin();
			}else{
				razonSocial = registro.getProyectoCoa().getUsuario().getPersona().getNombre();
				representanteLegal = registro.getProyectoCoa().getUsuario().getPersona().getNombre();
				cedulaRepresentanteLegal =  registro.getProyectoCoa().getUsuario().getNombre();
				}
		}else{
			Persona persona = registro.getProyectoCoa().getUsuario().getPersona();
			if(persona != null && persona.getNombre() != null){
				razonSocial = persona.getNombre();
				representanteLegal = persona.getNombre();
				cedulaRepresentanteLegal =  persona.getPin();
			}
		}
		entityOficio.setNombreOperador(razonSocial);
        
		entityOficio.setAntecedentes(oficio.getAntecedentes());
		entityOficio.setAsunto(oficio.getAsunto());
		entityOficio.setPronunciamiento(oficio.getPronunciamiento());
		entityOficio.setNumeroOficio(oficio.getCodigoDocumento());
        
        List<TipoUsuario> listaTipo = new ArrayList<TipoUsuario>();
		TipoUsuario tipoUsuario = new TipoUsuario();
		listaTipo = tipoUsuarioFacade.obtenerListaTipoUsuario(usuarioAutoridad);
		boolean encargado = false;
		if(listaTipo != null && !listaTipo.isEmpty()){
			tipoUsuario = listaTipo.get(0);
			
			if(tipoUsuario.getTipo().equals(2)){
				encargado = true;
			}
		}	
		entityOficio.setNombreAutoridad(nombreAutoridad);
		entityOficio.setAreaAutoridad(areaAutoridad);
        
        nombreReporte = "OficioArchivoProcesoRegularizacion.pdf";
        File informePdfAux = UtilGenerarPdf.generarFichero(
				plantillaReporte.getHtmlPlantilla(),
				nombreReporte, true, entityOficio);
		
		File informePdf = JsfUtil.fileMarcaAgua(informePdfAux, marcaAgua ? " - - BORRADOR - - " : " ", BaseColor.GRAY);

		Path path = Paths.get(informePdf.getAbsolutePath());
		
		archivoReporte = Files.readAllBytes(path);
		String reporteHtmlfinal = nombreReporte.replace("/", "-");
		File archivoFinal = new File(JsfUtil.devolverPathReportesHtml(reporteHtmlfinal));
		FileOutputStream file = new FileOutputStream(archivoFinal);
		file.write(archivoReporte);
		file.close();
		String url = JsfUtil.devolverContexto("/reportesHtml/" + nombreReporte);
		
		oficio.setUrl(url);
		oficio.setContenido(archivoReporte);
		oficio.setNombreReporte(nombreReporte);
		oficio.setDirector(usuarioAutoridad);
		urlReporte = url;		
		return oficio;	
	}
	
	
	
	

}
