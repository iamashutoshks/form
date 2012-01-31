
<div class="text">
    [#if content.title?has_content]
        <h2>${content.title!}</h2>
    [/#if]

    [#assign formSummaryBeanList = model.formSummaryBeanList!]
    [#list formSummaryBeanList as formSummaryBean]
        [#assign parametersMap = formSummaryBean.parameters!]
        [#assign parametersKeys = parametersMap?keys]
        [#if parametersKeys?has_content]
            <table cellspacing="1" cellpadding="1" border="0" width="100%" >
                <caption style="text-align:left;">${formSummaryBean.title!formSummaryBean.name!}</caption>
                <tbody>
                    [#list parametersKeys as parameterKey]
                        <tr>
                            <td>${parameterKey}</td>
                            <td>${parametersMap[parameterKey]!}</td>
                        </tr>
                    [/#list]
                </tbody>
            </table>
        [#else]
            <p>${i18n['summary.no.content']}</p>
        [/#if]
    [/#list]
</div>
