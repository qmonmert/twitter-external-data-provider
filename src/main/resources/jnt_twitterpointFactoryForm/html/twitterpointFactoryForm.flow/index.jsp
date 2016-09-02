<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="flowRequestContext" type="org.springframework.webflow.execution.RequestContext"--%>
<%--@elvariable id="twitterFactory" type="org.jahia.modules.twitter.admin.MountPointFactory"--%>

<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="admin/angular.min.js"/>
<template:addResources type="javascript" resources="admin/app/folderPicker.js"/>
<template:addResources type="css" resources="admin/app/folderPicker.css"/>



<h1 class="page-header"><fmt:message key="twitter_mountPoint"/></h1>
<div class="folderPickerApp" ng-app="folderPicker">
    <%@ include file="errors.jspf" %>

    <fmt:message var="selectTarget" key="twitter_mountPoint.selectTarget"/>

    <c:set var="i18NSelectTarget" value="${functions:escapeJavaScript(selectTarget)}"/>
    <div class="box-1" ng-controller="folderPickerCtrl" ng-init='init(${localFolders}, "${twitterFactory.localPath}", "localPath", true, "${i18NSelectTarget}")'>
        <form:form modelAttribute="twitterFactory" method="post">
            <fieldset title="local">
                <div class="container-fluid">

                    <div class="row-fluid">
                        <form:label path="name">
                            <fmt:message key="twitter_mountPoint.name"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="name"/>
                    </div>
                    <div class="row-fluid">
                        <form:label path="search">
                            <fmt:message key="twitter_mountPoint.search"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="search"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="consumerKey">
                            <fmt:message key="twitter_mountPoint.consumerKey"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="consumerKey"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="consumerSecret">
                            <fmt:message key="twitter_mountPoint.consumerSecret"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="consumerSecret"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="accessToken">
                            <fmt:message key="twitter_mountPoint.accessToken"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="accessToken"/>
                    </div>

                    <div class="row-fluid">
                        <form:label path="accessTokenSecret">
                            <fmt:message key="twitter_mountPoint.accessTokenSecret"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input path="accessTokenSecret" />
                    </div>

                    <div class="row-fluid">
                        <form:label path="numberTweets">
                            <fmt:message key="twitter_mountPoint.numberTweets"/> <span style="color: red">*</span>
                        </form:label>
                        <form:input type="number" name="quantity" min="1" max="100" path="numberTweets" />
                    </div>


                    <div class="row-fluid">
                        <jsp:include page="/modules/external-provider/angular/folderPicker.jsp"/>
                    </div>
                </div>
            </fieldset>
            <fieldset>
                <div class="container-fluid">
                    <button class="btn btn-primary" type="submit" name="_eventId_save">
                        <fmt:message key="label.save"/>
                    </button>
                    <button class="btn" type="submit" name="_eventId_cancel">
                        <fmt:message key="label.cancel"/>
                    </button>
                </div>
            </fieldset>
        </form:form>
    </div>
</div>