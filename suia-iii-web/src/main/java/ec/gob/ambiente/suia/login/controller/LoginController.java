package ec.gob.ambiente.suia.login.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.log4j.Logger;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import com.captcha.botdetect.examples.jsf.facelets_captcha.view.FaceletsCaptcha;

import ec.gob.ambiente.rcoa.util.WebSessionBindingListener;
import ec.gob.ambiente.suia.administracion.enums.MenuEnum;
import ec.gob.ambiente.suia.administracion.facade.MenuFacade;
import ec.gob.ambiente.suia.administracion.facade.RolFacade;
import ec.gob.ambiente.suia.domain.Menu;
import ec.gob.ambiente.suia.domain.RolUsuario;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.dto.EntityMenu;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.login.bean.LoginBean;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.rcoa.facade.UsersBlacklistFacade;

@ManagedBean
@ViewScoped
public class LoginController implements Serializable {

	private static final long serialVersionUID = -5698357045771863162L;

	@Getter
	@Setter
	@ManagedProperty(value = "#{loginBean}")
	private LoginBean loginBean;
	@Getter
	@Setter
	private Usuario usuarioAux;

	@EJB
	private RolFacade rolFacade;
	@EJB
	private UsuarioFacade usuarioFacade;
	@EJB
	private MenuFacade menuFacade;
    @EJB
    private UsersBlacklistFacade userblacklistfacade;	
	private List<RolUsuario> listaRolUsuario;
	private static final Logger LOG = Logger.getLogger(LoginController.class);
	private static final int EXITO = 2;
	private static final int NO_EXITO = 3;
	private static final int NO_PERMISOS = 3;
	private static final int RESETEAR = 4;
	private static final int PENDIENTE_RESETEAR = 5;
	private static final int NUEVOS_RESETEAR = 6;
	private static final int SESION_INICIADA = 7;
	private static final int NUMERO_MAXIMO_SESIONES = 8;

	@Getter
	@Setter
	private String bandera;

	@PostConstruct
	private void dirigir() {
		try {
			if (loginBean.getUsuario() != null && loginBean.getUsuario().getId() != null) {
				if (Usuario.isUserInRole(loginBean.getUsuario(), "SENAGUA")
						&& !Usuario.isUserInRole(loginBean.getUsuario(), "sujeto de control")) {
					JsfUtil.redirectTo("/bandeja/inicio.jsf");
				} else {
					JsfUtil.redirectTo("/bandeja/bandejaTareas.jsf");
				}
			}
		} catch (Exception e) {
		}
	}

	public void login() {
		int validarIngreso = validarIngreso(0);
		if (validarIngreso == SESION_INICIADA) {
			JsfUtil.addMessageError("El usuario ya tiene una sesión iniciada.");
		} else if (validarIngreso == NUMERO_MAXIMO_SESIONES) {
			JsfUtil.addMessageError("Número máximo de sesiones superado.");
		} else if (validarIngreso == EXITO) {
			// AÑADO EL OBJETO HALLADO EN LA SESION
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			HttpSession httpSession = request.getSession(false);
			httpSession.setAttribute("usuarioSesion", loginBean.getUsuario());
			httpSession.setAttribute("usuarioSesion", loginBean.getUsuario());
			if (rolFacade.isUserInRole(loginBean.getUsuario(), "sujeto de control"))
				httpSession.setAttribute("terminos", 1);
			else
				httpSession.setAttribute("terminos", 0);
			// FIN ADICION DE OBJETO EN SESION
			cargarPermisos();
			request.getSession().getServletContext().setAttribute("usuarioLogin", loginBean.getUsuario());
			request.getSession().setAttribute("usuario", new WebSessionBindingListener(loginBean.getUsuario()));

		} else {
			if (validarIngreso == NO_EXITO) {
				JsfUtil.addMessageError("Usuario o contraseña incorrecto");
			} else if (validarIngreso == PENDIENTE_RESETEAR) {
				JsfUtil.addMessageError(
						"Ha solicitado cambio de contraseña y se encuentra pediente para la confirmación");
			} else if (validarIngreso == RESETEAR || validarIngreso == NUEVOS_RESETEAR) {
				if (loginBean.getNombreUsuario() != null) {
					FaceletsCaptcha f = new FaceletsCaptcha();
					f.setNombreUsuario(loginBean.getNombreUsuario());
				}
				JsfUtil.redirectTo("/resetearClaves.jsf");
			} else {
				JsfUtil.addMessageError("Este usuario no tiene asignado permisos");
			}
		}
	}

