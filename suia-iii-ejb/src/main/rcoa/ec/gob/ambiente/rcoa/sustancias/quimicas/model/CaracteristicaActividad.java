package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

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

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="activity_characteristics", schema = "coa_chemical_sustances")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "acch_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "acch_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "acch_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "acch_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "acch_user_update")) })
public class CaracteristicaActividad extends EntidadAuditable implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="acch_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="acch_name")
	private String nombre;
	
	@Getter
	@Setter
	@Column(name="acch_type") //tipo de  botones de entrada de datos,  0 sin control, 1 radio booton, 2 text, 3 chek, 4 combobox etc
	private Integer tipo;
	
	@Getter
	@Setter
	@Column(name="acch_order")
	private Integer orden;
	
	@ManyToOne
	@JoinColumn(name = "acle_id")	
	@Getter
	@Setter
	private ActividadNivel actividadNivel;
	
	@Getter
	@Setter
	@Column(name="acch_enable_description")
	private Boolean habilitarDescripcion;
	
	@Getter
	@Setter
	@Column(name="acch_description_label")
	private String etiquetaDescripcion;
	
	
}