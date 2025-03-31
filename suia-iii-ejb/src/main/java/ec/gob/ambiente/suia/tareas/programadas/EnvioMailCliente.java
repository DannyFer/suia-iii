/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.tareas.programadas;

import ec.gob.ambiente.suia.domain.NotificacionesMails;
import ec.gob.ambiente.suia.exceptions.ServiceException;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 *
 * @author christian
 */
@Stateless
public class EnvioMailCliente {

    @Resource(mappedName = "java:jboss/queues/envioMail")
    private javax.jms.Queue cola;

    @Resource(mappedName = "java:/JmsXA")
    private javax.jms.ConnectionFactory jmsConnection;

    public void enviarMensaje(final NotificacionesMails notificacionesMail) throws ServiceException {
        try {
            Connection con = jmsConnection.createConnection();
            Session session = con.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(
                    cola
            );
            ObjectMessage objectMessage = session.createObjectMessage();
            objectMessage.setObject(notificacionesMail);
            producer.send(objectMessage);
            session.close();
            con.close();
        } catch (JMSException e) {
            throw new ServiceException(e);
        } catch (RuntimeException e) {
            throw new ServiceException(e);
        }
    }
}
