
<%@ page import="codereview.Changeset" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'changeset.label', default: 'Changeset')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-changeset" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="list-changeset" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="author" title="${message(code: 'changeset.author.label', default: 'Author')}" />
					
						<g:sortableColumn property="date" title="${message(code: 'changeset.date.label', default: 'Date')}" />
					
						<g:sortableColumn property="identifier" title="${message(code: 'changeset.identifier.label', default: 'Identifier')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${changesetInstanceList}" status="i" var="changesetInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td>${fieldValue(bean: changesetInstance, field: "author")}</td>
					
						<td><g:formatDate date="${changesetInstance.date}" /></td>
					
						<td>${fieldValue(bean: changesetInstance, field: "identifier")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${changesetInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
