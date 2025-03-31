package ec.gob.ambiente.suia.domain;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the licensing database table.
 */
@Entity
@Table(name = "licensing", schema = "suia_iii")
@NamedQueries({
        @NamedQuery(name = LicenciaAmbiental.OBTENER_TODOS, query = "SELECT l FROM LicenciaAmbiental l"),
        @NamedQuery(name = LicenciaAmbiental.OBTENER_LICENCIA_POR_PROYECTO, query = "SELECT l FROM LicenciaAmbiental l where l.proyecto.id = :idProyecto")

})
@AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "lice_status")),
        @AttributeOverride(name = "fechaCreacion", column = @Column(name = "lice_creation_date")),
        @AttributeOverride(name = "fechaModificacion", column = @Column(name = "lice_date_update")),
        @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "lice_creation_user")),
        @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "lice_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "lice_status = 'TRUE'")
public class LicenciaAmbiental extends EntidadAuditable {

    private static final long serialVersionUID = 2379245042323496247L;

    public static final String OBTENER_TODOS = "ec.gob.ambiente.suia.domain.LicenciaAmbiental.findAll";

    public static final String OBTENER_LICENCIA_POR_PROYECTO = "ec.gob.ambiente.suia.domain.LicenciaAmbiental.licenciaPorProyecto";
    @Id
    @SequenceGenerator(name = "LICENSING_LICE_GENERATOR", sequenceName = "lice_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LICENSING_LICE_GENERATOR")
    @Column(name = "lice_id")
    @Getter
    @Setter
    private Integer id;

    @Column(name = "lice_description")
    @Getter
    @Setter
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "cate_id")
    @ForeignKey(name = "category_licensing_lice_fk")
    @Getter
    @Setter
    private Categoria categoriaLicencia;

    @ManyToOne
    @JoinColumn(name = "lice_user_proponent")
    @ForeignKey(name = "proponent_licensing_lice_fk")
    @Getter
    @Setter
    private Usuario proponente;

    @ManyToOne
    @JoinColumn(name = "lice_user_technician")
    @ForeignKey(name = "technician_licensing_lice_fk")
    @Getter
    @Setter
    private Usuario tecnicoResponsable;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "pren_id")
    @ForeignKey(name = "fk_projects_environmental_licensingpren_id_licensingprendid")
    @Getter
    @Setter
    private ProyectoLicenciamientoAmbiental proyecto;

    @Column(name = "lice_environmental_status")
    @Getter
    @Setter
    private String estadoLicencia;

    @Column(name = "lice_quiz")
    @Getter
    @Setter
    private Boolean realizoEncuesta;

    @Column(name = "lice_code")
    @Getter
    @Setter
    private String numeroLicencia;

    @Column(name = "lice_issue_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaEmision;

    @Column(name = "lice_investment_cost")
    @Getter
    @Setter
    private Double costoInversion;

    @Column(name = "lice_validity_date")
    @Getter
    @Setter
    @Temporal(TemporalType.DATE)
    private Date fechaVigencia;

}