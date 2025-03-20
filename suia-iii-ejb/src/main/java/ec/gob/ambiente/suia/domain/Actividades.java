package ec.gob.ambiente.suia.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the proyectos_licenciamiento_ambiental database table.
 * 
 */
@Entity
@Table(name = "activities", schema = "suia_iii")
@NamedQueries({ @NamedQuery(name = Actividades.LISTAR_ACTIVIDADES, query = "SELECT a FROM Actividades a WHERE a.estado = true  ORDER BY a.nombre "),
	@NamedQuery(name = Actividades.LISTAR_SUBACTIVIDADES, query = "SELECT a FROM Actividades a WHERE a.estado = true "),
	@NamedQuery(name = Actividades.OBTENER_TODOS, query = "SELECT a FROM Actividades a WHERE a.estado = true  ") })

public class Actividades implements Serializable  {

	private static final long serialVersionUID = 1L;
	private static final String PAQUETE_CLASE = "ec.gob.ambiente.suia.domain.Actividades.";
	public static final String LISTAR_ACTIVIDADES = PAQUETE_CLASE + "obtenerActividadesPadre";
	public static final String LISTAR_SUBACTIVIDADES = PAQUETE_CLASE + "obtenerPorActividad";
	public static final String OBTENER_TODOS = PAQUETE_CLASE + "obtenerTodos";
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "acti_id")
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "acti_name")
	private String nombre;

	@Getter
	@Setter
	@Column(name = "acti_description")
	private String descripcion;

	@Getter
	@Setter
	@Column(name = "acti_status")
	private boolean estado;

	@ManyToOne
	@JoinColumn(name = "acti_parent_id")
	@ForeignKey(name = "activity_pkey")
	@Getter
	@Setter
	private Actividades actividad;

	public Actividades() {

	}

	public Actividades(Integer id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Actividades)) {
			return false;
		}
		Actividades other = (Actividades) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}
}