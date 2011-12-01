
[#assign link = cmsfn.link("website", content.link!)]
[#assign condition = content.condition!]

[@cms.edit /]

<div class="condition">
    [#if condition?has_content]
        [#assign conditionList = cmsfn.asContentMapList(condition?children)]
        [#list conditionList as conditionItem]
            <p>${i18n['contidion.author.info']}: ${conditionItem.condition!}, ${conditionItem.fieldName!}: ${conditionItem.fieldValue!}</p>
        [/#list]
    [/#if]

    [#if link?has_content]
        <p>${i18n['dialog.form.condition.tabMain.link.description']}: ${link}</p>
    [/#if]
</div><!-- end condition -->

