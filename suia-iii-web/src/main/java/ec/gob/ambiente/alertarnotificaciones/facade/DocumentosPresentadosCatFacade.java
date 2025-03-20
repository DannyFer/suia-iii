package ec.gob.ambiente.alertarnotificaciones.facade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.DocumentosPresentadosCate;

@Stateless
public class DocumentosPresentadosCatFacade {
//	private static final Logger LOG = Logger
//			.getLogger(DocumentosPresentadosCatFacade.class);
	@EJB
	private CrudServiceBean crudServiceBean;

	public DocumentosPresentadosCate datosDocumentosPresentadosCate(Integer id) {
		DocumentosPresentadosCate documentosPresentadosCate= new DocumentosPresentadosCate();
		try {			
			documentosPresentadosCate=(DocumentosPresentadosCate) crudServiceBean.getEntityManager().createQuery(" FROM DocumentosPresentadosCate d where d.id=:id").setParameter("id", id).getSingleResult();	
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("errrol--------------"+e);
		}
		return documentosPresentadosCate;
	}

	public void guadarDocumentosPresentadosCat(
			DocumentosPresentadosCate documentosPresentadosCat) {
		crudServiceBean.saveOrUpdateAlert(documentosPresentadosCat);
	}
}
