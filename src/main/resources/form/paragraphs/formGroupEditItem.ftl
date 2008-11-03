[#assign cms=JspTaglibs["cms-taglib"]]
[@cms.editBar /]
[#if content.title?has_content]
    <label ${model.style!} for="${content.controlName}">
        ${content.title}
    [#if content.mandatory]
         <dfn title="required">*</dfn>
    [/#if]
[/#if]
    <input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!}"/>

[#if content.title?has_content]
    </label>
[/#if]
[#if content.description?has_content]
    <span>${content.description}</span>
[/#if]
