package ec.gob.ambiente.suia.domain;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;


/**
 * The persistent class for the fapma_accessory_equipment database table.
 * 
 */
@NamedQueries({ @NamedQuery(name = EquipoAccesorioPma.GET_ALL, query = "SELECT e FROM EquipoAccesorioPma e") })
@Entity
@Table(name = "fapma_accessory_equipment", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "faae_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "faae_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "faae_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "faae_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "faae_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "faae_status = 'TRUE'")
public class EquipoAccesorioPma extends EntidadAuditable {

	private static final long serialVersionUID = 1L;
	
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.categoriaii.EquipoAccesorioPma.getAll";
	public static final String SEQUENCE_CODE = "fapma_accessory_equipment_caac_id_seq";
	
	@Id
	@Column(name="faae_id")
	@SequenceGenerator(name = "ACCESSORY_EQUIPMENT_PMA_FAAEID_GENERATOR", sequenceName = "fapma_accessory_equipment_caac_id_seq", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCESSORY_EQUIPMENT_PMA_FAAEID_GENERATOR")
	@Getter
	@Setter
	private Integer id;

	//bi-directional many-to-one association to CatiiFapma
	@ManyToOne
	@JoinColumn(name="cafa_id")
	@ForeignKey(name = "catii_fapma_detail_fapma_accessory_equipment_fk")
	@Getter
	@Setter
	private FichaAmbientalPma fichaAmbientalPma;
	
	@Column(name="faae_description")
	@Getter
	@Setter
	private String descripcion;
}