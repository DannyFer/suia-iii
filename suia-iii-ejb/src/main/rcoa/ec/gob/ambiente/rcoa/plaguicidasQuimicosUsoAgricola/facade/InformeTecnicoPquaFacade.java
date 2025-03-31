package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.InformeTecnicoPqua;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeTecnicoPquaFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigo(Area area) {
		try {			
			
			String nombreSecuencia = "INFORME_TECNICO_PQUA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 3);
			
			String secuencia = "";
			secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-INF-SGA-" + secuenciasFacade.getCurrentYear();
				
			return secuencia + "-" + valorSecuencial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public InformeTecnicoPqua guardar(InformeTecnicoPqua informe, Area area){
		if(informe.getNumeroInforme()==null)
			informe.setNumeroInforme(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	public InformeTecnicoPqua guardar(InformeTecnicoPqua informe) {
		return crudServiceBean.saveOrUpdate(informe);
	}

	@SuppressWarnings("unchecked")
	public List<InformeTecnicoPqua> getPorProyecto(Integer idProyecto, Integer nroRevision)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idProyecto);
		parameters.put("numeroRevision", nroRevision);
		
		List<InformeTecnicoPqua> lista = (List<InformeTecnicoPqua>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoPqua.GET_POR_PROYECTO_REVISION,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  null;
	}
}