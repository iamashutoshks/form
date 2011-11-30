

[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Include: Global --]
[#include "/form/components/init.required.ftl"]


[#-------------- RENDERING PART --------------]

[@cms.edit /]

<div ${model.style!} >
    [#if content.title?has_content]
        <label for="${content.controlName}">
            <span>
                [#if !model.isValid()]
                    <em>${i18n['form.error.field']}</em>
                [/#if]
                ${content.title}
                [#if content.mandatory!false]
                    <dfn title="required">${model.requiredSymbol!}</dfn>
                [/#if]
            </span>
        </label>
    [/#if]

    [#if content.rows?default(1) == 1]
        <input ${requiredAttribute!} type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}" maxlength="${content.maxLength!'50'}"/>
    [#else]
        <textarea ${requiredAttribute!} id="${content.controlName}" name="${content.controlName}" rows="${content.rows}">${model.value!?html}</textarea>
    [/#if]

    [#if content.description?has_content]
      <span>${content.description}</span>
    [/#if]

</div><!-- end ${model.style!} -->