
# FirebaseServer

El proyecto `FirebaseServer` es un microservicio encargado de gestionar interacciones con Firebase de otros microservicios del sistema REAKTOR. Este proyecto depende de [BaseServer](https://github.com/IESJandula/Base_Server/) para las configuraciones y utilidades genéricas.

## Descripción de los Servicios y Componentes

### CORSConfig
`CORSConfig` se encarga de la configuración de CORS (Cross-Origin Resource Sharing) para permitir solicitudes de diferentes dominios. Define qué orígenes, métodos HTTP y encabezados están permitidos al interactuar con el microservicio `FirebaseServer`.

### FirebaseConfig
`FirebaseConfig` gestiona la configuración de Firebase, incluyendo la inicialización del SDK de Firebase Admin con las credenciales adecuadas. Este componente carga el archivo JSON de configuración de la cuenta de servicio de Firebase y lo utiliza para interactuar con Firestore, Authentication, y otros servicios de Firebase.

### AuthorizationController
`AuthorizationController` es el controlador REST encargado de manejar las solicitudes relacionadas con la autorización de usuarios en el sistema. Proporciona el siguiente endpoint:

- **`/getCustomToken`**: Genera un token JWT personalizado para un usuario autenticado en Firebase, utilizando su UID. Este token puede ser utilizado por clientes para autenticar usuarios en otros servicios que requieren la autenticación de Firebase.

## Variables de Configuración

Las variables anotadas con `@Value` en este proyecto deben configurarse adecuadamente para que el microservicio `FirebaseServer` funcione correctamente. Estas variables se definen en el archivo `application.yml` del microservicio.

### Detalles de las Variables `@Value` y cómo obtenerlas:

1. **`reaktor.publicKeyFile`**: Ruta al archivo de clave pública.
   - **Cómo obtenerlo**: Genera la clave pública utilizando el siguiente comando:
     ```bash
     openssl rsa -in C:\claves\private_key.pem -pubout -out C:\claves\public_key.pem
     ```
   - **Dónde almacenarlo**: Coloca el archivo `public_key.pem` en `C:\claves`.

2. **`reaktor.privateKeyFile`**: Ruta al archivo de clave privada.
   - **Cómo obtenerlo**: Genera la clave privada utilizando el siguiente comando:
     ```bash
     openssl genrsa -out C:\claves\private_key.pem 2048
     ```
   - **Dónde almacenarlo**: Coloca el archivo `private_key.pem` en `C:\claves`.

3. **`reaktor.googleCredentialsFile`**: Ruta al archivo JSON de credenciales de Firebase.
   - **Cómo obtenerlo**: 
     - Ve a [Firebase Console](https://console.firebase.google.com/).
     - Accede a tu proyecto y ve a la pestaña **Configuración del proyecto** (icono de engranaje).
     - Selecciona **Cuentas de servicio** y haz clic en **Generar una nueva clave privada**.
     - Guarda el archivo JSON como `firebaseGoogleCredentials.json`.
   - **Dónde almacenarlo**: Colócalo en `C:\claves`.

4. **`reaktor.urlCors`**: Lista de orígenes permitidos para las solicitudes CORS.
   - **Cómo configurarlo**: Define los orígenes permitidos, por ejemplo: `http://localhost:5173, http://192.168.1.209:5173, http://192.168.1.181:5173, http://192.168.1.137:5173`.
   - **Dónde almacenarlo**: En el archivo `application.yml`.

5. **`reaktor.firebase_server_url`**: URL del servidor Firebase.
   - **Cómo configurarlo**: Define la URL del servidor Firebase, por ejemplo: `http://localhost:8083`.
   - **Dónde almacenarlo**: En el archivo `application.yml`.

6. **`reaktor.uidFile`**: Ruta al archivo que contiene el UID del usuario CLIENTE_IMPRESORA.
   - **Cómo obtenerlo**: Crea un archivo de texto llamado `uid_file.txt` que contenga el UID del usuario que tenga este role.
   - **Dónde almacenarlo**: Colócalo en `C:\claves`.

Asegúrate de que todos estos archivos estén correctamente ubicados y accesibles por el microservicio para garantizar su funcionamiento.


### Creación del Archivo `.p12`

Para crear un archivo `.p12` (archivo PKCS#12) para la autenticación, sigue estos pasos:

1. **Genera una clave privada y un certificado**:
   ```bash
   openssl req -x509 -newkey rsa:2048 -keyout private_key.pem -out cert.pem -days 365
   ```
2. **Convierte el archivo PEM a PKCS#12**:
   ```bash
   openssl pkcs12 -export -out firebase-server.p12 -inkey private_key.pem -in cert.pem -name "firebase-server"
   ```
3. **Dónde almacenarlo**: Guarda el archivo `firebase-server.p12` en la carpeta `C:\claves` o en un directorio seguro accesible por el microservicio.

## Dependencias

Este proyecto depende de [BaseServer](https://github.com/IESJandula/Base_Server/) para funcionalidades básicas como la autorización, almacenamiento de sesión y actualización de JARs.

## Creación de Elementos en la Colección de Firebase

Para que el sistema funcione correctamente, es necesario crear una colección en Firebase llamada `usuarios` donde se almacenarán los datos de los usuarios. Sigue los siguientes pasos para crear elementos en esta colección:

1. **Accede a la consola de Firebase**:
   Ve a [Firebase Console](https://console.firebase.google.com/) e inicia sesión con tu cuenta de Google.

2. **Selecciona tu proyecto**:
   Haz clic en tu proyecto para abrir el panel de control.

3. **Ve a Firestore Database**:
   En el menú de la izquierda, selecciona **Firestore Database** y haz clic en **Crear base de datos** si aún no lo has hecho. Asegúrate de seleccionar el modo de producción.

4. **Crear la colección `usuarios`**:
   - Haz clic en **Iniciar colección** y escribe `usuarios` como nombre de la colección.
   - Haz clic en **Siguiente** para añadir el primer documento.

5. **Añadir un documento**:
   - Define el **ID del documento**: Este será el UID del usuario, que puedes obtener desde la pestaña de Firebase Authentication.
   - Añade los siguientes campos al documento:
     - **email** (tipo: `string`): El correo electrónico del usuario.
     - **nombre** (tipo: `string`): El nombre del usuario.
     - **apellidos** (tipo: `string`): Los apellidos del usuario.
     - **roles** (tipo: `array`): Lista de roles asignados al usuario, por ejemplo: `["PROFESOR", "DIRECCIÓN"]`.

6. **Guardar el documento**:
   Haz clic en **Guardar** para crear el documento en la colección `usuarios`.

Repite estos pasos para cada usuario que necesites agregar al sistema.
