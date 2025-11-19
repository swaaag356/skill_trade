<#-- src/main/resources/static/template/header.ftl -->
<header class="header">
    <div class="container header__inner">
        <a href="${contextPath}/" class="logo">SkillTrade</a>
        <div class="user-info">
            <#if currentUser??>
                <span>Привет, ${currentUser.username}!</span>
                <a href="${contextPath}/profile" class="btn btn-sm btn-primary">Профиль</a>
                <a href="${contextPath}/logout" class="btn btn-sm btn-danger">Выйти</a>
            <#else>
                <a href="${contextPath}/login" class="btn btn-sm btn-primary">Войти</a>
                <a href="${contextPath}/register" class="btn btn-sm btn-primary">Регистрация</a>
            </#if>
        </div>
    </div>
</header>