package ec.gob.ambiente.retce.services;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import ec.gob.ambiente.retce.model.DatosLaboratorio;
import ec.gob.ambiente.retce.model.LaboratorioGeneral;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;

@Stateless
public class LaboratorioGeneralFacade {
	@EJB
	private CrudServiceBean crudServiceBean;
	
	
	public LaboratorioGeneral guardar(LaboratorioGeneral obj) {
		return crudServiceBean.saveOrUpdate(obj);
	}
	
	public LaboratorioGeneral laboratorioGeneralXRuc(String ruc)
	{
		LaboratorioGeneral obj = new LaboratorioGeneral();
		try
		{
			Query sql = crudServiceBean.getEntityManager()
					.createQuery("SELECT l FROM LaboratorioGeneral l where l.ruc = :ruc and l.estado=true")
					.setParameter("ruc", ruc);
			if(sql.getResultList().size()>0)
				obj=(LaboratorioGeneral) sql.getResultList().get(0);
			
			return obj;
		}
		catch(Exception e)
		{			
			e.printStackTrace();
		}
		return obj;
	}

	public DatosLaboratorio datoLaboratorioXRuc(String ruc){
		DatosLaboratorio obj = new DatosLaboratorio(); 
		try {
			Query sql= crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM DatosLaboratorio o where o.ruc = :ruc")
					.setParameter("ruc", ruc);
			if(sql.getResultList().size()>0)
				obj=(DatosLaboratorio) sql.getResultList().get(0);
			
			return obj;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
