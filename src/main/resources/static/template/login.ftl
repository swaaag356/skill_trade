<#-- src/main/resources/static/template/login.ftl -->
<#assign pageTitle="Вход">
<#include "main.ftl">

<#macro content>
    <div class="form-wrapper">
        <form action="${contextPath}/login" method="post" class="form">
            <h2>Вход в аккаунт</h2>
            <#if error??>
                <p class="error">${error}</p>
            </#if>
            <div class="form-group">
                <label>Логин</label>
                <input type="text" name="username" required>
            </div>
            <div class="form-group">
                <label>Пароль</label>
                <input type="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary">Войти</button>
            <p><a href="${contextPath}/register">Нет аккаунта? Зарегистрируйся</a></p>
        </form>
    </div>
</#macro>