[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    [#if content.title?has_content]
        <label for="${content.controlName?html}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${mgnl.encode(content).title!}
            [#if content.mandatory]
                <dfn title="required">${model.requiredSymbol!?html}</dfn>
            [/#if]
            </span>
        </label>
    [/#if]
    [#if content.rows?default(1) == 1]
        <input type="text" name="${content.controlName?html}" id="${content.controlName?html}" value="${model.value!?html}" maxlength="${(content.maxLength?html)!'50'}"/>
    [#else]
        <textarea id="${content.controlName?html}" name="${content.controlName?html}" rows="${content.rows}">${model.value!?html}</textarea>
    [/#if]

    [#if content.description?has_content]
      <span>${content.description?html}</span>
    [/#if]
</div>
