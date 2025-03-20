package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformacionProyectoEia;
import ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.InformeTecnicoEsIA;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Area;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class InformeTecnicoEsIAFacade {
	
	@EJB
    private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	private String generarCodigo(Area area) {
		try {			
			
			String nombreSecuencia = "INFORME_TECNICO_ESIA_"
					+ secuenciasFacade.getCurrentYear() + "_"
					+ area.getAreaAbbreviation();
			
			String valorSecuencial = secuenciasFacade.getNextValueDedicateSequence(nombreSecuencia, 6);
			
			String secuencia = "";
			if(area.getTipoArea().getSiglas().equals("PC")){
				if(area.getArea() != null){
					secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getArea().getAreaAbbreviation() + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
				}else{
					secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
				}			
				
			}else{
				secuencia = Constantes.SIGLAS_INSTITUCION + "-" + area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
			}
			
			if(area.getTipoArea().getId()==3){
				secuencia = area.getAreaAbbreviation() + "-" + secuenciasFacade.getCurrentYear();
				return valorSecuencial + "-" + secuencia;
			}
				
			return secuencia + "-" + valorSecuencial;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public InformeTecnicoEsIA guardar(InformeTecnicoEsIA informe, Area area){
		if(informe.getCodigoInforme()==null)
			informe.setCodigoInforme(generarCodigo(area));		
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	public InformeTecnicoEsIA guardar(InformeTecnicoEsIA informe) {
		return crudServiceBean.saveOrUpdate(informe);
	}
	
	@SuppressWarnings("unchecked")
	public InformeTecnicoEsIA obtenerPorEstudioTarea(InformacionProyectoEia estudio, Integer idTarea)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		parameters.put("idTarea", idTarea);
		
		List<InformeTecnicoEsIA> lista = (List<InformeTecnicoEsIA>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoEsIA.GET_POR_ESTUDIO_TAREA,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}

	@SuppressWarnings("unchecked")
	public List<InformeTecnicoEsIA> obtenerInformesPorEstudio(InformacionProyectoEia estudio)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		
		List<InformeTecnicoEsIA> lista = (List<InformeTecnicoEsIA>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoEsIA.GET_POR_ESTUDIO,
							parameters);
		if(lista.size() > 0 ){
			return lista;
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public InformeTecnicoEsIA obtenerPorEstudioTipoInforme(InformacionProyectoEia estudio, Integer tipoInforme)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		parameters.put("tipoInforme", tipoInforme);
		
		List<InformeTecnicoEsIA> lista = (List<InformeTecnicoEsIA>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoEsIA.GET_POR_ESTUDIO_INFORME,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public InformeTecnicoEsIA obtenerPorId(Integer idInforme)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idInforme", idInforme);
		
		List<InformeTecnicoEsIA> lista = (List<InformeTecnicoEsIA>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoEsIA.GET_POR_ID,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
	
	@SuppressWarnings("unchecked")
	public InformeTecnicoEsIA obtenerPorEstudioTipoInformeNroRevision(InformacionProyectoEia estudio, Integer tipoInforme, Integer nroRevision)
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idEstudio", estudio.getId());
		parameters.put("tipoInforme", tipoInforme);
		parameters.put("numeroRevision", nroRevision);
		
		List<InformeTecnicoEsIA> lista = (List<InformeTecnicoEsIA>) crudServiceBean
					.findByNamedQuery(
							InformeTecnicoEsIA.GET_POR_ESTUDIO_INFORME_REVISION,
							parameters);
		if(lista.size() > 0 ){
			return lista.get(0);
		}
		return  null;
	}
}