	public void loginSuplantacion(Usuario usuario) {
		setUsuarioAux(usuario);
		int validarIngreso = validarIngreso(1);
		if (validarIngreso == EXITO) {
			cargarPermisos();
		} else {
			if (validarIngreso == NO_EXITO) {
				JsfUtil.addMessageError("Usuario o contraseña incorrecto");
			} else {
				JsfUtil.addMessageError("Este usuario no tiene asignado permisos");
			}
		}
	}

	private int validarIngreso(int opcion) {
		int retorno = 0;
		
		if (opcion == 0) {
			Integer contador = 0;
			Usuario usuarioConsultado = usuarioFacade.buscarUsuario(loginBean.getNombreUsuario());

			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			String usuario = null;

			@SuppressWarnings("unchecked")
			HashMap<String, Object> listaUsuariosLogueados = (HashMap<String, Object>) request.getSession()
					.getServletContext().getAttribute("listadoUsuarios");
			if (usuarioConsultado.getNumeroSesiones() == null || usuarioConsultado.getNumeroSesiones().equals("")) {
				usuarioConsultado.setNumeroSesiones(1);
			}
			if (usuarioConsultado.getNumeroSesiones() > 1) {
				contador = 0;
				for (int i = 1; i <= usuarioConsultado.getNumeroSesiones(); i++) {
					try {
						usuario = (String) listaUsuariosLogueados.get(loginBean.getNombreUsuario() + "|" + i);
					} catch (Exception e) {
						usuario = null;
					}
					if (usuario != null) {
						contador += 1;
					}
				}
			} else {
				try {
					usuario = (String) listaUsuariosLogueados.get(loginBean.getNombreUsuario() + "|1");
				} catch (Exception e) {
					usuario = null;
				}
			}

			if (opcion == 0 && usuario != null && usuarioConsultado.getNumeroSesiones() == 1) {
				retorno = SESION_INICIADA;
			} else if (opcion == 0 && usuario != null && contador.equals(usuarioConsultado.getNumeroSesiones())) {
				retorno = NUMERO_MAXIMO_SESIONES;
			} else if (opcion == 0) {
				try {
					this.listaRolUsuario = usuarioFacade.listarPorLoginPassword(loginBean.getNombreUsuario(),
							JsfUtil.claveEncriptadaSHA1(loginBean.getPassword()));
					if (this.listaRolUsuario != null && !this.listaRolUsuario.isEmpty()) {
						//
						if (usuarioConsultado.getFechaExpiracionUsuario() != null) {
							Integer totalDias = diferenciaEnDias2(new Date(),
									usuarioConsultado.getFechaExpiracionUsuario());
							if (usuarioConsultado.getEstadoCodigoCapcha() == null) {
								usuarioConsultado.setEstadoCodigoCapcha(true);
							}
							if (totalDias == 0 || totalDias < 0) {
								retorno = RESETEAR;
							} else if (usuarioConsultado.getEstadoCodigoCapcha() == null
									&& (totalDias == 0 || totalDias < 0)) {
								retorno = NUEVOS_RESETEAR;
							} else if (!usuarioConsultado.getEstadoCodigoCapcha()) {
								retorno = PENDIENTE_RESETEAR;
							} else {
								retorno = EXITO;
								loginBean.setUsuario(
										usuarioFacade.buscarUsuarioPorId(this.listaRolUsuario.get(0).getIdUsuario()));
								if (loginBean.getUsuario() != null) {
									loginBean.setPassword(loginBean.getUsuario().getPassword());
								}
							}
						} else {
							if (usuarioConsultado.getEstadoCodigoCapcha() == null) {
								retorno = NUEVOS_RESETEAR;
							} else if (!usuarioConsultado.getEstadoCodigoCapcha()) {
								retorno = PENDIENTE_RESETEAR;
							} else {
								retorno = RESETEAR;
							}
						}

					} else {
						retorno = NO_PERMISOS;
					}
				} catch (ServiceException e) {
					loginBean.setNombreUsuario("");
					retorno = NO_EXITO;
					LOG.error(e);
				} catch (RuntimeException e) {
					retorno = NO_EXITO;
					LOG.error(e);
				}
			}		
		} else if (opcion == 1) {
			try {			
				this.listaRolUsuario = usuarioFacade.listarPorIdUsuario(getUsuarioAux().getId());
				
				if (this.listaRolUsuario != null && !this.listaRolUsuario.isEmpty()) {
					retorno = EXITO;
					loginBean.setUsuario(usuarioFacade.buscarUsuarioPorId(this.listaRolUsuario.get(0).getIdUsuario()));
				} else {
					retorno = NO_PERMISOS;
				}
			} catch (ServiceException e) {
				retorno = NO_EXITO;
				LOG.error(e);
			} catch (RuntimeException e) {
				retorno = NO_EXITO;
				LOG.error(e);
			}

		}
		return retorno;
	}

