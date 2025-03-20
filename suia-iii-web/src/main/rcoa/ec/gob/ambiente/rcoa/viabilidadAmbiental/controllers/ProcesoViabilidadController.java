package ec.gob.ambiente.rcoa.viabilidadAmbiental.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.rcoa.viabilidadAmbiental.facade.ViabilidadCoaFacade;
import ec.gob.ambiente.suia.administracion.facade.AreaFacade;
import ec.gob.ambiente.suia.administracion.facade.OrganizacionFacade;
import ec.gob.ambiente.suia.mensajenotificacion.facade.MensajeNotificacionFacade;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.usuario.facade.UsuarioFacade;
import ec.gob.ambiente.suia.utils.Constantes;
import ec.gob.ambiente.suia.utils.JsfUtil;

@ManagedBean
@ViewScoped
public class ProcesoViabilidadController {
	
	private static final Logger LOG = Logger.getLogger(ProcesoViabilidadController.class);
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@EJB
	private AreaFacade areaFacade;
	
	@EJB
	private UsuarioFacade usuarioFacade;
	
	@EJB
	private MensajeNotificacionFacade mensajeNotificacionFacade;
	
	@EJB
	private OrganizacionFacade organizacionFacade;
	
	@EJB
	private ViabilidadCoaFacade viabilidadCoaFacade;
	
	public boolean iniciarProceso(ProyectoLicenciaCoa proyecto){
		try {
			Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
			parametros.put("operador", JsfUtil.getLoggedUser().getNombre());
			parametros.put("idProyecto", proyecto.getId());
			parametros.put("codigoProyecto", proyecto.getCodigoUnicoAmbiental());
			
			Boolean intersecaSnap = proyecto.getInterecaSnap();
			Boolean intersecaForestal = false; 
			if(proyecto.getInterecaBosqueProtector())
				intersecaForestal = true;
			else if(proyecto.getInterecaPatrimonioForestal())
				intersecaForestal = true;			
			
			parametros.put("intersecaSnap", intersecaSnap);
			parametros.put("intersecaForestal", intersecaForestal);
			
			procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), Constantes.RCOA_PROCESO_VIABILIDAD, proyecto.getCodigoUnicoAmbiental(), parametros);
			
			return true;
		} catch (Exception e) {
			JsfUtil.addMessageError("Ha ocurrido un error. Por favor comuníquese con Mesa de Ayuda.");
			LOG.error(e.getMessage()+" "+e.getCause().getMessage());
			return false;
		}
	}
}
