<#assign pageTitle="Предложение #${offer.id}">
<#include "main.ftl">

<#macro content>
    <div class="offer-detail">
        <h2>${offer.offerSkill.name} ↔ ${offer.requestSkill.name}</h2>
        <p><strong>Описание:</strong> ${offer.description}</p>
        <p><strong>Автор:</strong> <a href="${contextPath}/profile?id=${offer.user.id}">${offer.user.username}</a></p>
        <p><strong>Статус:</strong> ${offer.status}</p>

        <#if currentUser?? && offer.status == "ACTIVE" && currentUser.id != offer.user.id>
            <form action="${contextPath}/respond/offer/${offer.id}" method="post" class="form-inline">
                <textarea name="message" placeholder="Ваше сообщение..." required></textarea>
                <button type="submit" class="btn btn-primary">Откликнуться</button>
            </form>
        </#if>

        <#if currentUser?? && offer.status == "IN_PROGRESS">
            <#assign isParticipant = (currentUser.id == offer.user.id) ||
            (offer.responses?? && offer.responses?filter(r -> r.responder.id == currentUser.id)?size > 0)>

            <#if isParticipant>
                <form action="${contextPath}/complete/offer/${offer.id}" method="post" class="form-inline">
                    <button type="submit" class="btn btn-success">Завершить обмен</button>
                </form>
            </#if>
        </#if>
    </div>
</#macro>