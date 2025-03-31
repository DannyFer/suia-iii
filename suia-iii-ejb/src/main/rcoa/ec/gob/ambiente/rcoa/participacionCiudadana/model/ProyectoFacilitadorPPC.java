package ec.gob.ambiente.rcoa.participacionCiudadana.model;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.rcoa.model.ProyectoLicenciaCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects_facilitators_ppc", schema = "coa_citizen_participation")
@AttributeOverrides({
	@AttributeOverride(name = "estado", column = @Column(name = "prfa_status")),
	@AttributeOverride(name = "fechaCreacion", column = @Column(name = "prfa_creation_date")),
	@AttributeOverride(name = "fechaModificacion", column = @Column(name = "prfa_date_update")),
	@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "prfa_creator_user")),
	@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "prfa_user_update")) 
})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "prfa_status = 'TRUE'")

public class ProyectoFacilitadorPPC extends EntidadAuditable{
	
	private static final long serialVersionUID = 1801511545851715084L;

	@Getter
	@Setter
	@Id
	@Column(name = "prfa_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "prco_id")
	@ForeignKey(name = "fk_prco_id")
	@Getter
	@Setter
	private ProyectoLicenciaCoa proyectoLicenciaCoa;
	
	@Getter
	@Setter
	@Column(name="prfa_number_facilitadores")
	private Integer numeroFacilitadores;
	
	@Getter
	@Setter
	@Column(name="prfa_total_value")
	private Double valorTotal;
	
	@Getter
	@Setter
	@Column(name="prfa_additional_facilitators")
	private Boolean facilitadoresAdicionales;
	
	@Getter
	@Setter
	@Column(name="prfa_number_additional_facilitators")
	private Integer numeroFacilitadoresAdicionales;
	
	@Setter
	@Getter
	@Column(name="prfa_publication_start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaInicioPublicacion;
	
	@Setter
	@Getter
	@Column(name="prfa_publication_end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaFinPublicacion;
	
	@Getter
	@Setter
	@Column(name="prfa_status_citizen_observations")
	private Boolean observacionesCiudadania;
	
	@Getter
	@Setter
	@Column(name="prfa_status_date_publication")
	private Boolean habilitadaPublicacion;
	
	@Getter
	@Setter
	@Column(name="prfa_total_value_additional_facilitators")
	private Double valorTotalFacilitadoresAdicionales;
	
	@Getter
	@Setter
	@Column(name = "prfa_full_payment_inspection")
	private Double pagoInspeccion;
	
}
