# sanitasPrueba

He seguido los siguientes pasos:
	-	Descargar el proyecto
	-	Crear en github un proyecto nuevo 
	-	Clonar el proyecto de github, incorporar los archivos y commitear el estado inicial.
	-	Descomentar del POM la librería sportalclientesweb e instalar la misma en el repositorio maven local con el siguiente comando:
		- mvn install:install-file -Dfile=sportalclientesweb-1.19.0.jar -DgroupId=sanitas.bravo.clientes -DartifactId=sportalclientesweb -Dversion=1.19.0 -Dpackaging=jar -DgeneratePom=true
	-	Estudiar el codigo:
		-	La clase Zendesk tiene un patrón factory y sirve para comunicarse por rest con un WS. 
			Además de los metodos que realizan la petición rest y la seguridad por Outh (pasandole el token de aplicación a la factoria) contiene createTicket que llama a la url
			"{{url con la que se configura la factory}} + '/api/v2' + 'tickets.json'" por POST pasandole la información del ticket.
		-	La clase ZendeskService tiene los siguientes metodos:
			altaTicketZendesk -> Pasandole un usuario recoge por rest datos de dos origenes de datos diferentes y despues utiliza la clase Zendesk para llamar a createTicket, en caso de fallar manda un correo.
	-	Divido las clases en un sistema de paquetes bajo el siguiente criterio y bajo el paquete zendesk para diferenciarlo de otras aplicaciones de la empresa:
		-	Controllers: Capa de acceso a la aplicación. Tendrán las clases de controlles
		-	Service: Capa de negocio. Contiene las clases que se encargan del negocio, comunmente Servicios.
		-	Integration: Capa de acceso a servicios externos. Contiene Componentes de acceso a servicios externos como puede ser WebService o comunicación por email.
	-	Añado spring-boot
		-	Pongo como parent del proyecto "spring-boot-starter-parent", creo el ZendeskApplication habilitando la autoconfiguración, leo de los paquete de 'com.mycorp' y creo un controller
		-	Creo la carpeta de resources con el application.properties y la agrego al context path
	-	Paso las properties de los @values de ZendeskService al application.properties
	-	Mirando el codigo veo que hay dos Beans necesarias por ZendeskService que no tienen implementación:
		-	PortalclientesWebEJB -> Entiendo que sea un bean de una comunicación con un componente EJB. Para no extenderme inicialmente he hecho una implementación y más adelante si tengo tiempo me meteré con el tratamiento del EJB.
		-	restTemplateUTF8 -> Me creo el beam del RestTemplate señalando codificación UTF-8
		-	EmailService -> Me creo la implementación del componente de email. Vacia por ahora.
	-	Levanta correctamente la aplicación
	
	-	Me creo un metodo en el controller que llame al ZendeskService.altaTicketZendesk siguiendo restful (URL: ticket Method: POST) y aprovecho para poner swagger
	
	- 	Añado modificaciones/mejoras en ZendeskService de cara a refactorizar
		-	Creo interfaz
		-	Paso la generación de los datos del ticket a un mapper