package ec.gob.ambiente.suia.domain;

import java.io.Serializable;

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

import ec.gob.ambiente.suia.domain.base.EntidadBase;

/**
 * The persistent class for the categories_catalog database table.
 * 
 */
@Entity
@Table(name = "catalog_types", catalog = "", schema = "public")
@AttributeOverrides({ @AttributeOverride(name = "estado", column = @Column(name = "caty_status")) })
@Filter(name = EntidadBase.FILTER_ACTIVE, condition = "caty_status = 'TRUE'")
@NamedQueries({
		@NamedQuery(name = "TipoCatalogo.findAll", query = "SELECT c FROM TipoCatalogo c"),
		@NamedQuery(name = "TipoCatalogo.tipoCatalogoXCodigo", query = "SELECT c FROM TipoCatalogo c where c.codigo = :p_codigo") })
public class TipoCatalogo extends EntidadBase implements Serializable {

	// TIPOS DE CATALOGOS
	public static final int NORMATIVAS = 1;
	public static final int TIPO_USO_RAHOE = 2;
	public static final int PARAMETRO_CALIDAD_AGUA = 3;
	public static final int CICLO_HORARIO = 4;
	public static final int TIPO_MUESTREO = 5;
	public static final int METODOLOGIA = 6;
	public static final int TIPO_VEGETACION = 7;
	public static final int NIVEL_IDENTIFICACION = 8;
	public static final int HABITO = 9;
	public static final int ESTADO_INDIVIDUO = 10;
	public static final int ORIGEN = 11;
	public static final int UICN_INTERNACIONAL = 12;
	public static final int RANGO = 14;
	public static final int GRUPOS_TAXONOMICOS = 15;
	public static final int TIPO_REGISTRO = 16;
	public static final int CONDICION_CLIMATICA = 17;
	public static final int DISTRIBUCION_VERTICAL_ESPECIE = 18;
	public static final int COMPORTAMIENTO_SOCIAL = 19;
	public static final int GREMIO_ALIMENTICIO = 20;
	public static final int FASE_LUNAR = 21;
	public static final int SENSIBILIDAD = 22;
	public static final int ESPECIES_BIOINDICADORAS = 23;
	public static final int USO = 24;
	public static final int ESEPECIES_MIGRATORIAS = 25;
	public static final int SISTEMAS_HIDROGRAFICOS = 26;
	public static final int CITIES = 31;
	public static final int LIBRO_ROJO = 32;
	public static final int COLECTAS_INCIDENTALES = 34;
	public static final int PISO_ZOOGEOGRAFICO = 33;
	public static final int DESCRIPCION_PROYECTO_OBRA_ACTIVIDAD = 35;
	public static final int MODOS_REPRODUCCION = 36;
	public static final int DISTRIBUCION_COLUMNA_AGUA = 40;
	public static final int ELEMENTOS_SENCIBLES = 41;
	public static final int PERIODO = 42;
	public static final int COBERTURA_VEGETAL = 7;
	public static final int ZONAS_ICTIOHIDROGRAFICAS = 108;
	public static final int EPOCA_DEL_ANIO = 110;
	public static final int LABORATORIOS = 106;
	public static final int CRITERIOS_CALIDAD_SUELO = 109;
	public static final int MEDIO_FISICO = 37;
	public static final int MEDIO_BIOTICO = 38;
	public static final int MEDIO_SOCIAL = 39;
	public static final int ECOSISTEMAS = 129;
	public static final int GRUPOS_TROFICOS = 130;
	public static final int HABITOS_ALIMENTICIOS = 131;
	public static final int ESPECIE_BIOINDICADORA_CALIDAD_AGUA = 132;
	public static final int TIPO_SECTOR_CARGA_TRABAJO = 206;
	public static final int TIPO_TRAMITE = 207;
	public static final int OPERADORAS_HIDROCARBUROS = 208;
	public static final int BLOQUES_HIDROCARBUROS = 216;
	public static final int TIPO_SERVICIOS = 217;
	public static final int TIPO_OBLIGACION= 218;
	/**
	 * Inicio Tipos de Catálogos para Categoría II
	 */
	public static final int ESTADO_PROYECTO_OBRA_ACTIVIDAD = 101;
	public static final int TIPO_POBLACION = 102;
	public static final int TIPO_TELEFONIA = 103;
	public static final int SITUACION_PREDIO = 104;
	public static final int TIPO_INFRAESTRUCTURA = 105;
	public static final int TIPO_PLAN = 13;
	public static final int TIPO_VIA = 180;

	public static final int CLIMA = 133;
	
	public static final int ESTADO_FISICO = 208;
	public static final int TIPO_TRANSPORTE_DESECHOS = 214;	
	public static final int TIPO_MANEJO_DESECHOS_RECICLAJE = 215;
	public static final int TIPO_MANEJO_DESECHOS_REUSO = 216;

