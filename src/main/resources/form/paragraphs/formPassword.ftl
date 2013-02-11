[#assign cms=JspTaglibs["cms-taglib"]]

<div ${model.style!} >
[@cms.editBar /]
    [#if content.title?has_content]
        <label id="${content.controlName?html}_label" class="${content.editLength!?html}" for="${content.controlName?html}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${mgnl.encode(content).title!?html}
            [#if content.mandatory]
                <dfn title="required">${model.requiredSymbol!?html}</dfn>
            [/#if]
            </span>
    [/#if]
    [#if content.rows == 1]
        <input type="password" name="${content.controlName?html}" id="${content.controlName?html}" value=""/>
    [#else]
        <textarea id="${content.controlName?html}" name="${content.controlName?html}" rows="${content.rows?html}"></textarea>
    [/#if]
    [#if content.title?has_content]
      </label>
    [/#if]
    [#if content.description?has_content]
      <span>${content.description?html}</span>
    [/#if]
</div>
