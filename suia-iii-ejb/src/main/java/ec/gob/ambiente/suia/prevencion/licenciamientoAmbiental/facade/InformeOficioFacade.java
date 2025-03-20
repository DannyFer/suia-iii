package ec.gob.ambiente.suia.prevencion.licenciamientoAmbiental.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.*;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import ec.gob.ambiente.suia.domain.enums.TipoDocumentoSistema;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Query;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class InformeOficioFacade {

    @EJB
    CrudServiceBean crudServiceBean;

    public void guardarOficioSolicitarPronuncimiento(OficioSolicitarPronunciamiento oficio) {
        try {
            crudServiceBean.saveOrUpdate(oficio);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarInformeTecnicoEia(InformeTecnicoEia informe) {
        try {
            crudServiceBean.saveOrUpdate(informe);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarOficioAprobacionEia(OficioAprobacionEia oficio) {
        try {
            crudServiceBean.saveOrUpdate(oficio);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void guardarOficioObservacionEia(OficioObservacionEia oficio) {
        crudServiceBean.saveOrUpdate(oficio);
    }

    public void guardarInforme(EntidadBase informe) {
        try {
            crudServiceBean.saveOrUpdate(informe);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public PlantillaReporte obtenerPlantillaReporte(Integer tipoDocumentoId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumentoId);

            List<PlantillaReporte> lista = crudServiceBean.findByNamedQueryGeneric(
                    PlantillaReporte.OBTENER_PLANTILLA_POR_INFORME, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public BigInteger obtenerNumeroInforme(String sequenceName, String schema) {
        try {
            return (BigInteger) crudServiceBean.getSecuenceNextValue(sequenceName, schema);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public OficioSolicitarPronunciamiento obtenerOficioSolicitarPronunciamientoPorEstudio(Integer tipoDocumentoId,
                                                                                          Integer estudioImpactoAmbientalId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumentoId);
            parametros.put("p_estudioImpactoAmbientalId", estudioImpactoAmbientalId);

            List<OficioSolicitarPronunciamiento> lista = crudServiceBean
                    .findByNamedQueryGeneric(
                            OficioSolicitarPronunciamiento.OBTENER_OFICIO_SOLICTAR_PRONUNCIAMIENTO_POR_ESTUDIO_TIPO,
                            parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }   
    
	public InformeTecnicoEia obtenerInformeTecnicoEiaPorEstudio(TipoDocumentoSistema tipoDocumento,Integer estudioImpactoAmbientalId,Integer numero) {
		try {
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_tipoDocumentoId",tipoDocumento.getIdTipoDocumento());
			parametros.put("p_estudioImpactoAmbientalId",estudioImpactoAmbientalId);
			
			List<InformeTecnicoEia> lista = crudServiceBean.findByNamedQueryGeneric(InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_ESTUDIO_TIPO,parametros);
			if (lista != null && !lista.isEmpty()) {
				
				for (InformeTecnicoEia it : lista) {
					if(numero==it.getNumero())
						return it;
				}
				if(lista.get(0).getNumero()==null)
					return lista.get(0);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}		
		return null;
	}

    public OficioAprobacionEia obtenerOficioAprobacionEiaPorEstudio(TipoDocumentoSistema tipoDocumento,
                                                                    Integer estudioImpactoAmbientalId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
            parametros.put("p_estudioImpactoAmbientalId", estudioImpactoAmbientalId);

            List<OficioAprobacionEia> lista = crudServiceBean.findByNamedQueryGeneric(
                    OficioAprobacionEia.OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public OficioObservacionEia obtenerOficioObservacionEiaPorEstudio(TipoDocumentoSistema tipoDocumento,
                                                                      Integer estudioImpactoAmbientalId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
            parametros.put("p_estudioImpactoAmbientalId", estudioImpactoAmbientalId);

            List<OficioObservacionEia> lista = crudServiceBean.findByNamedQueryGeneric(
                    OficioObservacionEia.OBTENER_OFICIO_APROBACION_EIA_POR_ESTUDIO_TIPO, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public InformeTecnicoGeneralLA obtenerInformeTecnicoLAGeneralPorLicenciaId(Integer tipoDocumentoId,
                                                                               Integer licenciaId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("tipoDocumentoId", tipoDocumentoId);
            parametros.put("licenciaId", licenciaId);

            List<InformeTecnicoGeneralLA> lista = crudServiceBean.findByNamedQueryGeneric(
                    InformeTecnicoGeneralLA.OBTENER_INFORME_TECNICO_GENERAL_POR_LICENCIA, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public InformeTecnicoGeneralLA obtenerInformeTecnicoLAGeneralPorProyectoId(Integer tipoDocumentoId,
                                                                               Integer proyectoId) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("tipoDocumentoId", tipoDocumentoId);
            parametros.put("proyectoId", proyectoId);

            List<InformeTecnicoGeneralLA> lista = crudServiceBean.findByNamedQueryGeneric(
                    InformeTecnicoGeneralLA.OBTENER_INFORME_TECNICO_GENERAL_POR_PROYECTO, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (Exception e) {

            return null;
        }

        return null;
    }

    public void guardarInformeTecnicoGeneralLA(InformeTecnicoGeneralLA informe) {
        try {
            crudServiceBean.saveOrUpdate(informe);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    public InformeTecnicoTDRLA obtenerInformeTecnicoTDRLAPorProyecto(TipoDocumentoSistema tipoDocumento,
                                                                     Integer idProyecto) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
            parametros.put("p_proyecto_id", idProyecto);

            List<InformeTecnicoTDRLA> lista = crudServiceBean.findByNamedQueryGeneric(
                    InformeTecnicoTDRLA.OBTENER_INFORME_TECNICO_TDR_POR_PROYECTO, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }



    public OficioAprobacionTDRLA obtenerOficioAprobacionTDRLAPorProyecto(TipoDocumentoSistema tipoDocumento,
                                                                     Integer idProyecto) {
        try {
            Map<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("p_tipoDocumentoId", tipoDocumento.getIdTipoDocumento());
            parametros.put("p_proyecto_id", idProyecto);

            List<OficioAprobacionTDRLA> lista = crudServiceBean.findByNamedQueryGeneric(
                    OficioAprobacionTDRLA.OBTENER_OFICIO_TDR_POR_PROYECTO, parametros);
            if (lista != null && !lista.isEmpty()) {
                return lista.get(0);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    
    public List<InformeTecnicoEia> obtenerInformeTecnicoEiaPorEstudioList(TipoDocumentoSistema tipoDocumento,Integer estudioImpactoAmbientalId) {
		try {
									
			Map<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("p_tipoDocumentoId",tipoDocumento.getIdTipoDocumento());
			parametros.put("p_estudioImpactoAmbientalId",estudioImpactoAmbientalId);
			
			List<InformeTecnicoEia> lista = crudServiceBean.findByNamedQueryGeneric(InformeTecnicoEia.OBTENER_INFORME_TECNICO_EIA_POR_ESTUDIO_TIPO,parametros);
			if (lista != null && !lista.isEmpty()) {				
				return lista;
			}
			
		} catch (RuntimeException e) {
			throw new RuntimeException(e);
		}		
		return null;
	}
    
  //CF: método para obtener la resolución de licencia ambiental
    public String obtenerResolucion(String codigo){
    	try {
			String numeroResolucion = "";
    		
			String queryString = "SELECT o FROM InformeTecnicoGeneralLA o where o.licenciaAmbiental.proyecto.codigo = :codigo and o.finalizado = true";
					

			Query query = crudServiceBean.getEntityManager().createQuery(queryString).setParameter("codigo", codigo);

			List<InformeTecnicoGeneralLA> informe = query.getResultList();

			if (informe != null && !informe.isEmpty()) {
				numeroResolucion = informe.get(0).getNumeroResolucion();
			}
			    		
			return numeroResolucion;
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    public Date obtenerFecha(String codigo){
    	try {
			Date fecha = new Date();
    		
			String queryString = "SELECT o FROM InformeTecnicoGeneralLA o where o.licenciaAmbiental.proyecto.codigo = :codigo and o.finalizado = true";
					

			Query query = crudServiceBean.getEntityManager().createQuery(queryString).setParameter("codigo", codigo);

			List<InformeTecnicoGeneralLA> informe = query.getResultList();

			if (informe != null && !informe.isEmpty()) {
				fecha = informe.get(0).getFechaModificacion();
			}
			    		
			return fecha;
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    

}
