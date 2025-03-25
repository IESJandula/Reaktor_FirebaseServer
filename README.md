
# FirebaseServer

El proyecto `FirebaseServer` es un microservicio encargado de gestionar las interacciones con otros microservicios del sistema REAKTOR ya que se encargará de asignar tokens JWT personalizados. Este proyecto depende de [BaseServer](https://github.com/IESJandula/Reaktor_BaseServer) para las configuraciones y utilidades genéricas.

## Descripción de los Servicios y Componentes

### CORSConfig
`CORSConfig` se encarga de la configuración de CORS (Cross-Origin Resource Sharing) para permitir solicitudes de diferentes dominios. Define qué orígenes, métodos HTTP y encabezados están permitidos al interactuar con el microservicio `FirebaseServer`.

### FirebaseConfig
`FirebaseConfig` gestiona la configuración de Firebase, incluyendo la inicialización del SDK de Firebase Admin con las credenciales adecuadas. Este componente carga el archivo JSON de configuración de la cuenta de servicio de Firebase y lo utiliza para interactuar con Firestore, Authentication, y otros servicios de Firebase.

### TokensManager
`TokensManager` es el controlador REST encargado de manejar las solicitudes relacionadas con la autorización de usuarios en el sistema. Proporciona los siguientes endpoints:

- **`/user`**: Genera un token JWT personalizado para un usuario autenticado en Firebase, utilizando su correo electrónico.

- **`/app`**: Genera un token JWT personalizado para una aplicación autenticada en Firebase, utilizando un identificador único. 

## Creación del Archivo `.p12`

Para crear un archivo `.p12` (archivo PKCS#12) para la autenticación, sigue estos pasos:

1. **Genera una clave privada y un certificado**:
   ```bash
   openssl req -x509 -newkey rsa:2048 -keyout private_key.pem -out cert.pem -days 365
   ```
2. **Convierte el archivo PEM a PKCS#12**:
   ```bash
   openssl pkcs12 -export -out apijandula.p12 -inkey private_key.pem -in cert.pem -name "apijandula"
   ```
3. **Dónde almacenarlo**: Guarda el archivo `apijandula.p12` en la carpeta `src/main/resources`.

## Dependencias

Este proyecto depende de [BaseServer](https://github.com/IESJandula/Base_Server/) para funcionalidades básicas como la autorización, almacenamiento de sesión y actualización de JARs.