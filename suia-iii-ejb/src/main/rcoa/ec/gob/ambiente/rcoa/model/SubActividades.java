package ec.gob.ambiente.rcoa.model;

import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

@Entity
@Table(name = "catalog_subcategories", schema = "coa_mae")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "cosu_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "casu_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "cosu_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "cosu_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "cosu_user_update")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cosu_status = 'TRUE'")
public class SubActividades extends EntidadAuditable{

	
	private static final long serialVersionUID = 8759441366382348310L;
	
	@Getter
	@Setter
	@Id
	@Column(name = "cosu_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
	@Setter
	@ManyToOne
	@JoinColumn(name="caci_id")
	private CatalogoCIUU catalogoCIUU;
	
	@Getter
	@Setter
	@ManyToOne	
	@JoinColumn(name = "cosu_parent_id")
	private SubActividades subActividades;
	
	@Getter
	@Setter
	@OneToMany(mappedBy = "subActividades")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "cosu_status = 'TRUE'")
	@OrderBy("orden ASC")
	private List<SubActividades> hijos;
	
	@Getter
	@Setter	
	@Column(name = "cosu_name")
	private String nombre;
	@Getter
	@Setter	
	@Column(name = "cosu_ismultiple")
	private Boolean esMultiple;
	@Getter
	@Setter	
	@Column(name = "cosu_ordinal")
	private Integer orden;
	@Getter
	@Setter	
	@Column(name = "cosu_permit_type")
	private String tipoPermiso;
	@Getter
	@Setter	
	@Column(name = "cosu_process_entity")
	private String entidadProceso;//1 PC, 2 ZONAL, 3 ENTES, 5 PNG, 6 POR ASIGNAR
	@Getter
	@Setter	
	@Column(name = "cosu_istrue")
	private Integer opcionPermisoSi;
	@Getter
	@Setter	
	@Column(name = "cosu_isfalse")
	private Integer opcionPermisoNo;
	@Getter
	@Setter	
	@Column(name = "cosu_process_entity_istrue")
	private Integer entidadProcesoSi;
	@Getter
	@Setter	
	@Column(name = "cosu_process_entity_isfalse")
	private Integer entidadProcesoNo;
	@Getter
	@Setter
	@Transient
	private Boolean valorOpcion;
	@Getter
	@Setter
	@Column(name = "cosu_financed_state_bank")
	private Boolean financiadoBancoEstado;
	
	@Getter
	@Setter	
	@Column(name = "cosu_title_children")
	private String tituloHijos;
	
	@Getter
	@Setter	
	@Column(name = "cosu_true_parent_answer")
	private Boolean padreVerdadero;
	
	@Getter
	@Setter	
	@Column(name = "cosu_question_type")
	private Integer tipoPregunta;// 1 radio, 2 text, 3 chek, 4 combobox.
	
	@Getter
	@Setter	
	@Column(name = "cosu_trd")
	private Boolean requiereIngresoResiduos;
	
	@Getter
	@Setter	
	@Column(name = "cosu_tsq")
	private Boolean requiereTransporteSustanciasQuimicas;
	
	@Getter
	@Setter	
	@Column(name = "cosu_grdpe")
	private Boolean requiereGestionResiduosDesechos;
	
	@Getter
	@Setter	
	@Column(name = "cosu_ordinal_superior")
	private Integer numeroCombinacion;
	
	@Getter
	@Setter	
	@Column(name = "cosu_selection_type")
	private Integer tipoSeleccionParaCombinacion;
	
	@Getter
	@Setter	
	@Column(name = "cosu_categoria_depend")
	private Boolean esDependiente;
	
	@Getter
	@Setter	
	@Column(name = "cosu_ordinal_depend")
	private String categoriaDependiente;
	
	@Getter
	@Setter	
	@Column(name = "cosu_default_modes")
	private String modalidadesArtPredeterminadas;

	@Getter
	@Setter	
	@Column(name = "cosu_operator_modes")
	private String modalidadesArtPermitidas;
	
	@Getter
	@Setter	
	@Column(name = "cosu_mining_regime")
	private Boolean requiereRegimenMinero;

	@Getter
	@Setter	
	@Column(name = "cosu_coordinate")
	private Integer requiereValidacionCoordenadas;

	@Getter
	@Setter	
	@Column(name = "cosu_additional_component")
	private Integer tieneInformacionAdicional;
	
	@Getter
	@Setter	
	@Column(name = "cosu_block")
	private Integer bloque;
	
	@Getter
	@Setter
	@Transient
	private Boolean subSeleccionada;
	
	@Getter
	@Setter
	@Transient
	private SubActividades hijoSeleccionado;
	
	@Getter
	@Setter
	@Transient
	private List<SubActividades> listaHijosSeleccionado = new ArrayList<SubActividades>();
	

}
