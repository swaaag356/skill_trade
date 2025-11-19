<#assign pageTitle="Предложения обмена">
<#include "main.ftl">

<#macro content>
    <div class="offers">
        <div class="offers__header">
            <h2>Активные предложения</h2>
            <#if currentUser??>
                <a href="${contextPath}/offer/create" class="btn btn-success">+ Создать</a>
            </#if>
        </div>

        <#if offers?? && (offers?size > 0)>
            <div class="offers-grid">
                <#list offers as offer>
                    <div class="offer-card">
                        <div class="offer-card__skills">
                            <span class="skill-offer">${offer.offerSkill.name}</span>
                            <span class="arrow">↔</span>
                            <span class="skill-request">${offer.requestSkill.name}</span>
                        </div>
                        <p class="offer-card__desc">${offer.description}</p>
                        <div class="offer-card__footer">
                            <small>от <a href="${contextPath}/profile?id=${offer.user.id}">${offer.user.username}</a></small>
                            <a href="${contextPath}/offer/${offer.id}" class="btn btn-sm">Подробнее</a>
                        </div>
                    </div>
                </#list>
            </div>
        <#else>
            <p class="empty">Пока нет предложений. Будьте первым!</p>
        </#if>
    </div>
</#macro>