	public static int diferenciaEnDias2(Date fechaIn, Date fechaFinal) {
		GregorianCalendar fechaInicio = new GregorianCalendar();
		fechaInicio.setTime(fechaIn);
		GregorianCalendar fechaFin = new GregorianCalendar();
		fechaFin.setTime(fechaFinal);
		int dias = 0;
		if (fechaFin.get(Calendar.YEAR) == fechaInicio.get(Calendar.YEAR)) {
			dias = (fechaFin.get(Calendar.DAY_OF_YEAR) - fechaInicio.get(Calendar.DAY_OF_YEAR)) + 1;
		} else {
			int rangoAnyos = fechaFin.get(Calendar.YEAR) - fechaInicio.get(Calendar.YEAR);
			for (int i = 0; i <= rangoAnyos; i++) {
				int diasAnio = fechaInicio.isLeapYear(fechaInicio.get(Calendar.YEAR)) ? 366 : 365;
				if (i == 0) {
					dias = 1 + dias + (diasAnio - fechaInicio.get(Calendar.DAY_OF_YEAR));
				} else if (i == rangoAnyos) {
					dias = dias + fechaFin.get(Calendar.DAY_OF_YEAR);
				} else {
					dias = dias + diasAnio;
				}
			}
		}
		return dias;
	}

	private void cargarPermisos() {
		HttpServletRequest request = JsfUtil.getRequest();
		String paginaUrl = null;
		try {
			if (request.getUserPrincipal() != null) {
				request.logout();
			}
			request.login(loginBean.getNombreUsuario(), loginBean.getPassword());
			loginBean.setAuthenticated(true);
			loginBean.setTiempoSession(request.getSession().getMaxInactiveInterval());
			if (usuarioAux == null) {
				if (loginBean.getUsuario().getFechaExpiracionUsuario() != null) {
					Integer totalDias = diferenciaEnDias2(new Date(),
							loginBean.getUsuario().getFechaExpiracionUsuario());
					if (totalDias == 0 || totalDias < 0) {
						paginaUrl = "/resetearClaves.jsf";
						loginBean.setAuthenticated(false);
					} else {
						cargarMenuDinamico();
						paginaUrl = "/bandeja/bandejaTareas.jsf";
						if (Usuario.isUserInRole(loginBean.getUsuario(), "SENAGUA")
								&& !Usuario.isUserInRole(loginBean.getUsuario(), "sujeto de control"))
							paginaUrl = "/bandeja/inicio.jsf";
						if (Usuario.isUserInRole(loginBean.getUsuario(), "TECNICO VIABILIDAD TECNICA")
								&& (loginBean.getUsuario().getRolUsuarios().size())==1)
							paginaUrl = "/pages/rcoa/viabilidadTecnica/ingresoInformacionViabilidadTecnica.jsf";
					}
				} else {
					paginaUrl = "/resetearClaves.jsf";
					loginBean.setAuthenticated(false);
				}
			} else {
				cargarMenuDinamico();
				paginaUrl = "/bandeja/bandejaTareas.jsf";
				if (Usuario.isUserInRole(loginBean.getUsuario(), "SENAGUA")
						&& !Usuario.isUserInRole(loginBean.getUsuario(), "sujeto de control"))
					paginaUrl = "/bandeja/inicio.jsf";
			}

			JsfUtil.redirectTo(paginaUrl);

		} catch (ServletException | ServiceException ex) {
			LOG.error("Error en el login", ex);
			loginBean.setAuthenticated(false);
		}
	}

