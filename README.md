# Knect - Backend API (Spring Boot)

API RESTful para la plataforma **Knect: Directorio Digital y Vitrina Comercial de Manizales**. Desarrollada con **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA** y base de datos **MySQL**.

---

## 🛠️ Requisitos Previos

1. **Java JDK 21** o superior instalado y configurado en el `PATH` (`java -version`).
2. **MySQL Server 8.0** o superior activo en el puerto local por defecto (`3306`).
3. **Maven** (o usar el wrapper incluido `./mvnw` en Linux/macOS o `mvnw.cmd` en Windows).

---

## ⚙️ Configuración de Base de Datos

El backend crea automáticamente la base de datos `knect_db` si no existe al arrancar.

Puedes configurar las credenciales en [`src/main/resources/application.properties`](src/main/resources/application.properties) o definir variables de entorno:

| Variable | Valor por defecto | Descripción |
| :--- | :--- | :--- |
| `PORT` | `8080` | Puerto HTTP del backend |
| `DB_URL` | `jdbc:mysql://localhost:3306/knect_db?...` | Cadena de conexión JDBC MySQL |
| `DB_USERNAME` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | `SAR?mySQL0210` | Contraseña de MySQL |
| `JWT_SECRET` | *(Clave secreta predefinida)* | Firma criptográfica de tokens JWT |
| `CORS_ORIGINS`| `http://localhost:4200,http://127.0.0.1:4200` | Orígenes frontend permitidos |

---

## 🚀 Cómo Ejecutar el Backend

### En Windows (PowerShell / CMD)
```powershell
cd c:\Users\chech\Documents\knect\knect_app_backend
.\mvnw.cmd spring-boot:run
```

### En Linux / macOS (Bash)
```bash
cd /ruta/hacia/knect_app_backend
chmod +x mvnw
./mvnw spring-boot:run
```

Una vez iniciado, el backend estará respondiendo en:  
👉 **`http://localhost:8080`**

---

## 🔑 Usuarios y Credenciales Precargadas (Seed Data)

El sistema precarga automáticamente los roles, usuarios y comercios de Manizales:

### 1. Super Administrador (Acceso global total)
* **Correo:** `admin@knect.com`
* **Contraseña:** `Admin123*`
* **Rol:** `ROLE_SUPER_ADMIN`
* **Alcance:** Sin local fijo, puede auditar, editar y gestionar cualquier comercio y usuario.

### 2. Administradores de Locales (Comercios de Manizales)
* **Contraseña general para todos los locales:** `Local123*`

| Comercio | Correo de Acceso | Categoría |
| :--- | :--- | :--- |
| **La Suiza Pastelería** | `lasuiza@knect.com` | Panadería / Café |
| **El Dauntaun** | `dauntaun@knect.com` | Café de Especialidad |
| **Café Tazzioli** | `tazzioli@knect.com` | Café / Catedral |
| **El Corcho Manizaleño** | `elcorcho@knect.com` | Gastronomía Típica |
| **Cevicheria Las Olas** | `lasolas@knect.com` | Mariscos / Restaurante |
| **La Barbería Manizales** | `labarberia@knect.com` | Barbería y Cuidado |
| **Boutique Nicole** | `nicole@knect.com` | Moda y Calzado |
| **Ferretería El Cóndor** | `elcondor@knect.com` | Ferretería y Hogar |
| **Veterinaria San Fermín** | `sanfermin@knect.com` | Mascotas y Salud |
| **Gimnasio Titanes Manizales** | `titanes@knect.com` | Fitness y Deporte |

---

## 📦 Compilación y Generación del JAR
Para generar el archivo ejecutable de producción:
```bash
./mvnw clean package -DskipTests
```
El archivo `.jar` resultante se genera en la carpeta `target/knect_app_backend-0.0.1-SNAPSHOT.jar`. Para ejecutarlo:
```bash
java -jar target/knect_app_backend-0.0.1-SNAPSHOT.jar
```
