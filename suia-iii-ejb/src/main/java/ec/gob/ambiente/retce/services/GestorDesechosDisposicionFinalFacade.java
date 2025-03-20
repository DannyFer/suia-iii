package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.GestorDesechosDisposicionFinal;
import ec.gob.ambiente.retce.model.GestorDesechosEliminacion;
import ec.gob.ambiente.retce.model.GestorDesechosPeligrosos;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DesechoPeligroso;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.AprobacionRequisitosTecnicos;
import ec.gob.ambiente.suia.domain.aprobacionrequisitostecnicos.EliminacionDesecho;
import ec.gob.ambiente.suia.exceptions.ServiceException;

@Stateless
public class GestorDesechosDisposicionFinalFacade {

	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public GestorDesechosDisposicionFinal guardarDisFinal(GestorDesechosDisposicionFinal disFinal) {
		return crudServiceBean.saveOrUpdate(disFinal);
	}
	
	@SuppressWarnings("unchecked")
	public List<GestorDesechosDisposicionFinal> listarGestorDisfinal(GestorDesechosPeligrosos gestor) {
		List<GestorDesechosDisposicionFinal> listagestor= new ArrayList<GestorDesechosDisposicionFinal>();
		try
		{	
			listagestor=(ArrayList<GestorDesechosDisposicionFinal>) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosDisposicionFinal g where g.gestorDesechosPeligrosos=:gestor and g.estado=true").
					setParameter("gestor", gestor).
					getResultList();
			return listagestor;
		}
		catch(Exception e)
		{
			return listagestor;			
		}
	}
	
	public GestorDesechosDisposicionFinal gestorDesechoDisFinal(GestorDesechosPeligrosos gestor,EliminacionDesecho desecho) {
		GestorDesechosDisposicionFinal gestordesecho= new GestorDesechosDisposicionFinal();
		try
		{
			gestordesecho=(GestorDesechosDisposicionFinal) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosDisposicionFinal g where g.gestorDesechosPeligrosos=:gestor and g.eliminacionDesecho=:desecho and g.estado=true").
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
	
	public GestorDesechosDisposicionFinal gestorDesechoDisFinal(GestorDesechosPeligrosos gestor,DesechoPeligroso desecho) {
		GestorDesechosDisposicionFinal gestordesecho= new GestorDesechosDisposicionFinal();
		try
		{
			gestordesecho=(GestorDesechosDisposicionFinal) crudServiceBean.
					getEntityManager().
					createQuery("select g from GestorDesechosDisposicionFinal g where g.gestorDesechosPeligrosos=:gestor and g.desechoPeligroso=:desecho and g.estado=true").
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
	
	@SuppressWarnings("unchecked")
	public List<EliminacionDesecho> getDesechosPorTipoElimiancion(int idModalidad,
			AprobacionRequisitosTecnicos aprobacionRequisitosTecnicos) throws ServiceException {
		StringBuilder query = new StringBuilder();

		query.append("select wd.* from suia_iii.waste_disposal wd ");
		query.append("inner join suia_iii.waste_disposal_types wdt on wd.wdty_id=wdt.wdty_id ");
		query.append("inner join suia_iii.waste_disposal_receipt wdr on wdr.wadr_id=wd.wadr_id ");
		query.append("inner join suia_iii.receipt_hazardous_waste rhw on wdr.rehw_id=rhw.rehw_id ");
		query.append("inner join suia_iii.waste_dangerous wdg on wdg.wada_id=rhw.wada_id ");
		query.append("where wdt.wdty_code_modality=" + idModalidad + " and wdr.apte_id="
				+ aprobacionRequisitosTecnicos.getId()
				+ " and wd.wadi_status=true and wdg.wada_status=true and wdr.wadr_status=true order by wdg.wada_key");

		List<EliminacionDesecho> lista = (List<EliminacionDesecho>) crudServiceBean.findNativeQuery(query.toString(),
				EliminacionDesecho.class);

		return lista;
	}
	
	public DesechoPeligroso obtenerDesecho(GestorDesechosDisposicionFinal gestor) throws ServiceException {
		DesechoPeligroso desechos = new DesechoPeligroso();
		try {
			Query sql = crudServiceBean.getEntityManager().createNativeQuery("select d.wada_id from retce.waste_manager_final_disposition a "
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
	
	public AprobacionRequisitosTecnicos getArtPorTipoElimiancion(EliminacionDesecho wadi) throws ServiceException {
		StringBuilder query = new StringBuilder();

		query.append("select art.* from suia_iii.waste_disposal wd ");
		query.append("inner join suia_iii.waste_disposal_types wdt on wd.wdty_id=wdt.wdty_id ");
		query.append("inner join suia_iii.waste_disposal_receipt wdr on wdr.wadr_id=wd.wadr_id ");
		query.append("inner join suia_iii.approval_technical_requirements art on wdr.apte_id=art.apte_id ");
		query.append("inner join suia_iii.receipt_hazardous_waste rhw on wdr.rehw_id=rhw.rehw_id ");
		query.append("inner join suia_iii.waste_dangerous wdg on wdg.wada_id=rhw.wada_id ");
		query.append("where wd.wadi_id="+wadi.getId()+" "
				+ "and wd.wadi_status=true "
				+ "and wdg.wada_status=true "
				+ "and wdr.wadr_status=true order by wdg.wada_key");
		AprobacionRequisitosTecnicos art= new AprobacionRequisitosTecnicos();
		try
		{
			 art = (AprobacionRequisitosTecnicos) crudServiceBean.findNativeQuery(query.toString(),AprobacionRequisitosTecnicos.class).get(0);
		}
		catch(Exception e)
		{
			art = new AprobacionRequisitosTecnicos();
		}
		return art;
	}
	 
}
