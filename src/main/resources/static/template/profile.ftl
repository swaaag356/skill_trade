<#-- src/main/resources/static/template/profile.ftl -->
<#assign pageTitle="Профиль">
<#include "main.ftl">

<#macro content>
    <div class="profile">
        <div class="profile__header">
            <h2>${user.username}</h2>
            <div class="rating">Рейтинг: <strong>${user.rating!0.0}</strong> ★</div>
        </div>
        <p><strong>Email:</strong> ${user.email}</p>
        <#if user.about??>
            <p><strong>О себе:</strong> ${user.about}</p>
        </#if>

        <h3>Предложения</h3>
        <#if userOffers?? && (userOffers?size > 0)>
            <#list userOffers as offer>
                <div class="offer-mini">
                    ${offer.offerSkill.name} → ${offer.requestSkill.name}
                    <small>(${offer.status})</small>
                </div>
            </#list>
        <#else>
            <p>Нет предложений</p>
        </#if>
    </div>
</#macro>