<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>

<html>
<head>
	<title>Facelets Greeting Page</title>
</head>
<body>
	<f:view>
		<f:loadBundle basename="resources" var="msg" />
		
		<strong>
		&nbsp;
		#{user.name}
		<h:outputText value="#{user.name}" />
		<h:outputText value="!" />
		</strong>
	</f:view>
</body>
</html>