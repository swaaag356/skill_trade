<#-- src/main/resources/static/template/home.ftl -->
<#assign pageTitle="Главная">
<#include "main.ftl">

<#macro content>
    <section class="hero">
        <h2>Обменивайтесь навыками — бесплатно</h2>
        <p class="lead">
            SkillTrade — платформа, где вы можете предложить свои умения и получить нужные вам в обмен.
            Никаких денег, только взаимопомощь.
        </p>
        <a href="${contextPath}/offers" class="btn btn-primary">Смотреть предложения</a>
    </section>

    <section class="stats" id="stats">
        <div class="stat">
            <span class="stat__number" data-target="127">0</span>
            <span class="stat__label">активных предложений</span>
        </div>
        <div class="stat">
            <span class="stat__number" data-target="89">0</span>
            <span class="stat__label">обменов завершено</span>
        </div>
        <div class="stat">
            <span class="stat__number" data-target="312">0</span>
            <span class="stat__label">участников</span>
        </div>
    </section>

    <section class="how-it-works">
        <h3>Как это работает?</h3>
        <ol>
            <li>Создайте предложение: что вы умеете и что хотите получить.</li>
            <li>Другие пользователи откликаются на ваше предложение.</li>
            <li>Договоритесь о деталях и обменяйтесь навыками.</li>
        </ol>
    </section>

    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const counters = document.querySelectorAll('.stat__number');
            counters.forEach(counter => {
                const target = +counter.getAttribute('data-target');
                const increment = target / 100;
                let current = 0;

                const timer = setInterval(() => {
                    current += increment;
                    if (current >= target) {
                        counter.textContent = target;
                        clearInterval(timer);
                    } else {
                        counter.textContent = Math.ceil(current);
                    }
                }, 20);
            });
        });
    </script>
</#macro>