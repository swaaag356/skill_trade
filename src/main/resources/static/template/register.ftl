<#-- src/main/resources/static/template/register.ftl -->
<#assign pageTitle="Регистрация">
<#include "main.ftl">

<#macro content>
    <div class="form-wrapper">
        <form action="${contextPath}/register" method="post" class="form">
            <h2>Создать аккаунт</h2>
            <#if error??>
                <p class="error">${error}</p>
            </#if>
            <div class="form-group">
                <label>Логин</label>
                <input type="text" name="username" required minlength="3">
            </div>
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" required>
            </div>
            <div class="form-group">
                <label>Пароль</label>
                <input type="password" name="password" required minlength="6">
            </div>
            <div class="form-group">
                <label>О себе (необязательно)</label>
                <textarea name="about"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Зарегистрироваться</button>
        </form>
    </div>
</#macro>