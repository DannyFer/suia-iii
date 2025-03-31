/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.administracion.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Image;

import ec.gob.ambiente.alfresco.connection.Documents;
import ec.gob.ambiente.alfresco.service.AlfrescoServiceBean;
import ec.gob.ambiente.cliente.ConsultaRucCedula;
import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.rcoa.facade.AreasSnapProvinciaFacade;
import ec.gob.ambiente.rcoa.model.AreasSnapProvincia;
import ec.gob.ambiente.suia.administracion.bean.PromotorBean;
import ec.gob.ambiente.suia.administracion.bean.UsuarioBean;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaUsuarioFacade;
import ec.gob.ambiente.suia.administracion.facade.ContactoFacade;
import ec.gob.ambiente.suia.administracion.facade.EntidadRolFacade;
import ec.gob.ambiente.suia.administracion.facade.ImpedidosFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.administracion.facade.TipoUsuarioFacade;
import ec.gob.ambiente.suia.administracion.facade.UnidadAreaFacade;
import ec.gob.ambiente.suia.administracion.facade.UnidadAreaUsuarioFacade;
import ec.gob.ambiente.suia.administracion.facade.UnidadFacade;
import ec.gob.ambiente.suia.bandeja.beans.BandejaTareasBean;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.domain.AreaUsuario;
import ec.gob.ambiente.suia.domain.AuditarUsuario;
import ec.gob.ambiente.suia.domain.Contacto;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.EntidadRol;
import ec.gob.ambiente.suia.domain.FormasContacto;
import ec.gob.ambiente.suia.domain.Nacionalidad;
import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Persona;
import ec.gob.ambiente.suia.domain.Rol;
import ec.gob.ambiente.suia.domain.TipoOrganizacion;
import ec.gob.ambiente.suia.domain.TipoTratos;
import ec.gob.ambiente.suia.domain.TipoUsuario;
import ec.gob.ambiente.suia.domain.UbicacionesGeografica;
import ec.gob.ambiente.suia.domain.UnidadArea;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoImpedidoEnum;
import ec.gob.ambiente.suia.domain.enums.TipoMensajeMailEnum;
import ec.gob.ambiente.suia.dto.EntityUsuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.persona.facade.PersonaFacade;
import ec.gob.ambiente.suia.reportes.UtilDocumento;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;
import ec.gob.registrocivil.consultacedula.Cedula;
import ec.gov.sri.wsconsultacontribuyente.ContribuyenteCompleto;

/**
 * @author ishmael
 */
@ManagedBean
@ViewScoped
public class UsuarioController implements Serializable {

	/**
     *
     */
	private static final long serialVersionUID = -6183428861330881548L;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private RolFacade rolFacade;
	@EJB
	private ImpedidosFacade impedidosFacade;
	@EJB
	private AreaFacade areaFacade;
	@EJB
	private ContactoFacade contactoFacade;
	@EJB
	private OrganizacionFacade organizacionFacade;
	@EJB
	private EntidadRolFacade entidadRolFacade;
	@EJB
	private UnidadFacade unidadFacade;
	@EJB
	private AreaUsuarioFacade areaUsuarioFacade;
	@EJB
	private UnidadAreaFacade unidadAreaFacade;
	@EJB
	private UnidadAreaUsuarioFacade unidadAreaUsuarioFacade;
	@EJB
	private PersonaFacade personaFacade;
	@EJB
	private AreasSnapProvinciaFacade areasSnapProvinciaFacade;
	@EJB
	private TipoUsuarioFacade tipoUsuarioFacade;

	@Getter
	@Setter
	private UsuarioBean usuarioBean;
	@Getter
	@Setter
	private PromotorBean promotorBean;
	private final ConsultaRucCedula consultaRucCedula = new ConsultaRucCedula(Constantes.getUrlWsRegistroCivilSri());
	@Getter
	@Setter
	@ManagedProperty(value = "#{ubicacionGeograficaController}")
	private UbicacionGeograficaController ubicacionGeograficaController;
	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	
	@Getter
	@Setter
	@ManagedProperty(value = "#{bandejaTareasBean}")
	private BandejaTareasBean bandejaTareasBean;
	
	@Setter
	@Getter
	private UploadedFile file;
	private Documento documentofirma;
	
	 private static final String SOLO_NUMEROS = "return numbersonly(this, event);";
	    private static final String TAMANIO_250 = "250";
	    private static final String TAMANIO_50 = "50";
	    private static final String TAMANIO_15 = "15";
	    private static final String TAMANIO_10 = "10";
	    private static final String TAMANIO_13 = "13";
	    private static final String NADA = "";
	    
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UsuarioController.class);
	private static final String PERSONA_NATURAL = "N";
	private static final String PERSONA_JURIDICA = "J";
	private static final String GENERO_MASCULINO = "HOMBRE";
	private static final String ID_EMPRESA_MIXTA = "8";
	private static final String SIN_SERVICIOS = "Sin servicio";
	private static final int TAMANIO_ARCHIVO = 1024;
	@Getter
	private Boolean admin;
	@Getter
	private Boolean adminInstitucional;
	@Getter
	@Setter
	private List<Documents> resultFirmas= new ArrayList<Documents>();
	@Getter
	@Setter
	private Documents documents= new Documents();

	@Getter
	@Setter
	private String nuevaFirma =null;;
	@Getter
    private UploadedFile uploadedFile;
