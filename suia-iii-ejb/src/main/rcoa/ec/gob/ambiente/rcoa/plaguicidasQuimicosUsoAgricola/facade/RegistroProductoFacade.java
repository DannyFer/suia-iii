package ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.rcoa.plaguicidasQuimicosUsoAgricola.model.RegistroProducto;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class RegistroProductoFacade {
	
	@EJB
	private CrudServiceBean crudServiceBean;

	public RegistroProducto guardar(RegistroProducto registro){
		return crudServiceBean.saveOrUpdate(registro);
	}
	
	@SuppressWarnings("unchecked")
	public List<RegistroProducto> getRegistroProducto(){
		List<RegistroProducto> lista = new ArrayList<RegistroProducto>();
		try {
			
			lista = (ArrayList<RegistroProducto>) crudServiceBean
					.findByNamedQuery(RegistroProducto.GET_REGISTROS, null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<RegistroProducto> getPorMesIngreso(Date fechaMesIngreso){
		List<RegistroProducto> lista = new ArrayList<RegistroProducto>();
		try {
			
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("fechaMesIngreso", fechaMesIngreso);

			lista = (ArrayList<RegistroProducto>) crudServiceBean
					.findByNamedQuery(RegistroProducto.GET_POR_MES_INGRESO, parameters);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return lista;
	}

}
