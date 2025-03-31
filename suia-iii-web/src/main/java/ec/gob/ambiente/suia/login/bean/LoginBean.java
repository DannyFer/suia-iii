package ec.gob.ambiente.suia.login.bean;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import lombok.Setter;

import org.primefaces.model.menu.MenuModel;

import ec.gob.ambiente.integracion.facade.IntegracionFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.domain.Organizacion;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 3777462143683568551L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LoginBean.class);

	@Getter
	@Setter
	int tiempoSession = 900000000;

	@Setter
	@Getter
	private Usuario usuario;

	private String infoToShow;
	
	@Getter
	@Setter
	private boolean reactivarContrasena;

	@Getter
	@Setter
	private String nombreUsuario;

	@Getter
	@Setter
	private String password;

	@Getter
	@Setter
	private boolean authenticated;

	@Getter
	@Setter
	private MenuModel model;

	@EJB
	private IntegracionFacade integracionFacade;

	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@Getter
	@Setter
	private  String mensajeRea="";

	public Usuario getUsuario() {
		if (usuario == null) {
			usuario = new Usuario();
		}
		return usuario;
	}

	public String getInfoToShow() {
		infoToShow = usuario.getNombre();
		

		if (usuario.getPersona() != null) {
			try {
				if (organizacionFacade.tieneOrganizacionPorPersona(usuario.getPersona())) {
					Organizacion organizacion = organizacionFacade.buscarPorPersona(usuario.getPersona(),
							usuario.getNombre());
					if (organizacion != null && organizacion.getNombre() != null
							&& organizacion.getRuc().trim().equals(usuario.getNombre())) {
						Date fechaActual= new Date();
						if (usuario.getFechaExpiracionUsuario()!=null){			
						Integer resultadoFecha=diferenciaEnDias2(fechaActual,usuario.getFechaExpiracionUsuario());
						if (resultadoFecha==0 || resultadoFecha<0){
							reactivarContrasena=true;
							mensajeRea="SU CONTRASEÑA HA EXPIRADO EL DIA DE HOY";
						}else{
							if (resultadoFecha<6){
								mensajeRea="SU CONTRASEÑA SE ENCUENTRA POR EXPIRAR EN :" +resultadoFecha+ " DÍAS";
							}
						}
				}else{
					reactivarContrasena=true;
					mensajeRea="SU CONTRASEÑA HA EXPIRADO EL DIA DE HOY";
				}
						
						return organizacion.getNombre();
					}
				}
			} catch (ServiceException e) {
				LOG.error("Error al obtener la organización de la persona.", e);
			}
			if (usuario.getPersona().getNombre() != null && !usuario.getPersona().getNombre().trim().isEmpty()) {
				infoToShow = usuario.getPersona().getNombre().trim();
			}
		}
//		if (usuario.getRenovarPassusuario()!=null){
//		if (usuario.getRenovarPassusuario()){			
			Date fechaActual= new Date();
			if (usuario.getFechaExpiracionUsuario()!=null){			
			Integer resultadoFecha=diferenciaEnDias2(fechaActual,usuario.getFechaExpiracionUsuario());
			if (resultadoFecha==0 || resultadoFecha<0){
				reactivarContrasena=true;
				mensajeRea="SU CONTRASEÑA HA EXPIRADO EL DIA DE HOY";
			}else{
				if (resultadoFecha<6){
					reactivarContrasena=true;
					mensajeRea="SU CONTRASEÑA SE ENCUENTRA POR EXPIRAR EN :" +resultadoFecha+ " DÍAS";
				}
			}
	}else{
		reactivarContrasena=true;
		mensajeRea="SU CONTRASEÑA HA EXPIRADO EL DIA DE HOY";
	}
			
//		}
//		}
		return infoToShow;
	}

//	public static int diferenciaEnDias2(Date fechaMayor, Date fechaMenor) {
//		long diferenciaEn_ms = fechaMayor.getTime() - fechaMenor.getTime();
//		long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
//		return (int) dias;
//	}
	
	public static int diferenciaEnDias2(Date fechaIn, Date fechaFinal ){
    	GregorianCalendar fechaInicio= new GregorianCalendar();
    	fechaInicio.setTime(fechaIn);
    	GregorianCalendar fechaFin= new GregorianCalendar();
    	fechaFin.setTime(fechaFinal);
    	int dias = 0;
    	if(fechaFin.get(Calendar.YEAR)==fechaInicio.get(Calendar.YEAR)){
    	dias =(fechaFin.get(Calendar.DAY_OF_YEAR)- fechaInicio.get(Calendar.DAY_OF_YEAR))+1;
    	}else{
    	int rangoAnyos = fechaFin.get(Calendar.YEAR) - fechaInicio.get(Calendar.YEAR);
    	for(int i=0;i<=rangoAnyos;i++){
    	int diasAnio = fechaInicio.isLeapYear(fechaInicio.get(Calendar.YEAR)) ? 366 : 365;
    	if(i==0){
    	dias=1+dias +(diasAnio- fechaInicio.get(Calendar.DAY_OF_YEAR));
    	}else	if(i==rangoAnyos){
    	dias=dias +fechaFin.get(Calendar.DAY_OF_YEAR);
    	}else{
    	dias=dias+diasAnio;
    	}
    	}
    	}
    	return dias;
    	}
	
	
	public void clearCredentials() {
		usuario = null;
		authenticated = false;
	}

	public void cerrarSession() {
		HttpServletRequest request = JsfUtil.getRequest();
		request.getSession().invalidate();
		JsfUtil.redirectTo("/index.html");
	}

	public void cerrarSessionExpirada() {
		HttpServletRequest request = JsfUtil.getRequest();
		request.getSession().invalidate();
		JsfUtil.redirectTo("/errors/viewExpired.jsf");
	}

	public void cerrarSessionRedireccionar(String url) {
		HttpServletRequest request = JsfUtil.getRequest();
		request.getSession().invalidate();
		JsfUtil.redirectTo(url);
	}

	public boolean isUserInRole(String roles) {
		String[] rolArray = roles.trim().split(",");
		for (String rol : rolArray) {
			if (Usuario.isUserInRole(getUsuario(), "admin") || Usuario.isUserInRole(getUsuario(), rol.trim()))
				return true;
		}
		return false;
	}
}
