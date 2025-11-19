<#-- src/main/resources/static/template/offer-create.ftl -->
<#assign pageTitle="Новое предложение">
<#include "main.ftl">

<#macro content>
    <div class="form-wrapper">
        <form action="${contextPath}/offer/create" method="post" class="form">
            <h2>Создать предложение обмена</h2>
            <#if error??>
                <p class="error">${error}</p>
            </#if>

            <div class="form-group">
                <label>Предлагаю навык</label>
                <input type="text" name="offerSkill" placeholder="Например: Гитара" required>
            </div>
            <div class="form-group">
                <label>Ищу навык</label>
                <input type="text" name="requestSkill" placeholder="Например: Английский" required>
            </div>
            <div class="form-group">
                <label>Описание</label>
                <textarea name="description" required minlength="10"></textarea>
            </div>
            <button type="submit" class="btn btn-success">Опубликовать</button>
        </form>
    </div>
</#macro>