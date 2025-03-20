/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.ambiente.suia.domain;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * 
 * @author carlos.pupo
 */
@Entity
@Table(name = "implantation_activities", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "imac_status")) })
@NamedQueries({ @NamedQuery(name = ActividadImplantacion.GET_BY_ESTUDIO, query = "SELECT a FROM ActividadImplantacion a WHERE a.estudioImpactoAmbiental.id = :idEstudio and idHistorico = null order by 1") })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "imac_status = 'TRUE'")
public class ActividadImplantacion extends EntidadBase implements Cloneable {

	private static final long serialVersionUID = -2041978604031098895L;

	public static final String GET_BY_ESTUDIO = "ec.gob.ambiente.suia.domain.ActividadImplantacion.Get_by_estudio";

	@Id
	@Column(name = "imac_id")
	@SequenceGenerator(name = "IMPLANTATION_ACTIVITIES_GENERATOR", initialValue = 1, sequenceName = "seq_imac_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IMPLANTATION_ACTIVITIES_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "imac_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "imac_historical_id")
	private Integer idHistorico;
	
	@Getter
	@Setter
	@Column(name = "imac_notification_number")
	private Integer numeroNotificacion;

	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "eist_id")
	@ForeignKey(name = "fk_actimac_studieseist_id")
	private EstudioImpactoAmbiental estudioImpactoAmbiental;

	@Getter
	@Setter
	@OneToMany(mappedBy = "actividadImplantacion")
	@OrderBy("id ASC")
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "alte_status = 'TRUE'")
	private List<Alternativa> alternativas;

	@Getter
	@Setter
	@Transient
	private Alternativa alternativaMejorOpcion;
	
	@Getter
	@Setter
	@Transient
	private Boolean nuevoEnModificacion;
	
	@Getter
	@Setter
	@Transient
	private Boolean registroModificado;
	
	@Getter
	@Setter
	@Transient
	private Boolean mejorOpcionModificado = false;
	
	@Getter
	@Setter
	@Column(name = "imac_date_create")
	private Date fechaCreacion;

	@Override
	public String toString() {
		return nombre;
	}
	
	public ActividadImplantacion() {
		super();
	}
	
	public ActividadImplantacion(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ActividadImplantacion)) {
			return false;
		}
		ActividadImplantacion other = (ActividadImplantacion) obj;
		if (this.nombre !=null && other.nombre !=null && this.nombre.trim().equals(other.nombre.trim())){
			return true;
		}
		else{
			return false;
		}

	}
	
	@Override
	public ActividadImplantacion clone() throws CloneNotSupportedException {

		 ActividadImplantacion clone = (ActividadImplantacion)super.clone();
		 clone.setId(null);		 
		 return clone;
	}	
}