//	private byte[] transformedFile;
//	private String nombreDocumento="";
	@Getter
    @Setter
    private String nombreArchivo="Seleccione documento";
	@Getter
    @Setter
	boolean verFirmas;
	
	@Getter
    @Setter
	boolean habilitarDescargaFirmas;
	
	@Getter
	private List<String> areasProtegidas;
	
	@Setter
	@Getter
	private List<String> areasProtegidasSeleccionadas;
	
	@Getter
	@Setter
	private boolean mostrarTodasAreas;
	
	@Getter
	@Setter
	private boolean mostrarTodo = false;
	
	@Getter
	@Setter
	private boolean esAutoridad = false;
	
	@Getter
	@Setter
	private boolean mostrarAreasProtegidas = false;
	
	@Getter
	@Setter
	private boolean adminAreas;
	
	@Getter
	@Setter
	private boolean deshabilitarCamposAP = false;
	
	@Getter
	@Setter
	private Boolean encargado, subrogante;
	
	@Getter
	@Setter
	private TipoUsuario tipoUsuario;

		
	/**
     *
     */
	@PostConstruct
	public void inicio() {
		usuarioBean = new UsuarioBean();
		usuarioBean.setVerAsignarRol(false);
		usuarioBean.setVerDatosUsuario(false);
		usuarioBean.setVerListaUsuario(true);
		usuarioBean.setUsuario(new Usuario());
		cargarUsuarios();
		cargarRoles();		
		promotorBean = new PromotorBean();
		promotorBean.setContacto(new Contacto());
		promotorBean.setAreasGuardar(new ArrayList<Area>());
		ubicacionGeograficaController.inicio();
		admin = Usuario.isUserInRole(loginBean.getUsuario(), "admin");
		adminInstitucional=Usuario.isUserInRole(loginBean.getUsuario(), "ADMINISTRADOR INSTITUCIONAL");
		adminAreas=Usuario.isUserInRole(loginBean.getUsuario(), "ADMINISTRADOR AREAS");
		cargarUsuarios();
		cargarRoles();
	}		
	
	private void cargarAreasProtegidas() {
		if(areasProtegidas==null)
		{
			areasProtegidas=new ArrayList<String>();			
			List<String>areasTemp=areaFacade.getAreasProtegidas();
			for (String area : areasTemp) {
				Integer uAsignado=areaFacade.getRolDetalleAsignado(area,AreaFacade.JEFE_AREA);
				if(uAsignado==null || usuarioBean.getUsuario().getId()== null ||(usuarioBean.getUsuario().getId()!= null && usuarioBean.getUsuario().getId().intValue()==uAsignado.intValue()))
					areasProtegidas.add(area);
				//else 
					//System.out.println(area);
			}
		}		
		
	}
	
	public boolean isJefeArea(){
		List<Rol> listaRoles=usuarioBean.getListaDualRoles().getTarget() != null ? usuarioBean.getListaDualRoles().getTarget():new ArrayList<Rol>();
		for (Rol rol : listaRoles) {
			if(rol.getDescripcion().compareTo(AreaFacade.JEFE_AREA)==0)
				return true;
		}
		return false;
	}
	
	private void cargarUsuarios() {
		
		try {
			usuarioBean.setListaEntityUsuario(new ArrayList<EntityUsuario>());
			List<EntityUsuario> listaAux;
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if(rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst = 3;
					break;
				}else{
					adminInst=0;
				}				
			}
			if (adminInst==2){				
				listaAux = areaUsuarioFacade.listarUsuarioEntityAdministradorInstitucional(loginBean.getUsuario());		
				
			}else if(adminInst==3){
				deshabilitarCamposAP = true;
				List<EntityUsuario> listaAdmin = new ArrayList<EntityUsuario>();
				List<EntityUsuario> listaResponsableAP = new ArrayList<EntityUsuario>();
				listaAux = new ArrayList<EntityUsuario>();
				
				listaAdmin = areaUsuarioFacade.listarUsuarioEntityJefesArea("ADMINISTRADOR ÁREA PROTEGIDA");
				listaResponsableAP = areaUsuarioFacade.listarUsuarioEntityJefesArea("RESPONSABLE DE ÁREAS PROTEGIDAS");
								
				List<EntityUsuario> listaResponsableSnap = new ArrayList<EntityUsuario>();
				List<EntityUsuario> listaResponsableSnapAux = new ArrayList<EntityUsuario>();
				
				listaResponsableSnap = areaUsuarioFacade.listaUsuariosSnap();
				listaResponsableSnapAux.addAll(listaResponsableSnap);
				
				listaAux.addAll(listaResponsableAP);
				listaAux.addAll(listaAdmin);				
				
				for(EntityUsuario us : listaResponsableSnap){
					
					for(EntityUsuario usa : listaAux){
						if(us.getId().equals(usa.getId())){
							listaResponsableSnapAux.remove(us);
						}						
					}					
				}
				
				listaAux.addAll(listaResponsableSnapAux);				
				
				for(EntityUsuario usu : listaAux){
					Usuario usuario = usuarioFacade.buscarUsuarioPorId(Integer.valueOf(usu.getId()));
					List<Rol> roles = rolFacade.listarPorUsuario(usuario);
					
					String rolString = "";
					for(Rol rol : roles){
						if(rolString.isEmpty()){
							rolString = rol.getNombre();
						}else{
							rolString = rolString + ", " + rol.getNombre(); 
						}
					}
					usu.setRoles(rolString);
				}
				
			}else{
				listaAux = usuarioFacade.listarUsuarioEntityListarAdmin();
			}
			
			try {
				
				EntityUsuario uAux = new EntityUsuario();
				int tamaniLista = listaAux.size();
				if (listaAux != null && !listaAux.isEmpty()) {
					int i = 0;
					for (EntityUsuario uI : listaAux) {
						i++;
						if (uAux.getId() == null) {
							uAux = comparaUsuario(uAux, uI, i, tamaniLista);
							continue;
						}
						uAux = comparaUsuario(uAux, uI, i, tamaniLista);
					}
				}

			} catch (Exception e) {
				LOG.error(e, e);
			}
			
			
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
	}
	
	public Boolean validarUsuarioIngresado() throws ServiceException {
		boolean resultado=false;
		List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());

		for (Rol rol : rolesusuario) {
			if (rol.getDescripcion().contains("admin")){
				resultado=false;
				break;
			}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
				resultado=true;
				break;
			}else if(rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
				resultado = true;
			}else{
				resultado=false;
			}
		}
		return resultado;
		
	}

	private EntityUsuario comparaUsuario(EntityUsuario uAux, EntityUsuario uI, int i, int tamanioLista) {
		if (uAux.getId() != null && uAux.getId().equals(uI.getId())) {
			uAux.setRoles(uAux.getRoles() + "," + uI.getRoles());
			if (tamanioLista == i) {
				usuarioBean.getListaEntityUsuario().add(uAux);
			}
			return uAux;
		}
		if (uAux.getId() != null) {
			usuarioBean.getListaEntityUsuario().add(uAux);
		}
		if (tamanioLista == i) {
			usuarioBean.getListaEntityUsuario().add(uI);
		}
		return uI;
	}

	private void cargarUsuario(Integer idUsuario) {
		try {
			usuarioBean.setUsuario(usuarioFacade.buscarUsuarioPorId(idUsuario));
			promotorBean.setUsuario(usuarioBean.getUsuario());
            //Si no es promotor se activan las opciones para jefe de área y jefe inmediato
			
			List<AreaUsuario> listaAreas = areaUsuarioFacade.buscarAreaUsuarioAbreviacion(usuarioBean.getUsuario(), "PTE");
			
			if(listaAreas!= null && !listaAreas.isEmpty()){
				 usuarioBean.setMostrarOpcionesJefe(false);
			}else{
                usuarioBean.setMostrarOpcionesJefe(true);
			}			
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	/**
     *
     */
	public void nuevo() {
		usuarioBean.setVerDatosUsuario(true);
		usuarioBean.setVerListaUsuario(false);
//		cambioTipoPersona();
//		inicializar--------
		promotorBean.setUsuario(new Usuario());
		promotorBean.setPersona(new Persona());
		promotorBean.setOrganizacion(new Organizacion());
		promotorBean.setContacto(new Contacto());
		promotorBean.setListaContacto(new ArrayList<Contacto>());
		promotorBean.setAreasGuardar(new ArrayList<Area>());
		promotorBean.setAreasProtegidasGuardar(new ArrayList<AreasSnapProvincia>()); 
//		--------------------
		
		promotorBean.setSelectedNode(null);
		promotorBean.setTipoPersona(PERSONA_NATURAL);
		promotorBean.setRoot(new DefaultTreeNode());
		promotorBean.setRootOt(new DefaultTreeNode());
		promotorBean.setRootAP(new DefaultTreeNode());
		try {
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst=3;
					break;
				}else{
					adminInst=0;
				}
				
			}
			if (adminInst==2){	
				
				List<Area> listaAreasUsuario = areaUsuarioFacade.buscarAreas(loginBean.getUsuario());
				
				Area areaPadre = new Area();
				
				if(listaAreasUsuario != null && !listaAreasUsuario.isEmpty()){
					Area areaUsuario = listaAreasUsuario.get(0);
					
					if(areaUsuario.getArea() == null){
						areaPadre = areaUsuario;
					}else{
						areaPadre = areaUsuario.getArea();
					}
				}				
				
				if(areaPadre == null || areaPadre.getId() == null){
					JsfUtil.addMessageError("El usuario no tiene asignada un área.");
					return;					
				}else if(areaPadre.getTipoArea().getSiglas().equals("OT") || areaPadre.getTipoArea().getSiglas().toUpperCase().equals("ZONALES")){					
					
					List<Area> areas= areaFacade.listarAreasPadreInstitucionalTotal(areaPadre);
					cargarArbolAreasInstitucionalOT(areas, null);

					mostrarTodo = false;
					mostrarTodasAreas = true;
				}else{
					List<Area> areas= areaFacade.listarAreasPadreInstitucionalTotal(areaPadre);
					cargarArbolAreasInstitucional(areas, null);
					mostrarTodo = false;
					mostrarTodasAreas = false;
				}
			}else if (adminInst==3){	
							
				mostrarTodasAreas = false;
				mostrarTodo = false;
				mostrarAreasProtegidas = true;
				
				List<AreasSnapProvincia>areasP=areaFacade.obtenerAreasProtegidasSnap();
				cargarArbolAreasProtegidas(areasP, null);
				
				Area areaPadre = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
				List<Area> listaA = new ArrayList<>();
				listaA.add(areaPadre);
				cargarArbolAreasPr(listaA, null);
				
			}else{
				cargarArbolAreasOficinasTecnicas(null, null);
				cargarArbolAreas(null, null);
				mostrarTodo = true;
				mostrarTodasAreas = false;
			}
			
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}		
	}

	/**
     *
     */
	public void cancelar() {
		setVerFirmas(false);
		usuarioBean.setVerDatosUsuario(false);
		usuarioBean.setVerListaUsuario(true);
		usuarioBean.setVerAsignarRol(false);
		cargarUsuarios();
		cargarRoles();
	}

	/**
	 * @param usuario
	 */
	public void seleccionarUsuario(EntityUsuario usuario) {
		cargarUsuario(Integer.valueOf(usuario.getId()));
		usuarioBean.setVerDatosUsuario(true);
		usuarioBean.setVerListaUsuario(false);

		promotorBean.setSelectedNode(null);
		promotorBean.setRoot(new DefaultTreeNode());
		promotorBean.setRootOt(new DefaultTreeNode());
		promotorBean.setRootAP(new DefaultTreeNode());
		promotorBean.setAreasGuardar(new ArrayList<Area>());
		cargarDatosUsuario();
		esAutoridad = false;
		try {
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst=3;
					break;
				}else{
					adminInst=0;
				}				
			}
			
			if (adminInst==2){	
				promotorBean.setDeshabilitarRegistro(true);
				
				List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(usuarioBean.getUsuario());

				String tipoArea = "";
				
				for(AreaUsuario au: listaAreasUsuario){
	        		tipoArea = au.getArea().getTipoArea().getSiglas();
	        		break;				        		
	        	}				
				
				if(tipoArea.toUpperCase().equals("OT")){
					List<Area> areas= new ArrayList<>();
					
					for(AreaUsuario au: listaAreasUsuario){
						List<Area> areasAux= areaFacade.listarAreasPadreInstitucionalTotal(au.getArea().getArea());
						for(Area area:areasAux){
							if(!areas.contains(area)){
								if(au.getUnidadArea() != null){
									area.setIdUnidadArea(au.getUnidadArea().getId());
								}								
								areas.add(area);
							}
						}
					}
					
					cargarArbolAreasInstitucionalOT(areas, null);					
				
					mostrarTodasAreas = true;
				}else if(tipoArea.toUpperCase().equals("ZONALES")){
					List<Area> areas= new ArrayList<>();
					
					for(AreaUsuario au: listaAreasUsuario){
						List<Area> areasAux= areaFacade.listarAreasPadreInstitucionalTotal(au.getArea());
						for(Area area:areasAux){
							if(!areas.contains(area)){
								if(au.getUnidadArea() != null){
									area.setIdUnidadArea(au.getUnidadArea().getId());
								}	
								areas.add(area);
							}
						}
					}
					
					cargarArbolAreasInstitucionalOT(areas, null);	
					mostrarTodasAreas = true;
				}else{	
					List<Area> areas= new ArrayList<>();
					for(AreaUsuario au: listaAreasUsuario){
						List<Area> areasAux= areaFacade.listarAreasPadreInstitucionalTotal(au.getArea());
						for(Area area:areasAux){
							if(!areas.contains(area)){
								if(au.getUnidadArea() != null){
									area.setIdUnidadArea(au.getUnidadArea().getId());
								}	
								areas.add(area);
							}
						}
					}
					//List<Area> areas= areaFacade.listarAreasPadreInstitucionalTotal(usuarioBean.getUsuario().getArea());
					cargarArbolAreasInstitucional(areas, null);
					///////////////////
					if(tipoArea.toUpperCase().equals("EA")){
						mostrarTodasAreas = false;
					}else{
						mostrarTodasAreas = true;
					}				
				}				
			}else if(adminInst == 1){
				
				List<Rol> rolesUsuario = rolFacade.listarPorUsuario(promotorBean.getUsuario());
				esAutoridad = false;
				for(Rol rol : rolesUsuario){
					if(rol.getNombre().equals("AUTORIDAD AMBIENTAL")){
						esAutoridad = true;
						break;
					}
				}
				
				if(esAutoridad){
					List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(usuarioBean.getUsuario());					
					String tipoArea = "";
					Area areaGalapagos = new Area();
					for(AreaUsuario au: listaAreasUsuario){
						areaGalapagos = au.getArea();
		        		tipoArea = au.getArea().getTipoArea().getSiglas();		        		
		        		break;				        		
		        	}	
					
					if(tipoArea.toUpperCase().equals("ZONALES")){
						List<Area> areas = areaFacade.listarDireccionesZonales();
						cargarArbolAreasDireccionesZonales(areas, null);
						esAutoridad = true;
					}else if(tipoArea.toUpperCase().equals("DP") && areaGalapagos.getId().equals(272)){
						List<Area> areas = areaFacade.listarDireccionesZonales();
						cargarArbolAreasDireccionesZonales(areas, null);
						esAutoridad = true;
					}
					else{
						cargarArbolAreasOficinasTecnicas(null, null);
						esAutoridad = false;
					}
				}else{
					cargarArbolAreasOficinasTecnicas(null, null);
				}
				
				cargarArbolAreas(null, null);
				mostrarTodasAreas = true;
				mostrarTodo = true;	
				
			}else if(adminInst == 3){
				promotorBean.setDeshabilitarRegistro(true);
			
				mostrarTodasAreas = false;
				mostrarTodo = false;
				mostrarAreasProtegidas = true;
				List<AreasSnapProvincia>areasP=areaFacade.obtenerAreasProtegidasSnap();
				cargarArbolAreasProtegidas(areasP, null);
				
				Area areaPadre = areaFacade.getAreaPorAreaAbreviacion(Constantes.SIGLAS_DIRECCION_AREAS_PROTEGIDAS);
				List<Area> listaA = new ArrayList<>();
				listaA.add(areaPadre);
				cargarArbolAreasPr(listaA, null);
				
			}else{
				cargarArbolAreasOficinasTecnicas(null, null);
				cargarArbolAreas(null, null);
				mostrarTodasAreas = true;
				mostrarTodo = true;
			}
			
			promotorBean.setDeshabilitarRegistro(false);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	private void cargarDatosUsuario() {
		try {
			promotorBean.setPersona(promotorBean.getUsuario().getPersona());
			identificarTipoPersona();
			if(promotorBean.getOrganizacion()!=null){
			promotorBean.setListaContacto(contactoFacade.buscarPorOrganizacion(promotorBean.getOrganizacion()));
			}else {
				promotorBean.setListaContacto(contactoFacade.buscarPorPersona(promotorBean.getPersona()));
			}
			List<AreaUsuario> listaAreaU = areaUsuarioFacade.buscarAreaUsuario(promotorBean.getUsuario());
			
			promotorBean.setAreasGuardar(new ArrayList<Area>());
			for(AreaUsuario au : listaAreaU){		
				
				List<UnidadArea> listaUnidadesAreas = unidadAreaUsuarioFacade.buscarUnidadAreaporUsuario(au);
				
				if(listaUnidadesAreas != null && !listaUnidadesAreas.isEmpty()){
					au.getArea().setListaUnidadesGuardadas(listaUnidadesAreas);
				}				
				promotorBean.getAreasGuardar().add(au.getArea());								
			}
			
			List<AreasSnapProvincia> listaSnap = areasSnapProvinciaFacade.consultarAreasProtegidasPorUsuario(promotorBean.getUsuario());
			
			promotorBean.setAreasProtegidasGuardar(new ArrayList<AreasSnapProvincia>());
			promotorBean.getAreasProtegidasGuardar().addAll(listaSnap);				
			
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void identificarTipoPersona() throws ServiceException {
		promotorBean.setOrganizacion(organizacionFacade.buscarPorPersona(promotorBean.getPersona(), promotorBean
				.getUsuario().getNombre()));
		if (promotorBean.getOrganizacion() != null
				&& promotorBean.getOrganizacion().getRuc().trim().equals(promotorBean.getUsuario().getNombre())) {
			promotorBean.setTipoPersona(PERSONA_JURIDICA);
			if(promotorBean.getOrganizacion().getIdUbicacionGeografica() != null){
				promotorBean.setIdParroquia(promotorBean.getOrganizacion().getIdUbicacionGeografica().toString());
				recuperarUbicacionGeografica();
			}
			promotorBean.setIdTipoOrganizacion(promotorBean.getOrganizacion().getIdTipoOrganizacion().toString());
		} else {
			promotorBean.setTipoPersona(PERSONA_NATURAL);
			promotorBean.setIdParroquia(promotorBean.getPersona().getIdUbicacionGeografica().toString());
			promotorBean.setIdNacionalidad(promotorBean.getPersona().getIdNacionalidad().toString());
			promotorBean.setIdTipoTrato(promotorBean.getPersona().getIdTipoTratos().toString());
			recuperarUbicacionGeografica();
		}
	}

	private void recuperarUbicacionGeografica() {
		ubicacionGeograficaController.getUbicacionGeograficaBean().setIdParroquia(promotorBean.getIdParroquia());
		ubicacionGeograficaController.cargarParametros();
	}

	/**
	 * @param usuario
	 */
	//-----------------------------
	@EJB(lookup = Constantes.ALFRESCO_SERVICE_BEAN)
    private AlfrescoServiceBean alfrescoServiceBean;
	
	//------------------------------
	public void seleccionarUsuarioAsignar(EntityUsuario usuario) {
		cargarUsuario(Integer.valueOf(usuario.getId()));
		usuarioBean.setVerAsignarRol(true);
		usuarioBean.setVerListaUsuario(false);
		setHabilitarDescargaFirmas(true);
		cargarRolesAsignar();
		cargarAreasProtegidas();
		cargarAreas();
		cargarTipoUsuario();
		try {						
			
//			if (tipoAreaEA() && !(alfrescoServiceBean.archivosFirmas("logo__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId())==null)){
//				setVerFirmas(true);
//				setHabilitarDescargaFirmas(true);
//				resultFirmas= new ArrayList<Documents>();	
//				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("logo__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
//				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("firma__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
//				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("pie__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
//			}else{
//				setVerFirmas(true);
//				resultFirmas= new ArrayList<Documents>();	
//				if (!(alfrescoServiceBean.archivosFirmas("firma__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasIdMAE())==null)){
//					resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("firma__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasIdMAE()));
//				}else{
//					setHabilitarDescargaFirmas(false);
//					documents.setName("firma__"+usuarioBean.getUsuario().getArea().getAreaAbbreviation().toString()+".png");
//					resultFirmas.add(documents);
//				}		
//			}				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void cargarAreas(){
		
		try {
			usuarioBean.setAreasResponsables("");
			List<AreaUsuario> lista = areaUsuarioFacade.buscarAreaUsuario(usuarioBean.getUsuario());
			
			List<Area> listaNombres = new ArrayList<Area>();
			if(lista != null && !lista.isEmpty()){
				
				String areas =	"";
				for(AreaUsuario au : lista){
					
					Comparator<Area> c = new Comparator<Area>() {
						
						@Override
						public int compare(Area o1, Area o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaNombres, c);
					int index = Collections.binarySearch(listaNombres, new Area(au.getArea().getId()), c);
					
					if(index >= 0){
						
					}else{
						listaNombres.add(au.getArea());
					}														
				}
				
				if(listaNombres != null && !listaNombres.isEmpty()){
					
					for(Area au :listaNombres){
						areas+=au.getAreaName() + ", ";
					}					
				}
				
				areas = areas.substring(0, areas.length()-2);
				usuarioBean.setAreasResponsables(areas);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void cargarRoles() {
		try {
			usuarioBean.setListaRol(rolFacade.listarRolesSinAdmin());
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}
	
	private void cargarTipoUsuario(){
		List<TipoUsuario> listaTipo = new ArrayList<TipoUsuario>();
		
		listaTipo = tipoUsuarioFacade.obtenerListaTipoUsuario(usuarioBean.getUsuario());
		
		if(listaTipo != null && !listaTipo.isEmpty()){
			tipoUsuario = listaTipo.get(0);
			
			if(tipoUsuario.getTipo().equals(2)){
				encargado = true;
			}
		}
	}

	private void cargarRolesAsignar() {
		try {
			//AKI COLOCAR LA LOGICA
			usuarioBean.setListaEntityUsuario(new ArrayList<EntityUsuario>());

			List<Rol> rolesusuario = rolFacade.listarPorUsuario(loginBean.getUsuario());
			Integer adminInst=0;
			for (Rol rol : rolesusuario) {
				if (rol.getDescripcion().contains("admin")){
					adminInst=1;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR INSTITUCIONAL")){
					adminInst=2;
					break;
				}else if (rol.getDescripcion().contains("ADMINISTRADOR AREAS")){
					adminInst=3;
					break;
				}else{
					adminInst=0;
				}
				
			}
			if (adminInst==1){
				List<Rol> rolesUsuario = rolFacade.listarPorUsuario(usuarioBean.getUsuario());
				if (rolesUsuario == null || rolesUsuario.isEmpty()) {
					usuarioBean.setListaDualRoles(new DualListModel<Rol>(usuarioBean.getListaRol(), new ArrayList<Rol>()));
				} else {
					List<Rol> listaRolAux = new ArrayList<Rol>();
					for (Rol rol : usuarioBean.getListaRol()) {
						if (!rolesUsuario.contains(rol)) {
							listaRolAux.add(rol);
						}
					}
					usuarioBean.setListaDualRoles(new DualListModel<Rol>(listaRolAux, rolesUsuario));
					for (Rol r : rolesUsuario) {
						if(r.getNombre().compareTo(AreaFacade.JEFE_AREA)==0)
						{							
							areasProtegidasSeleccionadas=new ArrayList<String>();
							String areasP=areaFacade.getRolDetalle(usuarioBean.getUsuario(), AreaFacade.JEFE_AREA);							
							if(areasP!=null)
								areasProtegidasSeleccionadas.addAll(Arrays.asList(areasP.split(";")));
						}
					}
				}
			}else if (adminInst==2){
				
				List<Rol> rolesUsuario = rolFacade.listarPorUsuario(usuarioBean.getUsuario());
				
				List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(loginBean.getUsuario());
				String tipoArea = "";
				
				for(AreaUsuario areaU : listaAreasUsuario){
					if(areaU.getArea().getTipoEnteAcreditado() != null && areaU.getArea().getTipoEnteAcreditado().equals("ZONAL")){
						tipoArea = "ZONAL";
						break;
					}
					
					if(areaU.getArea().getTipoEnteAcreditado() != null && areaU.getArea().getTipoEnteAcreditado().equals("GOBIERNO")){
						tipoArea = "GOBIERNO";
						break;
					}
					
					if(areaU.getArea().getTipoEnteAcreditado() != null && areaU.getArea().getTipoEnteAcreditado().equals("MUNICIPIO")){
						tipoArea = "MUNICIPIO";
						break;
					}
					
					if(areaU.getArea().getTipoArea().getSiglas().equals("DP")){
						tipoArea = "DIRECCION PROVINCIAL";
						break;
					}
				}
				
				
				List<Rol> listaRolAux = new ArrayList<Rol>();
				if(tipoArea.equals("ZONAL") || tipoArea.equals("DIRECCION PROVINCIAL")){				
					
					List<EntidadRol> listaEntidadRol = entidadRolFacade.buscarPorRolZonal();
					
					for(EntidadRol entRol : listaEntidadRol){
						if (!rolesUsuario.contains(entRol.getRol())) {
							listaRolAux.add(entRol.getRol());
						}
					}
					
					usuarioBean.setListaDualRoles(new DualListModel<Rol>(listaRolAux, rolesUsuario));
								
				}else if(tipoArea.equals("GOBIERNO")){
					List<EntidadRol> listaEntidadRol = entidadRolFacade.buscarPorRolProvincial();
					
					for(EntidadRol entRol : listaEntidadRol){
						if (!rolesUsuario.contains(entRol.getRol())) {
							listaRolAux.add(entRol.getRol());
						}
					}
				}else if(tipoArea.equals("MUNICIPIO")){
					List<EntidadRol> listaEntidadRol = entidadRolFacade.buscarPorRolMunicipio();
					
					for(EntidadRol entRol : listaEntidadRol){
						if (!rolesUsuario.contains(entRol.getRol())) {
							listaRolAux.add(entRol.getRol());
						}
					}
				}
				
				usuarioBean.setListaDualRoles(new DualListModel<Rol>(listaRolAux, rolesUsuario));
			
			
			}else if (adminInst==3){
				
				List<Rol> rolesUsuario = rolFacade.listarPorUsuario(usuarioBean.getUsuario());
				List<Rol> listaRolesSnap = rolFacade.listarRolesSNAP();
				List<Rol> listaRolAux = new ArrayList<Rol>();
				
				for(Rol rol : listaRolesSnap){
					if(!rolesUsuario.contains(rol)){
						listaRolAux.add(rol);
					}
				}
								
				usuarioBean.setListaDualRoles(new DualListModel<Rol>(listaRolAux, rolesUsuario));
			
			}else{
				
				List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(loginBean.getUsuario());
				
				Integer tipoDp = null;
				for(AreaUsuario au: listaAreasUsuario){
					tipoDp = au.getArea().getTipoArea().getId();
					break;
				}
				
				List<Rol> rolesUsuario = rolFacade.listarPorUsuario(usuarioBean.getUsuario());
				
				String tipoDP;
				if (tipoDp==1){
					tipoDP="PC";
				}else if (tipoDp==2){
					tipoDP="DP";
				}else{
					tipoDP="ENTE";
				}
				
				List<Rol> listaRolAux = new ArrayList<Rol>();
				for (Rol rol : usuarioBean.getListaRol()) {
					if (!rolesUsuario.contains(rol)) {
						if ((rol.getAutoridadUso()!=null && rol.getUsoSistemas()!=null)){							
						if (rol.getAutoridadUso().contains(tipoDP) && rol.getUsoSistemas().contains("SUIA-III")){
							listaRolAux.add(rol);
						}
					}
					}
				}
				usuarioBean.setListaDualRoles(new DualListModel<Rol>(listaRolAux, rolesUsuario));
			
			}
		} catch (Exception e) {
			LOG.error(e, e);
		}
	}

	private void asignarRoles() throws ServiceException{
		boolean validaUnico=false;
		AuditarUsuario auditarUsuario= new AuditarUsuario();
    	auditarUsuario.setDescripcion("asignacion de roles y estado");
    	auditarUsuario.setEstado(true);
    	auditarUsuario.setFechaActualizacion(new Date());
    	List<String> mensajes = new ArrayList<>();
    	List<Rol> rolUsuario=rolFacade.listarPorUsuario(promotorBean.getUsuario());
    	String listadoRoles="";
    	if (rolUsuario.size()>0){
    		for (Rol rol : rolUsuario) {
    			listadoRoles+="-"+rol.getNombre();
			}    		
    	}    	
    	
    	List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(promotorBean.getUsuario());
    	if (listaAreasUsuario == null || listaAreasUsuario.isEmpty()){
    		auditarUsuario.setPasswordAnterior("estado:"+promotorBean.getUsuario().getEstado() +" funcionario:"+promotorBean.getUsuario().getFuncionario()+ "planta central:"+promotorBean.getUsuario().getPlantaCentral()+ "subrogante:"+promotorBean.getUsuario().getSubrogante()+"area:Proponente Roles: "+listadoRoles);  
    	}else{
    		String areas = areaUsuarioFacade.buscarAreaUsuarioNombres(promotorBean.getUsuario());
    		auditarUsuario.setPasswordAnterior("estado:"+promotorBean.getUsuario().getEstado() +" funcionario:"+promotorBean.getUsuario().getFuncionario()+ "planta central:"+promotorBean.getUsuario().getPlantaCentral()+ "subrogante:"+promotorBean.getUsuario().getSubrogante()+"area:"+areas+ "Roles: "+listadoRoles);
    	}
    	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
    	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
    	
    	/**
    	 * Se valida que se tenga el rol unico en la dirección zonal
    	 */
    	boolean esUnicoDz = false;
    	if (usuarioBean.getListaDualRoles().getTarget() != null) {    			
			
			for (Rol rol : usuarioBean.getListaDualRoles().getTarget()) {
				if (rol.getUnicoPorDireccionZonal() != null && rol.getUnicoPorDireccionZonal()) {
					esUnicoDz = true;
					for(AreaUsuario au : listaAreasUsuario){
						List<Usuario> listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea(rol.getNombre(), au.getArea());
						if(!listaUsuario.isEmpty()){
							for(Usuario u : listaUsuario){
								if(!u.getId().equals(promotorBean.getUsuario().getId())){
									mensajes.add("Ya existe un usuario con el rol " + rol.getNombre() + " para la " + au.getArea().getAreaName());
									break;
								}
							}							
						}
					}					
				}
			}
		}    
    	
    	if(!mensajes.isEmpty()){
    		JsfUtil.addMessageError(mensajes);
    		return;
    	}
    	
    	for(AreaUsuario au: listaAreasUsuario){
    		if(au.getArea().getHabilitarArea() == null){
    			au.getArea().setHabilitarArea(false);
    		}
    	}
    
        //Se verifica si es null esta opcion por ser proponente
        if(usuarioBean.getUsuario().getEsResponsableArea() == null)
            usuarioBean.getUsuario().setEsResponsableArea(false);

        //Se verifica que en caso de seleccionar al usuario como responsable de área, este sea el únicoesUnicoDz
        if(usuarioBean.getUsuario().getEsResponsableArea()) {
        	List<Usuario> usuariosRes = new ArrayList<>();

        	if(!esUnicoDz){
        		usuariosRes = areaUsuarioFacade.buscarUsuarioResponsableArea(usuarioBean.getUsuario());
	        	
            	if (usuariosRes.size()>1){
            		JsfUtil.addMessageError(JsfUtil.ERROR_RESPONSABLE_AREA_DUPLICADO + areaUsuarioFacade.buscarAreaUsuarioNombresSql(usuarioBean.getUsuario()));
                    return;
            	}else{
    				if (!(usuariosRes.size() == 0)) {
    					if (!usuariosRes.get(0).getNombre().equals(usuarioBean.getUsuario().getNombre())) {
    						if (!usuarioFacade.esUsuarioUnicoResponsableArea(usuarioBean.getUsuario())) {
    							JsfUtil.addMessageError(JsfUtil.ERROR_RESPONSABLE_AREA_DUPLICADO + areaUsuarioFacade.buscarAreaUsuarioNombresSql(usuarioBean.getUsuario()));
    							return;
    						}
    					}
    				}
            	}
        	}        	
        }
        
        if(encargado){
        	if(tipoUsuario != null && tipoUsuario.getId() != null){
        		if(tipoUsuario.getTipo().equals(1)){ //tiene almacenado un subrogante se debe quitar el subrogante primero e ingresar un nuevo registro para encargado
        			tipoUsuario.setFechaFin(new Date());
        			tipoUsuario.setEstado(false);
        			
        			tipoUsuarioFacade.guardar(tipoUsuario);
        			
        			TipoUsuario tipoUsuarioEn = new TipoUsuario();
        			tipoUsuarioEn.setFechaInicio(new Date());
        			tipoUsuarioEn.setTipo(2); // 2 de encargado
        			tipoUsuarioEn.setUsuario(usuarioBean.getUsuario());
        			
        			tipoUsuarioFacade.guardar(tipoUsuarioEn);
        			
        			tipoUsuario = new TipoUsuario();
        			setTipoUsuario(tipoUsuarioEn);
        		}
        	}else{
        		tipoUsuario = new TipoUsuario();
        		tipoUsuario.setFechaInicio(new Date());
        		tipoUsuario.setTipo(2); // 2 de encargado
        		tipoUsuario.setUsuario(usuarioBean.getUsuario());
    			
    			tipoUsuarioFacade.guardar(tipoUsuario);
        	}        	
        }else if(usuarioBean.getUsuario().getSubrogante() != null && usuarioBean.getUsuario().getSubrogante() == true){
        	if(tipoUsuario != null && tipoUsuario.getId() != null){
        		if(tipoUsuario.getTipo().equals(2)){ /**tiene almacenado un encargado se debe quitar el encargado primero e ingresar un nuevo registro para subrogante*/
        			tipoUsuario.setFechaFin(new Date());
        			tipoUsuario.setEstado(false);
        			
        			tipoUsuarioFacade.guardar(tipoUsuario);
        			
        			TipoUsuario tipoUsuarioEn = new TipoUsuario();
        			tipoUsuarioEn.setFechaInicio(new Date());
        			tipoUsuarioEn.setTipo(1); // 2 de subrogante
        			tipoUsuarioEn.setUsuario(usuarioBean.getUsuario());
        			
        			tipoUsuarioFacade.guardar(tipoUsuarioEn);
        			
        			tipoUsuario = new TipoUsuario();
        			setTipoUsuario(tipoUsuarioEn);
        		}
        	}else{
        		tipoUsuario = new TipoUsuario();
        		tipoUsuario.setFechaInicio(new Date());
        		tipoUsuario.setTipo(1); // 1 de subrogante
        		tipoUsuario.setUsuario(usuarioBean.getUsuario());
    			
    			tipoUsuarioFacade.guardar(tipoUsuario);
        	}        
        }else{
        	if(tipoUsuario != null && tipoUsuario.getId() != null){
        		tipoUsuario.setFechaFin(new Date());
    			tipoUsuario.setEstado(false);
    			
    			tipoUsuarioFacade.guardar(tipoUsuario);
        	}        	
        }                     

		usuarioFacade.guardar(usuarioBean.getUsuario());
		
		String areasPString="";
		if(areasProtegidasSeleccionadas!=null && !areasProtegidasSeleccionadas.isEmpty())
			for (String area : areasProtegidasSeleccionadas) {
				areasPString+=area+";";
			}
		
		List<Area> listaAreasUsuarioRol = areaUsuarioFacade.buscarAreas(usuarioBean.getUsuario());
		
		if(listaAreasUsuarioRol == null || listaAreasUsuarioRol.isEmpty()){
			JsfUtil.addMessageError("El usuario no tiene área por favor primero asignar el área correspondiente");
			return;
		}

		/**
		 * Validación para impedir que el rol de áreas protegidas quite roles que no el corresponden
		 */
		
		if(adminAreas){
			if(usuarioBean.getListaDualRoles().getSource() != null){
				List<Rol> listaRolesAux = new ArrayList<>();
				List<Rol> listaRolesSel = new ArrayList<>();
				listaRolesAux = rolFacade.listarRolesSNAP();
				listaRolesSel = usuarioBean.getListaDualRoles().getSource();
				
				boolean rolNoAutorizado = false;
				for(Rol r : listaRolesSel){
					if(!listaRolesAux.contains(r)){
						rolNoAutorizado = true;
						break;
					}
				}
				
				if(rolNoAutorizado){
					JsfUtil.addMessageError("No tiene privilegios para eliminar roles que no sean de áreas protegidas.");
					return;	
				}							
			}
		}
		usuarioFacade.guardarAsignacionRolAreas(usuarioBean.getListaDualRoles().getTarget() != null ? usuarioBean
				.getListaDualRoles().getTarget() : null, listaAreasUsuarioRol, areasPString.isEmpty()?null:areasPString, usuarioBean.getUsuario());
		
		cargarUsuarios();
		cancelar();
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
		// ejecuta la migracion de usuarios			
		usuarioFacade.actualizarEstadoUsuarioVerde(promotorBean.getUsuario().getEstado(),promotorBean.getUsuario().getFuncionario(),promotorBean.getUsuario().getPlantaCentral(),promotorBean.getUsuario().getSubrogante(), promotorBean.getUsuario().getNombre());
		
		List<Rol> rolUsuarioActual=rolFacade.listarPorUsuario(promotorBean.getUsuario());
    	String listadoRolesActual="";
    	if (rolUsuarioActual.size()>0){
    		for (Rol rolactual : rolUsuarioActual) {
    			listadoRolesActual+="-"+rolactual.getNombre();				
			} 
    	}
    	
    	if (listaAreasUsuario == null || listaAreasUsuario.isEmpty()){
			auditarUsuario.setPasswordActual("estado:"+promotorBean.getUsuario().getEstado() +" funcionario:"+promotorBean.getUsuario().getFuncionario()+ "planta central:"+promotorBean.getUsuario().getPlantaCentral()+ "subrogante:"+promotorBean.getUsuario().getSubrogante()+"area: Propomente Roles: "+listadoRolesActual);
		}else{
			String areas = areaUsuarioFacade.buscarAreaUsuarioNombres(promotorBean.getUsuario());
			auditarUsuario.setPasswordActual("estado:"+promotorBean.getUsuario().getEstado() +" funcionario:"+promotorBean.getUsuario().getFuncionario()+ "planta central:"+promotorBean.getUsuario().getPlantaCentral()+ "subrogante:"+promotorBean.getUsuario().getSubrogante()+"area:"+areas+ "Roles: "+listadoRolesActual);
		}
		 usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
	}
	
	/**
     *
     */
	public void guardarAsignacionRoles() {
		try {			
			if (promotorBean.getUsuario().getEstado()==true){
				asignarRoles();				
			}else{	
				if (usuarioBean.getListaDualRoles().getTarget().size() == 0){
					
					if (((usuarioFacade.consultaTaskhyd4cat(promotorBean.getUsuario().getNombre())==0) && (usuarioFacade.consultaTaskhyd(promotorBean.getUsuario().getNombre())==0)) && (usuarioFacade.consultaTaskhydSuiaiii(promotorBean.getUsuario().getNombre())==0)){
						asignarRoles();
					}else{
						JsfUtil.addMessageError("No se permite desactivar al usuario ya que tiene tareas activas");
					}
					
				}else{
					JsfUtil.addMessageInfo("No se permite asignar roles a un usuario desactivado");
				}
			}
			
//			usuarioFacade.ejecutarMigracionUsuario();
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(JsfUtil.ERROR_ACTUALIZAR_REGISTRO + e.getMessage());
		}
	}

	/**
     *
     */
	public void cambioTipoPersona() {
		if(promotorBean.getTipoPersona().equals("J"))
		{
			Organizacion org;
			try {
				org = organizacionFacade.buscarPorRuc(usuarioBean.getUsuario().getNombre());
				if(org!=null)
					promotorBean.setOrganizacion(org);
				else
					promotorBean.setOrganizacion(new Organizacion());
					
			} catch (ServiceException e) {
				
			}
		}		
	}
	
	public void validarNumeros() {
        if (getPromotorBean().getIdFormaContacto() != null && !getPromotorBean().getIdFormaContacto().trim().isEmpty()) {
            int  opcion = new Integer(getPromotorBean()
                    .getIdFormaContacto());

            switch (opcion) {
                case FormasContacto.CELULAR:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    getPromotorBean().setMaskValores("(999) 999-999-999");;
                    break;
                case FormasContacto.TELEFONO:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    getPromotorBean().setMaskValores("(999) 999-9999");
                    break;
                case FormasContacto.FAX:
                    getPromotorBean().setScriptNumeros(SOLO_NUMEROS);
                    getPromotorBean().setScriptTamanio(TAMANIO_15);
                    getPromotorBean().setMaskValores("(999) 999-9999");
                    break;
                case FormasContacto.DIRECCION:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    getPromotorBean().setMaskValores("Madrid y Andalucia");
                    break;
                case FormasContacto.EMAIL:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    getPromotorBean().setMaskValores("ambiente@gmail.com");
                    break;
                case FormasContacto.POBOX:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    getPromotorBean().setMaskValores("");
                    break;
                case FormasContacto.URL:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    getPromotorBean().setMaskValores("");
                    break;
                case FormasContacto.POSTFIX_ZIP:
                    getPromotorBean().setScriptNumeros(NADA);
                    getPromotorBean().setScriptTamanio(TAMANIO_250);
                    getPromotorBean().setMaskValores("");
                    break;
                default:
                	getPromotorBean().setMaskValores("");
                    throw new IllegalStateException();
            }
        }
    }
	
	 
	/**
     *
     */
	public void validarCedula() {
		try {
			promotorBean.setDeshabilitarRegistro(false);
			if (promotorBean.getUsuario().getPin() != null && !promotorBean.getUsuario().getPin().isEmpty()) {
				promotorBean.setCausas(impedidosFacade.listarNumeroDocumentoTiposImpedimento(promotorBean.getUsuario()
						.getPin(), TipoImpedidoEnum.CONSULTOR));
				validarUsuarioRegistrado(promotorBean.getUsuario().getPin());
				validarImpedido();
				if (!("Pasaporte").equals(promotorBean.getUsuario().getDocuId())
						&& !promotorBean.isDeshabilitarRegistro()) {
					if (("Cédula").equals(promotorBean.getUsuario().getDocuId())) {
						Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
								Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean.getUsuario().getPin());
						cargarDatosWsCedula(cedula);
					} else {
						ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
								Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean
										.getUsuario().getPin());
						cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
					}
				}
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("Debe ingresar el número de documento");
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}
	
	public void validarCedulaEditar() {
		try {
			promotorBean.setDeshabilitarRegistro(false);
			if (promotorBean.getUsuario().getPin() != null && !promotorBean.getUsuario().getPin().isEmpty()) {
				promotorBean.setCausas(impedidosFacade.listarNumeroDocumentoTiposImpedimento(promotorBean.getUsuario()
						.getPin(), TipoImpedidoEnum.CONSULTOR));
				validarImpedido();
				if (!("Pasaporte").equals(promotorBean.getUsuario().getDocuId())
						&& !promotorBean.isDeshabilitarRegistro()) {
					if (("Cédula").equals(promotorBean.getUsuario().getDocuId())) {
						Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
								Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean.getUsuario().getPin());
						cargarDatosWsCedula(cedula);
					} else {
						ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
								Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean
										.getUsuario().getPin());
						cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
					}

				}
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("Debe ingresar el número de documento");
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}
	
	public void validarCedulaEditarJ() {
		try {
			promotorBean.setDeshabilitarRegistro(false);
			if (promotorBean.getPersona().getPin() != null && !promotorBean.getPersona().getPin().isEmpty()) {
				promotorBean.setCausas(impedidosFacade.listarNumeroDocumentoTiposImpedimento(promotorBean.getPersona()
						.getPin(), TipoImpedidoEnum.CONSULTOR));
				validarImpedido();
				if (!("Pasaporte").equals(promotorBean.getUsuario().getDocuId())
						&& !promotorBean.isDeshabilitarRegistro()) {
					if (promotorBean.getPersona().getPin().length()==10) {
						Cedula cedula = consultaRucCedula.obtenerPorCedulaRC(Constantes.USUARIO_WS_MAE_SRI_RC,
								Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean.getPersona().getPin());
						cargarDatosWsCedula(cedula);
					} else {
						ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
								Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean
										.getPersona().getPin());
						if(contribuyenteCompleto.getRazonSocial()!=null){
						cargarDatosWsRucPersonaNatural(contribuyenteCompleto);
						}else {
							JsfUtil.addMessageError("Cédula / RUC no encontrado, ingrese manualmente.");
						}
					}
				}
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("Debe ingresar el número de documento");
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarDatosWsRucPersonaNatural(ContribuyenteCompleto contribuyenteCompleto) {
		if (contribuyenteCompleto != null) {
			if (contribuyenteCompleto.getRazonSocial() != null) {
				
				Persona persona = new Persona();
				try {
					persona = personaFacade.buscarPersonaPorPin(contribuyenteCompleto.getNumeroRuc());
				} catch (Exception e) {
					persona = null;
				}
				
				if(persona != null){
					promotorBean.setPersona(persona);
					promotorBean.getPersona().setPin(contribuyenteCompleto.getNumeroRuc());
					promotorBean.getPersona().setNombre(contribuyenteCompleto.getRazonSocial());
				}else{
					promotorBean.getPersona().setId(null);
					promotorBean.getPersona().setPin(contribuyenteCompleto.getNumeroRuc());
					promotorBean.getPersona().setNombre(contribuyenteCompleto.getRazonSocial());
				}				
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("RUC no encontrado");
			}
		} else {
			promotorBean.setDeshabilitarRegistro(true);
			JsfUtil.addMessageError(SIN_SERVICIOS);
		}
	}

	private void validarUsuarioRegistrado(final String pin) {
		if (usuarioFacade.buscarUsuario(pin) != null) {
			promotorBean.setDeshabilitarRegistro(true);
			JsfUtil.addMessageError("Usuario ya se encuentra registrado");
		}
	}

	private void validarImpedido() {
		if (promotorBean.getCausas() != null && !promotorBean.getCausas().isEmpty()) {
			promotorBean.setDeshabilitarRegistro(true);
			JsfUtil.addMessageError(promotorBean.getCausas());
		}
	}

	private void cargarDatosWsCedula(Cedula cedula) {
		if (cedula != null && cedula.getError().equals(Constantes.NO_ERROR_WS_MAE_SRI_RC)) {
			Persona persona = new Persona();
			try {
				persona = personaFacade.buscarPersonaPorPin(cedula.getCedula());
			} catch (Exception e) {
				persona = null;
			}			
			
			if(persona != null){
				promotorBean.setPersona(persona);
				promotorBean.getPersona().setNombre(cedula.getNombre());
				promotorBean.getPersona().setGenero(cedula.getGenero().equals(GENERO_MASCULINO) ? "MASCULINO" : "FEMENINO");
				cargarTratamiento(cedula);
			}else{
				promotorBean.getPersona().setId(null);
				promotorBean.getPersona().setNombre(cedula.getNombre());
				promotorBean.getPersona().setGenero(cedula.getGenero().equals(GENERO_MASCULINO) ? "MASCULINO" : "FEMENINO");
				cargarTratamiento(cedula);
			}			
		} else {
			promotorBean.setDeshabilitarRegistro(true);
			JsfUtil.addMessageError(cedula != null ? "Cédula Incorrecta" : SIN_SERVICIOS);
		}
	}

	private void cargarTratamiento(Cedula cedula) {
		if (cedula.getGenero().equals(GENERO_MASCULINO)) {
			promotorBean.setIdTipoTrato("1");
		} else if (("CASADO").equals(cedula.getEstadoCivil())) {
			promotorBean.setIdTipoTrato("2");
		} else {
			promotorBean.setIdTipoTrato("3");
		}
	}

	public void validarRuc() {
		try {
			promotorBean.setDeshabilitarRegistro(false);
			if (promotorBean.getUsuario().getNombre() != null && !promotorBean.getUsuario().getNombre().isEmpty()) {
				promotorBean.setCausas(impedidosFacade.listarNumeroDocumentoTiposImpedimento(promotorBean
						.getOrganizacion().getRuc(), TipoImpedidoEnum.CONSULTOR));
				if(promotorBean.getUsuario().getId()==null)
					validarUsuarioRegistrado(promotorBean.getUsuario().getNombre());
				
				validarImpedido();
				if (!promotorBean.isDeshabilitarRegistro()) {
					ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
							Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean
									.getUsuario().getNombre());
					try
					{
					if(contribuyenteCompleto.getRepresentanteLegal().getIdentificacion()!=null)
						{
							cargarDatosWsRuc(contribuyenteCompleto);
							promotorBean.setDeshabilitarRegistro(false);
						}
						else
						{
							JsfUtil.addMessageError("No tiene representante legal");
							promotorBean.setDeshabilitarRegistro(true);
						}

					}
					catch(Exception e)
					{
						JsfUtil.addMessageError("Sin servicios");
					}
				}
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("Debe ingresar el RUC");
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		}
	}

	private void cargarDatosWsRuc(ContribuyenteCompleto contribuyenteCompleto) {
		if (contribuyenteCompleto != null) {
			if (contribuyenteCompleto.getRazonSocial() != null) {
				promotorBean.getOrganizacion().setRuc(contribuyenteCompleto.getNumeroRuc());
				promotorBean.getOrganizacion().setNombre(contribuyenteCompleto.getRazonSocial());
				promotorBean.getOrganizacion().setCargoRepresentante(
						contribuyenteCompleto.getRepresentanteLegal().getCargo());
				promotorBean.getPersona().setPin(contribuyenteCompleto.getRepresentanteLegal().getIdentificacion());
				promotorBean.getPersona().setNombre(contribuyenteCompleto.getRepresentanteLegal().getNombre());				
				try {
					organizacionFacade.modificar(promotorBean.getOrganizacion());
					promotorBean.setOrganizacion(organizacionFacade.buscarPorRuc(contribuyenteCompleto.getNumeroRuc()));
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			} else {
				promotorBean.setDeshabilitarRegistro(true);
				JsfUtil.addMessageError("RUC no encontrado");
			}
		} else {
			promotorBean.setDeshabilitarRegistro(true);
			JsfUtil.addMessageError(SIN_SERVICIOS);
		}
	}

	 private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	 
	    /**
	     * Validate given email with regular expression.
	     * 
	     * @param email
	     *            email for validation
	     * @return true valid email, otherwise false
	     */
	    public static boolean validateEmail(String email) {
	 
	        // Compiles the given regular expression into a pattern.
	        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
	 
	        // Match the given input against this pattern
	        Matcher matcher = pattern.matcher(email);
	        return matcher.matches();
	 
	    }
	    
	/**
	 * @throws ServiceException 
     *
     */
	public void agregarContacto() throws ServiceException {
		boolean validarmail=false;
		if (promotorBean.getContacto().getValor() == null || promotorBean.getContacto().getValor().isEmpty()) {
			JsfUtil.addMessageError("Debe ingresar un valor para contactos");
			return;
		}
		if (Integer.valueOf(promotorBean.getIdFormaContacto())==5){
			validarmail= validateEmail(promotorBean.getContacto().getValor());
			if (validarmail){
				promotorBean.getContacto().setFormasContacto(
						new FormasContacto(Integer.valueOf(promotorBean.getIdFormaContacto())));
				if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
					promotorBean.getContacto().setPersona(promotorBean.getPersona());
				} else {
					promotorBean.getContacto().setOrganizacion(promotorBean.getOrganizacion());
				}
				if (promotorBean.getUsuario().getId() != null) {
					try {
						Contacto contactoPersist = contactoFacade.guardar(promotorBean.getContacto());
						contactoPersist
								.setFormasContacto(new FormasContacto(Integer.valueOf(promotorBean.getIdFormaContacto())));
						promotorBean.getListaContacto().add(contactoPersist);
					} catch (ServiceException e) {
						JsfUtil.addMessageError(e.getMessage());
						LOG.error(e, e);
					} catch (NumberFormatException e) {
						JsfUtil.addMessageError(e.getMessage());
						LOG.error(e, e);
					}
				} else {
					AuditarUsuario auditarUsuario= new AuditarUsuario();
					auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
					auditarUsuario.setDescripcion("se creo nuevo contacto: " +promotorBean.getContacto().getValor());
		        	auditarUsuario.setEstado(true);
		        	auditarUsuario.setFechaActualizacion(new Date());
		        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
		        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
					promotorBean.getListaContacto().add(promotorBean.getContacto());
				}
				promotorBean.setContacto(new Contacto());
				promotorBean.setIdFormaContacto(null);
			}else{
				JsfUtil.addMessageError("Correo incorrecto");
			}
		}else{
			promotorBean.getContacto().setFormasContacto(
					new FormasContacto(Integer.valueOf(promotorBean.getIdFormaContacto())));
			if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
				promotorBean.getContacto().setPersona(promotorBean.getPersona());
			} else {
				promotorBean.getContacto().setOrganizacion(promotorBean.getOrganizacion());
			}
			if (promotorBean.getUsuario().getId() != null) {
				try {
					Contacto contactoPersist = contactoFacade.guardar(promotorBean.getContacto());
					contactoPersist
							.setFormasContacto(new FormasContacto(Integer.valueOf(promotorBean.getIdFormaContacto())));
					promotorBean.getListaContacto().add(contactoPersist);
				} catch (ServiceException e) {
					JsfUtil.addMessageError(e.getMessage());
					LOG.error(e, e);
				} catch (NumberFormatException e) {
					JsfUtil.addMessageError(e.getMessage());
					LOG.error(e, e);
				}
			} else {
				AuditarUsuario auditarUsuario= new AuditarUsuario();
				auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
				auditarUsuario.setDescripcion("se creo nuevo contacto: " +promotorBean.getContacto().getValor());
	        	auditarUsuario.setEstado(true);
	        	auditarUsuario.setFechaActualizacion(new Date());
	        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
	        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
				promotorBean.getListaContacto().add(promotorBean.getContacto());
			}
			promotorBean.setContacto(new Contacto());
			promotorBean.setIdFormaContacto(null);
		}
		
	}

	/**
	 * @param contacto
	 * @throws ServiceException 
	 */
	public void removerContacto(Contacto contacto) throws ServiceException {
		promotorBean.getListaContacto().remove(contacto);
		promotorBean.setIdFormaContacto(null);
		try {
			if (contacto.getId() != null) {
				AuditarUsuario auditarUsuario= new AuditarUsuario();
				auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
				auditarUsuario.setDescripcion("se elimino el contacto: " +contacto.getValor());
	        	auditarUsuario.setEstado(true);
	        	auditarUsuario.setFechaActualizacion(new Date());
	        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
	        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
				contacto.setEstado(false);
				contactoFacade.modificar(contacto);
			}
		} catch (Exception e) {
			JsfUtil.addMessageError(e.getMessage());
			LOG.error(e, e);
		}
	}	
	
	public void guardaUsuario() throws Exception {
		
		if(mostrarTodasAreas && !esAutoridad){
			if(getPromotorBean().getSelectedNodes() == null || getPromotorBean().getSelectedNodes().length == 0){
	        	JsfUtil.addMessageError("Debe seleccionar al menos una Oficina Técnica");
	    		return;
	        }
			
			validarAreas();
		}	
		
		if(mostrarTodasAreas && esAutoridad){
			if(getPromotorBean().getSelectedNodes() == null || getPromotorBean().getSelectedNodes().length == 0){
	        	JsfUtil.addMessageError("Debe seleccionar al menos una Dirección Zonal");
	    		return;
	        }			
		}	
		
		promotorBean.getUsuario().setFechaCreacion(new Date());

		promotorBean.getPersona().setUbicacionesGeografica(new UbicacionesGeografica(Integer.valueOf(promotorBean.getIdParroquia())));
		
		if (getPromotorBean().getUsuario().getId() == null) {
			getPromotorBean().getUsuario().setFechaCreacion(new Date());
			getPromotorBean().getUsuario().setUsuarioCreacion(loginBean.getUsuario().getNombre());
			promotorBean.getUsuario().setTempPassword(JsfUtil.generatePassword());
			promotorBean.getUsuario().setPassword(JsfUtil.claveEncriptadaSHA1(promotorBean.getUsuario().getTempPassword()));
		} else {
			getPromotorBean().getUsuario().setFechaModificacion(new Date());
			getPromotorBean().getUsuario().setUsuarioModificacion(loginBean.getUsuario().getNombre());
		}		
		
		if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
			Usuario usuarioInicial= usuarioFacade.buscarUsuario(usuarioBean.getUsuario().getNombre());
			AuditarUsuario auditarUsuario= new AuditarUsuario();
			
			String d = null;
			if (!(usuarioInicial==null)){		
				String areas = areaUsuarioFacade.buscarAreaUsuarioNombres(usuarioInicial);
				d=usuarioInicial.getPersona().getPin() +"-TITULO:"+ usuarioInicial.getPersona().getTitulo()+"-TIPO TRATO:"+
						promotorBean.getIdTipoTrato()+"-NOMBRE: "+usuarioInicial.getPersona().getNombre() +"-GENERO: "+usuarioInicial.getPersona().getGenero()+"- NACIONALIDAD:"+Integer.parseInt(promotorBean.getIdNacionalidad())
						+"-IDPARROQUIA: "+Integer.parseInt(promotorBean.getIdParroquia())+
						"-AREA:"+areas;
			}
        	auditarUsuario.setDescripcion(d);
        	auditarUsuario.setEstado(true);
        	auditarUsuario.setFechaActualizacion(new Date());
        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
        	
			promotorBean.getPersona().setTipoTratos(
					new TipoTratos(Integer.valueOf(promotorBean.getIdTipoTrato())));
			promotorBean.getPersona().setContactos(promotorBean.getListaContacto());
			promotorBean.getPersona().setNacionalidad(
					new Nacionalidad(Integer.valueOf(promotorBean.getIdNacionalidad())));
			promotorBean.getUsuario().setNombre(promotorBean.getUsuario().getPin());
			promotorBean.getUsuario().setPersona(promotorBean.getPersona());
			promotorBean.getPersona().setPin(promotorBean.getUsuario().getPin());
			usuarioFacade.guardar(
					promotorBean.getUsuario(),
					cargarMail(promotorBean.getPersona().getNombre(), promotorBean.getUsuario().getNombre(),
							promotorBean.getUsuario().getTempPassword(), retornaMail()), false);
			
			if(mostrarTodasAreas){			
				getPromotorBean().setAreasGuardar(new ArrayList<Area>());
				for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
		             Area a = (Area) tn.getData(); 
		             //para no guardar direcciones zonales
		             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){     	
		                   getPromotorBean().getAreasGuardar().add(a);
		             }                
		         }	
				
				areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
			}else if(mostrarAreasProtegidas){
				if(getPromotorBean().getSelectedNode() != null){
					getPromotorBean().setAreasGuardar(new ArrayList<Area>());
					getPromotorBean().getAreasGuardar().add((Area) getPromotorBean().getSelectedNode().getData());
					
					areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
				}				
			}else{
				getPromotorBean().setAreasGuardar(new ArrayList<Area>());
				getPromotorBean().getAreasGuardar().add((Area) getPromotorBean().getSelectedNode().getData());
				
				areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
			}
			
			if(mostrarAreasProtegidas){
				
				for (TreeNode tn : getPromotorBean().getSelectedNodeAP()) {
					
					AreasSnapProvincia areaP = (AreasSnapProvincia) tn.getData();
					
					List<AreasSnapProvincia> lista = areasSnapProvinciaFacade.consultarAreasProtegidasSimilares(areaP);
					
					for(AreasSnapProvincia snap : lista){
						snap.setUsuario(promotorBean.getUsuario());
						
						getPromotorBean().setAreasProtegidasGuardar(new ArrayList<AreasSnapProvincia>());
						getPromotorBean().getAreasProtegidasGuardar().add(snap);
						areasSnapProvinciaFacade.guardar(snap);
					}					
		         }				
			}
			
			// insertar en 4 categorias.- persona natural con ruc o cédula
			//inserta persona
//			if (promotorBean.getUsuario().getArea().getTipoArea().getId()!=3){
				boolean token= false;
				boolean funcionario= false;
				boolean plantacentral=false;
				boolean encargado= false;
				if (!(promotorBean.getUsuario().getToken()==null)){
					token= promotorBean.getUsuario().getToken();
				}					
            	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
            	if (!(promotorBean.getUsuario().getFuncionario()==null)){
            		funcionario=promotorBean.getUsuario().getFuncionario();           		
            	}                
            	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
            		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
            	}
            	if (!(promotorBean.getUsuario().getSubrogante()==null)){
            		encargado=promotorBean.getUsuario().getSubrogante();            		
            	}    
			usuarioFacade.insertarPersonaVerde(promotorBean.getPersona().getPin(), promotorBean.getPersona().getTitulo(),
					promotorBean.getIdTipoTrato(),promotorBean.getPersona().getNombre(),promotorBean.getPersona().getGenero(),Integer.parseInt(promotorBean.getIdNacionalidad())
					,promotorBean.getIdTipoTrato(),Integer.parseInt(promotorBean.getIdParroquia()), promotorBean.getUsuario().getPassword(), 
					promotorBean.getListaContacto(), promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea(),token,estadoUsuario,funcionario,plantacentral,encargado);
			
        	
//			}
			
		} else {					
			Usuario usuarioInicial= usuarioFacade.buscarUsuario(usuarioBean.getUsuario().getNombre());
			AuditarUsuario auditarUsuario= new AuditarUsuario();
			
			Organizacion organiza = null;
			String areas = "";
			if(usuarioInicial != null){
				areas=areaFacade.getAreaUsuario(usuarioInicial);			
				organiza= organizacionFacade.buscarPorRuc(usuarioInicial.getNombre());	
			}					
			// Add Luis Lema -- carga datos de la organizacion con datos del sri
			PromotorBean promotorBeanTemporal;
			promotorBeanTemporal=new PromotorBean();
			if(organiza==null){				

				ContribuyenteCompleto contribuyenteCompleto = consultaRucCedula.obtenerPorRucSRI(
						Constantes.USUARIO_WS_MAE_SRI_RC, Constantes.PASSWORD_WS_MAE_SRI_RC, promotorBean
								.getOrganizacion().getRuc());	
				
				if (contribuyenteCompleto != null) {
					if (contribuyenteCompleto.getRazonSocial() != null) {
						promotorBeanTemporal.setOrganizacion(new Organizacion());
						promotorBeanTemporal.getOrganizacion().setRuc(contribuyenteCompleto.getNumeroRuc());
						promotorBeanTemporal.getOrganizacion().setNombre(contribuyenteCompleto.getRazonSocial());
						promotorBeanTemporal.getOrganizacion().setCargoRepresentante(
								contribuyenteCompleto.getRepresentanteLegal().getCargo());
						promotorBeanTemporal.setPersona(new Persona());
						promotorBeanTemporal.getPersona().setPin(contribuyenteCompleto.getRepresentanteLegal().getIdentificacion());
						promotorBeanTemporal.getPersona().setNombre(contribuyenteCompleto.getRepresentanteLegal().getNombre());
					} else {
						//promotorBeanTemporal.setDeshabilitarRegistro(true);
						JsfUtil.addMessageError("RUC no encontrado");
					}
				} else {
					//promotorBean.setDeshabilitarRegistro(true);
					JsfUtil.addMessageError(SIN_SERVICIOS);
				}
				organiza=promotorBeanTemporal.getOrganizacion();
//				organizacionFacade.modificar(organiza);				
			}			
			//End Add Luis Lema
			
			String organizacion = organiza.getNombre();
			String representanteLegal = usuarioInicial == null ? promotorBeanTemporal.getPersona().getPin() : usuarioInicial.getPersona().getPin();
			String tipoTrato = promotorBean.getIdTipoTrato() == null ? "" : promotorBean.getIdTipoTrato();
			String nombreRepresentante = usuarioInicial == null ? promotorBeanTemporal.getPersona().getNombre() : usuarioInicial.getPersona().getNombre();
			String idParroquia ="-IDPARROQUIA: "+ Integer.parseInt(promotorBean.getIdParroquia());
						
			String d="ORGANIZACION: "+organizacion+ "REPRESENTANTE LEGAL: "+ representanteLegal +
			"-TIPO TRATO:"+	tipoTrato
			+"-NOMBRE REPRESENTANTE: "+ nombreRepresentante +
					idParroquia+
			"-AREA:"+areas;
			
        	auditarUsuario.setDescripcion(d);
        	auditarUsuario.setEstado(true);
        	auditarUsuario.setFechaActualizacion(new Date());
        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
        	
			promotorBean.getOrganizacion().setContactos(promotorBean.getListaContacto());
			promotorBean.getUsuario().setNombre(promotorBean.getOrganizacion().getRuc());
			promotorBean.getPersona().setOrganizaciones(new ArrayList<Organizacion>());
			promotorBean.getPersona().getOrganizaciones().add(promotorBean.getOrganizacion());
			promotorBean.getUsuario().setPersona(promotorBean.getPersona());
			promotorBean.getOrganizacion().setParticipacionEstado(
					promotorBean.getIdTipoOrganizacion().equals(ID_EMPRESA_MIXTA) ? promotorBean
							.getOrganizacion().getParticipacionEstado() : null);
			promotorBean.getOrganizacion().setTipoOrganizacion(
					new TipoOrganizacion(Integer.valueOf(promotorBean.getIdTipoOrganizacion())));
			promotorBean.getOrganizacion().setIdUbicacionGeografica(
					Integer.valueOf(promotorBean.getIdParroquia()));
			promotorBean.getOrganizacion().setPersona(promotorBean.getPersona());
			
			promotorBean.getUsuario().setPin(promotorBean.getPersona().getPin());

			usuarioFacade.guardar(
					promotorBean.getUsuario(),
					cargarMail(promotorBean.getPersona().getNombre(), promotorBean.getOrganizacion().getRuc(),
							promotorBean.getUsuario().getTempPassword(), retornaMail()), false);
			
			if(mostrarTodasAreas){			
				getPromotorBean().setAreasGuardar(new ArrayList<Area>());
				for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
		             Area a = (Area) tn.getData(); 
		             //para no guardar direcciones zonales
		             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){     	
		                   getPromotorBean().getAreasGuardar().add(a);
		             }                
		         }			
						
				usuarioFacade.guardar(promotorBean.getUsuario());
				
				Usuario promotor = usuarioFacade.buscarUsuario(getPromotorBean().getUsuario().getNombre());
				areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotor);
			}else{
				getPromotorBean().setAreasGuardar(new ArrayList<Area>());
				getPromotorBean().getAreasGuardar().add((Area) getPromotorBean().getSelectedNode().getData());
				
				usuarioFacade.guardar(promotorBean.getUsuario());
				
				Usuario promotor = usuarioFacade.buscarUsuario(getPromotorBean().getUsuario().getNombre());
				
				areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotor);
			}
			
			
			boolean token= false;
			boolean funcionario= false;
			boolean plantacentral=false;
			boolean encargado= false;
			if (!(promotorBean.getUsuario().getToken()==null)){
				token= promotorBean.getUsuario().getToken();
			}					
        	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
        	if (!(promotorBean.getUsuario().getFuncionario()==null)){
        		funcionario=promotorBean.getUsuario().getFuncionario();           		
        	}                
        	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
        		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
        	}
        	if (!(promotorBean.getUsuario().getSubrogante()==null)){
        		encargado=promotorBean.getUsuario().getSubrogante();            		
        	}
			
			for (Contacto contacto : promotorBean.getListaContacto()) {
				contacto.setPersona(null);
				contacto.setOrganizacion(organizacionFacade.buscarPorRuc(promotorBean.getOrganizacion().getRuc()));
				contactoFacade.guardar(contacto);
			}
			
			List<Contacto> listacontactos= contactoFacade.buscarUsuarioNativeQuery(promotorBean.getUsuario().getNombre());
			usuarioFacade.insertarOrganizacionVerde(
        			promotorBean.getPersona().getPin(),promotorBean.getPersona().getTitulo(),promotorBean.getPersona().getIdTipoTratos().toString()
        			,promotorBean.getPersona().getNombre(),promotorBean.getPersona().getIdTipoTratos().toString(),Integer.parseInt(promotorBean.getIdParroquia())
        			,promotorBean.getOrganizacion().getRuc(),promotorBean.getOrganizacion().getNombre(),promotorBean.getPersona().getPosicion(),
        			promotorBean.getOrganizacion().getTipoOrganizacion().getId().toString(),Integer.parseInt(promotorBean.getIdParroquia()),
        			promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea()
        			, promotorBean.getUsuario().getPassword(),listacontactos,token,estadoUsuario,funcionario,plantacentral,encargado
        			);
			
			//aki ahi que quitar el etl
//			usuarioFacade.ejecutarMigracionUsuario();						
		}
//		usuarioFacade.ejecutarMigracionUsuario();
		JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
		inicio();
	}
	
	private String validarAreas(){
		try {
			String mensaje = "";
			
			List<Area> listaAreas = new ArrayList<Area>();
			List<Area> listaAreasAux = new ArrayList<Area>();
			for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
	             Area a = (Area) tn.getData(); 	             
	             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){   
	            	 Area area = areaFacade.getArea(a.getId());
	            	 area.setIdUnidadArea(a.getIdUnidadArea());
	            	 listaAreas.add(area);
	             }                
	         }	
			
			listaAreasAux.addAll(listaAreas);
			boolean mismaDireccion = true;
			for(Area a1 : listaAreas){
				for(Area a2 : listaAreasAux){					
					
					if(!a1.getArea().getId().equals(a2.getArea().getId())){
						mismaDireccion = false;	
						break;
					}
				}
			}
			
			if(!mismaDireccion){
				mensaje = "Las oficinas técnicas seleccionadas no tienen la misma Dirección Zonal";
				return mensaje;
			}			
			
			//validando cantidad de unidades, solo puede seleccionar una
			boolean variasUnidades = false;
			List<Integer> listaUnidades = new ArrayList<>();
			for(Area a1 : listaAreas){
				for(Area a2 : listaAreasAux){					
					if(a1.getId().equals(a2.getId())){
						if(a1.getIdUnidadArea() != null && a2.getIdUnidadArea() != null){
							listaUnidades.add(a1.getIdUnidadArea());
						}						
					}
				}
				
				if(listaUnidades.size() > 1){
					variasUnidades = true;
					break;
				}
				listaUnidades = new ArrayList<>();
			}
			
			if(variasUnidades){
				mensaje = "Debe seleccionar una sola unidad por oficina técnica";					
				return mensaje;
			}
			
			//escoger la misma unidad en la oficina tecnica
			boolean diferenteUnidad = false;
			for(Area a1 : listaAreas){
				for(Area a2 : listaAreasAux){
					
					if(a1.getIdUnidadArea() != null && a2.getIdUnidadArea() != null){
						
						UnidadArea unidadArea1 = new UnidadArea();
						UnidadArea unidadArea2 = new UnidadArea();
						unidadArea1 = unidadAreaFacade.buscarPorID(a1.getIdUnidadArea());
						unidadArea2 = unidadAreaFacade.buscarPorID(a2.getIdUnidadArea());
						
						if(!unidadArea1.getUnidad().getId().equals(unidadArea2.getUnidad().getId())){
							diferenteUnidad = true;
							break;
						}
					}					
				}
				if(diferenteUnidad)
					break;
			}
			
			if(diferenteUnidad){
				mensaje = "Debe seleccionar la misma unidad en todas las oficinas técnicas";					
				return mensaje;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	/**
     *
     */
	public void guardar() {
		try {
			List<Rol> rolesusuario = rolFacade.listarPorUsuario(promotorBean.getUsuario());
			Usuario usuarioBuscar= usuarioFacade.buscarUsuario(promotorBean.getUsuario().getNombre());
			
			if(mostrarTodasAreas && !esAutoridad){
				
				if(getPromotorBean().getSelectedNodes() == null || getPromotorBean().getSelectedNodes().length == 0){
		        	JsfUtil.addMessageError("Debe seleccionar al menos una Oficina Técnica");
		    		return;
		        }				
				
				String mensaje = validarAreas();
				if(!mensaje.isEmpty()){
					JsfUtil.addMessageError(mensaje);
					return;
				}				
			}

			if(mostrarTodasAreas && esAutoridad){
				
				if(getPromotorBean().getSelectedNodes() == null || getPromotorBean().getSelectedNodes().length == 0){
		        	JsfUtil.addMessageError("Debe seleccionar al menos una Dirección Zonal");
		    		return;
		        }							
			}

			if(adminAreas){
				if(getPromotorBean().getSelectedNodeAP() == null || getPromotorBean().getSelectedNodeAP().length == 0){
					JsfUtil.addMessageError("Debe seleccionar al menos un área Protegida");
					return;
				}
			}
			
			if (usuarioBuscar==null){
				if (validarRegistro()) {
					guardaUsuario();
				}
			}else{
				if (validarAreaProponente(promotorBean.getUsuario(), "PROPONENTE")){
					if (validarRegistro()) {
						guardaUsuario();
					}
				}else if (!validarExistenciaAreas(promotorBean.getUsuario())){
					if (validarRegistro()) {
						guardaUsuario();
					}
				}else if (validarExistenciaMismasAreas(promotorBean.getUsuario())){							
					if (validarRegistro()) {
						guardaUsuario();
					}
				}else{
					if(validacionAreasTramites(promotorBean.getUsuario())){
						JsfUtil.addMessageError("No se permite asignar una nueva área ya que tienen trámites pendientes");
					}else{
						if(validarAreasRoles(rolesusuario)){
							JsfUtil.addMessageError("Por favor quitar los roles para proceder con la asignación de la nueva área");
					}else{
						if (validarRegistro()) {
							promotorBean.getUsuario().setFechaCreacion(new Date());
							promotorBean.getPersona().setUbicacionesGeografica(
									new UbicacionesGeografica(Integer.valueOf(promotorBean.getIdParroquia())));
							if (getPromotorBean().getUsuario().getId() == null) {	
								getPromotorBean().getUsuario().setFechaCreacion(new Date());
								getPromotorBean().getUsuario().setUsuarioCreacion(loginBean.getUsuario().getNombre());
								promotorBean.getUsuario().setTempPassword(JsfUtil.generatePassword());
								promotorBean.getUsuario().setPassword(
										JsfUtil.claveEncriptadaSHA1(promotorBean.getUsuario().getTempPassword()));
							} else {
								getPromotorBean().getUsuario().setFechaModificacion(new Date());
								getPromotorBean().getUsuario().setUsuarioModificacion(loginBean.getUsuario().getNombre());
							}
							if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)) {
								Usuario usuarioInicial= usuarioFacade.buscarUsuario(usuarioBean.getUsuario().getNombre());
								AuditarUsuario auditarUsuario= new AuditarUsuario();
								String areas=areaFacade.getAreaUsuario(usuarioInicial);
								String d = null;
								if (!(usuarioInicial==null)){																	
									d=usuarioInicial.getPersona().getPin() +"-TITULO:"+ usuarioInicial.getPersona().getTitulo()+"-TIPO TRATO:"+
											promotorBean.getIdTipoTrato()+"-NOMBRE: "+usuarioInicial.getPersona().getNombre() +"-GENERO: "+usuarioInicial.getPersona().getGenero()+"- NACIONALIDAD:"+Integer.parseInt(promotorBean.getIdNacionalidad())
											+"-IDPARROQUIA: "+Integer.parseInt(promotorBean.getIdParroquia())+
											"-AREA:"+areas;
								}
					        	auditarUsuario.setDescripcion(d);
					        	auditarUsuario.setEstado(true);
					        	auditarUsuario.setFechaActualizacion(new Date());
					        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
					        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
					        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
					        	
								promotorBean.getPersona().setTipoTratos(
										new TipoTratos(Integer.valueOf(promotorBean.getIdTipoTrato())));
								promotorBean.getPersona().setContactos(promotorBean.getListaContacto());
								promotorBean.getPersona().setNacionalidad(
										new Nacionalidad(Integer.valueOf(promotorBean.getIdNacionalidad())));
								promotorBean.getUsuario().setNombre(promotorBean.getUsuario().getPin());
								promotorBean.getUsuario().setPersona(promotorBean.getPersona());
								promotorBean.getPersona().setPin(promotorBean.getUsuario().getPin());
								usuarioFacade.guardar(
										promotorBean.getUsuario(),
										cargarMail(promotorBean.getPersona().getNombre(), promotorBean.getUsuario().getNombre(),
												promotorBean.getUsuario().getTempPassword(), retornaMail()), false);
								//nuevo código
							
							if(mostrarTodasAreas && !esAutoridad){			
								getPromotorBean().setAreasGuardar(new ArrayList<Area>());
								for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
						             Area a = (Area) tn.getData(); 
						             //para no guardar direcciones zonales
						             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){
						                    getPromotorBean().getAreasGuardar().add(a);
						             }                
						         }				
								areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
							}else if(mostrarTodasAreas && esAutoridad){
								getPromotorBean().setAreasGuardar(new ArrayList<Area>());
								List<String> mensajes = new ArrayList<String>();
								
								for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
						             Area a = (Area) tn.getData(); 
						             
						             List<Usuario> listaUsuario = new ArrayList<>();						             
						            
						            listaUsuario = usuarioFacade.buscarUsuariosPorRolYArea("AUTORIDAD AMBIENTAL", a);
						             
						             
						             if(!listaUsuario.isEmpty()){
						            	 for(Usuario au : listaUsuario){
						            		 if(!au.equals(promotorBean.getUsuario())){
						            			 mensajes.add("Ya existe un usuario con el rol AUTORIDAD AMBIENTAL para la " + a.getAreaName());
						            		 }
						            	 }						            	 
						             }
						             
						             getPromotorBean().getAreasGuardar().add(a);
						         }
								
								 if(!mensajes.isEmpty()){
									 JsfUtil.addMessageError(mensajes);
					            	 return;
					             }
								
								areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
							}else{
								getPromotorBean().setAreasGuardar(new ArrayList<Area>());
								getPromotorBean().getAreasGuardar().add((Area) getPromotorBean().getSelectedNode().getData());
								areaUsuarioFacade.guardarAreasUsuario(getPromotorBean().getAreasGuardar(), promotorBean.getUsuario());
							}
							
							// insertar en 4 categorias.- persona natural con ruc o cédula
							//inserta persona
							
							List<AreaUsuario> listaAreasUsuario = areaUsuarioFacade.buscarAreaUsuario(promotorBean.getUsuario());
							
							boolean ente = false;
							
							for(AreaUsuario au: listaAreasUsuario){
				        		if(au.getArea().getTipoArea().getId() == 3){
				        			ente = true;
				        			break;
				        		}else{
				        			ente = false;
				        			break;
				        		}
				        	}				        	
							
							
							if (!ente){
								boolean token= false;
								boolean funcionario= false;
								boolean plantacentral=false;
								boolean encargado= false;
								if (!(promotorBean.getUsuario().getToken()==null)){
									token= promotorBean.getUsuario().getToken();
								}					
			                	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
			                	if (!(promotorBean.getUsuario().getFuncionario()==null)){
			                		funcionario=promotorBean.getUsuario().getFuncionario();           		
			                	}                
			                	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
			                		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
			                	}
			                	if (!(promotorBean.getUsuario().getSubrogante()==null)){
			                		encargado=promotorBean.getUsuario().getSubrogante();            		
			                	}    
							usuarioFacade.insertarPersonaVerde(promotorBean.getPersona().getPin(), promotorBean.getPersona().getTitulo(),
									promotorBean.getIdTipoTrato(),promotorBean.getPersona().getNombre(),promotorBean.getPersona().getGenero(),Integer.parseInt(promotorBean.getIdNacionalidad())
									,promotorBean.getIdTipoTrato(),Integer.parseInt(promotorBean.getIdParroquia()), promotorBean.getUsuario().getPassword(), 
									promotorBean.getListaContacto(), promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea(),token,estadoUsuario,funcionario,plantacentral,encargado);
							
				        	
							}
							
						} else {					
							Usuario usuarioInicial= usuarioFacade.buscarUsuario(usuarioBean.getUsuario().getNombre());
							AuditarUsuario auditarUsuario= new AuditarUsuario();
							String areas=areaFacade.getAreaUsuario(usuarioInicial);
							Organizacion organiza= organizacionFacade.buscarPorRuc(usuarioInicial.getNombre());
							String d="ORGANIZACION: "+organiza.getNombre()+ "REPRESENTANTE LEGAL: "+usuarioInicial.getPersona().getPin() +"-TIPO TRATO:"+
									promotorBean.getIdTipoTrato()+"-NOMBRE REPRESENTANTE: "+usuarioInicial.getPersona().getNombre() +"-IDPARROQUIA: "+Integer.parseInt(promotorBean.getIdParroquia())+
									"-AREA:"+areas;
				        	auditarUsuario.setDescripcion(d);
				        	auditarUsuario.setEstado(true);
				        	auditarUsuario.setFechaActualizacion(new Date());
				        	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
				        	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
				        	usuarioFacade.modificarAuditoriAUsuario(auditarUsuario);	
				        	
							promotorBean.getOrganizacion().setContactos(promotorBean.getListaContacto());
							promotorBean.getUsuario().setNombre(promotorBean.getOrganizacion().getRuc());
							promotorBean.getPersona().setOrganizaciones(new ArrayList<Organizacion>());
							promotorBean.getPersona().getOrganizaciones().add(promotorBean.getOrganizacion());
							promotorBean.getUsuario().setPersona(promotorBean.getPersona());
							promotorBean.getOrganizacion().setParticipacionEstado(
									promotorBean.getIdTipoOrganizacion().equals(ID_EMPRESA_MIXTA) ? promotorBean
											.getOrganizacion().getParticipacionEstado() : null);
							promotorBean.getOrganizacion().setTipoOrganizacion(
									new TipoOrganizacion(Integer.valueOf(promotorBean.getIdTipoOrganizacion())));
							promotorBean.getOrganizacion().setIdUbicacionGeografica(
									Integer.valueOf(promotorBean.getIdParroquia()));
							promotorBean.getOrganizacion().setPersona(promotorBean.getPersona());
							
							promotorBean.getUsuario().setPin(promotorBean.getPersona().getPin());
	
							usuarioFacade.guardar(
									promotorBean.getUsuario(),
									cargarMail(promotorBean.getPersona().getNombre(), promotorBean.getOrganizacion().getRuc(),
											promotorBean.getUsuario().getTempPassword(), retornaMail()), false);
							
							boolean token= false;
							boolean funcionario= false;
							boolean plantacentral=false;
							boolean encargado= false;
							if (!(promotorBean.getUsuario().getToken()==null)){
								token= promotorBean.getUsuario().getToken();
							}					
				        	boolean estadoUsuario =promotorBean.getUsuario().getEstado();
				        	if (!(promotorBean.getUsuario().getFuncionario()==null)){
				        		funcionario=promotorBean.getUsuario().getFuncionario();           		
				        	}                
				        	if (!(promotorBean.getUsuario().getPlantaCentral()==null)){
				        		plantacentral=promotorBean.getUsuario().getPlantaCentral();        		
				        	}
				        	if (!(promotorBean.getUsuario().getSubrogante()==null)){
				        		encargado=promotorBean.getUsuario().getSubrogante();            		
				        	}
				        	
				        	for (Contacto contacto : promotorBean.getListaContacto()) {
								contacto.setPersona(null);
								contacto.setOrganizacion(organizacionFacade.buscarPorRuc(usuarioInicial.getNombre()));				
								contactoFacade.guardar(contacto);
							}
				        	
							List<Contacto> listacontactos= contactoFacade.buscarUsuarioNativeQuery(promotorBean.getUsuario().getNombre());
							
							usuarioFacade.insertarOrganizacionVerde(
				        			promotorBean.getPersona().getPin(),promotorBean.getPersona().getTitulo(),promotorBean.getPersona().getIdTipoTratos().toString()
				        			,promotorBean.getPersona().getNombre(),promotorBean.getPersona().getIdTipoTratos().toString(),Integer.parseInt(promotorBean.getIdParroquia())
				        			,promotorBean.getOrganizacion().getRuc(),promotorBean.getOrganizacion().getNombre(),promotorBean.getPersona().getPosicion(),
				        			promotorBean.getOrganizacion().getTipoOrganizacion().getId().toString(),Integer.parseInt(promotorBean.getIdParroquia()),
				        			promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea()
				        			, promotorBean.getUsuario().getPassword(),listacontactos,token,estadoUsuario,funcionario,plantacentral,encargado
				        			);
							
//							usuarioFacade.ejecutarMigracionUsuario();						
						}
	//					usuarioFacade.ejecutarMigracionUsuario();
						JsfUtil.addMessageInfo(JsfUtil.REGISTRO_ACTUALIZADO);
						inicio();
						}
					}
				}			
			}
		}
			

		} catch (NumberFormatException e) {
			LOG.error(e, e);
		} catch (ServiceException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(e.getMessage());
		} catch (RuntimeException e) {
			LOG.error(e, e);
			JsfUtil.addMessageError(e.getMessage());
		} catch (Exception e) {
			LOG.error(e, e);
			JsfUtil.addMessageError("Ocurrio un error mientras se almacenaban los datos.");
		}
	}
	
	private  boolean validarAreaProponente(Usuario usuario, String nombre){
		boolean existeRol = false;
		try {
			List<AreaUsuario> lista = areaUsuarioFacade.buscarPorAreaUsuario(usuario, nombre);
			
			if(lista != null && !lista.isEmpty()){
				existeRol = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return existeRol;
	}
	
	private  boolean validarExistenciaAreas(Usuario usuario){
		boolean existeRol = false;
		try {
			List<AreaUsuario> lista = areaUsuarioFacade.buscarAreaUsuario(usuario);
			
			if(lista != null && !lista.isEmpty()){
				existeRol = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return existeRol;
	}
	
	private  boolean validarExistenciaMismasAreas(Usuario usuario){
		boolean areasIguales = false;
		try {		
			
			if(mostrarTodasAreas){
				
				for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
		             Area a = (Area) tn.getData(); 
		             //para no guardar direcciones zonales
		             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){		            	
		            	List<AreaUsuario> lista = areaUsuarioFacade.buscarPorAreaUsuario(usuario, a.getAreaName());	    
		            	
		            	if(lista != null && !lista.isEmpty()){
		            		areasIguales = true;
		            	}else{
		            		areasIguales = false;
		            		break;
		            	}
		             }                
		         }				
			}else{
				
				List<AreaUsuario> lista = areaUsuarioFacade.buscarPorAreaUsuario(usuario, promotorBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaName());
				if(lista != null && !lista.isEmpty()){
            		areasIguales = true;
            	}else{
            		areasIguales = false;            		
            	}				
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return areasIguales;
	}

	private boolean validarRegistro() {
		List<String> listaMensajes = new ArrayList<String>();

		if(!mostrarTodasAreas){

			if (getPromotorBean().getSelectedNode() == null) {
				listaMensajes.add("Debe asignar un área/subárea");
			}
		}
		
//		if (promotorBean.getSelectedNode().getParent().toString().startsWith(DefaultTreeNode.class.getName())) {
//			listaMensajes.add("Ud ha seleccionado el área " + promotorBean.getSelectedNode()
//					+ ", Debe seleccionar una subárea");
//		}
		if (promotorBean.getListaContacto() == null || promotorBean.getListaContacto().isEmpty()) {
			listaMensajes.add("Debe ingresar la información de Contacto");
		}
		if (promotorBean.getIdParroquia() == null || promotorBean.getIdParroquia().isEmpty()) {
			listaMensajes.add("Debe seleccionar la ubicación geográfica");
		}
		if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL) && promotorBean.getIdNacionalidad().isEmpty()) {
			listaMensajes.add("Debe seleccionar la nacionalidad");
		}
		if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL) && promotorBean.getIdTipoTrato().isEmpty()) {
			listaMensajes.add("Debe seleccionar tratamiento");
		}
		if (promotorBean.getTipoPersona().equals(PERSONA_NATURAL)
				&& (promotorBean.getPersona().getTitulo() == null || promotorBean.getPersona().getTitulo().isEmpty())) {
			listaMensajes.add("Debe ingresar el título académico");
		}
		if (promotorBean.getTipoPersona().equals(PERSONA_JURIDICA) && promotorBean.getIdTipoOrganizacion().isEmpty()) {
			listaMensajes.add("Debe seleccionar el tipo de organización");
		}
		if (listaMensajes.isEmpty()) {
			return true;
		} else {
			JsfUtil.addMessageError(listaMensajes);
			return false;
		}
	}

	private NotificacionesMails cargarMail(final String nombres, final String login, final String clave,
			final String email) {
		NotificacionesMails nm = new NotificacionesMails();
		nm.setAsunto("Aprobación registro de usuario");
		StringBuilder mensaje = new StringBuilder();
		mensaje.append("<p>Estimado/a Se&ntilde;or/a ").append(nombres).append("</p>").append("<br/> <br/>");
		mensaje.append("<p>Confirmamos que su solicitud de registro de usuario en el sistema SUIA fue aprobada con los siguientes datos: </p><br/>");
		mensaje.append("<ul><li>Nombre de usuario: ").append(login).append("</li>");
		mensaje.append("<li>Contrase&ntilde;a: ").append(clave).append("</li>").append("</ul><br/>");
		mensaje.append(
				"<p>Por favor solicitamos el cambio de contrase&ntilde;a, ingresando al sistema Perfil de usuario, una vez que haya iniciado sesi&oacute;n en su cuenta, haga clic en la opci&oacute;n Editar Perfil que se encuentra en la esquina superior derecha de la pantalla.")
				.append("</p> <br/>");
		mensaje.append("<p>Saludos,").append("</p>").append("<br/>");
		mensaje.append("<p>Ministerio del Ambiente y Agua").append("</p>").append("<br/>");
		mensaje.append("<img src=\"").append(Constantes.getHttpSuiaImagenFooterMail())
				.append("\" height=\"110\" width=\"750\" alt=\"Smiley face\">");
		nm.setContenido(mensaje.toString());
		nm.setEmail(email);
		nm.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
		return nm;
	}

	private String retornaMail() throws ServiceException {
		String email = null;
		for (Contacto c : promotorBean.getListaContacto()) {
			if (c.getFormasContacto().getId() == FormasContacto.EMAIL) {
				email = c.getValor();
			}
		}
		if (email == null) {
			throw new ServiceException("Debe ingresar un Email");
		}
		return email;
	}

	private void cargarArbolAreas(List<Area> areas, TreeNode nodo) {
		try {
			if (nodo == null) {
				List<Area> areasH = areaFacade.listarAreasPadre();
				cargarArbolAreas(areasH, getPromotorBean().getRoot());
			} else {
				for (Area m : areas) {
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarArea(m));

					List<Area> menusN = new ArrayList<Area>();
					List<Area> areasNuevas = new ArrayList<Area>();
					if(m.getPadre() == null){
						menusN = areaFacade.listarAreasHijos(m);
						List<UnidadArea> listaUn = unidadAreaFacade.obtenerUnidadesAreaPorArea(m.getId());
						
						if(listaUn != null && !listaUn.isEmpty()){
							for(UnidadArea una : listaUn){								
								Area area = new Area();
								area.setId(una.getArea().getId());
								area.setAreaName(una.getUnidad().getNombre());
								area.setIdUnidadArea(una.getId());		
								area.setPadre("unidad");
								area.setAreaAbbreviation(una.getUnidad().getAbreviacion());
								area.setTipoArea(m.getTipoArea());
								areasNuevas.add(area);
							}
						}
					}
					if(areasNuevas != null && !areasNuevas.isEmpty()){
						menusN.addAll(areasNuevas);
					}				
					
					cargarArbolAreas(menusN, nodoH);
				
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}
	
	private void cargarArbolAreasOficinasTecnicas(List<Area> areas, TreeNode nodo) {
		try {
			if (nodo == null) {
				List<Area> areasH = areaFacade.listarAreasPadreOT();
				cargarArbolAreasOficinasTecnicas(areasH, getPromotorBean().getRootOt());
			} else {
				for (Area m : areas) {					
					
					List<Area> areasNuevas = new ArrayList<Area>();
					if(m.getPadre() == null){
						List<UnidadArea> listaUn = unidadAreaFacade.obtenerUnidadesAreaPorArea(m.getId());
						
						if(listaUn != null && !listaUn.isEmpty()){
							for(UnidadArea una : listaUn){								
								Area area = new Area();
								area.setId(una.getArea().getId());
								area.setAreaName(una.getUnidad().getNombre());
								area.setIdUnidadArea(una.getId());		
								area.setPadre("unidad");
								area.setAreaAbbreviation(una.getUnidad().getAbreviacion());
								area.setTipoArea(m.getTipoArea());
								areasNuevas.add(area);
							}
						}
					}
					
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarArea(m));
					
					if(areasNuevas != null && !areasNuevas.isEmpty()){											
						cargarArbolAreasOficinasTecnicas(areasNuevas, nodoH);
					}else{
						List<Area> menusN = areaFacade.listarAreasHijos(m);
						cargarArbolAreasOficinasTecnicas(menusN, nodoH);
					}						
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}
	
	private void cargarArbolAreasDireccionesZonales(List<Area> areas, TreeNode nodo) {
		try {
			if (nodo == null) {
				List<Area> areasH = areaFacade.listarDireccionesZonales();
				cargarArbolAreasDireccionesZonales(areasH, getPromotorBean().getRootOt());
			} else {
				for (Area m : areas) {					
					
					List<Area> areasNuevas = new ArrayList<Area>();					
					
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarArea(m));
															
					cargarArbolAreasDireccionesZonales(areasNuevas, nodoH);										
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}
	
	private void cargarArbolAreasInstitucional(List<Area> areas, TreeNode nodo){
		
		try {
			if (nodo == null) {
				List<AreaUsuario> lista = areaUsuarioFacade.buscarAreaUsuario(usuarioBean.getUsuario());
				
				List<Area> areasH = new ArrayList<>();
				if(lista != null && !lista.isEmpty()){
					
					for(AreaUsuario au: lista){
						List<Area> areasHAux = areaFacade.listarAreasPadreInstitucional(au.getArea());
						for(Area area: areasHAux){
							if(!areasH.contains(area)){
								areasH.add(area);
							}
						}
					}
				}else{
					List<AreaUsuario> listaA = areaUsuarioFacade.buscarAreaUsuario(loginBean.getUsuario());
					if(listaA != null && !listaA.isEmpty()){
						
						for(AreaUsuario au: listaA){
							List<Area> areasHAux = areaFacade.listarAreasPadreInstitucional(au.getArea());
							for(Area area: areasHAux){
								if(!areasH.contains(area)){
									areasH.add(area);
								}
							}
						}
					}
				}
				
				cargarArbolAreasInstitucional(areasH, getPromotorBean().getRoot());
			} else {
				for (Area m : areas) {
					
					List<Area> areasNuevas = new ArrayList<Area>();
					if(m.getPadre() == null){
						List<UnidadArea> listaUn = unidadAreaFacade.obtenerUnidadesAreaPorArea(m.getId());
						
						if(listaUn != null && !listaUn.isEmpty()){
							for(UnidadArea una : listaUn){								
								Area area = new Area();
								area.setId(una.getArea().getId());
								area.setAreaName(una.getUnidad().getNombre());
								area.setIdUnidadArea(una.getId());		
								area.setPadre("unidad");
								area.setAreaAbbreviation(una.getUnidad().getAbreviacion());
								area.setTipoArea(m.getTipoArea());
								areasNuevas.add(area);
							}
						}
					}
					
					
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarArea(m));
					
					if(areasNuevas != null && !areasNuevas.isEmpty()){									
						cargarArbolAreasInstitucional(areasNuevas, nodoH);
					}else{
						if(!m.getTipoArea().getId().equals(3)){
							List<Area> menusN = areaFacade.listarAreasHijos(m);
							cargarArbolAreasInstitucional(menusN, nodoH);
						}						
					}					
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}
	
	private void cargarArbolAreasInstitucionalOT(List<Area> areas, TreeNode nodo){
		
		try {
			
			if (nodo == null) {
				
				cargarArbolAreasInstitucional(areas, getPromotorBean().getRootOt());
				
			} else {
				for (Area m : areas) {
					
					List<Area> areasNuevas = new ArrayList<Area>();
					if(m.getPadre() == null){
						List<UnidadArea> listaUn = unidadAreaFacade.obtenerUnidadesAreaPorArea(m.getId());
						
						if(listaUn != null && !listaUn.isEmpty()){
							for(UnidadArea una : listaUn){								
								Area area = new Area();
								area.setId(una.getArea().getId());
								area.setAreaName(una.getUnidad().getNombre());
								area.setIdUnidadArea(una.getId());		
								area.setPadre("unidad");
								area.setAreaAbbreviation(una.getUnidad().getAbreviacion());
								area.setTipoArea(m.getTipoArea());
								areasNuevas.add(area);
							}
						}
					}
					
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarArea(m));
					
					if(areasNuevas != null && !areasNuevas.isEmpty()){												
						cargarArbolAreasInstitucionalOT(areasNuevas, nodoH);
					}else{
						List<Area> menusN = areaFacade.listarAreasHijos(m);
						cargarArbolAreasInstitucionalOT(menusN, nodoH);						
					}					
				}
			}
		} catch (ServiceException e) {
			LOG.error(e, e);
		} catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}

	private boolean seleccionarArea(Area a) {
		
		if(a.getIdUnidadArea() !=null){
			Comparator<Area> c = new Comparator<Area>() {
				
				@Override
				public int compare(Area o1, Area o2) {							
					return o1.getId().compareTo(o2.getId());
				}
			};
			
			Collections.sort(promotorBean.getAreasGuardar(), c);
			int index = Collections.binarySearch(promotorBean.getAreasGuardar(), new Area(a.getId()), c);
			
			if(index >= 0){
				Area area = promotorBean.getAreasGuardar().get(index);
				
				boolean unidad = false;
				if(area.getListaUnidadesGuardadas() != null && !area.getListaUnidadesGuardadas().isEmpty()){
					for(UnidadArea ua : area.getListaUnidadesGuardadas()){
						if(a.getIdUnidadArea().equals(ua.getId())){
							unidad = true;
							break;
						}else{
							unidad = false;
						}
					}
				}
				
				return unidad;

			}else
				return false;
		}
		
		if(promotorBean.getAreasGuardar().contains(a)){
			return true;
		}else{
			return false;
		}		
	}	
	
	private void cargarArbolAreasProtegidas(List<AreasSnapProvincia> areas, TreeNode nodo){		
		try {
			
			if (nodo == null) {				
				cargarArbolAreasProtegidas(areas, getPromotorBean().getRootAP());				
			} else {
				for(AreasSnapProvincia m : areas){
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(seleccionarSnap(m));
				}
			}
		}catch (RuntimeException e) {
			LOG.error(e, e);
		}
	}
	
	private boolean seleccionarSnap(AreasSnapProvincia snap){
		
		if(promotorBean.getAreasProtegidasGuardar().contains(snap)){
			return true;
		}else
			return false;
	}
	
	
	private void cargarArbolAreasPr(List<Area> areas, TreeNode nodo){		
		try {
			
			if (nodo == null) {				
				cargarArbolAreasPr(areas, getPromotorBean().getRoot());				
			} else {
				for(Area m : areas){
					TreeNode nodoH = new DefaultTreeNode(m, nodo);
					nodoH.setExpanded(true);
					nodoH.setSelected(false);
					
					List<Area> areasHijas = areaFacade.listarAreasHijos(m);
					
					cargarArbolAreasPr(areasHijas, nodoH);		
				}
			}
		}catch (Exception e) {
			LOG.error(e, e);
		}
	}
	
	
	public boolean usuarioConectadoAdmin(){
		Usuario u=JsfUtil.getLoggedUser();
		return Usuario.isUserInRole(u, "admin");
	}
	
	public boolean usuarioConectadoAdminInstitucional(){
		Usuario u=JsfUtil.getLoggedUser();
		boolean admin= Usuario.isUserInRole(u, "admin");
		boolean adminInsti=Usuario.isUserInRole(u, "ADMINISTRADOR INSTITUCIONAL");
		boolean adminAreas=Usuario.isUserInRole(u, "ADMINISTRADOR AREAS");
		if (admin==true || adminInsti==true || adminAreas==true){
			return true;
		}else{
			return false;
		}
	}
		
	public void handleFileUpload(FileUploadEvent event) throws IOException, BadElementException {
		file = event.getFile();		
		nuevaFirma=file.getFileName();
		Image imagen = Image.getInstance(file.getContents());
		if ((imagen.getHeight()<=80 && imagen.getHeight()>49) && ((imagen.getWidth()<=201)&& imagen.getWidth()>130)){
			documentofirma =UtilDocumento.generateDocumentPngFromUpload(file.getContents(),
					file.getFileName());
		}else{
			JsfUtil.addMessageError("La imagen debe tener una resolución de pixeles de 200*50");
		}		
	}	
	
	public void documentosSeleccionado(Documents documentsfirma) {
		documents=new Documents();
		documents=documentsfirma;
		nuevaFirma= documents.getName();
	}
	
	public void saveDocument() throws CmisAlfrescoException, IOException, BadElementException {			
		Image imagen = Image.getInstance(file.getContents());
		setHabilitarDescargaFirmas(false);
		if ((imagen.getHeight()<=80 && imagen.getHeight()>49) && ((imagen.getWidth()<=201)&& imagen.getWidth()>130)){
			file.getSize();		
			String fileid = null ;
					if (!documents.getObjectId().isEmpty()){
					fileid =documents.getObjectId().substring(0,
			documents.getObjectId().indexOf(";"));
					}
			String nuevoNombre[]=documents.getName().replace("/", "_").split(".png");	
			String name = new SimpleDateFormat("dd-MM-yyyy-HHmmssSS").format(new Date());				
			nuevaFirma=nuevoNombre[0]+"-"+loginBean.getNombreUsuario()+"-"+name+".png";
			if (!documents.getObjectId().isEmpty()){
				if (alfrescoServiceBean.updateDocumentPropertiesFirma(fileid,(nuevaFirma))) {
					System.out.println("cambio realizado");
				}
			}
			if (usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getId()==2){
				if (alfrescoServiceBean.findFolder(Constantes.getFirmasIdMAE(), usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_"))==null){
					alfrescoServiceBean.folderCreate(Constantes.getFirmasIdMAE(), usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_"));
				}
				alfrescoServiceBean.fileSaveStreamFirmas(documentofirma.getContenidoDocumento(), documents.getName().replace("/", "_"), alfrescoServiceBean.findFolder(Constantes.getFirmasIdMAE(), usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_")).getObjectId(), "", "", 0);	
			}else{
				alfrescoServiceBean.fileSaveStreamFirmas(documentofirma.getContenidoDocumento(), documents.getName().replace("/", "_"), Constantes.getFirmasId(), "", "", 0);	
			}
			if (usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getId()==3){
				setVerFirmas(true);
				resultFirmas= new ArrayList<Documents>();	
				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("logo__"+usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("firma__"+usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("pie__"+usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_") + ".png", Constantes.getFirmasId()));
				setHabilitarDescargaFirmas(true);
			}else{
				setVerFirmas(true);
				resultFirmas= new ArrayList<Documents>();
				resultFirmas.addAll(alfrescoServiceBean.archivosFirmas("firma__"+usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_") + ".png", alfrescoServiceBean.findFolder(Constantes.getFirmasIdMAE(), usuarioBean.getUsuario().getListaAreaUsuario().get(0).getArea().getAreaAbbreviation().replace("/", "_")).getObjectId()));
				setHabilitarDescargaFirmas(true);
			}
			AuditarUsuario auditarUsuario= new AuditarUsuario();
			if (file.getFileName().contains("LOGO__")){
				auditarUsuario.setDescripcion("Modificación");
			}else if (file.getFileName().contains("FIRMA__")){
				auditarUsuario.setDescripcion("Modificación");
			}else{
				auditarUsuario.setDescripcion("Modificación");
			}
	    	auditarUsuario.setEstado(true);
	    	auditarUsuario.setFechaActualizacion(new Date());
	    	auditarUsuario.setUsuario(promotorBean.getUsuario().getId());
	    	auditarUsuario.setUsuarioMae(loginBean.getUsuario().getId());
	    	JsfUtil.addMessageInfo("Se procedió con la "+auditarUsuario.getDescripcion());
		}else{
			JsfUtil.addMessageError("La imagen debe tener una resolución de pixeles de 200*50");
		}		
	}
	
	public boolean validacionAreasTramites(Usuario usuario){
		boolean tieneTramites = false;
		try {		
			
			if(mostrarTodasAreas){
				
				List<Area> listaAreasUsuario = areaUsuarioFacade.buscarAreas(usuario);
				
				List<Area> listaAreasUsuarioAux = new ArrayList<>();
				listaAreasUsuarioAux.addAll(listaAreasUsuario);
				
				List<Area> listaAreasUnidadesSeleccionadas = new ArrayList<>();
				List<Area> listaAreasComparar = new ArrayList<>();
				for (TreeNode tn : getPromotorBean().getSelectedNodes()) {	
					Area a = (Area) tn.getData();
					listaAreasUnidadesSeleccionadas.add(a);
				}
											
				for(Area a1 : listaAreasUnidadesSeleccionadas){
					
					Comparator<Area> c = new Comparator<Area>() {
						
						@Override
						public int compare(Area o1, Area o2) {							
							return o1.getId().compareTo(o2.getId());
						}
					};
					
					Collections.sort(listaAreasComparar, c);
					int index = Collections.binarySearch(listaAreasComparar, new Area(a1.getId()), c);
					
					if(index >= 0){

					}else{
						a1.setListaUnidades(new ArrayList<Area>());
						listaAreasComparar.add(a1);
					}				
				}				
				
				if(!esAutoridad){
					for (Area a : listaAreasComparar) {					
//			             Area a = (Area) tn.getData(); 
			             //para no guardar direcciones zonales
			             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){		            	
			            	
			            	 Comparator<Area> c = new Comparator<Area>() {
									
									@Override
									public int compare(Area o1, Area o2) {							
										return o1.getId().compareTo(o2.getId());
									}
								};
								
								Collections.sort(listaAreasUsuarioAux, c);
								int index = Collections.binarySearch(listaAreasUsuarioAux, new Area(a.getId()), c);
								
								if(index >= 0){
									listaAreasUsuarioAux.remove(index);								
								}		            	 
			             }                
			         }	
				}else{
					for (Area a : listaAreasComparar) {	
			             if(a.getTipoArea().getSiglas().toUpperCase().equals("ZONALES") || a.getId().equals(272)){		            	
			            	
			            	 Comparator<Area> c = new Comparator<Area>() {
									
									@Override
									public int compare(Area o1, Area o2) {							
										return o1.getId().compareTo(o2.getId());
									}
								};
								
								Collections.sort(listaAreasUsuarioAux, c);
								int index = Collections.binarySearch(listaAreasUsuarioAux, new Area(a.getId()), c);
								
								if(index >= 0){
									listaAreasUsuarioAux.remove(index);								
								}		            	 
			             }                
			         }	
				}		
				
				if(listaAreasUsuarioAux.isEmpty()){
					tieneTramites = false;
				}else{
					//Tiene un área diferente a la guardada.
					if(!(((usuarioFacade.consultaTaskhyd4cat(promotorBean.getUsuario().getNombre())==0) && (usuarioFacade.consultaTaskhyd(promotorBean.getUsuario().getNombre())==0)) 
						&& (usuarioFacade.consultaTaskhydSuiaiii(promotorBean.getUsuario().getNombre())==0))){
						tieneTramites = true;
					}
				}				
			}else{
				
				if(!(((usuarioFacade.consultaTaskhyd4cat(promotorBean.getUsuario().getNombre())==0) && (usuarioFacade.consultaTaskhyd(promotorBean.getUsuario().getNombre())==0)) 
						&& (usuarioFacade.consultaTaskhydSuiaiii(promotorBean.getUsuario().getNombre())==0))){					
            		tieneTramites = true;
            	}else{
            		tieneTramites = false;            		
            	}				
			}			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return tieneTramites;
    }
	    
    private Boolean validarAreasRoles(List<Rol> rolesusuario){		
			boolean tieneRoles = false;
			
			 if(rolFacade.isUserInRole(promotorBean.getUsuario(),"sujeto de control")){
				 tieneRoles = true;
				 return tieneRoles;
			 }
			
			if(mostrarTodasAreas){
				List<Area> listaAreas = new ArrayList<Area>();
				List<Area> listaAreasAux = new ArrayList<Area>();
				for (TreeNode tn : getPromotorBean().getSelectedNodes()) {
		             Area a = (Area) tn.getData(); 	             
		             if(a.getTipoArea().getSiglas().toUpperCase().equals("OT")){   
		            	 Area area = areaFacade.getArea(a.getId());
		            	 area.setIdUnidadArea(a.getIdUnidadArea());
		            	 listaAreas.add(area);
		             }                
		         }	
				
				listaAreasAux.addAll(listaAreas);
				boolean mismaDireccion = true;
				for(Area a1 : listaAreas){
					for(Area a2 : listaAreasAux){					
						
						if(!a1.getArea().getId().equals(a2.getArea().getId())){
							mismaDireccion = false;	
							break;
						}
					}
				}
				
				if(!mismaDireccion){					
					if(rolesusuario.size()!=0){
						tieneRoles = true;
					}			
				}
			}else{
				if(rolesusuario.size()!=0){
					tieneRoles = true;
				}
			}			
		return tieneRoles;
	}
    
    @EJB
	private DocumentosFacade documentosFacade;
    
    public StreamedContent getStream(Documents documento) throws Exception {
    	DefaultStreamedContent content = null;
    	 byte[] firmaSubsecretario = null;
    	firmaSubsecretario = documentosFacade.descargarDocumentoPorNombre(documento.getName().toString());
    	if (firmaSubsecretario != null) {
            content = new DefaultStreamedContent(new ByteArrayInputStream(firmaSubsecretario));
            content.setName(documento.getName());
        }else{
            JsfUtil.addMessageError("Error al descargar el documento.");
            throw new Exception("Error al descargar el documento.");
        }
    	return content;
	}

    public void cambiarTipoSubrogante(){
    	try {
    		
    		if(usuarioBean.getUsuario().getSubrogante() != null && usuarioBean.getUsuario().getSubrogante() == true){
    			setEncargado(false);
    		} 		
    				
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    public void cambiarTipoEncargado(){
    	try {
    		    		
    		if(encargado != null && encargado == true){
    			usuarioBean.getUsuario().setSubrogante(false);
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
	    
    
   
}
