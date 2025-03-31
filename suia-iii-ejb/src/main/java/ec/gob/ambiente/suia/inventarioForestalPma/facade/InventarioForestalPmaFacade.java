package ec.gob.ambiente.suia.inventarioForestalPma.facade;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import ec.gob.ambiente.exceptions.CmisAlfrescoException;
import ec.gob.ambiente.suia.control.documentos.facade.DocumentosFacade;
import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.Documento;
import ec.gob.ambiente.suia.domain.DocumentosTareasProceso;
import ec.gob.ambiente.suia.domain.InventarioForestalPma;
import ec.gob.ambiente.suia.domain.ProyectoLicenciamientoAmbiental;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import ec.gob.ambiente.suia.inventarioForestalPma.service.InventarioForestalPmaService;

@Stateless
public class InventarioForestalPmaFacade {

    @EJB
    private CrudServiceBean crudServiceBean;

    @EJB
    InventarioForestalPmaService inventarioForestalService;

    @EJB
    DocumentosFacade documentosFacade;

    private static final String NOMBRE_CARPETA_INVENTARIO_FORESTAL_PMA = "Carpeta_inventario_forestal_pma";

    public void guardar(InventarioForestalPma inventarioForestal, TipoDocumentoSistema tipoDocumento,
                        boolean existioCambiosArchivo) throws ServiceException, CmisAlfrescoException {
        System.out.println("Existe cambio: " + existioCambiosArchivo);


        if (existioCambiosArchivo) {
            inventarioForestal.setInventarioForestal(subirFileAlfresco(inventarioForestal.getInventarioForestal(),
            		inventarioForestal.getFichaAmbientalPma()!=null?inventarioForestal.getFichaAmbientalPma()
                            .getProyectoLicenciamientoAmbiental():inventarioForestal.getProyectoLicenciamientoAmbiental(), tipoDocumento));                     
        }

        System.out.println("Guardo bien el archivo");
        crudServiceBean.saveOrUpdate(inventarioForestal);
    }

    public void guardarSinInventario(InventarioForestalPma inventarioForestal) {
        crudServiceBean.saveOrUpdate(inventarioForestal);
    }

    public void guardarMineria(InventarioForestalPma inventarioForestal,
                               boolean existioCambiosArchivo) throws ServiceException, CmisAlfrescoException {

        //TODO Verificar el tipo de documento
        if (existioCambiosArchivo) {
            inventarioForestal.setInventarioForestal(subirFileAlfresco(inventarioForestal.getInventarioForestal(),
                    inventarioForestal.getFichaAmbientalMineria()
                            .getProyectoLicenciamientoAmbiental(), TipoDocumentoSistema.INVENTARIO_FORESTAL_GEN
            ));
            
//            if (documentoOriginal != null) {
//				documentoOriginal.setIdHistorico(inventarioForestal.getInventarioForestal().getId());
//				crudServiceBean.saveOrUpdate(documentoOriginal);
//			}
        }

        crudServiceBean.saveOrUpdate(inventarioForestal);
    }

    public InventarioForestalPma obtenerInventarioForestalPmaPorFicha(
            int idFicha) throws ServiceException {
        return inventarioForestalService.obtenerInventarioForestalPma(idFicha);
    }

    public InventarioForestalPma obtenerInventarioForestalPmaMineriaPorFicha(
            int idFicha) throws ServiceException {
        return inventarioForestalService.obtenerInventarioForestalPmaMineria(idFicha);
    }
    
    public InventarioForestalPma obtenerInventarioForestalPmaPorProyecto(
            int idProyecto) throws ServiceException {
        return inventarioForestalService.obtenerInventarioForestalPmaProyecto(idProyecto);
    }

    private Documento subirFileAlfresco(Documento documento,
                                        ProyectoLicenciamientoAmbiental proyecto, TipoDocumentoSistema tipo)
            throws ServiceException, CmisAlfrescoException {

        DocumentosTareasProceso documentoTarea = new DocumentosTareasProceso();
        documentoTarea.setIdTarea(0L);
        documento.setNombreTabla(InventarioForestalPma.class.getSimpleName());
        documento.setIdTable(136143);
        documentoTarea.setProcessInstanceId(0L);

        return documentosFacade.guardarDocumentoAlfresco(proyecto.getCodigo(),
                NOMBRE_CARPETA_INVENTARIO_FORESTAL_PMA, 0L, documento, tipo,
                documentoTarea);
    }

    public byte[] descargarFile(Documento documento)
            throws CmisAlfrescoException {
        return documentosFacade.descargar(documento.getIdAlfresco());
    }

    public byte[] getPlantillaInventarioForestal(String nombre)
            throws ServiceException, CmisAlfrescoException {

        try {
            return documentosFacade.descargarDocumentoPorNombre(nombre);
        } catch (RuntimeException e) {
            throw new ServiceException(
                    "Error al recuperar plantilla inventario forestal", e);
        }
    }
    
    public List<InventarioForestalPma> obtenerAllInventarioForestalPmaPorFicha(
            int idFicha) throws ServiceException {
        return inventarioForestalService.obtenerAllInventarioForestalPma(idFicha);
    }
    
	public void guardarOriginal(InventarioForestalPma inventarioForestal){
		crudServiceBean.saveOrUpdate(inventarioForestal);
	}
    
	public List<InventarioForestalPma> obtenerAllInventarioForestalPmaMineriaPorFicha(
            int idFicha) throws ServiceException {
        return inventarioForestalService.obtenerAllInventarioForestalPmaMineria(idFicha);
    }
	
	public void guardar(InventarioForestalPma inventarioForestal, TipoDocumentoSistema tipoDocumento,
            boolean existioCambiosArchivo, Documento documentoOriginal) throws ServiceException, CmisAlfrescoException {
		System.out.println("Existe cambio: " + existioCambiosArchivo);

		if (existioCambiosArchivo) {
			inventarioForestal.setInventarioForestal(subirFileAlfresco(inventarioForestal.getInventarioForestal(),
							inventarioForestal.getFichaAmbientalPma() != null ? 
									inventarioForestal.getFichaAmbientalPma().getProyectoLicenciamientoAmbiental()
									: inventarioForestal.getProyectoLicenciamientoAmbiental(), tipoDocumento));

			if (documentoOriginal != null) {
				documentoOriginal.setIdHistorico(inventarioForestal.getInventarioForestal().getId());
				crudServiceBean.saveOrUpdate(documentoOriginal);
			}
		}

		System.out.println("Guardo bien el archivo");
		crudServiceBean.saveOrUpdate(inventarioForestal);
	}
}
