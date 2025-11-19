<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title!"SkillTrade"}</title>
    <link rel="stylesheet" href="${contextPath}/static/css/main.css">
    <link rel="stylesheet" href="${contextPath}/static/css/forms.css">

</head>
<body>
<#include "header.ftl">
<#include "nav.ftl">

<main class="container">
    <h1>${pageTitle!"Главная"}</h1>
    <@content />
</main>

<#include "footer.ftl">
</body>
</html>