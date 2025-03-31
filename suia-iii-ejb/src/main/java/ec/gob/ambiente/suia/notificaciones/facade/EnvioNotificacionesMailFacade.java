package ec.gob.ambiente.suia.notificaciones.facade;

import ec.gob.ambiente.suia.crud.service.CrudServiceBean;
import ec.gob.ambiente.suia.domain.EnvioNotificacionesMail;
import ec.gob.ambiente.suia.domain.Usuario;
import ec.gob.ambiente.suia.exceptions.ServiceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "EnvioNotificacionesMailFacade")
public class EnvioNotificacionesMailFacade {

	
	@EJB
	private CrudServiceBean crudServiceBean;
	
	public void save(EnvioNotificacionesMail obj) {
		try {
			obj.setIdSistema(EnvioNotificacionesMail.SISTEMA_REGULARIZACION);
			if (obj.getId() == null) {
				obj.setFechaCreacion(new Date());
			} else {
				obj.setFechaModificacion(new Date());
			}
			crudServiceBean.saveOrUpdate(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    @SuppressWarnings("unchecked")
	public List<EnvioNotificacionesMail> listarNotificacionesPorUsuarios(Usuario usuario) throws ServiceException {
        List<EnvioNotificacionesMail> listaNotificaciones = null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("usuarioId", usuario.getId());
        try {
        	listaNotificaciones = (List<EnvioNotificacionesMail>) crudServiceBean.getEntityManager().createQuery(
					"SELECT m FROM EnvioNotificacionesMail m "
					+ " WHERE m.estado = true and m.enviado = true and (m.esVisible is true or m.esVisible is null) "
					+ " and m.usuarioDestinoId = :usuarioId "
					+ " and m.idSistema = " + EnvioNotificacionesMail.SISTEMA_REGULARIZACION
					+ " order by m.fechaEnvio ")
		        	.setParameter("usuarioId", usuario.getId()).getResultList();
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
        return listaNotificaciones;
    }
    /*  para lazy */


	public Integer contarRegistros(Usuario usuario, boolean estado) {
		String sql = "select count (*) "
				+ " FROM EnvioNotificacionesMail m "
				+ " WHERE m.estado = true and m.enviado = true  and (m.esVisible is true or m.esVisible is null) "
				+ " and m.notificacionRevisada = " + (estado ? "true" : "false") 
				+ " and m.usuarioDestinoId = :usuarioId "
				+ " and m.idSistema = " + EnvioNotificacionesMail.SISTEMA_REGULARIZACION;
		List<Object> result = (List<Object>) crudServiceBean.getEntityManager()
				.createQuery(sql).setParameter("usuarioId", usuario.getId())
				.getResultList();
		for (Object object : result) {
			return ((Long) object).intValue();
		}
		return 0;
	}	

	public List<EnvioNotificacionesMail> buscarNotificacionesEnviadas(Integer inicio,Integer total, Usuario usuario,String codigo, boolean estado) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("total", total);
		params.put("inicio", inicio);
		String like = "";
		if (!codigo.isEmpty()) {
			like += " AND LOWER(seno_project_code) like LOWER("+"'%" + codigo + "%'"+")";
		}
		String sql = "SELECT seno_id, seno_project_code, seno_destination_mail, seno_subject, seno_content, seno_send_date, seno_check_message "
				+ " FROM send_notifications m "
				+ " WHERE m.seno_status = true and m.seno_status_send = true and (m.seno_observable is true or m.seno_observable is null ) "
				+ " and m.seno_check_message = " + (estado ? "true" : "false") 
				+ " and m.seno_user_id_destination = " + usuario.getId() 
				+ like
				+ " and seno_system = " + EnvioNotificacionesMail.SISTEMA_REGULARIZACION
				+ " order by m.seno_send_date desc LIMIT " + total + " OFFSET " + inicio;
		
		List<EnvioNotificacionesMail> result = (List<EnvioNotificacionesMail>)crudServiceBean.getEntityManager().createNativeQuery(sql)
	    		.getResultList();
		
		List<EnvioNotificacionesMail> listaEnvioMail =new ArrayList<EnvioNotificacionesMail>();
		if(result.size()>0)
		{
			for (Object object : result) {
				Object[] obj=(Object[]) object;
				EnvioNotificacionesMail mail =new EnvioNotificacionesMail();
				mail.setId((Integer)obj[0]);
				mail.setCodigoProyecto((String)obj[1]);
				mail.setEmail((String)obj[2]);
				mail.setAsunto((String)obj[3]);
				mail.setContenido((String)obj[4]);
				mail.setFechaEnvio((Date)obj[5]);
				mail.setNotificacionRevisada((boolean)obj[6]);
				listaEnvioMail.add(mail);
			}
		}
		return listaEnvioMail;
	}

	public void actualizarNotificacionesRevisada(Integer id, Usuario usuario) {
		String sql="UPDATE send_notifications "
				+ " SET seno_check_message = true, seno_check_message_date = now(), seno_date_update = now(), seno_user_update = "+ usuario.getNombre()
				+ " WHERE seno_id = "+id+" and seno_check_message = false and seno_status_send = true ";
		
		crudServiceBean.getEntityManager().createNativeQuery(sql).executeUpdate();
		
	}
}
