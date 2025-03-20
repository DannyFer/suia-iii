package ec.gob.ambiente.retce.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.retce.model.Inclusion;
import ec.gob.ambiente.retce.model.InformacionProyecto;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

@Stateless
public class InclusionFacade{	
	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	@EJB
	private DocumentosFacade documentosFacade;
	
	public Inclusion findById(Integer id){		
		try{
			Inclusion inclusion = (Inclusion) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Inclusion o where o.id = :id")
					.setParameter("id", id)					
					.getSingleResult();
			return inclusion;
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}	

	public void save(Inclusion obj,Usuario usuario) {
		if(obj.getId()==null){
			obj.setUsuarioCreacion(usuario.getNombre());
			obj.setFechaCreacion(new Date());			
		}
		else{
			obj.setUsuarioModificacion(usuario.getNombre());
			obj.setFechaModificacion(new Date());			
		}
		crudServiceBean.saveOrUpdate(obj);
	}
	
	public void saveRevision(Inclusion obj,Usuario usuario) {		
		obj.setUsuarioRevision(usuario.getNombre());
		obj.setFechaRevision(new Date());
		crudServiceBean.saveOrUpdate(obj);
	}
	
	@SuppressWarnings("unchecked")
	public List<Inclusion> findByProyecto(InformacionProyecto informacionProyecto){		
		List<Inclusion> inclusions = new ArrayList<Inclusion>();
		try{
			inclusions = (List<Inclusion>) crudServiceBean
					.getEntityManager()
					.createQuery("SELECT o FROM Inclusion o where o.estado=true and o.informacionProyecto.id = :id")
					.setParameter("id", informacionProyecto.getId())					
					.getResultList();
			for (Inclusion inclusion : inclusions) {
				inclusion.setDocumento(documentosFacade.documentoXTablaIdXIdDocUnico(inclusion.getId(),Inclusion.class.getSimpleName(),TipoDocumentoSistema.RETCE_RESOLUCION_INCLUSION));
				inclusion.setDocumentoEstudio(documentosFacade.documentoXTablaIdXIdDocUnico(inclusion.getId(),Inclusion.class.getSimpleName(),TipoDocumentoSistema.RETCE_ESTUDIO_COMPLEMENTARIO));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return inclusions;
	}
}
