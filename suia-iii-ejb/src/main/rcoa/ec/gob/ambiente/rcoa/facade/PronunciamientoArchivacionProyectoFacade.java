package ec.gob.ambiente.rcoa.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.model.PronunciamientoArchivacionProyecto;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.utils.Constantes;


@Stateless
public class PronunciamientoArchivacionProyectoFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;

	private String generarCodigo() {
		try {			
			String nombreSecuencia="OFICIO_PROHIBICION_ACTIVIDAD_"+secuenciasFacade.getCurrentYear();
			String secuencia = Constantes.SIGLAS_INSTITUCION + "-SUIA-RA-"+Constantes.SIGLAS_DIRECCION_REGULARIZACION_AMBIENTAL+"-"+secuenciasFacade.getCurrentYear();
			return secuencia+"-" + secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia,5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public PronunciamientoArchivacionProyecto guardar(PronunciamientoArchivacionProyecto oficio){
		if(oficio.getCodigo()==null)
			oficio.setCodigo(generarCodigo());		
		return crudServiceBean.saveOrUpdate(oficio);
	}

	@SuppressWarnings("unchecked")
	public PronunciamientoArchivacionProyecto obtenerPorProyecto(Integer idProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);

		try {
			List<PronunciamientoArchivacionProyecto> lista = (List<PronunciamientoArchivacionProyecto>) crudServiceBean
					.findByNamedQuery(PronunciamientoArchivacionProyecto.GET_POR_PROYECTO, parameters);

			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}	
	}

}
