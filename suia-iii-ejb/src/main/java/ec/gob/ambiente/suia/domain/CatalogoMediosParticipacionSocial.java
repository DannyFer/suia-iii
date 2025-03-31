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

@NamedQueries({ @NamedQuery(name = CatalogoMediosParticipacionSocial.GET_ALL, query = "SELECT m FROM CatalogoMediosParticipacionSocial m") })
@Entity
@Table(name = "social_participation_media_catalogs", schema = "suia_iii")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "spmc_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "spmc_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "spmc_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "spmc_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "spmc_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "spmc_status = 'TRUE'")
public class CatalogoMediosParticipacionSocial extends EntidadAuditable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -231162457644642711L;
	public static final String GET_ALL = "ec.com.magmasoft.business.domain.CatalogoMediosParticipacionSocial.getAll";
	
	@Id
	@SequenceGenerator(name = "SOCIAL_PARTICIPARION_MEDIA_CATALOGS_SPMCID_GENERATOR", sequenceName = "seq_spmc_id", schema = "suia_iii", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOCIAL_PARTICIPARION_MEDIA_CATALOGS_SPMCID_GENERATOR")
	@Column(name = "spmc_id")
	@Getter
	@Setter
	private Integer id;
	
	@Getter
	@Setter
	@Column(name = "spmc_name")
	private String nombreMedio;
	
	@Getter
	@Setter
	@ManyToOne
	@ForeignKey(name = "fk_social_participation_media_catalogsspmt_idsocial_participati")
	@JoinColumn(name = "spmt_id")
	private CatalogoTipoParticipacionSocial tipoCatalogo;
	

	public String toString()
	{
		return nombreMedio;
	}

}
