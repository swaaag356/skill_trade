<#-- src/main/resources/static/template/nav.ftl -->
<nav class="nav">
    <div class="container">
        <ul class="nav__list">
            <li><a href="${contextPath}/">Главная</a></li>
            <li><a href="${contextPath}/offers">Предложения</a></li>
            <#if currentUser??>
                <li><a href="${contextPath}/offer/create">Создать</a></li>
                <li><a href="${contextPath}/my-responses">Мои отклики</a></li>
            </#if>
        </ul>
    </div>
</nav>