	public static final String CODIGO_CLIMA = "clima";
	public static final String CODIGO_TIPO_SUELO = "tipoSuelo";
	public static final String CODIGO_PENDIENTE_SUELO = "pendienteSuelo";
	public static final String CODIGO_FORMACION_VEGETAL = "formacionVegetal";
	public static final String CODIGO_CONDICIONES_DRENAJE = "condicionesDrenaje";
	public static final String CODIGO_DEMOGRAFIA = "demografia";
	public static final String CODIGO_ABASTECIMIENTO_AGUA = "abastecimientoAguaPoblacion";
	public static final String CODIGO_EVACUACION_AGUAS_SERVIDAS_POBLACION = "evacuacionAguasServidasPonlacion";
	public static final String CODIGO_ELECTRIFICACION = "electrificacion";
	public static final String CODIGO_VIALIDAD_ACCESO_POBLACION = "vialidadAccesoPoblacion";
	public static final String CODIGO_ORGANIZACION_SOCIAL = "organizacionSocial";
	public static final String CODIGO_HABITAT = "habitat";
	public static final String CODIGO_TIPOS_BOSQUES = "tiposBosques";
	public static final String CODIGO_GRADO_INTERVENCION_COBERTURA_VEGETAL = "gradoIntervencionCoberturaVegetal";
	public static final String CODIGO_ASPECTOS_ECOLOGICOS = "aspectosEcologicos";
	public static final String CODIGO_PISO_ZOOGEOLOGICO = "pisoZoogeografico";
	public static final String CODIGO_GRUPO_FAUNISTICO = "gruposFaunistico";
	public static final String CODIGO_SENSIBILIDAD_PRESENTADA_AREA = "sensibilidadPresentadaArea";
	public static final String CODIGO_DATOS_ECOLOGICOS_PRESENTES = "datosEcologicosPresentes";
	
	//Cris F: aumento variable pendiente 020
		public static final String CODIGO_PENDIENTE = "pendienteSueloNuevo";
		public static final String CODIGO_COBERTURA_VEGETAL_SD = "coberturaVegetalSD";
		public static final String CODIGO_PISOS_ZOOGEOGRAFICOS_SD = "pisosZoogeograficosSD";
		public static final String CODIGO_COMPONENTE_BIOTICO_NATIVO_SD = "componenteBioticoNativoSD";
		public static final String CODIGO_COMPONENTE_BIOTICO_INTRODUCIDO_SD = "componenteBioticoIntroducidoSD";
		public static final String CODIGO_ASPECTOS_ECOLOGICOS_SD = "aspectosEcologicosSD";
		public static final String CODIGO_AREAS_SENSIBLES_SD = "areasSensiblesSD";
		//Fin

	public static final String CODIGO_PREDIO_SECUDARIO = "SECUNDARIO";
	public static final String CODIGO_PREDIO_PRIMARIO = "PRINCIPAL";
	public static final String CODIGO_TIPO_ZONA = "TIPOZONA";

	// Codigos para mineria artesanal
	public static final String CODIGO_FORMACION_VEGETAL_MINERIA = "formacionVegetalMineria";
	public static final String CODIGO_HABITAT_MINERIA = "habitatMineria";
	public static final String CODIGO_TIPO_BOSQUE_MINERIA = "tipoBosqueMineria";
	public static final String CODIGO_GRADO_INTERVENCION = "gradoIntervencion";
	public static final String CODIGO_AREAS_INTERVENIDAS = "areasIntervenidas";
	public static final String CODIGO_DATOS_ECOLOGICOS = "datosEcologicos";
	public static final String CODIGO_USO_RECURSO = "usoRecurso";
	public static final String CODIGO_USO_RECURSO_FAUNA = "usoRecursoFauna";
	public static final String CODIGO_PISO_ZOOGEOGRAFICO = "pisoZoogeograficoMineria";
	public static final String CODIGO_COMPONENTE_BIOTICO = "componenteBiotico";
	public static final String CODIGO_SENSIBILIDAD = "sensibilidad";
	public static final String CODIGO_NIVEL_CONSOLIDACION_AREA_INFLUENCIA = "nivelAreaInfluencia";
	public static final String CODIGO_TAMANIO_POBLACION = "tamanioPoblacion";
	public static final String CODIGO_COMPOSICION_ETNICA = "composicionEtnica";
	public static final String CODIGO_ABASTECIMIENTO_AGUA_MINERIA = "abastecimientoAgua";
	public static final String CODIGO_EVACUACION_AGUA_SERVIDAS = "evacuacionAguasServidas";
	public static final String CODIGO_EVACUACION_AGUAS_LLUVIA = "evacuacionAguasLLuvia";
	public static final String CODIGO_DESECHOS_SOLIDOS = "desechosSolidos";
	public static final String CODIGO_ELECTRIFICACION_MINERIA = "electrificacionMineria";
	public static final String CODIGO_TRANSPORTE_PUBLICO = "transportePublico";
	public static final String CODIGO_VIALIDAD_Y_ACCESOS = "vialidadYAcceso";
	public static final String CODIGO_TELEFONIA = "telefonia";

