
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

Asegúrate de que todos estos archivos estén correctamente ubicados y accesibles por el microservicio para garantizar su funcionamiento.


### Creación del Archivo `.p12`

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

## Creación de usuarios

Para que el sistema funcione correctamente, es necesario tantos usuarios como personas quieras que accedan a la aplicación. Normalmente, en entorno local solo te bastará con añadir una fila con tu usuario. Para ello, necesitarás hacer un INSERT en la tabla usuario con tu correo electrónico, tu nombre y apellidos, y los roles que quieras tener. En cuanto a los roles, para poder visualizar todas las opciones de la aplicación, se aconseja que el valor sea `PROFESOR,DIRECCION,ADMINISTRADOR`

Repite este paso para cada usuario que necesites agregar al sistema.
