

[#-------------- INCLUDE AND ASSIGN PART --------------]

[#-- Include: Global --]
[#include "/form/components/init.required.ftl"]


[#-------------- RENDERING PART --------------]

<div ${model.style!} >
    [#if content.title?has_content]
        <label for="${content.controlName!}">
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

    <input ${requiredAttribute!} type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!}" maxlength="${content.maxLength!'50'}"/>
</div><!-- end ${model.style!} -->
