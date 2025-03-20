package ec.gob.ambiente.suia.domain;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.ForeignKey;

import ec.gob.ambiente.suia.domain.base.EntidadAuditable;
import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the fapma_activities_schedule database table.
 *
 */
@NamedQueries({
    @NamedQuery(name = CronogramaActividadesPma.GET_ALL, query = "SELECT c FROM CronogramaActividadesPma c"),
    @NamedQuery(name = CronogramaActividadesPma.OBTENER_POR_FASE, query = "SELECT c FROM CronogramaActividadesPma c WHERE c.catalogoCategoriaFase.id = :p_categoriaFaseId"),
    @NamedQuery(name = CronogramaActividadesPma.OBTENER_POR_FICHAPMA_CATALOGO, query = "SELECT c FROM CronogramaActividadesPma c WHERE c.fichaAmbientalPma = :fichaAmbientalPma AND c.catalogoCategoriaFase = :catalogoCategoriaFase")})
@Entity
@Table(name = "catii_activities_schedule", schema = "suia_iii")
@AttributeOverrides({
    @AttributeOverride(name = "estado", column = @Column(name = "caas_status")),
    @AttributeOverride(name = "fechaCreacion", column = @Column(name = "caas_creation_date")),
    @AttributeOverride(name = "fechaModificacion", column = @Column(name = "caas_date_update")),
    @AttributeOverride(name = "usuarioCreacion", column = @Column(name = "caas_creator_user")),
    @AttributeOverride(name = "usuarioModificacion", column = @Column(name = "caas_user_update"))})
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caas_status = 'TRUE'")
public class CronogramaActividadesPma extends EntidadAuditable {

    public static final String GET_ALL = "ec.com.magmasoft.business.domain.categoriaii.CronogramaActividadesPma.getAll";
    public static final String OBTENER_POR_FICHAPMA_CATALOGO = "ec.com.magmasoft.business.domain.categoriaii.CronogramaActividadesPma.obtenerPorFichaPmaCatalogo";
    public static final String OBTENER_POR_FASE = "ec.com.magmasoft.business.domain.CronogramaActividadesPma.obtenerPorFase";
    //public static final String SEQUENCE_CODE = "fapma_activities_schedule_caas_id_seq";
    private static final long serialVersionUID = 5404907320898466400L;

    @Id
    @Column(name = "caas_id")
    @SequenceGenerator(name = "ACTIVITIES_SCHEDULE_CAAS_GENERATOR", sequenceName = "catii_activities_schedule_caas_id_seq", schema = "suia_iii", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITIES_SCHEDULE_CAAS_GENERATOR")
    @Getter
    @Setter
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cafa_id")
    @ForeignKey(name = "catii_fapma_fapma_activities_schedule_fk")
    @Getter
    @Setter
    private FichaAmbientalPma fichaAmbientalPma;

    //bi-directional many-to-one association to GeneralCatalog
    @ManyToOne
    @JoinColumn(name = "secp_id")
    @ForeignKey(name = "sectors_classifications_phases_fapma_activities_fk")
    @Getter
    @Setter
    private CatalogoCategoriaFase catalogoCategoriaFase;

    @OneToMany(mappedBy = "cronogramaActividadesPma", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Actividad> actividadList;
}
