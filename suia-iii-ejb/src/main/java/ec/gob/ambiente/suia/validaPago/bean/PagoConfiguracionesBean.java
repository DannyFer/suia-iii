package ec.gob.ambiente.suia.validaPago.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import ec.gob.ambiente.suia.domain.PagoConfiguraciones;
import ec.gob.ambiente.suia.validaPago.facade.PagoConfiguracionesFacade;


@ApplicationScoped
public class PagoConfiguracionesBean {
	private Map<String, String> pagoConfiguraciones = null;
	
	@EJB
	private PagoConfiguracionesFacade pagoConfigFacade;
	
	public String getPagoConfigValue(String key) {
		Map<String, String> config = loadPaymentConfig();
		if(config != null && config.containsKey(key)) {
			return config.get(key);
		}
		return null;
	}
	
	// método para invocar claves del facade
	private Map<String, String> loadPaymentConfig() {
		if(pagoConfiguraciones == null) {
			pagoConfiguraciones = new HashMap<String, String>();
			try {
				List<PagoConfiguraciones> pagoConfig = pagoConfigFacade.getPagoConfiguraciones();
				for(PagoConfiguraciones pagoTmp : pagoConfig) {
					pagoConfiguraciones.put(pagoTmp.getPacoDescripcion(), pagoTmp.getPacoValue());
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return pagoConfiguraciones;
	}
}