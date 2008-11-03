[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    [#if content.title?has_content]
        <label class="${content.editLength!}" for="${content.controlName}">
            ${content.title}
        [#if content.mandatory]
            <dfn title="required">*</dfn>
        [/#if]
    [/#if]
    [#if content.rows == 1]
        <input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!}"/>
    [#else]
        <textarea id="${content.controlName}" name="${content.controlName}" rows="${content.rows}">${model.value!}</textarea>
    [/#if]
    [#if content.title?has_content]
      </label>
    [/#if]
    [#if content.description?has_content]
      <span>${content.description}</span>
    [/#if]
</div>