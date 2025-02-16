
# API CIUDADES ABIERTAS - MÓDULO PADRÓN

Este módulo integra el vocabulario de padrón para las consultas semánticas (RDF)  se basa en estructuras de cubos, definidas en el módulo DSD, actualmente los cubos implantados son:
-	Cubo Edad
-   Cubo Edad Grupo Quinquenales
-	Cubo Estudios
-	Cubo Indicadores
-	Cubo Nacionalidad
-   Cubo Pais Nacimiento
-	Cubo Procedencia


## Configuración del Módulo

La Aplicación permite realizar configuraciones específicas asociadas a cada uno de los módulos que se integran.

Para realizar esto lo único que se necesita es crear un fichero **padron.properties** e incluirlo en la carpeta de recursos del proyecto WEB (API_WEB/src/main/resources/) donde se distribuyen el resto de ficheros de configuración.

Esto permite que el módulo de **padron** utilice la información que contenga este fichero cuando se ejecutan peticiones sobre dicho módulo.

A continuación se detallan los parámetros que se pueden configurar en este fichero **padron.properties**.


```sh
#Configuracion BBDD
db.driver=
db.url=
db.user=
db.password=
db.initialSize=
db.maxActive=
db.maxIdle=
db.minIdle=
db.hibernate.dialect=
db.validationQuery=

#Configuracion Properties Hibernates
#posibles valores true o false activa que se muestre en el log de salida todas las sentencias de hibernate que se ejecutan en la aplicación.
hibernate.show_sql=
#posibles valores true o false cuando esta activo el log de hibernate las sentencias de SQL se les da formato para que puedan verse en mas de una unica linea de log.
hibernate.format_sql=
#posibles valores true o false permite que se puedan añadir comentarios a las sentencias de SQL mediante programación
hibernate.use_sql_comments=


#Control para el listado de peticiones 
peticiones.identificadas.public_auth=
peticiones.identificadas.basic_auth=
peticiones.identificadas.admin_auth= 


```

### Configuración BBDD

- Ejemplo configuración BBDD SQLServer

```sh
#Configuracion SQLServer
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
db.url=jdbc:sqlserver://localhost:1433;databaseName=ciudadesAbiertas
db.user=ciudadesAbiertas
db.password=ciudadesAbiertas
db.initialSize=5
db.maxActive=10
db.maxIdle=10
db.minIdle=0
db.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
db.validationQuery=Select 1


#Configuracion Properties Hibernates
#posibles valores true o false activa que se muestre en el log de salida todas las sentencias de hibernate que se ejecutan en la aplicación.
hibernate.show_sql=false
#posibles valores true o false cuando esta activo el log de hibernate las sentencias de SQL se les da formato para que puedan verse en mas de una unica linea de log.
hibernate.format_sql=true
#posibles valores true o false permite que se puedan añadir comentarios a las sentencias de SQL mediante programación
hibernate.use_sql_comments=true
```


- Ejemplo configuración BBDD MySQL

```sh
#Configuracion Mysql
db.driver=com.mysql.jdbc.Driver
db.url=jdbc:mysql://127.0.0.1:3306/ciudadesAbiertas
db.user=ciudadesAbiertas
db.password=ciudadesAbiertas
db.initialSize=5
db.maxActive=10
db.maxIdle=10
db.minIdle=0
db.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
db.validationQuery=Select 1

#Configuracion Properties Hibernates
#posibles valores true o false activa que se muestre en el log de salida todas las sentencias de hibernate que se ejecutan en la aplicación.
hibernate.show_sql=false
#posibles valores true o false cuando esta activo el log de hibernate las sentencias de SQL se les da formato para que puedan verse en mas de una unica linea de log.
hibernate.format_sql=true
#posibles valores true o false permite que se puedan añadir comentarios a las sentencias de SQL mediante programación
hibernate.use_sql_comments=true
```


- Ejemplo configuración BBDD Oracle

```sh
#Configuracion Oracle
db.driver=oracle.jdbc.OracleDriver
db.url=jdbc:oracle:thin:@127.0.0.1:1521:xe
db.user=ciudadesAbiertas
db.password=ciudadesAbiertas
db.initialSize=5
db.maxActive=10
db.maxIdle=10
db.minIdle=0
db.hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
db.validationQuery=Select 1 from dual

#Configuracion Properties Hibernates
#posibles valores true o false activa que se muestre en el log de salida todas las sentencias de hibernate que se ejecutan en la aplicación.
hibernate.show_sql=false
#posibles valores true o false cuando esta activo el log de hibernate las sentencias de SQL se les da formato para que puedan verse en mas de una unica linea de log.
hibernate.format_sql=true
#posibles valores true o false permite que se puedan añadir comentarios a las sentencias de SQL mediante programación
hibernate.use_sql_comments=true
```


