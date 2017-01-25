<p>El inicio de sesi&oacute;n &uacute;nico es un mecanismo que permite autenticar a los usuarios en el sistema y luego informar a Insight View que el usuario ha sido autenticado, el usuario entonces puede acceder a Insight View sin que se le solicite ingresar credenciales de inicio de sesi&oacute;n separadas.</p>
<p>El n&uacute;cleo de un inicio de sesi&oacute;n &uacute;nico es un mecanismo de seguridad que permite que Insight View conf&iacute;e en las solicitudes de inicio de sesi&oacute;n que recibe de sus sistemas.&nbsp;Insight View solo brinda acceso a los usuarios que usted ha autenticado.</p>
<p>El SSO de Insight View depende de una tecnolog&iacute;a denominada Token Web JSON (JWT) para garantizar la seguridad del intercambio de datos de autenticaci&oacute;n de usuarios.</p>
<p>La implementaci&oacute;n de JWT en s&iacute; es sencilla y la mayor&iacute;a de los lenguajes modernos tienen bibliotecas disponibles.</p>
<p>En este repositorio de github podr&aacute; encontrar un ejemplo de implementaci&oacute;n usando java, si implementa JWT de otra manera, nos encantar&iacute;a incluir su ejemplo en nuestro repositorio.</p>
<h4>Activaci&oacute;n del SSO en InsightView</h4>
<p>Una vez activado y configurado el inicio de sesi&oacute;n &uacute;nico desde su perfil de usuario en InsightView, las solicitudes de inicio de sesi&oacute;n ser&aacute;n dirigidas a la URL de SSO del servidor del cliente.</p>
<p>Los siguientes son los pasos para activar el sso en insightview:</p>
<ul>
<li>Autentificarse en insightview usando su&nbsp;usuario y contrase&ntilde;a (el usuario debe ser de tipo administrador)</li>
<li>Navegar a la p&aacute;gina de perfil de usuario y acceder a la opci&oacute;n de Single Sign-On.</li>
<li>Rellenar la url de autentificaci&oacute;n con la url de su servidor {ServerSSOUrl} donde va a alojar el script de autentificaci&oacute;n.</li>
</ul>
<p>En esa p&aacute;gina adem&aacute;s debe apuntarse 2 datos:</p>
<ul>
<li>{SecretKey}: esta secret Key deber&aacute; ser usada en el script de autentificaci&oacute;n para firmar los tokens.</li>
<li>{SSOUrl}: Esta es la url&nbsp;a la que deber&aacute;n acceder los usuarios SSO del cliente para entrar en InsightView</li>
</ul>
<p>El workflow de la autentificaci&oacute;n usando SSO en InsightView es el siguiente:</p>
<ol>
<li>Un usuario no autenticado (que a&uacute;n no ha iniciado sesi&oacute;n) intenta acceder a Insight View usando la url {SSOUrl}</li>
<li>El mecanismo SSO de Insight View reconoce que SSO est&aacute; activado y que el usuario no est&aacute; autenticado.</li>
<li>El usuario es redirigido a la URL de inicio de sesi&oacute;n remoto configurado en su servidor ({ServerSSOUrl}).</li>
<li>El usuario se autentica contra su servidor de SSO.</li>
<li>Su servidor de SSO crea una solicitud JWT que contiene los datos pertinentes del usuario.</li>
<li>Su servidor de SSO redirige al navegador del usuario&nbsp;a la url de acceso de Insight View ({SSOUrl}) con la carga JWT.</li>
<li>Insight View analiza los detalles del usuario en la carga JWT y luego otorga una sesi&oacute;n al usuario. En el caso de que el usuario no exista en la base de datos de Insight View ser&aacute; dado de alta.</li>
</ol>
<p>Como se puede ver, este proceso depende de los redireccionamientos del navegador y el paso de mensajes firmados usando JWT. Los redireccionamientos se realizan en su totalidad en el navegador y <strong>no hay comunicaci&oacute;n directa entre Insight View y sus sistemas</strong>, de manera que puede guardar sus p&aacute;ginas de autenticaci&oacute;n de manera segura detr&aacute;s del firewall de su compa&ntilde;&iacute;a.</p>
<h4><img src="https://raw.githubusercontent.com/cabsa/ssoSample/master/Diagrama%20SSO%20Clientes.png" alt="" width="755" height="832" /></h4>
<h4>Configurar su implementaci&oacute;n de JWT</h4>
<p>Para realizar un SSO para un usuario, tendr&aacute; que enviar varios atributos de usuario necesarios a Insight View como un hash (tabla de hash, diccionario). Lo m&aacute;s importante: Insight View necesita una direcci&oacute;n de correo electr&oacute;nico para poder identificar de manera exclusiva al usuario. Adem&aacute;s de los atributos requeridos, que se muestran en la tabla a continuaci&oacute;n, puede si lo desea enviar datos adicionales del perfil del usuario. Estos datos se sincronizan en su sistema de administraci&oacute;n de usuarios y en Insight View.</p>
<p>La mayor&iacute;a de las implementaciones JWT utilizan un hash y un secreto, y devuelven una carga de una cadena simple para enviar al otro lado.</p>
<h4>URL return_to</h4>
<p>Cuando Insight View redirige a un usuario a su p&aacute;gina de inicio de sesi&oacute;n, tambi&eacute;n pasar&aacute; un par&aacute;metro return_to en el URL. Este par&aacute;metro contiene la p&aacute;gina a la cual su servidor devolver&aacute; al usuario una vez realizada satisfactoriamente la autenticaci&oacute;n. Por ejemplo:</p>
<p>Insight View reconoce que el usuario no est&aacute; autentificado con lo que se redirige al usuario a: http://{ServerSSOUrl}?return_to={SSOUrl}&amp;jwtId=3123123</p>
<p>Lo &uacute;nico que tiene que hacer el script de SSO del servidor del cliente es tomar el valor return_to del URL invocado y redirigir al usuario enviando tambi&eacute;n el token JWT rellenado con los datos del usuario. En otras palabras, una vez realizada la autenticaci&oacute;n de su lado, su servidor redirige al usuario a:</p>
<p>https://{SSOUrl}?jwt=eyJhbGciOiJIUzI1NiJ9.eyJub21icmUiOiJub21icmUiLCJhcGVsbGlkbyI6ImFwZWxsaWRvIiwiZXhwIjoxNDg0MTI1OTY2LCJzdWIiOiJub21icmUuYXBlbGxpZG8zQGNhYnNhLmVzIiwiY29udHJhdG9FTyI6IlhYWFgxIiwicm9sZXMiOlsidXNlciJdLCJqdGkiOiJIVUREIiwiaWF0IjoxNDg0MTI1OTM2LCJyZWZlcmVuY2lhIjoiMDAwMSJ9.CtuaMFPFHD4TNdW4wkV72hrn018HbUFlbt0rLCqbSpo</p>
<h4>Informaci&oacute;n adicional sobre JWT</h4>
<p>JWT es un est&aacute;ndar abierto reciente que est&aacute; siendo liderado por el organismo internacional de est&aacute;ndares IETF y patrocinado por empresas de alto nivel del sector tecnol&oacute;gico (por ejemplo, Microsoft, Facebook y Google).</p>
<p>Los componentes b&aacute;sicos de JWT son bien comprendidos y el resultado es una especificaci&oacute;n bastante simple, que se encuentra aqu&iacute; http://tools.ietf.org/html/draft-jones-json-web-token-10 . Hay varias implementaciones de fuente abierta de la especificaci&oacute;n JWT que abarcan la mayor&iacute;a de las tecnolog&iacute;as modernas. Esto quiere decir que el inicio de sesi&oacute;n &uacute;nico JWT se puede configurar sin mayores dificultades.</p>
<p>Una cosa que debe tener en cuenta es que la carga de JWT solo est&aacute; codificada y firmada, no encriptada, de manera que no ponga informaci&oacute;n confidencial en la tabla hash. JWT funciona mediante la serializaci&oacute;n de JSON que se transmite a una cadena. Luego, codifica dicha cadena en base 64 y, a continuaci&oacute;n, hace un HMAC de la cadena en base 64 que depende del secreto compartido. Esto produce una firma que el lado del destinatario puede usar para validar al usuario.</p>
<p>Use the free <a href="http://divtable.com/generator/" rel="nofollow">HTML table generator</a> to create the perfect spreadsheets for your website!</p>