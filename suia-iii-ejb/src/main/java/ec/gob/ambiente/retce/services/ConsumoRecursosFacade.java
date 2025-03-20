package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.ConsumoAgua;
import ec.gob.ambiente.retce.model.ConsumoCombustible;
import ec.gob.ambiente.retce.model.ConsumoEnergia;
import ec.gob.ambiente.retce.model.ConsumoRecursos;
import ec.gob.ambiente.retce.model.DetalleCatalogoGeneral;
import ec.gob.ambiente.retce.model.SubstanciasRetce;
import ec.gob.ambiente.retce.model.TipoProcesoConsumo;
import ec.gob.ambiente.suia.crud.facade.SecuenciasFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.utils.Constantes;

@Stateless
public class ConsumoRecursosFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private SecuenciasFacade secuenciasFacade;
	
	public String generarCodigoTramite() {
		try {
			return Constantes.SIGLAS_INSTITUCION + "-RETCE-CR-"
					+ secuenciasFacade.getCurrentYear()
					+ "-"
					+ secuenciasFacade.getNextValueDedicateSequence("MAAE-RETCE-CONSUMO", 4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumoRecursos> getConsumosByUsuario(String usuario) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("usuario", usuario);
		try {
			List<ConsumoRecursos> lista = (List<ConsumoRecursos>) crudServiceBean.findByNamedQuery(ConsumoRecursos.GET_POR_USUARIO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumoRecursos> getConsumosByInformacionProyecto(Integer idInformacionProyecto) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);
		try {
			List<ConsumoRecursos> lista = (List<ConsumoRecursos>) crudServiceBean.findByNamedQuery(ConsumoRecursos.GET_POR_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ConsumoRecursos getByInformacionProyectoAnio(Integer idInformacionProyecto, Integer anio) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idProyecto", idInformacionProyecto);
		parameters.put("anio", anio);
		try {
			List<ConsumoRecursos> lista = (List<ConsumoRecursos>) crudServiceBean.findByNamedQuery(ConsumoRecursos.GET_POR_ANIO_INFORMACION_PROYECTO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista.get(0);
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public ConsumoRecursos getById(Integer idConsumo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idConsumo", idConsumo);
		try {
			ConsumoRecursos consumo = (ConsumoRecursos) crudServiceBean.find(ConsumoRecursos.class, idConsumo);
			return consumo;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardarConsumo(ConsumoRecursos consumoRetce) {
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	public void eliminarConsumo(ConsumoRecursos consumoRecursos) {
		List<ConsumoCombustible> listaConsumoCombustibles = getConsumoCombustible(consumoRecursos.getId());
		if(listaConsumoCombustibles != null)
			eliminarConsumoCombustible(listaConsumoCombustibles);
		
		List<ConsumoEnergia> listaConsumoEnergia = getConsumoEnergia(consumoRecursos.getId()) ;
		if(listaConsumoEnergia != null)
			eliminarConsumoEnergia(listaConsumoEnergia);
		
		List<ConsumoAgua> listaConsumoAgua = getAprovechamientoAgua(consumoRecursos.getId());
		if(listaConsumoAgua != null)
			eliminarAprovechamientoAgua(listaConsumoAgua);
		
		crudServiceBean.saveOrUpdate(consumoRecursos);
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumoCombustible> getConsumoCombustible(Integer idConsumoRecurso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idConsumo", idConsumoRecurso);
		try {
			List<ConsumoCombustible> lista = (List<ConsumoCombustible>) crudServiceBean.findByNamedQuery(ConsumoCombustible.GET_POR_CONSUMO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardarConsumoCombustible(ConsumoCombustible consumoRetce) {
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	public void eliminarConsumoCombustible(List<ConsumoCombustible> consumoRetce) {
		for (ConsumoCombustible consumoCombustible : consumoRetce) {
			consumoCombustible.setEstado(false);
			for (TipoProcesoConsumo tipoProceso : consumoCombustible.getListaTipoProcesos()) {
				tipoProceso.setEstado(false);
				crudServiceBean.saveOrUpdate(tipoProceso);
			}
			
			if(consumoCombustible.getListaMediosVerificacion() != null && consumoCombustible.getListaMediosVerificacion().size() > 0){
				for (Documento documento : consumoCombustible.getListaMediosVerificacion()) {
					documento.setEstado(false);
					crudServiceBean.saveOrUpdate(documento);
				}
			}
		}
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	public void guardarTipoProceso(TipoProcesoConsumo tipoProceso) {
		crudServiceBean.saveOrUpdate(tipoProceso);
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalleCatalogoGeneral> getTiposProcesoConsumo(Integer idConsumo, Integer tipoConsumo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		try {
			List<DetalleCatalogoGeneral> lista = new ArrayList<>();
			
			switch (tipoConsumo) {
			case 1:
				parameters.put("idConsumoCombustible", idConsumo);
				lista = (List<DetalleCatalogoGeneral>) crudServiceBean.findByNamedQuery(TipoProcesoConsumo.GET_POR_ID_CONSUMO_COMBUSTIBLE,parameters);
				break;
			case 2:
				parameters.put("idConsumoElectrico", idConsumo);
				lista = (List<DetalleCatalogoGeneral>) crudServiceBean.findByNamedQuery(TipoProcesoConsumo.GET_POR_ID_CONSUMO_ENERGIA,parameters);
				break;
			case 3:
				parameters.put("idConsumoAgua", idConsumo);
				lista = (List<DetalleCatalogoGeneral>) crudServiceBean.findByNamedQuery(TipoProcesoConsumo.GET_POR_ID_CONSUMO_AGUA,parameters);
				break;
			default:
				break;
			}
			
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void guardarConsumoEnergia(ConsumoEnergia consumoRetce) {
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	public void eliminarConsumoEnergia(List<ConsumoEnergia> consumoRetce) {
		for (ConsumoEnergia consumo : consumoRetce) {
			for (TipoProcesoConsumo tipoProceso : consumo.getListaTipoProcesos()) {
				tipoProceso.setEstado(false);
				crudServiceBean.saveOrUpdate(tipoProceso);
			}
			
			if(consumo.getListaMediosVerificacion() != null && consumo.getListaMediosVerificacion().size() > 0){
				for (Documento documento : consumo.getListaMediosVerificacion()) {
					documento.setEstado(false);
					crudServiceBean.saveOrUpdate(documento);
				}
			}
			
			eliminarSustancia(consumo.getListaSustanciasRetce());
			
			consumo.setEstado(false);
		}
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumoEnergia> getConsumoEnergia(Integer idConsumoRecurso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idConsumo", idConsumoRecurso);
		try {
			List<ConsumoEnergia> lista = (List<ConsumoEnergia>) crudServiceBean.findByNamedQuery(ConsumoEnergia.GET_POR_CONSUMO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
	
	public void eliminarSustancia(List<SubstanciasRetce> listaSustancia) {
		for (SubstanciasRetce sustancia : listaSustancia) {
			if(sustancia.getDatosLaboratorio() != null){
				sustancia.getDatosLaboratorio().setEstado(false);
				crudServiceBean.saveOrUpdate(sustancia.getDatosLaboratorio());
			}
			
			if(sustancia.getDocumento() != null) {
				sustancia.getDocumento().setEstado(false);
				crudServiceBean.saveOrUpdate(sustancia.getDocumento());
			}
			
			sustancia.setEstado(false);
			crudServiceBean.saveOrUpdate(sustancia);
		}
	}
	
	public void guardarConsumoAgua(ConsumoAgua consumoRetce) {
		crudServiceBean.saveOrUpdate(consumoRetce);
	}
	
	public void eliminarAprovechamientoAgua(List<ConsumoAgua> consumoAgua) {
		for (ConsumoAgua consumo : consumoAgua) {
			for (TipoProcesoConsumo tipoProceso : consumo.getListaTipoProcesos()) {
				tipoProceso.setEstado(false);
				crudServiceBean.saveOrUpdate(tipoProceso);
			}
			
			if(consumo.getListaMediosVerificacion() != null && consumo.getListaMediosVerificacion().size() > 0){
				for (Documento documento : consumo.getListaMediosVerificacion()) {
					documento.setEstado(false);
					crudServiceBean.saveOrUpdate(documento);
				}
			}
			consumo.setEstado(false);
		}
		crudServiceBean.saveOrUpdate(consumoAgua);
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsumoAgua> getAprovechamientoAgua(Integer idConsumoRecurso) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("idConsumo", idConsumoRecurso);
		try {
			List<ConsumoAgua> lista = (List<ConsumoAgua>) crudServiceBean.findByNamedQuery(ConsumoAgua.GET_POR_CONSUMO,parameters);
			
			if (lista == null || lista.isEmpty()) {
				return null;
			} else {
				return lista;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
