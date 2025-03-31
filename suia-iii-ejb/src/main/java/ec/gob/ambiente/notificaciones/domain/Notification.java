package ec.gob.ambiente.notificaciones.domain;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ec.gob.ambiente.suia.utils.DateAdapter;
import ec.gob.ambiente.suia.utils.JsonDateSerializer;

@lombok.Data
@XmlRootElement(name = "notification")
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement
	private String id;

	@XmlElement
	private String actorId;

	@XmlElement
	private long taskId;

	@XmlElement
	private String taskName;

	@XmlElement
	private String deploymentId;

	@XmlElement
	private String processId;

	@XmlElement
	private long processInstanceId;

	@XmlElement
	private String subject;

	@XmlElement
	private String text;

	@XmlElement
	@XmlJavaTypeAdapter(value=DateAdapter.class)
	@JsonSerialize(using=JsonDateSerializer.class)
	private Date date;
 
	private String dateAsString;
	
	@XmlElement
	private boolean readed;

	@XmlElement
	private boolean status = true;
	
	@XmlElement
	private String project;

}
