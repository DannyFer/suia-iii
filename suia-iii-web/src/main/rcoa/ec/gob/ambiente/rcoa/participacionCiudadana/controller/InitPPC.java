package ec.gob.ambiente.rcoa.participacionCiudadana.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ec.gob.ambiente.exceptions.JbpmException;
import ec.gob.ambiente.suia.proceso.facade.ProcesoFacade;
import ec.gob.ambiente.suia.utils.JsfUtil;
import lombok.Getter;
import lombok.Setter;

@ManagedBean
@ViewScoped
public class InitPPC {
	
	@EJB
	private ProcesoFacade procesoFacade;
	
	@Getter
	@Setter
	private String operador;
	
	@Getter
	@Setter
	private Integer idProyecto;
	
	@Getter
	@Setter
	private String tramite;
	
	@Getter
	@Setter
	private String numeroFacilitadores;
	
	@Getter
	@Setter
	private Boolean facilitadorAdicional=false;
	
	public void iniciar()
	{
		Map<String, Object> parametros = new ConcurrentHashMap<String, Object>();
		parametros.put("operador", operador);
		parametros.put("tramite",tramite);					
		parametros.put("idProyecto", idProyecto);
		parametros.put("numeroFacilitadores", numeroFacilitadores);
		parametros.put("facilitadorAdicional", facilitadorAdicional);
		
		
		try {
			Long idProceso = procesoFacade.iniciarProceso(JsfUtil.getLoggedUser(), "rcoa.ProcesoParticipacionCiudadana", tramite, parametros);
		} catch (JbpmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