### Configurar Seguridad de las Operaciones (Servicios disponibles de la API)
A continuación describimos brevemente el funcionamiento de estos parámetros
- **peticiones.identificadas.public_auth=**  Parametro para incluir las peticiones Públicas sin identificación
- **peticiones.identificadas.basic_auth=** Parámetro para incluir las peticiones con identificación de usuario con rol básico
- **peticiones.identificadas.admin_auth=**
Parámetro para incluir las peticiones con identificación de usuario con rol administrador. 

Si no se incluyen todos o algunos de estos parámetros se aplica la configuración básica por defecto que seria la que se describe a continuación:
- Auntentificación **Public** para peticiones de: Listado, Ficha, búsquedas Agrupadas y transformación de un recurso externo.
- Auntentificación **Basic** para peticiones de: Alta, Baja y Modificación.

Cuando se implementa esta funcionalidad se configura a nivel de etiquetas de los servicios de cada uno de los controladores asociados al módulo de Territorio: (Como ejemplo mostramos los asociados a CuboEdad)
- **CUBOEDAD_LIST**: Listado de generico de los cubos
- **CUBOEDAD_RECORD**: Ficha del cubo
- **CUBOEDAD_QUERY**: Consulta standard de cubo de Datos.


Y los parámetros configurados quedarían de la siguiente manera en el fichero (**padron.properties**) Como Ejemplo:
- **peticiones.identificadas.public_auth=CUBOEDAD_QUERY,CUBOEDAD_RECORD,CUBOEDAD_LIST**

Para el resto de conjunto de datos se realizaria igual.

- **peticiones.identificadas.public_auth=CUBOESTUDIO_QUERY,CUBOESTUDIO_RECORD,CUBOESTUDIO_LIST,CUBOEDAD_QUERY,CUBOEDAD_RECORD,CUBOEDAD_LIST**


```sh
peticiones.identificadas.public_auth=CUBOESTUDIO_QUERY,CUBOESTUDIO_RECORD,CUBOESTUDIO_LIST,CUBOEDAD_QUERY,CUBOEDAD_RECORD,CUBOEDAD_LIST
```

Listado de Operaciones:
- **CUBOEDAD_LIST**: Listado de generico de los cubos
- **CUBOEDAD_RECORD**: Ficha del cubo
- **CUBOEDAD_QUERY**: Consulta standard de cubo de Datos.
- **CUBOEDAD_G_Q_LIST**: Listado de generico de los cubos
- **CUBOEDAD_G_Q_RECORD**: Ficha del cubo
- **CUBOEDAD_G_Q_QUERY**: Consulta standard de cubo de Datos.
- **CUBOESTUDIO_LIST**: Listado de generico de los cubos
- **CUBOESTUDIO_RECORD**: Ficha del cubo
- **CUBOESTUDIO_QUERY**: Consulta standard de cubo de Datos.
- **CUBOINDICADORES_LIST**: Listado de generico de los cubos
- **CUBOINDICADORES_RECORD**: Ficha del cubo
- **CUBOINDICADORES_QUERY**: Consulta standard de cubo de Datos.
- **CUBONACIONALIDAD_LIST**: Listado de generico de los cubos
- **CUBONACIONALIDAD_RECORD**: Ficha del cubo
- **CUBONACIONALIDAD_QUERY**: Consulta standard de cubo de Datos.
- **CUBOPROCEDENCIA_LIST**: Listado de generico de los cubos
- **CUBOPROCEDENCIA_RECORD**: Ficha del cubo
- **CUBOPROCEDENCIA_QUERY**: Consulta standard de cubo de Datos.
- **CUBO_PAIS_N_LIST**: Listado de generico de los cubos
- **CUBO_PAIS_N_RECORD**: Ficha del cubo
- **CUBO_PAIS_N_QUERY**: Consulta standard de cubo de Datos.



**NOTA:** Si alguna etiqueta de identificación de servicio no se incluyera en los parametros se le aplicaria la configuración por defecto asociada al mismo.

# Autores
Juan Carlos Ballesteros (Localidata)
Carlos Martínez de la Casa (Localidata)
Oscar Corcho (UPM, Localidata)