	private void cargarMenuDinamico() throws ServiceException {
		Boolean validacion = userblacklistfacade.listaUsuarios(loginBean.getUsuario().getNombre());
		List<EntityMenu> listaTodos = menuFacade.listarPorUsuarioRol(this.listaRolUsuario);
		List<EntityMenu> listaTodosNoMuestras = menuFacade.listarPorUsuarioRolSoloNodosFinales(this.listaRolUsuario);
		List<EntityMenu> listaEnvio = new ArrayList<EntityMenu>();
		for (EntityMenu listaMenu : listaTodos) {
			if ((loginBean.getUsuario().getListaAreaUsuario() != null
					|| loginBean.getUsuario().getListaAreaUsuario().isEmpty())
					&& loginBean.getUsuario().getListaAreaUsuario().get(0).getArea() != null
					&& loginBean.getUsuario().getListaAreaUsuario().get(0).getArea().getTipoArea().getId() == 3) {
				if (listaMenu.getEtiquetaMenu().equals("PROCESOS")
						|| listaMenu.getEtiquetaMenu().equals("Listado de procesos")) {
				} else if (!listaMenu.getEtiquetaMenu().equals("INGRESOS")
						&& !listaMenu.getEtiquetaMenu().equals("RGDP Físicos")) {
					listaEnvio.add(listaMenu);
				}
			} else
				listaEnvio.add(listaMenu);
		}
		listaTodos = new ArrayList<EntityMenu>();
		listaTodos.addAll(listaEnvio);
		listaEnvio.addAll(listaTodosNoMuestras);
		cargarPaginasSession(listaEnvio);
		loginBean.setModel(new DefaultMenuModel());
		Menu menuPadre = menuFacade.obtenerPorNemonico(MenuEnum.MENU_SUIA.getNemonico());
		for (EntityMenu menu : listaTodos) {
			if (menu.getIdMenuPadre() != null && menu.getIdMenuPadre().equals(menuPadre.getId().longValue())) {
				if (!menu.getNodoFinal()) {
					DefaultSubMenu subMenu = new DefaultSubMenu();
					subMenu.setLabel(menu.getEtiquetaMenu());
					subMenu.setIcon(menu.getIcono());
					subMenu.setStyle("font-size: 14px; font-weight: bold");
					cargarItems(menu, listaTodos, subMenu, null);
					//loginBean.getModel().addElement(subMenu);
					if (!validacion)
					{
						loginBean.getModel().addElement(subMenu);
					}
				} else {
					DefaultMenuItem menuItem1 = new DefaultMenuItem();
					menuItem1.setValue(menu.getEtiquetaMenu());
					menuItem1.setUrl(("S/N").equals(menu.getUrlMenu()) ? null : menu.getUrlMenu());
					menuItem1.setCommand(menu.getActionMenu().isEmpty() ? null : menu.getActionMenu());
					menuItem1.setIcon(menu.getIcono());
					//loginBean.getModel().addElement(menuItem1);
					if (!validacion)
					{
						loginBean.getModel().addElement(menuItem1);
					}						
				}
			}
		}
	}

	private void cargarPaginasSession(final List<EntityMenu> listaPermisos) {
		List<String> listaPaginas = new ArrayList<String>();
		for (EntityMenu en : listaPermisos) {
			listaPaginas.add(extraerPagina(en.getUrlMenu()));
		}
		JsfUtil.cargarObjetoSession("listaPermisos", listaPaginas);
	}

	private String extraerPagina(String url) {
		StringTokenizer str = new StringTokenizer(url == null ? "" : url, "/");
		String retorno = null;
		while (str.hasMoreTokens()) {
			retorno = str.nextToken();
		}
		return retorno;
	}

	private DefaultMenuItem cargarItems(EntityMenu men, List<EntityMenu> listaTodo, DefaultSubMenu menuPadre,
			DefaultMenuItem menuItem) {
		for (EntityMenu menu : listaTodo) {
			if (men.getIdMenu().equals(menu.getIdMenuPadre())) {
				if (menu.getNodoFinal()) {
					DefaultMenuItem menuItem1 = new DefaultMenuItem();
					menuItem1.setValue(menu.getEtiquetaMenu());
					menuItem1.setUrl(("S/N").equals(menu.getUrlMenu()) ? null : menu.getUrlMenu());
					menuItem1.setCommand(menu.getActionMenu().isEmpty() ? null : menu.getActionMenu());
					menuItem1.setIcon(menu.getIcono());
					menuPadre.getElements().add(menuItem1);
				} else {
					aniadirElementoHijo(menuPadre, menu, listaTodo, menuItem);
				}
			}
		}
		return menuItem;
	}

	private void aniadirElementoHijo(DefaultSubMenu menuPadre, EntityMenu menu, List<EntityMenu> listaTodo,
			DefaultMenuItem menuItem) {
		DefaultSubMenu submenuHijo = new DefaultSubMenu();
		submenuHijo.setLabel(menu.getEtiquetaMenu());
		menuPadre.getElements().add(submenuHijo);
		submenuHijo.setIcon(menu.getIcono());
		DefaultMenuItem menues = cargarItems(menu, listaTodo, submenuHijo, menuItem);
		if (menues != null) {
			submenuHijo.getElements().add(menues);
		}
	}

	@SuppressWarnings("deprecation")
	public static void killSession(String sid) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		HttpSessionContext sc = request.getSession().getSessionContext();

		HttpSession session = sc.getSession(sid);
		session.invalidate();
		JsfUtil.redirectTo("/index.html");
	}
}
