package ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import ec.gob.ambiente.suia.domain.base.EntidadAuditable;


/**
 * The persistent class for the technical_report_forest database table.
 * 
 */
@Entity
@Table(name="clarification_themes", schema = "coa_environmental_impact_study")
@AttributeOverrides({
		@AttributeOverride(name = "estado", column = @Column(name = "clat_status")),
		@AttributeOverride(name = "fechaCreacion", column = @Column(name = "clat_creation_date")),
		@AttributeOverride(name = "fechaModificacion", column = @Column(name = "clat_update_date")),
		@AttributeOverride(name = "usuarioCreacion", column = @Column(name = "clat_creator_user")),
		@AttributeOverride(name = "usuarioModificacion", column = @Column(name = "clat_update_user")) })

@NamedQueries({ 
@NamedQuery(name="AclaracionProrrogaEsIA.findAll", query="SELECT a FROM AclaracionProrrogaEsIA a"),
@NamedQuery(name=AclaracionProrrogaEsIA.GET_POR_ESTUDIO, query="SELECT a FROM AclaracionProrrogaEsIA a where a.idEstudio = :idEstudio and a.estado = true order by id desc")
})
public class AclaracionProrrogaEsIA extends EntidadAuditable implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private static final String PAQUETE = "ec.gob.ambiente.rcoa.estudioImpactoAmbiental.model.";
	
	public static final String GET_POR_ESTUDIO = PAQUETE + "AclaracionProrrogaEsIA.getPorEstudio";

	@Getter
	@Setter
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clat_id")
	private Integer id;

	@Getter
	@Setter
	@Column(name="prin_id")
	private Integer idEstudio;	
	
	@Getter
	@Setter
	@Column(name="clat_clarification_meeting")
	private Boolean requiereReunion;
	
	@Getter
	@Setter
	@Column(name="clat_time_extension")
	private Boolean requiereProrroga;

	@Getter
	@Setter
	@Column(name="clat_days_extension")
	private Integer diasProrroga;

	@Getter
	@Setter
	@Column(name="clat_date_clarification_meeting")
	private Date fechaReunion;	

	@Getter
	@Setter
	@Column(name="clat_hour_clarification_meeting")
	private String horaReunion;
	
	@Getter
	@Setter
	@Column(name="clat_place_clarification_meeting")
	private String lugarReunion;

	@Getter
	@Setter
	@Column(name="clat_status_clarification_meeting")
	private Boolean reunionRealizada;

}