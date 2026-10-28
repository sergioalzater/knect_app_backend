package com.knect.knect_app_backend.config;

import com.knect.knect_app_backend.entity.*;
import com.knect.knect_app_backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EstablishmentTypeRepository establishmentTypeRepository;
    private final BusinessRepository businessRepository;
    private final EstablishmentRepository establishmentRepository;
    private final ReviewRepository reviewRepository;
    private final BlogPostRepository blogPostRepository;
    private final CmsViewRepository cmsViewRepository;
    private final CmsSectionRepository cmsSectionRepository;
    private final CmsSectionDataRepository cmsSectionDataRepository;
    private final AnalyticsLogRepository analyticsLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (roleRepository.count() > 0 && businessRepository.count() > 0) {
            log.info("Base de datos Knect ya contiene información inicial.");
            return;
        }

        log.info("Inicializando datos maestros de Knect (Comercios de Manizales con usuarios dedicados)...");

        // 1. Permisos
        Permission pCms = savePerm("MANAGE_CMS", "Administrar CMS del Home y Noticias");
        Permission pDir = savePerm("MANAGE_DIRECTORY", "CRUD global del directorio de comercios");
        Permission pOwn = savePerm("MANAGE_OWN_BUSINESS", "Administrar ficha, sedes y reseñas de su propio comercio");
        Permission pRev = savePerm("WRITE_REVIEW", "Calificar y comentar comercios como cliente");

        // 2. Roles (RNF-03)
        Role superAdminRole = roleRepository.save(Role.builder()
                .name("SUPER_ADMIN")
                .description("Super Administrador de la plataforma Knect")
                .permissions(Set.of(pCms, pDir, pOwn, pRev))
                .build());

        Role businessAdminRole = roleRepository.save(Role.builder()
                .name("BUSINESS_ADMIN")
                .description("Administrador de Local / Marca Comercial")
                .permissions(Set.of(pOwn, pRev))
                .build());

        Role clientRole = roleRepository.save(Role.builder()
                .name("CLIENT")
                .description("Usuario Cliente Comprador")
                .permissions(Set.of(pRev))
                .build());

        // 3. Usuarios dedicados
        // Super Admin Global: No tiene ningún local propio amarrado, supervisa y accede a todo
        User superAdmin = userRepository.save(User.builder()
                .name("Sergio")
                .lastName("Alzate Román (Super Admin)")
                .phone("+573001112233")
                .email("admin@knect.com")
                .password(passwordEncoder.encode("Admin123*"))
                .isActive(true)
                .roles(Set.of(superAdminRole))
                .build());

        // Usuario para Local 1: Anatilde Cocina de Autor
        User ownerAnatilde = userRepository.save(User.builder()
                .name("Carlos")
                .lastName("Restrepo (Anatilde)")
                .phone("+573104445566")
                .email("anatilde@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario para Local 2: Buffalo Republic Smash Lab
        User ownerBuffalo = userRepository.save(User.builder()
                .name("Valentina")
                .lastName("Ospina (Buffalo Smash)")
                .phone("+573157778899")
                .email("buffalo@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario para Local 3: Pastelería La Suiza
        User ownerLaSuiza = userRepository.save(User.builder()
                .name("Guillermo")
                .lastName("Villegas (La Suiza)")
                .phone("+573116203344")
                .email("lasuiza@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario para Local 4: Santo Kaffeto & Differente
        User ownerSantoKaffeto = userRepository.save(User.builder()
                .name("Felipe")
                .lastName("Cardona (Santo Kaffeto)")
                .phone("+573128504411")
                .email("santokaffeto@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario para Local 5: Sancancio & Cable Plaza Moda Local
        User ownerSancancioModa = userRepository.save(User.builder()
                .name("Mariana")
                .lastName("Duque (Moda Sancancio)")
                .phone("+573189001122")
                .email("sancanciomoda@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario para Local 6: Thinka Coworking & Wellness Hub
        User ownerThinka = userRepository.save(User.builder()
                .name("Alejandro")
                .lastName("Giraldo (Thinka)")
                .phone("+573217004545")
                .email("thinka@knect.com")
                .password(passwordEncoder.encode("Marca123*"))
                .isActive(true)
                .roles(Set.of(businessAdminRole))
                .build());

        // Usuario cliente comprador
        User clientUser = userRepository.save(User.builder()
                .name("María Camila")
                .lastName("Gómez")
                .phone("+573209990011")
                .email("cliente@knect.com")
                .password(passwordEncoder.encode("Cliente123*"))
                .isActive(true)
                .roles(Set.of(clientRole))
                .build());

        // 4. Categorías / Tipos de Establecimiento (RF-04)
        EstablishmentType catRestaurante = establishmentTypeRepository.save(EstablishmentType.builder()
                .name("Restaurante")
                .nameEn("Restaurant")
                .icon("utensils")
                .description("Gastronomía, cocina de autor, gastrobares y cocinas ocultas en Manizales")
                .build());

        EstablishmentType catCafe = establishmentTypeRepository.save(EstablishmentType.builder()
                .name("Café & Panadería")
                .nameEn("Coffee & Bakery")
                .icon("coffee")
                .description("Cafés de especialidad del Paisaje Cultural Cafetero y pastelerías tradicionales")
                .build());

        EstablishmentType catTienda = establishmentTypeRepository.save(EstablishmentType.builder()
                .name("Tienda & Comercio")
                .nameEn("Retail & Store")
                .icon("shopping-bag")
                .description("Tiendas de moda, centros comerciales, diseño y comercio local en Manizales")
                .build());

        EstablishmentType catServicios = establishmentTypeRepository.save(EstablishmentType.builder()
                .name("Servicios")
                .nameEn("Services")
                .icon("briefcase")
                .description("Coworking, tecnología, gimnasios, salud y bienestar en Manizales")
                .build());

        // 5. Comercios Reales de Manizales con su respectivo Usuario Administrador
        // Comercio 1: Anatilde Cocina de Autor & Gastrobar -> Propietario: ownerAnatilde (anatilde@knect.com)
        Business b1 = businessRepository.save(Business.builder()
                .owner(ownerAnatilde)
                .establishmentType(catRestaurante)
                .name("Anatilde Cocina de Autor & Gastrobar")
                .legalName("Anatilde Gastronomía Caldense S.A.S.")
                .taxNumber("901.452.881-3")
                .description("Cocina colombiana de autor en Manizales que reinterpreta platos tradicionales de la cordillera central con técnicas contemporáneas. Sedes en el sector de El Cable y el bulevar gastronómico de Milán (Zona G).")
                .descriptionEn("Signature Colombian cuisine in Manizales reinterpreting traditional recipes from the Central Andes with contemporary techniques. Locations in El Cable and Milán Gourmet District.")
                .primaryColor("#D97706")
                .logoUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("El Cable")
                .address("Calle 66 # 23-31, Sector El Cable, Manizales")
                .phone("+57 310 444 5566")
                .whatsapp("573104445566")
                .email("anatilde@knect.com")
                .website("https://anatildemanizales.co")
                .isGhostKitchen(false)
                .hasDineIn(true)
                .isFeatured(true)
                .isActive(true)
                .ratingAvg(4.9)
                .reviewCount(2)
                .viewCount(412)
                .searchAppearanceCount(620)
                .whatsappClickCount(154)
                .build());

        Establishment b1s1 = establishmentRepository.save(Establishment.builder()
                .business(b1).establishmentType(catRestaurante)
                .name("Sede Principal El Cable")
                .description("Salón principal y terraza frente al corredor universitario y empresarial de El Cable.")
                .schedules("Mar - Sáb: 12:00 PM - 10:00 PM | Dom: 12:00 PM - 4:00 PM")
                .neighborhood("El Cable")
                .address("Calle 66 # 23-31, Sector El Cable, Manizales")
                .latitude(5.0558).longitude(-75.4855)
                .phone("+57 310 444 5566").whatsapp("573104445566")
                .email("anatilde@knect.com")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(82)
                .build());

        Establishment b1s2 = establishmentRepository.save(Establishment.builder()
                .business(b1).establishmentType(catRestaurante)
                .name("Sede Bulevar Milán (Zona G)")
                .description("Ambiente gastrobar y coctelería de autor en la Zona G de Milán con vista a la montaña.")
                .schedules("Mar - Dom: 12:00 PM - 11:00 PM")
                .neighborhood("Milán")
                .address("Calle 75 # 23-45, Bulevar de Milán, Manizales")
                .latitude(5.0459).longitude(-75.4799)
                .phone("+57 310 444 5567").whatsapp("573104445567")
                .email("anatilde@knect.com")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(48)
                .build());

        Establishment b1s3 = establishmentRepository.save(Establishment.builder()
                .business(b1).establishmentType(catRestaurante)
                .name("Sede Avenida Santander (Palermo)")
                .description("Punto gastronómico sobre la Av. Santander con parqueadero privado y salón de eventos.")
                .schedules("Lun - Dom: 12:00 PM - 10:00 PM")
                .neighborhood("Palermo")
                .address("Avenida Santander # 75-75, Sector Palermo, Manizales")
                .latitude(5.0488).longitude(-75.4878)
                .phone("+57 310 444 5568").whatsapp("573104445568")
                .email("anatilde@knect.com")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(24)
                .build());

        // Comercio 2: Buffalo Republic Smash Lab -> Propietario: ownerBuffalo (buffalo@knect.com)
        Business b2 = businessRepository.save(Business.builder()
                .owner(ownerBuffalo)
                .establishmentType(catRestaurante)
                .name("Buffalo Republic Smash Lab")
                .legalName("Buffalo Smash Cloud Kitchens Manizales S.A.S.")
                .taxNumber("901.789.321-0")
                .description("Hamburguesas artesanales tipo Smash Burger en pan brioche de papa criolla caldense y costillas ahumadas. Operamos bajo modalidad de Cocina Oculta (Dark Kitchen) en Manizales para entregas ultra rápidas por WhatsApp.")
                .descriptionEn("Artisanal Smash Burgers on Caldas potato brioche buns and smoked ribs. Operating as a Dark Kitchen in Manizales for ultra-fast WhatsApp delivery.")
                .primaryColor("#E11D48")
                .logoUrl("https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("Palermo")
                .address("Calle 69 # 24-36, Barrio Palermo, Manizales")
                .phone("+57 315 777 8899")
                .whatsapp("573157778899")
                .email("buffalo@knect.com")
                .website("https://buffalosmashmanizales.co")
                .isGhostKitchen(true)
                .hasDineIn(false)
                .isFeatured(true)
                .isActive(true)
                .ratingAvg(4.9)
                .reviewCount(1)
                .viewCount(318)
                .searchAppearanceCount(490)
                .whatsappClickCount(192)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b2).establishmentType(catRestaurante)
                .name("Hub Cocina Oculta Palermo / El Cable (Oriente)")
                .description("Centro de despacho exclusivo para domicilios en El Cable, Milán, Palermo, Sancancio y La Estrella.")
                .schedules("Todos los días: 11:30 AM - 11:30 PM")
                .neighborhood("Palermo")
                .address("Calle 69 # 24-36, Barrio Palermo, Manizales")
                .latitude(5.0475).longitude(-75.4910)
                .phone("+57 315 777 8899").whatsapp("573157778899")
                .isGhostKitchen(true).hasDineIn(false).isActive(true).whatsappClicks(122)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b2).establishmentType(catRestaurante)
                .name("Hub Cocina Oculta Chipre / Centro (Occidente)")
                .description("Despacho rápido para Chipre, Francia, Los Alcázares, Centro y Fundadores.")
                .schedules("Todos los días: 12:00 PM - 11:00 PM")
                .neighborhood("Chipre")
                .address("Carrera 9C # 8-42, Sector Mirador de Chipre, Manizales")
                .latitude(5.0758).longitude(-75.5254)
                .phone("+57 315 777 8890").whatsapp("573157778890")
                .isGhostKitchen(true).hasDineIn(false).isActive(true).whatsappClicks(70)
                .build());

        // Comercio 3: Pastelería La Suiza & Café de Origen -> Propietario: ownerLaSuiza (lasuiza@knect.com)
        Business b3 = businessRepository.save(Business.builder()
                .owner(ownerLaSuiza)
                .establishmentType(catCafe)
                .name("Pastelería La Suiza & Café de Origen")
                .legalName("Pastelería La Suiza Manizales S.A.S.")
                .taxNumber("890.801.634-9")
                .description("Tradición pastelera y gastronómica de Manizales desde 1951. Milhojas artesanales, panadería europea de masa madre y métodos de filtrado con cafés especiales premiados de Caldas en El Cable y el Centro.")
                .descriptionEn("Iconic bakery and coffee house in Manizales since 1951. Artisanal mille-feuille, European sourdough bakery, and award-winning Caldas specialty coffee in El Cable and Downtown.")
                .primaryColor("#059669")
                .logoUrl("https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("El Cable")
                .address("Carrera 23B # 64-06, Sector El Cable, Manizales")
                .phone("+57 (606) 885 0545")
                .whatsapp("573116203344")
                .email("lasuiza@knect.com")
                .website("https://lasuiza.com.co")
                .isGhostKitchen(false)
                .hasDineIn(true)
                .isFeatured(true)
                .isActive(true)
                .ratingAvg(5.0)
                .reviewCount(1)
                .viewCount(380)
                .searchAppearanceCount(540)
                .whatsappClickCount(134)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b3).establishmentType(catCafe)
                .name("Sede El Cable (Zona Universitaria)")
                .description("Sede icónica en la zona rosa de El Cable con terraza, repostería tradicional y barra de espresso.")
                .schedules("Lun - Dom: 7:30 AM - 8:30 PM")
                .neighborhood("El Cable")
                .address("Carrera 23B # 64-06, Sector El Cable, Manizales")
                .latitude(5.0563).longitude(-75.4864)
                .phone("+57 (606) 885 0545").whatsapp("573116203344")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(84)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b3).establishmentType(catCafe)
                .name("Sede Centro Histórico (Carrera 23)")
                .description("Punto clásico en el corazón bancario y cultural del Centro Histórico de Manizales.")
                .schedules("Lun - Sáb: 8:00 AM - 7:30 PM")
                .neighborhood("Centro")
                .address("Carrera 23 # 26-57, Centro Histórico, Manizales")
                .latitude(5.0689).longitude(-75.5174)
                .phone("+57 (606) 884 6331").whatsapp("573116203345")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(50)
                .build());

        // Comercio 4: Santo Kaffeto & Differente Roasters -> Propietario: ownerSantoKaffeto (santokaffeto@knect.com)
        Business b4 = businessRepository.save(Business.builder()
                .owner(ownerSantoKaffeto)
                .establishmentType(catCafe)
                .name("Santo Kaffeto & Differente Roasters")
                .legalName("Tostadores Especiales de Caldas S.A.S.")
                .taxNumber("900.912.408-1")
                .description("Laboratorio de barismo y café de especialidad cultivado en las montañas de Caldas. Espacios con WiFi de alta velocidad para trabajo remoto en La Estrella, Avenida Santander y C.C. Cable Plaza.")
                .descriptionEn("Barista lab and specialty coffee grown in the mountains of Caldas. High-speed WiFi workspaces in La Estrella, Avenida Santander, and Cable Plaza Mall.")
                .primaryColor("#0D9488")
                .logoUrl("https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1442512595331-e89e73853f31?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("La Estrella")
                .address("Calle 60 # 24A-36, Sector La Estrella, Manizales")
                .phone("+57 312 850 4411")
                .whatsapp("573128504411")
                .email("santokaffeto@knect.com")
                .website("https://santokaffeto.com")
                .isGhostKitchen(false)
                .hasDineIn(true)
                .isFeatured(true)
                .isActive(true)
                .ratingAvg(4.9)
                .reviewCount(0)
                .viewCount(265)
                .searchAppearanceCount(390)
                .whatsappClickCount(98)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b4).establishmentType(catCafe)
                .name("Sede La Estrella / Av. Santander")
                .description("Casa cafetera especializada en microlotes, métodos V60/Chemex y escuela de barismo.")
                .schedules("Lun - Sáb: 8:00 AM - 8:00 PM")
                .neighborhood("La Estrella")
                .address("Calle 60 # 24A-36, Sector La Estrella, Manizales")
                .latitude(5.0598).longitude(-75.4925)
                .phone("+57 312 850 4411").whatsapp("573128504411")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(58)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b4).establishmentType(catCafe)
                .name("Barra de Experiencia C.C. Cable Plaza")
                .description("Punto de degustación y venta de café en grano dentro del Centro Comercial Cable Plaza.")
                .schedules("Lun - Dom: 10:00 AM - 8:30 PM")
                .neighborhood("El Cable")
                .address("Carrera 23 # 65-11, C.C. Cable Plaza Local 108, Manizales")
                .latitude(5.0566).longitude(-75.4868)
                .phone("+57 312 850 4412").whatsapp("573128504412")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(40)
                .build());

        // Comercio 5: Sancancio & Cable Plaza Concept Store -> Propietario: ownerSancancioModa (sancanciomoda@knect.com)
        Business b5 = businessRepository.save(Business.builder()
                .owner(ownerSancancioModa)
                .establishmentType(catTienda)
                .name("Sancancio & Cable Plaza Moda Local")
                .legalName("Vitrina Comercial de Caldas S.A.S.")
                .taxNumber("901.119.402-5")
                .description("Tienda multimarca de moda, marroquinería en cuero, calzado y diseño local ubicada en los centros comerciales Sancancio y Cable Plaza de Manizales. Asesoría y envíos urbanos por WhatsApp.")
                .descriptionEn("Multi-brand fashion, leather goods, footwear, and local design concept store located in Sancancio and Cable Plaza malls in Manizales. Personal styling and delivery via WhatsApp.")
                .primaryColor("#0284C7")
                .logoUrl("https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("Sancancio")
                .address("Carrera 27A # 66-30, C.C. Sancancio, Manizales")
                .phone("+57 318 900 1122")
                .whatsapp("573189001122")
                .email("sancanciomoda@knect.com")
                .website("https://sancancio.com")
                .isGhostKitchen(false)
                .hasDineIn(true)
                .isFeatured(true)
                .isActive(true)
                .ratingAvg(4.8)
                .reviewCount(0)
                .viewCount(210)
                .searchAppearanceCount(305)
                .whatsappClickCount(74)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b5).establishmentType(catTienda)
                .name("Boutique Centro Comercial Sancancio")
                .description("Showroom principal de moda, calzado y accesorios con parqueadero en el C.C. Sancancio.")
                .schedules("Lun - Sáb: 10:00 AM - 8:00 PM | Dom: 11:00 AM - 6:00 PM")
                .neighborhood("Sancancio")
                .address("Carrera 27A # 66-30, C.C. Sancancio Local 212, Manizales")
                .latitude(5.0477).longitude(-75.4923)
                .phone("+57 318 900 1122").whatsapp("573189001122")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(46)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b5).establishmentType(catTienda)
                .name("Tienda Centro Comercial Cable Plaza")
                .description("Punto de venta y recolección rápida en el corazón comercial de El Cable.")
                .schedules("Lun - Sáb: 10:00 AM - 8:30 PM | Dom: 10:00 AM - 7:00 PM")
                .neighborhood("El Cable")
                .address("Carrera 23 # 65-11, C.C. Cable Plaza Piso 2, Manizales")
                .latitude(5.0564).longitude(-75.4862)
                .phone("+57 318 900 1123").whatsapp("573189001123")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(28)
                .build());

        // Comercio 6: Thinka Coworking & Wellness Hub -> Propietario: ownerThinka (thinka@knect.com)
        Business b6 = businessRepository.save(Business.builder()
                .owner(ownerThinka)
                .establishmentType(catServicios)
                .name("Thinka Coworking & Wellness Hub")
                .legalName("Thinka Espacios & Bienestar Manizales S.A.S.")
                .taxNumber("901.550.901-8")
                .description("Espacios corporativos flexibles, oficinas privadas en el Centro de Negocios Siglo XXI (El Cable), salas de capacitación en Palermo y convenio de acondicionamiento físico en Sancancio.")
                .descriptionEn("Flexible corporate workspaces, private offices at Siglo XXI Business Center (El Cable), training rooms in Palermo, and fitness wellness center in Sancancio.")
                .primaryColor("#7C3AED")
                .logoUrl("https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=300&q=80")
                .bannerUrl("https://images.unsplash.com/photo-1527192491265-7e15c55b1ed2?auto=format&fit=crop&w=1400&q=80")
                .neighborhood("El Cable")
                .address("Carrera 23C # 62-06, Edificio Siglo XXI, El Cable, Manizales")
                .phone("+57 321 700 4545")
                .whatsapp("573217004545")
                .email("thinka@knect.com")
                .website("https://thinka.com.co")
                .isGhostKitchen(false)
                .hasDineIn(true)
                .isFeatured(false)
                .isActive(true)
                .ratingAvg(4.9)
                .reviewCount(0)
                .viewCount(178)
                .searchAppearanceCount(260)
                .whatsappClickCount(81)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b6).establishmentType(catServicios)
                .name("Sede Thinka Edificio Siglo XXI (El Cable)")
                .description("Piso corporativo con oficinas privadas, cabinas de videollamadas y terraza mirador.")
                .schedules("Lun - Vie: 7:00 AM - 8:00 PM | Sáb: 8:00 AM - 2:00 PM")
                .neighborhood("El Cable")
                .address("Carrera 23C # 62-06, Centro de Negocios Siglo XXI, Manizales")
                .latitude(5.0574).longitude(-75.4881)
                .phone("+57 321 700 4545").whatsapp("573217004545")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(52)
                .build());

        establishmentRepository.save(Establishment.builder()
                .business(b6).establishmentType(catServicios)
                .name("Sede Wellness & Fitness C.C. Sancancio")
                .description("Centro de acondicionamiento físico, zonas húmedas y coworking deportivo en Sancancio.")
                .schedules("Lun - Vie: 5:00 AM - 10:00 PM | Sáb - Dom: 7:00 AM - 4:00 PM")
                .neighborhood("Sancancio")
                .address("Carrera 27A # 66-30, Interior C.C. Sancancio, Manizales")
                .latitude(5.0479).longitude(-75.4920)
                .phone("+57 321 700 4546").whatsapp("573217004546")
                .isGhostKitchen(false).hasDineIn(true).isActive(true).whatsappClicks(29)
                .build());

        // 6. Reseñas con respuesta oficial del administrador (RF-10, RF-12, RF-15)
        reviewRepository.save(Review.builder()
                .business(b1)
                .establishment(b1s1)
                .user(clientUser)
                .rating(5)
                .comment("¡Espectacular experiencia en la sede de El Cable en Manizales! Los platos de autor con ingredientes caldenses estuvieron increíbles y reservamos mesa directo por WhatsApp en menos de dos minutos.")
                .adminReply("¡Muchas gracias por visitarnos en El Cable, María Camila! Nos alegra enormemente saber que disfrutaste nuestra propuesta gastronómica en Manizales. ¡Te esperamos pronto!")
                .adminReplyAt(LocalDateTime.now().minusDays(1))
                .build());

        reviewRepository.save(Review.builder()
                .business(b1)
                .establishment(b1s2)
                .user(ownerBuffalo)
                .rating(5)
                .comment("El ambiente nocturno en la sede del Bulevar de Milán (Zona G) es inigualable. Muy recomendados los cócteles de café y uchuva.")
                .build());

        reviewRepository.save(Review.builder()
                .business(b2)
                .user(clientUser)
                .rating(5)
                .comment("Pedí por el botón de WhatsApp desde el Hub de Palermo y llegó a mi apartamento en El Cable en 20 minutos súper caliente. La mejor Smash Burger de Manizales.")
                .adminReply("¡Gracias por tus 5 estrellas! Nuestro equipo de Cocina Oculta en Palermo y Chipre trabaja al segundo para cubrir toda Manizales.")
                .adminReplyAt(LocalDateTime.now().minusHours(12))
                .build());

        reviewRepository.save(Review.builder()
                .business(b3)
                .user(clientUser)
                .rating(5)
                .comment("La milhoja de La Suiza en El Cable acompañada de un café filtrado de origen caldense es una parada obligatoria en Manizales.")
                .build());

        // 7. Noticias / Artículos del Blog contextualizados en Manizales (RF-02, RF-05, RF-19)
        blogPostRepository.save(BlogPost.builder()
                .title("Knect impulsa la transformación digital de más de 150 comercios locales en Manizales")
                .titleEn("Knect drives digital transformation for over 150 local businesses in Manizales")
                .slug("knect-impulsa-transformacion-digital-comercios-manizales")
                .summary("Conoce cómo los establecimientos de El Cable, Milán, Palermo, Sancancio y el Centro Histórico están multiplicando sus ventas directas vía WhatsApp y mapas interactivos.")
                .summaryEn("Learn how establishments in El Cable, Milán, Palermo, Sancancio, and Downtown Manizales are multiplying their direct sales via WhatsApp and interactive maps.")
                .content("La digitalización del comercio local en Manizales se ha convertido en el motor principal del crecimiento empresarial en Caldas. A través de Knect, los establecimientos de sectores emblemáticos como El Cable, la Zona G de Milán, Palermo, Sancancio y Chipre cuentan ahora con una vitrina personalizada que respeta los colores e identidad de su marca.\n\nGracias a la georreferenciación de múltiples sedes y el botón de contacto directo a WhatsApp sin intermediarios ni comisiones ocultas, los manizaleños y visitantes pueden identificar en segundos la sucursal más cercana, verificar si un restaurante cuenta con atención en mesa o funciona como Cocina Oculta (Dark Kitchen), y realizar su pedido o reserva al instante.")
                .contentEn("Digitalization of local commerce in Manizales has become the primary engine of business growth in Caldas. Through Knect, establishments across iconic districts like El Cable, Milán Gourmet Zone, Palermo, Sancancio, and Chipre enjoy a customized storefront that showcases their brand identity.\n\nWith multi-branch interactive mapping and commission-free direct WhatsApp messaging, locals and visitors can locate the nearest branch in seconds, check whether a restaurant offers dine-in service or operates as a Dark Kitchen, and place their order or reservation immediately.")
                .imageUrl("https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?auto=format&fit=crop&w=1200&q=80")
                .authorName("Equipo Editorial Knect Manizales")
                .publishedAt(LocalDateTime.now().minusDays(4))
                .isActive(true)
                .build());

        blogPostRepository.save(BlogPost.builder()
                .title("Tendencia Gastronómica en Manizales: Cocinas Ocultas en Palermo vs. Experiencia en Mesa en Milán")
                .titleEn("Dining Trends in Manizales: Dark Kitchens in Palermo vs. Dine-In Experience in Milán")
                .slug("tendencia-gastronomica-manizales-cocinas-ocultas-vs-milan")
                .summary("Descubre por qué la claridad en la modalidad operativa de los restaurantes mejora la experiencia de los comensales en la capital de Caldas.")
                .summaryEn("Discover why transparency in restaurant operating modes improves the dining experience across the capital of Caldas.")
                .content("Uno de los mayores retos para los amantes de la gastronomía en Manizales es saber con certeza si una marca gastronómica cuenta con salón físico para atención al público (como los gastrobares del Bulevar de Milán o El Cable) o si opera como un laboratorio culinario exclusivo para domicilios (Cocina Oculta / Dark Kitchen).\n\nEn Knect hemos incorporado distintivos claros en cada ficha comercial para que antes de desplazarte por la Avenida Santander o pedir por WhatsApp sepas exactamente qué modalidad ofrece cada sede.")
                .contentEn("One of the biggest challenges for food lovers in Manizales is knowing for sure whether a culinary brand has a physical dining room (like the gastrobars in Milán or El Cable) or operates exclusively as a delivery-only Dark Kitchen.\n\nAt Knect, we have built clear operational badges into every restaurant profile so you know before heading down Avenida Santander or messaging on WhatsApp exactly how each branch operates.")
                .imageUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=1200&q=80")
                .authorName("Sergio Alzate Román")
                .publishedAt(LocalDateTime.now().minusDays(2))
                .isActive(true)
                .build());

        blogPostRepository.save(BlogPost.builder()
                .title("Ruta del Café de Especialidad y Pastelería en El Cable, La Estrella y el Centro de Manizales")
                .titleEn("Specialty Coffee & Bakery Route in El Cable, La Estrella, and Downtown Manizales")
                .slug("ruta-del-cafe-de-especialidad-manizales-el-cable-la-estrella")
                .summary("Recorrimos las mejores barras de café de origen caldense y pastelerías tradicionales para trabajar remoto o disfrutar de una tarde en Manizales.")
                .summaryEn("We toured the top Caldas single-origin coffee bars and traditional bakeries for remote work or an afternoon in Manizales.")
                .content("Como corazón del Paisaje Cultural Cafetero, Manizales ofrece una de las escenas de café de especialidad y pastelería más ricas de Colombia. Desde íconos tradicionales como La Suiza en El Cable y el Centro, hasta barras de microlotes como Santo Kaffeto, Differente Coffee y Café San Alberto en Cable Plaza.\n\nEn nuestro directorio puedes filtrar por barrio (El Cable, La Estrella, Milán, Centro, Palermo, Sancancio) para descubrir tu próximo café favorito y dejar tu reseña con estrellas.")
                .contentEn("As the heart of the Coffee Cultural Landscape, Manizales offers one of Colombia's richest specialty coffee and bakery scenes. From traditional icons like La Suiza in El Cable and Downtown, to micro-lot bars like Santo Kaffeto, Differente Coffee, and Café San Alberto at Cable Plaza.\n\nUse Knect's neighborhood filters (El Cable, La Estrella, Milán, Centro, Palermo, Sancancio) to discover your next favorite spot and leave your star rating.")
                .imageUrl("https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=1200&q=80")
                .authorName("Comunidad Knect Caldas")
                .publishedAt(LocalDateTime.now().minusDays(1))
                .isActive(true)
                .build());

        // 8. CMS del Home (view -> section -> section_data)
        CmsView homeView = cmsViewRepository.save(CmsView.builder()
                .name("HOME")
                .description("Configuración de Portada Dinámica Knect Manizales")
                .build());

        CmsSection mainSection = cmsSectionRepository.save(CmsSection.builder()
                .view(homeView)
                .name("MAIN_CONTENT")
                .description("Textos e imágenes administrables del Home")
                .build());

        saveCmsData(mainSection, "heroTitleEs", "Conectamos a Manizales con sus mejores comercios locales");
        saveCmsData(mainSection, "heroTitleEn", "Connecting Manizales with its best local businesses");
        saveCmsData(mainSection, "heroSubtitleEs", "Explora restaurantes, cafés de especialidad, tiendas y servicios en El Cable, Milán, Palermo, Sancancio, Chipre y el Centro. Ubica sus sedes en el mapa interactivo y habla directo por WhatsApp.");
        saveCmsData(mainSection, "heroSubtitleEn", "Explore restaurants, specialty cafes, stores, and services across El Cable, Milán, Palermo, Sancancio, Chipre, and Downtown Manizales. Locate branches on the interactive map and chat via WhatsApp.");
        saveCmsData(mainSection, "heroBannerUrl", "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&q=80");
        saveCmsData(mainSection, "heroCtaTextEs", "Explorar Directorio de Manizales");
        saveCmsData(mainSection, "heroCtaTextEn", "Explore Manizales Directory");
        saveCmsData(mainSection, "featuredTitleEs", "Locales Destacados en Manizales");
        saveCmsData(mainSection, "featuredTitleEn", "Featured Establishments in Manizales");
        saveCmsData(mainSection, "newsTitleEs", "Últimas Noticias y Artículos de la Ciudad");
        saveCmsData(mainSection, "newsTitleEn", "Latest City News & Articles");
        saveCmsData(mainSection, "contactTitleEs", "Contáctanos – Administración Knect Manizales");
        saveCmsData(mainSection, "contactTitleEn", "Contact Us – Knect Manizales Administration");
        saveCmsData(mainSection, "contactSubtitleEs", "¿Quieres vincular tu establecimiento en Manizales al directorio o necesitas soporte? Envíanos un mensaje y recibirás respuesta inmediata.");
        saveCmsData(mainSection, "contactSubtitleEn", "Want to list your Manizales establishment in the directory or need support? Send us a message and get an immediate response.");
        saveCmsData(mainSection, "contactEmail", "administracion@knect.com");
        saveCmsData(mainSection, "contactPhone", "+57 (606) 885 9000");
        saveCmsData(mainSection, "contactAddress", "Centro de Negocios Siglo XXI, Sector El Cable, Manizales, Caldas");

        // 9. Registros iniciales de analítica de búsqueda en Manizales (RF-20)
        List.of("Café en El Cable", "Restaurantes Zona G Milán", "La Suiza Manizales", "Hamburguesas Palermo", "tiendas C.C. Sancancio")
                .forEach(q -> analyticsLogRepository.save(AnalyticsLog.builder()
                        .eventType("SEARCH")
                        .searchQuery(q)
                        .build()));

        log.info("Datos maestros de Knect Manizales inicializados correctamente con usuarios individuales para cada local.");
    }

    private Permission savePerm(String code, String desc) {
        return permissionRepository.save(Permission.builder()
                .code(code)
                .description(desc)
                .build());
    }

    private void saveCmsData(CmsSection section, String key, String value) {
        cmsSectionDataRepository.save(CmsSectionData.builder()
                .section(section)
                .name(key)
                .content(value)
                .type("TEXT")
                .build());
    }
}
