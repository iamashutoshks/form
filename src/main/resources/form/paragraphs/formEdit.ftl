[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    [#if content.title?has_content]
        <label for="${content.controlName}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${content.title!}
            [#if content.mandatory]
                <dfn title="required">${model.requiredSymbol!}</dfn>
            [/#if]
            </span>
        </label>
    [/#if]
    [#if content.rows?default(1) == 1]
        <input type="text" name="${content.controlName}" id="${content.controlName}" value="${model.value!?html}"/>
    [#else]
        <textarea id="${content.controlName}" name="${content.controlName}" rows="${content.rows}">${model.value!?html}</textarea>
    [/#if]

    [#if content.description?has_content]
      <span>${content.description}</span>
    [/#if]
</div>