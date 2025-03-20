package ec.gob.ambiente.suia.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadBase;

import java.io.File;
import java.util.Date;


/**
 * The persistent class for the fapma_process_vehicle_disposal database table.
 * 
 */
@Entity
@Table(name="fapma_process_vehicle_disposal", schema = "suia_iii")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "fapv_status"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "fapv_status = 'TRUE'")
public class VehiculoDesechoSanitarioProcesoPma extends EntidadBase {

    /**
     *
     */
    private static final long serialVersionUID = -5210894892720540494L;

    @Id
    @SequenceGenerator(name = "PROCCES_VEHICLE_DISPOSAL_GENERATOR", sequenceName = "fapma_process_vehicle_disposal_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROCCES_VEHICLE_DISPOSAL_GENERATOR")
    @Column(name = "fapv_id")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "fapv_plate_number")
    @Getter
    @Setter
    private String numeroPlaca;

    @Column(name = "fapv_engine_number")
    @Getter
    @Setter
    private String numeroMotor;

    @Column(name = "fapv_model")
    @Getter
    @Setter
    private String modelo;

    @Column(name = "fapv_type")
    @Getter
    @Setter
    private String tipo;

    @Column(name = "fapv_cyl")
    @Getter
    @Setter
    private Double cilindraje;

    @Column(name = "fapv_pbv")
    @Getter
    @Setter
    private Double pbv;

    @Column(name = "fapv_pv")
    @Getter
    @Setter
    private Double pv;

    @Column(name = "fapv_tonnage")
    @Getter
    @Setter
    private Double tonelaje;

    // bi-directional many-to-one association to CatiiFapma
    @ManyToOne
    @JoinColumn(name = "cafa_id")
    @ForeignKey(name = "catii_fapma_process_sanitary_disposal_fk")
    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbientalPma;

    @ManyToOne
    @JoinColumn(name = "docu_id_iv")
    @ForeignKey(name = "docu_image_process_sanitary_disposal_fk")
    @Getter
    @Setter
    private Documento documentoImagenVehículo;

    @ManyToOne
    @JoinColumn(name = "docu_id_ci")
    @ForeignKey(name = "docu_certificade_process_sanitary_disposal_fk")
    @Getter
    @Setter
    private Documento documentoCertificadoInspeccion;

    @ManyToOne
    @JoinColumn(name = "docu_id_pv")
    @ForeignKey(name = "docu_plate_process_sanitary_disposal_fk")
    @Getter
    @Setter
    private Documento documentoMatricula;

    @Column(name = "fapv_dnca")
    @Getter
    @Setter
    private String codigoDNCA;

    @Column(name = "fapv_wash_place")
    @Getter
    @Setter
    private String lugarLavado;

    @Transient
    @Getter
    @Setter
    private File imagenVehiculo;

    @Transient
    @Getter
    @Setter
    private File certificadoInspeccion;

    @Transient
    @Getter
    @Setter
    private File matricula;
    
    /**
     * CF: nuevos campos para historico
     */    
    @Getter
    @Setter
    @Column(name = "fapv_original_record_id")
    private Integer idRegistroOriginal;
        
    @Getter
    @Setter
    @Column(name = "fapv_historical_date")
    private Date fechaHistorico;
    
	@Getter
	@Setter
	@Transient
	private String nuevoEnModificacion;

	public VehiculoDesechoSanitarioProcesoPma() {
		super();
	}

	public VehiculoDesechoSanitarioProcesoPma(Integer id) {
		super();
		this.id = id;
	}
    
}