package ec.gob.ambiente.rcoa.model;

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

import org.hibernate.annotations.Filter;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "catalog_combined_subcategories", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "ccsu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "ccsu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "ccsu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "ccsu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "ccsu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "ccsu_status = 'TRUE'")
public class CombinacionSubActividades extends EntidadAuditable{

	
	private static final long serialVersionUID = 8759441366382348310L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "ccsu_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="caci_id")
	private CatalogoCIUU catalogoCIUU;
	
	@Getter
	@Setter	
	@Column(name = "ccsu_combined")
	private String combinaciones;

	@Getter
	@Setter	
	@Column(name = "ccsu_permit_type")
	private String tipoPermiso;

	@Getter
	@Setter	
	@Column(name = "ccsu_process_entity")
	private String entidadProceso;

	@Getter
	@Setter	
	@Column(name = "ccsu_trd")
	private Boolean requiereIngresoResiduos;
	
	@Getter
	@Setter	
	@Column(name = "ccsu_tsq")
	private Boolean requiereTransporteSustanciasQuimicas;
	
	@Getter
	@Setter	
	@Column(name = "ccsu_grdpe")
	private Boolean requiereGestionResiduosDesechos;

	@Getter
	@Setter	
	@Column(name = "ccsu_default_modes")
	private String modalidadesArtPredeterminadas;

	@Getter
	@Setter	
	@Column(name = "ccsu_operator_modes")
	private String modalidadesArtPermitidas;

}
