[@cms.editBar /]

<div ${model.style!} >

[#if content.title?has_content]
    <label for="${content.controlName}">
        <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${cmsfn.encode(cmsfn.asJCRNode(content)).title!}
            [#if content.mandatory]
                 <dfn title="required">${model.requiredSymbol!}</dfn>
            [/#if]
        </span>
   </label>
[/#if]


<input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}" maxlength="${content.maxLength!'50'}"/>

</div>