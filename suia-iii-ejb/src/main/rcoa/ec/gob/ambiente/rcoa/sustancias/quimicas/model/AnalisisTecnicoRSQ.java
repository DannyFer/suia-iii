package ec.gob.ambiente.rcoa.sustancias.quimicas.model;

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

import ec.gob.ambiente.rcoa.enums.CatalogoTipoCoaEnum;
import ec.gob.ambiente.rcoa.model.CatalogoGeneralCoa;
import ec.gob.ambiente.rcoa.model.CatalogoTipoCoa;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import lombok.Getter;
import lombok.Setter;

/**
 * The persistent class for the technical_analysis database table.
 * Tabla de Hallazgos y Requerimientos
 */
@Entity
@Table(name = "technical_analysis", schema = "coa_chemical_sustances")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "tean_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "tean_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "tean_date_update")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "tean_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "tean_user_update")) })
public class AnalisisTecnicoRSQ extends EntidadAuditable {
		
	private static final long serialVersionUID = -1L;
	
	public AnalisisTecnicoRSQ() {}
	
	public AnalisisTecnicoRSQ(InformeOficioRSQ informeOficioRSQ,CatalogoTipoCoaEnum tipo,UbicacionSustancia ubicacionSustancia,CatalogoGeneralCoa catalogoGeneralCoa) {
		this.informeOficioRSQ=informeOficioRSQ;
		this.tipo=new CatalogoTipoCoa(tipo.getId());
		this.ubicacionSustancia=ubicacionSustancia;
		this.catalogoGeneralCoa=catalogoGeneralCoa;
		this.hallazgo=catalogoGeneralCoa.getNombre();
		this.habilitado=true;
	}
	
	public AnalisisTecnicoRSQ(InformeOficioRSQ informeOficioRSQ,CatalogoTipoCoaEnum tipo,CatalogoGeneralCoa catalogoGeneralCoa) {
		this.informeOficioRSQ=informeOficioRSQ;
		this.tipo=new CatalogoTipoCoa(tipo.getId());
		this.catalogoGeneralCoa=catalogoGeneralCoa;
		this.hallazgo=catalogoGeneralCoa.getNombre();
		this.habilitado=true;
	}

	@Getter
	@Setter
	@Id
	@Column(name = "tean_id")	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Getter
    @Setter
    @JoinColumn(name = "retr_id")
    @ManyToOne    
    private InformeOficioRSQ informeOficioRSQ;
	
	@ManyToOne
	@JoinColumn(name = "caty_id")	
	@Getter
	@Setter
	private CatalogoTipoCoa tipo;//Hallazgo o Requisito
	
	@Getter
    @Setter
    @JoinColumn(name = "losu_id")
    @ManyToOne    
    private UbicacionSustancia ubicacionSustancia;

	@Getter
	@Setter	
	@JoinColumn(name = "geca_id")
	@ManyToOne		
	private CatalogoGeneralCoa catalogoGeneralCoa;
	
	@Getter
	@Setter
	@Column(name = "tean_compliance")
	private Boolean tieneCumplimiento;
    
	@Getter
	@Setter
	@Column(name = "tean_findings_requeriments")
	private String hallazgo;
	
	@Getter
	@Setter
	@Column(name = "tean_findings_requeriments_description")
	private String hallazgoDescripcion;//Texto del hallazgo que vienbe de la tabla catlogo, puede modificarse.
	
	@Getter
	@Setter
	@Column(name = "tean_enabled")
	private Boolean habilitado;
	
	@Getter
	@Setter
	@Column(name = "tean_observation")
	private String observacion;

	@Getter
	@Setter
	@Transient
	private DocumentosSustanciasQuimicasRcoa documento;
	
	
}