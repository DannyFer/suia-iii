package ec.gob.ambiente.rcoa.consultores.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="multidisciplinary_team", schema="coa_consultants")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "mute_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "mute_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "mute_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "mute_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "mute_user_update")) })

@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "mute_status = 'TRUE'")
public class EquipoMultidisciplinarioConsultor extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="mute_id")
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="conu_id")
	private ConsultorRcoa consultor;
	
	@Getter
	@Setter
	@Column(name = "mute_identification")
	private String identificador;
	
	@Getter
	@Setter
	@Column(name = "mute_names")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name = "mute_senecyt")
	private String numeroSenecyt;

	@Getter
	@Setter
	@Column(name = "mute_job_title")
	private String titulo;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name = "geca_id")
	private CatalogoGeneralCoa componente;
	
	@Transient
    private boolean seleccionado;
}