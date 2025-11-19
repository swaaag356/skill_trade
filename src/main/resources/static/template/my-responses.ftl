<#assign pageTitle="Мои отклики">
<#include "main.ftl">

<#macro content>
    <div class="my-responses">
        <h2>Предложения, на которые вы откликнулись</h2>

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
            <p class="empty">Вы пока ни на что не откликнулись.</p>
        </#if>
    </div>
</#macro>