package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class GestorDesechosEliminacionFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public GestorDesechosEliminacion guardarEliminacion(GestorDesechosEliminacion eliminacion) {
		return crudServiceBean.saveOrUpdate(eliminacion);
	}
	
	@SuppressWarnings("unchecked")
	public List<GestorDesechosEliminacion> listarGestorEliminacion(GestorDesechosPeligrosos gestor) {
		List<GestorDesechosEliminacion> listagestor= new ArrayList<GestorDesechosEliminacion>();
		try
		{	
			listagestor=(ArrayList<GestorDesechosEliminacion>) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosEliminacion g where g.gestorDesechosPeligrosos=:gestor and g.estado=true").
					setParameter("gestor", gestor).
					getResultList();
			return listagestor;
		}
		catch(Exception e)
		{
			return listagestor;			
		}
	}
	
	public GestorDesechosEliminacion gestorDesechoEliminacion(GestorDesechosPeligrosos gestor,EliminacionDesecho desecho) {
		GestorDesechosEliminacion gestordesecho= new GestorDesechosEliminacion();
		try
		{
			gestordesecho=(GestorDesechosEliminacion) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosEliminacion g where g.gestorDesechosPeligrosos=:gestor and g.eliminacionDesecho=:desecho and g.estado=true").
					setParameter("gestor", gestor).
					setParameter("desecho", desecho).
					getResultList().get(0);
			return gestordesecho;
		}
		catch(Exception e)
		{
			return gestordesecho;
		}
	}
	
	public GestorDesechosEliminacion gestorDesechoEliminacion(GestorDesechosPeligrosos gestor,DesechoPeligroso desecho) {
		GestorDesechosEliminacion gestordesecho= new GestorDesechosEliminacion();
		try
		{
			gestordesecho=(GestorDesechosEliminacion) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosEliminacion g where g.gestorDesechosPeligrosos=:gestor and g.desechoPeligroso=:desecho and g.estado=true").
					setParameter("gestor", gestor).
					setParameter("desecho", desecho).
					getResultList().get(0);
			return gestordesecho;
		}
		catch(Exception e)
		{
			return gestordesecho;
		}
	}
	
	public DesechoPeligroso obtenerDesecho(GestorDesechosEliminacion gestor) throws ServiceException {
		DesechoPeligroso desechos = new DesechoPeligroso();
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery("select d.wada_id from retce.waste_manager_elimination a "
					+ "inner join suia_iii.waste_disposal b on a.wadi_id=b.wadi_id "
					+ "inner join suia_iii.waste_disposal_receipt c on b.wadr_id = c.wadr_id "
					+ "inner join suia_iii.receipt_hazardous_waste d on c.rehw_id = d.rehw_id "
					+ "where a.wadi_id="+gestor.getEliminacionDesecho().getId()+" and a.hwm_id="+gestor.getGestorDesechosPeligrosos().getId()+"");
			Object obj =new Object();
			if(sql.getResultList().size()>0)
			{
				obj=sql.getSingleResult();

				desechos = (DesechoPeligroso) crudServiceBean.getEntityManager()
						.createQuery("From DesechoPeligroso d where d.id = :id ").setParameter("id", obj)
						.getSingleResult();
			}
		} catch (Exception e) {
			System.out.printf("Error al obtener el desecho.", e);
		}
		return desechos;
	}
	
	@SuppressWarnings("unchecked")
	public List<DesechoPeligroso> getDesechoByART(Integer apte_id) throws ServiceException {
		List<DesechoPeligroso> listDesechos = new ArrayList<DesechoPeligroso>();
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery("select wada.wada_id "
				+ "from suia_iii.approval_technical_requirements apte "
				+ "inner join suia_iii.waste_disposal_receipt wadr on(apte.apte_id=wadr.apte_id) "
				+ "inner join suia_iii.waste_disposal wadi on(wadr.wadr_id=wadi.wadr_id) "
				+ "inner join suia_iii.waste_disposal_types wdty on(wdty.wdty_id=wadi.wdty_id) "
				+ "inner join suia_iii.receipt_hazardous_waste rehw on(rehw.rehw_id=wadr.rehw_id) "
				+ "inner join general_catalogs geca on (geca.geca_id=rehw.rehw_physical_state_id) "
				+ "inner join suia_iii.phisical_state_types psty on (psty.psty_id=rehw.rehw_physical_state_id) "
				+ "inner join suia_iii.waste_dangerous wada on(wada.wada_id=rehw.wada_id) "
				+ "where wadi.wadi_status=true and wada.wada_status=true and wadr.wadr_status=true "
				+ "and apte.apte_id="+apte_id
				+ " group by wada.wada_id order by 1");
			List<Object> obj =new ArrayList<Object>();
			if(sql.getResultList().size()>0) {
				obj=sql.getResultList();
				for (Object object : obj) {
					DesechoPeligroso desechos = (DesechoPeligroso) crudServiceBean.getEntityManager()
							.createQuery("From DesechoPeligroso d where d.id = :id ").setParameter("id", object)
							.getSingleResult();
					listDesechos.add(desechos);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			System.out.printf("Error al obtener el desecho.", e);
		}
		return listDesechos;
	}
	
}