	public static final String CODIGO_APROVECHAMIENTO_TIERRA = "aprovechamientoTierra";
	public static final String CODIGO_TENENCIA_TIERRA = "tenenciaTierra";
	public static final String CODIGO_ORGANIZACION_SOCIAL_MINERIA = "organizacionSocialMineria";
	public static final String CODIGO_LENGUA = "lengua";
	public static final String CODIGO_RELIGION = "religion";
	public static final String CODIGO_TRADICIONES = "tradiciones";
	public static final String CODIGO_PAISAJE_Y_TURISMO = "paisaje";
	public static final String CODIGO_PELIGRO_DESLIZAMIENTO = "peligroDeslizamiento";
	public static final String CODIGO_PELIGRO_INUNDACIONES = "peligroInundamiento";
	public static final String CODIGO_PELIGRO_TERREMOTO = "peligroTerremoto";
	public static final String CODIGO_INSUMOS_MINERIA = "insumoMineriaArtesanal";

	/**
	 * Fin Tipos de Catálogos para Categoría II
	 */

	public static final int TIPO_CATALOGO_MATERIAL_CONSTRUCCION = 1;
	public static final int TIPO_CATALOGO_TIPO_ILUMINACION = 2;
	public static final int TIPO_CATALOGO_TIPO_VENTILACION = 3;
	public static final int TIPO_CATALOGO_ESTADO_LOCAL = 4;
	public static final int TIPO_CATALOGO_UNIDAD_MEDIDA = 5;
	public static final int TIPO_CATALOGO_MARCO_LEGAL = 27;
	public static final int TIPO_ANALISIS_RIESGO_EIA = 124;
	public static final int TIPO_DETERMINACION_AREA_INFLUENCIA_EIA = 125;
	public static final int PREDIO_ACTIVIDAD_MINERA = 134;
	public static final int ETAPA_ACTIVIDAD_MINERA = 135;
	/**
	 * Tipos calidad agua.
	 */
	public static final String CODIGO_PARAMETRO_AGUA_TULSMA = "CAT";
	public static final String CODIGO_PARAMETRO_AGUA_RAOHE = "CAR";
	public static final String ALTITUD = "altitud";
	public static final String CLIMA1 = "clima1";
	public static final String OCUPACION_AREA_INFLUENCIA = "ocai";
	public static final String PENDIENTE_SUELO1 = "pendienteSuelo1";
	public static final String TIPO_SUELO = "tisu";
	public static final String CALIDAD_SUELO = "calidadSuelo";
	public static final String PERMIABILIDAD_SUELO = "pesu";
	public static final String CONDICIONES_DRENAJE = "condicionDrenaje";
	public static final String RECURSOS_HIDRICOS = "recursoHidrico";
	public static final String NIVEL_FREATICO = "nivelFreatico";
	public static final String PRECIPITANES = "precipitaciones";
	public static final String CARACTEREISTICAS_AGUA = "caracteristicaAgua";
	public static final String CARACTEREISTICAS_AIRE = "caracteristicaAire";
	public static final String RECIRCULACION_AIRE = "recirculacionAire";
	public static final String RUIDO = "ruido";
	public static final String INSUMOS_MINERIA_ARTESANAL = "insumoMineriaArtesanal";

	
	public static final String CODIGO_ESTADO_FISICO = "estadoFisico";
	public static final String CODIGO_TIPO_TRANSPORTE_DESECHOS = "tipoTransporteRecoleccionDesecho";
	public static final String CODIGO_TIPO_MANEJO_DESECHOS_RECICLAJE = "tipoManejoDesechoReciclaje";
	public static final String CODIGO_TIPO_MANEJO_DESECHOS_REUSO = "tipoManejoDesechoReuso";
	
	public static final int MODALIDAD = 206;
	public static final String CODIGO_MODALIDAD = "MODALIDAD";			    
	public static final int LOCAL = 209;
    public static final String CODIGO_LOCAL = "local";
    public static final int MATERIALES = 210;
    public static final String CODIGO_MATERIALES = "materiales";
    public static final int VENTILACION = 211;
    public static final String CODIGO_VENTILACION = "ventilacion";
    public static final int ILUMINACION = 212;
    public static final String CODIGO_ILUMINACION = "iluminacion";
    public static final int TIPO_ENVASE = 213;
    public static final String CODIGO_TIPO_ENVASE = "tipoEnvase";

	/**
	 * /**
	 * 
	 */
	private static final long serialVersionUID = -3947900087722750769L;

	@Id
	@Getter
	@Setter
	@SequenceGenerator(name = "WASTE_DANGEROUS_TYPE_CATLYID_GENERATOR", sequenceName = "seq_caty_id", schema = "public")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WASTE_DANGEROUS_TYPE_CATLYID_GENERATOR")
	@Column(name = "caty_id", unique = true, nullable = false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "caty_type", nullable = true, length = 255)
	private String tipo;

	@Getter
	@Setter
	@Column(name = "caty_code", nullable = false, length = 255)
	private String codigo;

}
