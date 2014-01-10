DEPARTAPLUS+ 1.0.1
==================

DepartaPlus es una pequeña aplicacion que da de alta, baja, modifica y consulta empleados
y departamentos.

Tiene una conexion con una base de datos Oracle 10g, http://www.oracle.com/technetwork/database/database10g/overview/index-095623.html

Descargar aquí: http://www.oracle.com/technetwork/database/enterprise-edition/downloads/index.html

Tiene una interfaz MDI, con mucho que mejorar.

Creada y diseñada por Antonio López Marín, la licencia es GNU v3: http://www.gnu.org/licenses/gpl.html

Eres libre de difundir y modificar este codigo.

Instrucciones
==================	

	Hay que ejecutar en la base de datos, el fichero Departaplus.sql.

		
	Hay que configurar la base de datos con el fichero base-datos.cfg.


	No hace falta encender antes la base de datos ya que la aplicacion, puede 
	encender y apagar la base de datos, una vez apagada no puedes volver a encenderla.

Patrones de diseño
==================

	Esta aplicacion tiene diferentes patrones de diseño, los dos mas importantes son
	Data Acces Object (DAO) y Value Object (VO), ambos son patrones de diseño para 
	ayudar el manejo de informacion de la base de datos.

	A parte, se han definido el patron singleton para la conectividad, y solo haber 
	una conexion a la base de datos para toda la aplicación y evitar futuros errores
	de demasiadas conexiones abiertas, etc... etc...

	Y ya en referente a la interfaz grafica se ha definido el patron de diseño de 
	Observers, con este patron de diseño actualizo la vista cada vez que hay una 
	modificacion de la base de datos. 

