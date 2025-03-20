package ec.gob.ambiente.core.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ec.gob.ambiente.suia.domain.Usuario;

/**
 * Enables messages to be rendered on different pages from which they were set.
 * 
 * After each phase where messages may be added, this moves the messages from
 * the page-scoped FacesContext to the session-scoped session map.
 * 
 * Before messages are rendered, this moves the messages from the session-scoped
 * session map back to the page-scoped FacesContext.
 * 
 * Only global messages, not associated with a particular component, are moved.
 * Component messages cannot be rendered on pages other than the one on which
 * they were added.
 * 
 * To enable multi-page messages support, add a <code>lifecycle</code> block to
 * your faces-config.xml file. That block should contain a single
 * <code>phase-listener</code> block containing the fully-qualified classname of
 * this file.
 * 
 * @author Jesse Wilson jesse[AT]odel.on.ca
 * @secondaryAuthor Lincoln Baxter III lincoln[AT]ocpsoft.com
 */
public class MultiPageMessagesSupport implements PhaseListener {

	private static final long serialVersionUID = 1250469273857785274L;
	private static final String sessionToken = "MULTI_PAGE_MESSAGES_SUPPORT";

	@Override
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/*
	 * Check to see if we are "naturally" in the RENDER_RESPONSE phase. If we
	 * have arrived here and the response is already complete, then the page is
	 * not going to show up: don't display messages yet.
	 */
	// TODO: Blog this (MultiPageMessagesSupport)
	@Override
	public void beforePhase(final PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
		this.saveMessages(facesContext);
		if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
			if (!facesContext.getResponseComplete()) {
				this.restoreMessages(facesContext);
			}
		}
	}

	/*
	 * Save messages into the session after every phase.
	 */
	@Override
	public void afterPhase(final PhaseEvent event) {
		validarSesion(event);
		if (!PhaseId.RENDER_RESPONSE.equals(event.getPhaseId())) {
			FacesContext facesContext = event.getFacesContext();
			this.saveMessages(facesContext);
		}
	}

	@SuppressWarnings("unchecked")
	private int saveMessages(final FacesContext facesContext) {
		List<FacesMessage> messages = new ArrayList<FacesMessage>();
		for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();) {
			messages.add(iter.next());
			iter.remove();
		}

		if (messages.size() == 0) {
			return 0;
		}

		Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
		List<FacesMessage> existingMessages = (List<FacesMessage>) sessionMap.get(sessionToken);
		if (existingMessages != null) {
			existingMessages.addAll(messages);
		} else {
			sessionMap.put(sessionToken, messages);
		}
		return messages.size();
	}

	@SuppressWarnings("unchecked")
	private int restoreMessages(final FacesContext facesContext) {
		Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
		List<FacesMessage> messages = (List<FacesMessage>) sessionMap.remove(sessionToken);

		if (messages == null) {
			return 0;
		}

		int restoredCount = messages.size();
		for (Object element : messages) {
			facesContext.addMessage(null, (FacesMessage) element);
		}
		return restoredCount;
	}
	
	public void validarSesion(PhaseEvent event) {
		FacesContext facesContext = event.getFacesContext();
        FacesContext context = FacesContext.getCurrentInstance();
        String currentPage = facesContext.getViewRoot().getViewId();
        NavigationHandler nh = facesContext.getApplication().getNavigationHandler();
        boolean isLoginPage = facesContext.getViewRoot().getViewId().lastIndexOf("start") > -1 ? true : false;
        
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        HttpSession httpSession = request.getSession(false);
        Usuario usuarioLoggin = new Usuario();
        try {
        	usuarioLoggin = (Usuario) httpSession.getAttribute("usuarioSesion");
        } catch (Exception e) {
        	usuarioLoggin = null;
		}
		// condición para cuando se require el reseteo de la contraseña
        if (!isLoginPage
				&& (usuarioLoggin == null)
				&& !(currentPage.equals("/resetearClaves.xhtml")
						|| currentPage.equals("/claveCambiada.xhtml")
						|| currentPage.equals("/claveExitosaCambio.xhtml")
						|| currentPage.equals("/claveRecuperada.xhtml")
						|| currentPage.equals("/claverReseteada.xhtml")
						|| currentPage.equals("/recuperarNuevaClave.xhtml")
						|| currentPage.equals("/usuarioRegistrado.xhtml")
						|| currentPage.equals("/promotor.xhtml") 
						|| currentPage.equals("/ResumenFichas.xhtml")
						|| currentPage.equals("/publicarEstudio.xhtml")
						|| currentPage.equals("/CatalogoActividadesCIIU.xhtml")
						|| currentPage.equals("/pages/rcoa/simulador/inicioSimulador.xhtml")
						|| currentPage.equals("/pages/rcoa/simulador/registroProyectoSimulador.xhtml")
						|| currentPage.equals("/validacion/validarCertificado.xhtml")
						|| currentPage.equals("/validacion/validarCertificadoAmbiental.xhtml")
						|| currentPage.equals("/demo_agua.xhtml")
						)) {
			nh.handleNavigation(facesContext, null,
					"/start?faces-redirect=true");
			// System.out.println("Estado: el usuario no esta Logeado y pretende entrar al sistema.");
		}
        try {
            /*if ((currentPage.equals("/pages/reportes/gerenciales/Falta.xhtml")
                    && usuarioLoggin.getFkRolId().getRolNombre().equals("ADMINISTRADOR")) {
                nh.handleNavigation(facesContext, null, "/index?faces-redirect=true");
                System.out.println("Unicamente puedes acceder a las páginas para el perfil de Administrador");
            }*/
        } catch (NullPointerException e) {
            System.out.println("Exception del Listener: " + e);
        }
	}
}
