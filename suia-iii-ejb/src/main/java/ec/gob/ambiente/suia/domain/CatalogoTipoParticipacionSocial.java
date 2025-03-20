package ec.gob.ambiente.suia.domain;


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the social_participation_media_types database table.
 *
 */
@NamedQueries({ @NamedQuery(name = CatalogoTipoParticipacionSocial.GET_ALL, query = "SELECT m FROM CatalogoTipoParticipacionSocial m") })
@Entity
@Table(name = "social_participation_media_types", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "spmt_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "spmt_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "spmt_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spmt_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spmt_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spmt_status = 'TRUE'")
public class CatalogoTipoParticipacionSocial extends EntidadAuditable {

	private static final long serialVersionUID = 3751317952460228376L;
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.CatalogoTipoParticipacionSocial.getAll";

	@Id
	@SequenceGenerator(name = "MEDIA_TYPES_PPS_GENERATOR", sequenceName = "seq_spmt_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEDIA_TYPES_PPS_GENERATOR")
	@Column(name = "spmt_id")
	@Getter
	@Setter
	private Integer id;

	@Getter
	@Setter
	@Column(name = "spmt_name")
	private String nombre;


	@Getter
	@Setter
	@Column(name = "spmt_code")
	private String codigo;

}
