
[#assign link = cmsfn.link("website", content.link)]
[#assign condition = content.condition]
[@cms.edit /]
<div>
  [#if condition?has_content]
    [#assign conditionList = cmsfn.asContentMapList(condition?children)]
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

