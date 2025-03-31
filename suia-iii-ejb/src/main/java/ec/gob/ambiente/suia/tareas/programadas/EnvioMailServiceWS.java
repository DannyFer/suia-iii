/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.domain.enums.TipoMensajeMailEnum;
import ec.gob.ambiente.suia.utils.BeanLocator;
import ec.gob.ambiente.suia.utils.Constantes;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

/**
 *
 * @author ishmael
 */
@Path("/RestServices/envioMailServiceWs")
@Stateless
public class EnvioMailServiceWS {
	private static final Logger LOG= Logger.getLogger(EnvioMailServiceWS.class);

    @GET
    @Path("/envioMail")
    @Produces("text/plain")
    public String getDetalleInterseccion(@QueryParam("remitente") String remitente, @QueryParam("asunto") String asunto, @QueryParam("mensaje") String mensaje) {
        try {
            String[] remitentes = remitente.split(",");
            EnvioMailCliente envioMailCliente = (EnvioMailCliente) BeanLocator
                    .getInstance(EnvioMailCliente.class);
            for (String r : remitentes) {
                envioMailCliente.enviarMensaje(cargarMail(r, asunto, mensaje));
            }
        } catch (Exception e) {
        	LOG.error("Ocurrio eror en el detalle de interseccion", e);
            
        }
        return null;
    }

    private NotificacionesMails cargarMail(final String direccionEemail, final String asunto, final String body) {
        NotificacionesMails nm = new NotificacionesMails();
        nm.setAsunto(asunto);
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<p>Estimado/a Usuario </p>").append("<br/>");
        mensaje.append("<p style=\"text-align: justify\">").append(body).append("</p>");
        mensaje.append("<br/><p>Ministerio del Ambiente y Agua").append("</p>").append("<br/>");
        mensaje.append("<img src=\"").append(Constantes.getHttpSuiaImagenFooterMail()).append("\" height=\"110\" width=\"750\" alt=\"Smiley face\">");
        nm.setContenido(mensaje.toString());
        nm.setEmail(direccionEemail);
        nm.setTipoMensaje(TipoMensajeMailEnum.TEXT_HTML.getNemonico());
        return nm;
    }

}
