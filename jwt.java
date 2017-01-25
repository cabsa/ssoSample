/*
	This example depends on the following jar files
	commons-codec.jar from http://commons.apache.org/proper/commons-codec/
	json-smart.jar from https://code.google.com/p/json-smart/
	nimbus-jose-jwt.jar from https://bitbucket.org/nimbusds/nimbus-jose-jwt/overview

	Because of this [1] issue in nimbus-jose-jwt, please make sure to use a 
	version >= 2.13.1 as Insight View expects seconds in the iat parameter
	[1]: https://bitbucket.org/nimbusds/nimbus-jose-jwt/issue/35/jwtclaimsset-milliseconds-vs-seconds-issue
	*/
package es.insightview.sso;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

public class jwt extends HttpServlet {

	private static final String SHARED_KEY = "{my insight view token}";

	@Override
	 protected void service(HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {

		// En este punto se deberá hacer la autentificación por parte del cliente de sus usuarios.
		
		String usuario = "username"; // usuario (debe ser un email)
		String nombre = "nombre"; // nombre del usuario
		String apellido = "apellido"; // apellido del usuario
		String departamento = "Departamento Comercial"; // departamento del usuario
		String referencia = "identificador_propio"; // identificador interno del usuario

		// Creatte JWT with data
		JWTClaimsSet jwtClaims = new JWTClaimsSet();
		jwtClaims.setIssueTime(new Date());
		jwtClaims.setJWTID(request.getHeader("jwtId")); 
		jwtClaims.setSubject(usuario); // OBLIGATORIO
		jwtClaims.setCustomClaim("nombre", nombre); // OBLIGATORIO
		jwtClaims.setCustomClaim("apellido", apellido); // OBLIGATORIO
		jwtClaims.setCustomClaim("roles", "user"); // OBLIGATORIO
		
		// los siguientes claims son opcionales
		jwtClaims.setCustomClaim("departamento", departamento); // OPCIONAL
		jwtClaims.setCustomClaim("referencia", referencia); // OPCIONAL
		
		jwtClaims.setExpirationTime(new Date(System.currentTimeMillis() + 30000));

		// Create JWS header with HS256 algorithm
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

		// Create JWS object
		JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));

		// Create HMAC signer
		JWSSigner signer = new MACSigner(SHARED_KEY.getBytes());

		try {
			jwsObject.sign(signer);
		} catch (com.nimbusds.jose.JOSEException e) {
			System.err.println("Error signing JWT: " + e.getMessage());
			return;
		}

		// Serialise to JWT compact form
		String jwtString = jwsObject.serialize();

		String returnTo = request.getParameter("return_to");
		String redirectUrl = returnTo + "?jwt=" + jwtString;

		response.sendRedirect(redirectUrl);
	}

}
