
[#assign link = cmsfn.link("website", content.link)]
[#assign condition = content.condition]

[@cms.editBar /]
<div>
  [#if condition?has_content]
    [#assign conditionList = condition?children]
    [#list conditionList as conditionItem]
      <p>
            ${conditionItem.condition!}, ${conditionItem.fieldName!}: ${conditionItem.fieldValue!}
        </p>
        [/#list]
    [/#if]
    [#if link?has_content]
      <p>
          ${i18n['dialog.form.condition.tabMain.link.description']}: ${link}
      </p>
    [/#if]
</div>
